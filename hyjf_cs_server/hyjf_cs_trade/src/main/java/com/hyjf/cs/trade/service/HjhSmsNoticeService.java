package com.hyjf.cs.trade.service;
/**
 * 还款逾期短信提醒
 * @author jun 20180626
 */
public interface HjhSmsNoticeService extends BaseTradeService {

    public void overdueSmsNotice();
    
}
