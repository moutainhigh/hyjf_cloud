package com.hyjf.cs.trade.bean.app;

import java.io.Serializable;

/**
 * @author wangjun
 * @version BaseResultBeanFrontEnd, v0.1 2018/8/16 14:53
 */
public class BaseResultBeanFrontEnd implements Serializable{

	/**
     * 此处为属性说明
     */
    private static final long serialVersionUID = -2899752944266497051L;
    public static final String SUCCESS = "000";
	public static final String SUCCESS_MSG = "成功";
	public static final String FAIL = "99";
	public static final String FAIL_MSG = "失败";

	private String status;
	private String statusDesc;

	public BaseResultBeanFrontEnd() {
		this.status = SUCCESS;
		this.statusDesc = SUCCESS_MSG;
	}

	public BaseResultBeanFrontEnd(String status, String statusDesc) {
		this.status = status;
		this.statusDesc = statusDesc;
	}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }
}
