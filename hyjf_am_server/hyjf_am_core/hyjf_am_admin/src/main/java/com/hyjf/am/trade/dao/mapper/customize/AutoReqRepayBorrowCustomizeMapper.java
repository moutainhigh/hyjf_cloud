package com.hyjf.am.trade.dao.mapper.customize;


import java.util.List;

import com.hyjf.am.trade.dao.model.customize.AutoReqRepayBorrowCustomize;
import com.hyjf.am.trade.dao.model.customize.BorrowInvestCustomize;

public interface AutoReqRepayBorrowCustomizeMapper {

	/**
	 * 当然应还款列表
	 * 
	 * @param borrowInvestCustomize
	 * @return
	 */
	List<BorrowInvestCustomize> selectBorrowInvestList(BorrowInvestCustomize borrowInvestCustomize);

	/**
	 * 投资明细记录 总数COUNT
	 * 
	 * @param borrowInvestCustomize
	 * @return
	 */
	Long countBorrowInvest(BorrowInvestCustomize borrowInvestCustomize);

	/**
	 * 导出投资明细列表
	 * 
	 * @param borrowInvestCustomize
	 * @return
	 */
	List<BorrowInvestCustomize> exportBorrowInvestList(BorrowInvestCustomize borrowInvestCustomize);

	/**
	 * 投资金额合计
	 * 
	 * @param borrowInvestCustomize
	 * @return
	 */
	String sumBorrowInvestAccount(BorrowInvestCustomize borrowInvestCustomize);

	/**
	 * 取得本日应还款标的列表
	 * @return
	 */
	List<AutoReqRepayBorrowCustomize> getAutoReqRepayBorrow();

}