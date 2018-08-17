package com.hyjf.cs.user.controller.app.trans;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.vo.user.BankOpenAccountVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.constants.UserConstant;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.DES;
import com.hyjf.common.util.SecretUtil;
import com.hyjf.common.validator.Validator;
import com.hyjf.cs.user.config.SystemConfig;
import com.hyjf.cs.user.controller.BaseUserController;
import com.hyjf.cs.user.service.trans.MobileModifyService;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * APP绑定新手机号
 * @author hesy
 * @version AppMobileModifyController, v0.1 2018/7/18 14:53
 */
@Api(value = "app端-绑定新手机号",tags = "app端-绑定新手机号")
@RestController
@RequestMapping("/hyjf-app/appUser")
public class AppMobileModifyController extends BaseUserController {
    @Autowired
    SystemConfig systemConfig;
    @Autowired
    MobileModifyService mobileModifyService;

    @ApiOperation(value = "绑定新手机",notes = "绑定新手机")
    @PostMapping("/bindNewPhoneAction")
    public JSONObject bindNewPhoneAction(@RequestHeader(value = "userId") Integer userId, HttpServletRequest request) {
        JSONObject ret = new JSONObject();
        ret.put("request", "/hyjf-app/app/appUser/bindNewPhoneAction");

        // 唯一标识
        String sign = request.getParameter("sign");

        // 江西银行业务码
        String bankCode = request.getParameter("bankCode");
        String platform = request.getParameter("platform");
        logger.info("江西银行业务码bankCode :{}", bankCode);

        String failReturnUrl = systemConfig.AppFrontHost + "/user/setting/mobile/result/failed";
        String successReturnUrl = systemConfig.AppFrontHost + "/user/setting/mobile/result/success";

        // 验证码
        String verificationCode = request.getParameter("newVerificationCode");
        // 手机号
        String mobile = request.getParameter("newMobile");
        logger.info("绑定新手机获取mobile :{}", mobile);
        // 取得加密用的Key
        String key = SecretUtil.getKey(sign);
        logger.info("取得加密用的key :{}", key);
        if (Validator.isNull(key)) {
            ret.put("status", "1");
            ret.put("statusDesc", "请求参数非法");
            return ret;
        }

        // 业务逻辑
        try {
            if (userId != null) {
                // 取得验证码
                mobile = DES.decodeValue(key, mobile);
                logger.info("des解密后得到的mobile :{}", mobile);
                if (Validator.isNull(mobile)) {
                    ret.put("status", "1");
                    ret.put("statusDesc", "手机号不能为空");
                    return ret;
                }
                if (Validator.isNull(verificationCode)) {
                    ret.put("status", "1");
                    ret.put("statusDesc", "验证码不能为空");
                    return ret;
                }
                if (!Validator.isMobile(mobile)) {
                    ret.put("status", "1");
                    ret.put("statusDesc", "请输入您的真实手机号码");
                    return ret;
                }
                {
                    UserVO userVO = mobileModifyService.getUsersByMobile(mobile);
                    if (userVO != null) {
                        ret.put("status", "1");
                        ret.put("statusDesc", "该手机号已经注册");
                        return ret;
                    }
                }
                // 判断是否开户  假如未开户  修改平台手机号   已开户 修改江西银行和平台
                BankOpenAccountVO bankOpenAccount = mobileModifyService.getBankOpenAccount(userId);
                if (bankOpenAccount == null) {
                    int cnt = mobileModifyService.updateCheckMobileCode(mobile, verificationCode, UserConstant.PARAM_TPL_BDYSJH, platform, UserConstant.CKCODE_NEW, UserConstant.CKCODE_YIYAN,true);
                    if (cnt > 0) {
                        // 未开户 修改平台手机号
                        UserVO userVO = new UserVO();
                        userVO.setUserId(userId);
                        userVO.setMobile(mobile);
                        Integer result = mobileModifyService.updateUserByUserId(userVO);
                        if(result > 0) {
                            ret.put("status", "0");
                            ret.put("statusDesc", "修改手机号成功");
                            ret.put("mobile", mobile);
                            ret.put("successUrl", successReturnUrl + "?status=000&statusDesc=" + "您已绑定手机号" + mobile.substring(0, 3).concat("****").concat(mobile.substring(7)));
                        }else {
                            ret.put("status", "1");
                            ret.put("statusDesc", "修改手机号码失败");
                        }
                    } else {
                        ret.put("status", "1");
                        ret.put("statusDesc", "验证码无效");
                    }
                    return ret;
                }

                if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(bankCode) || StringUtils.isEmpty(verificationCode)) {
                    ret.put("status", "1");
                    ret.put("statusDesc", "请求参数非法");
                    return ret;
                }

                // 调用电子账号手机号修改增强
                BankCallBean retBean = mobileModifyService.callMobileModify(userId,mobile,verificationCode,bankCode);
                if (retBean == null) {
                    ret.put("status", "1");
                    ret.put("statusDesc", "修改手机号失败，系统异常");
                    ret.put("successUrl", failReturnUrl + "?status=99&statusDesc=修改手机号失败，系统异常！");
                    return ret;
                }
                //返回失败
                if (!BankCallConstant.RESPCODE_SUCCESS.equals(retBean.getRetCode())) {
                    String errorMsg = retBean.getRestMsg();
                    if(StringUtils.isBlank(errorMsg)){
                        errorMsg = this.mobileModifyService.getBankReturnErrorMsg(retBean.getRetCode());
                    }
                    if(StringUtils.isBlank(errorMsg)){
                        errorMsg = "修改手机号失败...";
                    }
                    ret.put("status", "1");
                    ret.put("statusDesc", errorMsg);
                    ret.put("successUrl", failReturnUrl + "?status=99&statusDesc=" + errorMsg);
                    return ret;
                }
                //修改手机号
                UserVO userVO = new UserVO();
                userVO.setUserId(userId);
                userVO.setMobile(mobile);
                Integer result = mobileModifyService.updateUserByUserId(userVO);
                if(result > 0){
                    // add by liuyang 修改手机号之后 发送同步CA认证信息修改MQ start
                    this.mobileModifyService.updateUserCAMQ(userId);
                    // add by liuyang 修改手机号之后 发送同步CA认证信息修改MQ end
                    ret.put("status", "0");
                    ret.put("statusDesc", "修改手机号成功");
                    ret.put("mobile", mobile);
                    ret.put("successUrl", successReturnUrl + "?status=000&statusDesc=" + "您已绑定手机号" + mobile.substring(0, 3).concat("****").concat(mobile.substring(7)));
                }else {
                    ret.put("status", "1");
                    ret.put("statusDesc", "修改手机号码失败");
                }
            } else {
                ret.put("status", "1");
                ret.put("statusDesc", "用户信息不存在");
                ret.put("successUrl", failReturnUrl + "?status=99&statusDesc=用户信息不存在!");
            }
        } catch (Exception e) {
            logger.error("绑定新手机发生错误...", e);
            ret.put("status", "1");
            ret.put("statusDesc", "绑定新手机发生错误");
            ret.put("successUrl", failReturnUrl + "?status=99&statusDesc=绑定新手机发生错误");
        }
//		}
        return ret;
    }
}
