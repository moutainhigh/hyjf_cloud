/*
 * @Copyright: 2005-2018 so_what. All rights reserved.
 */
package com.hyjf.cs.trade.controller.web.tender.hjh;

import com.hyjf.am.resquest.trade.TenderRequest;
import com.hyjf.common.util.ClientConstants;
import com.hyjf.common.util.CustomUtil;
import com.hyjf.cs.trade.controller.BaseTradeController;
import com.hyjf.cs.trade.service.HjhTenderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * @Description web端加入计划
 * @Author sss
 * @Version v0.1
 * @Date 2018/6/19 9:32
 */
@Api(value = "Web端加入计划")
@RestController
@RequestMapping("/web/hjh")
public class HjhPlanController extends BaseTradeController {
    private static final Logger logger = LoggerFactory.getLogger(HjhPlanController.class);

    @Autowired
    private HjhTenderService hjhTenderService;

    @ApiOperation(value = "web端加入计划", notes = "web端加入计划")
    @PostMapping(value = "/homeBorrowProjectList", produces = "application/json; charset=utf-8")
    public Object homeBorrowProjectList(@RequestHeader(value = "token", required = true) String token, @RequestBody @Valid TenderRequest tender, HttpServletRequest request) {
        String ip = CustomUtil.getIpAddr(request);
        tender.setIp(ip);
        tender.setToken(token);
        tender.setPlatform(String.valueOf(ClientConstants.WEB_CLIENT));
        hjhTenderService.joinPlan(tender);
        return null;
    }

}