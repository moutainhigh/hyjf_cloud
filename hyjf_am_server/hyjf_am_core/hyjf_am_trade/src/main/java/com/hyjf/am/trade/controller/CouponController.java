package com.hyjf.am.trade.controller;

import com.hyjf.am.response.trade.*;
import com.hyjf.am.trade.dao.model.auto.BorrowTenderCpn;
import com.hyjf.am.trade.dao.model.customize.trade.CouponCustomize;
import com.hyjf.am.trade.service.CouponService;
import com.hyjf.am.vo.trade.coupon.CouponTenderVO;
import com.hyjf.am.vo.trade.coupon.CouponUserVO;
import com.hyjf.am.vo.trade.borrow.BorrowTenderCpnVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Description 优惠券相关
 * @Author sunss
 * @Version v0.1
 * @Date 2018/6/19 16:32
 */
@Api(value = "优惠券相关")
@RestController
@RequestMapping("/am-trade/coupon")
public class CouponController extends BaseController{

    @Autowired
    CouponService couponService;

    @ApiOperation(value = " 根据用户ID和优惠券编号查询优惠券")
    @GetMapping("/getCouponUser/{couponId}/{userId}")
    public CoupUserResponse getCouponUser(@PathVariable(value = "couponId") String couponId,
                                                       @PathVariable(value = "userId") Integer userId) {
        CoupUserResponse response = new CoupUserResponse();
        CouponCustomize couponUser  = couponService.getCouponUser(couponId,userId);
        if(null != couponUser){
            CouponUserVO couponUserVO = new CouponUserVO();
            BeanUtils.copyProperties(couponUser,couponUserVO);
            response.setResult(couponUserVO);
        }
        return response;
    }

    @ApiOperation(value = " 优惠券投资")
    @GetMapping("/updateCouponTender")
    public Integer updateCouponTender(CouponTenderVO couponTender) {
        try{
            couponService.updateCouponTender(couponTender);
            return 1;
        }catch (Exception e ) {
            return 0;
        }
    }

    @ApiOperation(value = "获取优惠券投资信息")
    @GetMapping("/getCouponTenderByTender/{userId}/{borrowNid}/{logOrdId}/{couponGrantId}")
    public BorrowTenderCpnResponse getCouponTenderByTender(Integer userId, String borrowNid, String logOrdId, Integer couponGrantId) {
        BorrowTenderCpnResponse response = new BorrowTenderCpnResponse();
        BorrowTenderCpn cpn = couponService.getCouponTenderByTender(userId,borrowNid,logOrdId,couponGrantId);
        if(null != cpn){
            BorrowTenderCpnVO borrowTenderCpnVO = new BorrowTenderCpnVO();
            BeanUtils.copyProperties(cpn,borrowTenderCpnVO);
            response.setResult(borrowTenderCpnVO);
        }
        return response;
    }

}
