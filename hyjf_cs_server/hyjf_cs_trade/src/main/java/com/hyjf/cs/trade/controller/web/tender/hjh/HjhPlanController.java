/*
 * @Copyright: 2005-2018 so_what. All rights reserved.
 */
package com.hyjf.cs.trade.controller.web.tender.hjh;

import com.hyjf.am.resquest.trade.TenderRequest;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.exception.CheckException;
import com.hyjf.common.util.ClientConstants;
import com.hyjf.common.util.CustomUtil;
import com.hyjf.cs.common.annotation.RequestLimit;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.cs.trade.bean.TenderInfoResult;
import com.hyjf.cs.trade.controller.BaseTradeController;
import com.hyjf.cs.trade.service.hjh.HjhTenderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Description web端加入计划
 * @Author sss
 * @Version v0.1
 * @Date 2018/6/19 9:32
 */
@Api(tags = "web端-加入计划")
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/hyjf-web/tender/hjh")
public class HjhPlanController extends BaseTradeController {
    private static final Logger logger = LoggerFactory.getLogger(HjhPlanController.class);

    @Autowired
    private HjhTenderService hjhTenderService;

    @ApiOperation(value = "web端加入计划", notes = "web端加入计划")
    @PostMapping(value = "/joinPlan", produces = "application/json; charset=utf-8")
    @RequestLimit(seconds=3)
    public WebResult<Map<String, Object>> joinPlan(@RequestHeader(value = "userId", required = false) Integer userId, @RequestBody TenderRequest tender, HttpServletRequest request) {
        String ip = CustomUtil.getIpAddr(request);
        tender.setIp(ip);
        tender.setUserId(userId);
        tender.setPlatform(String.valueOf(ClientConstants.WEB_CLIENT));
        WebResult<Map<String, Object>> result = null;
        try {
            result = hjhTenderService.joinPlan(tender);
        } catch (CheckException e) {
            throw e;
        } finally {
            RedisUtils.del(RedisConstants.HJH_TENDER_REPEAT + tender.getUser().getUserId());
        }
        return result;
    }

    @ApiOperation(value = "web获取计划投资信息", notes = "web获取计划投资信息")
    @PostMapping(value = "/investInfo", produces = "application/json; charset=utf-8")
    public WebResult<TenderInfoResult> getInvestInfo(@RequestHeader(value = "userId", required = false) Integer userId, @RequestBody TenderRequest tender) {
        tender.setUserId(userId);
        tender.setPlatform(String.valueOf(ClientConstants.WEB_CLIENT));
        return  hjhTenderService.getInvestInfo(tender);
    }

    @ApiOperation(value = "web计划投资校验", notes = "web计划投资校验")
    @PostMapping(value = "/planCheck", produces = "application/json; charset=utf-8")
    public WebResult<TenderInfoResult> planCheck(@RequestHeader(value = "userId", required = false) Integer userId, @RequestBody TenderRequest tender) {
        tender.setUserId(userId);
        tender.setPlatform(String.valueOf(ClientConstants.WEB_CLIENT));
        hjhTenderService.checkPlan(tender);
        WebResult<TenderInfoResult> resultWebResult = new WebResult();
        return resultWebResult;
    }

}
