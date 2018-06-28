package com.hyjf.am.resquest.trade;

import com.hyjf.am.vo.BaseVO;

/**
 * 还款列表Bean
 * @author hesy
 * @version MyCouponListRequest, v0.1 2018/6/23 11:46
 */
public class RepayListRequest extends BaseVO {
    String userId;
    String startDate;
    String endDate;
    String repayTimeOrder;
    String checkTimeOrder;
    String borrowNid;
    Integer limitStart;
    Integer limitEnd;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getRepayTimeOrder() {
        return repayTimeOrder;
    }

    public void setRepayTimeOrder(String repayTimeOrder) {
        this.repayTimeOrder = repayTimeOrder;
    }

    public String getCheckTimeOrder() {
        return checkTimeOrder;
    }

    public void setCheckTimeOrder(String checkTimeOrder) {
        this.checkTimeOrder = checkTimeOrder;
    }

    public String getBorrowNid() {
        return borrowNid;
    }

    public void setBorrowNid(String borrowNid) {
        this.borrowNid = borrowNid;
    }

    public Integer getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(Integer limitStart) {
        this.limitStart = limitStart;
    }

    public Integer getLimitEnd() {
        return limitEnd;
    }

    public void setLimitEnd(Integer limitEnd) {
        this.limitEnd = limitEnd;
    }
}
