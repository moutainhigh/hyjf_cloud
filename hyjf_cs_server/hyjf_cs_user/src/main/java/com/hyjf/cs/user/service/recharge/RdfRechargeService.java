package com.hyjf.cs.user.service.recharge;

import com.hyjf.am.vo.user.UserVO;
import com.hyjf.cs.user.service.BaseUserService;

/**
 * 融东风用户充值接口
 */
public interface RdfRechargeService extends BaseUserService {

	/**
	 * 根据手机号获取用户信息
	 * @param mobile
	 * @return
	 */
	UserVO findUserByMobile(String mobile);
	


}
