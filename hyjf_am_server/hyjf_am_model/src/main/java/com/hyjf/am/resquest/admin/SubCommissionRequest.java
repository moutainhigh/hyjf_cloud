/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.resquest.admin;

import com.hyjf.am.vo.BasePage;
import com.hyjf.am.vo.BaseVO;

import java.io.Serializable;

/**
 * @author: sunpeikai
 * @version: SubCommissionRequest, v0.1 2018/7/10 10:43
 */
public class SubCommissionRequest extends BasePage implements Serializable {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1251849043086260227L;

    /**
     * 转入用户名检索
     */
    private String receiveUserNameSrch;

    /**
     * 转入姓名
     */
    private String truename;

    /**
     * 订单号检索用
     */
    private String orderIdSrch;

    /**
     * 转账状态检索用
     */
    private String tradeStatusSrch;

    /**
     * 添加时间开始(检索用)
     */
    private String timeStartSrch;
    /**
     * 添加时间结束(检索用)
     */
    private String timeEndSrch;

    /**
     * 转出方用户电子账户号
     */
    private String accountId;

    /**
     * 收款方用户ID
     */
    private Integer receiveUserId;

    /**
     * 收款方用户名
     */
    private String receiveUserName;

    /**
     * 收款方用户电子账户号
     */
    private String receiveAccountId;

    /**
     * 分佣金额
     */
    private String txAmount;
    /**
     * 交易密码
     */
    private String password;

    /**
     * 账户余额
     */
    private String balance;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 分页变量
     */
    private int limitStart = -1;
    private int limitEnd = -1;

    public String getReceiveUserNameSrch() {
        return receiveUserNameSrch;
    }

    public void setReceiveUserNameSrch(String receiveUserNameSrch) {
        this.receiveUserNameSrch = receiveUserNameSrch;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getOrderIdSrch() {
        return orderIdSrch;
    }

    public void setOrderIdSrch(String orderIdSrch) {
        this.orderIdSrch = orderIdSrch;
    }

    public String getTradeStatusSrch() {
        return tradeStatusSrch;
    }

    public void setTradeStatusSrch(String tradeStatusSrch) {
        this.tradeStatusSrch = tradeStatusSrch;
    }

    public String getTimeStartSrch() {
        return timeStartSrch;
    }

    public void setTimeStartSrch(String timeStartSrch) {
        this.timeStartSrch = timeStartSrch;
    }

    public String getTimeEndSrch() {
        return timeEndSrch;
    }

    public void setTimeEndSrch(String timeEndSrch) {
        this.timeEndSrch = timeEndSrch;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Integer getReceiveUserId() {
        return receiveUserId;
    }

    public void setReceiveUserId(Integer receiveUserId) {
        this.receiveUserId = receiveUserId;
    }

    public String getReceiveUserName() {
        return receiveUserName;
    }

    public void setReceiveUserName(String receiveUserName) {
        this.receiveUserName = receiveUserName;
    }

    public String getReceiveAccountId() {
        return receiveAccountId;
    }

    public void setReceiveAccountId(String receiveAccountId) {
        this.receiveAccountId = receiveAccountId;
    }

    public String getTxAmount() {
        return txAmount;
    }

    public void setTxAmount(String txAmount) {
        this.txAmount = txAmount;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public int getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(int limitStart) {
        this.limitStart = limitStart;
    }

    public int getLimitEnd() {
        return limitEnd;
    }

    public void setLimitEnd(int limitEnd) {
        this.limitEnd = limitEnd;
    }
}