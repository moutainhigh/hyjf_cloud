package com.hyjf.am.vo.admin;

import com.hyjf.am.vo.BaseVO;

import java.io.Serializable;

/**
 * @author xiehuili on 2019/4/12.
 */
public class WorkFlowNodeVO  extends BaseVO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 业务id
     */
    private String businessId;
    /**
     * 流程顺序（第几审核人）
     */
    private String flowNodeNum;
    /**
     * 审核人
     */
    private Integer auditUser;
    /**
     * 审核人真实姓名
     */
    private String auditUserName;

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getFlowNodeNum() {
        return flowNodeNum;
    }

    public void setFlowNodeNum(String flowNodeNum) {
        this.flowNodeNum = flowNodeNum;
    }

    public Integer getAuditUser() {
        return auditUser;
    }

    public void setAuditUser(Integer auditUser) {
        this.auditUser = auditUser;
    }

    public String getAuditUserName() {
        return auditUserName;
    }

    public void setAuditUserName(String auditUserName) {
        this.auditUserName = auditUserName;
    }
}
