/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.callcenter.clientImpl;

import com.hyjf.am.response.callcenter.CallCenterCouponBackMoneyResponse;
import com.hyjf.am.response.callcenter.CallCenterCouponTenderResponse;
import com.hyjf.am.response.callcenter.CallCenterCouponUserResponse;
import com.hyjf.am.resquest.callcenter.CallCenterBaseRequest;
import com.hyjf.am.vo.callcenter.CallCenterCouponBackMoneyVO;
import com.hyjf.am.vo.callcenter.CallCenterCouponTenderVO;
import com.hyjf.am.vo.callcenter.CallCenterCouponUserVO;
import com.hyjf.callcenter.client.CouponClient;
import com.hyjf.ribbon.EurekaInvokeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author wangjun
 * @version CouponClientImpl, v0.1 2018/6/19 11:51
 */
@Service
public class CouponClientImpl implements CouponClient {
    private static final Logger logger = LoggerFactory.getLogger(CouponClientImpl.class);
    private RestTemplate restTemplate = EurekaInvokeClient.getInstance().buildRestTemplate();

    /**
     * 查询优惠券
     * @param callCenterBaseRequest
     * @return List<CallCenterCouponUserVO>
     * @author wangjun
     */
    public List<CallCenterCouponUserVO> selectCouponUserList(CallCenterBaseRequest callCenterBaseRequest){
        CallCenterCouponUserResponse callCenterCouponUserResponse = restTemplate
                .postForEntity("http://AM-TRADE/am-trade/callcenter/getUserCouponInfoList", callCenterBaseRequest,
                        CallCenterCouponUserResponse.class).getBody();
        if (callCenterCouponUserResponse != null) {
            return callCenterCouponUserResponse.getResultList();
        }
        return null;
    }

    /**
     * 查询优惠券使用（直投产品）
     * @param callCenterBaseRequest
     * @return List<CallCenterCouponTenderVO>
     * @author wangjun
     */
    public List<CallCenterCouponTenderVO> selectCouponTenderList(CallCenterBaseRequest callCenterBaseRequest){
        CallCenterCouponTenderResponse callCenterCouponTenderResponse = restTemplate
                .postForEntity("http://AM-TRADE/am-trade/callcenter/getUserCouponTenderList", callCenterBaseRequest,
                        CallCenterCouponTenderResponse.class).getBody();
        if (callCenterCouponTenderResponse != null) {
            return callCenterCouponTenderResponse.getResultList();
        }
        return null;
    }

    /**
     * 查询优惠券回款（直投产品）
     * @param callCenterBaseRequest
     * @return List<CallCenterCouponBackMoneyVO>
     * @author wangjun
     */
    public List<CallCenterCouponBackMoneyVO> selectCouponBackMoneyList(CallCenterBaseRequest callCenterBaseRequest){
        CallCenterCouponBackMoneyResponse callCenterCouponBackMoneyResponse = restTemplate
                .postForEntity("http://AM-TRADE/am-trade/callcenter/getUserCouponBackMoneyList", callCenterBaseRequest,
                        CallCenterCouponBackMoneyResponse.class).getBody();
        if (callCenterCouponBackMoneyResponse != null) {
            return callCenterCouponBackMoneyResponse.getResultList();
        }
        return null;
    }

    /**
     * 查询优惠券内容
     * @param couponCode
     * @return String
     * @author wangjun
     */
    public String getCouponContent(String couponCode){
        return restTemplate
                .getForEntity("http://AM-USER/am-user/callcenter/getVipLevel/"+ couponCode,
                        String.class).getBody();
    }
}