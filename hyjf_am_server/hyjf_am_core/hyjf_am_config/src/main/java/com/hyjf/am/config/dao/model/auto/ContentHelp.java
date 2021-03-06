package com.hyjf.am.config.dao.model.auto;

import java.io.Serializable;
import java.util.Date;

public class ContentHelp implements Serializable {
    private Integer id;

    private Integer pcateId;

    /**
     * 分类ID
     *
     * @mbggenerated
     */
    private Integer cateId;

    /**
     * 问题标题
     *
     * @mbggenerated
     */
    private String title;

    /**
     * 内容
     *
     * @mbggenerated
     */
    private String content;

    /**
     * 排序
     *
     * @mbggenerated
     */
    private Integer order;

    /**
     * 状态  0启用 1开启
     *
     * @mbggenerated
     */
    private Integer status;

    /**
     * 是否单页1单页，0非单页
     *
     * @mbggenerated
     */
    private Integer type;

    /**
     * 别名
     *
     * @mbggenerated
     */
    private String code;

    /**
     * 文章来源
     *
     * @mbggenerated
     */
    private String source;

    /**
     * 作者
     *
     * @mbggenerated
     */
    private String author;

    /**
     * 缩略图
     *
     * @mbggenerated
     */
    private String thumb;

    /**
     * 简要介绍
     *
     * @mbggenerated
     */
    private String summary;

    private String seoTitle;

    private String seoKeyword;

    private String seoDescription;

    /**
     * 点击数
     *
     * @mbggenerated
     */
    private Integer hits;

    private String outLink;

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

    private Date createTime;

    private Date updateTime;

    /**
     * 智齿客服状态
     *
     * @mbggenerated
     */
    private Integer zhichiStatus;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPcateId() {
        return pcateId;
    }

    public void setPcateId(Integer pcateId) {
        this.pcateId = pcateId;
    }

    public Integer getCateId() {
        return cateId;
    }

    public void setCateId(Integer cateId) {
        this.cateId = cateId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source == null ? null : source.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author == null ? null : author.trim();
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb == null ? null : thumb.trim();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    public String getSeoTitle() {
        return seoTitle;
    }

    public void setSeoTitle(String seoTitle) {
        this.seoTitle = seoTitle == null ? null : seoTitle.trim();
    }

    public String getSeoKeyword() {
        return seoKeyword;
    }

    public void setSeoKeyword(String seoKeyword) {
        this.seoKeyword = seoKeyword == null ? null : seoKeyword.trim();
    }

    public String getSeoDescription() {
        return seoDescription;
    }

    public void setSeoDescription(String seoDescription) {
        this.seoDescription = seoDescription == null ? null : seoDescription.trim();
    }

    public Integer getHits() {
        return hits;
    }

    public void setHits(Integer hits) {
        this.hits = hits;
    }

    public String getOutLink() {
        return outLink;
    }

    public void setOutLink(String outLink) {
        this.outLink = outLink == null ? null : outLink.trim();
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

    public Integer getZhichiStatus() {
        return zhichiStatus;
    }

    public void setZhichiStatus(Integer zhichiStatus) {
        this.zhichiStatus = zhichiStatus;
    }
}