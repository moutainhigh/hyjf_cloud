package com.hyjf.admin.beans.vo;

import com.hyjf.am.vo.BaseVO;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户绑定的银行卡VO
 * @author hesy
 */
public class BankCardCustomizeVO extends BaseVO implements Serializable {
	private static final long serialVersionUID = 1L;
	@ApiModelProperty(value = "id")
	private Integer id;
	@ApiModelProperty(value = "用户userId")
	private Integer userId;
	@ApiModelProperty(value = "用户名")
	private String userName;
	@ApiModelProperty(value = "银行账号")
	private String cardNo;
	@ApiModelProperty(value = "银行ID")
	private Integer bankId;
	@ApiModelProperty(value = "银行绑定的手机号码")
	private String mobile;
	@ApiModelProperty(value = "开户行联行行号")
	private String payAllianceCode;
	@ApiModelProperty(value = "所属银行")
	private String bank;
	@ApiModelProperty(value = "银行卡是否有效 0无效 1有效")
	private Integer status;
	@ApiModelProperty(value = "创建用户id")
	private Integer createUserId;
	@ApiModelProperty(value = "更新用户名")
	private Integer updateUserId;
	@ApiModelProperty(value = "添加时间")
	private Date createTime;
	@ApiModelProperty(value = "更新时间")
	private Date updateTime;
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getCardNo() {
		return cardNo;
	}

	public void setCardNo(String cardNo) {
		this.cardNo = cardNo;
	}

	public Integer getBankId() {
		return bankId;
	}

	public void setBankId(Integer bankId) {
		this.bankId = bankId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getPayAllianceCode() {
		return payAllianceCode;
	}

	public void setPayAllianceCode(String payAllianceCode) {
		this.payAllianceCode = payAllianceCode;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
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
