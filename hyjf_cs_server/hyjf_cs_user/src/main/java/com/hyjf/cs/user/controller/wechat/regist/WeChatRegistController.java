/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.controller.wechat.regist;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.util.ClientConstants;
import com.hyjf.common.util.DES;
import com.hyjf.cs.user.client.AmUserClient;
import com.hyjf.cs.user.constants.LoginError;
import com.hyjf.cs.user.constants.RegisterError;
import com.hyjf.cs.user.result.BaseResultBean;
import com.hyjf.cs.user.service.regist.RegistService;
import com.hyjf.cs.user.util.GetCilentIP;
import com.hyjf.cs.user.vo.RegisterVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author zhangqingqing
 * @version RegistController, v0.1 2018/6/11 14:35
 */

@Api(value = "weChat端用户注册接口")
@RestController
@RequestMapping("/wechat/user")
public class WeChatRegistController {

    private static final Logger logger = LoggerFactory.getLogger(WeChatRegistController.class);
    @Autowired
    private RegistService registService;
    @Autowired
    private AmUserClient amUserClient;

    /**
     * @Author: zhangqingqing
     * @Desc :注册
     * @Param:  * @param key
     * @param mobile
     * @param verificationCode
     * @param password
     * @param reffer
     * @param request
     * @param response
     * @Date: 16:34 2018/5/30
     * @Return: com.hyjf.cs.user.result.BaseResultBean
     */

    @ApiOperation(value = "用户注册", notes = "用户注册")
    @PostMapping(value = "/register", produces = "application/json; charset=utf-8")
    public BaseResultBean register(@RequestHeader String key,@RequestBody RegisterVO register, HttpServletRequest request, HttpServletResponse response) {
        logger.info("register start, mobile is :{}", JSONObject.toJSONString(register));
        BaseResultBean resultBean = new BaseResultBean();

        String mobilephone = DES.decodeValue(key, register.getMobilephone());
        String smsCode = DES.decodeValue(key, register.getSmsCode());
        String pwd = DES.decodeValue(key, register.getPassword());
        String reffer = DES.decodeValue(key, register.getReffer());
        if (StringUtils.isNotBlank(reffer)) {
            int count = amUserClient.countUserByRecommendName(reffer);
            if (count == 0) {
                resultBean.setStatus(LoginError.REFFER_INVALID_ERROR.getErrCode());
                resultBean.setStatusDesc(LoginError.REFFER_INVALID_ERROR.getMessage());
                return resultBean;
            }
        }
        RegisterVO registerVO = new RegisterVO();
        registerVO.setMobilephone(mobilephone);
        registerVO.setPassword(pwd);
        registerVO.setReffer(reffer);
        registerVO.setSmsCode(smsCode);
        UserVO userVO = registService.register(registerVO, GetCilentIP.getIpAddr(request), ClientConstants.WECHAT_CLIENT);

        if (userVO != null) {
            logger.info("register success, userId is :{}", userVO.getUserId());
        } else {
            logger.error("register failed...");
            resultBean.setStatus("1");
            resultBean.setStatusDesc(RegisterError.REGISTER_ERROR.getMessage());
        }
        return resultBean;
    }

}
