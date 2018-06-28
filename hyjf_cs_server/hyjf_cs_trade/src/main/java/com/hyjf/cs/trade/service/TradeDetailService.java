package com.hyjf.cs.trade.service;

import com.hyjf.am.resquest.trade.TradeDetailBeanRequest;
import com.hyjf.am.vo.trade.AccountTradeVO;
import com.hyjf.cs.trade.bean.TradeDetailBean;

import java.util.List;

/**
 * @author pangchengchao
 * @version TradeDetailService, v0.1 2018/6/27 10:12
 */
public interface TradeDetailService extends BaseTradeService{
    List<AccountTradeVO> selectTradeTypes();

    TradeDetailBean searchUserTradeList(TradeDetailBeanRequest form);

    TradeDetailBean searchUserRechargeList(TradeDetailBeanRequest form);

    TradeDetailBean searchUserWithdrawList(TradeDetailBeanRequest form);
}
