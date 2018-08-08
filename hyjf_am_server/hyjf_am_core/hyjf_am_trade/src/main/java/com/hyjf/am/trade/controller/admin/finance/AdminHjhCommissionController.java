/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.controller.admin.finance;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.HjhCommissionResponse;
import com.hyjf.am.resquest.admin.HjhCommissionRequest;
import com.hyjf.am.trade.service.admin.finance.AdminHjhCommissionService;
import com.hyjf.am.vo.trade.hjh.HjhCommissionCustomizeVO;

import io.swagger.annotations.Api;
import com.hyjf.common.paginator.Paginator;
/**
 * @author libin
 * @version AdminHjhCommissionController.java, v0.1 2018年8月7日 下午4:42:18
 */
@Api(value = "汇计划提成列表")
@RestController
@RequestMapping("/am-trade/hjhCommission")
public class AdminHjhCommissionController {
	
	@Autowired
	AdminHjhCommissionService adminHjhCommissionService;
	
	/**
	 * @Author: libin
	 * @Desc :汇计划提成列表
	 */
	@RequestMapping(value = "/selectHjhCommissionList",method = RequestMethod.POST)
	public HjhCommissionResponse selectHjhCommissionList(@RequestBody @Valid HjhCommissionRequest request){
		HjhCommissionResponse response = new HjhCommissionResponse();
		Integer count = adminHjhCommissionService.countTotal(request);
		// 查询列表传入分页
		Paginator paginator;
		if(request.getLimit() == 0){
			// 前台传分页
			paginator = new Paginator(request.getCurrPage(), count);
		} else {
			// 前台未传分页那默认 10
			paginator = new Paginator(request.getCurrPage(), count,request.getPageSize());
		}
		List<HjhCommissionCustomizeVO> list = adminHjhCommissionService.selectHjhCommissionList(request,paginator.getOffset(), paginator.getLimit());
        if(count > 0){
            if (!CollectionUtils.isEmpty(list)) {
                response.setResultList(list);
                response.setCount(count);
                //代表成功
                response.setRtn(Response.SUCCESS);
            }
        }
        return response;
	}
	
    /**
     * 查询金额总计 
     * @param id
     * @return
     */
	@RequestMapping(value = "/selecthjhCommissionTotal",method = RequestMethod.POST)
	public HjhCommissionResponse selecthjhCommissionTotal(@RequestBody @Valid HjhCommissionRequest request){
		HjhCommissionResponse response = new HjhCommissionResponse();
		Integer count = adminHjhCommissionService.countTotal(request);
		// 查询列表传入分页
		Paginator paginator;
		if(request.getLimit() == 0){
			// 前台传分页
			paginator = new Paginator(request.getCurrPage(), count);
		} else {
			// 前台未传分页那默认 10
			paginator = new Paginator(request.getCurrPage(), count,request.getPageSize());
		}
		Map<String , String> totalMap = this.adminHjhCommissionService.queryPushMoneyTotle(request,paginator.getOffset(), paginator.getLimit());
		if(count > 0){
            if (totalMap != null) {
            	response.setTenderTotal(totalMap.get("tenderTotle"));
            	response.setCommissionTotal(totalMap.get("commissionTotle"));
                /*response.setCount(count);*/
                //代表成功
                response.setRtn(Response.SUCCESS);
            }
        }
        return response;
		
		
		
		
		
	}
	
}
