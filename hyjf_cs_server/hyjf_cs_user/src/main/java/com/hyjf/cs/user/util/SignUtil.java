/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.util;

import com.hyjf.cs.common.util.ApiSignUtil;
import com.hyjf.cs.user.bean.*;
import org.apache.commons.lang3.StringUtils;

/**
 * @author fuqiang
 * @version SignUtil, v0.1 2018/6/11 14:06
 */
public class SignUtil {

    /**
     * AEMS验证外部请求签名
     * @author Zha Daojian
     * @date 2018/12/5 10:20
     * @param paramBean, methodName
     * @return boolean
     **/
    public static boolean aemsVerifyRequestSign(BaseBean paramBean, String methodName) {
        String sign = org.apache.commons.lang.StringUtils.EMPTY;
        // 机构编号必须参数
        String instCode = paramBean.getInstCode();
        if (org.apache.commons.lang.StringUtils.isEmpty(instCode)) {
            return false;
        }
        if (("/aems/authState/status").equals(methodName)) {
            //aems授权状态查询
            AemsAuthStatusQueryRequestBean bean = (AemsAuthStatusQueryRequestBean) paramBean;
            sign = bean.getInstCode() + bean.getAccountId() + bean.getTimestamp();
        }else if (("/aems/bindcardpage/bind").equals(methodName)){
            //aems用户页面绑卡
            AemsBindCardPageRequestBean bean = (AemsBindCardPageRequestBean) paramBean;
            sign = bean.getInstCode() + bean.getAccountId() + bean.getTimestamp();
        }
        // TODO AEMS验签修改
        return ApiSignUtil.verifyByRSA("AEMS", paramBean.getChkValue(), sign);
    }

    /**
     * 验证外部请求签名
     *
     * @param paramBean
     * @param methodName
     * @return
     */
    public static boolean verifyRequestSign(BaseBean paramBean, String methodName) {
        String sign = StringUtils.EMPTY;

        // 机构编号必须参数
        String instCode = paramBean.getInstCode();
        if (StringUtils.isEmpty(instCode)) {
            return false;
        }
        if(methodName.equals("/server/user/paymentAuthPage/page")){
            // 缴费授权
            PaymentAuthPageRequestBean bean = (PaymentAuthPageRequestBean) paramBean;
            sign = bean.getInstCode() + bean.getAccountId()+bean.getRetUrl()+bean.getNotifyUrl() + bean.getTimestamp();
        } else if (BaseDefine.METHOD_SERVER_TRUSTEE_PAY.equals(methodName)) {
            // 借款人受托支付申请
            TrusteePayRequestBean bean = (TrusteePayRequestBean) paramBean;
            sign = bean.getChannel() + bean.getAccountId() + bean.getProductId() + bean.getIdType() + bean.getIdNo()
                    + bean.getReceiptAccountId() + bean.getForgotPwdUrl() + bean.getRetUrl() + bean.getNotifyUrl()
                    + bean.getTimestamp();
        } else if (BaseDefine.METHOD_SERVER_TRUSTEE_PAY_QUERY.equals(methodName)) {
            // 借款人受托支付申请查询
            TrusteePayRequestBean bean = (TrusteePayRequestBean) paramBean;
            sign = bean.getChannel() + bean.getAccountId() + bean.getProductId() + bean.getTimestamp();
        } else if (methodName.equals("/server/repayAuth/repayAuth")) {
            // 还款授权
            PaymentAuthPageRequestBean bean = (PaymentAuthPageRequestBean) paramBean;
            sign = bean.getInstCode() + bean.getAccountId()+bean.getRetUrl()+bean.getNotifyUrl() + bean.getTimestamp();
        }else if(BaseDefine.METHOD_SERVER_TRUSTEE_PAY.equals(methodName)){
            //借款人受托支付申请
            TrusteePayRequestBean bean = (TrusteePayRequestBean) paramBean;
            sign = bean.getChannel() + bean.getAccountId() + bean.getProductId() + bean.getIdType() + bean.getIdNo()
                    + bean.getReceiptAccountId() + bean.getForgotPwdUrl() + bean.getRetUrl() + bean.getNotifyUrl()
                    + bean.getTimestamp();
        }
        return ApiSignUtil.verifyByRSA(instCode, paramBean.getChkValue(), sign);
    }
}
