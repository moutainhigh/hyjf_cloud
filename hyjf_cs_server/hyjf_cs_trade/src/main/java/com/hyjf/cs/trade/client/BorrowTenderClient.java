package com.hyjf.cs.trade.client;

import com.hyjf.am.resquest.trade.BorrowTenderRequest;
import com.hyjf.am.vo.trade.CreditTenderLogVO;
import com.hyjf.am.vo.trade.FddTempletVO;
import com.hyjf.am.vo.trade.TenderAgreementVO;
import com.hyjf.am.vo.trade.borrow.BorrowTenderVO;

import java.math.BigDecimal;
import java.util.List;

public interface BorrowTenderClient {

    public Integer  countUserInvest(Integer userId, String borrowNid);

	public BorrowTenderVO selectBorrowTender(BorrowTenderRequest btRequest);

	public List<FddTempletVO> getFddTempletList(Integer protocolType);

	public int saveTenderAgreement(TenderAgreementVO info);

	public int updateTenderAgreement(TenderAgreementVO tenderAgreement);

    List<BorrowTenderVO> getBorrowTenderListByNid(String nid);

	/**
	 * 根据投资订单号查询已承接金额
	 * @param tenderNid
	 * @return
	 */
	BigDecimal getAssignCapital(String tenderNid);

	/**
	 * 保存债转日志
	 * @param creditTenderLog
	 * @return
	 */
    Integer saveCreditTenderAssignLog(CreditTenderLogVO creditTenderLog);
}
