package com.hyjf.cs.user.service.bankopen;

import com.hyjf.am.vo.user.UserVO;
import com.hyjf.cs.user.bean.ApiBankOpenRequestBean;
import com.hyjf.cs.user.bean.OpenAccountPageBean;
import com.hyjf.cs.user.result.AppResult;
import com.hyjf.cs.user.service.BaseService;
import com.hyjf.cs.user.vo.BankOpenVO;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.bean.BankCallResult;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

/**
 * 
 * @author Administrator
 *
 */

public interface BankOpenService extends BaseService {

	boolean existUser(String mobile);

	boolean checkIdNo(String idNo);

	int updateUserAccountLog(int userId, String userName, String mobile, String logOrderId, String clientPc, String name,
			String idno, String cardNo);

	void checkRequestParam(UserVO user, BankOpenVO bankOpenVO);

	ModelAndView getOpenAccountMV(OpenAccountPageBean openBean);

	BankCallResult openAccountBgReturn(BankCallBean bean);

	Map<String,String> openAccountReturn(String token, String isSuccess);

    Map<String,String> checkApiParam(ApiBankOpenRequestBean requestBean);
}
