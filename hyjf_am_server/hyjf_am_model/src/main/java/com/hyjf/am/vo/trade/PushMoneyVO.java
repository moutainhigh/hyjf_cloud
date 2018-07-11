/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.vo.trade;

import com.hyjf.am.vo.BaseVO;

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
}
