package com.hyjf.am.vo.trade.hjh;

import com.hyjf.am.vo.BaseVO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class HjhDebtCreditTenderVO extends BaseVO implements Serializable {
    private Integer id;

    private Integer userId;

    private String userName;

    private Integer creditUserId;

    private String creditUserName;

    private String borrowNid;

    private Integer repayPeriod;

    private String creditNid;

    private String investOrderId;

    private String sellOrderId;

    private String liquidatesPlanNid;

    private String liquidatesPlanOrderId;

    private String assignPlanNid;

    private String assignPlanOrderId;

    private String assignOrderId;

    private String assignOrderDate;

    private String authCode;

    private BigDecimal assignCapital;

    private BigDecimal assignAccount;

    private BigDecimal assignInterest;

    private BigDecimal assignRepayDelayInterest;

    private BigDecimal assignRepayLateInterest;

    private BigDecimal assignInterestAdvance;

    private BigDecimal assignPrice;

    private BigDecimal assignPay;

    private BigDecimal repayAccountYes;

    private BigDecimal repayCapitalYes;

    private BigDecimal repayInterestYes;

    private BigDecimal repayAccountWait;

    private BigDecimal repayCapitalWait;

    private BigDecimal repayInterestWait;

    private Integer assignRepayEndTime;

    private Integer assignRepayLastTime;

    private Integer assignRepayNextTime;

    private Integer assignRepayYesTime;

    private Integer assignRepayPeriod;

    private Integer assignType;

    private Integer status;

    private BigDecimal assignServiceApr;

    private BigDecimal assignServiceFee;

    private Integer client;

    private Integer delFlag;

    private Integer createUserId;

    private String createUserName;

    private Integer updateUserId;

    private String updateUserName;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName == null ? null : userName.trim();
    }

    public Integer getCreditUserId() {
        return creditUserId;
    }

    public void setCreditUserId(Integer creditUserId) {
        this.creditUserId = creditUserId;
    }

    public String getCreditUserName() {
        return creditUserName;
    }

    public void setCreditUserName(String creditUserName) {
        this.creditUserName = creditUserName == null ? null : creditUserName.trim();
    }

    public String getBorrowNid() {
        return borrowNid;
    }

    public void setBorrowNid(String borrowNid) {
        this.borrowNid = borrowNid == null ? null : borrowNid.trim();
    }

    public Integer getRepayPeriod() {
        return repayPeriod;
    }

    public void setRepayPeriod(Integer repayPeriod) {
        this.repayPeriod = repayPeriod;
    }

    public String getCreditNid() {
        return creditNid;
    }

    public void setCreditNid(String creditNid) {
        this.creditNid = creditNid == null ? null : creditNid.trim();
    }

    public String getInvestOrderId() {
        return investOrderId;
    }

    public void setInvestOrderId(String investOrderId) {
        this.investOrderId = investOrderId == null ? null : investOrderId.trim();
    }

    public String getSellOrderId() {
        return sellOrderId;
    }

    public void setSellOrderId(String sellOrderId) {
        this.sellOrderId = sellOrderId == null ? null : sellOrderId.trim();
    }

    public String getLiquidatesPlanNid() {
        return liquidatesPlanNid;
    }

    public void setLiquidatesPlanNid(String liquidatesPlanNid) {
        this.liquidatesPlanNid = liquidatesPlanNid == null ? null : liquidatesPlanNid.trim();
    }

    public String getLiquidatesPlanOrderId() {
        return liquidatesPlanOrderId;
    }

    public void setLiquidatesPlanOrderId(String liquidatesPlanOrderId) {
        this.liquidatesPlanOrderId = liquidatesPlanOrderId == null ? null : liquidatesPlanOrderId.trim();
    }

    public String getAssignPlanNid() {
        return assignPlanNid;
    }

    public void setAssignPlanNid(String assignPlanNid) {
        this.assignPlanNid = assignPlanNid == null ? null : assignPlanNid.trim();
    }

    public String getAssignPlanOrderId() {
        return assignPlanOrderId;
    }

    public void setAssignPlanOrderId(String assignPlanOrderId) {
        this.assignPlanOrderId = assignPlanOrderId == null ? null : assignPlanOrderId.trim();
    }

    public String getAssignOrderId() {
        return assignOrderId;
    }

    public void setAssignOrderId(String assignOrderId) {
        this.assignOrderId = assignOrderId == null ? null : assignOrderId.trim();
    }

    public String getAssignOrderDate() {
        return assignOrderDate;
    }

    public void setAssignOrderDate(String assignOrderDate) {
        this.assignOrderDate = assignOrderDate == null ? null : assignOrderDate.trim();
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode == null ? null : authCode.trim();
    }

    public BigDecimal getAssignCapital() {
        return assignCapital;
    }

    public void setAssignCapital(BigDecimal assignCapital) {
        this.assignCapital = assignCapital;
    }

    public BigDecimal getAssignAccount() {
        return assignAccount;
    }

    public void setAssignAccount(BigDecimal assignAccount) {
        this.assignAccount = assignAccount;
    }

    public BigDecimal getAssignInterest() {
        return assignInterest;
    }

    public void setAssignInterest(BigDecimal assignInterest) {
        this.assignInterest = assignInterest;
    }

    public BigDecimal getAssignRepayDelayInterest() {
        return assignRepayDelayInterest;
    }

    public void setAssignRepayDelayInterest(BigDecimal assignRepayDelayInterest) {
        this.assignRepayDelayInterest = assignRepayDelayInterest;
    }

    public BigDecimal getAssignRepayLateInterest() {
        return assignRepayLateInterest;
    }

    public void setAssignRepayLateInterest(BigDecimal assignRepayLateInterest) {
        this.assignRepayLateInterest = assignRepayLateInterest;
    }

    public BigDecimal getAssignInterestAdvance() {
        return assignInterestAdvance;
    }

    public void setAssignInterestAdvance(BigDecimal assignInterestAdvance) {
        this.assignInterestAdvance = assignInterestAdvance;
    }

    public BigDecimal getAssignPrice() {
        return assignPrice;
    }

    public void setAssignPrice(BigDecimal assignPrice) {
        this.assignPrice = assignPrice;
    }

    public BigDecimal getAssignPay() {
        return assignPay;
    }

    public void setAssignPay(BigDecimal assignPay) {
        this.assignPay = assignPay;
    }

    public BigDecimal getRepayAccountYes() {
        return repayAccountYes;
    }

    public void setRepayAccountYes(BigDecimal repayAccountYes) {
        this.repayAccountYes = repayAccountYes;
    }

    public BigDecimal getRepayCapitalYes() {
        return repayCapitalYes;
    }

    public void setRepayCapitalYes(BigDecimal repayCapitalYes) {
        this.repayCapitalYes = repayCapitalYes;
    }

    public BigDecimal getRepayInterestYes() {
        return repayInterestYes;
    }

    public void setRepayInterestYes(BigDecimal repayInterestYes) {
        this.repayInterestYes = repayInterestYes;
    }

    public BigDecimal getRepayAccountWait() {
        return repayAccountWait;
    }

    public void setRepayAccountWait(BigDecimal repayAccountWait) {
        this.repayAccountWait = repayAccountWait;
    }

    public BigDecimal getRepayCapitalWait() {
        return repayCapitalWait;
    }

    public void setRepayCapitalWait(BigDecimal repayCapitalWait) {
        this.repayCapitalWait = repayCapitalWait;
    }

    public BigDecimal getRepayInterestWait() {
        return repayInterestWait;
    }

    public void setRepayInterestWait(BigDecimal repayInterestWait) {
        this.repayInterestWait = repayInterestWait;
    }

    public Integer getAssignRepayEndTime() {
        return assignRepayEndTime;
    }

    public void setAssignRepayEndTime(Integer assignRepayEndTime) {
        this.assignRepayEndTime = assignRepayEndTime;
    }

    public Integer getAssignRepayLastTime() {
        return assignRepayLastTime;
    }

    public void setAssignRepayLastTime(Integer assignRepayLastTime) {
        this.assignRepayLastTime = assignRepayLastTime;
    }

    public Integer getAssignRepayNextTime() {
        return assignRepayNextTime;
    }

    public void setAssignRepayNextTime(Integer assignRepayNextTime) {
        this.assignRepayNextTime = assignRepayNextTime;
    }

    public Integer getAssignRepayYesTime() {
        return assignRepayYesTime;
    }

    public void setAssignRepayYesTime(Integer assignRepayYesTime) {
        this.assignRepayYesTime = assignRepayYesTime;
    }

    public Integer getAssignRepayPeriod() {
        return assignRepayPeriod;
    }

    public void setAssignRepayPeriod(Integer assignRepayPeriod) {
        this.assignRepayPeriod = assignRepayPeriod;
    }

    public Integer getAssignType() {
        return assignType;
    }

    public void setAssignType(Integer assignType) {
        this.assignType = assignType;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public BigDecimal getAssignServiceApr() {
        return assignServiceApr;
    }

    public void setAssignServiceApr(BigDecimal assignServiceApr) {
        this.assignServiceApr = assignServiceApr;
    }

    public BigDecimal getAssignServiceFee() {
        return assignServiceFee;
    }

    public void setAssignServiceFee(BigDecimal assignServiceFee) {
        this.assignServiceFee = assignServiceFee;
    }

    public Integer getClient() {
        return client;
    }

    public void setClient(Integer client) {
        this.client = client;
    }

    public Integer getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName == null ? null : createUserName.trim();
    }

    public Integer getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Integer updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName == null ? null : updateUserName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}