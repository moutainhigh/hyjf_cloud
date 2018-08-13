/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.vo.trade;

import com.hyjf.am.vo.BaseVO;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author fuqiang
 * @version PushMoneyVO, v0.1 2018/7/10 10:57
 */
public class PushMoneyVO extends BaseVO {
    private Integer id;

    private String type;

    private Integer projectType;

    private Integer rewardSend;

    private String dayTender;

    private String monthTender;

    private Integer createBy;

    private Integer updateBy;

    private Date createTime;

    private Date updateTime;

    private String remark;
    //项目编号
    public String borrowNid;
    // 项目标题
    public String borrowName;
    // 项目还款方式  = endday 天   !=endday 个月
    public String borrowStyle;
    // 融资期限
    public String borrowPeriod;
    // 融资金额
    public String account;
    // 提成总额
    public String commission;
    // 放款时间
    public String recoverLastTime;
    public String recoverLastTimeStart;
    public String recoverLastTimeEnd;
    // 提成列表用
    // 融资期限
    private String rzqx;
    // 分公司
    private String regionName;
    // 分部
    private String branchName;
    // 团队
    private String departmentName;
    // 提成人
    private String username;
    // 电子账号
    private String accountId;
    // 提成人用户属性0=>无主单 1=>有主单 2=>线下员工 3=>线上员工
    private String attribute;
    // 是否51老用户，1：是 0：否
    private Integer is51;
    // 导出用
    private String is51Name;
    // 投资人
    private String usernameTender;
    // 投资金额
    private BigDecimal accountTender;
    // 状态
    private String statusName;
    // 投资时间
    private String tenderTimeView;
    // 提成发放时间
    private String sendTimeView;

    public String getBorrowNid() {
        return borrowNid;
    }

    public void setBorrowNid(String borrowNid) {
        this.borrowNid = borrowNid;
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

    public String getBorrowPeriod() {
        return borrowPeriod;
    }

    public void setBorrowPeriod(String borrowPeriod) {
        this.borrowPeriod = borrowPeriod;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCommission() {
        return commission;
    }

    public void setCommission(String commission) {
        this.commission = commission;
    }

    public String getRecoverLastTime() {
        return recoverLastTime;
    }

    public void setRecoverLastTime(String recoverLastTime) {
        this.recoverLastTime = recoverLastTime;
    }

    public String getRecoverLastTimeStart() {
        return recoverLastTimeStart;
    }

    public void setRecoverLastTimeStart(String recoverLastTimeStart) {
        this.recoverLastTimeStart = recoverLastTimeStart;
    }

    public String getRecoverLastTimeEnd() {
        return recoverLastTimeEnd;
    }

    public void setRecoverLastTimeEnd(String recoverLastTimeEnd) {
        this.recoverLastTimeEnd = recoverLastTimeEnd;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public Integer getProjectType() {
        return projectType;
    }

    public void setProjectType(Integer projectType) {
        this.projectType = projectType;
    }

    public Integer getRewardSend() {
        return rewardSend;
    }

    public void setRewardSend(Integer rewardSend) {
        this.rewardSend = rewardSend;
    }

    public String getDayTender() {
        return dayTender;
    }

    public void setDayTender(String dayTender) {
        this.dayTender = dayTender == null ? null : dayTender.trim();
    }

    public String getMonthTender() {
        return monthTender;
    }

    public void setMonthTender(String monthTender) {
        this.monthTender = monthTender == null ? null : monthTender.trim();
    }

    public Integer getCreateBy() {
        return createBy;
    }

    public void setCreateBy(Integer createBy) {
        this.createBy = createBy;
    }

    public Integer getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(Integer updateBy) {
        this.updateBy = updateBy;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public String getRzqx() {
        return rzqx;
    }

    public void setRzqx(String rzqx) {
        this.rzqx = rzqx;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Integer getIs51() {
        return is51;
    }

    public void setIs51(Integer is51) {
        this.is51 = is51;
    }

    public String getIs51Name() {
        return is51Name;
    }

    public void setIs51Name(String is51Name) {
        this.is51Name = is51Name;
    }

    public String getUsernameTender() {
        return usernameTender;
    }

    public void setUsernameTender(String usernameTender) {
        this.usernameTender = usernameTender;
    }

    public BigDecimal getAccountTender() {
        return accountTender;
    }

    public void setAccountTender(BigDecimal accountTender) {
        this.accountTender = accountTender;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getTenderTimeView() {
        return tenderTimeView;
    }

    public void setTenderTimeView(String tenderTimeView) {
        this.tenderTimeView = tenderTimeView;
    }

    public String getSendTimeView() {
        return sendTimeView;
    }

    public void setSendTimeView(String sendTimeView) {
        this.sendTimeView = sendTimeView;
    }
}
