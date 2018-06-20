package com.hyjf.cs.trade.client.impl;

import com.hyjf.am.response.user.BankCardResponse;
import com.hyjf.am.response.user.BankOpenAccountResponse;
import com.hyjf.am.response.user.UserEvalationResultResponse;
import com.hyjf.am.response.user.UserInfoResponse;
import com.hyjf.am.resquest.user.BankOpenRequest;
import com.hyjf.am.vo.user.BankCardVO;
import com.hyjf.am.vo.user.BankOpenAccountVO;
import com.hyjf.am.vo.user.UserEvalationResultVO;
import com.hyjf.am.vo.user.UserInfoVO;
import com.hyjf.cs.trade.client.BankOpenClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


/**
 * @Description 
 * @Author pangchengchao
 * @Version v0.1
 * @Date
 */
@Service
public class BankOpenClientImpl implements BankOpenClient {
	private static Logger logger = LoggerFactory.getLogger(BankOpenClient.class);

	@Autowired
	private RestTemplate restTemplate;

	
	@Override
	public UserInfoVO findUserInfoByCardNo(String cradNo) {
		UserInfoResponse response = restTemplate
				.getForEntity("http://AM-USER/am-user/bankopen/findByCardId/" + cradNo, UserInfoResponse.class).getBody();
		if (response != null) {
			return response.getResult();
		}
		return null;
	}

	
	@Override
	public int updateUserAccountLog(BankOpenRequest request) {
		Integer result = restTemplate
				.postForEntity("http://AM-USER/am-user/bankopen/updateUserAccountLog", request, Integer.class).getBody();
		if (result != null) {
			return result;
		}
		return 0;
	}

	@Override
	public BankOpenAccountVO selectById(int userId) {
		BankOpenAccountResponse response = restTemplate
				.getForEntity("http://AM-USER/am-user/bankopen/selectById/" + userId, BankOpenAccountResponse.class).getBody();
		if (response != null) {
			return response.getResult();
		}
		return null;
	}

	@Override
	public BankOpenAccountVO selectByAccountId(String accountId) {
		BankOpenAccountResponse response = restTemplate
				.getForEntity("http://AM-USER/am-user/bankopen/selectByAccountId/" + accountId, BankOpenAccountResponse.class).getBody();
		if (response != null) {
			return response.getResult();
		}
		return null;
	}

	@Override
	public UserEvalationResultVO selectUserEvalationResultByUserId(Integer userId) {
		UserEvalationResultResponse response = restTemplate
				.getForEntity("http://AM-USER/am-user/user/selectUserEvalationResultByUserId/" + userId, UserEvalationResultResponse.class).getBody();
		if (response != null) {
			return response.getResult();
		}
		return null;
	}


	@Override
	public BankCardVO selectBankCardByUserId(Integer userId) {
		BankCardResponse response = restTemplate
				.getForEntity("http://AM-USER/am-user/bankopen/selectByUserId/" + userId, BankCardResponse.class).getBody();
		if (response != null) {
			return response.getResult();
		}
		return null;
	}
}