/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.dao.mapper.customize.admin;

import java.util.List;
import java.util.Map;

import com.hyjf.am.vo.trade.hjh.HjhCreditTenderCustomizeVO;

/**
 * @author libin
 * @version AdminHjhCreditTenderCustomizeMapper.java, v0.1 2018年7月11日 下午3:47:55
 */
public interface AdminHjhCreditTenderCustomizeMapper {
	/**
	 * COUNT
	 * 
	 * @param DebtCreditCustomize
	 * @return
	 */
	int countDebtCreditTender(Map<String, Object> param); 
	
	/**
	 * 获取列表
	 * 
	 * @param DebtCreditCustomize
	 * @return
	 */
	List<HjhCreditTenderCustomizeVO> selectDebtCreditTenderList(Map<String, Object> param);

}