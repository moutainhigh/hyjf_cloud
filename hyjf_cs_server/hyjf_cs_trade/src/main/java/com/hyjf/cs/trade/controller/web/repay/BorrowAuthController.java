package com.hyjf.cs.trade.controller.web.repay;

import com.alibaba.fastjson.JSON;
import com.hyjf.am.resquest.trade.BorrowAuthRequest;
import com.hyjf.am.vo.trade.repay.BorrowAuthCustomizeVO;
import com.hyjf.am.vo.user.WebViewUserVO;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.cs.common.util.Page;
import com.hyjf.cs.trade.controller.BaseTradeController;
import com.hyjf.cs.trade.service.BorrowAuthService;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.bean.BankCallResult;
import com.hyjf.pay.lib.bank.util.BankCallStatusConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 借款人受托支付
 * @author hesy
 * @version BorrowAuthController, v0.1 2018/7/6 14:15
 */
@Api(value = "web端-借款人受托支付相关", description ="web端-借款人受托支付相关")
@RestController
@RequestMapping("/web/borrowauth")
public class BorrowAuthController extends BaseTradeController {
    @Autowired
    BorrowAuthService borrowAuthService;

    /**
     * 借款人待授权列表
     * @auther: hesy
     * @date: 2018/7/6
     */
    @ApiOperation(value = "待授权列表", notes = "待授权列表")
    @PostMapping(value = "/list_auth", produces = "application/json; charset=utf-8")
    public WebResult<List<BorrowAuthCustomizeVO>> authList(@RequestHeader(value = "token", required = true) String token, @RequestBody BorrowAuthRequest requestBean, HttpServletRequest request){
        WebResult<List<BorrowAuthCustomizeVO>> result = new WebResult<List<BorrowAuthCustomizeVO>>();
        WebViewUserVO userVO = borrowAuthService.getUsersByToken(token);

        logger.info("获取待授权列表开始，requestBean：{}", JSON.toJSONString(requestBean));

        // 请求参数校验
        borrowAuthService.checkForAuthList(requestBean);

        Page page = Page.initPage(requestBean.getCurrPage(), requestBean.getPageSize());
        Integer count = borrowAuthService.selectAuthCount(requestBean);
        page.setTotal(count);

        try {
            List<BorrowAuthCustomizeVO> resultList = borrowAuthService.selectAuthList(requestBean);
            result.setData(resultList);
        } catch (Exception e) {
            logger.error("获取待授权列表异常", e);
            result.setStatus(WebResult.ERROR);
            result.setStatusDesc(WebResult.ERROR_DESC);
        }

        result.setPage(page);
        return result;
    }

    /**
     * 借款人已授权列表
     * @auther: hesy
     * @date: 2018/7/6
     */
    @ApiOperation(value = "已授权列表", notes = "已授权列表")
    @PostMapping(value = "/list_authed", produces = "application/json; charset=utf-8")
    public WebResult<List<BorrowAuthCustomizeVO>> authedList(@RequestHeader(value = "token", required = true) String token, @RequestBody BorrowAuthRequest requestBean, HttpServletRequest request){
        WebResult<List<BorrowAuthCustomizeVO>> result = new WebResult<List<BorrowAuthCustomizeVO>>();
        WebViewUserVO userVO = borrowAuthService.getUsersByToken(token);

        logger.info("获取已授权列表开始，requestBean：{}", JSON.toJSONString(requestBean));

        // 请求参数校验
        borrowAuthService.checkForAuthList(requestBean);

        Page page = Page.initPage(requestBean.getCurrPage(), requestBean.getPageSize());
        Integer count = borrowAuthService.selectAuthedCount(requestBean);
        page.setTotal(count);

        try {
            List<BorrowAuthCustomizeVO> resultList = borrowAuthService.selectAuthedList(requestBean);
            result.setData(resultList);
        } catch (Exception e) {
            logger.error("获取已授权列表异常", e);
            result.setStatus(WebResult.ERROR);
            result.setStatusDesc(WebResult.ERROR_DESC);
        }

        result.setPage(page);
        return result;
    }

    /**
     * 受托支付授权
     * @auther: hesy
     * @date: 2018/7/7
     */
    @ApiOperation(value = "受托支付授权", notes = "受托支付授权")
    @GetMapping(value = "/trusteepay/{borrowNid}", produces = "application/json; charset=utf-8")
    public WebResult<Object> trusteePay(@RequestHeader(value = "token", required = true) String token, @PathVariable String borrowNid, HttpServletRequest request){
        WebResult<Object> result = new WebResult<Object>();
        WebViewUserVO userVO = borrowAuthService.getUsersByToken(token);

        logger.info("受托支付授权开始，borrowNid：{}", borrowNid);

        // 请求参数校验
        borrowAuthService.checkForAuth(borrowNid, userVO);

        try {
            Map<String,Object> data = borrowAuthService.callTrusteePay(borrowNid,userVO);
            result.setData(data);
        } catch (Exception e) {
            result.setStatus(WebResult.ERROR);
            result.setStatusDesc(WebResult.ERROR_DESC);
            logger.error("受托支付授权异常", e);
        }

        return result;
    }

    /**
     * 受托支付授权异步回调
     * @auther: hesy
     * @date: 2018/7/7
     */
    @ApiOperation(value = "受托支付授权异步回调", notes = "受托支付授权异步回调")
    @PostMapping(value = "/auth_bgrturn", produces = "application/json; charset=utf-8")
    public BankCallResult authBgReturn(@RequestHeader(value = "token", required = true) String token, @RequestBody BankCallBean bean, HttpServletRequest request) {
        WebViewUserVO user = borrowAuthService.getUsersByToken(token);

        BankCallResult result = new BankCallResult();
        String phone = request.getParameter("phone");
        logger.info("受托支付申请异步回调start");
        bean.setMobile(phone);
        bean.convert();
        int userId = Integer.parseInt(bean.getLogUserId());

        // 绑卡后处理
        try {
            boolean checkResult = RedisUtils.tranactionSet("trusteePay" + bean.getLogOrderId(), 600);
            if(checkResult){
                if (bean!=null&&BankCallStatusConstant.RESPCODE_SUCCESS.equals(bean.getRetCode()) && "1".equals(bean.getState())) {
                    Integer rtn = borrowAuthService.updateTrusteePaySuccess(bean.getProductId());
                    if(rtn != 1){
                        result.setStatus(false);
                        result.setMessage("受托支付申请异步回调处理失败");
                        logger.error("受托支付异步回调处理失败,borrowNid{}", bean.getProductId());
                        return result;
                    }
                }
            }
        } catch (Exception e) {
            result.setStatus(false);
            result.setMessage("受托支付申请异步回调处理异常");
            logger.error("受托支付申请异步回调处理异常", e);
            return result;
        }
        result.setStatus(true);
        logger.info("受托支付异步回调处理成功,borrowNid{}", bean.getProductId());
        return result;
    }
}
