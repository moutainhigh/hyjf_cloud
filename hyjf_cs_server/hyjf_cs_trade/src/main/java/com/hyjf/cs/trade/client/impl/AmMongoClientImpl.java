package com.hyjf.cs.trade.client.impl;

import com.hyjf.am.response.app.AppChannelStatisticsDetailResponse;
import com.hyjf.am.vo.datacollect.AppChannelStatisticsDetailVO;
import com.hyjf.cs.trade.client.AmMongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * @Description mongo查询类
 * @Author sunss
 * @Date 2018/6/23 9:29
 */
@Service
public class AmMongoClientImpl implements AmMongoClient {
    @Autowired
    private RestTemplate restTemplate;

    /**
     * 根据userId查询用户渠道信息
     *
     * @param userId
     * @return
     */
    @Override
    public AppChannelStatisticsDetailVO getAppChannelStatisticsDetailByUserId(Integer userId) {
        AppChannelStatisticsDetailResponse response = restTemplate.getForEntity(
                "http://CS-MESSAGE/cs-message/search/getAppChannelStatisticsDetailByUserId/" + userId,
                AppChannelStatisticsDetailResponse.class).getBody();
        if (response != null) {
            return response.getResult();
        }
        return null;
    }
}
