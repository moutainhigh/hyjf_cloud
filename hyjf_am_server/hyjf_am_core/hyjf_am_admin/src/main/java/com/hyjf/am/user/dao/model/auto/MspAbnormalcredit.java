package com.hyjf.am.user.dao.model.auto;

import java.io.Serializable;

public class MspAbnormalcredit implements Serializable {
    private String id;

    /**
     * 申请编号
     *
     * @mbggenerated
     */
    private String applyId;

    /**
     * 借款日期
     *
     * @mbggenerated
     */
    private String creditstartdate;

    /**
     * 到期日期
     *
     * @mbggenerated
     */
    private String creditenddate;

    /**
     * 借款类型
     *
     * @mbggenerated
     */
    private String loantype;

    /**
     * 借款金额
     *
     * @mbggenerated
     */
    private String loanmoney;

    /**
     * 担保方式
     *
     * @mbggenerated
     */
    private String assuretype;

    /**
     * 借款日期
     *
     * @mbggenerated
     */
    private String loanperiods;

    /**
     * 编号
     *
     * @mbggenerated
     */
    private String num;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId == null ? null : applyId.trim();
    }

    public String getCreditstartdate() {
        return creditstartdate;
    }

    public void setCreditstartdate(String creditstartdate) {
        this.creditstartdate = creditstartdate == null ? null : creditstartdate.trim();
    }

    public String getCreditenddate() {
        return creditenddate;
    }

    public void setCreditenddate(String creditenddate) {
        this.creditenddate = creditenddate == null ? null : creditenddate.trim();
    }

    public String getLoantype() {
        return loantype;
    }

    public void setLoantype(String loantype) {
        this.loantype = loantype == null ? null : loantype.trim();
    }

    public String getLoanmoney() {
        return loanmoney;
    }

    public void setLoanmoney(String loanmoney) {
        this.loanmoney = loanmoney == null ? null : loanmoney.trim();
    }

    public String getAssuretype() {
        return assuretype;
    }

    public void setAssuretype(String assuretype) {
        this.assuretype = assuretype == null ? null : assuretype.trim();
    }

    public String getLoanperiods() {
        return loanperiods;
    }

    public void setLoanperiods(String loanperiods) {
        this.loanperiods = loanperiods == null ? null : loanperiods.trim();
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num == null ? null : num.trim();
    }
}