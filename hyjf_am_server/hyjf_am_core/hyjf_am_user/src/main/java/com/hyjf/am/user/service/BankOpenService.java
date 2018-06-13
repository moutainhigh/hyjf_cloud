package com.hyjf.am.user.service;

import com.hyjf.am.resquest.user.BankCardRequest;
import com.hyjf.am.user.dao.model.auto.*;

public interface BankOpenService {
	
	/**
	 * 保存用户的初始开户记录
	 * @param userId
	 * @param userName
	 * @param
	 * @param clientPc
	 * @param
	 * @return
	 */
	public boolean updateUserAccountLog(int userId, String userName, String mobile, String logOrderId, String clientPc,String name,String idno,String cardNo);


    
    /**
     * 更新开户日志表
     *
     * @param userId
     * @param logOrderId
     * @param status
     */
    void updateUserAccountLog(Integer userId, String logOrderId, int status);


    boolean updateUserAccount(Integer userId,String trueName,  String orderId, String accountId, String idNo,Integer bankAccountEsb,String mobile);



	UserInfo findUserInfoByCradId(String cardNo);

	BankOpenAccount selectByExample(BankOpenAccountExample example);

	/**
	 * 根据用户Id,银行卡号检索用户银行卡信息
	 * @param userId
	 * @return
	 */
	BankCard selectBankCardByUserId(Integer userId);

	BankCard getBankCardByCardNo(Integer userId, String cardNo);
	/**
	 * 根据用户ID查询企业用户信息
	 * @param userId
	 * @return
	 */
	CorpOpenAccountRecord getCorpOpenAccountRecord(Integer userId);

	/**
	 * 开户成功后保存用户银行卡信息
	 * @param request
	 * @return
	 */
	boolean saveCardNoToBank(BankCardRequest request);
}
