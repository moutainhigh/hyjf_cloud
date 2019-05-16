package com.hyjf.cs.market.controller.web.activity;

import com.hyjf.am.bean.result.BaseResult;
import com.hyjf.cs.market.controller.AbstractActivity51Controller;
import com.hyjf.cs.market.vo.Activity51VO;
import com.hyjf.cs.market.vo.ActivityTimeVO;
import com.hyjf.cs.market.vo.GuessVO;
import com.hyjf.cs.market.vo.RewardReceiveVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiasq
 * @version WebActivity51Controller, v0.1 2019-04-17 9:48
 */
@Api(tags = "web端-2019-5-1青马活动")
@RestController
@RequestMapping("/hyjf-web/activity/qingma")
public class WebActivity51Controller extends AbstractActivity51Controller {
    private Logger logger = LoggerFactory.getLogger(WebActivity51Controller.class);

    private final String WEB_SUCCESS_STATUS = "000";
    private final String WEB_FAIL_STATUS = "1";

    /**
     * 查询当前时间是不是在活动时间范围内
     * @return
     */
    @ApiOperation(value = "查询当前时间是不是在活动时间范围内", notes = "查询当前时间是不是在活动时间范围内")
    @RequestMapping(value = "/isActivityTime", method = RequestMethod.GET)
    public BaseResult<ActivityTimeVO> isActivityTime() {
        logger.info("web-查询当前时间是不是在活动时间范围内...");
        return isActivityTime(WEB_SUCCESS_STATUS, WEB_FAIL_STATUS);
    }


    /**
     * 查询活动期间当前累计出借金额
     *
     * @return
     */
    @ApiOperation(value = "查询活动期间当前累计出借金额", notes = "累计出借金额")
    @RequestMapping(value = "/sumAmount", method = RequestMethod.GET)
    public BaseResult<Activity51VO> getSumAmount() {
        logger.info("查询活动期间当前累计出借金额...");
        return getSumAmount(WEB_SUCCESS_STATUS, WEB_FAIL_STATUS);
    }

    /**
     * 领取优惠券  判断是否满足条件，发放优惠券
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "领取优惠券", notes = "活动期间累计出借金额达到目标值领取优惠券，用户需要满足出借年化>=1w")
    @RequestMapping(value = "/sendCoupon", method = RequestMethod.GET)
    public BaseResult sendCoupon(@RequestHeader int userId,
                                 @ApiParam(required = true, name = "grade", value = "累计出借金额区间， 从第一档（3000万）开始，依次传递1,2,3,4")
                                 @RequestParam int grade) {
        logger.info("web端-领取优惠券, userId is: {}", userId);

        return sendCoupon(userId, grade, WEB_SUCCESS_STATUS, WEB_FAIL_STATUS);
    }

    @ApiOperation(value = "单个档位判断用户是否已经领取优惠券", notes = "判断用户是否已经领取优惠券-对应累计出借金额区间")
    @RequestMapping(value = "/isReceiveCoupon", method = RequestMethod.GET)
    public BaseResult<RewardReceiveVO> isReceiveCoupon(@RequestHeader int userId,
                                                       @ApiParam(required = true, name = "grade", value = "累计出借金额区间， 从第一档（3000万）开始，依次传递1,2,3,4")
                                                       @RequestParam int grade) {
        logger.info("web端-单个档位判断用户是否已经领取优惠券, userId is: {}， grade is: {}", userId, grade);

        return isReceiveCoupon(userId, grade, WEB_SUCCESS_STATUS, WEB_FAIL_STATUS);
    }

    @ApiOperation(value = "批量判断用户是否已经领取优惠券", notes = "判断用户是否已经领取优惠券-对应累计出借金额区间")
    @RequestMapping(value = "/getReceiveStatusList", method = RequestMethod.GET)
    public BaseResult<List<RewardReceiveVO>> getReceiveStatusList(@RequestHeader int userId) {
        logger.info("web端-批量判断用户是否已经领取优惠券, userId is: {}", userId);
        return getReceiveStatusList(userId, WEB_SUCCESS_STATUS, WEB_FAIL_STATUS);
    }


    /**
     * 用户竞猜
     *
     * @param userId
     * @param grade
     * @return
     */
    @ApiOperation(value = "用户竞猜", notes = "不支持重复竞猜")
    @RequestMapping(value = "/guess", method = RequestMethod.GET)
    public BaseResult guess(@RequestHeader int userId,
                            @ApiParam(required = true, name = "grade", value = "用户竞猜区间， 从第一档（1-99）开始，依次传递1,2,3,4")
                            @RequestParam int grade) {
        logger.info("web端-用户竞猜, userId is: {}, grade is: {}", userId, grade);
        return guess(userId, grade, WEB_SUCCESS_STATUS, WEB_FAIL_STATUS);
    }


    /**
     * 查询用户是否竞猜
     *
     * @param userId
     * @return
     */
    @ApiOperation(value = "查询用户是否竞猜", notes = "查询用户是否竞猜")
    @RequestMapping(value = "/isGuess", method = RequestMethod.GET)
    public BaseResult<GuessVO> isGuess(@RequestHeader int userId) {
        logger.info("web端-用户竞猜, userId is: {}", userId);
        return isGuess(userId, WEB_SUCCESS_STATUS, WEB_FAIL_STATUS);
    }

}
