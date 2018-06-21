package com.hyjf.am.vo.user;

import com.hyjf.am.vo.BaseVO;

import java.io.Serializable;
import java.util.Date;

/**
 * @author xiasq
 * @version UserVO, v0.1 2018/1/21 22:39
 */
public class UserVO extends BaseVO implements Serializable {


	private Integer userId;

	private String username;

	private String mobile;

	private String email;

	private String password;

	private String salt;

	private Integer status;

	private Integer openAccount;

	private Integer rechargeSms;

	private Integer withdrawSms;

	private Boolean ifReceiveNotice;

	private Integer investSms;

	private Integer recieveSms;

	private Integer regEsb;

	private String eprovince;

	private Integer sendSms;

	private Integer isInstFlag;

	private String instCode;

	private Integer accountEsb;

	private Integer investflag;

	private Integer userType;

	private Boolean paymentAuthStatus;

	private Integer isSetPassword;

	private Integer bankOpenAccount;

	private Integer bankAccountEsb;

	private Integer isEvaluationFlag;

	private Integer isCaFlag;

	private Integer isSmtp;

	private String regIp;

	private Date regTime;

	private Date createTime;

	private Date updateTime;

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
		this.username = username;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
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
		this.eprovince = eprovince;
	}

	public Integer getSendSms() {
		return sendSms;
	}

	public void setSendSms(Integer sendSms) {
		this.sendSms = sendSms;
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
		this.instCode = instCode;
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

	public Boolean getPaymentAuthStatus() {
		return paymentAuthStatus;
	}

	public void setPaymentAuthStatus(Boolean paymentAuthStatus) {
		this.paymentAuthStatus = paymentAuthStatus;
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
		this.regIp = regIp;
	}

	public Date getRegTime() {
		return regTime;
	}

	public void setRegTime(Date regTime) {
		this.regTime = regTime;
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
