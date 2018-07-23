package com.hyjf.am.user.service;

import com.hyjf.am.resquest.user.BankCardUpdateRequest;
import com.hyjf.am.user.dao.model.auto.BankCard;
import com.hyjf.am.user.dao.model.auto.BankCardLog;

/**
 * 绑卡接口
 * @author hesy
 */
public interface BindCardService {

	BankCard queryUserCardValid(Integer userId, String cardNo);

	int insertUserCard(BankCard bankCard);

	int deleteUserCardByUserId(Integer userId);

	int countUserCardValid(Integer userId);

	int deleteUserCardByCardNo(String cardNo);

	int updateUserCard(BankCard bankCard);

	int insertBindCardLog(BankCardLog bankCardLog);
	
	boolean updateBankSmsLog(Integer userId, String srvTxCode, String srvAuthCode);

	String selectBankSmsLog(Integer userId, String srvTxCode, String srvAuthCode);

    boolean updateAfterDeleteCard(BankCardUpdateRequest requestBean) throws Exception;
}
