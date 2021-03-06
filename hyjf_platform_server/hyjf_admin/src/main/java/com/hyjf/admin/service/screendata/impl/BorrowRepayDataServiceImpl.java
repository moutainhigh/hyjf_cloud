package com.hyjf.admin.service.screendata.impl;

import com.hyjf.admin.client.AmAdminClient;
import com.hyjf.admin.client.AmTradeClient;
import com.hyjf.admin.service.screendata.BorrowRepayDataService;
import com.hyjf.am.response.IntegerResponse;
import com.hyjf.am.response.trade.RepayResponse;
import com.hyjf.am.resquest.trade.ScreenDataBean;
import com.hyjf.am.vo.trade.RepaymentPlanVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lisheng
 * @version BorrowRepayDataServiceImpl, v0.1 2019/3/20 14:48
 */
@Service
public class BorrowRepayDataServiceImpl implements BorrowRepayDataService {
    @Autowired
    AmAdminClient adminClient;
    @Override
    public RepayResponse findRepayUser(Integer startTime, Integer endTime, Integer currPage, Integer pageSize) {
        return adminClient.findRepayUser(startTime,endTime,currPage,pageSize);
    }

    @Override
    public void addRepayUserList(List<RepaymentPlanVO> resultList) {
        adminClient.addRepayUserList(resultList);
    }

    @Override
    public IntegerResponse countRepayUserList() {
        return adminClient.countRepayUserList();
    }

    @Override
    public List<ScreenDataBean> getRechargeList(Integer startIndex, Integer endIndex) {
        return adminClient.getRechargeList(startIndex, endIndex);
    }
    @Override
    public List<ScreenDataBean> getPlanTenderList(Integer startIndex, Integer endIndex) {
        return adminClient.getPlanTenderList(startIndex, endIndex);
    }
    @Override
    public List<ScreenDataBean> getPlanRepayList(Integer startIndex, Integer endIndex) {
        return adminClient.getPlanRepayList(startIndex, endIndex);
    }
    @Override
    public List<ScreenDataBean> getCreditTenderList(Integer startIndex, Integer endIndex) {
        return adminClient.getCreditTenderList(startIndex, endIndex);
    }

    @Override
    public List<ScreenDataBean> getWithdrawList(Integer startIndex, Integer endIndex) {
        return adminClient.getWithdrawList(startIndex, endIndex);
    }

    @Override
    public List<ScreenDataBean> getBorrowRecoverList(Integer startIndex, Integer endIndex) {
        return adminClient.getBorrowRecoverList(startIndex, endIndex);
    }

    @Override
    public List<ScreenDataBean> getBorrowTenderList(Integer startIndex, Integer endIndex) {
        return adminClient.getBorrowTenderList(startIndex, endIndex);
    }

}
