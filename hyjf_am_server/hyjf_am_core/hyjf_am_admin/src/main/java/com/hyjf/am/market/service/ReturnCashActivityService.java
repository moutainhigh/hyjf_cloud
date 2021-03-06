/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.market.service;

import com.hyjf.am.market.dao.model.auto.InviterReturnDetail;
import com.hyjf.am.market.dao.model.auto.NmUser;
import com.hyjf.am.market.dao.model.auto.PerformanceReturnDetail;
import com.hyjf.am.resquest.market.InviterReturnCashCustomize;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @author tyy
 * @version ReturnCashActivityService, v0.1 2018/12/29 15:48
 */
public interface ReturnCashActivityService {

    Map<String,Object> selectReturnCash(Integer userId, String orderId, Integer productType, BigDecimal investMoney, InviterReturnCashCustomize inviterReturnCashCustomize, List<NmUser> nmUserList);

    InviterReturnCashCustomize selectReturnCashList(Integer userId);

    List<InviterReturnDetail> selectInviterReturnDetailList(String borrowNid);

    List<PerformanceReturnDetail> selectPerformanceReturnDetailList(String borrowNid);

}
