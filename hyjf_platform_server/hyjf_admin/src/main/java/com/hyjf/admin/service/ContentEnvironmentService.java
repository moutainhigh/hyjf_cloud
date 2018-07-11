/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service;

import com.hyjf.admin.beans.request.ContentEnvironmentRequestBean;
import com.hyjf.am.response.admin.ContentEnvironmentResponse;

/**
 * @author fuqiang
 * @version ContentEnvironmentService, v0.1 2018/7/11 11:15
 */
public interface ContentEnvironmentService {
	/**
	 * 根据条件查询内容管理-办公环境
	 *
	 * @param requestBean
	 * @return
	 */
	ContentEnvironmentResponse searchAction(ContentEnvironmentRequestBean requestBean);

	/**
	 * 添加内容管理-办公环境
	 *
	 * @param requestBean
	 * @return
	 */
	ContentEnvironmentResponse insertAction(ContentEnvironmentRequestBean requestBean);

	/**
	 * 修改内容管理-办公环境
	 *
	 * @param requestBean
	 * @return
	 */
	ContentEnvironmentResponse updateAction(ContentEnvironmentRequestBean requestBean);

	/**
	 * 修改内容管理-办公环境
	 *
	 * @param requestBean
	 * @return
	 */
	ContentEnvironmentResponse updateStatus(ContentEnvironmentRequestBean requestBean);
}