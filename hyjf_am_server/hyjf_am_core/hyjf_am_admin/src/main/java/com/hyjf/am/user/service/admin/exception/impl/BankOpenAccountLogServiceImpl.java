package com.hyjf.am.user.service.admin.exception.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.admin.config.SystemConfig;
import com.hyjf.am.admin.mq.base.CommonProducer;
import com.hyjf.am.config.dao.mapper.auto.CardBinMapper;
import com.hyjf.am.config.dao.mapper.auto.JxBankConfigMapper;
import com.hyjf.am.config.dao.model.auto.CardBin;
import com.hyjf.am.config.dao.model.auto.CardBinExample;
import com.hyjf.am.config.dao.model.auto.JxBankConfig;
import com.hyjf.am.config.dao.model.auto.JxBankConfigExample;
import com.hyjf.am.config.service.BankConfigService;
import com.hyjf.am.response.user.OpenAccountEnquiryResponse;
import com.hyjf.am.resquest.admin.OpenAccountEnquiryDefineRequest;
import com.hyjf.am.resquest.user.BankCardRequest;
import com.hyjf.am.resquest.user.BankOpenAccountLogRequest;
import com.hyjf.am.user.dao.model.auto.*;
import com.hyjf.am.user.dao.model.customize.OpenAccountEnquiryCustomize;
import com.hyjf.am.user.service.admin.exception.BankOpenAccountLogService;
import com.hyjf.am.user.service.admin.membercentre.UserManagerService;
import com.hyjf.am.user.service.front.user.AppUtmRegService;
import com.hyjf.am.user.service.front.user.UserInfoService;
import com.hyjf.am.user.service.impl.BaseServiceImpl;
import com.hyjf.am.vo.admin.OpenAccountEnquiryDefineResultBeanVO;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.GetOrderIdUtils;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import com.hyjf.pay.lib.bank.util.BankCallUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * @version BankOpenAccountLogSrviceImpl, v0.1 2018/8/21 14:41
 * @Author: Zha Daojian
 */
@Service
public class BankOpenAccountLogServiceImpl extends BaseServiceImpl implements BankOpenAccountLogService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private SystemConfig systemConfig;

    @Autowired
    private UserManagerService userService;

    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    private BankConfigService bankConfigService;

    @Autowired
    private AppUtmRegService appUtmRegService;

    @Autowired
    private CommonProducer commonProducer;

    @Autowired
    private JxBankConfigMapper jxBankConfigMapper;

    @Autowired
    private CardBinMapper cardBinMapper;


    /**
     * 通过手机号和身份证查询用户信息
    * @author Zha Daojian
    * @date 2018/8/21 18:53
    * @param request
    * @return com.hyjf.am.user.dao.model.customize.OpenAccountEnquiryCustomize
    **/
    @Override
    public OpenAccountEnquiryCustomize accountEnquiry(BankOpenAccountLogRequest request) {
         return this.adminAccountCustomizeQuiryMapper.selectAccountEnquiry(request);
    }


    /**
     * 获取掉单用户信息
     *
     * @param mobile,idcard
     * @return java.util.List<com.hyjf.admin.beans.vo.BankOpenAccountLogVO>
     * @author Zha Daojian
     * @date 2018/8/21 13:54
     **/
    @Override
    public List<BankOpenAccountLog> bankOpenAccountLogSelect(String mobile,String idcard ) {
        logger.info("bankOpenAccountLogSelect:::::::::");
        // 借款人用户
        BankOpenAccountLogExample openAccoutLogExample = new BankOpenAccountLogExample();
        BankOpenAccountLogExample.Criteria crt = openAccoutLogExample.createCriteria();
        if(StringUtils.isNoneEmpty(idcard)){
            crt.andIdNoEqualTo(idcard);
        }
        if(StringUtils.isNoneEmpty(mobile)){
            crt.andMobileEqualTo(mobile);
        }
        List<BankOpenAccountLog> openAccountLogs = this.bankOpenAccountLogMapper.selectByExample(openAccoutLogExample);
        if (openAccountLogs != null && openAccountLogs.size()> 0) {
            return openAccountLogs;
        }
        return null;
    }

    /**
     * 获取掉单用户信息
     *
     * @param orderId
     * @return java.util.List<com.hyjf.admin.beans.vo.BankOpenAccountLogVO>
     * @author Zha Daojian
     * @date 2018/8/21 13:54
     **/
    @Override
    public BankOpenAccountLog selectBankOpenAccountLogByOrderId(String orderId) {

        logger.info("selectBankOpenAccountLogByOrderId:::::::::userId=[{}]",orderId);
        // 借款人用户
        BankOpenAccountLogExample openAccoutLogExample = new BankOpenAccountLogExample();
        BankOpenAccountLogExample.Criteria crt = openAccoutLogExample.createCriteria();
        crt.andOrderIdEqualTo(orderId);
        List<BankOpenAccountLog> openAccountLogs = this.bankOpenAccountLogMapper.selectByExample(openAccoutLogExample);
        if (openAccountLogs != null && openAccountLogs.size() == 1) {
            return openAccountLogs.get(0);
        }
        return null;
    }

    /**
     * 查询返回的电子账号是否已开户
     *
     * @param accountId
     * @return java.lang.Boolean
     * @author Zha Daojian
     * @date 2018/8/23 9:36
     **/
    @Override
    public Boolean checkAccountByAccountId(String accountId) {
        // 根据account查询用户是否开户
        BankOpenAccountExample example = new BankOpenAccountExample();
        example.createCriteria().andAccountEqualTo(accountId);
        List<BankOpenAccount> bankOpenList = this.bankOpenAccountMapper.selectByExample(example);
        if (bankOpenList != null && bankOpenList.size() > 0) {
            for (int i = 0; i < bankOpenList.size(); i++) {
                Integer userId = bankOpenList.get(i).getUserId();
                UserExample userExample = new UserExample();
                userExample.createCriteria().andUserIdEqualTo(userId);
                List<User> user = this.userMapper.selectByExample(userExample);
                if (user != null && user.size() > 0) {
                    for (int j = 0; j < user.size(); j++) {
                        User info = user.get(j);
                        Integer bankOpenFlag = info.getBankOpenAccount();
                        if (bankOpenFlag == 1) {
                            return true;
                        }
                    }
                }
            }

        }
        return false;
    }

    /**
     * 删除用户开户日志
     *
     * @param userId
     * @return java.lang.Boolean
     * @author Zha Daojian
     * @date 2018/8/23 9:36
     **/
    @Override
    public Boolean deleteBankOpenAccountLogByUserId(Integer userId) {
        BankOpenAccountLogExample accountLogExample = new BankOpenAccountLogExample();
        accountLogExample.createCriteria().andUserIdEqualTo(userId);
        boolean deleteLogFlag = this.bankOpenAccountLogMapper.deleteByExample(accountLogExample) > 0 ? true : false;
        return  deleteLogFlag;
    }

    /**
     * 根据USERID查询开户掉单
     *
     * @param userId
     * @return
     */
    @Override
    public List<BankOpenAccountLog> selectBankOpenAccountLogByUserId(Integer userId) {
        // 借款人用户
        BankOpenAccountLogExample openAccoutLogExample = new BankOpenAccountLogExample();
        BankOpenAccountLogExample.Criteria crt = openAccoutLogExample.createCriteria();
        crt.andUserIdEqualTo(userId);
        crt.andStatusEqualTo(0);
        openAccoutLogExample.setOrderByClause("id asc");
        List<BankOpenAccountLog> openAccountLogs = this.bankOpenAccountLogMapper.selectByExample(openAccoutLogExample);
        if (openAccountLogs != null && openAccountLogs.size()> 0) {
            return openAccountLogs;
        }
        return null;
    }

    /**
     * 保存开户(User)的数据
     */
    public OpenAccountEnquiryResponse updateUser(OpenAccountEnquiryDefineRequest requestBean) {
        logger.info("==========保存开户(User)的数据" );
        OpenAccountEnquiryDefineResultBeanVO resultBean= new OpenAccountEnquiryDefineResultBeanVO();
        OpenAccountEnquiryResponse response =  new OpenAccountEnquiryResponse();
        String idCard = requestBean.getIdcard();
        String platform = requestBean.getPlatform();//开户平台
        try {
            if (StringUtils.isEmpty(idCard)) {
                resultBean.setStatus(BankCallConstant.BANKOPEN_USER_ACCOUNT_N);
                resultBean.setResult("身份不能为空");
                response.setOpenAccountEnquiryDefineResultBeanVO(resultBean);
                return response;
            }
            if (StringUtils.isEmpty(platform)) {
                resultBean.setStatus(BankCallConstant.BANKOPEN_USER_ACCOUNT_N);
                resultBean.setResult("开户平台platform不能为空");
                response.setOpenAccountEnquiryDefineResultBeanVO(resultBean);
                return response;
            }
            Integer userId = Integer.valueOf(requestBean.getUserid());
            // 获取用户信息
            User user = userService.findUserByUserId(userId);
            // 开户更新开户渠道统计开户时间
            AppUtmReg appUtmReg = appUtmRegService.findByUserId(userId);
            logger.info("----------开户更新开户渠道统计开户时间。。。appUtmReg:"+JSONObject.toJSONString(appUtmReg));
            logger.info("----------开户更新开户渠道统计开户时间。。。,userId:"+userId);
            if (appUtmReg != null) {
                AppUtmReg appUtmRegUser = new AppUtmReg();
                BeanUtils.copyProperties(appUtmRegUser, appUtmReg);
                appUtmReg.setOpenAccountTime(GetDate.str2Date(requestBean.getRegTimeEnd(), GetDate.yyyyMMdd));
                logger.info("开户更新开户渠道统计开户时间。。。appUtmRegUser："+JSONObject.toJSONString(appUtmRegUser));
                appUtmRegService.updateByPrimaryKeySelective(appUtmRegUser);
            }
            BankCard card = userService.getBankCardByUserId(userId);
            if (card == null) {
                logger.info("开始保存银行卡信息。。。");
                BankCallBean bean = new BankCallBean();
                bean.setAccountId(requestBean.getAccountId());
                bean.setLogUserId(requestBean.getUserid());
                bean.setMobile(requestBean.getMobile());
                updateCardNoToBank(bean, user);
            }

           /* boolean deleteLogFlag = this.deleteBankOpenAccountLogByUserId(userId);
            if (!deleteLogFlag) {
                throw new Exception("删除用户开户日志表失败");
            }
            // 查询返回的电子账号是否已开户
            boolean result = checkAccountByAccountId(requestBean.getAccountId());
            if (result) {
                // 校验未通过
                logger.error("==========该电子账号已被用户关联,无法完成掉单修复!============关联电子账号: " + requestBean.getAccountId());
                throw new Exception("该电子账号已被用户关联,无法完成掉单修复!");
            }

            String trueName = requestBean.getName();
            if (idCard.length() < 18) {
                try {
                    idCard = IdCard15To18.getEighteenIDCard(idCard);
                } catch (Exception e) {
                    logger.error("===========身份证转换异常!=============");
                    throw new Exception("身份证转换异常!");
                }
            }
            int sexInt = Integer.parseInt(idCard.substring(16, 17));// 性别
            if (sexInt % 2 == 0) {
                sexInt = 2;
            } else {
                sexInt = 1;
            }
            String birthDayTemp = idCard.substring(6, 14);// 出生日期
            String birthDay = StringUtils.substring(birthDayTemp, 0, 4) + "-" + StringUtils.substring(birthDayTemp, 4, 6) + "-" + StringUtils.substring(birthDayTemp, 6, 8);
            user.setBankOpenAccount(1);
            user.setBankAccountEsb(Integer.parseInt(requestBean.getPlatform()));
            user.setRechargeSms(0);
            user.setWithdrawSms(0);
            user.setUserType(0);
            user.setIsSetPassword(getIsSetPassword(requestBean.getAccountId(), userId));
            user.setMobile(requestBean.getMobile());
            // 更新相应的用户表
            boolean usersFlag = userService.updateUserSelective(user) > 0 ? true : false;
            if (!usersFlag) {
                logger.error("更新用户表失败！");
                throw new Exception("更新用户表失败！");

            }
            UserInfo userInfo = userInfoService.findUsersInfo(userId);
            if (userInfo == null) {
                logger.error("用户详情表数据错误，用户id：" + user.getUserId());
                throw new Exception("用户详情表数据错误！");
            }
            userInfo.setTruename(trueName);
            userInfo.setIdcard(idCard);
            userInfo.setSex(sexInt);
            userInfo.setBirthday(birthDay);
            userInfo.setTruenameIsapprove(1);
            userInfo.setMobileIsapprove(1);
            // 更新用户详细信息表
            boolean userInfoFlag = userService.updateUserInfoByUserInfoSelective(userInfo) > 0 ? true : false;
            if (!userInfoFlag) {
                logger.error("更新用户详情表失败！");
                throw new Exception("更新用户详情表失败！");
            }
            // 插入江西银行关联表
            BankOpenAccount openAccount = new BankOpenAccount();
            openAccount.setUserId(userId);
            openAccount.setUserName(user.getUsername());
            openAccount.setAccount(requestBean.getAccountId());
            //openAccount.setCreateTime(GetDate.stringToDate(requestBean.getRegTimeEnd()));
            openAccount.setCreateUserId(userId);
            boolean openAccountFlag = userService.insertBankOpenAccount(openAccount) > 0 ? true : false;
            if (!openAccountFlag) {
                logger.error("插入用户开户表失败！");
                throw new Exception("插入用户开户表失败！");
            }

            // add 合规数据上报 埋点 liubin 20181122 start
            // 推送数据到MQ 开户 出借人
            if (requestBean.getRoleId().equals("1")) {
                JSONObject params = new JSONObject();
                params.put("userId", userId);
                commonProducer.messageSendDelay2(new MessageContent(MQConstant.HYJF_TOPIC, MQConstant.OPEN_ACCOUNT_SUCCESS_TAG, UUID.randomUUID().toString(), params),
                        MQConstant.HG_REPORT_DELAY_LEVEL);
            }
            // add by liuyang 20180227 开户掉单处理成功之后 发送法大大CA认证MQ  start
            // 加入到消息队列
            Map<String, String> params = new HashMap<String, String>();
            params.put("mqMsgId", GetCode.getRandomCode(10));
            params.put("userId", String.valueOf(userId));
            try {
                logger.info("开户异步处理，发送MQ，userId:[{}],mqMgId:[{}]", userId, params.get("mqMsgId"));

                commonProducer.messageSend(new MessageContent(MQConstant.FDD_CERTIFICATE_AUTHORITY_TOPIC, UUID.randomUUID().toString(), params));
            } catch (Exception e) {
                logger.error("开户掉单处理成功之后 发送法大大CA认证MQ消息失败！userId:[{}]", userId);
            }*/
        }catch (Exception e) {
            logger.error("开户掉单处理成功之后 发送法大大CA认证MQ消息失败！");
        }
        resultBean.setStatus(BankCallConstant.BANKOPEN_USER_ACCOUNT_Y);
        resultBean.setResult("开户掉单同步用户成功!");
        response.setOpenAccountEnquiryDefineResultBeanVO(resultBean);
        return response;
    }

    /**
     * 保存用户银行卡信息
     * @param bean
     * @return
     */
    private void updateCardNoToBank(BankCallBean bean,User user) {
        Integer userId = Integer.parseInt(bean.getLogUserId());
        logger.info("保存用户银行卡信息  userId {}   ",userId);
        // 调用江西银行接口查询用户绑定的银行卡
        BankCallBean cardBean = new BankCallBean(userId, BankCallConstant.TXCODE_CARD_BIND_DETAILS_QUERY, bean.getLogClient());
        cardBean.setChannel(bean.getChannel());// 交易渠道
        cardBean.setAccountId(bean.getAccountId());// 存管平台分配的账号
        cardBean.setState("1"); // 查询状态 0-所有（默认） 1-当前有效的绑定卡
        cardBean.setLogRemark("银行卡关系查询");
        // 调用江西银行查询银行卡
        BankCallBean call = BankCallUtils.callApiBg(cardBean);
        String respCode = call == null ? "" : call.getRetCode();
        logger.info("保存用户银行卡信息  银行返回码  {}   ",respCode);
        // 如果调用成功
        if (BankCallConstant.RESPCODE_SUCCESS.equals(respCode)) {
            String usrCardInfolist = call.getSubPacks();
            logger.info("保存用户银行卡信息  usrCardInfolist:{} ",JSONObject.toJSONString(usrCardInfolist));
            if (!StringUtils.isEmpty(usrCardInfolist)) {
                JSONArray array = JSONObject.parseArray(usrCardInfolist);
                if (array != null && array.size() > 0) {
                    logger.info("保存用户银行卡信息  array:{}",JSONObject.toJSONString(array));
                    // 查询有结果  取第一条
                    JSONObject obj = null;
                    obj = array.getJSONObject(0);
                    logger.info("保存用户银行卡信息  obj:{}",JSONObject.toJSONString(obj));
                    BankCardRequest bank = new BankCardRequest();
                    bank.setUserId(userId);
                    // 设置绑定的手机号
                    bank.setMobile(bean.getMobile());
                    bank.setUserName(user.getUsername());
                    bank.setStatus(1);// 默认都是1
                    bank.setCardNo(obj.getString("cardNo"));
                    // 银行联号
                    String payAllianceCode = "";
                    // 调用江西银行接口查询银行联号
                    logger.info("调用江西银行接口查询银行联号  cardNo:{},  userId:{} ",obj.getString("cardNo"),userId);
                    BankCallBean payAllianceCodeQueryBean = this.payAllianceCodeQuery(obj.getString("cardNo"), userId);
                    if (payAllianceCodeQueryBean != null && BankCallConstant.RESPCODE_SUCCESS.equals(payAllianceCodeQueryBean.getRetCode())) {
                        payAllianceCode = payAllianceCodeQueryBean.getPayAllianceCode();
                    } else {
                        logger.error("调用江西银行查询联行号失败，userId:{}", userId);
                    }
                    logger.info("------------------------------保存用户银行卡信息  payAllianceCode:{}",payAllianceCode);
                    SimpleDateFormat sdf = GetDate.yyyymmddhhmmss;
                    try {
                        bank.setCreateTime(sdf.parse(obj.getString("txnDate") + obj.getString("txnTime")));
                    } catch (ParseException e) {
                        logger.error("银行返回日期格式化失败，userId:{}", userId);
                    }
                    logger.info("保存用户银行卡信息  setCreateTime:{}",bank.getCreateTime());
                    bank.setCreateUserId(userId);
                    bank.setCreateUsername(user.getUsername());
                    // 根据银行卡号查询所  bankId
                    // 调用config原子层
                    //String bankId = queryBankIdByCardNo(bank.getCardNo());
                    String bankId ="";
                            logger.info("保存用户银行卡信息  bankId  {}   ",bankId);
                    if (!StringUtils.isEmpty(bankId)) {
                        bank.setBankId(Integer.parseInt(bankId));
                        JxBankConfig banksConfigVO = getJxBankConfigByBankId(Integer.parseInt(bankId));
                        if (banksConfigVO != null) {
                            bank.setBank(banksConfigVO.getBankName());
                            logger.info("保存用户银行卡所属银行  banksConfigVO.getBankName()  {}   ",banksConfigVO.getBankName());
                            // 如果联行号为空  则更新联行号
                            if (StringUtils.isEmpty(payAllianceCode)) {
                                payAllianceCode = banksConfigVO.getPayAllianceCode();
                            }
                        }
                    }else {
                        logger.error("根据银行卡号查询所  bankId失败，bankId:{}", bankId);
                    }
                    // 更新联行号
                    bank.setPayAllianceCode(payAllianceCode);
                    BankCard bankCardbean = new BankCard();
                    BeanUtils.copyProperties(bankCardbean, bank);
                    logger.info("保存银行卡信息，插入用户银行卡,bankCardbean:",JSONObject.toJSONString(bankCardbean));
                    boolean bankFlag = bankCardMapper.insertSelective(bankCardbean) > 0 ? true : false;
                    if (!bankFlag) {
                        logger.error("插入用户银行卡失败！");
                    }
                } else {
                    logger.error("更新银行卡信息出错，转换array失败，userId:{}", userId);
                }
            } else {
                logger.error("更新银行卡信息出错，返回空，userId:{}", userId);
            }
        } else {
            logger.error("更新银行卡信息出错，银行返回码  {},  BankCallConstant.RESPCODE_SUCCESS{}", respCode,BankCallConstant.RESPCODE_SUCCESS);
        }
    }

    private BankCallBean payAllianceCodeQuery(String cardNo, Integer userId) {
        logger.info("调用江西银行接口查询银行联号");
        BankCallBean bean = new BankCallBean();
        String channel = BankCallConstant.CHANNEL_PC;
        String orderDate = GetOrderIdUtils.getOrderDate();
        String txDate = GetOrderIdUtils.getTxDate();
        String txTime = GetOrderIdUtils.getTxTime();
        String seqNo = GetOrderIdUtils.getSeqNo(6);
        bean.setVersion(BankCallConstant.VERSION_10);// 版本号
        bean.setTxCode(BankCallConstant.TXCODE_PAY_ALLIANCE_CODE_QUERY);// 交易代码
        bean.setTxDate(txDate);
        bean.setTxTime(txTime);
        bean.setSeqNo(seqNo);
        bean.setChannel(channel);
        bean.setAccountId(cardNo);
        bean.setLogOrderId(GetOrderIdUtils.getOrderId2(userId));
        bean.setLogOrderDate(orderDate);
        bean.setLogUserId(String.valueOf(userId));
        bean.setLogRemark("联行号查询");
        bean.setLogClient(0);
        return BankCallUtils.callApiBg(bean);
    }

    /**
     * 根据银行卡号获取bankId
     * @param cardNo
     * @return
     */
    private String queryBankIdByCardNo(String cardNo) {
        logger.info("-------------根据银行卡号获取bankId  cardNo  {}   ",cardNo);
        String bankId = null;
        if (cardNo == null || cardNo.length() < 14 || cardNo.length() > 19) {
            return "";
        }
        // 把常用的卡BIN放到最前面
        // 6位卡BIN
        String cardBin_6 = cardNo.substring(0, 6);
        bankId = this.getBankId(6, cardBin_6);
        if (StringUtils.isNotBlank(bankId)) {
            return bankId;
        }
        // 7位卡BIN
        String cardBin_7 = cardNo.substring(0, 7);
        bankId = this.getBankId(7, cardBin_7);
        if (StringUtils.isNotBlank(bankId)) {
            return bankId;
        }
        // 8位卡BIN
        String cardBin_8 = cardNo.substring(0, 8);
        bankId = this.getBankId(8, cardBin_8);
        if (StringUtils.isNotBlank(bankId)) {
            return bankId;
        }
        // 9位卡BIN
        String cardBin_9 = cardNo.substring(0, 9);
        bankId = this.getBankId(9, cardBin_9);
        if (StringUtils.isNotBlank(bankId)) {
            return bankId;
        }
        // 2位卡BIN
        String cardBin_2 = cardNo.substring(0, 2);
        bankId = this.getBankId(2, cardBin_2);
        if (StringUtils.isNotBlank(bankId)) {
            return bankId;
        }
        // 3位卡BIN
        String cardBin_3 = cardNo.substring(0, 3);
        bankId = this.getBankId(3, cardBin_3);
        if (StringUtils.isNotBlank(bankId)) {
            return bankId;
        }
        // 4位卡BIN
        String cardBin_4 = cardNo.substring(0, 4);
        bankId = this.getBankId(4, cardBin_4);
        if (StringUtils.isNotBlank(bankId)) {
            return bankId;
        }
        // 5位卡BIN
        String cardBin_5 = cardNo.substring(0, 5);
        bankId = this.getBankId(5, cardBin_5);
        if (StringUtils.isNotBlank(bankId)) {
            return bankId;
        }
        // 10位卡BIN
        String cardBin_10 = cardNo.substring(0, 10);
        bankId = this.getBankId(10, cardBin_10);
        if (StringUtils.isNotBlank(cardBin_10)) {
            return bankId;
        }
        logger.info("=================根据银行卡号获取bankId  cardNo  {}   ",cardNo);
        return bankId;
    }

    private String getBankId(int cardBinLength, String cardBin) {
        CardBinExample example = new CardBinExample();
        CardBinExample.Criteria cra = example.createCriteria();
        cra.andBinLengthEqualTo(cardBinLength);
        cra.andBinValueEqualTo(cardBin);
        List<CardBin> list = this.cardBinMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0).getBankId();
        }
        return null;
    }

    /**
     * 根据bankId获取江西银行配置
     * @auth sunpeikai
     * @param
     * @return
     */

    private JxBankConfig getJxBankConfigByBankId(Integer bankId) {
        JxBankConfigExample example = new JxBankConfigExample();
        JxBankConfigExample.Criteria cra = example.createCriteria();
        //bankId
        cra.andBankIdEqualTo(bankId);
        //未删除
        cra.andDelFlagEqualTo(0);

       List<JxBankConfig> jxBankConfigList = jxBankConfigMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(jxBankConfigList)){
            return jxBankConfigList.get(0);
        }
        return null;
    }
}
