package com.hyjf.am.user.dao.model.auto;

import java.io.Serializable;
import java.util.Date;

public class Utm implements Serializable {
    private Integer utmId;

    /**
     * 平台
     *
     * @mbggenerated
     */
    private String utmSource;

    /**
     * 账户推广平台
     *
     * @mbggenerated
     */
    private Integer sourceId;

    /**
     * 媒介
     *
     * @mbggenerated
     */
    private String utmMedium;

    /**
     * 关键词
     *
     * @mbggenerated
     */
    private String utmTerm;

    /**
     * 广告系列内容
     *
     * @mbggenerated
     */
    private String utmContent;

    /**
     * 广告系列名称
     *
     * @mbggenerated
     */
    private String utmCampaign;

    /**
     * 推荐人id
     *
     * @mbggenerated
     */
    private Integer utmReferrer;

    /**
     * 链接地址
     *
     * @mbggenerated
     */
    private String linkAddress;

    private String remark;

    /**
     * 状态 1启用 2禁用
     *
     * @mbggenerated
     */
    private Integer status;

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

    public Integer getUtmId() {
        return utmId;
    }

    public void setUtmId(Integer utmId) {
        this.utmId = utmId;
    }

    public String getUtmSource() {
        return utmSource;
    }

    public void setUtmSource(String utmSource) {
        this.utmSource = utmSource == null ? null : utmSource.trim();
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getUtmMedium() {
        return utmMedium;
    }

    public void setUtmMedium(String utmMedium) {
        this.utmMedium = utmMedium == null ? null : utmMedium.trim();
    }

    public String getUtmTerm() {
        return utmTerm;
    }

    public void setUtmTerm(String utmTerm) {
        this.utmTerm = utmTerm == null ? null : utmTerm.trim();
    }

    public String getUtmContent() {
        return utmContent;
    }

    public void setUtmContent(String utmContent) {
        this.utmContent = utmContent == null ? null : utmContent.trim();
    }

    public String getUtmCampaign() {
        return utmCampaign;
    }

    public void setUtmCampaign(String utmCampaign) {
        this.utmCampaign = utmCampaign == null ? null : utmCampaign.trim();
    }

    public Integer getUtmReferrer() {
        return utmReferrer;
    }

    public void setUtmReferrer(Integer utmReferrer) {
        this.utmReferrer = utmReferrer;
    }

    public String getLinkAddress() {
        return linkAddress;
    }

    public void setLinkAddress(String linkAddress) {
        this.linkAddress = linkAddress == null ? null : linkAddress.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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