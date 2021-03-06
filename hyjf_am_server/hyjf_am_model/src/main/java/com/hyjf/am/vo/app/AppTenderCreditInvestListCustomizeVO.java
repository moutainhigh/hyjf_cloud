package com.hyjf.am.vo.app;

import java.io.Serializable;

/**
 * @author liuyang
 */

public class AppTenderCreditInvestListCustomizeVO implements Serializable {

    /**
     * 序列化id
     */
    private static final long serialVersionUID = -4720030760960740262L;

    // 用户名
    private String userName;

    // vip等级
    private String vipId;

    // 出借金额
    private String account;

    // 出借时间
    private String investTime;

    /**
     * 构造方法
     */

    public AppTenderCreditInvestListCustomizeVO() {
        super();
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getInvestTime() {
        return investTime;
    }

    public void setInvestTime(String investTime) {
        this.investTime = investTime;
    }

    public String getVipId() {
        return vipId;
    }

    public void setVipId(String vipId) {
        this.vipId = vipId;
    }

}
