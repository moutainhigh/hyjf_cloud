package com.hyjf.callcenter.service;

import com.hyjf.am.vo.callcenter.CallCenterBorrowCreditVO;

import java.util.List;

/**
 * Description:按照用户名/手机号查询承接债权信息
 * Copyright: Copyright (HYJF Corporation)2015
 * Company: HYJF Corporation
 * @author: LIBIN
 * @version: 1.0
 *           Created at: 2017年7月15日 下午1:50:02
 *           Modification History:
 *           Modified by :
 */
public interface SrchUndertakeInfoService {
	/**
	 * 获取详细列表
	 * 
	 * @param borrowCreditCustomize
	 * @return
	 */
	public List<CallCenterBorrowCreditVO> selectBorrowCreditTenderList(CallCenterBorrowCreditVO callCenterBorrowCreditVO);
}
