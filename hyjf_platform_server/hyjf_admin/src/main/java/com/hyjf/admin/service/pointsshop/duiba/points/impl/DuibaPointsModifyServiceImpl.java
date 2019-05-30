/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service.pointsshop.duiba.points.impl;

import com.hyjf.admin.client.AmMarketClient;
import com.hyjf.admin.common.service.BaseServiceImpl;
import com.hyjf.admin.service.pointsshop.duiba.points.DuibaPointsModifyService;
import com.hyjf.am.response.admin.DuibaPointsModifyResponse;
import com.hyjf.am.resquest.admin.DuibaPointsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author PC-LIUSHOUYI
 * @version DuibaPointsModifyServiceImpl, v0.1 2019/5/29 9:50
 */
@Service
public class DuibaPointsModifyServiceImpl extends BaseServiceImpl implements DuibaPointsModifyService {

    @Autowired
    AmMarketClient amMarketClient;

    /**
     * 兑吧积分账户修改明细
     *
     * @param requestBean
     * @return
     */
    @Override
    public DuibaPointsModifyResponse selectDuibaPointsModifyList(DuibaPointsRequest requestBean) {
        return amMarketClient.selectDuibaPointsModifyList(requestBean);
    }
}
