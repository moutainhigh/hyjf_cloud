/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service.impl;

import com.hyjf.admin.client.AmConfigClient;
import com.hyjf.admin.service.MsgPushTemplateService;
import com.hyjf.am.response.config.MessagePushTemplateResponse;
import com.hyjf.am.resquest.config.MsgPushTemplateRequest;
import com.hyjf.am.vo.config.MessagePushTagVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author fuqiang
 * @version MsgPushTemplateServiceImpl, v0.1 2018/6/26 9:36
 */
@Service
public class MsgPushTemplateServiceImpl implements MsgPushTemplateService {

	@Autowired
	private AmConfigClient amConfigClient;

	@Override
	public MessagePushTemplateResponse findAll() {
		return amConfigClient.findAll();
	}

	@Override
	public MessagePushTemplateResponse findMsgPushTemplate(MsgPushTemplateRequest request) {
	    if (request != null) {
            String tagName = request.getTagName();
			MessagePushTagVO tag = amConfigClient.findMsgTagByTagName(tagName);
			if (tag != null && tag.getTagName() != null) {
				request.setTagCode(tag.getTagName());
			}
        }
		return amConfigClient.findMsgPushTemplate(request);
	}

	@Override
	public void insertMsgPushTemplate(MsgPushTemplateRequest request) {
		amConfigClient.insertMsgPushTemplate(request);
	}
}
