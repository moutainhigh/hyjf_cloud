/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service.impl;

import com.hyjf.admin.client.AmConfigClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyjf.admin.beans.request.EventRequestBean;
import com.hyjf.admin.client.EventClient;
import com.hyjf.admin.service.EventService;
import com.hyjf.am.response.config.EventResponse;
import com.hyjf.am.vo.config.EventVO;

/**
 * @author fuqiang
 * @version EventServiceImpl, v0.1 2018/7/11 17:36
 */
@Service
public class EventServiceImpl implements EventService {
	@Autowired
	private AmConfigClient amConfigClient;

	@Override
	public EventResponse searchAction(EventRequestBean requestBean) {
		return amConfigClient.searchAction(requestBean);
	}

	@Override
	public EventResponse insertAction(EventRequestBean requestBean) {
		return amConfigClient.insertAction(requestBean);
	}

	@Override
	public EventResponse updateAction(EventRequestBean requestBean) {
		return amConfigClient.updateAction(requestBean);
	}

	@Override
	public EventResponse updateStatus(EventRequestBean requestBean) {
		Integer id = requestBean.getId();
		EventVO record = amConfigClient.getEventRecord(id);
		if (record.getStatus() == 1) {
			record.setStatus(0);
		} else if (record.getStatus() == 0) {
			record.setStatus(1);
		}
		BeanUtils.copyProperties(record, requestBean);
		return amConfigClient.updateAction(requestBean);
	}

	@Override
	public EventResponse deleteById(Integer id) {
		return amConfigClient.deleteEventById(id);
	}
}
