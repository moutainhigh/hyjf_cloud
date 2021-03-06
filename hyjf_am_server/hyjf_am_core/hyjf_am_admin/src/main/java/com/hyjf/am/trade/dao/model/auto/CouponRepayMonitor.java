package com.hyjf.am.trade.dao.model.auto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class CouponRepayMonitor implements Serializable {
    /**
     * 主键
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * 日
     *
     * @mbggenerated
     */
    private String day;

    /**
     * 星期
     *
     * @mbggenerated
     */
    private String week;

    /**
     * 待收收益总额
     *
     * @mbggenerated
     */
    private BigDecimal interestWaitTotal;

    /**
     * 已收收益总额
     *
     * @mbggenerated
     */
    private BigDecimal interestYesTotal;

    /**
     * 删除标识 0：未删除，1：已删除
     *
     * @mbggenerated
     */
    private Integer delFlag;

    /**
     * 创建人
     *
     * @mbggenerated
     */
    private Integer createUserId;

    /**
     * 修改人
     *
     * @mbggenerated
     */
    private Integer updateUserId;

    /**
     * 创建时间
     *
     * @mbggenerated
     */
    private Date createTime;

    /**
     * 更新时间
     *
     * @mbggenerated
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day == null ? null : day.trim();
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week == null ? null : week.trim();
    }

    public BigDecimal getInterestWaitTotal() {
        return interestWaitTotal;
    }

    public void setInterestWaitTotal(BigDecimal interestWaitTotal) {
        this.interestWaitTotal = interestWaitTotal;
    }

    public BigDecimal getInterestYesTotal() {
        return interestYesTotal;
    }

    public void setInterestYesTotal(BigDecimal interestYesTotal) {
        this.interestYesTotal = interestYesTotal;
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

    public Integer getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Integer updateUserId) {
        this.updateUserId = updateUserId;
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