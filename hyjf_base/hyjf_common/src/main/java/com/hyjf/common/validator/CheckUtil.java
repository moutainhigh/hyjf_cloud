/**
 * Description:（类功能描述-必填） 需要在每个方法前添加业务描述，方便业务业务行为的BI工作
 * Copyright: Copyright (HYJF Corporation)2017
 * Company: HYJF Corporation
 * @author: lb
 * @version: 1.0
 * Created at: 2017年9月14日 上午8:51:53
 * Modification History:
 * Modified by : 
 */
	
package com.hyjf.common.validator;

import com.hyjf.common.constants.MsgCode;
import com.hyjf.common.util.StringUtil;

import com.hyjf.common.enums.utils.MsgEnum;
import com.hyjf.common.exception.CheckException;

/**
 * @author liubin
 */

public class CheckUtil {
	private static Object data = null;

	/**
	 * data
	 * @return the data
	 */
	public static Object getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public static void setData(Object data) {
		CheckUtil.data = data;
	}

	/**
	 * 验证结果为false时，抛出自定义Check异常(适用 信息码和信息在枚举中)
	 * @param condition	验证结果
	 * @param msgEnum   枚举信息
	 * @param params 	信息用参数集
	 */
	public static void check(boolean condition, MsgEnum msgEnum, Object... params) {
		if (!condition) {
			throwCheckErrMsg(msgEnum, params);
		}
	}

	/**
	 * 抛出自定义Check异常(适用 信息码和信息在枚举中)
	 * @param msgCode
	 * @param params
	 */
	private static void throwCheckErrMsg(MsgCode msgCode, Object... params) {
		if (data != null) {
			throw new CheckException(msgCode.getCode(), StringUtil.getEnumMessage(msgCode, params), data);
		}else {
			throw new CheckException(msgCode.getCode(), StringUtil.getEnumMessage(msgCode, params));
		}
	}
//	/**
//     * 验证结果为false时，抛出自定义Check异常(适用 信息码和信息在properties中)
//     * @param condition	验证结果
//     * @param errKey	信息key
//     * @param params    信息用参数集
//     */
//    public static void check(boolean condition, String errKey, Object... params) {
//    	if (!condition) {
//    		throwCheckErrMsg(errKey, params);
//    	}
//    }

//    /**
//     * 抛出自定义Check异常(适用 信息码和信息在properties中)
//     * @param errKey
//     * @param params
//     */
//    private static void throwCheckErrMsg(String errKey, Object... params) {
//    	if (data != null) {
//    		throw new CheckException(errKey, getErrorMessage(errKey, params), data);
//    	}else {
//    		throw new CheckException(errKey, getErrorMessage(errKey, params));
//    	}
//    }
}

	