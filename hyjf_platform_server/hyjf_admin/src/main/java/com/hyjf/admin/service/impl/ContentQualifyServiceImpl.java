/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service.impl;

import com.hyjf.admin.beans.request.ContentQualifyRequestBean;
import com.hyjf.admin.client.AmConfigClient;
import com.hyjf.admin.service.ContentQualifyService;
import com.hyjf.am.response.admin.ContentQualifyResponse;
import com.hyjf.am.vo.config.ContentQualifyVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fuqiang
 * @version ContentQualifyServiceImpl, v0.1 2018/7/11 9:34
 */
@Service
public class ContentQualifyServiceImpl implements ContentQualifyService {
	@Autowired
	private AmConfigClient amConfigClient;

	@Override
	public ContentQualifyResponse searchAction(ContentQualifyRequestBean requestBean) {
		return amConfigClient.searchAction(requestBean);
	}

	@Override
	public int insertAction(ContentQualifyRequestBean requestBean) {
		return amConfigClient.insertAction(requestBean);
	}

	@Override
	public int updateAction(ContentQualifyRequestBean requestBean) {
		return amConfigClient.updateAction(requestBean);
	}

	@Override
	public int updateStatus(ContentQualifyRequestBean requestBean) {
		if (requestBean != null && requestBean.getId() != null) {
			Integer id = requestBean.getId();
			ContentQualifyVO record = amConfigClient.getContentQualifyRecord(id);
			ContentQualifyRequestBean bean = new ContentQualifyRequestBean();
			BeanUtils.copyProperties(record, bean);
			bean.setStatus(requestBean.getStatus());
			return amConfigClient.updateAction(bean);
		}
		return 0;
	}

	@Override
	public int deleteById(Integer id) {
		return amConfigClient.deleteContentQualifyById(id);
	}

	@Override
	public ContentQualifyVO selectById(ContentQualifyRequestBean requestBean) {
		return amConfigClient.getContentQualifyRecord(requestBean.getId());
	}
}
