package com.hyjf.cs.trade.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 计划标的组成响应
 */
public class WechatPlanBorrowResultBean implements Serializable {

	// 加入人次
	private Integer userCount;
	// 加入金额
	private String account;
	private Boolean isEnd;

	private List<BorrowList> borrowList;

	public WechatPlanBorrowResultBean() {
		super();
		this.borrowList = new ArrayList<BorrowList>();
	}

    public static final class BorrowList {
        // 项目编号
		private String borrowNid;
        // 历史年回报率
		private String borrowApr;
        // 项目期限
		private String borrowPeriod;
		//真实姓名
		private String trueName;

		public String getTrueName() {
			return trueName;
		}

		public void setTrueName(String trueName) {
			this.trueName = trueName;
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
	}

	public Integer getUserCount() {
		return userCount;
	}

	public void setUserCount(Integer userCount) {
		this.userCount = userCount;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Boolean isEnd() {
		return isEnd;
	}

	public void setEnd(Boolean end) {
		isEnd = end;
	}

	public List<BorrowList> getBorrowList() {
		return borrowList;
	}

	public void setBorrowList(List<BorrowList> borrowList) {
		this.borrowList = borrowList;
	}
}
