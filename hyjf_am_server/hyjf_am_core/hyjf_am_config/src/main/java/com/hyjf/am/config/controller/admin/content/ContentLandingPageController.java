/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.config.controller.admin.content;

import com.hyjf.am.config.controller.BaseConfigController;
import com.hyjf.am.config.dao.model.auto.Job;
import com.hyjf.am.config.dao.model.auto.LandingPage;
import com.hyjf.am.config.service.JobService;
import com.hyjf.am.config.service.LandingPageService;
import com.hyjf.am.response.AdminResponse;
import com.hyjf.am.response.admin.ContentEnvironmentResponse;
import com.hyjf.am.response.config.JobResponse;
import com.hyjf.am.response.config.LandingPageResponse;
import com.hyjf.am.resquest.admin.JobRequest;
import com.hyjf.am.resquest.admin.LandingPageRequest;
import com.hyjf.am.vo.config.JobsVo;
import com.hyjf.am.vo.config.LandingPageVo;
import com.hyjf.common.util.CommonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author tanyy
 * @version ContentLandingPageController, v0.1 2018/7/16 14:32
 */
@RestController
@RequestMapping("/am-config/content/contentlandingpage")
public class ContentLandingPageController extends BaseConfigController {
	@Autowired
	private LandingPageService landingPageService;

	/**
	 * 根据条件查询着路页管理
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("/searchaction")
	public LandingPageResponse searchAction(@RequestBody LandingPageRequest request) {
		logger.info("着路页管理开始......");
		LandingPageResponse response = new LandingPageResponse();
		List<LandingPage> list = landingPageService.searchAction(request);
		if (!CollectionUtils.isEmpty(list)) {
			List<LandingPageVo> voList = CommonUtils.convertBeanList(list, LandingPageVo.class);
			response.setResultList(voList);
		}
		return response;
	}

	/**
	 * 添加着路页管理
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("/insert")
	public LandingPageResponse insertAction(@RequestBody LandingPageRequest request) {
		LandingPageResponse response = new LandingPageResponse();
		landingPageService.insertAction(request);
		response.setRtn(AdminResponse.SUCCESS);
		return response;
	}

	/**
	 * 修改着路页管理
	 *
	 * @param request
	 * @return
	 */
	@RequestMapping("/update")
	public LandingPageResponse updateAction(@RequestBody LandingPageRequest request) {
		LandingPageResponse response = new LandingPageResponse();
		landingPageService.updateAction(request);
		response.setRtn(AdminResponse.SUCCESS);
		return response;
	}

	/**
	 * 根据id查询着路页管理
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping("/getrecord/{id}")
	public LandingPageResponse getRecord(@PathVariable Integer id) {
		LandingPageResponse response = new LandingPageResponse();
		LandingPage landingPage = landingPageService.getRecord(id);
		if (landingPage != null) {
			LandingPageVo vo = new LandingPageVo();
			BeanUtils.copyProperties(landingPage, vo);
		}
		return response;
	}

	/**
	 * 删除着路页管理
	 *
	 * @param id
	 * @return
	 */
	@RequestMapping("/delete/{id}")
	public LandingPageResponse delete(@PathVariable Integer id) {
		LandingPageResponse response = new LandingPageResponse();
		landingPageService.deleteById(id);
		response.setRtn(AdminResponse.SUCCESS);
		return response;
	}
}