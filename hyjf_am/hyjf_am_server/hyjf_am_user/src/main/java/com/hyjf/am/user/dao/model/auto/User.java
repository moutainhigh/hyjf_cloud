package com.hyjf.am.user.dao.model.auto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class User implements Serializable {
    private Integer userId;

    private String username;

    private String mobile;

    private String email;

    private String password;

    private String paypassword;

    private Integer referrer;

    private String referrerUserName;

    private String salt;

    private Integer status;

    private Integer openAccount;

    private Integer borrowSms;

    private Integer rechargeSms;

    private Integer withdrawSms;

    private Boolean ifReceiveNotice;

    private String iconurl;

    private BigDecimal version;

    private Integer investSms;

    private Integer recieveSms;

    private Integer regEsb;

    private String eprovince;

    private Integer sendSms;

    private Integer pid;

    private String usernamep;

    private Integer isInstFlag;

    private String instCode;

    private Integer ptype;

    private Integer accountEsb;

    private Integer investflag;

    private Integer userType;

    private Boolean authType;

    private Boolean authStatus;

    private Boolean paymentAuthStatus;

    private Date authTime;

    private Byte recodTotal;

    private Date recodTime;

    private Date recodTruncateTime;

    private Integer isSetPassword;

    private Integer bankOpenAccount;

    private Integer bankAccountEsb;

    private Integer isDataUpdate;

    private Integer isEvaluationFlag;

    private Integer isCaFlag;

    private Integer isSmtp;

    private String regIp;

    private Date regTime;

    private Integer createUserId;

    private Integer updateUserId;

    private Date createTime;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getPaypassword() {
        return paypassword;
    }

    public void setPaypassword(String paypassword) {
        this.paypassword = paypassword == null ? null : paypassword.trim();
    }

    public Integer getReferrer() {
        return referrer;
    }

    public void setReferrer(Integer referrer) {
        this.referrer = referrer;
    }

    public String getReferrerUserName() {
        return referrerUserName;
    }

    public void setReferrerUserName(String referrerUserName) {
        this.referrerUserName = referrerUserName == null ? null : referrerUserName.trim();
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt == null ? null : salt.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getOpenAccount() {
        return openAccount;
    }

    public void setOpenAccount(Integer openAccount) {
        this.openAccount = openAccount;
    }

    public Integer getBorrowSms() {
        return borrowSms;
    }

    public void setBorrowSms(Integer borrowSms) {
        this.borrowSms = borrowSms;
    }

    public Integer getRechargeSms() {
        return rechargeSms;
    }

    public void setRechargeSms(Integer rechargeSms) {
        this.rechargeSms = rechargeSms;
    }

    public Integer getWithdrawSms() {
        return withdrawSms;
    }

    public void setWithdrawSms(Integer withdrawSms) {
        this.withdrawSms = withdrawSms;
    }

    public Boolean getIfReceiveNotice() {
        return ifReceiveNotice;
    }

    public void setIfReceiveNotice(Boolean ifReceiveNotice) {
        this.ifReceiveNotice = ifReceiveNotice;
    }

    public String getIconurl() {
        return iconurl;
    }

    public void setIconurl(String iconurl) {
        this.iconurl = iconurl == null ? null : iconurl.trim();
    }

    public BigDecimal getVersion() {
        return version;
    }

    public void setVersion(BigDecimal version) {
        this.version = version;
    }

    public Integer getInvestSms() {
        return investSms;
    }

    public void setInvestSms(Integer investSms) {
        this.investSms = investSms;
    }

    public Integer getRecieveSms() {
        return recieveSms;
    }

    public void setRecieveSms(Integer recieveSms) {
        this.recieveSms = recieveSms;
    }

    public Integer getRegEsb() {
        return regEsb;
    }

    public void setRegEsb(Integer regEsb) {
        this.regEsb = regEsb;
    }

    public String getEprovince() {
        return eprovince;
    }

    public void setEprovince(String eprovince) {
        this.eprovince = eprovince == null ? null : eprovince.trim();
    }

    public Integer getSendSms() {
        return sendSms;
    }

    public void setSendSms(Integer sendSms) {
        this.sendSms = sendSms;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public String getUsernamep() {
        return usernamep;
    }

    public void setUsernamep(String usernamep) {
        this.usernamep = usernamep == null ? null : usernamep.trim();
    }

    public Integer getIsInstFlag() {
        return isInstFlag;
    }

    public void setIsInstFlag(Integer isInstFlag) {
        this.isInstFlag = isInstFlag;
    }

    public String getInstCode() {
        return instCode;
    }

    public void setInstCode(String instCode) {
        this.instCode = instCode == null ? null : instCode.trim();
    }

    public Integer getPtype() {
        return ptype;
    }

    public void setPtype(Integer ptype) {
        this.ptype = ptype;
    }

    public Integer getAccountEsb() {
        return accountEsb;
    }

    public void setAccountEsb(Integer accountEsb) {
        this.accountEsb = accountEsb;
    }

    public Integer getInvestflag() {
        return investflag;
    }

    public void setInvestflag(Integer investflag) {
        this.investflag = investflag;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Boolean getAuthType() {
        return authType;
    }

    public void setAuthType(Boolean authType) {
        this.authType = authType;
    }

    public Boolean getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(Boolean authStatus) {
        this.authStatus = authStatus;
    }

    public Boolean getPaymentAuthStatus() {
        return paymentAuthStatus;
    }

    public void setPaymentAuthStatus(Boolean paymentAuthStatus) {
        this.paymentAuthStatus = paymentAuthStatus;
    }

    public Date getAuthTime() {
        return authTime;
    }

    public void setAuthTime(Date authTime) {
        this.authTime = authTime;
    }

    public Byte getRecodTotal() {
        return recodTotal;
    }

    public void setRecodTotal(Byte recodTotal) {
        this.recodTotal = recodTotal;
    }

    public Date getRecodTime() {
        return recodTime;
    }

    public void setRecodTime(Date recodTime) {
        this.recodTime = recodTime;
    }

    public Date getRecodTruncateTime() {
        return recodTruncateTime;
    }

    public void setRecodTruncateTime(Date recodTruncateTime) {
        this.recodTruncateTime = recodTruncateTime;
    }

    public Integer getIsSetPassword() {
        return isSetPassword;
    }

    public void setIsSetPassword(Integer isSetPassword) {
        this.isSetPassword = isSetPassword;
    }

    public Integer getBankOpenAccount() {
        return bankOpenAccount;
    }

    public void setBankOpenAccount(Integer bankOpenAccount) {
        this.bankOpenAccount = bankOpenAccount;
    }

    public Integer getBankAccountEsb() {
        return bankAccountEsb;
    }

    public void setBankAccountEsb(Integer bankAccountEsb) {
        this.bankAccountEsb = bankAccountEsb;
    }

    public Integer getIsDataUpdate() {
        return isDataUpdate;
    }

    public void setIsDataUpdate(Integer isDataUpdate) {
        this.isDataUpdate = isDataUpdate;
    }

    public Integer getIsEvaluationFlag() {
        return isEvaluationFlag;
    }

    public void setIsEvaluationFlag(Integer isEvaluationFlag) {
        this.isEvaluationFlag = isEvaluationFlag;
    }

    public Integer getIsCaFlag() {
        return isCaFlag;
    }

    public void setIsCaFlag(Integer isCaFlag) {
        this.isCaFlag = isCaFlag;
    }

    public Integer getIsSmtp() {
        return isSmtp;
    }

    public void setIsSmtp(Integer isSmtp) {
        this.isSmtp = isSmtp;
    }

    public String getRegIp() {
        return regIp;
    }

    public void setRegIp(String regIp) {
        this.regIp = regIp == null ? null : regIp.trim();
    }

    public Date getRegTime() {
        return regTime;
    }

    public void setRegTime(Date regTime) {
        this.regTime = regTime;
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
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", paypassword='" + paypassword + '\'' +
                ", referrer=" + referrer +
                ", referrerUserName='" + referrerUserName + '\'' +
                ", salt='" + salt + '\'' +
                ", status=" + status +
                ", openAccount=" + openAccount +
                ", borrowSms=" + borrowSms +
                ", rechargeSms=" + rechargeSms +
                ", withdrawSms=" + withdrawSms +
                ", ifReceiveNotice=" + ifReceiveNotice +
                ", iconurl='" + iconurl + '\'' +
                ", version=" + version +
                ", investSms=" + investSms +
                ", recieveSms=" + recieveSms +
                ", regEsb=" + regEsb +
                ", eprovince='" + eprovince + '\'' +
                ", sendSms=" + sendSms +
                ", pid=" + pid +
                ", usernamep='" + usernamep + '\'' +
                ", isInstFlag=" + isInstFlag +
                ", instCode='" + instCode + '\'' +
                ", ptype=" + ptype +
                ", accountEsb=" + accountEsb +
                ", investflag=" + investflag +
                ", userType=" + userType +
                ", authType=" + authType +
                ", authStatus=" + authStatus +
                ", paymentAuthStatus=" + paymentAuthStatus +
                ", authTime=" + authTime +
                ", recodTotal=" + recodTotal +
                ", recodTime=" + recodTime +
                ", recodTruncateTime=" + recodTruncateTime +
                ", isSetPassword=" + isSetPassword +
                ", bankOpenAccount=" + bankOpenAccount +
                ", bankAccountEsb=" + bankAccountEsb +
                ", isDataUpdate=" + isDataUpdate +
                ", isEvaluationFlag=" + isEvaluationFlag +
                ", isCaFlag=" + isCaFlag +
                ", isSmtp=" + isSmtp +
                ", regIp='" + regIp + '\'' +
                ", regTime=" + regTime +
                ", createUserId=" + createUserId +
                ", updateUserId=" + updateUserId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}