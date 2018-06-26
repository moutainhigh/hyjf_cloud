/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.controller.task;


import com.hyjf.am.response.trade.BatchCouponTimeoutCommonResponse;
import com.hyjf.am.response.trade.CouponRecoverCustomizeResponse;
import com.hyjf.am.trade.dao.model.customize.trade.BatchCouponTimeoutCommonCustomize;
import com.hyjf.am.trade.dao.model.customize.trade.CouponRecoverCustomize;
import com.hyjf.am.trade.service.task.CouponRepaySerivce;
import com.hyjf.am.vo.trade.BatchCouponTimeoutCommonCustomizeVO;
import com.hyjf.am.vo.trade.CouponRecoverCustomizeVO;
import com.hyjf.common.util.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;


/**
 * @author yaoy
 * @version CouponRepayInterestController, v0.1 2018/6/21 18:00
 */
@RestController
@RequestMapping("/am-trade/batch")
public class CouponRepayInterestController {

    @Autowired
    private CouponRepaySerivce couponRepaySerivce;

    @GetMapping("/selectCouponInterestWaitToday/{timeStart}/{timeEnd}")
    public CouponRecoverCustomizeResponse selectCouponInterestWaitToday(@PathVariable long timeStart, @PathVariable long timeEnd) {
        CouponRecoverCustomizeResponse response = new CouponRecoverCustomizeResponse();
        List<CouponRecoverCustomize> couponRecoverCustomizes = couponRepaySerivce.selectCouponInterestWaitToday(timeStart, timeEnd);
        if (!CollectionUtils.isEmpty(couponRecoverCustomizes)) {
            List<CouponRecoverCustomizeVO> couponRecoverCustomizeVOS = CommonUtils.convertBeanList(couponRecoverCustomizes, CouponRecoverCustomizeVO.class);
            response.setResultList(couponRecoverCustomizeVOS);
        }
        return response;
    }

    @GetMapping("/selectCouponInterestReceivedToday/{timeStart}/{timeEnd}")
    public BigDecimal selectCouponInterestReceivedToday(@PathVariable long timeStart, @PathVariable long timeEnd) {
        BigDecimal interestReceived = couponRepaySerivce.selectCouponInterestReceivedToday(timeStart, timeEnd);
        if (interestReceived != null) {
            return interestReceived;
        }
        return null;
    }

    @GetMapping("/selectCouponQuota/{threeBeginDate}/{threeEndDate}")
    public BatchCouponTimeoutCommonResponse selectCouponQuota(@PathVariable int threeBeginDate, @PathVariable int threeEndDate) {
        BatchCouponTimeoutCommonResponse response = new BatchCouponTimeoutCommonResponse();
        List<BatchCouponTimeoutCommonCustomize> batchCouponTimeoutCommonCustomizes = couponRepaySerivce.selectCouponQuota(threeBeginDate, threeEndDate);
        if (!CollectionUtils.isEmpty(batchCouponTimeoutCommonCustomizes)) {
            List<BatchCouponTimeoutCommonCustomizeVO> batchCouponTimeoutCommonCustomizeVOS = CommonUtils.convertBeanList(batchCouponTimeoutCommonCustomizes, BatchCouponTimeoutCommonCustomizeVO.class);
            response.setResultList(batchCouponTimeoutCommonCustomizeVOS);
        }
        return response;
    }
}
