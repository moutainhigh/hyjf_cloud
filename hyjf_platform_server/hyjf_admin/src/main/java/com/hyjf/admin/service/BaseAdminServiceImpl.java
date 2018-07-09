package com.hyjf.admin.service;

import com.hyjf.admin.client.AmUserClient;
import com.hyjf.admin.config.SystemConfig;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.cs.common.service.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class BaseAdminServiceImpl extends BaseServiceImpl implements BaseAdminService {

	Logger logger = LoggerFactory.getLogger(BaseAdminServiceImpl.class);

	@Autowired
	public AmUserClient amUserClient;

	@Autowired
	public SystemConfig systemConfig;

	@Override
	public UserVO getUserByUserName(String userName) {
		UserVO user = amUserClient.getUserByUserName(userName);
		return user;
	}
}
