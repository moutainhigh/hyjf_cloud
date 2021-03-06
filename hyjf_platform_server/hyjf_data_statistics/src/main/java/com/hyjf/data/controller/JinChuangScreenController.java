/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.data.controller;

import com.hyjf.am.vo.admin.JcCustomerServiceVO;
import com.hyjf.data.bean.jinchuang.*;
import com.hyjf.data.response.JcScreenResponse;
import com.hyjf.data.result.ApiResult;
import com.hyjf.data.market.service.JinChuangDataService;
import com.hyjf.data.vo.jinchuang.JcDataStatisticsVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * @author yaoyong
 * @version JinChuangScreenController, v0.1 2019/6/19 9:23
 */
@Api(value = "金创展厅大屏", tags = "金创展厅大屏数据")
@RestController
@RequestMapping("/hyjf-web/jinChuangScreen")
public class JinChuangScreenController {

    @Autowired
    private JinChuangDataService jinChuangDataService;

    @ApiOperation(value = "获取大屏数据", notes = "获取大屏数据")
    @RequestMapping(value = "/getData", method = RequestMethod.POST)
    public ApiResult getData() {
        JcScreenResponse response = new JcScreenResponse();
        JcUserConversion userConversion = jinChuangDataService.getUserConversion();
        String jsonObject = jinChuangDataService.getUserPoint();
        List<JcDataStatisticsVO> dataStatisticsList = jinChuangDataService.getDataStatistics();
        JcUserAnalysis userAnalysis = jinChuangDataService.getUserAnalysis();
        String tradeAmount = jinChuangDataService.getTotalInvestAmount();
        List<JcRegisterTrade> registerTrades = jinChuangDataService.getRegisterTrade();
        if (!CollectionUtils.isEmpty(registerTrades)) {
            response.setRegisterTrades(registerTrades);
        }
        JcCustomerServiceVO customerService = jinChuangDataService.getCustomerService();
        List<JcUserInterest> interests = jinChuangDataService.getUserInterest();
        response.setInterests(interests);
        response.setTradeAmount(tradeAmount);
        response.setUserConversion(userConversion);
        response.setJsonObject(jsonObject);
        response.setDataStatisticsList(dataStatisticsList);
        response.setAnalysis(userAnalysis);
        response.setCustomerService(customerService);
        return new ApiResult(response);
    }
}
