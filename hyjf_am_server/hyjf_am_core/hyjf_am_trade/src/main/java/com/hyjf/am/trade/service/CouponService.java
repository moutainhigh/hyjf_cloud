/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service;

import com.hyjf.am.trade.dao.model.auto.BorrowTenderCpn;
import com.hyjf.am.trade.dao.model.auto.CouponRecover;
import com.hyjf.am.trade.dao.model.customize.trade.CouponCustomize;
import com.hyjf.am.vo.trade.coupon.CouponTenderVO;

import java.util.List;

/**
 * @Description 优惠券相关
 * @Author sunss
 * @Version v0.1
 * @Date 2018/6/19 16:50
 */
public interface CouponService {

    /**
     * @Description 根据用户ID和优惠券编号查询优惠券
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/19 16:51
     */
    CouponCustomize getCouponUser(String couponGrantId, Integer userId);

    /**
     * 优惠券投资
     * @param couponTender
     */
    void updateCouponTender(CouponTenderVO couponTender);

    /**
     * 获取优惠券投资
     * @param userId
     * @param borrowNid
     * @param logOrdId
     * @param couponGrantId
     * @return
     */
    BorrowTenderCpn getCouponTenderByTender(Integer userId, String borrowNid, String logOrdId, Integer couponGrantId);

    /**
     * 获取优惠券还款记录
     * @param id
     * @return
     */
    CouponRecover getCouponRecoverByPrimaryKey(Integer id);

    /**
     * 取得优惠券投资信息
     * @param nid
     * @return
     */
    BorrowTenderCpn getCouponTenderInfoByNid(String nid);

    /**
     * @Author walter.limeng
     * @Description  获取汇计划投资列表（优惠券）
     * @Date 10:37 2018/7/17
     * @Param orderId 订单ID
     * @return
     */
    List<BorrowTenderCpn> getBorrowTenderCpnHjhList(String orderId);

    /**
     * @Author walter.limeng
     * @Description  优惠券单独投资时用
     * @Date 10:50 2018/7/17
     * @Param couponOrderId   nid
     * @return
     */
    List<BorrowTenderCpn> getBorrowTenderCpnHjhCouponOnlyList(String couponOrderId);

    /**
     * @Author walter.limeng
     * @Description  更新放款状态(优惠券)
     * @Date 10:58 2018/7/17
     * @Param borrowTenderCpn
     * @return 
     */
    int updateBorrowTenderCpn(BorrowTenderCpn borrowTenderCpn);
}
