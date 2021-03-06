package com.hyjf.cs.trade.service.myasset.impl;

import com.hyjf.am.resquest.trade.WechatMyProjectRequest;
import com.hyjf.am.vo.trade.assetmanage.QueryMyProjectVO;
import com.hyjf.cs.trade.service.impl.BaseTradeServiceImpl;
import com.hyjf.cs.trade.service.myasset.WechatMyProjectService;
import org.springframework.stereotype.Service;

/**
 * @author pangchengchao
 * @version WechatMyProjectServiceImpl, v0.1 2018/7/24 14:05
 */
@Service
public class WechatMyProjectServiceImpl extends BaseTradeServiceImpl implements WechatMyProjectService {
    @Override
    public QueryMyProjectVO selectCurrentHoldObligatoryRightList(Integer userId, int currentPage, int pageSize, QueryMyProjectVO vo) {
        WechatMyProjectRequest request=createWechatMyProjectRequest(userId,currentPage,pageSize);
        request.setOrderByFlag("2");
        request.setSortBy("DESC");
        return amTradeClient.selectWechatCurrentHoldObligatoryRightList(request);
    }

    @Override
    public QueryMyProjectVO selectRepaymentList(Integer userId, int currentPage, int pageSize, QueryMyProjectVO vo) {
        WechatMyProjectRequest request=createWechatMyProjectRequest(userId,currentPage,pageSize);
        return amTradeClient.selectWechatRepaymentList(request);
    }

    @Override
    public QueryMyProjectVO selectCreditRecordList(Integer userId, int currentPage, int pageSize, QueryMyProjectVO vo) {
        WechatMyProjectRequest request=createWechatMyProjectRequest(userId,currentPage,pageSize);
        return amTradeClient.selectWechatCreditRecordList(request);
    }

    @Override
    public QueryMyProjectVO selectCurrentHoldPlanList(Integer userId, int currentPage, int pageSize, QueryMyProjectVO vo) {
        WechatMyProjectRequest request=createWechatMyProjectRequest(userId,currentPage,pageSize);
        request.setOrderByFlag("2");
        request.setSortBy("DESC");
        return amTradeClient.selectWechatCurrentHoldPlanList(request);
    }

    @Override
    public QueryMyProjectVO selectRepayMentPlanList(Integer userId, int currentPage, int pageSize, QueryMyProjectVO vo) {
        WechatMyProjectRequest request=createWechatMyProjectRequest(userId,currentPage,pageSize);

        request.setOrderByFlag("3");
        request.setSortBy("DESC");
        return amTradeClient.selectWechatRepayMentPlanList(request);
    }


    private WechatMyProjectRequest createWechatMyProjectRequest(Integer userId, int currentPage, int pageSize) {
        WechatMyProjectRequest request=new WechatMyProjectRequest();
        request.setUserId(userId);
        int offSet = (currentPage - 1) * pageSize;
        if (offSet == 0 || offSet > 0) {
            request.setLimitStart(offSet);
        }
        if (pageSize > 0) {
            request.setLimitEnd(pageSize);
        }
        request.setCurrentPage(currentPage);
        request.setPageSize(pageSize);
        return request;
    }

}
