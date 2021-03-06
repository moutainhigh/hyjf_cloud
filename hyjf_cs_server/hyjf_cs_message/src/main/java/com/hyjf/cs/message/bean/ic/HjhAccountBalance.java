/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.message.bean.ic;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author liubin
 * @version HjhAccountBalance, v0.1 2018/7/31 14:05
 */
@Document(collection = "ht_hjh_account_balance")
public class HjhAccountBalance implements Serializable {

    private String id;

    private Date date;

    private BigDecimal investAccount;

    private BigDecimal creditAccount;

    private BigDecimal reinvestAccount;

    private BigDecimal addAccount;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Integer updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDelFlg() {
        return delFlg;
    }

    public void setDelFlg(Integer delFlg) {
        this.delFlg = delFlg;
    }
}
