/**
 * Description:（类功能描述-必填） 需要在每个方法前添加业务描述，方便业务业务行为的BI工作
 * Copyright: Copyright (HYJF Corporation)2017
 * Company: HYJF Corporation
 * @author: liubin
 * @version: 1.0
 * Created at: 2017年9月11日 下午4:48:51
 * Modification History:
 * Modified by : 
 */
	
package com.hyjf.cs.common.bean.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.hyjf.common.util.ApiSignUtil;

import java.io.Serializable;

/**
 * API结果返回Bean
 * 成功返回“000”，返回值加签chkValue
 * @author liubin
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResult<T> extends BaseResult<T> implements Serializable {
	private static final long serialVersionUID = 5413541226545232L;
	public static final String SUCCESS = "0";
	
	private String chkValue = null; // response时的签名，可选

	{
		// 成功码变更 "0" → "000"
		if (super.getStatus() == BaseResult.SUCCESS) {
			super.setStatus(ApiResult.SUCCESS);
		}
		// 返回值加签
		this.chkValue = ApiSignUtil.encryptByRSA(super.getStatus());
	}

	public ApiResult() {
		super();
	}

	public ApiResult(T data) {
		super(data);
	}

	public ApiResult(Throwable e) {
		super(e);
	}

	public ApiResult(String status, String statusDesc) {
		super(status,statusDesc);
	}

	/**
	 * @param status
	 * @param StatusDesc
	 */
	@Override
	public void setStatusInfo(String status, String StatusDesc) {
		super.setStatus(status);
		super.setStatusDesc(StatusDesc);
		// 返回值加签
		this.setChkValue(status);
	}

	@Override
	public void setStatus(String status) {
		super.setStatus(status);
		// 返回值加签
		this.setChkValue(status);
	}

	public String getChkValue() {
		return chkValue;
	}
	
	public void setChkValue(String... encPramas) {
		//加签
		this.chkValue = ApiSignUtil.encryptByRSA(encPramas);
	}
}
