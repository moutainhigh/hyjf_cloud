/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.admin;

import java.util.List;

import com.hyjf.am.resquest.admin.ProtocolsRequest;
import com.hyjf.am.trade.dao.model.customize.FddTempletCustomize;

/**
 * @author fuqiang
 * @version ProtocolsService, v0.1 2018/7/11 9:05
 */
public interface ProtocolsService {
	/**
	 * 获取协议列表
	 *
	 * @param request
	 * @return
	 */
	List<FddTempletCustomize> selectFddTempletList(ProtocolsRequest request);

	/**
	 * 添加协议列表
	 *
	 * @param request
	 */
	void insertAction(ProtocolsRequest request);

	/**
	 * 插入协议列表
	 *
	 * @param request
	 */
	void updateAction(ProtocolsRequest request);
}