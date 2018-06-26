/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.controller.web.autoplus;

import com.hyjf.am.vo.user.AuthorizedVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.enums.MsgEnum;
import com.hyjf.common.exception.ReturnMessageException;
import com.hyjf.common.util.ClientConstants;
import com.hyjf.common.validator.CheckUtil;
import com.hyjf.cs.common.bean.result.ApiResult;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.cs.user.config.SystemConfig;
import com.hyjf.cs.user.controller.BaseUserController;
import com.hyjf.cs.user.service.autoplus.AutoPlusService;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import com.hyjf.pay.lib.bank.util.BankCallStatusConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * @author zhangqingqing
 * @version AutoPlusController, v0.1 2018/6/11 14:09
 */
@Api(value = "web端用户自动投标自动债转授权")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/web/user")
public class WebAutoPlusController extends BaseUserController {

    private static final Logger logger = LoggerFactory.getLogger(WebAutoPlusController.class);

    @Autowired
    private AutoPlusService autoPlusService;

    @Autowired
    SystemConfig systemConfig;

    @ApiOperation(value = "授权发送短信验证码", notes = "授权发送短信验证码")
    @ApiImplicitParam(name = "param",value = "{type: string} type=1授权自动投标；type=2授权自动债转", dataType = "Map")
    @PostMapping(value = "/autoPlusSendCode", produces = "application/json; charset=utf-8")
    public WebResult<Object> autoPlusSendCode(@RequestHeader(value = "token", required = true) String token, @RequestBody Map<String,String> param) {
        logger.info("Web端授权发送短信验证码, param :{}", param);
        WebResult<Object> result = new WebResult<Object>();
        UserVO user = autoPlusService.getUsers(token);
        CheckUtil.check(user!=null,MsgEnum.ERR_USER_NOT_LOGIN);
        CheckUtil.check(user.getMobile()!=null,MsgEnum.ERR_OBJECT_BLANK,"手机号");//手机号未填写
        CheckUtil.check(null!=param && StringUtils.isNotBlank(param.get("type")), MsgEnum.ERR_PARAM_TYPE);
        String srvTxCode = "1".equals(param.get("type"))? BankCallConstant.TXCODE_AUTO_BID_AUTH_PLUS:BankCallConstant.TXCODE_AUTO_CREDIT_INVEST_AUTH_PLUSS;
                // 请求银行绑卡接口
        BankCallBean bankBean = null;
        try {
            bankBean = autoPlusService.callSendCode(user.getUserId(),user.getMobile(),srvTxCode, ClientConstants.CHANNEL_PC,null);
        } catch (Exception e) {
            result.setStatus(ApiResult.FAIL);
            result.setStatusDesc(MsgEnum.ERR_BANK_CALL.getMsg());
            logger.error("请求验证码接口发生异常", e);
        }
        if(bankBean == null || !(BankCallStatusConstant.RESPCODE_SUCCESS.equals(bankBean.getRetCode()))) {
            result.setStatus(ApiResult.FAIL);
            result.setStatusDesc(MsgEnum.ERR_BANK_CALL.getMsg());
            logger.error("请求验证码接口失败");
        }else {
            result.setData(bankBean.getSrvAuthCode());
        }
        return result;
    }

    /**
     * @Author: zhangqingqing
     * @Desc :用户授权自动投资
     * @Param: * @param token
     * @param authorizedVO
     * @Date: 16:43 2018/5/30
     * @Return: ModelAndView
     */
    @ApiOperation(value = "用户授权自动投资", notes = "用户授权自动投资")
    @PostMapping(value = "/userAuthInves" , produces = "application/json; charset=utf-8")
    public  WebResult<Object> userAuthInves(@RequestHeader(value = "token", required = true) String token, @RequestBody AuthorizedVO authorizedVO) {
        WebResult<Object> result = new WebResult<Object>();
        String lastSrvAuthCode = authorizedVO.getLastSrvAuthCode();
        String smsCode = authorizedVO.getSmsCode();
        // 验证请求参数
        if (token == null) {
            throw new ReturnMessageException(MsgEnum.ERR_USER_NOT_LOGIN);
        }
        UserVO user = this.autoPlusService.getUsers(token);
        //检查用户信息
       autoPlusService.checkUserMessage(user,lastSrvAuthCode,smsCode);
        Map<String,Object> map = autoPlusService.userCreditAuthInves(user, ClientConstants.WEB_CLIENT, ClientConstants.QUERY_TYPE_1, ClientConstants.CHANNEL_PC, lastSrvAuthCode, smsCode);
        result.setData(map);
        return result;
    }

    /**
     * @Author: zhangqingqing
     * @Desc :用户授权自动债转
     * @Param: * @param token
     * @param authorizedVO
     * @Date: 16:42 2018/5/30
     * @Return: ModelAndView
     */
    @ApiOperation(value = "用户授权自动债转", notes = "用户授权自动债转")
    @PostMapping(value = "/creditUserAuthInves", produces = "application/json; charset=utf-8")
    public  WebResult<Object> creditUserAuthInves(@RequestHeader(value = "token", required = true) String token,@RequestBody AuthorizedVO authorizedVO) {
        WebResult<Object> result = new WebResult<Object>();
        String lastSrvAuthCode = authorizedVO.getLastSrvAuthCode();
        String smsCode = authorizedVO.getSmsCode();
        // 验证请求参数
        if (token == null) {
            throw new ReturnMessageException(MsgEnum.ERR_USER_NOT_LOGIN);
        }
        UserVO user = this.autoPlusService.getUsers(token);
        //检查用户信息
        autoPlusService.checkUserMessage(user,lastSrvAuthCode,smsCode);
        Map<String,Object> map = autoPlusService.userCreditAuthInves(user, ClientConstants.WEB_CLIENT, ClientConstants.QUERY_TYPE_2, ClientConstants.CHANNEL_PC, lastSrvAuthCode, smsCode);
        result.setData(map);
        return result;
    }

    /**
     * @Author: zhangqingqing
     * @Desc :用户授权自动债转异步回调
     * @Param: * @param bean
     * @Date: 16:43 2018/5/30
     * @Return: String
     */
    @ApiOperation(value = "用户授权异步回调", notes = "用户授权自动债转异步回调")
    @PostMapping(value = "/bgreturn", produces = "application/json; charset=utf-8")
    public String userCreditAuthInvesBgreturn(@RequestBody @Valid BankCallBean bean) {
        String result = autoPlusService.userBgreturn(bean);
        return result;
    }

    @ApiOperation(value = "授权状态接口", notes = "授权状态接口")
    @PostMapping(value = "/userAutoStatus", produces = "application/json; charset=utf-8")
    public WebResult<Object> userAutoStatus(@RequestHeader(value = "token") String token){
        WebResult<Object> result = new WebResult<Object>();
        UserVO user = autoPlusService.getUsers(token);
        Map<String, Integer> map = autoPlusService.userAutoStatus(user.getUserId());
        result.setData(map);
        return result;
    }
}
