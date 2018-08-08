/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.admin.finance;

import java.util.List;

import com.hyjf.am.resquest.admin.HjhCommissionRequest;
import com.hyjf.am.vo.trade.hjh.HjhCommissionCustomizeVO;

/**
 * @author libin
 * @version AdminHjhCommissionService.java, v0.1 2018年8月7日 下午4:44:24
 */
public interface AdminHjhCommissionService {
	
    /**
     * 按照筛选条件查询数据条数
     * @param request 筛选条件
     * @return
     */
	Integer countTotal(HjhCommissionRequest request);
	
    /**
     * 根据筛选条件查询list
     * @param request 筛选条件
     * @return
     */
	List<HjhCommissionCustomizeVO> selectHjhCommissionList(HjhCommissionRequest request,int limitStart,int limitEnd);
}
