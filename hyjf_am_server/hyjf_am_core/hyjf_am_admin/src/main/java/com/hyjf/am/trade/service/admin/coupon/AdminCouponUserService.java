/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.admin.coupon;

import com.hyjf.am.resquest.admin.AdminCouponUserRequestBean;
import com.hyjf.am.resquest.admin.CouponUserBeanRequest;
import com.hyjf.am.resquest.admin.CouponUserRequest;
import com.hyjf.am.trade.dao.model.auto.CouponUser;
import com.hyjf.am.trade.dao.model.customize.CouponUserCustomize;

import java.util.List;

/**
 * @author yaoyong
 * @version AdminCouponUserService, v0.1 2018/7/23 17:00
 */
public interface AdminCouponUserService {
    Integer countCouponUser(CouponUserBeanRequest request);

    List<CouponUserCustomize> getRecordList(CouponUserBeanRequest request, int offset, int limit);

    int deleteCouponUserById(int id, String remark, String userId);

    int insertCouponUser(CouponUserRequest request);

    List<CouponUser> getCouponUserByCouponCode(String couponCode);

    CouponUser selectCouponUserById(Integer couponUserId);

    Integer auditRecord(AdminCouponUserRequestBean couponUserRequestBean);
}
