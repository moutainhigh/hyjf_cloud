/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.controller.web.login;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.hyjf.am.resquest.trade.SensorsDataBean;
import com.hyjf.am.vo.admin.UserOperationLogEntityVO;
import com.hyjf.am.vo.user.UserInfoVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.am.vo.user.WebViewUserVO;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.constants.UserOperationLogConstant;
import com.hyjf.common.enums.MsgEnum;
import com.hyjf.common.exception.MQException;
import com.hyjf.cs.common.bean.result.ApiResult;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.cs.user.controller.BaseUserController;
import com.hyjf.cs.user.mq.base.CommonProducer;
import com.hyjf.cs.user.mq.base.MessageContent;
import com.hyjf.cs.user.service.login.LoginService;
import com.hyjf.cs.user.util.GetCilentIP;
import com.hyjf.cs.user.vo.LoginRequestVO;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * @author zhangqingqing
 * @version LoginController, v0.1 2018/6/11 13:56
 */
@Api(value = "web端-用户登录接口", tags = "web端-用户登录接口")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/hyjf-web/user")
public class WebLoginController extends BaseUserController {

    private static final Logger logger = LoggerFactory.getLogger(WebLoginController.class);

    @Autowired
    private LoginService loginService;
    @Autowired
    private CommonProducer commonProducer;

    /**
     * 登录
     *
     * @param user
     * @param request
     * @return
     */
    @ApiOperation(value = "用户登录", notes = "用户登录")
    @PostMapping(value = "/login", produces = "application/json; charset=utf-8")
    public WebResult<WebViewUserVO> login(@RequestBody LoginRequestVO user,
                                          HttpServletRequest request) {
        logger.info("web端登录接口, user is :{}", JSONObject.toJSONString(user));
        String loginUserName = user.getUsername();
        String loginPassword = user.getPassword();
        WebResult<WebViewUserVO> result = new WebResult<WebViewUserVO>();
        //判断用户输入的密码错误次数---开始
        Map<String, String> errorInfo = loginService.insertErrorPassword(loginUserName, loginPassword, BankCallConstant.CHANNEL_PC);
        if (!errorInfo.isEmpty()) {
            logger.error("web端登录失败...");
            result.setStatus(ApiResult.FAIL);
            result.setStatusDesc(errorInfo.get("info"));
            return result;
        }
        //判断用户输入的密码错误次数---结束
        long start1 = System.currentTimeMillis();
        WebViewUserVO userVO = loginService.login(loginUserName, loginPassword, GetCilentIP.getIpAddr(request), BankCallConstant.CHANNEL_PC);
        logger.info("web登录操作===================:"+(System.currentTimeMillis()-start1));
        if (userVO != null) {
            logger.info("web端登录成功 userId is :{}", userVO.getUserId());
            // add by liuyang 神策数据统计追加 20181029 start
            if (user != null && StringUtils.isNotBlank(user.getPresetProps())) {
                logger.info("Web登录事件,神策预置属性:" + user.getPresetProps());
                try {
                    SensorsDataBean sensorsDataBean = new SensorsDataBean();
                    // 将json串转换成Bean
                    Map<String, Object> sensorsDataMap = JSONObject.parseObject(user.getPresetProps(), new TypeReference<Map<String, Object>>() {
                    });
                    sensorsDataBean.setPresetProps(sensorsDataMap);
                    sensorsDataBean.setUserId(userVO.getUserId());
                    // 发送神策数据统计MQ
                    this.loginService.sendSensorsDataMQ(sensorsDataBean);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // add by liuyang 神策数据统计追加 20181029 end
            //登录成功发送mq
            UserOperationLogEntityVO userOperationLogEntity = new UserOperationLogEntityVO();
            userOperationLogEntity.setOperationType(UserOperationLogConstant.USER_OPERATION_LOG_TYPE1);
            userOperationLogEntity.setIp(GetCilentIP.getIpAddr(request));
            userOperationLogEntity.setPlatform(0);
            userOperationLogEntity.setRemark("");
            userOperationLogEntity.setOperationTime(new Date());
            userOperationLogEntity.setUserName(userVO.getUsername());
            userOperationLogEntity.setUserRole(userVO.getRoleId());
            logger.info("userOperationLogEntity发送数据==="+JSONObject.toJSONString(userOperationLogEntity));
            try {
                commonProducer.messageSend(new MessageContent(MQConstant.USER_OPERATION_LOG_TOPIC, UUID.randomUUID().toString(), userOperationLogEntity));
            } catch (MQException e) {
                logger.error("保存用户日志失败" , e);
            }
            result.setData(userVO);
        } else {
            logger.error("web端登录失败...");
            result.setStatus(ApiResult.FAIL);
            result.setStatusDesc(MsgEnum.ERR_USER_LOGIN.getMsg());
        }
        return result;
    }

    /**
     * @Author: zhangqingqing
     * @Desc : 退出登录
     * @Param: * @param token
     * @Date: 16:29 2018/6/5
     */
    @ApiOperation(value = "登出", notes = "web端-登出")
    @PostMapping(value = "logout")
    public WebResult<Object> loginout(@RequestHeader(value = "userId") int userId, HttpServletRequest request) {
        WebResult<Object> result = new WebResult<>();
        JSONObject ret = new JSONObject();
        ret.put("request", "/user/logout");
        // 退出到首页
        result.setData("index");
        try {
            UserVO userVO = loginService.getUsersById(userId);
            UserInfoVO userInfoVO =  loginService.getUserInfo(userId);
            UserOperationLogEntityVO userOperationLogEntity = new UserOperationLogEntityVO();
            userOperationLogEntity.setOperationType(UserOperationLogConstant.USER_OPERATION_LOG_TYPE2);
            userOperationLogEntity.setIp(GetCilentIP.getIpAddr(request));
            userOperationLogEntity.setPlatform(0);
            userOperationLogEntity.setRemark("");
            userOperationLogEntity.setOperationTime(new Date());
            userOperationLogEntity.setUserName(userVO.getUsername());
            userOperationLogEntity.setUserRole(String.valueOf(userInfoVO.getRoleId()));
            try {
                commonProducer.messageSend(new MessageContent(MQConstant.USER_OPERATION_LOG_TOPIC, UUID.randomUUID().toString(), userOperationLogEntity));
            } catch (MQException e) {
                logger.error("保存用户日志失败" , e);
            }
            RedisUtils.del(RedisConstants.USERID_KEY + userId);
        } catch (Exception e) {
            result.setStatus(ApiResult.FAIL);
            result.setStatusDesc("退出失败");
        }
        result.setData(ret);
        return result;
    }


}
