/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.controller.user.wechat.autoplus;

import com.hyjf.common.exception.ReturnMessageException;
import com.hyjf.cs.user.beans.BaseMapBean;
import com.hyjf.cs.user.constants.AuthorizedError;
import com.hyjf.cs.user.result.ApiResult;
import com.hyjf.cs.user.service.autoplus.AutoPlusService;
import com.hyjf.cs.user.util.ClientConstant;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import com.hyjf.pay.lib.bank.util.BankCallUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author zhangqingqing
 * @version AutoPlusController, v0.1 2018/6/11 14:37
 */

@Api(value = "weChat端用户授权自动投资债转接口")
@RestController
@RequestMapping("/wechat/user")
public class WeChatAutoPlusController {

    private static final Logger logger = LoggerFactory.getLogger(WeChatAutoPlusController.class);
    @Autowired
    AutoPlusService autoPlusService;
    /**
     * @Author: zhangqingqing
     * @Desc :用户自动债转授权
     * @Param: * @param token
     * @param request
     * @Date: 16:36 2018/5/30
     * @Return: org.springframework.web.servlet.ModelAndView
     */
    @ApiOperation(value = "用户自动债转授权", notes = "用户自动债转授权")
    @RequestMapping("/userAuthCredit")
    public ModelAndView userAuthCredit(@RequestHeader(value = "token", required = true) String token, HttpServletRequest request){
        String lastSrvAuthCode = request.getParameter("lastSrvAuthCode");
        String smsCode = request.getParameter("smsCode");
        BankCallBean bean = autoPlusService.userCreditAuthInves(token, ClientConstant.WECHAT_CLIENT, BankCallConstant.QUERY_TYPE_2,ClientConstant.CHANNEL_WEI,lastSrvAuthCode,smsCode);
        ModelAndView modelAndView = new ModelAndView();
        try {
            modelAndView = BankCallUtils.callApi(bean);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ReturnMessageException(AuthorizedError.CALL_BANK_ERROR);
        }
        return modelAndView;
    }

    /**
     * @Author: zhangqingqing
     * @Desc :自动投资授权接口
     * @Param: * @param token
     * @param request
     * @Date: 16:36 2018/5/30
     * @Return: org.springframework.web.servlet.ModelAndView
     */
    @ApiOperation(value = "自动投资授权接口", notes = "自动投资授权接口")
    @RequestMapping("/userAuthInves")
    public ModelAndView userAuthInves(@RequestHeader(value = "token", required = true) String token,HttpServletRequest request){

        String lastSrvAuthCode = request.getParameter("lastSrvAuthCode");
        String smsCode = request.getParameter("smsCode");
        BankCallBean bean = autoPlusService.userCreditAuthInves(token, ClientConstant.WECHAT_CLIENT, BankCallConstant.QUERY_TYPE_1,ClientConstant.CHANNEL_WEI,lastSrvAuthCode,smsCode);
        ModelAndView modelAndView = new ModelAndView();
        try {
            modelAndView = BankCallUtils.callApi(bean);
        } catch (Exception e) {
            e.printStackTrace();
            throw new ReturnMessageException(AuthorizedError.CALL_BANK_ERROR);
        }
        return modelAndView;
    }

    /**
     * @Author: zhangqingqing
     * @Desc :用户授权自动债转同步回调
     * @Param: * @param token
     * @param bean
     * @param request
     * @Date: 16:37 2018/5/30
     * @Return: com.hyjf.cs.user.result.ApiResult<java.lang.String>
     */
    @ApiOperation(value = "用户授权自动债转同步回调", notes = "用户授权自动债转同步回调")
    @RequestMapping("/userAuthCreditReturn")
    public ApiResult<Object> userAuthCreditReturn(@RequestHeader(value = "token", required = true) String token, BankCallBean bean, HttpServletRequest request) {
        ApiResult<Object> apiResult  = new ApiResult<>();
        String sign = request.getHeader("sign");
        String isSuccess = request.getParameter("isSuccess");
        Map<String, BaseMapBean> result = autoPlusService.userAuthCreditReturn(token, bean, ClientConstant.CREDIT_AUTO_TYPE, sign, isSuccess);
        apiResult.setResult(result);
        return apiResult;
    }

    /**
     * @Author: zhangqingqing
     * @Desc :用户授权自动投资同步回调
     * @Param: * @param token
     * @param bean
     * @param request
     * @Date: 16:37 2018/5/30
     * @Return: com.hyjf.cs.user.result.ApiResult<java.lang.String>
     */
    @ApiOperation(value = "用户授权自动投资同步回调", notes = "用户授权自动投资同步回调")
    @RequestMapping("/userAuthInvesReturn")
    public ApiResult<Object> userAuthInvesReturn(@RequestHeader(value = "token") String token,BankCallBean bean, HttpServletRequest request) {
        ApiResult<Object> apiResult  = new ApiResult<>();
        String sign = request.getHeader("sign");
        String isSuccess = request.getParameter("isSuccess");
        Map<String, BaseMapBean> result = autoPlusService.userAuthCreditReturn(token, bean, ClientConstant.INVES_AUTO_TYPE, sign, isSuccess);
        apiResult.setResult(result);
        return apiResult;
    }

    /**
     * @Author: zhangqingqing
     * @Desc :用户授权自动投资异步回调
     * @Param: * @param bean
     * @Date: 16:37 2018/5/30
     * @Return: java.lang.String
     */
    @ApiOperation(value = "用户授权自动投资异步回调", notes = "用户授权自动投资异步回调")
    @ResponseBody
    @RequestMapping("/userAuthInvesBgreturn")
    public String userAuthInvesBgreturn(BankCallBean bean) {
        String result = autoPlusService.userBgreturn(bean);
        return result;
    }

    /**
     * @Author: zhangqingqing
     * @Desc :用户授权自动债转异步回调
     * @Param: * @param bean
     * @Date: 16:37 2018/5/30
     * @Return: java.lang.String
     */
    @ApiOperation(value = "用户授权自动债转异步回调", notes = "用户授权自动债转异步回调")
    @RequestMapping("/userAuthCreditBgreturn")
    public String userCreditAuthInvesBgreturn(BankCallBean bean) {

        String result = autoPlusService.userBgreturn(bean);
        return result;
    }
}
