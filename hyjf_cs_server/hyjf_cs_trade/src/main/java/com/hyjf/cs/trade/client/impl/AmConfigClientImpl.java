package com.hyjf.cs.trade.client.impl;

import com.hyjf.am.response.Response;
import com.hyjf.am.response.config.FeeConfigResponse;
import com.hyjf.am.response.config.SmsConfigResponse;
import com.hyjf.am.response.trade.BankCardBeanResponse;
import com.hyjf.am.response.trade.BankReturnCodeConfigResponse;
import com.hyjf.am.response.trade.BanksConfigResponse;
import com.hyjf.am.response.user.QuestionCustomizeResponse;
import com.hyjf.am.resquest.user.AnswerRequest;
import com.hyjf.am.vo.bank.BankCallBeanVO;
import com.hyjf.am.vo.config.FeeConfigVO;
import com.hyjf.am.vo.config.SmsConfigVO;
import com.hyjf.am.vo.trade.BankReturnCodeConfigVO;
import com.hyjf.am.vo.trade.BanksConfigVO;
import com.hyjf.am.vo.trade.account.AccountWithdrawVO;
import com.hyjf.am.vo.user.QuestionCustomizeVO;
import com.hyjf.cs.trade.client.AmConfigClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * 配置中心请求
 */
@Service
public class AmConfigClientImpl implements AmConfigClient {
    private static Logger logger = LoggerFactory.getLogger(AmConfigClientImpl.class);

    @Autowired
    private RestTemplate restTemplate;


    @Override
    public BankReturnCodeConfigVO getBankReturnCodeConfig(String retCode) {
        BankReturnCodeConfigResponse response = restTemplate
                .getForEntity("http://AM-CONFIG/am-config/config/getBankReturnCodeConfig/"+retCode,BankReturnCodeConfigResponse.class).getBody();
        if (response != null) {
            return response.getResult();
        }
        return null;
    }

	/**
	 * 调用银行接口
	 */
	@Override
	public BankCallBeanVO bankCallFundTransQuery(AccountWithdrawVO accountWithdraw) {
		BankCardBeanResponse response = restTemplate
				.postForEntity("http://AM-CONFIG/am-config/config/bankCallFundTransQuery", accountWithdraw,
						BankCardBeanResponse.class)
				.getBody();
		if (response != null) {
			return response.getResult();
		}
		return null;
	}

	/**
	 * 提现费率
	 */
	@Override
	public List<FeeConfigVO> getFeeConfig(String bankCode) {
		String url = "http://AM-CONFIG/am-config/feeConfig/getFeeConfig/"+bankCode;
		FeeConfigResponse response = restTemplate.getForEntity(url, FeeConfigResponse.class).getBody();
		if (response != null) {
			return response.getResultList();
		}
		return null;
	}

}
