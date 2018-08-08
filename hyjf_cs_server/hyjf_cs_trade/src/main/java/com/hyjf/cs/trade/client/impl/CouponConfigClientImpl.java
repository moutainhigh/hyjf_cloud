/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.trade.client.impl;

import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.CouponConfigCustomizeResponse;
import com.hyjf.am.response.admin.CouponRecoverResponse;
import com.hyjf.am.response.admin.TransferExceptionLogResponse;
import com.hyjf.am.response.trade.CouponConfigResponse;
import com.hyjf.am.response.trade.CouponTenderCustomizeResponse;
import com.hyjf.am.resquest.trade.TransferExceptionLogWithBLOBsVO;
import com.hyjf.am.vo.admin.coupon.CouponRecoverVO;
import com.hyjf.am.vo.trade.TransferExceptionLogVO;
import com.hyjf.am.vo.trade.account.AccountVO;
import com.hyjf.am.vo.trade.coupon.CouponConfigVO;
import com.hyjf.am.vo.trade.coupon.CouponTenderCustomizeVO;
import com.hyjf.cs.trade.client.CouponConfigClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author yaoy
 * @version CouponConfigClientImpl, v0.1 2018/6/19 18:32
 */
@Service
public class CouponConfigClientImpl implements CouponConfigClient {

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public CouponConfigVO selectCouponConfig(String couponCode) {
        CouponConfigResponse response = restTemplate.getForEntity("http://AM-TRADE/am-trade/couponConfig/selectCouponConfig/" + couponCode, CouponConfigResponse.class).getBody();
        if (response != null) {
            return response.getResult();
        }
        return null;
    }


    @Override
    public Integer checkCouponSendExcess(String couponCode) {
        CouponConfigCustomizeResponse cccr = restTemplate.getForEntity("http://AM-TRADE/am-trade/couponConfig/checkCouponSendExcess/"+couponCode,CouponConfigCustomizeResponse.class).getBody();
        if (Response.isSuccess(cccr)) {
            return cccr.getCount();
        }
        return null;
    }




    @Override
    public CouponConfigVO getCouponConfig(String ordId) {
        CouponConfigResponse response = restTemplate.getForEntity("http://AM-TRADE/am-trade/couponConfig/getcouponconfigbyorderid/" + ordId, CouponConfigResponse.class).getBody();
        if (response != null) {
            return response.getResult();
        }
        return null;
    }

    @Override
    public Integer getCouponProfitTime(String tenderNid) {
        CouponConfigResponse response = restTemplate.getForEntity("http://AM-TRADE/am-trade/couponConfig/getcouponprofittime/" + tenderNid, CouponConfigResponse.class).getBody();
        if (response != null) {
            return response.getCount();
        }
        return null;
    }

    @Override
    public Integer insertSelective(CouponRecoverVO cr) {
        CouponConfigResponse response = restTemplate.postForEntity("http://AM-TRADE/am-trade/couponConfig/insertcouponrecover" ,cr, CouponConfigResponse.class).getBody();
        if (response != null) {
            return response.getCount();
        }
        return 0;
    }

    @Override
    public int updateOfLoansTenderHjh(AccountVO account) {
        CouponConfigResponse response = restTemplate.postForEntity("http://AM-TRADE/am-trade/couponConfig/updateloanstenderhjh" ,account, CouponConfigResponse.class).getBody();
        if (response != null) {
            return response.getCount();
        }
        return 0;
    }

    @Override
    public List<CouponTenderCustomizeVO> getCouponTenderListHjh(String orderId) {
        CouponTenderCustomizeResponse response = restTemplate.getForEntity("http://AM-TRADE/am-trade/couponConfig/getcoupontenderlisthjh/"+orderId , CouponTenderCustomizeResponse.class).getBody();
        if (response != null) {
            return response.getResultList();
        }
        return null;
    }

    @Override
    public CouponRecoverVO updateByPrimaryKeySelective(CouponRecoverVO cr) {
        CouponRecoverResponse response = restTemplate.postForEntity("http://AM-TRADE/am-trade/couponConfig/updatecouponrecoverhjh" ,cr, CouponRecoverResponse.class).getBody();
        if (response != null) {
            return response.getResult();
        }
        return null;
    }

    @Override
    public List<TransferExceptionLogVO> selectByRecoverId(int recoverId) {
        TransferExceptionLogResponse response = restTemplate.getForEntity("http://AM-TRADE/am-trade/couponConfig/selectbyrecoverid/"+recoverId , TransferExceptionLogResponse.class).getBody();
        if (response != null) {
            return response.getResultList();
        }
        return null;
    }

    @Override
    public Integer insertTransferExLog(TransferExceptionLogWithBLOBsVO transferExceptionLog) {
        TransferExceptionLogResponse response = restTemplate.postForEntity("http://AM-TRADE/am-trade/couponConfig/insertTransferexloghjh" ,transferExceptionLog, TransferExceptionLogResponse.class).getBody();
        if (response != null) {
            return response.getFlag();
        }
        return 0;
    }

    @Override
    public List<CouponTenderCustomizeVO> getCouponTenderList(String borrowNid) {
        CouponTenderCustomizeResponse response = restTemplate.getForEntity("http://AM-TRADE/am-trade/couponConfig/getcoupontenderlist/"+borrowNid , CouponTenderCustomizeResponse.class).getBody();
        if (response != null) {
            return response.getResultList();
        }
        return null;
    }

    @Override
    public Integer crRecoverPeriod(String tenderNid, int period) {
        CouponRecoverResponse response = restTemplate.getForEntity("http://AM-TRADE/am-trade/couponConfig/updaterecoverperiod/"+tenderNid + "/" + period ,CouponRecoverResponse.class).getBody();
        if (response != null) {
            return response.getRecordTotal();
        }
        return null;
    }

}
