/*
 * @Copyright: 2005-2018 so_what. All rights reserved.
 */
package com.hyjf.cs.trade.controller.app.tender.invest;

import com.hyjf.am.resquest.trade.TenderRequest;
import com.hyjf.am.vo.user.WebViewUserVO;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.exception.CheckException;
import com.hyjf.common.util.ClientConstants;
import com.hyjf.common.util.CustomUtil;
import com.hyjf.cs.common.annotation.RequestLimit;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.cs.trade.bean.TenderInfoResult;
import com.hyjf.cs.trade.controller.BaseTradeController;
import com.hyjf.cs.trade.service.BorrowTenderService;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.bean.BankCallResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * app端散标投资
 */
@Api(value = "APP端散标投资")
@RestController
@RequestMapping("/hyjf-app/user/invest")
public class AppBorrowTenderController extends BaseTradeController {
    private static final Logger logger = LoggerFactory.getLogger(AppBorrowTenderController.class);

    @Autowired
    private BorrowTenderService borrowTenderService;

    @ApiOperation(value = "APP端散标投资", notes = "APP端散标投资")
    @PostMapping(value = "/tender", produces = "application/json; charset=utf-8")
    @RequestLimit(seconds=3)
    public WebResult<Map<String,Object>> borrowTender(@RequestHeader(value = "token", required = true) String token, @RequestBody @Valid TenderRequest tender, HttpServletRequest request) {
        logger.info("APP端请求投资接口");
        String ip = CustomUtil.getIpAddr(request);
        tender.setIp(ip);
        tender.setToken(token);

        WebResult<Map<String,Object>> result = null;
        try{
            result =  borrowTenderService.borrowTender(tender);
        }catch (CheckException e){
            throw e;
        }finally {
            RedisUtils.del(RedisConstants.BORROW_TENDER_REPEAT + tender.getUser().getUserId());
        }
        return result;
    }

    /**
     * APP端散标异步处理
     * @param bean
     * @param couponGrantId
     * @return
     */
    @RequestMapping("/bgReturn")
    @ResponseBody
    public BankCallResult borrowTenderBgReturn(BankCallBean bean , @RequestParam("couponGrantId") String couponGrantId) {
        logger.info("APP端散标投资异步处理start,userId:{}", bean.getLogUserId());
        BankCallResult result ;
        try{
            result = borrowTenderService.borrowTenderBgReturn(bean,couponGrantId);
        }catch (CheckException e){
            throw e;
        }finally {
            RedisUtils.del(RedisConstants.BORROW_TENDER_REPEAT + bean.getLogUserId());
        }
        return result;
    }

    @ApiOperation(value = "APP端散标投资获取投资结果", notes = "APP端散标投资获取投资结果")
    @PostMapping(value = "/getBorrowTenderResult", produces = "application/json; charset=utf-8")
    public WebResult<Map<String,Object>> getBorrowTenderResult(@RequestHeader(value = "token", required = true) String token,
                                                               @RequestParam String logOrdId,
                                                               @RequestParam String borrowNid,
                                                               HttpServletRequest request) {
        logger.info("APP端请求获取投资结果接口，logOrdId{}",logOrdId);
        WebViewUserVO userVO = borrowTenderService.getUsersByToken(token);
        return  borrowTenderService.getBorrowTenderResult(userVO,logOrdId,borrowNid);
    }

    @ApiOperation(value = "APP端散标投资获取投资成功结果", notes = "APP端散标投资获取投资成功结果")
    @PostMapping(value = "/getBorrowTenderResultSuccess", produces = "application/json; charset=utf-8")
    public WebResult<Map<String, Object>> getBorrowTenderResultSuccess(@RequestHeader(value = "token", required = true) String token,
                                                                       @RequestParam String logOrdId,
                                                                       @RequestParam Integer couponGrantId,
                                                                       @RequestParam String borrowNid) {
        logger.info("APP端散标投资获取投资成功结果，logOrdId{}", logOrdId);
        WebViewUserVO userVO = borrowTenderService.getUsersByToken(token);
        return borrowTenderService.getBorrowTenderResultSuccess(userVO, logOrdId, borrowNid, couponGrantId);
    }

    @ApiOperation(value = "APP端获取投资信息", notes = "APP端获取投资信息")
    @PostMapping(value = "/getInvestInfo", produces = "application/json; charset=utf-8")
    public WebResult<TenderInfoResult> getInvestInfo(@RequestHeader(value = "token", required = true) String token, @RequestBody @Valid TenderRequest tender, HttpServletRequest request) {
        logger.info("APP端获取投资信息");
        tender.setToken(token);
        return borrowTenderService.getInvestInfo(tender);
    }

}
