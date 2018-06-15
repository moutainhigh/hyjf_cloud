package com.hyjf.am.response;

import com.hyjf.am.util.Page;
import com.hyjf.am.vo.BaseVO;

import java.util.List;

/**
 * @author xiasq
 * @version Response, v0.1 2018/1/21 22:18
 */
public class Response<T extends BaseVO> {
	public static final String SUCCESS = "00";
	public static final String SUCCESS_MSG = "成功";
	public static final String FAIL = "99";
	public static final String FAIL_MSG = "失败";

	private String rtn;
	private String message;
	private T result;

	private List<T> resultList;

	/**
	 * 分页标志
	 */
	private Page page;

	public Response() {
		this.rtn = SUCCESS;
		this.message = SUCCESS_MSG;
	}

	public Response(String rtn, String message) {
		this.rtn = rtn;
		this.message = message;
	}

	public Response(String rtn, String message, T result) {
		this.rtn = rtn;
		this.message = message;
		this.result = result;
	}

	public String getRtn() {
		return rtn;
	}

	public void setRtn(String rtn) {
		this.rtn = rtn;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	public List<T> getResultList() {
		return resultList;
	}

	public void setResultList(List<T> resultList) {
		this.resultList = resultList;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
}
