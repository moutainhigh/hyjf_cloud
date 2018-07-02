package com.hyjf.cs.user.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SystemConfig {

    @Value("${aop.interface.accesskey}")
    public String aopAccesskey;

    @Value("${hyjf.api.web.url}")
    public String apiWebUrl;

    @Value("${hyjf.web.host}")
    public String webHost;
    
    @Value("{hyjf.web.ui.bindemail}")
    public String webUIBindEmail;

    @Value("${http.hyjf.web.host}")
    public String httpWebHost;

    @Value("${hyjf.bank.instcode}")
    public String bankInstcode;

    @Value("${hyjf.bank.bankcode}")
    public String bankCode;

    @Value("${file.domain.head.url}")
    public String headUrl;

    @Value("${file.domain.url}")
    public String fileDomainUrl;

    @Value("${file.upload.real.path}")
    public String fileUpload;

    @Value("${file.upload.head.path}")
    public String uploadHeadPath;

    @Value("${hyjf.front.host}")
    public String frontHost;

    @Value("${hyjf.activity.id}")
    public String activityId;

    @Value("${hyjf.web.bank.forgetpassword}")
    public String forgetpassword;

    @Value("${hyjf.bank.instcode}")
    public String instcode;

    @Value("${hyjf.bank.bankcode}")
    public String bankcode;

    @Value("${file.domain.app.url}")
    public String domainAppUrl;

    @Value("${hyjf.activity.888.id}")
    public Integer activity888Id;

    @Value("${file.physical.path}")
    public String physicalPath;

    public String getPhysicalPath() {
        return physicalPath;
    }

    public void setPhysicalPath(String physicalPath) {
        this.physicalPath = physicalPath;
    }

    public Integer getActivity888Id() {
        return activity888Id;
    }

    public void setActivity888Id(Integer activity888Id) {
        this.activity888Id = activity888Id;
    }

    public String getDomainAppUrl() {
        return domainAppUrl;
    }

    public void setDomainAppUrl(String domainAppUrl) {
        this.domainAppUrl = domainAppUrl;
    }

    public String getAopAccesskey() {
        return aopAccesskey;
    }

    public void setAopAccesskey(String aopAccesskey) {
        this.aopAccesskey = aopAccesskey;
    }

    public String getApiWebUrl() {
        return apiWebUrl;
    }

    public void setApiWebUrl(String apiWebUrl) {
        this.apiWebUrl = apiWebUrl;
    }

    public String getInstcode() {
        return instcode;
    }

    public void setInstcode(String instcode) {
        this.instcode = instcode;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public String getForgetpassword() {
        return forgetpassword;
    }

    public void setForgetpassword(String forgetpassword) {
        this.forgetpassword = forgetpassword;
    }

    public String getUploadHeadPath() {
        return uploadHeadPath;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public void setUploadHeadPath(String uploadHeadPath) {
        this.uploadHeadPath = uploadHeadPath;
    }

    public String getWebHost() {
        return webHost;
    }

    public void setWebHost(String webHost) {
        this.webHost = webHost;
    }

    public String getHttpWebHost() {
        return httpWebHost;
    }

    public void setHttpWebHost(String httpWebHost) {
        this.httpWebHost = httpWebHost;
    }

    public String getBankInstcode() {
        return bankInstcode;
    }

    public void setBankInstcode(String bankInstcode) {
        this.bankInstcode = bankInstcode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }

    public String getFileDomainUrl() {
        return fileDomainUrl;
    }

    public void setFileDomainUrl(String fileDomainUrl) {
        this.fileDomainUrl = fileDomainUrl;
    }

    public String getFileUpload() {
        return fileUpload;
    }

    public void setFileUpload(String fileUpload) {
        this.fileUpload = fileUpload;
    }

	public String getWebUIBindEmail() {
		return webUIBindEmail;
	}

	public void setWebUIBindEmail(String webUIBindEmail) {
		this.webUIBindEmail = webUIBindEmail;
	}

    public String getFrontHost() {
        return frontHost;
    }

    public void setFrontHost(String frontHost) {
        this.frontHost = frontHost;
    }
}
