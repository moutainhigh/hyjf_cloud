/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.service.htltrade.impl;

import com.hyjf.am.resquest.user.HtlTradeRequest;
import com.hyjf.am.vo.trade.HtlProductIntoRecordVO;
import com.hyjf.am.vo.trade.HtlProductRedeemVO;
import com.hyjf.cs.common.service.BaseServiceImpl;
import com.hyjf.cs.user.client.AmTradeClient;
import com.hyjf.cs.user.client.AmUserClient;
import com.hyjf.cs.user.service.htltrade.HtlTradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author: sunpeikai
 * @version: HtlTradeServiceImpl, v0.1 2018/7/25 15:17
 */
@Service
public class HtlTradeServiceImpl extends BaseServiceImpl implements HtlTradeService {

    @Autowired
    private AmTradeClient amTradeClient;
    @Autowired
    private AmUserClient amUserClient;

    /**
     * 获得购买列表数
     * @param htlTradeRequest
     * @return
     */
    @Override
    public Integer countHtlIntoRecord(HtlTradeRequest htlTradeRequest) {
        return amTradeClient.countHtlIntoRecord(htlTradeRequest);
    }
    /**
     * 获取购买产品列表
     * @param htlTradeRequest
     * @return
     */
    @Override
    public List<HtlProductIntoRecordVO> getIntoRecordList(HtlTradeRequest htlTradeRequest) {
        //TODO:这里还会用到amUser
        return amTradeClient.getIntoRecordList(htlTradeRequest);
    }
    /**
     * 获得汇天利转出列表数
     * @param htlTradeRequest
     * @return
     */
    @Override
    public Integer countProductRedeemRecord(HtlTradeRequest htlTradeRequest) {
        return amTradeClient.countProductRedeemRecord(htlTradeRequest);
    }
    /**
     * 获取汇天利转出记录列表(自定义)
     * @param htlTradeRequest
     * @return
     */
    @Override
    public List<HtlProductRedeemVO> getRedeemRecordList(HtlTradeRequest htlTradeRequest) {
        //TODO:这里还会用到amUser
        return amTradeClient.getRedeemRecordList(htlTradeRequest);
    }
}
