/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.controller.admin.hjhplan;

import com.hyjf.am.response.IntegerResponse;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.AccedeListResponse;
import com.hyjf.am.response.admin.HjhAccedeSumResponse;
import com.hyjf.am.response.admin.UserHjhInvistDetailResponse;
import com.hyjf.am.response.trade.HjhAccedeResponse;
import com.hyjf.am.resquest.admin.AccedeListRequest;
import com.hyjf.am.trade.service.admin.hjhplan.AdminAccedeListService;
import com.hyjf.am.vo.trade.hjh.AccedeListCustomizeVO;
import com.hyjf.am.vo.trade.hjh.HjhAccedeSumVO;
import com.hyjf.am.vo.trade.hjh.UserHjhInvistDetailVO;
import com.hyjf.common.cache.CacheUtil;
import com.hyjf.common.paginator.Paginator;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author libin
 * @version AdminAccedeListController.java, v0.1 2018年7月7日 下午4:18:52
 */
@Api(value = "加入计划列表")
@RestController
@RequestMapping("/am-trade/accedeList")
public class AdminAccedeListController {
	
	@Autowired
	AdminAccedeListService adminAccedeListService;

	/**
	 * 总数
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getAccedeListByParamCount",method = RequestMethod.POST)
	public AccedeListResponse getAccedeListByParamCount(@RequestBody @Valid AccedeListRequest request){
		AccedeListResponse response = new AccedeListResponse();
		Integer count = adminAccedeListService.countAccedeListTotal(request);
		response.setCount(count);
		return response;
	}

	/**
	 * 分页查询数据
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getAccedeListByParamList",method = RequestMethod.POST)
	public AccedeListResponse getAccedeListByParamList(@RequestBody @Valid AccedeListRequest request){
		AccedeListResponse response = new AccedeListResponse();

		List<AccedeListCustomizeVO> list = adminAccedeListService.selectAccedeList(request,request.getLimitStart(),
				request.getLimitEnd());
		// 原param表查询拼装
		if(!CollectionUtils.isEmpty(list)){
			Map<String, String> map1 = CacheUtil.getParamNameMap("USER_PROPERTY");
			for(AccedeListCustomizeVO vo : list){
				vo.setRecommendAttr(map1.getOrDefault(vo.getRecommendAttr(),null));
				vo.setInviteUserAttributeName(map1.getOrDefault(vo.getInviteUserAttributeName(),null));
				vo.setAttribute(map1.getOrDefault(vo.getAttribute(),null));
			}
		}

		if (!CollectionUtils.isEmpty(list)) {
			response.setResultList(list);
			//代表成功
			response.setRtn(Response.SUCCESS);
		}else{
			response.setRtn(Response.FAIL_MSG);
		}

		return response;
	}

	/**
	 * @Author: libin
	 * @Desc :加入计划列表
	 */
	@RequestMapping(value = "/getAccedeListByParam",method = RequestMethod.POST)
	public AccedeListResponse getAccedeListByParam(@RequestBody @Valid AccedeListRequest request){
		AccedeListResponse response = new AccedeListResponse();
		Integer count = adminAccedeListService.countAccedeListTotal(request);
		// 查询列表传入分页
		Paginator paginator;
		if(request.getLimit() == 0){
			// 前台传分页
			paginator = new Paginator(request.getCurrPage(), count);
		} else {
			// 前台未传分页那默认 10
			paginator = new Paginator(request.getCurrPage(), count,request.getPageSize());
		}
		List<AccedeListCustomizeVO> list = adminAccedeListService.selectAccedeList(request,paginator.getOffset(), paginator.getLimit());
		// 原param表查询拼装
		if(!CollectionUtils.isEmpty(list)){
			Map<String, String> map1 = CacheUtil.getParamNameMap("USER_PROPERTY");
			 for(AccedeListCustomizeVO vo : list){
				 vo.setRecommendAttr(map1.getOrDefault(vo.getRecommendAttr(),null));
				 vo.setInviteUserAttributeName(map1.getOrDefault(vo.getInviteUserAttributeName(),null));
				 vo.setAttribute(map1.getOrDefault(vo.getAttribute(),null));
			 }
		}
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
	 * @Desc :根据参数获取 HjhAccedeSumVO
	 */
    @RequestMapping(value = "/getCalcSumByParam", method = RequestMethod.POST)
    public HjhAccedeSumResponse getCalcSumByParam(@RequestBody @Valid AccedeListRequest request) {
    	HjhAccedeSumResponse response = new HjhAccedeSumResponse();
    	HjhAccedeSumVO vo  = adminAccedeListService.getCalcSumByParam(request);
    	if(vo != null){
            response.setResult(vo);
            //代表成功
            response.setRtn(Response.SUCCESS);
    	}
    	return response;
    }
	
	/**
	 * @Author: libin
	 * @Desc :加入计划列表不分页
	 */
	@RequestMapping(value = "/getAccedeListByParamWithoutPage",method = RequestMethod.POST)
	public AccedeListResponse getAccedeListByParamWithoutPage(@RequestBody @Valid AccedeListRequest request){
		AccedeListResponse response = new AccedeListResponse();
		List<AccedeListCustomizeVO> list = adminAccedeListService.selectAccedeListByParamWithoutPage(request);
		// 原param表查询拼装
		if(!CollectionUtils.isEmpty(list)){
			Map<String, String> map1 = CacheUtil.getParamNameMap("USER_PROPERTY");
			 for(AccedeListCustomizeVO vo : list){
				 vo.setRecommendAttr(map1.getOrDefault(vo.getRecommendAttr(),null));
				 vo.setInviteUserAttributeName(map1.getOrDefault(vo.getInviteUserAttributeName(),null));
				 vo.setAttribute(map1.getOrDefault(vo.getAttribute(),null));
			 }
			response.setResultList(list);
			response.setRtn(Response.SUCCESS);
		}
		return response;
	}
	
	/**
	 * @Author: libin
	 * @Desc :更新计划专区表
	 */
    @RequestMapping("/updateSendStatusByParam")
    public IntegerResponse updateSendStatusByParam(@RequestBody AccedeListRequest request) {
    	int flg = adminAccedeListService.updateSendStatusByParam(request);
    	return new IntegerResponse(flg);
    }
    
    
	/**
	 * @Author: libin
	 * @Desc :根据参数获取 UserHjhInvistDetailVO
	 */
    @RequestMapping(value = "/selectUserHjhInvistDetail", method = RequestMethod.POST)
    public UserHjhInvistDetailResponse selectUserHjhInvistDetail(@RequestBody @Valid AccedeListRequest request) {
    	UserHjhInvistDetailResponse response = new UserHjhInvistDetailResponse();
    	UserHjhInvistDetailVO vo  = adminAccedeListService.selectUserHjhInvistDetail(request);
    	if(vo != null){
            response.setResult(vo);
            //代表成功
            response.setRtn(Response.SUCCESS);
    	}
    	return response;
    }
	/**
	 * 判断用户是否有持有中的计划。如果有，则不能解除出借授权和债转授权
     * @Author: libin
     * @return
	 */
	@GetMapping("/canCancelAuth/{userId}")
	public HjhAccedeResponse selectByAssignNidAndUserId(@PathVariable Integer userId) {
		HjhAccedeResponse response = new HjhAccedeResponse();
		if(adminAccedeListService.canCancelAuth(userId)) {
			response.setRtn(HjhAccedeResponse.SUCCESS);
		}else {
			response.setRtn(HjhAccedeResponse.FAIL);
		}
		return response;
	}
    
    
    
    
    
}
