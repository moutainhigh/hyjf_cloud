/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.service.admin.membercentre;

import com.hyjf.am.user.dao.model.customize.*;
import com.hyjf.am.user.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author nxl
 * @version UserPayAuthService, v0.1 2018/6/20 9:47
 *          后台管理系统：会员中心->会员管理
 */
public interface UserPayAuthService extends BaseService {

    /**
     * 根据筛选条件查找会员列表
     * @param mapParam
     * @param limitStart
     * @param limitEnd
     * @return
     */
    List<AdminUserPayAuthCustomize> selectUserPayAuthList(Map<String, Object> mapParam, int limitStart, int limitEnd);

    /**
     * 根据条件查询用户管理总数
     *
     * @return
     */
    int countRecordTotalPay(Map<String, Object> mapParam);}
