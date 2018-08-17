package com.hyjf.admin.service.impl;

import com.hyjf.admin.client.AmUserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyjf.admin.client.ChangeLogClient;
import com.hyjf.admin.service.ChangeLogService;
import com.hyjf.am.response.user.ChangeLogResponse;
import com.hyjf.am.resquest.user.ChangeLogRequest;

@Service
public class ChangeLogServiceImpl  implements ChangeLogService{
	@Autowired
	private AmUserClient amUserClient;
	@Override
	public ChangeLogResponse getChangeLogList(ChangeLogRequest clr) {
		return amUserClient.getChangeLogList(clr);
	}

}
