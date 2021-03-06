/**
 * Description:用户开户列表前端显示查询所用po
 * Copyright: Copyright (HYJF Corporation) 2015
 * Company: HYJF Corporation
 * @author: 王坤
 * @version: 1.0
 * Created at: 2015年11月11日 下午2:17:31
 * Modification History:
 * Modified by :
 */

package com.hyjf.am.trade.dao.model.customize;

import java.io.Serializable;

/**
 * @author lm
 */

public class InvestListCustomize implements Serializable {

	/**
	 *序列化id
	 */
	private static final long serialVersionUID = 5748630051215873837L;
	// 电子账号
	private String accountId;
	// 交易流水号
	private String nid;
	// 标的编号
	private String borrowNid;
	// 年化利率
	private String borrowApr;
	// 借款期限
	private String borrowPeriod;
	// 出借金额
	private String amount;
	// 还款方式
	private String borrowStyleName;
	// 出借方式
	private String investType;
	// 出借时间
	private String addTime;
	// 预计还款时间
	private String recoverTime;
	// 预期收益
	private String recoverInterest;
	// 预期本息收益
	private String recoverTotal;
	// 实际收益
	private String recoverAmountYes;
	// 项目状态 0-出借中(出借未放款)  1-还款中(放款未还款) 2-已还款
	private String status;
	// 计息时间
	private String interestAt;
	// 推荐人
	private String inviteUserName;
	// 推荐人ID
	private String inviteUserId;
	// 推荐人一级部门
	private String inviteRegionName;
	// 推荐人二级部门
	private String inviteBranchName;
	// 推荐人三级部门
	private String inviteDepartmentName;
	// 推荐人一级部门ID
	private String inviteRegionId;
	// 推荐人二级部门ID
	private String inviteBranchId;
	// 推荐人三级部门ID
	private String inviteDepartmentId;
	// 出借人属性
	private String tenderUserAttribute;
	// 推荐人属性
	private String inviteUserAttribute;
	// 标的地址
	private String borrowUrl;
	// 标的类型
	private String projectType;
	// 加息收益率
    private String borrowExtraYield;
    // 预期加息收益
    private String repayInterest;
    // 实际加息收益
    private String repayInterestYes;
    
	public String getInterestAt() {
		return interestAt;
	}

	public void setInterestAt(String interestAt) {
		this.interestAt = interestAt;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public String getBorrowNid() {
		return borrowNid;
	}

	public void setBorrowNid(String borrowNid) {
		this.borrowNid = borrowNid;
	}

	public String getBorrowApr() {
		return borrowApr;
	}

	public void setBorrowApr(String borrowApr) {
		this.borrowApr = borrowApr;
	}

	public String getBorrowPeriod() {
		return borrowPeriod;
	}

	public void setBorrowPeriod(String borrowPeriod) {
		this.borrowPeriod = borrowPeriod;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getBorrowStyleName() {
		return borrowStyleName;
	}

	public void setBorrowStyleName(String borrowStyleName) {
		this.borrowStyleName = borrowStyleName;
	}

	public String getInvestType() {
		return investType;
	}

	public void setInvestType(String investType) {
		this.investType = investType;
	}

	public String getAddTime() {
		return addTime;
	}

	public void setAddTime(String addTime) {
		this.addTime = addTime;
	}

	public String getRecoverTime() {
		return recoverTime;
	}

	public void setRecoverTime(String recoverTime) {
		this.recoverTime = recoverTime;
	}

	public String getRecoverInterest() {
		return recoverInterest;
	}

	public void setRecoverInterest(String recoverInterest) {
		this.recoverInterest = recoverInterest;
	}

	public String getRecoverTotal() {
		return recoverTotal;
	}

	public void setRecoverTotal(String recoverTotal) {
		this.recoverTotal = recoverTotal;
	}

	public String getRecoverAmountYes() {
		return recoverAmountYes;
	}

	public void setRecoverAmountYes(String recoverAmountYes) {
		this.recoverAmountYes = recoverAmountYes;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getInviteUserName() {
		return inviteUserName;
	}

	public void setInviteUserName(String inviteUserName) {
		this.inviteUserName = inviteUserName;
	}

	public String getInviteUserId() {
		return inviteUserId;
	}

	public void setInviteUserId(String inviteUserId) {
		this.inviteUserId = inviteUserId;
	}

	public String getInviteRegionName() {
		return inviteRegionName;
	}

	public void setInviteRegionName(String inviteRegionName) {
		this.inviteRegionName = inviteRegionName;
	}

	public String getInviteBranchName() {
		return inviteBranchName;
	}

	public void setInviteBranchName(String inviteBranchName) {
		this.inviteBranchName = inviteBranchName;
	}

	public String getInviteDepartmentName() {
		return inviteDepartmentName;
	}

	public void setInviteDepartmentName(String inviteDepartmentName) {
		this.inviteDepartmentName = inviteDepartmentName;
	}

	public String getTenderUserAttribute() {
		return tenderUserAttribute;
	}

	public void setTenderUserAttribute(String tenderUserAttribute) {
		this.tenderUserAttribute = tenderUserAttribute;
	}

	public String getInviteUserAttribute() {
		return inviteUserAttribute;
	}

	public void setInviteUserAttribute(String inviteUserAttribute) {
		this.inviteUserAttribute = inviteUserAttribute;
	}

	public String getBorrowUrl() {
		return borrowUrl;
	}

	public void setBorrowUrl(String borrowUrl) {
		this.borrowUrl = borrowUrl;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public String getInviteRegionId() {
		return inviteRegionId;
	}

	public void setInviteRegionId(String inviteRegionId) {
		this.inviteRegionId = inviteRegionId;
	}

	public String getInviteBranchId() {
		return inviteBranchId;
	}

	public void setInviteBranchId(String inviteBranchId) {
		this.inviteBranchId = inviteBranchId;
	}

	public String getInviteDepartmentId() {
		return inviteDepartmentId;
	}

	public void setInviteDepartmentId(String inviteDepartmentId) {
		this.inviteDepartmentId = inviteDepartmentId;
	}

    public String getBorrowExtraYield() {
        return borrowExtraYield;
    }

    public void setBorrowExtraYield(String borrowExtraYield) {
        this.borrowExtraYield = borrowExtraYield;
    }

    public String getRepayInterest() {
        return repayInterest;
    }

    public void setRepayInterest(String repayInterest) {
        this.repayInterest = repayInterest;
    }

    public String getRepayInterestYes() {
        return repayInterestYes;
    }

    public void setRepayInterestYes(String repayInterestYes) {
        this.repayInterestYes = repayInterestYes;
    }
}
