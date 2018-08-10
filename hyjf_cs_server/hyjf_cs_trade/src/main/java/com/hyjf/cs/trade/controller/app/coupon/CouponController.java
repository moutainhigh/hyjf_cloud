package com.hyjf.cs.trade.controller.app.coupon;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.resquest.trade.AppCouponRequest;
import com.hyjf.am.vo.trade.coupon.CouponUserForAppCustomizeVO;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.util.SecretUtil;
import com.hyjf.common.validator.Validator;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.cs.trade.bean.WebViewUser;
import com.hyjf.cs.trade.controller.BaseTradeController;
import com.hyjf.cs.trade.service.coupon.AppCouponService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 优惠券Controller
 *
 * @author zhangqingqing
 *
 */
@Api(value = "app端用户优惠券接口")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/hyjf-app/coupon")
public class CouponController extends BaseTradeController {
    Logger logger = LoggerFactory.getLogger(CouponController.class);

    @Autowired
    private AppCouponService appCouponService;

    /**
     * APP获取我的优惠券列表
     * @param request
     * @return
     */
    @ApiOperation(value = "获取我的优惠券列表", notes = "获取我的优惠券列表")
    @PostMapping("/getUserCoupons")
    public WebResult<Map<String,Object>> getUserCoupons(HttpServletRequest request) throws Exception {
        WebResult<Map<String,Object>> result = new WebResult<Map<String,Object>>();
        // 版本号
        String version = request.getParameter("version");
        // 网络状态
        String netStatus = request.getParameter("netStatus");
        // 平台
        String platform = request.getParameter("platform");
        // token
        String token = request.getParameter("token");
        // 唯一标识
        String sign = request.getParameter("sign");
        // 随机字符串
        String randomString = request.getParameter("randomString");
        // Order
        String order = request.getParameter("order");

        // 检查参数正确性
        if (Validator.isNull(version) || Validator.isNull(netStatus) || Validator.isNull(platform) || Validator.isNull(token) || Validator.isNull(sign) || Validator.isNull(randomString) || Validator.isNull(order)) {
            result.setStatus("1");
            result.setStatusDesc("请求参数非法");
            return result;
        }
        // 取得加密用的Key
        String key = SecretUtil.getKey(sign);
        if (Validator.isNull(key)) {
            result.setStatus("1");
            result.setStatusDesc("请求参数非法");
            return result;
        }
        logger.info("获取我的优惠券列表");
        String couponStatus = "0";
        Integer page = 1;
        Integer pageSize = 100;
        // 取得用户ID
        Integer userId = SecretUtil.getUserId(sign);
        if(null != userId){
            Map<String,Object> resultMap = new HashMap<String,Object>();
            Integer count =  appCouponService.countMyCoupon(userId,couponStatus);
            List<CouponUserForAppCustomizeVO> couponList = appCouponService.getMyCoupon(userId,page,pageSize,couponStatus);
            resultMap.put("couponTotal",count);
            resultMap.put("couponStatus",couponStatus);
            resultMap.put("couponList",couponList);
            resultMap.put("request","/hyjf-app/coupon/getUserCoupons");
            result.setData(resultMap);
        }else{
            result.setStatus(WebResult.FAIL);
            result.setStatusDesc("用户未登录");
            logger.info("用户未登录！");
        }
        return result;
    }

    @ApiOperation(value = "APP根据borrowNid和用户id获取用户可用优惠券和不可用优惠券列表", notes = "根据borrowNid和用户id获取用户可用优惠券和不可用优惠券列表")
    @PostMapping("/getProjectAvailableUserCoupon")
    public JSONObject getProjectAvailableUserCoupon(@RequestHeader(value = "token") String token, @RequestBody AppCouponRequest appCouponRequest) throws Exception {
        JSONObject ret = new JSONObject();
        // 检查参数正确性
        if ( Validator.isNull(appCouponRequest.getBorrowNid()) || Validator.isNull(appCouponRequest.getSign())||
                Validator.isNull(appCouponRequest.getPlatform())) {
            ret.put("status", "1");
            ret.put("statusDesc", "请求参数非法");
            return ret;
        }
        String money = appCouponRequest.getMoney();
        if(money==null||"".equals(money)||money.length()==0){
            money="0";
        }
        String investType = appCouponRequest.getBorrowType();
        WebViewUser user = RedisUtils.getObj(token, WebViewUser.class);
        logger.info("investType is :{}", investType);

        if(investType != null){

            if(investType==null||!"HJH".equals(investType)){
                // 如果为空  就执行以前的逻辑
                ret = appCouponService.getBorrowCoupon(user.getUserId(),appCouponRequest.getBorrowNid(),money,appCouponRequest.getPlatform());
            }else{
                // HJH的接口
                ret = appCouponService.getPlanCoupon(user.getUserId(),appCouponRequest.getBorrowNid(),money,appCouponRequest.getPlatform());
            }
        }else {
            if(appCouponRequest.getBorrowNid().contains("HJH")){
                ret = appCouponService.getPlanCoupon(user.getUserId(),appCouponRequest.getBorrowNid(),money,appCouponRequest.getPlatform());
            }else{
                // 如果为空  就执行以前的逻辑
                ret = appCouponService.getBorrowCoupon(user.getUserId(),appCouponRequest.getBorrowNid(),money,appCouponRequest.getPlatform());
            }
        }
        return ret;
    }


    /**
     * APP,PC散标投资获取我的优惠券列表
     * @param request
     * @return
     */
    @ApiOperation(value = "APP散标投资获取我的优惠券列表", notes = "APP散标投资获取我的优惠券列表")
    @PostMapping("/getborrowcoupon")
    public WebResult<Map<String,Object>> getBorrowCoupon(@RequestHeader(value = "token") String token, HttpServletRequest request,String borrowNid,String money,String platform) throws Exception {
        WebResult<Map<String,Object>> result = new WebResult<Map<String,Object>>();
        WebViewUser user = RedisUtils.getObj(token, WebViewUser.class);
        if(null != user && null != user.getUserId()){
            JSONObject resultMap = new JSONObject();
            resultMap = appCouponService.getBorrowCoupon(user.getUserId(),borrowNid,money,platform);
            result.setData(resultMap);
        }else{
            result.setStatus(WebResult.FAIL);
            result.setStatusDesc("用户未登录");
            logger.info("用户未登录！");
        }
        return result;
    }

    /**
     * APP,PC加入计划获取我的优惠券列表
     * @param request
     * @return
     */
    @ApiOperation(value = "APP加入计划获取我的优惠券列表", notes = "APP加入计划获取我的优惠券列表")
    @PostMapping("/getplancoupon")
    public WebResult<Map<String,Object>> getPlanCoupon(@RequestHeader(value = "token") String token, HttpServletRequest request,String planNid,String money,String platform) throws Exception {
        WebResult<Map<String,Object>> result = new WebResult<Map<String,Object>>();
        WebViewUser user = RedisUtils.getObj(token, WebViewUser.class);
        if(null != user && null != user.getUserId()){
            JSONObject resultMap = new JSONObject();
            resultMap = appCouponService.getPlanCoupon(user.getUserId(),planNid,money,platform);
            result.setData(resultMap);
        }else{
            result.setStatus(WebResult.FAIL);
            result.setStatusDesc("用户未登录");
            logger.info("用户未登录！");
        }
        return result;
    }

}
