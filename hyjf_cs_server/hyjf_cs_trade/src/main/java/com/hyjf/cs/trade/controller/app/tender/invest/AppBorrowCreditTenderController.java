/*
 * @Copyright: 2005-2018 so_what. All rights reserved.
 */
package com.hyjf.cs.trade.controller.app.tender.invest;

import com.hyjf.am.resquest.trade.TenderRequest;
import com.hyjf.common.exception.CheckException;
import com.hyjf.common.util.CustomUtil;
import com.hyjf.cs.common.bean.result.AppResult;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.cs.trade.controller.BaseTradeController;
import com.hyjf.cs.trade.service.invest.BorrowCreditTenderService;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.bean.BankCallResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @Description APP端散标债转出借
 * @Author sunss
 * @Date 2018/7/3 14:02
 */
@Api(value = "app端-散标债转出借",tags = "app端-散标债转出借")
@RestController
@RequestMapping("/hyjf-app/tender/credit")
public class AppBorrowCreditTenderController extends BaseTradeController {

    @Autowired
    private BorrowCreditTenderService borrowTenderService;

    @ApiOperation(value = "APP端散标债转出借", notes = "APP端散标债转出借")
    @PostMapping(value = "/tender", produces = "application/json; charset=utf-8")
    public WebResult<Map<String,Object>> borrowTender(@RequestHeader(value = "userId") Integer userId, TenderRequest tender, HttpServletRequest request) {
        logger.info("APP端请求债转出借接口");
        String ip = CustomUtil.getIpAddr(request);
        tender.setIp(ip);
        tender.setUserId(userId);
        //tender.setPlatform(String.valueOf(ClientConstants.WEB_CLIENT));

        WebResult<Map<String,Object>> result = null;
        try{
            result =  borrowTenderService.borrowCreditTender(tender);
        }catch (CheckException e){
            throw e;
        }
        return result;
    }

    /**
     * APP端债转标异步处理
     * @param bean
     * @return
     */
    @ApiIgnore
    @PostMapping("/bgReturn")
    @ResponseBody
    public BankCallResult borrowCreditTenderBgReturn(BankCallBean bean ) {
        logger.info("APP端债转出借异步处理start,userId:{},返回码:{}", bean.getLogUserId(),bean.getRetCode());
        BankCallResult result = borrowTenderService.borrowCreditTenderBgReturn(bean);
        return result;
    }

    @ApiOperation(value = "APP端债转出借获取出借结果  失败", notes = "APP端债转出借获取出借结果  失败")
    @PostMapping(value = "/getResult", produces = "application/json; charset=utf-8")
    public AppResult<Map<String,Object>> getBorrowTenderResult(@RequestHeader(value = "userId", required = true) Integer userId,
                                                               @RequestParam String logOrdId) {
        logger.info("APP端债转出借获取出借结果，logOrdId{}",logOrdId);
        WebResult<Map<String,Object>> webResult = borrowTenderService.getFaileResult(userId,logOrdId);
        AppResult<Map<String,Object>> result  = new AppResult<Map<String,Object>>();
        Map<String,Object> data = webResult.getData();
        if(data!=null && data.containsKey("errorMsg")){
            data.put("error",data.get("errorMsg"));
            data.remove("errorMsg");
        }
        result.setStatusDesc(webResult.getStatusDesc());
        result.setStatus(webResult.getStatus());
        result.setData(data);
        return result;
    }

    @ApiOperation(value = "APP端债转出借获取出借结果  成功", notes = "APP端债转出借获取出借结果  成功")
    @PostMapping(value = "/getSuccessResult", produces = "application/json; charset=utf-8")
    public AppResult<Map<String,Object>> getSuccessResult(@RequestHeader(value = "userId", required = true) Integer userId,
                                                          @RequestParam String logOrdId) {
        logger.info("APP端债转出借获取出借结果，logOrdId{}",logOrdId + ",userId:" + userId);
        WebResult<Map<String,Object>> webResult = borrowTenderService.getSuccessResult(userId,logOrdId);
        AppResult<Map<String,Object>> result  = new AppResult<Map<String,Object>>();
        Map<String,Object> data = webResult.getData();
        if(data.containsKey("assignCapital")){
            data.put("account",data.get("assignCapital"));
            data.put("income",data.get("assignInterest"));
            data.remove("assignCapital");
            data.remove("assignInterest");
        }
        result.setStatusDesc(webResult.getStatusDesc());
        result.setStatus(webResult.getStatus());
        result.setData(data);
        return result;
    }

}
