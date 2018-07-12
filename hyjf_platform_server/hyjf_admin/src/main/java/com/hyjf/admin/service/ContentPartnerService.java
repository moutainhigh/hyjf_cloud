/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service;

import com.hyjf.admin.beans.request.ContentPartnerRequestBean;
import com.hyjf.am.response.admin.ContentPartnerResponse;

/**
 * @author fuqiang
 * @version ContentPartnerService, v0.1 2018/7/12 9:54
 */
public interface ContentPartnerService {
	/**
	 * 根据条件查询公司管理-合作伙伴
	 *
	 * @param requestBean
	 * @return
	 */
	ContentPartnerResponse searchAction(ContentPartnerRequestBean requestBean);

	/**
	 * 添加公司管理-合作伙伴
	 *
	 * @param requestBean
	 * @return
	 */
	ContentPartnerResponse insertAction(ContentPartnerRequestBean requestBean);

	/**
	 * 修改公司管理-合作伙伴
	 *
	 * @param requestBean
	 * @return
	 */
	ContentPartnerResponse updateAction(ContentPartnerRequestBean requestBean);

	/**
	 * 修改公司管理-合作伙伴状态
	 *
	 * @param requestBean
	 * @return
	 */
	ContentPartnerResponse updateStatus(ContentPartnerRequestBean requestBean);
}
