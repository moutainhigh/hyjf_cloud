/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.trade.service.recharge;

import com.hyjf.cs.trade.bean.UserDirectRechargeRequestBean;
import com.hyjf.cs.trade.service.BaseTradeService;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.bean.BankCallResult;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author: sunpeikai
 * @version: DirectRechargeService, v0.1 2018/8/28 19:29
 */
public interface DirectRechargeService extends BaseTradeService {
    Map<String,Object> recharge(UserDirectRechargeRequestBean userRechargeRequestBean, HttpServletRequest request);
    Map<String,Object> pageReturn(HttpServletRequest request, BankCallBean bean);
    BankCallResult bgreturn(HttpServletRequest request, BankCallBean bean);
}
