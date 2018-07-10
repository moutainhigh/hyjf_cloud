/*
 * @Copyright: 2005-2018 so_what. All rights reserved.
 */
package com.hyjf.cs.trade.controller.web.tender.invest;

import com.hyjf.am.resquest.trade.TenderRequest;
import com.hyjf.am.vo.user.WebViewUserVO;
import com.hyjf.common.exception.CheckException;
import com.hyjf.common.util.ClientConstants;
import com.hyjf.common.util.CustomUtil;
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
 * @Description Web端散标投资
 * @Author sss
 * @Version v0.1
 * @Date 2018/6/19 9:32
 */
@Api(value = "Web端散标投资")
@RestController
@RequestMapping("/web/tender/borrow")
public class BorrowTenderController extends BaseTradeController {
    private static final Logger logger = LoggerFactory.getLogger(BorrowTenderController.class);

    @Autowired
    private BorrowTenderService borrowTenderService;

    @ApiOperation(value = "web端散标投资", notes = "web端散标投资")
    @PostMapping(value = "/tender", produces = "application/json; charset=utf-8")
    public WebResult<Map<String,Object>> borrowTender(@RequestHeader(value = "token", required = true) String token, @RequestBody @Valid TenderRequest tender, HttpServletRequest request) {
        logger.info("web端请求投资接口");
        String ip = CustomUtil.getIpAddr(request);
        tender.setIp(ip);
        tender.setToken(token);
        tender.setPlatform(String.valueOf(ClientConstants.WEB_CLIENT));

        WebResult<Map<String,Object>> result = null;
        try{
            result =  borrowTenderService.borrowTender(tender);
        }catch (CheckException e){
            throw e;
        }finally {
            // TODO: 2018/6/25  删除redis校验
            //RedisUtils.del(RedisConstants.HJH_TENDER_REPEAT + tender.getUser().getUserId());
        }
        return result;
    }

    @ApiOperation(value = "web端散标投资异步处理", notes = "web端散标投资异步处理")
    @RequestMapping("/bgReturn")
    @ResponseBody
    public BankCallResult borrowTenderBgReturn(BankCallBean bean , @RequestParam("couponGrantId") String couponGrantId) {
        logger.info("web端散标投资异步处理start,userId:{}", bean.getLogUserId());
        BankCallResult result = borrowTenderService.borrowTenderBgReturn(bean,couponGrantId);
        return result;
    }

    @ApiOperation(value = "web端散标投资获取投资结果", notes = "web端散标投资获取投资结果")
    @PostMapping(value = "/getBorrowTenderResult", produces = "application/json; charset=utf-8")
    public WebResult<Map<String,Object>> getBorrowTenderResult(@RequestHeader(value = "token", required = true) String token,
                                                               @RequestParam String logOrdId,
                                                               @RequestParam String borrowNid,
                                                               HttpServletRequest request) {
        logger.info("web端请求获取投资结果接口，logOrdId{}",logOrdId);
        WebViewUserVO userVO = borrowTenderService.getUsersByToken(token);
        return  borrowTenderService.getBorrowTenderResult(userVO,logOrdId,borrowNid);
    }

    @ApiOperation(value = "web端散标投资获取投资成功结果", notes = "web端散标投资获取投资成功结果")
    @PostMapping(value = "/getBorrowTenderResultSuccess", produces = "application/json; charset=utf-8")
    public WebResult<Map<String, Object>> getBorrowTenderResultSuccess(@RequestHeader(value = "token", required = true) String token,
                                                                       @RequestParam String logOrdId,
                                                                       @RequestParam Integer couponGrantId,
                                                                       @RequestParam String borrowNid) {
        logger.info("web端散标投资获取投资成功结果，logOrdId{}", logOrdId);
        WebViewUserVO userVO = borrowTenderService.getUsersByToken(token);
        return borrowTenderService.getBorrowTenderResultSuccess(userVO, logOrdId, borrowNid, couponGrantId);
    }

    @ApiOperation(value = "web端获取投资信息", notes = "web端获取投资信息")
    @PostMapping(value = "/investInfo", produces = "application/json; charset=utf-8")
    public WebResult<TenderInfoResult> getInvestInfo(@RequestHeader(value = "token", required = true) String token, @RequestBody @Valid TenderRequest tender, HttpServletRequest request) {
        logger.info("web端获取投资信息");
        tender.setToken(token);
        tender.setPlatform(String.valueOf(ClientConstants.WEB_CLIENT));
        return borrowTenderService.getInvestInfo(tender);
    }

}
