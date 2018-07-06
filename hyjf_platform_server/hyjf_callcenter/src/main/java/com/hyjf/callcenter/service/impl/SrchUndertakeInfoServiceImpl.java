package com.hyjf.callcenter.service.impl;

import com.hyjf.am.resquest.callcenter.SrchTransferInfoRequest;
import com.hyjf.am.vo.callcenter.CallCenterBorrowCreditVO;
import com.hyjf.callcenter.client.AmTradeClient;
import com.hyjf.callcenter.service.SrchUndertakeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * @author libin
 * @version SrchTransferInfoServiceImpl, v0.1 2018/6/5
 */
@Service
public class SrchUndertakeInfoServiceImpl implements SrchUndertakeInfoService {
    @Autowired
    private AmTradeClient amTradeClient;
	@Override
	public List<CallCenterBorrowCreditVO> selectBorrowCreditTenderList(
			CallCenterBorrowCreditVO callCenterBorrowCreditVO) {
		
		SrchTransferInfoRequest SrchTransferInfoRequest = new SrchTransferInfoRequest();
		SrchTransferInfoRequest.setLimitStart(callCenterBorrowCreditVO.getLimitStart());
		SrchTransferInfoRequest.setLimitSize(callCenterBorrowCreditVO.getLimitEnd());
		SrchTransferInfoRequest.setUsernameSrch(callCenterBorrowCreditVO.getUsernameSrch());
		List<CallCenterBorrowCreditVO> list = amTradeClient.selectBorrowCreditTenderList(SrchTransferInfoRequest);
		return list;
	}
}
