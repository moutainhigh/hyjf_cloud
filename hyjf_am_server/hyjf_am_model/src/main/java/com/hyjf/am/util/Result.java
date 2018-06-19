package com.hyjf.am.util;

/**
 * @author xiasq
 * @version ApiResult, v0.1 2018/4/25 19:47
 */
public class Result<T> {

	public static final String STATUS_SUCCESS = "0";
	public static final String STATUS_FAIL = "1";
	public static final String STATUS_SUCCESS_DESC = "成功";
	public static final String STATUS_FAIL_DESC = "失败";

	public Result() {
		this.status = STATUS_SUCCESS;
		this.statusDesc = STATUS_SUCCESS_DESC;
		this.loginFlag = STATUS_SUCCESS;
	}

	public Result(String status, String statusDesc,String loginFlag) {
		this.status = status;
		this.statusDesc = statusDesc;
		this.loginFlag = loginFlag;
	}

	private T result;

	private String status;

	private String statusDesc;

	private String loginFlag;

	private Page page;

	public String getLoginFlag() {
		return loginFlag;
	}

	public void setLoginFlag(String loginFlag) {
		this.loginFlag = loginFlag;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
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

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}