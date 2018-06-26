package com.hyjf.cs.trade.client.impl;

import com.hyjf.am.response.Response;
import com.hyjf.am.response.trade.BorrowUserResponse;
import com.hyjf.am.vo.trade.borrow.BorrowUserVO;
import com.hyjf.cs.trade.client.AmBorrowUserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class AmBorrowUserClientImpl implements AmBorrowUserClient {


    @Autowired
    private RestTemplate restTemplate;

    public BorrowUserVO getBorrowUser(String borrowNid) {
        BorrowUserResponse response = restTemplate.getForEntity(
                "http://AM-TRADE/am-trade/borrowuser/borrowuserinfo/" + borrowNid,
                BorrowUserResponse.class).getBody();
        if (Response.isSuccess(response)) {
            return response.getResult();
        }
        return null;
    }
}