package com.hyjf.am.trade.dao.model.customize;

import com.hyjf.am.vo.BaseVO;

import java.io.Serializable;

/**
 * @author pangchengchao
 * @version BorrowRecoverCustomizeVO, v0.1 2018/7/2 14:50
 */
public class AdminBorrowRepaymentCustomize extends BaseVO implements Serializable {
    /**
     * serialVersionUID:
     */

    private static final long serialVersionUID = 1L;

    private String borrowNid;// 借款编号
    private String userId;// 借款人ID
    private String borrowUserName;// 借款人用户名
    private String borrowName;// 借款标题
    private String borrowStyle;//还款方式
    private String projectType;// 项目类型id
    private String projectTypeName;// 项目类型名称
    private String borrowPeriod;// 借款期限
    private String borrowApr;// 年化收益
    private String borrowAccount;// 借款金额
    private String borrowAccountYes;// 借到金额
    private String repayType;// 还款方式中文名
    private String verifyTime;//审核时间（发布时间）
    private String repayStyleType;// 还款方式英文名
    private String createUserName;//账户操作人
    private String registUserName;//备案
    private String repayAccountCapital;// 应还本金
    private String repayAccountInterest;// 应还利息
    private String repayAccountAll;// 应还本息
    private String borrowManager;// 没用到
    private String repayFee;//管理费
    private String repayAccountCapitalYes;// 已还本金
    private String repayAccountInterestYes;// 已还利息
    private String repayAccountYes;// 已换本息
    private String repayAccountCapitalWait;// 未还本金
    private String repayAccountInterestWait;// 未还利息
    private String repayAccountWait;// 未还本息
    private String status;// 还款状态
    private String repayStatus;// 还款状态(JOB)
    private String repayLastTime;// 最后还款日
    private String repayNextTime;//下次还款日
    private String repayMoneySource; //还款来源
    private String repayActionTime; //实际还款时间
    private String planNid;//计划编号
    private String instName; //机构名称


    public String getBorrowNid() {
        return borrowNid;
    }

    public void setBorrowNid(String borrowNid) {
        this.borrowNid = borrowNid;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBorrowUserName() {
        return borrowUserName;
    }

    public void setBorrowUserName(String borrowUserName) {
        this.borrowUserName = borrowUserName;
    }

    public String getBorrowName() {
        return borrowName;
    }

    public void setBorrowName(String borrowName) {
        this.borrowName = borrowName;
    }

    public String getBorrowStyle() {
        return borrowStyle;
    }

    public void setBorrowStyle(String borrowStyle) {
        this.borrowStyle = borrowStyle;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getProjectTypeName() {
        return projectTypeName;
    }

    public void setProjectTypeName(String projectTypeName) {
        this.projectTypeName = projectTypeName;
    }

    public String getBorrowPeriod() {
        return borrowPeriod;
    }

    public void setBorrowPeriod(String borrowPeriod) {
        this.borrowPeriod = borrowPeriod;
    }

    public String getBorrowApr() {
        return borrowApr;
    }

    public void setBorrowApr(String borrowApr) {
        this.borrowApr = borrowApr;
    }

    public String getBorrowAccount() {
        return borrowAccount;
    }

    public void setBorrowAccount(String borrowAccount) {
        this.borrowAccount = borrowAccount;
    }

    public String getBorrowAccountYes() {
        return borrowAccountYes;
    }

    public void setBorrowAccountYes(String borrowAccountYes) {
        this.borrowAccountYes = borrowAccountYes;
    }

    public String getRepayType() {
        return repayType;
    }

    public void setRepayType(String repayType) {
        this.repayType = repayType;
    }

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getRepayStyleType() {
        return repayStyleType;
    }

    public void setRepayStyleType(String repayStyleType) {
        this.repayStyleType = repayStyleType;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public String getRegistUserName() {
        return registUserName;
    }

    public void setRegistUserName(String registUserName) {
        this.registUserName = registUserName;
    }

    public String getRepayAccountCapital() {
        return repayAccountCapital;
    }

    public void setRepayAccountCapital(String repayAccountCapital) {
        this.repayAccountCapital = repayAccountCapital;
    }

    public String getRepayAccountInterest() {
        return repayAccountInterest;
    }

    public void setRepayAccountInterest(String repayAccountInterest) {
        this.repayAccountInterest = repayAccountInterest;
    }

    public String getRepayAccountAll() {
        return repayAccountAll;
    }

    public void setRepayAccountAll(String repayAccountAll) {
        this.repayAccountAll = repayAccountAll;
    }

    public String getBorrowManager() {
        return borrowManager;
    }

    public void setBorrowManager(String borrowManager) {
        this.borrowManager = borrowManager;
    }

    public String getRepayFee() {
        return repayFee;
    }

    public void setRepayFee(String repayFee) {
        this.repayFee = repayFee;
    }

    public String getRepayAccountCapitalYes() {
        return repayAccountCapitalYes;
    }

    public void setRepayAccountCapitalYes(String repayAccountCapitalYes) {
        this.repayAccountCapitalYes = repayAccountCapitalYes;
    }

    public String getRepayAccountInterestYes() {
        return repayAccountInterestYes;
    }

    public void setRepayAccountInterestYes(String repayAccountInterestYes) {
        this.repayAccountInterestYes = repayAccountInterestYes;
    }

    public String getRepayAccountYes() {
        return repayAccountYes;
    }

    public void setRepayAccountYes(String repayAccountYes) {
        this.repayAccountYes = repayAccountYes;
    }

    public String getRepayAccountCapitalWait() {
        return repayAccountCapitalWait;
    }

    public void setRepayAccountCapitalWait(String repayAccountCapitalWait) {
        this.repayAccountCapitalWait = repayAccountCapitalWait;
    }

    public String getRepayAccountInterestWait() {
        return repayAccountInterestWait;
    }

    public void setRepayAccountInterestWait(String repayAccountInterestWait) {
        this.repayAccountInterestWait = repayAccountInterestWait;
    }

    public String getRepayAccountWait() {
        return repayAccountWait;
    }

    public void setRepayAccountWait(String repayAccountWait) {
        this.repayAccountWait = repayAccountWait;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRepayStatus() {
        return repayStatus;
    }

    public void setRepayStatus(String repayStatus) {
        this.repayStatus = repayStatus;
    }

    public String getRepayLastTime() {
        return repayLastTime;
    }

    public void setRepayLastTime(String repayLastTime) {
        this.repayLastTime = repayLastTime;
    }

    public String getRepayNextTime() {
        return repayNextTime;
    }

    public void setRepayNextTime(String repayNextTime) {
        this.repayNextTime = repayNextTime;
    }

    public String getRepayMoneySource() {
        return repayMoneySource;
    }

    public void setRepayMoneySource(String repayMoneySource) {
        this.repayMoneySource = repayMoneySource;
    }

    public String getRepayActionTime() {
        return repayActionTime;
    }

    public void setRepayActionTime(String repayActionTime) {
        this.repayActionTime = repayActionTime;
    }

    public String getPlanNid() {
        return planNid;
    }

    public void setPlanNid(String planNid) {
        this.planNid = planNid;
    }

    public String getInstName() {
        return instName;
    }

    public void setInstName(String instName) {
        this.instName = instName;
    }
}
