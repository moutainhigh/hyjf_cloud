package com.hyjf.cs.user.constants;

import com.hyjf.common.constants.MsgCode;

/**
 * @author sunss
 * @version RegisterError, v0.1
 */
public enum OpenAccountError implements MsgCode {
	SUCCESS("0", ""),
	USER_NOT_LOGIN_ERROR("1", "用户未登录"),
	ERROR("1", "开户失败"),
	SYSTEM_ERROR("1","系统异常"),
	PARAM_ERROR("1", ""),
	GET_USER_INFO_ERROR("1", "获取用户信息失败"),
	MOBILE_NULL_ERROR("1", "手机号不能为空"),
	TRUENAME_NULL_ERROR("1", "真实姓名不能为空"),

	TRUENAME_BLANKL_ERROR("1", "真实姓名不能包含空格"),
	TRUENAME_LENGTH_ERROR("1", "真实姓名不能超过十位"),
	IDNO_NULL_ERROR("1", "身份证不能为空"),
	IDNO_FORMAT_ERROR("1", "身份证号格式错误"),
	IDNO_USED_ERROR("1", "身份证号已存在"),
	MOBILE_FORMAT_ERROR("1", "手机号格式错误"),
	MOBILE_USED_ERROR("1", "手机号码重复"),
	MOBILE_ERROR("1", "手机号码输入错误"),
	OPEN_ACCOUNTED_ERROR("1", "已开户")

	;

	private String code;
	private String msg;

	OpenAccountError(String code, String msg) {
		this.code = code;
		this.msg = msg;
	}

	@Override
	public String getCode() {
		return this.code;
	}

	@Override
	public String getMsg() {
		return this.msg;
	}
}
