package com.hyjf.cs.trade.controller.api.aems.authstatus;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.vo.user.BankOpenAccountVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.cs.common.controller.BaseController;
import com.hyjf.cs.trade.bean.AemsAuthStatusQueryRequestBean;
import com.hyjf.cs.trade.bean.AemsAuthStatusQueryResultBean;
import com.hyjf.cs.trade.service.aems.authstatus.AemsAuthStatusQueryService;
import com.hyjf.common.constants.AemsErrorCodeConstant;
import com.hyjf.cs.trade.util.SignUtil;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 授权状态查询
 * @version AemsAuthStatusQueryController, v0.1 2018/12/6 10:06
 * @Author: Zha Daojian
 */

@Api(value = "api端-Aems授权状态查询接口",tags = "api端-Aems授权状态查询接口")
@RestController
@RequestMapping("/hyjf-api/aems/authState")
public class AemsAuthStatusQueryController extends BaseController {

    @Autowired
    private AemsAuthStatusQueryService autoPlusService;

    /**
     *
     * 授权状态查询
     * @author sunss
     * @param autoStateQuery
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/status.do")
    @ApiParam(required = true, name = "findDetailById", value = "Aems授权状态查询接口")
    @ApiOperation(value = "Aems授权状态查询接口", httpMethod = "POST", notes = "Aems授权状态查询接口")
    public AemsAuthStatusQueryResultBean sendCode(@RequestBody AemsAuthStatusQueryRequestBean autoStateQuery, HttpServletRequest request, HttpServletResponse response) {

        logger.info("授权状态查询第三方请求参数：" + JSONObject.toJSONString(autoStateQuery));

        AemsAuthStatusQueryResultBean resultBean = new AemsAuthStatusQueryResultBean();
        String channel = BankCallConstant.CHANNEL_PC;
        // 电子账户号
        String accountId = autoStateQuery.getAccountId();

        // 验证请求参数
        // 机构编号
        if (autoStateQuery.checkParmIsNull()) {
            logger.info("请求参数非法");
            resultBean.setStatusForResponse(AemsErrorCodeConstant.STATUS_CE000001);
            resultBean.setStatusDesc("请求参数非法");
            return resultBean;
        }

        // 验签  accountId
        if (!SignUtil.AEMSVerifyRequestSign(autoStateQuery, "/aems/authState/status")) {
            logger.info("----验签失败----");
            resultBean.setStatusForResponse(AemsErrorCodeConstant.STATUS_CE000002);
            resultBean.setStatusDesc("验签失败！");
            return resultBean;
        }
        // 用户ID
        // 根据电子账户号查询用户ID
        BankOpenAccountVO bankOpenAccount = this.autoPlusService.selectBankOpenAccountByAccountId(accountId);
        if (bankOpenAccount == null) {
            logger.info("查询用户开户信息失败,用户电子账户号:[" + accountId + "]");
            resultBean.setStatusForResponse(AemsErrorCodeConstant.STATUS_CE000004);
            resultBean.setStatusDesc("根据电子账户号查询用户信息失败");
            return resultBean;
        }
        Integer userId = bankOpenAccount.getUserId();
        UserVO user = this.autoPlusService.getUserByUserId(userId);
        if (user == null) {
            logger.info("查询用户失败:[" + userId + "].");
            resultBean.setStatusForResponse(AemsErrorCodeConstant.STATUS_CE000007);
            resultBean.setStatusDesc("查询用户失败");
            return resultBean;
        }
        Integer passwordFlag = user.getIsSetPassword();
        if (passwordFlag != 1) {// 未设置交易密码
            logger.info("-------------------未设置交易密码！"+autoStateQuery.getAccountId()+"！--------------------status"+user.getIsSetPassword());
            resultBean.setStatusForResponse(AemsErrorCodeConstant.STATUS_TP000002);
            resultBean.setStatusDesc("未设置交易密码");
            return resultBean;
        }
        BankCallBean retBean=autoPlusService.getTermsAuthQuery(userId,channel);
        logger.info("调用江西银行授权状态查询接口:"+(retBean==null?"空":retBean.getPaymentAuth()));
        if(retBean==null){
            logger.info("银行返回为空,accountId:["+accountId+"]");
            resultBean.setStatusForResponse(AemsErrorCodeConstant.STATUS_CE999999);
            resultBean.setStatusDesc("授权状态查询接口失败！");
            return resultBean;
        }
        String retCode = retBean.getRetCode();
        if (!BankCallConstant.RESPCODE_SUCCESS.equals(retCode)) {
            logger.info("授权状态查询接口失败,accountId:["+accountId+"]返回码["+retCode+"]！");
            resultBean.setStatusForResponse(AemsErrorCodeConstant.STATUS_CE999999);
            resultBean.setStatusDesc("授权状态查询接口失败！");
            return resultBean;
        }
        resultBean = getResultJosn(resultBean,retBean);
        logger.info("授权状态查询第三方返回参数："+JSONObject.toJSONString(resultBean));
        resultBean.setStatusForResponse(AemsErrorCodeConstant.SUCCESS);
        resultBean.setStatusDesc("授权状态查询成功");
        return resultBean;
    }

    // 拼接返回参数
    private AemsAuthStatusQueryResultBean getResultJosn(AemsAuthStatusQueryResultBean resultBean, BankCallBean retBean) {
        resultBean.setAccountId(retBean.getAccountId());
        resultBean.setAgreeWithdrawStatus(retBean.getAgreeWithdraw());
        resultBean.setAutoBidDeadline(retBean.getAutoBidDeadline());
        resultBean.setAutoBidStatus(retBean.getAutoBid());
        resultBean.setAutoTransferStatus(retBean.getAutoTransfer());
        resultBean.setPaymentAuthStatus(retBean.getPaymentAuth());
        resultBean.setPaymentDeadline(retBean.getPaymentDeadline());
        resultBean.setRepayAuthStatus(retBean.getRepayAuth());
        resultBean.setRepayDeadline(retBean.getRepayDeadline());
        return resultBean;
    }
}
