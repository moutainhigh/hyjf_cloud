/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.beans.request;

import com.hyjf.admin.beans.BaseRequest;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author fuqiang
 * @version ContentPartnerRequestBean, v0.1 2018/7/12 9:55
 */
public class ContentPartnerRequestBean extends BaseRequest {
	private Integer id;
	@ApiModelProperty(value = "连接类型 1:友情连接 2:合作伙伴")
	private Integer type;
	@ApiModelProperty(value = "状态 0:关闭 1:开启")
	private Integer status;
    @ApiModelProperty(value = "排序")
	private Integer order;
	@ApiModelProperty(value = "官网")
	private String url;
	@ApiModelProperty(value = "名称")
	private String webname;
	@ApiModelProperty(value = "内容")
	private String summary;

	private String summary2;

	private String controlMeasures;

	private String operatingProcess;
	@ApiModelProperty(value = "logo地址")
	private String logo;

	private String province = "";

	private String city = "";

	private String area = "";

	private String phone = "";

	private String address = "";

	private String setupTime = "";

	private String cooperationTime = "";
	@ApiModelProperty(value = "logo1地址")
	private String logo1;

	private String approvalBy;

	private Integer registerCapital;
	@ApiModelProperty(value = "伙伴类别")
	private Integer partnerType;

	private Integer hits;

	private Integer isindex;

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

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url == null ? null : url.trim();
	}

	public String getWebname() {
		return webname;
	}

	public void setWebname(String webname) {
		this.webname = webname == null ? null : webname.trim();
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary == null ? null : summary.trim();
	}

	public String getSummary2() {
		return summary2;
	}

	public void setSummary2(String summary2) {
		this.summary2 = summary2 == null ? null : summary2.trim();
	}

	public String getControlMeasures() {
		return controlMeasures;
	}

	public void setControlMeasures(String controlMeasures) {
		this.controlMeasures = controlMeasures == null ? null : controlMeasures.trim();
	}

	public String getOperatingProcess() {
		return operatingProcess;
	}

	public void setOperatingProcess(String operatingProcess) {
		this.operatingProcess = operatingProcess == null ? null : operatingProcess.trim();
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo == null ? null : logo.trim();
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province == null ? null : province.trim();
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city == null ? null : city.trim();
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area == null ? null : area.trim();
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone == null ? null : phone.trim();
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address == null ? null : address.trim();
	}

	public String getSetupTime() {
		return setupTime;
	}

	public void setSetupTime(String setupTime) {
		this.setupTime = setupTime == null ? null : setupTime.trim();
	}

	public String getCooperationTime() {
		return cooperationTime;
	}

	public void setCooperationTime(String cooperationTime) {
		this.cooperationTime = cooperationTime == null ? null : cooperationTime.trim();
	}

	public String getLogo1() {
		return logo1;
	}

	public void setLogo1(String logo1) {
		this.logo1 = logo1 == null ? null : logo1.trim();
	}

	public String getApprovalBy() {
		return approvalBy;
	}

	public void setApprovalBy(String approvalBy) {
		this.approvalBy = approvalBy == null ? null : approvalBy.trim();
	}

	public Integer getRegisterCapital() {
		return registerCapital;
	}

	public void setRegisterCapital(Integer registerCapital) {
		this.registerCapital = registerCapital;
	}

	public Integer getPartnerType() {
		return partnerType;
	}

	public void setPartnerType(Integer partnerType) {
		this.partnerType = partnerType;
	}

	public Integer getHits() {
		return hits;
	}

	public void setHits(Integer hits) {
		this.hits = hits;
	}

	public Integer getIsindex() {
		return isindex;
	}

	public void setIsindex(Integer isindex) {
		this.isindex = isindex;
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

	/** 开始时间 */
	@ApiModelProperty(value = "开始时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date startTime;
	/** 结束时间 */
	@ApiModelProperty(value = "结束时间")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private Date endTime;

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

}
