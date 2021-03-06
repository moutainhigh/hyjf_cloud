/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.admin.finance;

import com.hyjf.am.resquest.admin.PushMoneyRequest;
import com.hyjf.am.trade.dao.model.auto.BorrowApicron;
import com.hyjf.am.trade.dao.model.auto.PushMoney;
import com.hyjf.am.trade.dao.model.customize.PushMoneyCustomize;

import java.util.List;
import java.util.Map;

/**
 * @author zdj
 * @version PushMoneyManageService, v0.1 2018/7/4 15:34
 */
public interface PushMoneyManageService {
    /**
     * 提成管理 （列表）
     *
     * @return
     */
    List<PushMoneyCustomize> selectPushMoneyList(PushMoneyRequest request);

    /**
     * 提成管理 （列表条数）
     *
     * @return
     */
    int countPushMoney(PushMoneyRequest request);

    int getPushMoneyListCount(PushMoneyRequest request);

    List<PushMoneyCustomize> searchPushMoneyList(PushMoneyRequest request);

    Map<String,Object> queryPushMoneyTotle(PushMoneyRequest request);

    /**
     * 根据userid查询 crm  cuttype
     * @auth sunpeikai
     * @param
     * @return
     */
    int queryCrmCuttype(Integer userId);

    /**
     * 发提成
     * @auth sunpeikai
     * @param
     * @return
     */
    int updateTenderCommissionRecord(PushMoneyRequest request);

    /**取得借款API表
    * @author Zha Daojian
    * @date 2018/10/24 19:33
    * @param borrowNid
    * @return java.util.List<com.hyjf.am.trade.dao.model.auto.BorrowApicron>
    **/
    List<BorrowApicron> selectBorrowApicronListByBorrowNid(String borrowNid);

    /** 取得提成配置
    * @author Zha Daojian
    * @date 2018/11/7 11:13
    * @param request
    * @return com.hyjf.am.trade.dao.model.auto.PushMoney
    **/
    List<PushMoney> getPushMoney(PushMoneyRequest request);
}
