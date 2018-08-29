/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.service.recharge.impl;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.resquest.trade.HandleAccountRechargeRequest;
import com.hyjf.am.resquest.user.BankCardRequest;
import com.hyjf.am.vo.bank.BankCallBeanVO;
import com.hyjf.am.vo.trade.account.AccountRechargeVO;
import com.hyjf.am.vo.user.BankCardVO;
import com.hyjf.am.vo.user.BankOpenAccountVO;
import com.hyjf.am.vo.user.UserInfoVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.util.*;
import com.hyjf.common.validator.Validator;
import com.hyjf.cs.user.bean.BaseResultBean;
import com.hyjf.cs.user.bean.TrusteePayResultBean;
import com.hyjf.cs.user.bean.UserDirectRechargeRequestBean;
import com.hyjf.cs.user.bean.UserDirectRechargeResultBean;
import com.hyjf.cs.user.config.SystemConfig;
import com.hyjf.cs.user.constants.ErrorCodeConstant;
import com.hyjf.cs.user.service.impl.BaseUserServiceImpl;
import com.hyjf.cs.user.service.recharge.DirectRechargeService;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.bean.BankCallResult;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import com.hyjf.pay.lib.bank.util.BankCallMethodConstant;
import com.hyjf.pay.lib.bank.util.BankCallUtils;
import com.hyjf.soa.apiweb.CommonSoaUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: sunpeikai
 * @version: DirectRechargeServiceImpl, v0.1 2018/8/28 19:29
 */
@Service
public class DirectRechargeServiceImpl extends BaseUserServiceImpl implements DirectRechargeService {

    @Autowired
    private SystemConfig systemConfig;
    // 充值状态:充值中
    private static final int RECHARGE_STATUS_WAIT = 1;
    // 充值状态:失败
    private static final int RECHARGE_STATUS_FAIL = 3;
    // 充值状态:成功
    private static final int RECHARGE_STATUS_SUCCESS = 2;

    @Override
    public ModelAndView recharge(UserDirectRechargeRequestBean userRechargeRequestBean, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("/bank/user/trusteePay/error");
        logger.info("-----------充值页面-----------");
        UserDirectRechargeResultBean resultBean = new UserDirectRechargeResultBean();
        try {
            if (checkIsNull(userRechargeRequestBean)) {
                getErrorMV(userRechargeRequestBean, modelAndView, ErrorCodeConstant.STATUS_CE000001, "请求参数异常");
                logger.info("请求参数异常[" + JSONObject.toJSONString(userRechargeRequestBean, true) + "]");
                return modelAndView;
            }
            // 加签字段     时间戳  电子帐户号   手机号  idno   cardNo  txamount   name
            if (!this.verifyRequestSign(userRechargeRequestBean,"/server/user/directRechargePage/recharge")) {
                logger.info("----验签失败----");
                getErrorMV(userRechargeRequestBean, modelAndView, ErrorCodeConstant.STATUS_CE000002, "验签失败");
                logger.info("验签失败[" + JSONObject.toJSONString(userRechargeRequestBean, true) + "]");
                return modelAndView;
            }
            // 根据用户电子账户号查询用户信息
            BankOpenAccountVO bankOpenAccount = amUserClient.selectBankOpenAccountByAccountId(userRechargeRequestBean.getAccountId());
            if (bankOpenAccount == null) {
                logger.info("查询用户开户信息失败,用户电子账户号:[" + userRechargeRequestBean.getAccountId() + "]");
                getErrorMV(userRechargeRequestBean, modelAndView, ErrorCodeConstant.STATUS_CE000006, "查询用户开户信息失败");
                return modelAndView;
            }
            // 用户ID
            Integer userId = bankOpenAccount.getUserId();
            // 根据用户ID查询用户信息
            UserVO user = amUserClient.findUserById(userId);
            if (user == null) {
                getErrorMV(userRechargeRequestBean, modelAndView, ErrorCodeConstant.STATUS_CE000004, "查询用户开户信息失败");
                return modelAndView;
            }
            // 根据用户ID查询用户详情
            UserInfoVO userInfo = amUserClient.findUserInfoById(userId);
            if (userInfo == null) {
                getErrorMV(userRechargeRequestBean, modelAndView, ErrorCodeConstant.STATUS_CE000004, "查询用户开户信息失败");
                return modelAndView;
            }
            // 根据用户ID查询用户平台银行卡信息
            BankCardVO bankCard = amUserClient.getBankCardByUserId(userId);
            if (bankCard == null) {
                logger.info("根据用户ID查询用户银行卡信息失败,用户电子账户号:[" + userRechargeRequestBean.getAccountId() + "],用户ID:[" + userId + "].");
                getErrorMV(userRechargeRequestBean, modelAndView, ErrorCodeConstant.STATUS_BC000002, "查询用户银行卡信息失败");
                return modelAndView;
            }

            // 用户汇盈平台的银行卡卡号
            String localCardNo = bankCard.getCardNo() == null ? "" : bankCard.getCardNo();
            if (!userRechargeRequestBean.getCardNo().equals(localCardNo)) {
                logger.info("用户银行卡信息不一致,用户电子账户号:[" + userRechargeRequestBean.getAccountId() + "],请求银行卡号:[" + userRechargeRequestBean.getCardNo() + "],平台保存的银行卡号:[" + localCardNo + "].");
                getErrorMV(userRechargeRequestBean, modelAndView, ErrorCodeConstant.STATUS_NC000002, "用户银行卡信息不一致");
                return modelAndView;
            }

            // 检查是否设置交易密码
            Integer passwordFlag = user.getIsSetPassword();
            if (passwordFlag != 1) {// 未设置交易密码
                getErrorMV(userRechargeRequestBean, modelAndView, ErrorCodeConstant.STATUS_TP000002, "未设置交易密码！");
                return modelAndView;
            }
            // 缴费授权
            /*if (user.getPaymentAuthStatus() !=1) {
                logger.info("用户未进行缴费授权,用户电子账户号:[" + userRechargeRequestBean.getAccountId() + "],用户ID:[" + userId + "].");
                getErrorMV(userRechargeRequestBean, modelAndView, ErrorCodeConstant.STATUS_CE000011, "用户未进行缴费授权！");
                return modelAndView;
            }*/

            // 拼装参数  调用江西银行
            // 同步调用路径
            String retUrl = systemConfig.webHost + request.getContextPath()
                    + "/server/user/directRechargePage/directRechargePageReturn.do?acqRes="
                    + userRechargeRequestBean.getAcqRes() + StringPool.AMPERSAND + "callback="
                    + userRechargeRequestBean.getRetUrl().replace("#", "*-*-*");
            // 异步调用路
            String bgRetUrl = systemConfig.webHost + request.getContextPath()
                    + "/server/user/directRechargePage/directRechargePageBgreturn.do?acqRes="
                    + userRechargeRequestBean.getAcqRes() + "&phone="+userRechargeRequestBean.getMobile()+"&callback=" + userRechargeRequestBean.getBgRetUrl().replace("#", "*-*-*");

            // 用户ID

            BankCallBean bankCallBean = new BankCallBean();
            // 充值订单号
            String logOrderId = GetOrderIdUtils.getOrderId2(userId);
            // 充值订单日期
            String orderDate = GetOrderIdUtils.getOrderDate();
            // 调用 2.3.4联机绑定卡到电子账户充值
            BankCallBean bean = new BankCallBean();
            bean.setVersion(BankCallConstant.VERSION_10);// 接口版本号
            bean.setTxCode(BankCallMethodConstant.TXCODE_DIRECT_RECHARGE_PAGE);// 交易代码
            bean.setInstCode(systemConfig.bankInstcode);// 机构代码
            bean.setBankCode(systemConfig.bankCode);// 银行代码
            bean.setTxDate(GetOrderIdUtils.getTxDate()); // 交易日期
            bean.setTxTime(GetOrderIdUtils.getTxTime()); // 交易时间
            bean.setSeqNo(GetOrderIdUtils.getSeqNo(6));// 交易流水号
            bean.setChannel(userRechargeRequestBean.getChannel()); // 交易渠道
            bean.setAccountId(userRechargeRequestBean.getAccountId()); // 电子账号
            bean.setCardNo(localCardNo); // 银行卡号
            bean.setCurrency(BankCallConstant.CURRENCY_TYPE_RMB);
            bean.setTxAmount(userRechargeRequestBean.getTxAmount());
            bean.setIdType(BankCallConstant.ID_TYPE_IDCARD); // 证件类型
            bean.setIdNo(userRechargeRequestBean.getIdNo()); // 身份证号
            bean.setName(userRechargeRequestBean.getName()); // 姓名
            bean.setMobile(userRechargeRequestBean.getMobile()); // 手机号
            bean.setForgotPwdUrl(userRechargeRequestBean.getForgotPwdUrl());
            bean.setUserIP(CustomUtil.getIpAddr(request));
            bean.setRetUrl(retUrl);
            bean.setNotifyUrl(bgRetUrl);
            bean.setLogOrderId(logOrderId);// 订单号
            bean.setLogOrderDate(orderDate);// 充值订单日期
            bean.setLogUserId(String.valueOf(userId));
            bean.setLogUserName(user.getUsername());
            bean.setLogRemark("充值页面");
            bean.setLogClient(Integer.parseInt(userRechargeRequestBean.getPlatform()));// 充值平台
            // 充值成功后跳转的url
            bean.setSuccessfulUrl(bean.getRetUrl()+"&isSuccess=1");
            // 页面调用必须传的
            bean.setLogBankDetailUrl(BankCallConstant.BANK_URL_DIRECT_RECHARGE_PAGE);
            // 插入充值记录
            int result = insertRechargeInfo(bean);
            if (result == 0) {
                throw new Exception("插入充值记录失败,userid=["+userId+"].accountid=["+userRechargeRequestBean.getAccountId()+"]");
            }
            // 跳转到汇付天下画面
            try {
                modelAndView = BankCallUtils.callApi(bean);
            } catch (Exception e) {
                e.printStackTrace();
            }


            return modelAndView;
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("充值发生异常,错误信息:[" + e.getMessage() + "]");
            // 充值失败
            resultBean.setStatus(BaseResultBean.STATUS_FAIL);
            resultBean.setStatusDesc("充值失败");
            return modelAndView;
        }
    }

    @Override
    public ModelAndView pageReturn(HttpServletRequest request, BankCallBean bean) {
        ModelAndView modelAndView = new ModelAndView("/callback/callback_trusteepay");
        TrusteePayResultBean repwdResult = new TrusteePayResultBean();
        repwdResult.setCallBackAction(request.getParameter("callback").replace("*-*-*", "#"));
        String isSuccess = request.getParameter("isSuccess");
        if (bean == null) {
            logger.info("调用江西银行页面充值接口,银行返回空");
            modelAndView.addObject("statusDesc", "页面充值,调用银行接口失败！");
            BaseResultBean resultBean = new BaseResultBean();
            resultBean.setStatusForResponse(ErrorCodeConstant.STATUS_CE999999);
            repwdResult.set("chkValue", resultBean.getChkValue());
            repwdResult.set("status", resultBean.getStatus());
            repwdResult.set("acqRes", request.getParameter("acqRes"));
            modelAndView.addObject("callBackForm", repwdResult);
            return modelAndView;
        }
        if (isSuccess != null && "1".equals(isSuccess)) {
            logger.info("页面充值成功,跳转到成功链接用户ID:[" + bean.getLogUserId() + "]");
            // 成功
            BaseResultBean resultBean = new BaseResultBean();
            modelAndView.addObject("statusDesc", "充值成功");
            resultBean.setStatusForResponse(ErrorCodeConstant.SUCCESS);
            repwdResult.set("chkValue", resultBean.getChkValue());
            repwdResult.set("status", resultBean.getStatus());
            repwdResult.set("acqRes", request.getParameter("acqRes"));
            modelAndView.addObject("callBackForm", repwdResult);
            logger.info("页面充值同步第三方返回参数："+JSONObject.toJSONString(repwdResult));
            return modelAndView;
        }
        // 银行返回响应代码
        String retCode = StringUtils.isNotBlank(bean.getRetCode()) ? bean.getRetCode() : "";

        if (!BankCallConstant.RESPCODE_SUCCESS.equals(retCode)) {
            // 根据银行相应代码,查询错误信息
            String retMsg = amConfigClient.getBankRetMsg(retCode);
            logger.info("页面充值失败,银行返回响应代码:[" + retCode + "],retMsg:[" + retMsg + "]");

            BaseResultBean resultBean = new BaseResultBean();
            modelAndView.addObject("statusDesc", "充值失败,调用银行接口失败");
            resultBean.setStatusForResponse(ErrorCodeConstant.STATUS_CE999999);
            repwdResult.set("chkValue", resultBean.getChkValue());
            repwdResult.set("status", resultBean.getStatus());
            repwdResult.set("acqRes", request.getParameter("acqRes"));
            modelAndView.addObject("callBackForm", repwdResult);
            logger.info("页面充值同步第三方返回参数："+JSONObject.toJSONString(repwdResult));
            return modelAndView;
        }else{
            logger.info("页面充值成功,用户ID:[" + bean.getLogUserId() + "]");
            // 成功
            BaseResultBean resultBean = new BaseResultBean();
            modelAndView.addObject("statusDesc", "充值成功");
            resultBean.setStatusForResponse(ErrorCodeConstant.SUCCESS);
            repwdResult.set("chkValue", resultBean.getChkValue());
            repwdResult.set("status", resultBean.getStatus());
            repwdResult.set("acqRes", request.getParameter("acqRes"));
            modelAndView.addObject("callBackForm", repwdResult);
            logger.info("页面充值同步第三方返回参数："+JSONObject.toJSONString(repwdResult));
            return modelAndView;
        }
    }

    @Override
    public BankCallResult bgreturn(HttpServletRequest request, BankCallBean bean) {
        BankCallResult result = new BankCallResult();
        BaseResultBean resultBean = new BaseResultBean();
        Map<String, String> params = new HashMap<String, String>();

        if (bean == null) {
            logger.info("调用江西银行充值接口,银行异步返回空");
            params = setResult(resultBean,result,ErrorCodeConstant.STATUS_CE999999,"充值失败,调用银行接口失败",request.getParameter("acqRes"),true);
            CommonSoaUtils.noRetPostThree(request.getParameter("callback").replace("*-*-*", "#"), params);
            return result;
        }

        String phone = request.getParameter("phone");
        bean.setMobile(phone);
        bean.convert();
        int userId = Integer.parseInt(bean.getLogUserId());
        // 银行返回响应代码
        String retCode = StringUtils.isNotBlank(bean.getRetCode()) ? bean.getRetCode() : "";
        String retMsg = amConfigClient.getBankRetMsg(retCode);
        HandleAccountRechargeRequest rechargeRequest = new HandleAccountRechargeRequest();
        rechargeRequest.setBankCallBeanVO(CommonUtils.convertBean(bean,BankCallBeanVO.class));
        rechargeRequest.setParams(params);
        rechargeRequest.setErrorMsg(retMsg);
        rechargeRequest.setUserVO(amUserClient.findUserById(userId));
        rechargeRequest.setUserInfoVO(amUserClient.findUserInfoById(userId));
        String resultStr = amTradeClient.handleRechargeOnlineInfo(rechargeRequest);
        JSONObject msg = JSONObject.parseObject(resultStr);
        if (!BankCallConstant.RESPCODE_SUCCESS.equals(retCode)) {
            // 根据银行相应代码,查询错误信息
            logger.info("充值失败,银行返回响应代码:[" + retCode + "],retMsg:[" + retMsg + "],订单号:[" + bean.getLogOrderId() + "].");
            params = setResult(resultBean,result,ErrorCodeConstant.STATUS_CE999999,"充值失败,调用银行接口失败",request.getParameter("acqRes"),true);
            logger.info("页面充值异步第三方返回参数："+JSONObject.toJSONString(params));
            CommonSoaUtils.noRetPostThree(request.getParameter("callback").replace("*-*-*", "#"), params);
            return result;
        }else{
            // 充值成功
            if (msg != null && msg.get("error").equals("0")) {
                logger.info("充值成功,手机号:[" + bean.getMobile() + "],用户ID:[" + userId + "],充值金额:[" + bean.getTxAmount() + "]");
                if(msg.getBoolean("flag")){
                    //这一步本来在amTradeClient.handleRechargeInfo这里面执行，但是跨库了
                    String cardId = msg.getString("cardId");
                    updateBankMobile(userId,cardId,bean.getMobile());
                }
                params = setResult(resultBean,result,ErrorCodeConstant.SUCCESS,"充值成功",request.getParameter("acqRes"),true);
                params.put("ip", bean.getUserIP());
                params.put("mobile",bean.getMobile());
                params.put("txAmount", bean.getTxAmount());
                params.put("orderId", bean.getLogOrderId());
                logger.info("页面充值异步第三方返回参数："+JSONObject.toJSONString(params));
                CommonSoaUtils.noRetPostThree(request.getParameter("callback").replace("*-*-*", "#"), params);
                return result;
            } else {
                logger.info("充值失败,手机号:[" + bean.getMobile() + "],用户ID:[" + userId + "],冲值金额:[" + bean.getTxAmount() + "],失败原因:[" + msg.get("data") + "].");
                params = setResult(resultBean,result,ErrorCodeConstant.STATUS_CE999999,"充值失败",request.getParameter("acqRes"),false);
                params.put("ip", bean.getUserIP());
                params.put("mobile",bean.getMobile());
                logger.info("页面充值异步第三方返回参数："+JSONObject.toJSONString(params));
                CommonSoaUtils.noRetPostThree(request.getParameter("callback").replace("*-*-*", "#"), params);
                return result;
            }
        }
    }

    /**
     * 更新银行预留手机号
     * @param userId
     * @param cardNo
     * @param newMobile
     */
    private void updateBankMobile(Integer userId, String cardNo, String newMobile){

        // 成功后更新银行预留手机号码
        if (StringUtils.isBlank(newMobile) || StringUtils.isBlank(cardNo)) {
            return;
        }
        BankCardRequest request = new BankCardRequest();
        request.setUserId(userId);
        request.setCardNo(cardNo);

        BankCardVO bankCardVO = amUserClient.selectBankCardByUserIdAndCardNo(request);
        if (bankCardVO != null) {

            if(bankCardVO.getMobile() == null || !bankCardVO.getMobile().equals(newMobile)){
                BankCardRequest bankCardRequest = new BankCardRequest();
                bankCardRequest.setId(bankCardVO.getId());
                bankCardRequest.setMobile(newMobile);
                bankCardRequest.setUpdateTime(new Date());
                bankCardRequest.setUpdateUserId(userId);
                amUserClient.updateUserCard(bankCardRequest);
            }
        }
    }

    /**
     * 预插入短信充值记录表
     * @auth sunpeikai
     * @param
     * @return
     */
    private int insertRechargeInfo(BankCallBean bean){
        int ret = 0;
        String ordId = bean.getLogOrderId() == null ? "" : bean.getLogOrderId(); // 订单号
        List<AccountRechargeVO> listAccountRecharge = amTradeClient.selectAccountRechargeByOrderId(ordId);
        if (listAccountRecharge != null && listAccountRecharge.size() > 0) {
            return ret;
        }
        int nowTime = GetDate.getNowTime10(); // 当前时间
        // 银行卡号
        String cardNo = bean.getCardNo();
        // 根据银行卡号检索银行卡信息
        // 组装请求参数
        BankCardRequest bankCardRequest = new BankCardRequest();
        bankCardRequest.setUserId(Integer.parseInt(bean.getLogUserId()));
        bankCardRequest.setCardNo(cardNo);
        BankCardVO bankCard = amUserClient.selectBankCardByUserIdAndCardNo(bankCardRequest);
        BigDecimal money = new BigDecimal(bean.getTxAmount()); // 充值金额
        AccountRechargeVO record = new AccountRechargeVO();
        record.setNid(bean.getLogOrderId()); // 订单号
        record.setUserId(Integer.parseInt(bean.getLogUserId())); // 用户ID
        record.setUsername(bean.getLogUserName());// 用户 名
        record.setTxDate(Integer.parseInt(bean.getTxDate()));// 交易日期
        record.setTxTime(Integer.parseInt(bean.getTxTime()));// 交易时间
        record.setSeqNo(bean.getSeqNo()); // 交易流水号
        record.setBankSeqNo(bean.getTxDate() + bean.getTxTime() + bean.getSeqNo()); // 交易日期+交易时间+交易流水号
        record.setStatus(RECHARGE_STATUS_WAIT); // 充值状态:0:初始,1:充值中,2:充值成功,3:充值失败
        record.setAccountId(bean.getAccountId());// 电子账号
        record.setMoney(money); // 金额
        record.setCardid(cardNo);// 银行卡号
        record.setFeeFrom(null);// 手续费扣除方式
        record.setFee(BigDecimal.ZERO); // 费用
        record.setDianfuFee(BigDecimal.ZERO);// 垫付费用
        record.setBalance(money); // 实际到账余额
        record.setPayment(bankCard == null ? "" : bankCard.getBank()); // 所属银行
        record.setGateType("QP"); // 网关类型：QP快捷支付
        record.setType(1); // 类型.1网上充值.0线下充值
        record.setRemark("快捷充值");// 备注
        record.setCreateTime(nowTime);
        record.setOperator(bean.getLogUserId());
        record.setAddtime(String.valueOf(nowTime));
        record.setAddip(bean.getUserIP());
        record.setClient(bean.getLogClient()); // 0pc
        record.setIsBank(1);// 资金托管平台 0:汇付,1:江西银行
        // 插入用户充值记录表
        return this.amTradeClient.insertAccountRecharge(record);
    }

    // 检查参数是否为空
    private boolean checkIsNull(UserDirectRechargeRequestBean requestBean) {
        if (Validator.isNull(requestBean.getInstCode())) {
            return true;
        }
        // 手机号
        if (Validator.isNull(requestBean.getMobile())) {
            return true;
        }
        if (Validator.isNull(requestBean.getRetUrl())) {
            return true;
        }
        if (Validator.isNull(requestBean.getBgRetUrl())) {
            return true;
        }
        // 身份证号
        if (Validator.isNull(requestBean.getIdNo())) {
            return true;
        }
        // 银行卡号
        if (Validator.isNull(requestBean.getCardNo())) {
            return true;
        }
        // 渠道
        if (Validator.isNull(requestBean.getChannel())) {
            return true;
        }
        // 充值金额
        if (Validator.isNull(requestBean.getTxAmount())) {
            return true;
        }
        if (Validator.isNull(requestBean.getName())) {
            return true;
        }
        // 充值金额校验
        if (!requestBean.getTxAmount().matches("-?[0-9]+.*[0-9]*")) {
            logger.info("充值金额格式错误,充值金额:[" + requestBean.getTxAmount() + "]");
            return true;
        }

        //判断小数位数
        if(requestBean.getTxAmount().indexOf(".")>=0){
            String l = requestBean.getTxAmount().substring(requestBean.getTxAmount().indexOf(".")+1,requestBean.getTxAmount().length());
            if(l.length()>2){
                logger.info("充值金额不能大于两位小数,充值金额:[" + requestBean.getTxAmount() + "]");
                return true;
            }
        }
        return false;
    }

    private ModelAndView getErrorMV(UserDirectRechargeRequestBean userOpenAccountRequestBean, ModelAndView modelAndView, String status, String des) {
        Map<String,Object> repwdResult = new HashMap<>();
        BaseResultBean resultBean = new BaseResultBean();
        resultBean.setStatusForResponse(status);
        repwdResult.put("callBackAction",userOpenAccountRequestBean.getRetUrl());
        repwdResult.put("chkValue", resultBean.getChkValue());
        repwdResult.put("status", resultBean.getStatus());
        repwdResult.put("acqRes",userOpenAccountRequestBean.getAcqRes());
        modelAndView.addObject("callBackForm", repwdResult);
        return modelAndView;
    }

    // 拼装返回值
    private Map<String, String> setResult(BaseResultBean resultBean, BankCallResult result, String status, String msg,
                                          String acqRes, boolean success) {
        Map<String, String> params = new HashMap<String, String>();
        resultBean.setStatus(status);
        resultBean.setStatusDesc(msg);
        resultBean.setStatusForResponse(status);
        params.put("status", status);
        params.put("statusDesc", msg);
        params.put("chkValue", resultBean.getChkValue());
        params.put("acqRes", acqRes);
        result.setMessage(msg);
        result.setStatus(success);
        return params;
    }
}
