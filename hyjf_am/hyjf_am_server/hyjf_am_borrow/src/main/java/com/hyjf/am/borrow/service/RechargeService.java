package com.hyjf.am.borrow.service;

import com.hyjf.am.borrow.dao.model.auto.Account;
import com.hyjf.am.borrow.dao.model.auto.AccountList;
import com.hyjf.am.borrow.dao.model.auto.AccountRecharge;
import com.hyjf.am.borrow.dao.model.auto.AccountRechargeExample;
import com.hyjf.am.resquest.user.BankRequest;
import com.hyjf.am.vo.borrow.AccountRechargeVO;
import com.hyjf.am.vo.user.BankCallVO;

/**
 * 用户充值Service
 * 
 * @author
 *
 */
public interface RechargeService {

    int selectByOrdId(String ordId);

    int insertSelective(BankRequest bankRequest);

	 AccountRecharge selectByExample(AccountRechargeExample example);

	 int updateByExampleSelective(AccountRechargeVO accountRecharge,AccountRechargeExample accountRechargeExample);

	 int updateBankRechargeSuccess(Account newAccount);

	 int insertSelective(AccountList accountList);

	 void updateByPrimaryKeySelective(AccountRecharge accountRecharge);

	boolean updateBanks(AccountRechargeVO accountRecharge, BankCallVO bean, String ip);
}
