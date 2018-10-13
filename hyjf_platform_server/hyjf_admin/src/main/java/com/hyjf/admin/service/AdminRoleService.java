/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service;

import com.alibaba.fastjson.JSONArray;
import com.hyjf.am.response.admin.AdminRoleResponse;
import com.hyjf.am.resquest.admin.UserRoleRequest;
import com.hyjf.am.resquest.config.AdminRoleRequest;

/**
 * @author DongZeShan
 * @version AdminRoleService.java, v0.1 2018年10月11日 下午3:51:34
 */
public interface AdminRoleService {

	AdminRoleResponse search(AdminRoleRequest form);

	AdminRoleResponse moveToInfoAction(AdminRoleRequest form);

	AdminRoleResponse insertRecord(AdminRoleRequest form);

	AdminRoleResponse updateRecord(AdminRoleRequest form);

	AdminRoleResponse deleteRecord(AdminRoleRequest form);

	JSONArray getAdminRoleMenu(String roleId);

	AdminRoleResponse checkAction(AdminRoleRequest bean);

	AdminRoleResponse modifyPermissionAction(UserRoleRequest bean);

}
