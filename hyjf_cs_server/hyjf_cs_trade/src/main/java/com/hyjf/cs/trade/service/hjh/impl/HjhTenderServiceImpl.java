/*
 * @Copyright: 2005-2018 so_what. All rights reserved.
 */
package com.hyjf.cs.trade.service.hjh.impl;

import com.alibaba.fastjson.JSON;
import com.hyjf.am.resquest.trade.MyCouponListRequest;
import com.hyjf.am.resquest.trade.TenderRequest;
import com.hyjf.am.vo.datacollect.AppChannelStatisticsDetailVO;
import com.hyjf.am.vo.trade.account.AccountVO;
import com.hyjf.am.vo.trade.coupon.BestCouponListVO;
import com.hyjf.am.vo.trade.coupon.CouponUserVO;
import com.hyjf.am.vo.trade.hjh.HjhAccedeVO;
import com.hyjf.am.vo.trade.hjh.HjhPlanVO;
import com.hyjf.am.vo.user.*;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.constants.MsgCode;
import com.hyjf.common.enums.MsgEnum;
import com.hyjf.common.exception.CheckException;
import com.hyjf.common.exception.MQException;
import com.hyjf.common.exception.ReturnMessageException;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetCode;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.GetOrderIdUtils;
import com.hyjf.common.util.calculate.DuePrincipalAndInterestUtils;
import com.hyjf.common.validator.Validator;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.cs.trade.bean.TenderInfoResult;
import com.hyjf.cs.trade.client.AmMongoClient;
import com.hyjf.cs.trade.client.AmTradeClient;
import com.hyjf.cs.trade.client.AmUserClient;
import com.hyjf.cs.trade.mq.base.MessageContent;
import com.hyjf.cs.trade.mq.producer.AppChannelStatisticsDetailProducer;
import com.hyjf.cs.trade.mq.producer.HjhCouponTenderProducer;
import com.hyjf.cs.trade.mq.producer.UtmRegProducer;
import com.hyjf.cs.trade.service.consumer.CouponService;
import com.hyjf.cs.trade.service.hjh.HjhTenderService;
import com.hyjf.cs.trade.service.impl.BaseTradeServiceImpl;
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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
    private AmTradeClient amTradeClient;
    @Autowired
    private AmMongoClient amMongoClient;
    @Autowired
    private CouponService couponService;
    @Autowired
    private AppChannelStatisticsDetailProducer appChannelStatisticsProducer;
    @Autowired
    private UtmRegProducer utmRegProducer;
    @Autowired
    private HjhCouponTenderProducer hjhCouponTenderProducer;


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
        String key = RedisConstants.HJH_TENDER_REPEAT + userId;
        boolean checkTender = RedisUtils.tranactionSet(key, RedisConstants.TENDER_OUT_TIME);
        if(!checkTender){
            // 用户正在投资
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_IN_PROGRESS);
        }
        if (StringUtils.isEmpty(request.getBorrowNid())) {
            // 项目编号不能为空
            throw new ReturnMessageException(MsgEnum.STATUS_CE000013);
        }

        if(request.getPlatform()==null){
            // 投资平台不能为空
            throw new ReturnMessageException(MsgEnum.STATUS_ZC000018);
        }
        // 查询选择的优惠券
        CouponUserVO cuc = null;
        if (request.getCouponGrantId() != null && request.getCouponGrantId() > 0) {
            cuc = amTradeClient.getCouponUser(request.getCouponGrantId(), userId);
        }
        // 查询计划
        if (StringUtils.isEmpty(request.getBorrowNid())) {
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_PLAN_NOT_EXIST);
        }
        HjhPlanVO plan = amTradeClient.getPlanByNid(request.getBorrowNid());
        if (plan == null) {
            throw new ReturnMessageException(MsgEnum.FIND_PLAN_ERROR);
        }
        if (plan.getPlanInvestStatus() == 2) {
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_PLAN_CLOSE);
        }
        logger.info("加入计划投资校验开始userId:{},planNid:{},ip:{},平台{},优惠券:{}", userId, request.getBorrowNid(), request.getIp(), request.getPlatform(), request.getCouponGrantId());
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
        AccountVO tenderAccount = amTradeClient.getAccount(userId);
        // 检查投资金额
        checkTenderMoney(request, plan, account, cuc, tenderAccount);
        logger.info("加入计划投资校验通过userId:{},ip:{},平台{},优惠券为:{}", userId, request.getIp(), request.getPlatform(), request.getCouponGrantId());

        // 开始投资------------------------------------------------------------------------------------------------------------------------------------------
        return tender(request, plan, account, cuc, tenderAccount);
    }

    /**
     * 根据投资项目id历史回报
     *
     * @param tender
     * @return
     */
    @Override
    public WebResult<TenderInfoResult> getInvestInfo(TenderRequest tender) {
        TenderInfoResult investInfo = new TenderInfoResult();
        String planNid = tender.getBorrowNid();
        String money = tender.getAccount();
        // 获取优惠券编号
        Integer couponId = tender.getCouponGrantId();
        DecimalFormat df = CustomConstants.DF_FOR_VIEW;
        df.setRoundingMode(RoundingMode.FLOOR);
        HjhPlanVO plan = amTradeClient.getPlanByNid(planNid);
        if (null == plan) {
            // 计划不存在
            throw new CheckException(MsgEnum.ERR_AMT_TENDER_PLAN_NOT_EXIST);
        }
        WebViewUserVO loginUser = RedisUtils.getObj(tender.getToken(), WebViewUserVO.class);
        tender.setUser(loginUser);
        BigDecimal couponInterest = BigDecimal.ZERO;
        BestCouponListVO couponConfig = new BestCouponListVO();
        if (!"0".equals(plan.getCouponConfig()) && loginUser != null) {
            BigDecimal borrowApr = plan.getExpectApr();
            /** 计算最优优惠券开始 isThereCoupon 1是有最优优惠券，0无最有优惠券 */
            MyCouponListRequest request = new MyCouponListRequest();
            request.setBorrowNid(tender.getBorrowNid());
            request.setUserId(String.valueOf(loginUser.getUserId()));
            request.setPlatform(tender.getPlatform());
            if (couponId == null || couponId == 0) {
                couponConfig = amTradeClient.selectHJHBestCoupon(request);
            }
            if (couponConfig != null) {
                investInfo.setIsThereCoupon(1);
                if (couponConfig != null) {
                    if (couponConfig.getCouponType() == 1) {
                        couponInterest = couponService.getInterestDj(couponConfig.getCouponQuota(), couponConfig.getCouponProfitTime().intValue(), plan.getExpectApr());
                    } else {
                        couponInterest = couponService.getInterest(plan.getBorrowStyle(), couponConfig.getCouponType(), plan.getExpectApr(), couponConfig.getCouponQuota(), money, plan.getLockPeriod());
                    }

                    couponConfig.setCouponInterest(df.format(couponInterest));
                    if (couponConfig.getCouponType() == 2) {
                        borrowApr = borrowApr.add(couponConfig.getCouponQuota());
                    }
                    if (couponConfig.getCouponType() == 3) {
                        money = new BigDecimal(money).add(couponConfig.getCouponQuota()).toString();
                    }
                }
                couponConfig.setCouponInterest(df.format(couponInterest));
            } else {
                investInfo.setIsThereCoupon(0);
            }
            investInfo.setCouponConfig(couponConfig);
            /** 计算最优优惠券结束 */

            /** 可用优惠券张数开始 */
            int availableCouponListCount = amTradeClient.countHJHAvaliableCoupon(request);
            investInfo.setCouponAvailableCount(availableCouponListCount);
            /** 可用优惠券张数结束 */

            /** 获取用户优惠券总张数开始 */
            int recordTotal = amTradeClient.getUserCouponCount(tender.getUser().getUserId(), "0");
            investInfo.setRecordTotal(recordTotal);
            /** 获取用户优惠券总张数结束 */
            investInfo.setCouponCapitalInterest(df.format(couponInterest));
        } else {
            investInfo.setCouponAvailableCount(0);
        }

        BigDecimal earnings = new BigDecimal("0");
        // 如果投资金额不为空
        if (!StringUtils.isBlank(money) && new BigDecimal(money).compareTo(BigDecimal.ZERO) == 1 ||!(StringUtils.isEmpty(money) && couponConfig != null && couponConfig.getCouponType() == 1 && couponConfig.getAddFlg() == 1)) {
            // 收益率
            BigDecimal borrowApr = plan.getExpectApr();
            // 周期
            Integer borrowPeriod = plan.getLockPeriod();
            // 还款方式
            String borrowStyle = plan.getBorrowStyle();

            if (StringUtils.isNotEmpty(borrowStyle)) {

                if (StringUtils.equals("endday", borrowStyle)){
                    // 还款方式为”按天计息，到期还本还息“：历史回报=投资金额*年化收益÷365*锁定期；
                    earnings = DuePrincipalAndInterestUtils.getDayInterest(new BigDecimal(money), borrowApr.divide(new BigDecimal("100")), borrowPeriod).divide(new BigDecimal("1"), 2, BigDecimal.ROUND_DOWN);
                    investInfo.setEarnings(df.format(earnings));
                } else {
                    // 还款方式为”按月计息，到期还本还息“：历史回报=投资金额*年化收益÷12*月数；
                    earnings = DuePrincipalAndInterestUtils.getMonthInterest(new BigDecimal(money), borrowApr.divide(new BigDecimal("100")), borrowPeriod).divide(new BigDecimal("1"), 2, BigDecimal.ROUND_DOWN);
                    investInfo.setEarnings(df.format(earnings));
                }
            }
        } else {
            // 投资金额错误
            throw new CheckException(MsgEnum.ERR_AMT_TENDER_MONEY_FORMAT);
        }
        if(couponConfig!=null && couponConfig.getCouponType()==3){
            investInfo.setCapitalInterest(df.format(earnings.add(couponInterest).subtract(couponConfig.getCouponQuota())));
        }else{
            investInfo.setCapitalInterest(df.format(earnings.add(couponInterest)));
        }
        WebResult<TenderInfoResult> result = new WebResult<TenderInfoResult>();
        result.setData(investInfo);
        return result;
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
            couponService.couponTender(request, plan,  cuc, userId);
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
                                throw new ReturnMessageException(redisMsgCode);
                            } else {
                                logger.info("加计划redis操作成功userId:{},平台:{},planNid{},计划扣除后可用开放额度redis", userId, request.getPlatform(), plan.getPlanNid(), lastAccount);
                                // 写队列
                                break;
                            }
                        }
                    } else {
                        logger.info("您来晚了：userId:{},planNid{},金额{}元", userId, plan.getPlanNid(), balance);
                        redisMsgCode = MsgEnum.ERR_AMT_TENDER_YOU_ARE_LATE;
                        throw new ReturnMessageException(redisMsgCode);
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
        // 预期年利率
        BigDecimal planApr = plan.getExpectApr();
        // 周期
        Integer planPeriod = plan.getLockPeriod();
        // 还款方式
        String borrowStyle = plan.getBorrowStyle();
        // 预期收益
        BigDecimal earnings = BigDecimal.ZERO;
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
        //用户投资金额
        BigDecimal accountDecimal = new BigDecimal(accountStr);
        /*(1)汇计划加入明细表插表开始*/
        // 处理汇计划加入明细表(以下涵盖所有字段)
        HjhAccedeVO planAccede = new HjhAccedeVO();
        planAccede.setAccedeOrderId(planOrderId);
        planAccede.setPlanNid(request.getBorrowNid());
        planAccede.setUserId(userId);
        planAccede.setUserName(request.getUser().getUsername());
        //用户属性 0=>无主单 1=>有主单 2=>线下员工 3=>线上员工
        planAccede.setUserAttribute(userInfo.getAttribute());
        // 加入金额
        planAccede.setAccedeAccount(accountDecimal);
        //已投资金额(投资时维护)
        planAccede.setAlreadyInvest(BigDecimal.ZERO);
        planAccede.setClient(Integer.parseInt(request.getPlatform()));
        //0自动投标中 2自动投标成功 3锁定中 5退出中 7已退出 99 自动投资异常(投资时维护)
        planAccede.setOrderStatus(0);
        //计息时间(最后放款时维护)
        planAccede.setCountInterestTime(0);
        //协议发送状态0未发送 1已发送
        planAccede.setSendStatus(0);
        planAccede.setLockPeriod(planPeriod);
        //提成计算状态:0:未计算,1:已计算
        planAccede.setCommissionStatus(0);
        //可投金额初始与加入金额一致
        planAccede.setAvailableInvestAccount(accountDecimal);
        //(投资时维护)
        planAccede.setWaitTotal(BigDecimal.ZERO);
        //(投资时维护)
        planAccede.setWaitCaptical(BigDecimal.ZERO);
        //(投资时维护)
        planAccede.setWaitInterest(BigDecimal.ZERO);
        //(退出时维护)
        planAccede.setReceivedTotal(BigDecimal.ZERO);
        //(退出时维护)
        planAccede.setReceivedInterest(BigDecimal.ZERO);
        //(退出时维护)
        planAccede.setReceivedCapital(BigDecimal.ZERO);
        //(退出时维护)
        planAccede.setQuitTime(0);
        //最后回款时间(复审时维护)
        planAccede.setLastPaymentTime(0);
        //实际回款时间(退出时维护)
        planAccede.setAcctualPaymentTime(0);
        //应还总额 = 应还本金 +应还利息
        planAccede.setShouldPayTotal(accountDecimal.add(earnings));
        planAccede.setShouldPayCapital(accountDecimal);
        planAccede.setShouldPayInterest(earnings);
        planAccede.setCreateUser(userId);
        //初始未未删除
        planAccede.setDelFlag(0);
		//汇计划三期要求加入计划存当时的预期年化收益率 LIBIN PC  微服务三端共用此方法
		if(plan.getExpectApr() != null){
			planAccede.setExpectApr(plan.getExpectApr());
		}
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
        boolean trenderFlag = amTradeClient.insertHJHPlanAccede(planAccede);
        logger.info("投资明细表插入完毕,userId{},平台{},结果{}", userId, request.getPlatform(), trenderFlag);
        if (trenderFlag) {
            //加入明细表插表成功的前提下，继续
            //crm投资推送
            // TODO: 2018/6/22  crm投资推送
           /* rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGES_NAME,
                    RabbitMQConstants.ROUTINGKEY_POSTINTERFACE_CRM, JSON.toJSONString(planAccede));*/
            // 更新  渠道统计用户累计投资  和  huiyingdai_utm_reg的首投信息 开始
            this.updateUtm(request, plan);
        }
        // 优惠券投资开始
        Integer couponGrantId = request.getCouponGrantId();
        if (couponGrantId != null && couponGrantId != 0) {
            logger.info("开始优惠券投资,userId{},平台{},优惠券{}", userId, request.getPlatform(), couponGrantId);
            // 优惠券投资校验
            try {
                // 开始使用优惠券
                Map<String, String> params = new HashMap<String, String>();
                params.put("mqMsgId", GetCode.getRandomCode(10));
                // 真实投资金额
                params.put("money", accountStr);
                // 借款项目编号
                params.put("borrowNid", plan.getPlanNid());
                // 平台
                params.put("platform", request.getPlatform());
                // 优惠券id
                params.put("couponGrantId", couponGrantId+"");
                // ip
                params.put("ip", request.getIp());
                // 真实投资订单号
                params.put("ordId", request.getOrderId());
                // 用户编号
                params.put("userId", userId+"");
                params.put("account", request.getAccount());
                params.put("mainTenderNid", request.getMainTenderNid());
                hjhCouponTenderProducer.messageSend(new MessageContent(MQConstant.HJH_COUPON_TENDER_TOPIC, UUID.randomUUID().toString(), JSON.toJSONBytes(params)));

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
                Map<String, Object> params = new HashMap<String, Object>();
                // 认购本金
                params.put("accountDecimal", request.getAccountDecimal());
                // 投资时间
                params.put("investTime", request.getNowTime());
                // 项目类型
                params.put("projectType", "汇计划");
                // 首次投标项目期限
                String investProjectPeriod = "";
                // 还款方式
                String borrowStyle = plan.getBorrowStyle();
                if ("endday".equals(borrowStyle)) {
                    investProjectPeriod = plan.getLockPeriod() + "天";
                } else {
                    investProjectPeriod = plan.getLockPeriod() + "月";
                }
                params.put("investProjectPeriod", investProjectPeriod);
                //根据investFlag标志位来决定更新哪种投资
                params.put("investFlag", checkIsNewUserCanInvest(request.getUser().getUserId()));
                //压入消息队列
                try {
                    appChannelStatisticsProducer.messageSend(new MessageContent(MQConstant.APP_CHANNEL_STATISTICS_DETAIL_TOPIC,
                            MQConstant.APP_CHANNEL_STATISTICS_DETAIL_INVEST_TAG, UUID.randomUUID().toString(), JSON.toJSONBytes(params)));
                } catch (MQException e) {
                    e.printStackTrace();
                    logger.error("渠道统计用户累计投资推送消息队列失败！！！");
                }
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
                    // 首次投标项目期限 // 还款方式
                    String borrowStyle = plan.getBorrowStyle();
                    if ("endday".equals(borrowStyle)) {
                        investProjectPeriod = plan.getLockPeriod() + "天";
                    } else {
                        investProjectPeriod = plan.getLockPeriod() + "月";
                    }
                    // 首次投标项目期限
                    params.put("investProjectPeriod", investProjectPeriod);
                    // 更新渠道统计用户累计投资
                    if(this.checkIsNewUserCanInvest(request.getUser().getUserId())){
                        // 更新huiyingdai_utm_reg的首投信息
                        try {
                            utmRegProducer.messageSend(new MessageContent(MQConstant.STATISTICS_UTM_REG_TOPIC, UUID.randomUUID().toString(), JSON.toJSONBytes(params)));
                        } catch (MQException e) {
                            e.printStackTrace();
                            logger.error("更新huiyingdai_utm_reg的首投信息失败");
                        }
                    }
                }
            }
        }
        /*(6)更新  渠道统计用户累计投资  和  huiyingdai_utm_reg的首投信息 结束*/
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

}