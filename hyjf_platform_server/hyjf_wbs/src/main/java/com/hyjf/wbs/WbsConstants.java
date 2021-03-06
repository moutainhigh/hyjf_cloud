package com.hyjf.wbs;

/**
 * @author cui
 * @version WbsConstants, v0.1 2019/4/16 15:27
 */
public interface WbsConstants {

    String WBS_RESPONSE_STATUS_KEY="code";

    String WBS_RESPONSE_STATUS_SUCCESS="0";

    String WBS_RESPONSE_ERROR_MSG_KEY="msg";

    String INTERFACE_NAME_SYNC_CUSTOMER="openapi.customer.bind";

    String INTERFACE_NAME_PASSPORT_AUTHORIZE ="passport.user.authorize";

    String INTERFACE_NAME_SYNC_PRODUCT_INFO="pdc.product.sync";

    //存管渠道 pc
    String CHANNEL_PC = "000002";
    //存管渠道 wechat
    String CHANNEL_WEI = "000003";

}