/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.trade.service.userlargescreen.impl;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.user.UserScreenConfigResponse;
import com.hyjf.am.resquest.admin.UserLargeScreenRequest;
import com.hyjf.am.vo.api.UserLargeScreenTwoVO;
import com.hyjf.am.vo.api.UserLargeScreenVO;
import com.hyjf.am.vo.user.ScreenConfigVO;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.exception.MQException;
import com.hyjf.cs.trade.bean.UserLargeScreenResultBean;
import com.hyjf.cs.trade.bean.UserLargeScreenTwoResultBean;
import com.hyjf.cs.trade.client.AmTradeClient;
import com.hyjf.cs.trade.client.AmUserClient;
import com.hyjf.cs.trade.mq.base.CommonProducer;
import com.hyjf.cs.trade.mq.base.MessageContent;
import com.hyjf.cs.trade.service.userlargescreen.UserLargeScreenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * @author: tanyy
 * @version: UserLargeScreenServiceImpl, v0.1 2018/7/23 15:18
 */
@Service
public class UserLargeScreenServiceImpl  implements UserLargeScreenService {
    private static final Logger logger = LoggerFactory.getLogger(UserLargeScreenServiceImpl.class);
    @Autowired
    private AmUserClient amUserClient;
    @Autowired
    public AmTradeClient amTradeClient;
    @Autowired
    private CommonProducer commonProducer;

    @Override
    public UserLargeScreenResultBean getOnePage(){
        UserLargeScreenResultBean bean = new UserLargeScreenResultBean();
        UserLargeScreenRequest request = new UserLargeScreenRequest();
        String dateString = getNowDateOfDay();
        request.setTaskTime(dateString);
        UserScreenConfigResponse userScreenConfigResponse = amUserClient.getScreenConfig(request);
        UserLargeScreenVO scalePerformanceVo =  amTradeClient.getScalePerformance();
        UserLargeScreenVO monthScalePerformanceListVo =  amTradeClient.getMonthScalePerformanceList();
        UserLargeScreenVO totalAmountVo =  amTradeClient.getTotalAmount();
        UserLargeScreenVO achievementDistributionListVo =  amTradeClient.getAchievementDistributionList();
        UserLargeScreenVO monthReceivedPaymentsVo =  amTradeClient.getMonthReceivedPayments();
        UserLargeScreenVO userCapitalDetailsVo = amTradeClient.getUserCapitalDetails();
       // UserCustomerTaskConfigResponse userCustomerTaskConfigResponse = amUserClient.getCustomerTaskConfig(request);
        ScreenConfigVO vo = userScreenConfigResponse.getResult();
        bean.setNewPassengerGoal(vo.getNewPassengerGoal().divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP));
        bean.setOldPassengerGoal(vo.getOldPassengerGoal().divide(new BigDecimal(10000),2,BigDecimal.ROUND_HALF_UP));
        bean.setScalePerformanceNew(scalePerformanceVo.getScalePerformanceNew());
        bean.setScalePerformanceOld(scalePerformanceVo.getScalePerformanceOld());
        bean.setMonthScalePerformanceListNew(monthScalePerformanceListVo.getMonthScalePerformanceListNew());
        bean.setMonthScalePerformanceListOld(monthScalePerformanceListVo.getMonthScalePerformanceListOld());
        bean.setTotalAmount(totalAmountVo.getTotalAmount());
        bean.setAchievementRate(totalAmountVo.getAchievementRate());
        bean.setAchievementDistribution(totalAmountVo.getAchievementDistribution());
        bean.setAchievementDistributionList(achievementDistributionListVo.getAchievementDistributionList());
        bean.setMonthReceivedPaymentsNew(monthReceivedPaymentsVo.getMonthReceivedPaymentsNew());
        bean.setMonthReceivedPaymentsOld(monthReceivedPaymentsVo.getMonthReceivedPaymentsOld());
        bean.setUserCapitalDetailList(userCapitalDetailsVo.getUserCapitalDetailList());
        return bean;
    }

    @Override
    public UserLargeScreenTwoResultBean getTwoPage() {
        try {
            commonProducer.messageSend(new MessageContent(MQConstant.SCREEN_DATA_TWO_TOPIC,
                    MQConstant.SCREEN_DATA_TWO_SELECT_TAG, UUID.randomUUID().toString(), new JSONObject()));
        } catch (MQException e) {
            logger.error("用户画像屏幕二运营部站岗资金获取异常,异常详情如下:{}", e);
        }
        UserLargeScreenTwoResultBean bean = new UserLargeScreenTwoResultBean();
        // 日业绩(新客组、老客组)
        UserLargeScreenTwoVO dayScalePerformanceListVo =  amTradeClient.getDayScalePerformanceList();
        bean.setDayScalePerformanceListNew(dayScalePerformanceListVo.getDayScalePerformanceListNew());
        bean.setDayScalePerformanceListOld(dayScalePerformanceListVo.getDayScalePerformanceListOld());
        // 日回款(新客组、老客组)
        UserLargeScreenTwoVO dayReceivedPaymentsVo =  amTradeClient.getDayReceivedPayments();
        bean.setDayReceivedPaymentsNew(dayReceivedPaymentsVo.getDayReceivedPaymentsNew());
        bean.setDayReceivedPaymentsOld(dayReceivedPaymentsVo.getDayReceivedPaymentsOld());
        // 本月数据统计(新客组、老客组)
        UserLargeScreenTwoVO monthDataStatisticsVo =  amTradeClient.getMonthDataStatistics();
        bean.setMonthDataStatisticsNew(monthDataStatisticsVo.getMonthDataStatisticsNew());
        bean.setMonthDataStatisticsOld(monthDataStatisticsVo.getMonthDataStatisticsOld());
        // 运营部所有用户id
        //List<Integer> userIds = amUserClient.getOperUserIds();
        // 运营部月度业绩数据
        UserLargeScreenTwoVO operMonthPerformanceDataVo =  amTradeClient.getOperMonthPerformanceData(null);
        bean.setOperMonthPerformanceData(operMonthPerformanceDataVo.getOperMonthPerformanceData());
        return bean;
    }

    /**
     * 获取现在时间
     *
     * @return 返回时间类型 yyyyMM
     */
    private  String getNowDateOfDay() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        String dateString = formatter.format(currentTime);
        return dateString;
    }
}
