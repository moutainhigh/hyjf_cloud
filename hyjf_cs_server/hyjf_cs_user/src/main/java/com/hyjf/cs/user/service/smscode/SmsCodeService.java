package com.hyjf.cs.user.service.smscode;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.common.exception.MQException;
import com.hyjf.cs.user.service.BaseUserService;
import com.hyjf.cs.user.vo.SmsRequest;

/**
 * @author xiasq
 * @version SmsCodeService, v0.1 2018/4/25 9:02
 */
public interface SmsCodeService extends BaseUserService {
    /**
     * 发送短信验证码
     * @param validCodeType
     * @param mobile
     * @param ip
     * @throws MQException
     */
    void sendSmsCode(String validCodeType, String mobile,String platform, String ip) throws MQException;

    /**
     *
     * @param request
     * @param verificationType
     * @param verificationCode
     * @param mobile
     * @param key
     * @return
     */
    void appCheckParam(SmsRequest request, String verificationType, String verificationCode, String mobile,String key);


    void sendSmsCodeCheckParam(String validCodeType, String mobile, String token, String ip);

    JSONObject appSendSmsCodeCheckParam(String validCodeType, String mobile, String token, String ip);

    void checkParam(String verificationType, String code, String mobile);

    /**
     * 微信发送验证码参数校验
     * @param verificationType
     * @param mobile
     * @param ipAddr
     */
    JSONObject wechatCheckParam(String verificationType, String mobile, String ipAddr,JSONObject ret);
}
