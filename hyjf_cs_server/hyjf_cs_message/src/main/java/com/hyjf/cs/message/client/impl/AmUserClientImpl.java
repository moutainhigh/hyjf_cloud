package com.hyjf.cs.message.client.impl;

import com.hyjf.am.response.IntegerResponse;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.user.*;
import com.hyjf.am.resquest.message.FindAliasesForMsgPushRequest;
import com.hyjf.am.vo.admin.AdminMsgPushCommonCustomizeVO;
import com.hyjf.am.vo.user.*;
import com.hyjf.cs.message.client.AmUserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;

/**
 * @author xiasq
 * @version AmUserClientImpl, v0.1 2018/4/19 12:44
 */
@Service
public class AmUserClientImpl implements AmUserClient {

	@Autowired
	private RestTemplate restTemplate;

	@Value("${am.user.service.name}")
	private String amUserServiceName;

	/**
	 * 根据手机号查询用户
	 * 
	 * @param mobile
	 * @return
	 */
	@Override
	public UserVO findUserByMobile(final String mobile) {
		UserResponse response = restTemplate
				.getForEntity("http://AM-USER/am-user/user/findByMobile/" + mobile, UserResponse.class).getBody();
		if (response != null) {
			return response.getResult();
		}
		return null;
	}

	/**
	 * 根据userId查询用户
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public UserVO findUserById(final int userId) {
		UserResponse response = restTemplate
				.getForEntity("http://AM-USER/am-user/user/findById/" + userId, UserResponse.class).getBody();
		if (response != null) {
			return response.getResult();
		}
		return null;
	}

	/**
	 * 根据userId查询用户信息
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public UserInfoVO findUsersInfoById(int userId) {
		UserInfoResponse response = restTemplate
				.getForEntity("http://AM-USER/am-user/userInfo/findById/" + userId, UserInfoResponse.class).getBody();
		if (response != null) {
			return response.getResult();
		}
		return null;
	}

	/**
	 * 根据手机号查询推送别名
	 * 
	 * @param mobile
	 * @return
	 */
	@Override
	public UserAliasVO findAliasByMobile(final String mobile) {
		UserAliasResponse response = restTemplate
				.getForEntity("http://AM-USER/am-user/userAlias/findAliasByMobile/" + mobile, UserAliasResponse.class)
				.getBody();
		if (response != null) {
			return response.getResult();
		}
		return null;
	}

	/**
	 * 根据手机号查询推送别名 - 批量
	 * 
	 * @param mobiles
	 * @return
	 */
	@Override
	public List<UserAliasVO> findAliasesByMobiles(final List<String> mobiles) {
		FindAliasesForMsgPushRequest request = new FindAliasesForMsgPushRequest();
		request.setMobiles(mobiles);
		UserAliasResponse response = restTemplate
				.postForEntity("http://AM-USER/am-user/userAlias/findAliasesByMobiles",request, UserAliasResponse.class)
				.getBody();
		if (response != null) {
			return response.getResultList();
		}
		return null;
	}

	/**
	 * 根据设备类型统计用户人数
	 * 
	 * @param clientAndroid
	 * @return
	 */
	@Override
	public int countAliasByClient(String clientAndroid) {
		UserAliasResponse response = restTemplate
				.getForEntity("http://AM-USER/am-user/user/findSmsTemplateByCode/" + clientAndroid,
						UserAliasResponse.class)
				.getBody();
		if (response != null) {
			return response.getCount();
		}
		return 0;
	}
	/**
	 * 查看用户详情
	 * @param userId
	 * @return
	 */
	@Override
	public UserInfoCustomizeVO queryUserInfoCustomizeByUserId(Integer userId) {
		String url = "http://AM-USER/am-user/userInfo/queryUserInfoCustomizeByUserId/" + userId;
		UserInfoCustomizeResponse response = restTemplate.getForEntity(url,UserInfoCustomizeResponse.class).getBody();
		if (response!=null){
			return response.getResult();
		}
		return null;
	}

	/**
	 * 通过手机号获取设备标识码
	 *
	 * @param mobile
	 * @return
	 */
	@Override
	public AdminMsgPushCommonCustomizeVO getMobileCodeByNumber(String mobile) {
		String url = "http://AM-USER/am-user/userInfo/getMobileCodeByNumber/" + mobile;
		AdminMsgPushCommonCustomizeVO response = restTemplate.getForEntity(url,AdminMsgPushCommonCustomizeVO.class).getBody();
		return response;
	}


	/**
	 * 获取用户表总记录数
	 *
	 * @return
	 */
	@Override
	public Integer countAllUser(){
		UserResponse response = restTemplate.getForEntity("http://AM-USER/am-user/user/countAll",UserResponse.class).getBody();
		if (!Response.isSuccess(response)) {
			return 0;
		}
		return response.getCount();
	}

	/**
	 * 根据userId查询用户推广链接注册
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public UtmRegVO findUtmRegByUserId(Integer userId) {
		String url = "http://AM-USER/am-user/user/findUtmRegByUserId/" + userId;
		UtmRegResponse response = restTemplate.getForEntity(url, UtmRegResponse.class).getBody();
		if (response != null) {
			return response.getResult();
		}
		return null;
	}

	/**
	 * 检查用户是不是新手
	 * @param userId
	 * @return
	 */
	@Override
	public int countNewUserTotal(Integer userId) {
		IntegerResponse result = restTemplate
				.getForEntity("http://AM-USER/am-user/user/countNewUserTotal/" + userId,  IntegerResponse.class).getBody();
		if (IntegerResponse.isSuccess(result)) {
			return result.getResultInt();
		}
		return 0;
	}

	/**
	 * 更新用户首次投资信息
	 *
	 * @param params
	 */
	@Override
	public Integer updateFirstUtmReg(HashMap<String, Object> params) {
		IntegerResponse result = restTemplate.postForEntity("http://AM-USER/am-user/user/updateFirstUtmReg",params,IntegerResponse.class).getBody();
		if (IntegerResponse.isSuccess(result)) {
			return result.getResultInt();
		}
		return 0;
	}
}
