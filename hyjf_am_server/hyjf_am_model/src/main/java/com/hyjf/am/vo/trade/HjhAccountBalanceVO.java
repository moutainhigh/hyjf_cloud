/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.vo.trade;

import com.hyjf.am.vo.BaseVO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author liubin
 * @version HjhAccountBalanceVO, v0.1 2018/7/31 14:05
 */
public class HjhAccountBalanceVO extends BaseVO implements Serializable {

    private String id;

    private Date date;

    private BigDecimal investAccount;

    private BigDecimal creditAccount;

    private BigDecimal reinvestAccount;

    private BigDecimal addAccount;

    private Integer createUser;

    private Integer createTime;

    private Integer updateUser;

    private Integer updateTime;

    private Integer delFlg;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public BigDecimal getInvestAccount() {
        return investAccount;
    }

    public void setInvestAccount(BigDecimal investAccount) {
        this.investAccount = investAccount;
    }

    public BigDecimal getCreditAccount() {
        return creditAccount;
    }

    public void setCreditAccount(BigDecimal creditAccount) {
        this.creditAccount = creditAccount;
    }

    public BigDecimal getReinvestAccount() {
        return reinvestAccount;
    }

    public void setReinvestAccount(BigDecimal reinvestAccount) {
        this.reinvestAccount = reinvestAccount;
    }

    public BigDecimal getAddAccount() {
        return addAccount;
    }

    public void setAddAccount(BigDecimal addAccount) {
        this.addAccount = addAccount;
    }

    public Integer getCreateUser() {
        return createUser;
    }

    public void setCreateUser(Integer createUser) {
        this.createUser = createUser;
    }

    public Integer getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Integer createTime) {
        this.createTime = createTime;
    }

    public Integer getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Integer updateUser) {
        this.updateUser = updateUser;
    }

    public Integer getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Integer updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDelFlg() {
        return delFlg;
    }

    public void setDelFlg(Integer delFlg) {
        this.delFlg = delFlg;
    }
}
