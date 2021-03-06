package com.hyjf.am.trade.dao.model.auto;

import java.io.Serializable;

public class BorrowConsumeWithBLOBs extends BorrowConsume implements Serializable {
    /**
     * 项目描述
     *
     * @mbggenerated
     */
    private String borrowContents;

    /**
     * 财务信息
     *
     * @mbggenerated
     */
    private String accountContents;

    /**
     * 借款冻结金额
     *
     * @mbggenerated
     */
    private String files;

    /**
     * 合作机构
     *
     * @mbggenerated
     */
    private String borrowMeasuresInstit;

    /**
     * 机构介绍
     *
     * @mbggenerated
     */
    private String borrowCompanyInstruction;

    /**
     * 风控措施
     *
     * @mbggenerated
     */
    private String borrowMeasuresMea;

    private static final long serialVersionUID = 1L;

    public String getBorrowContents() {
        return borrowContents;
    }

    public void setBorrowContents(String borrowContents) {
        this.borrowContents = borrowContents == null ? null : borrowContents.trim();
    }

    public String getAccountContents() {
        return accountContents;
    }

    public void setAccountContents(String accountContents) {
        this.accountContents = accountContents == null ? null : accountContents.trim();
    }

    public String getFiles() {
        return files;
    }

    public void setFiles(String files) {
        this.files = files == null ? null : files.trim();
    }

    public String getBorrowMeasuresInstit() {
        return borrowMeasuresInstit;
    }

    public void setBorrowMeasuresInstit(String borrowMeasuresInstit) {
        this.borrowMeasuresInstit = borrowMeasuresInstit == null ? null : borrowMeasuresInstit.trim();
    }

    public String getBorrowCompanyInstruction() {
        return borrowCompanyInstruction;
    }

    public void setBorrowCompanyInstruction(String borrowCompanyInstruction) {
        this.borrowCompanyInstruction = borrowCompanyInstruction == null ? null : borrowCompanyInstruction.trim();
    }

    public String getBorrowMeasuresMea() {
        return borrowMeasuresMea;
    }

    public void setBorrowMeasuresMea(String borrowMeasuresMea) {
        this.borrowMeasuresMea = borrowMeasuresMea == null ? null : borrowMeasuresMea.trim();
    }
}