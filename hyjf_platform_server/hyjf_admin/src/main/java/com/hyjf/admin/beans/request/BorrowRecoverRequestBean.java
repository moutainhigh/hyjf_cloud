package com.hyjf.admin.beans.request;

import com.hyjf.admin.beans.BaseRequest;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author pangchengchao
 * @version BorrowRecoverRequest, v0.1 2018/7/2 14:25
 */
public class BorrowRecoverRequestBean extends BaseRequest implements Serializable {

    /**
     * 项目编号 检索条件
     */
    @ApiModelProperty(value = "项目编号")
    private String borrowNidSrch;
    /**
     * 借款名称 检索条件
     */
    @ApiModelProperty(value = "借款名称")
    private String borrowNameSrch;
    /**
     * 资产来源 检索条件
     */
    @ApiModelProperty(value = "资产来源")
    private String instCodeSrch;
    /**
     * 检索条件 计划编号
     */
    @ApiModelProperty(value = "计划编号")
    private String planNidSrch;
    /**
     * 出借人
     */
    @ApiModelProperty(value = "出借人")
    private String usernameSrch;
    /**
     * 出借订单号 检索条件
     */
    @ApiModelProperty(value = "出借订单号")
    private String orderNumSrch;
    /**
     * 放款订单号 检索条件
     */
    @ApiModelProperty(value = "放款订单号")
    private String loanOrdid;
    /**
     * 放款状态 检索条件
     */
    @ApiModelProperty(value = "放款状态")
    private String isRecoverSrch;
    /**
     * 出借时间 检索条件
     */
    @ApiModelProperty(value = "出借时间开始")
    private String timeRecoverStartSrch;
    /**
     * 出借时间 检索条件
     */
    @ApiModelProperty(value = "出借时间结束")
    private String timeRecoverEndSrch;
    /**
     * 放款时间 检索条件
     */
    @ApiModelProperty(value = "放款时间开始")
    private String timeStartSrch;
    /**
     * 放款时间 检索条件
     */
    @ApiModelProperty(value = "放款时间结束")
    private String timeEndSrch;
    /**
     * 合作机构编号  检索条件
     */
    @ApiModelProperty(value = "合作机构编号")
    private String instCodeOrgSrch;
    /**
     * 放款批次号
     */
    @ApiModelProperty(value = "放款批次号")
    private String loanBatchNo;

    @ApiModelProperty(value = "是否具有组织架构查看权限")
    private String isOrganizationView;

    /**
     * borrowNidSrch
     *
     * @return the borrowNidSrch
     */

    public String getBorrowNidSrch() {
        return borrowNidSrch;
    }

    /**
     * @param borrowNidSrch
     *            the borrowNidSrch to set
     */

    public void setBorrowNidSrch(String borrowNidSrch) {
        this.borrowNidSrch = borrowNidSrch;
    }


    /**
     * usernameSrch
     *
     * @return the usernameSrch
     */

    public String getUsernameSrch() {
        return usernameSrch;
    }

    /**
     * @param usernameSrch
     *            the usernameSrch to set
     */

    public void setUsernameSrch(String usernameSrch) {
        this.usernameSrch = usernameSrch;
    }

    /**
     * orderNumSrch
     *
     * @return the orderNumSrch
     */

    public String getOrderNumSrch() {
        return orderNumSrch;
    }

    /**
     * @param orderNumSrch
     *            the orderNumSrch to set
     */

    public void setOrderNumSrch(String orderNumSrch) {
        this.orderNumSrch = orderNumSrch;
    }

    /**
     * isRecoverSrch
     *
     * @return the isRecoverSrch
     */

    public String getIsRecoverSrch() {
        return isRecoverSrch;
    }

    /**
     * @param isRecoverSrch
     *            the isRecoverSrch to set
     */

    public void setIsRecoverSrch(String isRecoverSrch) {
        this.isRecoverSrch = isRecoverSrch;
    }

    /**
     * timeRecoverStartSrch
     *
     * @return the timeRecoverStartSrch
     */

    public String getTimeRecoverStartSrch() {
        return timeRecoverStartSrch;
    }

    /**
     * @param timeRecoverStartSrch
     *            the timeRecoverStartSrch to set
     */

    public void setTimeRecoverStartSrch(String timeRecoverStartSrch) {
        this.timeRecoverStartSrch = timeRecoverStartSrch;
    }

    /**
     * timeRecoverEndSrch
     *
     * @return the timeRecoverEndSrch
     */

    public String getTimeRecoverEndSrch() {
        return timeRecoverEndSrch;
    }

    /**
     * @param timeRecoverEndSrch
     *            the timeRecoverEndSrch to set
     */

    public void setTimeRecoverEndSrch(String timeRecoverEndSrch) {
        this.timeRecoverEndSrch = timeRecoverEndSrch;
    }

    /**
     * timeStartSrch
     *
     * @return the timeStartSrch
     */

    public String getTimeStartSrch() {
        return timeStartSrch;
    }

    /**
     * @param timeStartSrch
     *            the timeStartSrch to set
     */

    public void setTimeStartSrch(String timeStartSrch) {
        this.timeStartSrch = timeStartSrch;
    }

    /**
     * timeEndSrch
     *
     * @return the timeEndSrch
     */

    public String getTimeEndSrch() {
        return timeEndSrch;
    }

    /**
     * @param timeEndSrch
     *            the timeEndSrch to set
     */

    public void setTimeEndSrch(String timeEndSrch) {
        this.timeEndSrch = timeEndSrch;
    }


    public String getLoanOrdid() {
        return loanOrdid;
    }

    public void setLoanOrdid(String loanOrdid) {
        this.loanOrdid = loanOrdid;
    }

    public String getLoanBatchNo() {
        return loanBatchNo;
    }

    public void setLoanBatchNo(String loanBatchNo) {
        this.loanBatchNo = loanBatchNo;
    }

    public String getPlanNidSrch() {
        return planNidSrch;
    }

    public void setPlanNidSrch(String planNidSrch) {
        this.planNidSrch = planNidSrch;
    }

    /**
     * instCodeSrch
     * @return the instCodeSrch
     */

    public String getInstCodeSrch() {
        return instCodeSrch;

    }

    /**
     * @param instCodeSrch the instCodeSrch to set
     */

    public void setInstCodeSrch(String instCodeSrch) {
        this.instCodeSrch = instCodeSrch;

    }

    public String getInstCodeOrgSrch() {
        return instCodeOrgSrch;
    }

    public void setInstCodeOrgSrch(String instCodeOrgSrch) {
        this.instCodeOrgSrch = instCodeOrgSrch;
    }


    public String getBorrowNameSrch() {
        return borrowNameSrch;
    }

    public void setBorrowNameSrch(String borrowNameSrch) {
        this.borrowNameSrch = borrowNameSrch;
    }

    public String getIsOrganizationView() {
        return isOrganizationView;
    }

    public void setIsOrganizationView(String isOrganizationView) {
        this.isOrganizationView = isOrganizationView;
    }
}
