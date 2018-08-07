/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.client.impl;

import com.hyjf.admin.client.CsMessageClient;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.AccountWebListResponse;
import com.hyjf.am.response.admin.AssociatedRecordListResponse;
import com.hyjf.am.resquest.admin.AssociatedRecordListRequest;
import com.hyjf.am.vo.admin.AssociatedRecordListVo;
import com.hyjf.am.vo.datacollect.AccountWebListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author zhangqingqing
 * @version AmDataCollect, v0.1 2018/6/25 10:26
 *
 * todo 这个类全部错了，都要改 ！！！！！！！！！！！！！！！！！！！！！！！！
 */
@Service
public class  CsMessageClientImpl  implements CsMessageClient {
    @Autowired
    private RestTemplate restTemplate;


    @Override
    public AccountWebListResponse queryAccountWebList(AccountWebListVO accountWebList) {
        AccountWebListResponse response = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/search/queryAccountWebList", accountWebList, AccountWebListResponse.class)
                .getBody();
        if (response != null) {
            return response;
        }
        return null;
    }

    @Override
    public String selectBorrowInvestAccount(AccountWebListVO accountWebList) {
        String response = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/search/selectBorrowInvestAccount", accountWebList, String.class)
                .getBody();
        if (response != null) {
            return response;
        }
        return null;
    }


    /**
     * 查询关联记录列表count
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public Integer getAssociatedRecordsCount(AssociatedRecordListRequest request) {
        Integer count = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/search/getassociatedrecordscount", request, Integer.class)
                .getBody();

        return count;
    }

    /**
     * 根据筛选条件查询关联记录list
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public List<AssociatedRecordListVo> getAssociatedRecordList(AssociatedRecordListRequest request) {
        AssociatedRecordListResponse response = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/search/searchassociatedrecordlist", request, AssociatedRecordListResponse.class)
                .getBody();
        if(Response.isSuccess(response)){
            return response.getResultList();
        }
        return null;
    }

    @Override
    public String getRetCode(String logOrdId) {
        String response = restTemplate.getForEntity("http://CS-MESSAGE/cs-message/search/getRetCode/" + logOrdId,String.class).getBody();
        return response;
    }

}