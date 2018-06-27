/*
 * @Copyright: 2005-2018 so_what. All rights reserved.
 */
package com.hyjf.cs.trade.service.impl;

import com.hyjf.am.resquest.trade.TenderRequest;
import com.hyjf.am.vo.statistics.AppChannelStatisticsDetailVO;
import com.hyjf.am.vo.trade.CouponUserVO;
import com.hyjf.am.vo.trade.account.AccountVO;
import com.hyjf.am.vo.trade.hjh.HjhAccedeVO;
import com.hyjf.am.vo.trade.hjh.HjhPlanVO;
import com.hyjf.am.vo.user.*;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.constants.MsgCode;
import com.hyjf.common.enums.MsgEnum;
import com.hyjf.common.exception.CheckException;
import com.hyjf.common.exception.ReturnMessageException;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetCode;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.GetOrderIdUtils;
import com.hyjf.common.util.calculate.DuePrincipalAndInterestUtils;
import com.hyjf.common.validator.CheckUtil;
import com.hyjf.common.validator.Validator;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.cs.trade.client.*;
import com.hyjf.cs.trade.service.BaseTradeServiceImpl;
import com.hyjf.cs.trade.service.CouponService;
import com.hyjf.cs.trade.service.HjhTenderService;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 加入计划
 * @Author sss
 * @Version v0.1
 * @Date 2018/6/19 9:50
 */
@Service
public class HjhTenderServiceImpl extends BaseTradeServiceImpl implements HjhTenderService {
    private static final Logger logger = LoggerFactory.getLogger(HjhTenderServiceImpl.class);

    @Autowired
    private AmUserClient amUserClient;

    @Autowired
    private AmBorrowClient amBorrowClient;

    @Autowired
    private CouponClient couponClient;

    @Autowired
    private RechargeClient rechargeClient;

    @Autowired
    private AmMongoClient amMongoClient;

    @Autowired
    private CouponService couponService;

    /**
     * @param request
     * @Description 检查加入计划的参数
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/19 9:47
     */
    @Override
    public WebResult<Map<String, Object>> joinPlan(TenderRequest request) {
        WebViewUserVO loginUser = RedisUtils.getObj(request.getToken(), WebViewUserVO.class);
        Integer userId = loginUser.getUserId();
        request.setUser(loginUser);
        if(request.getPlatform()==null){
            // 投资平台不能为空
            throw new ReturnMessageException(MsgEnum.STATUS_ZC000018);
        }
        // 查询选择的优惠券
        CouponUserVO cuc = null;
        if (request.getCouponGrantId() != null && request.getCouponGrantId() > 0) {
            cuc = couponClient.getCouponUser(request.getCouponGrantId(), userId);
        }
        // 查询计划
        if (StringUtils.isEmpty(request.getBorrowNid())) {
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_PLAN_NOT_EXIST);
        }
        HjhPlanVO plan = amBorrowClient.getPlanByNid(request.getBorrowNid());
        if (plan == null) {
            throw new ReturnMessageException(MsgEnum.FIND_PLAN_ERROR);
        }
        if (plan.getPlanInvestStatus() == 2) {
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_PLAN_CLOSE);
        }
        logger.info("加入计划投资校验开始userId:{},planNid:{},ip:{},平台{},优惠券:{}", userId, request.getBorrowNid(), request.getIp(), request.getPlatform(), request.getCouponGrantId());
        // 先检查redis  看用户是否重复投资
        boolean checkToken = checkRedisToken(request);
        if (!checkToken) {
            // 用户正在加入计划
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_IN_PROGRESS);
        }
        // 设置redis 的值  防重校验
        String redisValue = GetDate.getCalendar().getTimeInMillis() + GetCode.getRandomCode(5);
        RedisUtils.set(RedisConstants.HJH_TENDER_REPEAT + userId, redisValue, 300);
        // 查询用户信息
        UserInfoVO userInfo = amUserClient.findUsersInfoById(userId);
        UserVO user = amUserClient.findUserById(userId);
        // 检查用户状态  角色  授权状态等  是否允许投资
        checkUser(user, userInfo);
        // 检查江西银行账户
        BankOpenAccountVO account = amUserClient.selectBankAccountById(userId);
        if (account == null || user.getBankOpenAccount() == 0 || StringUtils.isEmpty(account.getAccount())) {
            throw new ReturnMessageException(MsgEnum.ERR_BANK_ACCOUNT_NOT_OPEN);
        }
        // 查询用户账户表-投资账户
        AccountVO tenderAccount = rechargeClient.getAccount(userId);
        // 检查投资金额
        checkTenderMoney(request, plan, account, cuc, tenderAccount);
        logger.info("加入计划投资校验通过userId:{},ip:{},平台{},优惠券为:{}", userId, request.getIp(), request.getPlatform(), request.getCouponGrantId());

        // 开始投资------------------------------------------------------------------------------------------------------------------------------------------
        return tender(request, plan, account, cuc, tenderAccount);
    }

    /**
     * @Description 开始投资
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/20 14:56
     */
    private  WebResult<Map<String, Object>> tender(TenderRequest request, HjhPlanVO plan, BankOpenAccountVO account, CouponUserVO cuc, AccountVO tenderAccount) {
        WebResult<Map<String, Object>> result= new WebResult<Map<String, Object>>();
        result.setStatus(WebResult.ERROR);
        Integer userId = request.getUser().getUserId();
        BigDecimal decimalAccount = new BigDecimal(request.getAccount());
        request.setBankOpenAccount(account);
        request.setTenderAccount(tenderAccount);
        // 体验金投资
        if (decimalAccount.compareTo(BigDecimal.ZERO) != 1 && cuc != null && (cuc.getCouponType() == 3 || cuc.getCouponType() == 1)) {
            logger.info("体验{},优惠金投资开始:userId:{},平台{},券为:{}", userId, request.getPlatform(), request.getCouponGrantId());
            // 体验金投资
            couponService.couponTender(request, plan, account, cuc, tenderAccount);
            // 计算收益
            Map<String, Object> tenderEarnings = getTenderEarnings(request,plan,cuc);
            result.setData(tenderEarnings);
            result.setStatus(WebResult.SUCCESS);
            logger.info("体验金投资结束:userId{}" + userId);
            return result;
        }
        String redisKey = RedisConstants.HJH_PLAN + plan.getPlanNid();
        // 计划剩余金额
        String balance = RedisUtils.get(redisKey);
        JedisPool pool = RedisUtils.getPool();
        Jedis jedis = pool.getResource();
        // 操作redis----------------------------------------------
        if (StringUtils.isNotBlank(balance)) {
            MsgCode redisMsgCode = null;
            try {
                while ("OK".equals(jedis.watch(redisKey))) {
                    if (StringUtils.isNotBlank(balance)) {
                        logger.info("加入计划冻结前可用金额为:{},userId:{},planNid:{},平台:{}", decimalAccount, userId, plan.getPlanNid(), request.getPlatform());
                        logger.info("加计划未减前可用开放额度redis:{},userId:{},planNid:{},平台:{}", balance, userId, plan.getPlanNid(), request.getPlatform());
                        if (new BigDecimal(balance).compareTo(BigDecimal.ZERO) == 0) {
                            logger.info("planNid:{},可加入剩余金额为{}元", plan.getPlanNid(), balance);
                            redisMsgCode = MsgEnum.ERR_AMT_TENDER_YOU_ARE_LATE;
                        } else {
                            Transaction tx = jedis.multi();
                            // 事务：计划当前可用额度 = 计划未投前可用余额 - 用户投资额度
                            BigDecimal lastAccount = new BigDecimal(balance).subtract(decimalAccount);
                            tx.set(redisKey, lastAccount + "");
                            List<Object> result1 = tx.exec();
                            if (result1 == null || result1.isEmpty()) {
                                jedis.unwatch();
                                logger.info("计划可用开放额度redis扣除失败：userId:{},planNid{},金额{}元", userId, plan.getPlanNid(), balance);
                                redisMsgCode = MsgEnum.ERR_AMT_TENDER_INVESTMENT;
                            } else {
                                logger.info("加计划redis操作成功userId:{},平台:{},planNid{},计划扣除后可用开放额度redis", userId, request.getPlatform(), plan.getPlanNid(), lastAccount);
                                // 写队列
                                break;
                            }
                        }
                    } else {
                        logger.info("您来晚了：userId:{},planNid{},金额{}元", userId, plan.getPlanNid(), balance);
                        redisMsgCode = MsgEnum.ERR_AMT_TENDER_YOU_ARE_LATE;
                    }
                }
            } catch (Exception e) {
                if (redisMsgCode != null) {
                    throw new ReturnMessageException(redisMsgCode);
                } else {
                    throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_INVESTMENT);
                }
            } finally {
                RedisUtils.returnResource(pool, jedis);
            }
        } else {
            logger.info("您来晚了：userId:{},planNid{},金额{}元", userId, plan.getPlanNid(), balance);
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_YOU_ARE_LATE);
        }
        // 生成冻结订单-----------------------
        boolean afterDealFlag = false;
        // 插入数据库  真正开始操作加入计划表
        afterDealFlag = updateAfterPlanRedis(request, plan);

        // 计算收益
        Map<String, Object> tenderEarnings = getTenderEarnings(request,plan,cuc);
        result.setStatus(WebResult.SUCCESS);
        result.setData(tenderEarnings);
        return result;
    }

    /**
     * 获取预期收益
     * @param request
     * @param plan
     * @param cuc
     * @return
     */
    private Map<String, Object> getTenderEarnings(TenderRequest request, HjhPlanVO plan, CouponUserVO cuc) {
        Map<String, Object> result = new HashedMap();
        // 历史回报
        result.put("earnings", request.getEarnings());
        // 优惠券收益
        result.put("couponInterest", request.getCouponInterest());
        // 投资金额
        result.put("account", request.getAccount());
        // 投资的计划
        result.put("borrowNid", plan.getPlanNid());
        // 如果有优惠券  放上优惠券面值和类型
        if (cuc != null) {
            // 优惠券类别
            result.put("couponType", cuc.getCouponType());
            // 优惠券额度
            result.put("couponQuota", cuc.getCouponQuota());
            // 优惠券ID
            result.put("couponGrantId", cuc.getId());
        }
        return result;

    }


    /**
     * @Description 真正开始加入计划
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/20 15:04
     */
    private boolean updateAfterPlanRedis(TenderRequest request, HjhPlanVO plan) {
        String accountStr = request.getAccount();
        Integer userId = request.getUser().getUserId();
        String planOrderId = GetOrderIdUtils.getOrderId0(userId);
        int nowTime = GetDate.getNowTime10();
        UserInfoVO userInfo = amUserClient.findUsersInfoById(userId);
        BigDecimal planApr = plan.getExpectApr();// 预期年利率
        Integer planPeriod = plan.getLockPeriod();// 周期
        String borrowStyle = plan.getBorrowStyle();// 还款方式
        BigDecimal earnings = BigDecimal.ZERO;// 预期收益
        if ("endday".equals(borrowStyle)) {
            // 还款方式为”按天计息，到期还本还息“：历史回报=投资金额*年化收益÷365*锁定期；
            earnings = DuePrincipalAndInterestUtils
                    .getDayInterest(new BigDecimal(accountStr), planApr.divide(new BigDecimal("100")), planPeriod)
                    .divide(new BigDecimal("1"), 2, BigDecimal.ROUND_DOWN);
        } else {
            // 还款方式为”按月计息，到期还本还息“：历史回报=投资金额*年化收益÷12*月数；
            earnings = DuePrincipalAndInterestUtils
                    .getMonthInterest(new BigDecimal(accountStr), planApr.divide(new BigDecimal("100")), planPeriod)
                    .divide(new BigDecimal("1"), 2, BigDecimal.ROUND_DOWN);

        }
        // 当大于等于100时 取百位 小于100 时 取十位
        BigDecimal accountDecimal = new BigDecimal(accountStr);//用户投资金额
        /*(1)汇计划加入明细表插表开始*/
        // 处理汇计划加入明细表(以下涵盖所有字段)
        HjhAccedeVO planAccede = new HjhAccedeVO();
        planAccede.setAccedeOrderId(planOrderId);
        planAccede.setPlanNid(request.getBorrowNid());
        planAccede.setUserId(userId);
        planAccede.setUserName(request.getUser().getUsername());
        planAccede.setUserAttribute(userInfo.getAttribute());//用户属性 0=>无主单 1=>有主单 2=>线下员工 3=>线上员工
        planAccede.setAccedeAccount(accountDecimal);// 加入金额
        planAccede.setAlreadyInvest(BigDecimal.ZERO);//已投资金额(投资时维护)
        planAccede.setClient(Integer.parseInt(request.getPlatform()));
        planAccede.setOrderStatus(0);//0自动投标中 2自动投标成功 3锁定中 5退出中 7已退出 99 自动投资异常(投资时维护)
        planAccede.setAddTime(nowTime);
        planAccede.setCountInterestTime(0);//计息时间(最后放款时维护)
        planAccede.setSendStatus(0);//协议发送状态0未发送 1已发送
        planAccede.setLockPeriod(planPeriod);
        planAccede.setCommissionStatus(0);//提成计算状态:0:未计算,1:已计算
        planAccede.setAvailableInvestAccount(accountDecimal);//可投金额初始与加入金额一致
        planAccede.setWaitTotal(BigDecimal.ZERO);//(投资时维护)
        planAccede.setWaitCaptical(BigDecimal.ZERO);//(投资时维护)
        planAccede.setWaitInterest(BigDecimal.ZERO);//(投资时维护)
        planAccede.setReceivedTotal(BigDecimal.ZERO);//(退出时维护)
        planAccede.setReceivedInterest(BigDecimal.ZERO);//(退出时维护)
        planAccede.setReceivedCapital(BigDecimal.ZERO);//(退出时维护)
        planAccede.setQuitTime(0);//(退出时维护)
        planAccede.setLastPaymentTime(0);//最后回款时间(复审时维护)
        planAccede.setAcctualPaymentTime(0);//实际回款时间(退出时维护)
        planAccede.setShouldPayTotal(accountDecimal.add(earnings));//应还总额 = 应还本金 +应还利息
        planAccede.setShouldPayCapital(accountDecimal);
        planAccede.setShouldPayInterest(earnings);
        planAccede.setCreateUser(userId);
        planAccede.setDelFlag(0);//初始未未删除
        // 给加入明细用的
        request.setPlanOrderId(planOrderId);
        request.setEarnings(earnings);
        request.setAccountDecimal(accountDecimal);
        request.setNowTime(nowTime);
        if (Validator.isNotNull(userInfo)) {
            UserVO spreadsUsers = amUserClient.getSpreadsUsersByUserId(userId);

            if (spreadsUsers != null) {
                int refUserId = spreadsUsers.getUserId();
                logger.info("推荐人信息：" + refUserId);
                // 查找用户推荐人详情信息  部门啥的
                UserInfoCrmVO userInfoCustomize = amUserClient.queryUserCrmInfoByUserId(refUserId);
                if (Validator.isNotNull(userInfoCustomize)) {
                    planAccede.setInviteUserId(userInfoCustomize.getUserId());
                    planAccede.setInviteUserName(userInfoCustomize.getUserName());
                    planAccede.setInviteUserAttribute(userInfoCustomize.getAttribute());
                    planAccede.setInviteUserRegionname(userInfoCustomize.getRegionName());
                    planAccede.setInviteUserBranchname(userInfoCustomize.getBranchName());
                    planAccede.setInviteUserDepartmentname(userInfoCustomize.getDepartmentName());
                    logger.info("InviteUserName: " + userInfoCustomize.getUserName());
                    logger.info("InviteUserRegionname: " + userInfoCustomize.getRegionName());
                    logger.info("InviteUserBranchname: " + userInfoCustomize.getBranchName());
                    logger.info("InviteUserDepartmentname: " + userInfoCustomize.getDepartmentName());
                }
            } else if (userInfo.getAttribute() == 2 || userInfo.getAttribute() == 3) {
                // 查找用户推荐人详情信息
                UserInfoCrmVO userInfoCustomize = amUserClient.queryUserCrmInfoByUserId(userId);
                if (Validator.isNotNull(userInfoCustomize)) {
                    planAccede.setInviteUserId(userInfoCustomize.getUserId());
                    planAccede.setInviteUserName(userInfoCustomize.getUserName());
                    planAccede.setInviteUserAttribute(userInfoCustomize.getAttribute());
                    planAccede.setInviteUserRegionname(userInfoCustomize.getRegionName());
                    planAccede.setInviteUserBranchname(userInfoCustomize.getBranchName());
                    planAccede.setInviteUserDepartmentname(userInfoCustomize.getDepartmentName());
                }
            }
        }
        planAccede.setRequest(request);
        // 插入汇计划加入明细表
        boolean trenderFlag = amBorrowClient.insertHJHPlanAccede(planAccede);
        logger.info("投资明细表插入完毕,userId{},平台{},结果{}", userId, request.getPlatform(), trenderFlag);
        if (trenderFlag) {//加入明细表插表成功的前提下，继续
            //crm投资推送
            // TODO: 2018/6/22  crm投资推送
           /* rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGES_NAME,
                    RabbitMQConstants.ROUTINGKEY_POSTINTERFACE_CRM, JSON.toJSONString(planAccede));*/
            // 不用更新用户是不是新手  通过查询获得
            /*UserVO user = amUserClient.findUserById(userId);
            if (user != null) {
                if (user.getInvestflag() == 0) {
                    user.setInvestflag(1);
                    boolean updateUserFlag = amUserClient.updateByPrimaryKeySelective(user);
                }
            }*/
            // 更新  渠道统计用户累计投资  和  huiyingdai_utm_reg的首投信息 开始
            this.updateUtm(request, plan);
        }
        // 优惠券投资开始
        Integer couponGrantId = request.getCouponGrantId();
        if (couponGrantId != null && couponGrantId != 0) {
            // 优惠券投资校验
            try {
                // 检查优惠券能用不
                logger.info("优惠券投资校验开始,userId{},平台{},券为:{}", userId, request.getPlatform(), couponGrantId);
                Map<String, String> validateMap = couponService.validateCoupon(userId, accountStr, couponGrantId,
                        request.getPlatform(), plan);
                if (MapUtils.isEmpty(validateMap)) {
                    // 校验通过 进行优惠券投资投资
                    logger.info("优惠券投资校验成功,userId{},券为:{}", userId, couponGrantId);
                    this.couponTender(request, plan);
                } else {
                    logger.info("优惠券投资校验失败返回结果{},userId{},券为:{}", validateMap.get("statusDesc"), userId, couponGrantId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 更新  渠道统计用户累计投资  和  huiyingdai_utm_reg的首投信息 开始
     *
     * @param request
     * @param plan
     */
    private void updateUtm(TenderRequest request, HjhPlanVO plan) {
        //更新汇计划列表成功的前提下
        // 更新渠道统计用户累计投资
        // 投资人信息
        UserVO users = amUserClient.findUserById(request.getUser().getUserId());
        if (users != null) {
            // 更新渠道统计用户累计投资 从mongo里面查询
            AppChannelStatisticsDetailVO appChannelStatisticsDetails = amMongoClient.getAppChannelStatisticsDetailByUserId(users.getUserId());
            if (appChannelStatisticsDetails != null) {
                // TODO: 2018/6/22  发送消息到mq
                // appChannelStatisticsDetails != null && appChannelStatisticsDetails.size() == 1) {
              /*  AppChannelStatisticsDetail channelDetail = appChannelStatisticsDetails.get(0);
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("id", channelDetail.getId());
                // 认购本金
                params.put("accountDecimal", accountDecimal);
                // 投资时间
                params.put("investTime", nowTime);
                // 项目类型
                params.put("projectType", "汇计划");
                // 首次投标项目期限
                String investProjectPeriod = debtPlan.getLockPeriod() + "天";
                params.put("investProjectPeriod", investProjectPeriod);
                // 更新渠道统计用户累计投资
                if (users.getInvestflag() == 1) {
                    this.appChannelStatisticsDetailCustomizeMapper.updateAppChannelStatisticsDetail(params);
                } else if (users.getInvestflag() == 0) {
                    // 更新首投投资
                    this.appChannelStatisticsDetailCustomizeMapper.updateFirstAppChannelStatisticsDetail(params);
                }
                logger.info("用户:"+ userId+ "***********************************预更新渠道统计表AppChannelStatisticsDetail，订单号："+ planAccede.getAccedeOrderId());
           */
            } else {
                // 更新huiyingdai_utm_reg的首投信息
                UtmRegVO utmReg = amUserClient.findUtmRegByUserId(request.getUser().getUserId());
                if (utmReg != null) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put("id", utmReg.getId());
                    params.put("accountDecimal", request.getAccountDecimal());
                    // 投资时间
                    params.put("investTime", request.getNowTime());
                    // 项目类型
                    params.put("projectType", "汇计划");
                    String investProjectPeriod = "";
                    // 首次投标项目期限
                    String borrowStyle = plan.getBorrowStyle();// 还款方式
                    if ("endday".equals(borrowStyle)) {
                        investProjectPeriod = plan.getLockPeriod() + "天";
                    } else {
                        investProjectPeriod = plan.getLockPeriod() + "月";
                    }
                    // 首次投标项目期限
                    params.put("investProjectPeriod", investProjectPeriod);
                    // 更新渠道统计用户累计投资
                    if (users.getInvestflag() == 0) {
                        // 更新huiyingdai_utm_reg的首投信息
                        boolean updateUtmFlag = amUserClient.updateFirstUtmReg(params);
                    }
                }
            }
        }
        /*(6)更新  渠道统计用户累计投资  和  huiyingdai_utm_reg的首投信息 结束*/
    }

    /**
     * 进行优惠券投资
     *
     * @param request
     * @param plan
     */
    private void couponTender(TenderRequest request, HjhPlanVO plan) {
        String accountStr = request.getAccount();
        Integer userId = request.getUser().getUserId();
        Integer couponGrantId = request.getCouponGrantId();
        CouponUserVO cuc = couponClient.getCouponUser(request.getCouponGrantId(), userId);
        logger.info("优惠券投资开始,userId{},平台{},券为:{}", userId, request.getPlatform(), couponGrantId);
        couponService.couponTender(request, plan, request.getBankOpenAccount(), cuc, request.getTenderAccount());
    }

    /**
     * @Description 检查投资金额
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/19 15:52
     */
    private void checkTenderMoney(TenderRequest request, HjhPlanVO plan, BankOpenAccountVO OpenAccount, CouponUserVO cuc, AccountVO tenderAccount) {
        String account = request.getAccount();
        // 投资金额必须是整数
        if (StringUtils.isEmpty(account)) {
            account = "0";
            request.setAccount(account);
        }
        if (!(StringUtils.isNotEmpty(account)
                || (StringUtils.isEmpty(account) && cuc != null && cuc.getCouponType() == 3)
                || (StringUtils.isEmpty(account) && cuc != null && cuc.getCouponType() == 1
                && cuc.getAddFlg() == 1))) {
            throw new CheckException(MsgEnum.ERR_ACTIVITY_NOT_EXIST);
        }
        // 投资金额小数点后超过两位
        if (account.contains(".")) {
            String accountSubstr = account.substring(account.indexOf(".") + 1);
            if (StringUtils.isNotEmpty(accountSubstr) && accountSubstr.length() > 2) {
                // 金额格式错误
                throw new ReturnMessageException(MsgEnum.ERR_FMT_MONEY);
            }
        }
        BigDecimal accountBigDecimal = new BigDecimal(account);
        int compareResult = accountBigDecimal.compareTo(BigDecimal.ZERO);
        if (compareResult < 0) {
            // 加入金额不能为负数
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_MONEY_NEGATIVE);
        }
        if ((compareResult == 0 && cuc == null)
                || (compareResult == 0 && cuc != null && cuc.getCouponType() == 2)) {
            // 投资金额不能为0
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_MONEY_ZERO);
        }
        if (compareResult > 0 && cuc != null && cuc.getCouponType() == 1
                && cuc.getAddFlg() == 1) {
            // 投资金额不能为0
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_COUPON_USE_ALONE);
        }
        String balance = RedisUtils.get(RedisConstants.HJH_PLAN + plan.getPlanNid());
        if (StringUtils.isEmpty(balance)) {
            // 您来晚了  下次再来
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_YOU_ARE_LATE);
        }
        // DB 该计划可投金额
        BigDecimal minInvest = plan.getMinInvestment();// 该计划的最小投资金额
        // 当剩余可投金额小于最低起投金额，不做最低起投金额的限制
        if (minInvest != null && minInvest.compareTo(BigDecimal.ZERO) != 0
                && new BigDecimal(balance).compareTo(minInvest) == -1) {
            if (accountBigDecimal.compareTo(new BigDecimal(balance)) == 1) {
                // 剩余可加入金额为" + balance + "元
                throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_MONEY_REMAIN);
            }
            if (accountBigDecimal.compareTo(new BigDecimal(balance)) != 0) {
                // 剩余可加入只剩" + balance + "元，须全部购买"
                //CheckUtil.check();
                throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_MONEY_LESS_NEED_BUY_ALL);
            }
        }
        if (accountBigDecimal.compareTo(plan.getMinInvestment()) == -1) {
            if (accountBigDecimal.compareTo(BigDecimal.ZERO) == 0) {
                if (cuc != null && cuc.getCouponType() != 3
                        && cuc.getCouponType() != 1) {
                    // plan.getMinInvestment() + "元起投"
                    throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_MIN_INVESTMENT);
                }
            } else {
                // plan.getMinInvestment() + "元起投"
                throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_MIN_INVESTMENT);
            }
        }
        BigDecimal max = plan.getMaxInvestment();
        if (max != null && max.compareTo(BigDecimal.ZERO) != 0
                && accountBigDecimal.compareTo(max) == 1) {
            // 项目最大加入额为" + max + "元
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_MAX_INVESTMENT);
        }
        if (accountBigDecimal.compareTo(plan.getAvailableInvestAccount()) > 0) {
            // 加入金额不能大于开放额度
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_GREATER_THAN_OPEN_LINE);
        }
        if (tenderAccount.getBankBalance().compareTo(accountBigDecimal) < 0) {
            // 你没钱了
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_MONEY_NOT_ENOUGH);
        }
        // 调用江西银行接口查询可用金额
        BigDecimal userBankBalance = this.getBankBalancePay(request.getUser().getUserId(),
                OpenAccount.getAccount());
        if (userBankBalance.compareTo(accountBigDecimal) < 0) {
            // 你又没钱了
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_MONEY_NOT_ENOUGH);
        }
        // redis剩余金额不足判断逻辑
        if (accountBigDecimal.compareTo(new BigDecimal(balance)) == 1) {
            // "项目太抢手了！剩余可加入金额只有" + balance + "元"
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_MONEY_REMAIN);
        }
        // 开放额度和阀值（1000）判断逻辑
        if (new BigDecimal(balance).compareTo(new BigDecimal(CustomConstants.TENDER_THRESHOLD)) == -1) {
            // 投资金额 != 开放额度
            if (accountBigDecimal.compareTo(new BigDecimal(balance)) != 0) {
                // 使用递增的逻辑
                if (plan.getInvestmentIncrement() != null
                        && BigDecimal.ZERO.compareTo((accountBigDecimal.subtract(minInvest)).remainder(plan.getInvestmentIncrement())) != 0) {
                    // 加入递增金额须为" + plan.getInvestmentIncrement() + " 元的整数倍
                    throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_MONEY_INTEGER_MULTIPLE);
                }
            }
        } else {
            // (用户投资额度 - 起投额度)%增量 = 0
            if (plan.getInvestmentIncrement() != null
                    && BigDecimal.ZERO.compareTo(accountBigDecimal.subtract(minInvest).remainder(plan.getInvestmentIncrement())) != 0
                    && accountBigDecimal.compareTo(new BigDecimal(balance)) == -1) {
                // 加入递增金额须为" + plan.getInvestmentIncrement() + " 元的整数倍
                throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_MONEY_INTEGER_MULTIPLE);
            }
        }
    }

    /**
     * @Description 检查用户状态  角色  授权状态 等  是否允许投资
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/19 11:37
     */
    private void checkUser(UserVO user, UserInfoVO userInfo) {
        if (userInfo.getRoleId() == 3) {// 担保机构用户
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_ONLY_LENDERS);
        }
        if (userInfo.getRoleId() == 2) {// 借款人不能投资
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_ONLY_LENDERS);
        }
        // 判断用户是否禁用
        if (user.getStatus() == 1) {// 0启用，1禁用
            throw new ReturnMessageException(MsgEnum.ERR_USER_INVALID);
        }
        // 用户未开户
        if (user.getBankOpenAccount() == 0) {
            throw new ReturnMessageException(MsgEnum.ERR_BANK_ACCOUNT_NOT_OPEN);
        }
        // 交易密码状态检查
        if (user.getIsSetPassword() == 0) {
            throw new ReturnMessageException(MsgEnum.ERR_TRADE_PASSWORD_NOT_SET);
        }
        // 检查用户授权状态
        HjhUserAuthVO userAuth = amUserClient.getHjhUserAuthVO(user.getUserId());
        // 为空则无授权
        if (userAuth == null) {
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_NEED_AUTO_INVEST);
        } else {
            if (userAuth.getAutoInvesStatus() == 0) {
                throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_NEED_AUTO_INVEST);
            }
            if (userAuth.getAutoCreditStatus() == 0) {
                throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_NEED_AUTO_DEBT);
            }
        }
        // 服务费授权校验
        if (userAuth.getAutoPaymentStatus() == 0) {
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_NEED_PAYMENT_AUTH);
        }
        // 风险测评校验
        this.checkEvaluation(user);
    }

    /**
     * @Description 检查redis里面的缓存  用户是否能投资
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/19 10:38
     */
    private boolean checkRedisToken(TenderRequest request) {
        String token_ = "";
        if (RedisUtils.exists(RedisConstants.HJH_TENDER_REPEAT + request.getUser().getUserId())) {
            // 如果已经有了
            String redisTenderToken = RedisUtils.get(token_ + request.getUser().getUserId());
            if (!redisTenderToken.equals(request.getTenderToken())) {
                logger.info("用户已经加入计划userId:{},ip:{},ip{},平台{}", request.getUser().getUserId(), request.getIp(), request.getPlatform());
                return false;
            }
        }

        return true;
    }
}
