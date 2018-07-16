/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service.impl;

import com.hyjf.admin.beans.request.ContentJobRequestBean;
import com.hyjf.admin.beans.request.ContentLandingPageRequestBean;
import com.hyjf.admin.client.ContentJobClient;
import com.hyjf.admin.client.ContentLandingPageClient;
import com.hyjf.admin.service.ContentJobService;
import com.hyjf.admin.service.ContentLandingPageService;
import com.hyjf.am.response.config.JobResponse;
import com.hyjf.am.response.config.LandingPageResponse;
import com.hyjf.am.vo.config.JobsVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author tanyy
 * @version ContentLandingPageServiceImpl, v0.1 2018/7/16 14:14
 */
@Service
public class ContentLandingPageServiceImpl implements ContentLandingPageService {
	@Autowired
	private ContentLandingPageClient contentLandingPageClient;

	@Override
	public LandingPageResponse searchAction(ContentLandingPageRequestBean requestBean) {
		return contentLandingPageClient.searchAction(requestBean);
	}

	@Override
	public LandingPageResponse insertAction(ContentLandingPageRequestBean requestBean) {
		return contentLandingPageClient.insertAction(requestBean);
	}

	@Override
	public LandingPageResponse updateAction(ContentLandingPageRequestBean requestBean) {
		return contentLandingPageClient.updateAction(requestBean);
	}


	@Override
	public LandingPageResponse deleteById(Integer id) {
		return contentLandingPageClient.deleteById(id);
	}
}
