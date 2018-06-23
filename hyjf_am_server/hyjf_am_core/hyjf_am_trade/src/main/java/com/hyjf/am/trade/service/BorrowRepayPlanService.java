/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service;

import com.hyjf.am.trade.dao.model.auto.BorrowRepayPlan;
import com.hyjf.am.vo.trade.borrow.BorrowRepayPlanVO;

import java.util.List;

/**
 * @author PC-LIUSHOUYI
 * @version BorrowRepayPlanService, v0.1 2018/6/23 11:42
 */
public interface BorrowRepayPlanService {

    /**
     * 标的还款记录分期(借款人)
     * @return
     */
    List<BorrowRepayPlan> selectBorrowRepayPlanList(String borrowNid, Integer repaySmsReminder);

    /**
     * 更新标的还款记录分期(借款人)
     * @param borrowRepayPlan
     * @return
     */
    Integer updateBorrowRepayPlan(BorrowRepayPlan borrowRepayPlan);
}
