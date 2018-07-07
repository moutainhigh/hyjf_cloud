/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.controller.admin.hjhplan;

import java.util.List;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.HjhAllocationEngineResponse;
import com.hyjf.am.response.admin.HjhPlanDetailResponse;
import com.hyjf.am.response.admin.HjhPlanResponse;
import com.hyjf.am.response.admin.HjhPlanSumResponse;
import com.hyjf.am.resquest.admin.AllocationEngineRuquest;
import com.hyjf.am.resquest.admin.PlanListRequest;
import com.hyjf.am.trade.service.admin.hjhplan.AdminHjhPlanService;
import com.hyjf.am.vo.admin.HjhAllocationEngineVO;
import com.hyjf.am.vo.admin.HjhRegionVO;
import com.hyjf.am.vo.trade.hjh.HjhPlanDetailVO;
import com.hyjf.am.vo.trade.hjh.HjhPlanSumVO;
import com.hyjf.am.vo.trade.hjh.HjhPlanVO;
import com.hyjf.common.paginator.Paginator;
import org.springframework.util.CollectionUtils;
import io.swagger.annotations.Api;

/**
 * @author libin
 * @version AdminHjhPlanController.java, v0.1 2018年7月6日 上午10:04:37
 */
@Api(value = "计划列表")
@RestController
@RequestMapping("/am-trade/planList")
public class AdminHjhPlanController {
	
	@Autowired
	AdminHjhPlanService adminHjhPlanService;
	
	/**
	 * @Author: libin
	 * @Desc :计划列表
	 */
	@RequestMapping(value = "/getHjhPlanListByParam",method = RequestMethod.POST)
	public HjhPlanResponse getHjhPlanListByParam(@RequestBody @Valid PlanListRequest request){
		HjhPlanResponse response = new HjhPlanResponse();
		Integer count = adminHjhPlanService.countHjhPlanListTotal(request);
		// 查询列表传入分页
		Paginator paginator;
		if(request.getLimit() == 0){
			// 前台传分页
			paginator = new Paginator(request.getCurrPage(), count);
		} else {
			// 前台未传分页那默认 10
			paginator = new Paginator(request.getCurrPage(), count,request.getPageSize());
		}
		List<HjhPlanVO> list = adminHjhPlanService.selectHjhPlanList(request,paginator.getOffset(), paginator.getLimit());
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
	 * @Author: libin
	 * @Desc :根据参数获取 HjhPlanSumVO
	 */
    @RequestMapping(value = "/getCalcSumByParam", method = RequestMethod.POST)
    public HjhPlanSumResponse getCalcSumByParam(@RequestBody @Valid PlanListRequest request) {
    	HjhPlanSumResponse response = new HjhPlanSumResponse();
    	HjhPlanSumVO vo  = adminHjhPlanService.getCalcSumByParam(request);
    	if(vo != null){
            response.setResult(vo);
            //代表成功
            response.setRtn(Response.SUCCESS);
    	}
    	return response;
    }
    
	/**
	 * @Author: libin
	 * @Desc :根据参数获取 HjhPlanDetailVO
	 */
    @RequestMapping(value = "/getHjhPlanDetailByPlanNid", method = RequestMethod.POST)
    public HjhPlanDetailResponse getHjhPlanDetailByPlanNid(@RequestBody @Valid PlanListRequest request) {
    	HjhPlanDetailResponse response = new HjhPlanDetailResponse();
    	List<HjhPlanDetailVO> list = adminHjhPlanService.selectHjhPlanDetailByPlanNid(request);
        if (!CollectionUtils.isEmpty(list)) {
            response.setResultList(list);
            //代表成功
            response.setRtn(Response.SUCCESS);
        }
    	return response;
    }
    
	/**
	 * @Author: libin
	 * @Desc :AJAX
	 */
	@RequestMapping(value = "/getPlanNameAjaxCheck",method = RequestMethod.POST)
	public HjhPlanResponse getPlanNameAjaxCheck(@RequestBody @Valid PlanListRequest request){
		HjhPlanResponse response = new HjhPlanResponse();
		String planName = request.getPlanNameSrch();
		if (StringUtils.isEmpty(planName)) {
			response.setMessage("未传入计划名称！");
			return response;
		}
		int debtPlanCount = this.adminHjhPlanService.isDebtPlanNameExist(planName);
		if (debtPlanCount != 0) {
			response.setMessage("计划名称不能重复！");
			return response;
		}
		return response;
	}
	
	
	/**
	 * @Author: libin
	 * @Desc :AJAX
	 */
	@RequestMapping(value = "/getPlanNidAjaxCheck",method = RequestMethod.POST)
	public HjhPlanResponse getPlanNidAjaxCheck(@RequestBody @Valid PlanListRequest request){
		HjhPlanResponse response = new HjhPlanResponse();
		String planNid = request.getPlanNidSrch();
		if (StringUtils.isEmpty(planNid)) {
			response.setMessage("未传入计划编号！");
			return response;
		}
		int debtPlanCount = this.adminHjhPlanService.isDebtPlanNidExist(planNid);
		if (debtPlanCount != 0) {
			response.setMessage("计划编号不能重复！");
			return response;
		}
		return response;
	}
	
	
	/**
	 * @Author: libin
	 * @Desc :修改计划状态
	 */
	@RequestMapping(value = "/updatePlanStatusByPlanNid",method = RequestMethod.POST)
	public HjhPlanResponse updatePlanStatusByPlanNid(@RequestBody @Valid PlanListRequest request){
		HjhPlanResponse response = new HjhPlanResponse();
		int flg = adminHjhPlanService.updatePlanStatusByPlanNid(request);
	  	if(flg > 0){
            //代表成功
            response.setMessage(Response.SUCCESS_MSG);
    	}
		return response;
	}
	
	/**
	 * @Author: libin
	 * @Desc :修改计划显示状态
	 */
	@RequestMapping(value = "/updatePlanDisplayByPlanNid",method = RequestMethod.POST)
	public HjhPlanResponse updatePlanDisplayByPlanNid(@RequestBody @Valid PlanListRequest request){
		HjhPlanResponse response = new HjhPlanResponse();
		int flg = adminHjhPlanService.updatePlanDisplayByPlanNid(request);
	  	if(flg > 0){
            //代表成功
            response.setMessage(Response.SUCCESS_MSG);
    	}
		return response;
	}
	
	/**
	 * @Author: libin
	 * @Desc :
	 */
    @RequestMapping(value = "/isExistsRecord/{planNid}", method = RequestMethod.POST)
    public boolean isExistsRecord(@PathVariable String planNid) {
    	boolean Flag = adminHjhPlanService.isExistsRecord(planNid);
    	return Flag;
    }
	
	/**
	 * @Author: libin
	 * @Desc :
	 */
    @RequestMapping(value = "/countByPlanName/{planName}", method = RequestMethod.POST)
    public Integer countByPlanName(@PathVariable String planName) {
    	int Flag = adminHjhPlanService.countByPlanName(planName);
    	return Flag;
    }
    
	/**
	 * @Author: libin
	 * @Desc :
	 */
    @RequestMapping(value = "/isLockPeriodExist/{lockPeriod}/{borrowStyle}/{isMonth}", method = RequestMethod.POST)
    public Integer isLockPeriodExist(@PathVariable String lockPeriod, @PathVariable String borrowStyle, @PathVariable String isMonth) {
    	int Flag = adminHjhPlanService.isLockPeriodExist(lockPeriod,borrowStyle,isMonth);
    	return Flag;
    }
    
	/**
	 * @throws Exception 
	 * @Author: libin
	 * @Desc :更新计划表
	 */
	@RequestMapping("/updateRecord")
	public int updateRecord(@RequestBody PlanListRequest form) throws Exception {
		int flg = adminHjhPlanService.updateRecord(form);
		return flg;
	}
    
	/**
	 * @throws Exception 
	 * @Author: libin
	 * @Desc :更新计划表
	 */
	@RequestMapping("/insertRecord")
	public int insertRecord(@RequestBody PlanListRequest form) throws Exception {
		int flg = adminHjhPlanService.insertRecord(form);
		return flg;
	}
    
}