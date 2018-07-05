/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.client.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hyjf.am.response.trade.AccountResponse;
import com.hyjf.am.response.trade.BatchUserPortraitQueryResponse;
import com.hyjf.am.response.trade.CouponUserListCustomizeResponse;
import com.hyjf.am.response.user.HjhInstConfigResponse;
import com.hyjf.am.response.user.RecentPaymentListCustomizeResponse;
import com.hyjf.am.vo.trade.BatchUserPortraitQueryVO;
import com.hyjf.am.vo.trade.account.AccountVO;
import com.hyjf.am.vo.trade.coupon.CouponUserListCustomizeVO;
import com.hyjf.am.vo.user.HjhInstConfigVO;
import com.hyjf.am.vo.user.RecentPaymentListCustomizeVO;
import com.hyjf.common.validator.Validator;
import com.hyjf.cs.user.client.AmTradeClient;

/**
 * @author zhangqingqing
 * @version AmTradeClientImpl, v0.1 2018/6/20 12:45
 */
@Service
public class AmTradeClientImpl implements AmTradeClient {
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public HjhInstConfigVO selectInstConfigByInstCode(String instCode) {
        HjhInstConfigResponse response = restTemplate
                .getForEntity("http://AM-TRADE/am-trade/trade/selectInstConfigByInstCode/"+instCode, HjhInstConfigResponse.class)
                .getBody();
        if (response != null) {
            return response.getResult();
        }
        return null;
    }

    /**
     * 获取用户account信息
     * @param userId
     * @return
     */
    @Override
    public AccountVO getAccount(Integer userId) {
        AccountResponse response = restTemplate
                .getForEntity("http://AM-TRADE/am-trade/trade/getAccount/" + userId, AccountResponse.class).getBody();
        if (response != null) {
            return response.getResult();
        }
        return null;
    }

    @Override
    public List<RecentPaymentListCustomizeVO> selectRecentPaymentList(Integer userId) {
        RecentPaymentListCustomizeResponse response = restTemplate
                .getForEntity("http://AM-TRADE/am-trade/borrow/selectRecentPaymentList/" + userId, RecentPaymentListCustomizeResponse.class)
                .getBody();
        if (response != null) {
            return response.getResultList();
        }
        return null;
    }

    @Override
    public List<BatchUserPortraitQueryVO> searchInfoForUserPortrait(String userIds) {
        String url = "http://AM-TRADE/am-trade/batch/search_user_portrait_list/" + userIds;
        BatchUserPortraitQueryResponse response = restTemplate.getForEntity(url, BatchUserPortraitQueryResponse.class).getBody();
        if(response != null){
            return response.getResultList();
        }
        return null;
    }


    @Override
    public Integer countCouponValid(Integer userId) {
        String url = "http://AM-TRADE/am-trade/couponUser/countCouponValid/" + userId;
        return restTemplate.getForEntity(url, Integer.class).getBody();
    }

    @Override
    public List<CouponUserListCustomizeVO> selectCouponUserList(Map<String, Object> mapParameter) {
        String url = "http://AM-TRADE/am-trade/couponUser/selectCouponUserList";
        CouponUserListCustomizeResponse response = restTemplate.postForEntity(url,mapParameter,CouponUserListCustomizeResponse.class).getBody();
        if (Validator.isNotNull(response)){
            return response.getResultList();
        }
        return null;
    }
}
