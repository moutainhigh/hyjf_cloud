package com.hyjf.am.trade.service.front.wrb;

import com.hyjf.am.vo.trade.wrb.WrbTenderNotifyCustomizeVO;

/**
 * @Auther: walter.limeng
 * @Date: 2018/7/20 14:16
 * 风车理财回调处理
 * @Description: WrbCallBackService
 */
public interface WrbCallBackService {
    /**
     * @Author walter.limeng
     * @Description  风车理财根据出借订单号查询出借信息
     * @Date 14:21 2018/7/20
     * @Param nid
     * @Param userId
     * @return
     */
    WrbTenderNotifyCustomizeVO searchBorrowTenderByNid(String nid, Integer userId);
}
