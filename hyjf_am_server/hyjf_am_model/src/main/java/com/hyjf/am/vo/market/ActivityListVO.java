package com.hyjf.am.vo.market;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.hyjf.am.vo.BaseVO;

import java.util.Date;

/**
 * @author xiasq
 * @version ActivityListVO, v0.1 2018/5/14 16:13
 */
public class ActivityListVO extends BaseVO {
    private int id;
    //活动名称
    private String title;
    /**
     * 前台时间接收
     */
    private String acStartTime;

    private String acEndTime;

    private int timeStart;

    private int timeEnd;

    private Date startCreate;

    private Date endCreate;

    private String platform;

    private String status;

    private String imgPc;

    private String imgApp;

    private String imgWei;

    private String activityPcUrl;

    private String activityAppUrl;

    private String activityWeiUrl;

    private String img;

    private String qr;

    private String urlForeground;

    private String urlBackground;

    private String description;

    private Date createTime;

    private Date updateTime;

    public int getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(int timeStart) {
        this.timeStart = timeStart;
    }

    public int getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(int timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getStartCreate() {
        return startCreate;
    }

    public void setStartCreate(Date startCreate) {
        this.startCreate = startCreate;
    }

    public Date getEndCreate() {
        return endCreate;
    }

    public void setEndCreate(Date endCreate) {
        this.endCreate = endCreate;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrlForeground() {
        return urlForeground;
    }

    public void setUrlForeground(String urlForeground) {
        this.urlForeground = urlForeground;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getImgPc() {
        return imgPc;
    }

    public void setImgPc(String imgPc) {
        this.imgPc = imgPc;
    }

    public String getImgApp() {
        return imgApp;
    }

    public void setImgApp(String imgApp) {
        this.imgApp = imgApp;
    }

    public String getImgWei() {
        return imgWei;
    }

    public void setImgWei(String imgWei) {
        this.imgWei = imgWei;
    }

    public String getActivityPcUrl() {
        return activityPcUrl;
    }

    public void setActivityPcUrl(String activityPcUrl) {
        this.activityPcUrl = activityPcUrl;
    }

    public String getActivityAppUrl() {
        return activityAppUrl;
    }

    public void setActivityAppUrl(String activityAppUrl) {
        this.activityAppUrl = activityAppUrl;
    }

    public String getActivityWeiUrl() {
        return activityWeiUrl;
    }

    public void setActivityWeiUrl(String activityWeiUrl) {
        this.activityWeiUrl = activityWeiUrl;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }

    public String getUrlBackground() {
        return urlBackground;
    }

    public void setUrlBackground(String urlBackground) {
        this.urlBackground = urlBackground;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getAcStartTime() {
        return acStartTime;
    }

    public void setAcStartTime(String acStartTime) {
        this.acStartTime = acStartTime;
    }

    public String getAcEndTime() {
        return acEndTime;
    }

    public void setAcEndTime(String acEndTime) {
        this.acEndTime = acEndTime;
    }
}
