package com.hyjf.cs.user.client;

import com.hyjf.am.resquest.user.BankCardLogRequest;
import com.hyjf.am.resquest.user.BankCardRequest;
import com.hyjf.am.vo.trade.account.AccountVO;
import com.hyjf.am.vo.trade.BanksConfigVO;
import com.hyjf.am.vo.user.BankCardVO;

public interface BindCardClient {

	int insertBindCardLog(BankCardLogRequest request);

	String queryBankIdByCardNo(String cardNo);

	int insertUserCard(BankCardRequest request);

	int deleteUserCardByUserId(String userId);

	int countUserCardValid(String userId);

	BankCardVO queryUserCardValid(String userId, String cardNo);

	BanksConfigVO getBanksConfigByBankId(String bankId);

	int updateUserCard(BankCardRequest request);

	int deleteUserCardByCardNo(String cardNo);

	AccountVO getAccount(Integer userId);
}

	