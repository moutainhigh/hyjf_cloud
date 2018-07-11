/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.controller.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hyjf.admin.beans.request.SiteSettingRequestBean;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.service.SiteSettingService;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.config.SiteSettingsResponse;
import com.hyjf.am.vo.config.SiteSettingsVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * @author fuqiang
 * @version SiteSettingController, v0.1 2018/7/10 11:21
 */
@Api(value = "网站设置")
@RestController
@RequestMapping("/hyjf-admin/sitesetting")
public class SiteSettingController extends BaseController {
	@Autowired
	private SiteSettingService siteSettingService;

	@ApiOperation(value = "网站设置", notes = "网站设置初始化")
	@RequestMapping("/init")
	public AdminResult<SiteSettingsVO> init() {
		SiteSettingsResponse response = siteSettingService.selectSiteSetting();
		if (response == null) {
			return new AdminResult<>(FAIL, FAIL_DESC);
		}
		if (!Response.isSuccess(response)) {
			return new AdminResult<>(FAIL, response.getMessage());
		}
		return new AdminResult<>(response.getResult());
	}

	@ApiOperation(value = "网站设置", notes = "修改网站设置")
	@RequestMapping("/update")
	public AdminResult update(@RequestBody SiteSettingRequestBean requestBean) {
		SiteSettingsResponse response = siteSettingService.updateAction(requestBean);
		if (response == null) {
			return new AdminResult<>(FAIL, FAIL_DESC);
		}
		if (!Response.isSuccess(response)) {
			return new AdminResult<>(FAIL, response.getMessage());
		}
		return new AdminResult<>();
	}
}