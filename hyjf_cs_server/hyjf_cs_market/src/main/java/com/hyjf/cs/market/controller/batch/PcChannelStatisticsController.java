/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.market.controller.batch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hyjf.cs.common.controller.BaseController;
import com.hyjf.cs.market.service.PcChannelStatisticsService;

/**
 * @author fuqiang
 * @version PcChannelStatisticsController, v0.1 2018/7/16 10:25
 */
@RestController
@RequestMapping("/cs-market/pcchannelstatistics")
public class PcChannelStatisticsController extends BaseController {
	@Autowired
	private PcChannelStatisticsService pcChannelStatisticsService;

	@RequestMapping("/insertStatistics")
	public String insertStatistics() {
		pcChannelStatisticsService.insertStatistics();
		return "success";
	}

}