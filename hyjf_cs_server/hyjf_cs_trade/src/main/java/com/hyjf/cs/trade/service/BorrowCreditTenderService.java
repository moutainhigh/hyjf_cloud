package com.hyjf.cs.trade.service;

import com.hyjf.am.resquest.trade.TenderRequest;
import com.hyjf.am.vo.user.WebViewUserVO;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.bean.BankCallResult;

import java.util.Map;

/**
 * @Description 债转投资
 * @Author sunss
 * @Date 2018/7/3 15:06
 */
public interface BorrowCreditTenderService extends BaseTradeService{

    /**
     * 债转投资
     * @param request
     * @return
     */
    WebResult<Map<String,Object>> borrowCreditTender(TenderRequest request);
}
