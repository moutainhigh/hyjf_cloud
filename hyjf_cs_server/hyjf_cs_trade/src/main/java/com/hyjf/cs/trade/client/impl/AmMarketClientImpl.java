package com.hyjf.cs.trade.client.impl;

import com.hyjf.am.response.market.InvitePrizeConfResponse;
import com.hyjf.am.resquest.trade.InvitePrizeConfVO;
import com.hyjf.common.annotation.Cilent;
import com.hyjf.cs.trade.client.AmMarketClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @Auther: walter.limeng
 * @Date: 2018/7/16 19:21
 * @Description: InvitePrizeConfigClientImpl
 */
@Cilent
public class AmMarketClientImpl implements AmMarketClient {
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public List<InvitePrizeConfVO> getListByGroupCode(String groupCode) {
        String url = "http://AM-MARKET/am-market/inviteprizeconfig/getlistbygroupcode/"+groupCode;
        InvitePrizeConfResponse response = restTemplate.getForEntity(url,InvitePrizeConfResponse.class).getBody();
        if (response != null) {
            return response.getResultList();
        }
        return null;
    }
}
