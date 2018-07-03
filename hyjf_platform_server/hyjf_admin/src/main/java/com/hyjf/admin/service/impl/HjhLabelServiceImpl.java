/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyjf.admin.client.HjhLabelClient;
import com.hyjf.admin.service.HjhLabelService;
import com.hyjf.am.resquest.admin.HjhLabelInfoRequest;
import com.hyjf.am.resquest.admin.HjhLabelRequest;
import com.hyjf.am.vo.admin.HjhLabelCustomizeVO;
import com.hyjf.am.vo.trade.borrow.BorrowProjectTypeVO;
import com.hyjf.am.vo.trade.borrow.BorrowStyleVO;

/**
 * @author libin
 * @version HjhLabelServiceImpl.java, v0.1 2018年6月30日 上午9:40:27
 */
@Service
public class HjhLabelServiceImpl implements HjhLabelService{

	@Autowired
	public HjhLabelClient hjhLabelClient;
	
	@Override
	public List<BorrowProjectTypeVO> getBorrowProjectTypeList() {
		List<BorrowProjectTypeVO> borrowProjectTypeList = hjhLabelClient.findBorrowProjectTypeList();
		return borrowProjectTypeList;
	}

	@Override
	public List<BorrowStyleVO> getBorrowStyleList() {
		List<BorrowStyleVO> borrowStyleList = hjhLabelClient.findBorrowStyleList();
		return borrowStyleList;
	}

	@Override
	public List<HjhLabelCustomizeVO> getHjhLabelList(HjhLabelRequest request) {
		List<HjhLabelCustomizeVO> list = hjhLabelClient.findHjhLabelList(request);
		return list;
	}

	@Override
	public List<HjhLabelCustomizeVO> getHjhLabelListById(HjhLabelRequest request) {
		List<HjhLabelCustomizeVO> list = hjhLabelClient.findHjhLabelListById(request);
		return list;
	}

	@Override
	public List<HjhLabelCustomizeVO> getHjhLabelListByLabelName(HjhLabelRequest request) {
		List<HjhLabelCustomizeVO> list = hjhLabelClient.findHjhLabelListLabelName(request);
		return list;
	}

	@Override
	public void insertHjhLabelRecord(HjhLabelInfoRequest hjhLabelInfoRequest) {
		hjhLabelClient.insertHjhLabelRecord(hjhLabelInfoRequest);
	}

	@Override
	public int updateHjhLabelRecord(HjhLabelInfoRequest hjhLabelInfoRequest) {
		int flg = hjhLabelClient.updateHjhLabelRecord(hjhLabelInfoRequest);
		return flg;
	}

	@Override
	public int updateAllocationRecord(HjhLabelInfoRequest hjhLabelInfoRequest) {
		int flg = hjhLabelClient.updateAllocationRecord(hjhLabelInfoRequest);
		return flg;
	}
}