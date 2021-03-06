/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service.impl;

import com.hyjf.admin.client.AmTradeClient;
import com.hyjf.admin.service.AllocationEngineService;
import com.hyjf.am.response.admin.HjhAllocationEngineResponse;
import com.hyjf.am.response.admin.HjhRegionResponse;
import com.hyjf.am.resquest.admin.AllocationEngineRuquest;
import com.hyjf.am.vo.admin.HjhAllocationEngineVO;
import com.hyjf.am.vo.admin.HjhRegionVO;
import com.hyjf.am.vo.trade.hjh.HjhPlanVO;
import com.hyjf.common.util.GetDate;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author libin
 * @version AllocationEngineServiceImpl.java, v0.1 2018年7月3日 下午12:52:06
 */
@Service
public class AllocationEngineServiceImpl implements  AllocationEngineService{

	@Autowired
	public AmTradeClient amTradeClient;
	
	@Override
	public HjhRegionResponse getHjhRegionList(AllocationEngineRuquest form) {
		HjhRegionResponse list = amTradeClient.getHjhRegionList(form);
		if(CollectionUtils.isNotEmpty(list.getResultList())){
			for(HjhRegionVO vo :list.getResultList()){
				if(vo.getConfigAddTime() != null){
					vo.setAddConfigTime(GetDate.timestamptoNUMStrYYYYMMDDHHMMSS(vo.getConfigAddTime()));
				}
			}
		}
		return list;
	}

	@Override
	public String getPlanNameByPlanNid(AllocationEngineRuquest form) {
		String planName = amTradeClient.getPlanNameByPlanNid(form);
		return planName;
	}

	@Override
	public int insertRecord(HjhRegionVO request) {
		int flg = amTradeClient.insertRecord(request);
		return flg;
	}

	@Override
	public HjhRegionResponse getPlanNidAjaxCheck(String planNid) {
		HjhRegionResponse response = amTradeClient.getPlanNidAjaxCheck(planNid);
		return response;
	}

	@Override
	public HjhRegionVO getHjhRegionVOById(String id) {
		HjhRegionVO vo = amTradeClient.getHjhRegionVOById(id);
		return vo;
	}

	@Override
	public int updateHjhRegionRecord(HjhRegionVO vo) {
		int flg = amTradeClient.updateHjhRegionRecord(vo);
		return flg;
	}

	@Override
	public HjhRegionResponse updateAllocationEngineRecord(HjhRegionVO vo) {
		HjhRegionResponse response = amTradeClient.updateAllocationEngineRecord(vo);
		return response;
	}

	@Override
	public List<HjhRegionVO> getHjhRegionListWithOutPage(AllocationEngineRuquest request) {
		List<HjhRegionVO> list = amTradeClient.getHjhRegionListWithOutPage(request);
		return list;
	}

	@Override
	public HjhAllocationEngineResponse getHjhAllocationEngineList(AllocationEngineRuquest form ) {
		HjhAllocationEngineResponse response = amTradeClient.getHjhAllocationEngineList(form);
		if(CollectionUtils.isNotEmpty(response.getResultList())){
			for(HjhAllocationEngineVO vo : response.getResultList()){
				if(vo.getAddTime() != null){
					vo.setAddTimeString(GetDate.timestamptoNUMStrYYYYMMDDHHMMSS(vo.getAddTime()));
				}
			}
		}
		return response;
	}

	@Override
	public List<HjhAllocationEngineVO> getAllocationList(AllocationEngineRuquest form) {
		List<HjhAllocationEngineVO> list = amTradeClient.getAllocationList(form);
		if(list == null){
			list = Collections.emptyList();
		}
		return list;
	}

	@Override
	public HjhAllocationEngineVO getPlanConfigRecord(Integer engineId) {
		HjhAllocationEngineVO vo = amTradeClient.getPlanConfigRecord(engineId);
		return vo;
	}

	@Override
	public int updateHjhAllocationEngineRecord(HjhAllocationEngineVO vo) {
		int flg = amTradeClient.updateHjhAllocationEngineRecord(vo);
		return flg;
	}

	@Override
	public HjhAllocationEngineVO getPlanConfigRecordByParam(AllocationEngineRuquest form) {
		HjhAllocationEngineVO vo = amTradeClient.getPlanConfigRecordByParam(form);
		return vo;
	}

	@Override
	public int checkRepeat(AllocationEngineRuquest form) {
		int flg = amTradeClient.checkRepeat(form);
		return flg;
	}

	@Override
	public String getPlanBorrowStyle(String planNid) {
		String borrowStyle = amTradeClient.getPlanBorrowStyle(planNid);
		return borrowStyle;
	}

	@Override
	public HjhRegionVO getHjhRegionRecordByPlanNid(String planNid) {
		HjhRegionVO vo = amTradeClient.getHjhRegionRecordByPlanNid(planNid);
		return vo;
	}

	@Override
	public int insertHjhAllocationEngineRecord(HjhAllocationEngineVO newForm) {
		int flg = amTradeClient.insertHjhAllocationEngineRecord(newForm);
		return flg;
	}

	@Override
	public List<HjhPlanVO> getHjhPlanByPlanNid(String planNid) {
		List<HjhPlanVO> list = amTradeClient.getHjhPlanByPlanNid(planNid);
		return list;
	}

	@Override
	public List<HjhRegionVO> getHjhRegioByPlanNid(String planNid) {
		List<HjhRegionVO> list = amTradeClient.getHjhRegioByPlanNid(planNid);
		return list;
	}

	@Override
	public HjhAllocationEngineVO getPlanConfigRecordByPlanNidLabelName(AllocationEngineRuquest form) {
		HjhAllocationEngineVO vo = amTradeClient.getPlanConfigRecordByPlanNidLabelName(form);
		return vo;
	}
}
