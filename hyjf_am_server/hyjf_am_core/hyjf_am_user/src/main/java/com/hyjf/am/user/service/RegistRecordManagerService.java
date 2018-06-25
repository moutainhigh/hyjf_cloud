/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.service;

import com.hyjf.am.resquest.user.RegistRcordRequest;
import com.hyjf.am.user.dao.model.customize.RegistRecordCustomize;

import java.util.List;

/**
 * @author nxl
 * @version UserManagerService, v0.1 2018/6/20 9:47
 *          后台管理系统：会员中心->注册记录
 */
public interface RegistRecordManagerService {

    /**
     * 根据筛选条件查找会员列表
     *
     * @param userRequest
     * @return
     */
    List<RegistRecordCustomize> selectRegistList(RegistRcordRequest userRequest);

    /**
     * 根据条件查询用户管理总数
     *
     * @return
     */
    int countRecordTotal(RegistRcordRequest userRequest);

}
