package com.hyjf.admin.controller.sponsor;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.hyjf.admin.common.util.ShiroConstants;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.interceptor.AuthorityAnnotation;
import com.hyjf.admin.service.SponsorLogService;
import com.hyjf.am.response.trade.SponsorLogResponse;
import com.hyjf.am.resquest.trade.SponsorLogRequest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@Api(value = "产品中心-担保户修改记录",tags = "产品中心-担保户修改记录")
@RestController
@RequestMapping("/hyjf-admin/sponsorLog")
public class SponsorLogController extends BaseController {
    @Autowired
    SponsorLogService sponsorLogService;
    
    private static final String PERMISSIONS = "sponsorLog";
	/**
	 * 查询列表
	 * @throws ParseException 
 	 */
	@ApiOperation(value = " 查询列表", notes = " 查询列表")
	@PostMapping(value = "/sponsorLogList")
	@ResponseBody
	@AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    private SponsorLogResponse sponsorLogList(@RequestBody SponsorLogRequest sponsorLogRequest)  {
        return sponsorLogService.sponsorLogList(sponsorLogRequest);
    }
	@ApiOperation(value = " 删除", notes = " 删除")
	@ResponseBody
	@AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_DELETE)
    @PostMapping(value = "/deleteSponsorLog")
    private SponsorLogResponse deleteSponsorLog(@RequestBody SponsorLogRequest sponsorLogRequest) {
    	return sponsorLogService.deleteSponsorLog(sponsorLogRequest);
    }
	@ApiOperation(value = " 查询", notes = " 查询")
	@ResponseBody
	@AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    @PostMapping(value = "/selectSponsorLog")
    private SponsorLogResponse selectSponsorLog(@RequestBody SponsorLogRequest sponsorLogRequest) {

        return sponsorLogService.selectSponsorLog(sponsorLogRequest);
    }
	@ApiOperation(value = "新增", notes = " 新增")
	@ResponseBody
	@PostMapping(value = "/insertSponsorLog")
	//@AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    private SponsorLogResponse insertSponsorLog(HttpServletRequest request,@RequestBody SponsorLogRequest sponsorLogRequest) {
		sponsorLogRequest.setAdminUserName(this.getUser(request).getUsername());
		sponsorLogRequest.setAdminUserId(Integer.valueOf(this.getUser(request).getId()));
    	return sponsorLogService.insertSponsorLog(sponsorLogRequest);
    }

}
