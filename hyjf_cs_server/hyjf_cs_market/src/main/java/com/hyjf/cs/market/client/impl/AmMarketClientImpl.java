package com.hyjf.cs.market.client.impl;

import java.util.List;
import java.util.Map;

import com.hyjf.am.vo.activity.ActivityUserGuessVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hyjf.am.response.BooleanResponse;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.market.*;
import com.hyjf.am.resquest.market.ActivityListRequest;
import com.hyjf.am.vo.activity.ActivityUserRewardVO;
import com.hyjf.am.vo.admin.SellDailyDistributionVO;
import com.hyjf.am.vo.market.ActivityListBeanVO;
import com.hyjf.am.vo.market.ActivityListVO;
import com.hyjf.am.vo.market.AppAdsCustomizeVO;
import com.hyjf.am.vo.market.SellDailyVO;
import com.hyjf.cs.market.client.AmMarketClient;

/**
 * @Auther: walter.limeng
 * @Date: 2018/7/26 11:18
 * @Description: AmMarketClientImpl
 */
@Service
public class AmMarketClientImpl implements AmMarketClient {
    @Autowired
    private RestTemplate restTemplate;
    @Override
    public Integer queryActivityCount(ActivityListRequest activityListRequest) {
        ActivityListResponse response = restTemplate.postForObject(
                "http://AM-MARKET/am-market/activity/queryactivitycount",activityListRequest,
                ActivityListResponse.class);
        if (response != null) {
            return response.getCount();
        }
        return null;
    }

    @Override
    public List<ActivityListBeanVO> queryActivityList(ActivityListRequest activityListRequest) {
        ActivityListResponse response = restTemplate.postForObject(
                "http://AM-MARKET/am-market/activity/queryactivitylist",activityListRequest,
                ActivityListResponse.class);
        if (response != null) {
            return response.getActivityList();
        }
        return null;
    }

    @Override
    public List<SellDailyDistributionVO> selectSellDailyDistribution() {
        SellDailyDistributionResponse response = restTemplate.getForEntity(
                "http://AM-MARKET/am-market/daily_distribution/selectdistribution",
                SellDailyDistributionResponse.class).getBody();
        if (response != null) {
            return response.getResultList();
        }
        return null;
    }

    @Override
    public List<SellDailyVO> selectSellDailyByDateStr(String dateStr) {
        SellDailyResponse response = restTemplate.getForObject(
                "http://AM-MARKET/am-market/sell_daily/selectbydatestr/" + dateStr,
                SellDailyResponse.class);
        return response.getResultList();
    }

    @Override
    public SellDailyVO selectOCSum(String formatDateStr) {
        SellDailyResponse response = restTemplate.getForObject(
                "http://AM-MARKET/am-market/sell_daily/selectocsum/" + formatDateStr,
                SellDailyResponse.class);
        return response.getResult();
    }

    @Override
    public SellDailyVO selectPrimaryDivisionSum(String formatDateStr, int drawOrder) {
        SellDailyResponse response = restTemplate.getForObject(
                "http://AM-MARKET/am-market/sell_daily/select_primary_divisionsum/" + formatDateStr + "/" + drawOrder,
                SellDailyResponse.class);
        return response.getResult();
    }

    @Override
    public SellDailyVO selectAllSum(String formatDateStr) {
        SellDailyResponse response = restTemplate.getForObject(
                "http://AM-MARKET/am-market/sell_daily/select_allsum/" + formatDateStr,
                SellDailyResponse.class);
        return response.getResult();
    }

	@Override
	public boolean hasGeneratorDataToday() {
		BooleanResponse response = restTemplate
				.getForObject("http://AM-MARKET/am-market/sell_daily/has_generator_data_today", BooleanResponse.class);
		if (response != null) {
			return response.getResultBoolean();
		}
		return false;
	}

    @Override
    public void batchInsertSellDaily(List<SellDailyVO> list) {
		restTemplate.postForObject("http://AM-MARKET/am-market/sell_daily/batch_insert_sell_daily", list,
                BooleanResponse.class);
    }

    @Override
    public void calculateRate() {
        restTemplate.getForObject("http://AM-MARKET/am-market/sell_daily/calculate_rate",
                BooleanResponse.class);
    }

    @Override
    public void callActivityEnd() {
        restTemplate.getForObject("http://AM-MARKET/am-market/ads/batch/update",
                Object.class);
    }

    /**
     * 查询首页banner
     * @param ads
     * @return
     */
    public List<AppAdsCustomizeVO> searchBannerList(Map<String, Object> ads) {
        AppAdsCustomizeResponse response = restTemplate.postForObject(
                "http://AM-MARKET/am-market/homepage/getStartPage", ads,
                AppAdsCustomizeResponse.class);
        if (response != null) {
            return response.getResultList();
        }
        return null;
    }

    @Override
    public ActivityListVO selectActivityList(Integer activityId) {
        ActivityListResponse response = restTemplate
                .getForEntity("http://AM-MARKET/am-market/activity/selectActivity/" + activityId,
                        ActivityListResponse.class)
                .getBody();
        if (Response.isSuccess(response)) {
            return response.getResult();
        }
        return null;
    }

    @Override
    public boolean insertActivityUserGuess(int userId, int grade) {
        BooleanResponse response = restTemplate.getForObject(
                "http://AM-MARKET/am-market/activity/guess/insert/" + userId + "/" + grade,
                BooleanResponse.class);
		if (response != null && response.getResultBoolean() != null) {
			return response.getResultBoolean();
		}
		return false;
    }

    @Override
    public ActivityUserGuessVO selectActivityUserGuess(int userId) {
        ActivityUserGuessResponse response = restTemplate.getForObject(
                "http://AM-MARKET/am-market/activity/guess/select/" + userId,
                ActivityUserGuessResponse.class);
        if (response != null && response.getResult() != null) {
            return response.getResult();
        }
        return null;
    }

    @Override
	public boolean insertActivityUserReward(int userId, int activityId, int grade, String rewardName, String rewardType) {
		ActivityUserRewardVO vo = new ActivityUserRewardVO();
        vo.setActivityId(activityId);
        vo.setRewardName(rewardName);
        vo.setRewardType(rewardType);
        vo.setUserId(userId);
        vo.setSendType("系统发放");
        vo.setSendStatus(1);
        vo.setGrade(grade);
		BooleanResponse response = restTemplate.postForObject("http://AM-MARKET/am-market/activity/reward/insert", vo,
				BooleanResponse.class);
		if (response != null && response.getResultBoolean() != null) {
			return response.getResultBoolean();
		}
		return false;
    }

    @Override
	public List<ActivityUserRewardVO> selectActivityUserReward(int activityId, int userId, int grade) {
		ActivityUserRewardResponse response = restTemplate.getForEntity(
				"http://AM-MARKET/am-market/activity/reward/select/" + activityId + "/" + userId + "/" + grade,
				ActivityUserRewardResponse.class).getBody();
		if (Response.isSuccess(response)) {
			return response.getResultList();
		}
		return null;
	}
}
