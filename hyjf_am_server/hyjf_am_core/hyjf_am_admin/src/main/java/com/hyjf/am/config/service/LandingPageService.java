/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.config.service;

import com.hyjf.am.config.dao.model.auto.LandingPage;
import com.hyjf.am.response.IntegerResponse;
import com.hyjf.am.response.config.LandingPageResponse;
import com.hyjf.am.resquest.admin.LandingPageRequest;

/**
 * @author tanyy
 * @version LandingPageService, v0.1 2018/7/16 14:33
 */
public interface LandingPageService {
	/**
	 * 根据条件查询着路页管理
	 *
	 * @param request
	 * @return
	 */
	LandingPageResponse searchAction(LandingPageRequest request);

	/**
	 * 添加着路页管理
	 *
	 * @param request
	 */
	void insertAction(LandingPageRequest request);

	/**
	 * 修改着路页管理
	 *
	 * @param request
	 */
	void updateAction(LandingPageRequest request);

	IntegerResponse countByPageName(LandingPageRequest requestBean);


	/**
	 * 根据id查询着路页管理
	 *
	 * @param id
	 * @return
	 */
	LandingPage getRecord(Integer id);

    /**
     * 删除着路页管理
     *
     * @param id
     */
    void deleteById(Integer id);
}
