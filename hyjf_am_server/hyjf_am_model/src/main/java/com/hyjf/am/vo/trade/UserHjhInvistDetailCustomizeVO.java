package com.hyjf.am.vo.trade;

import com.hyjf.am.vo.BaseVO;

import java.io.Serializable;
import java.text.DecimalFormat;

public class UserHjhInvistDetailCustomizeVO extends BaseVO implements Serializable {

    public static final DecimalFormat DF_FOR_VIEW = new DecimalFormat("#,##0.00");
	
	private String planNid;
    private String planName;
    private String accedeOrderId;
    private String planApr;
    private String planPeriod;
    private String userId;
    private String accedeAccount;
    private String addTime;
    private String quitTime;
    private String lastPaymentTime;
    private String acctualPaymentTime;
    private String countInterestTime;
    private String waitTotal;
    private String waitInterest;
    private String receivedTotal;
    private String receivedInterest;
    private String allTotal;
    private String allInterest;
    private String shouldPayTotal;
    private String repayMethod;
    private String orderStatus;
    private String repayActualTime;
    private int fddStatus;


    // 计划待收本金
    private String waitCaptical;
    // 还款方式 代号
    private String repayStyle;
    // add by nxl 智投服务添加计息结束日
    private String endInterestTime;
    // add by nxl 智投服务授权时间格式化
    private String addTimeFormat;

    public String getPlanNid() {
        return planNid;
    }

    public void setPlanNid(String planNid) {
        this.planNid = planNid;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getAccedeOrderId() {
        return accedeOrderId;
    }

    public void setAccedeOrderId(String accedeOrderId) {
        this.accedeOrderId = accedeOrderId;
    }

    public String getPlanApr() {
        return planApr;
    }

    public void setPlanApr(String planApr) {
        this.planApr = planApr;
    }

    public String getPlanPeriod() {
        return planPeriod;
    }

    public void setPlanPeriod(String planPeriod) {
        this.planPeriod = planPeriod;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAccedeAccount() {
        return accedeAccount;
    }

    public void setAccedeAccount(String accedeAccount) {
        this.accedeAccount = accedeAccount;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public String getQuitTime() {
        return quitTime;
    }

    public void setQuitTime(String quitTime) {
        this.quitTime = quitTime;
    }

    public String getLastPaymentTime() {
        return lastPaymentTime;
    }

    public void setLastPaymentTime(String lastPaymentTime) {
        this.lastPaymentTime = lastPaymentTime;
    }

    public String getAcctualPaymentTime() {
        return acctualPaymentTime;
    }

    public void setAcctualPaymentTime(String acctualPaymentTime) {
        this.acctualPaymentTime = acctualPaymentTime;
    }

    public String getCountInterestTime() {
        return countInterestTime;
    }

    public void setCountInterestTime(String countInterestTime) {
        this.countInterestTime = countInterestTime;
    }

    public String getWaitTotal() {
        return waitTotal;
    }

    public void setWaitTotal(String waitTotal) {
        this.waitTotal = waitTotal;
    }

    public String getWaitInterest() {
        return waitInterest;
    }

    public void setWaitInterest(String waitInterest) {
        this.waitInterest = waitInterest;
    }

    public String getReceivedTotal() {
        return receivedTotal;
    }

    public void setReceivedTotal(String receivedTotal) {
        this.receivedTotal = receivedTotal;
    }

    public String getReceivedInterest() {
        return receivedInterest;
    }

    public void setReceivedInterest(String receivedInterest) {
        this.receivedInterest = receivedInterest;
    }

    public String getAllTotal() {
        return allTotal;
    }

    public void setAllTotal(String allTotal) {
        this.allTotal = allTotal;
    }

    public String getAllInterest() {
        return allInterest;
    }

    public void setAllInterest(String allInterest) {
        this.allInterest = allInterest;
    }

    public String getShouldPayTotal() {
        return shouldPayTotal;
    }

    public void setShouldPayTotal(String shouldPayTotal) {
        this.shouldPayTotal = shouldPayTotal;
    }

    public String getRepayMethod() {
        return repayMethod;
    }

    public void setRepayMethod(String repayMethod) {
        this.repayMethod = repayMethod;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getRepayActualTime() {
        return repayActualTime;
    }

    public void setRepayActualTime(String repayActualTime) {
        this.repayActualTime = repayActualTime;
    }

    public int getFddStatus() {
        return fddStatus;
    }

    public void setFddStatus(int fddStatus) {
        this.fddStatus = fddStatus;
    }

    public String getWaitCaptical() {
        return waitCaptical;
    }

    public void setWaitCaptical(String waitCaptical) {
        this.waitCaptical = waitCaptical;
    }

    public String getRepayStyle() {
        return repayStyle;
    }

    public void setRepayStyle(String repayStyle) {
        this.repayStyle = repayStyle;
    }

    public String getEndInterestTime() {
        return endInterestTime;
    }

    public void setEndInterestTime(String endInterestTime) {
        this.endInterestTime = endInterestTime;
    }

    public String getAddTimeFormat() {
        return addTimeFormat;
    }

    public void setAddTimeFormat(String addTimeFormat) {
        this.addTimeFormat = addTimeFormat;
    }
}
