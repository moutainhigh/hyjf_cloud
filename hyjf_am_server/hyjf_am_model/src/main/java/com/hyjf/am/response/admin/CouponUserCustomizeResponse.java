/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.response.admin;

import com.hyjf.am.response.AdminResponse;
import com.hyjf.am.vo.admin.ActivityListCustomizeVO;
import com.hyjf.am.vo.admin.CouponConfigCustomizeVO;
import com.hyjf.am.vo.admin.coupon.CouponTenderDetailVo;
import com.hyjf.am.vo.admin.coupon.CouponUserCustomizeVO;
import com.hyjf.am.vo.trade.coupon.CouponUserVO;

import java.util.List;

/**
 * @author yaoyong
 * @version CouponUserCustomizeResponse, v0.1 2018/7/23 16:21
 */
public class CouponUserCustomizeResponse extends AdminResponse<CouponUserCustomizeVO> {
    private CouponTenderDetailVo detail;

    private CouponUserVO couponUser;

    private int count;

    private List<CouponConfigCustomizeVO> couponConfigCustomizeVOS;

    private List<ActivityListCustomizeVO> activityListCustomizeVOS;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public CouponTenderDetailVo getDetail() {
        return detail;
    }

    public void setDetail(CouponTenderDetailVo detail) {
        this.detail = detail;
    }

    public CouponUserVO getCouponUser() {
        return couponUser;
    }

    public void setCouponUser(CouponUserVO couponUser) {
        this.couponUser = couponUser;
    }

    public List<CouponConfigCustomizeVO> getCouponConfigCustomizeVOS() {
        return couponConfigCustomizeVOS;
    }

    public void setCouponConfigCustomizeVOS(List<CouponConfigCustomizeVO> couponConfigCustomizeVOS) {
        this.couponConfigCustomizeVOS = couponConfigCustomizeVOS;
    }

    public List<ActivityListCustomizeVO> getActivityListCustomizeVOS() {
        return activityListCustomizeVOS;
    }

    public void setActivityListCustomizeVOS(List<ActivityListCustomizeVO> activityListCustomizeVOS) {
        this.activityListCustomizeVOS = activityListCustomizeVOS;
    }
}
