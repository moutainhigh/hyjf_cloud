package com.hyjf.am.vo.config;

import com.hyjf.am.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xiasq
 * @version SmsMailTemplateVO, v0.1 2018/5/4 10:40
 */
public class SmsMailTemplateVO extends BaseVO implements Serializable {
    private Integer id;

    private String mailName;

    private String mailValue;

    private Integer mailStatus;

    private String mailContent;

    private Integer createUserId;

    private Integer updateUserId;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMailName() {
        return mailName;
    }

    public void setMailName(String mailName) {
        this.mailName = mailName == null ? null : mailName.trim();
    }

    public String getMailValue() {
        return mailValue;
    }

    public void setMailValue(String mailValue) {
        this.mailValue = mailValue == null ? null : mailValue.trim();
    }

    public Integer getMailStatus() {
        return mailStatus;
    }

    public void setMailStatus(Integer mailStatus) {
        this.mailStatus = mailStatus;
    }

    public String getMailContent() {
        return mailContent;
    }

    public void setMailContent(String mailContent) {
        this.mailContent = mailContent == null ? null : mailContent.trim();
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

    @Override
    public String toString() {
        return "SmsMailTemplateVO{" +
                "id=" + id +
                ", mailName='" + mailName + '\'' +
                ", mailValue='" + mailValue + '\'' +
                ", mailStatus=" + mailStatus +
                ", mailContent='" + mailContent + '\'' +
                ", createUserId=" + createUserId +
                ", updateUserId=" + updateUserId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
