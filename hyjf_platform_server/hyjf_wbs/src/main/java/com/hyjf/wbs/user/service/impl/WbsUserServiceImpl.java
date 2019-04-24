/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.wbs.user.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.google.common.collect.Maps;
import com.hyjf.am.bean.result.BaseResult;
import com.hyjf.am.resquest.trade.SensorsDataBean;
import com.hyjf.am.vo.admin.UserOperationLogEntityVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.bean.AccessToken;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.constants.UserOperationLogConstant;
import com.hyjf.common.enums.MsgEnum;
import com.hyjf.common.http.HttpClientUtils;
import com.hyjf.common.jwt.Token;
import com.hyjf.common.util.SecretUtil;
import com.hyjf.cs.common.bean.result.ApiResult;
import com.hyjf.wbs.WbsConstants;
import com.hyjf.wbs.client.AmUserClient;
import com.hyjf.wbs.configs.WbsConfig;
import com.hyjf.wbs.exceptions.WbsException;
import com.hyjf.wbs.qvo.*;
import com.hyjf.wbs.sign.WbsSignUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.hyjf.am.vo.user.WebViewUserVO;
import com.hyjf.am.vo.wbs.WbsRegisterMqVO;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.exception.CheckException;
import com.hyjf.common.exception.MQException;
import com.hyjf.wbs.client.CsUserClient;
import com.hyjf.wbs.mq.base.CommonProducer;
import com.hyjf.wbs.mq.base.MessageContent;
import com.hyjf.wbs.qvo.csuser.LoginRequestVO;
import com.hyjf.wbs.user.dao.mapper.auto.UserMapper;
import com.hyjf.wbs.user.dao.model.auto.User;
import com.hyjf.wbs.user.service.WbsUserService;

/**
 * @author cui
 * @version WbsUserServiceImpl, v0.1 2019/4/19 10:20
 */
@Service
public class WbsUserServiceImpl implements WbsUserService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private CsUserClient csUserClient;

	@Autowired
	private UserMapper userMapper;

	@Autowired
	private CommonProducer commonProducer;

	@Autowired
	private AmUserClient amUserClient;

	@Autowired
	private WbsConfig wbsConfig;

	@Override
	public void webBind(WebUserBindQO webUserBindQO, BaseResult response) {
		LoginRequestVO loginQO = new LoginRequestVO();
		loginQO.setUsername(webUserBindQO.getLoginUserName());
		loginQO.setPassword(webUserBindQO.getLoginPassword());
		loginQO.setPresetProps(webUserBindQO.getPresetProps());

		WebViewUserVO webViewUserVO = csUserClient.login(loginQO);

		// 组织返回数据
		response.setStatus("000");
		response.setStatusDesc("授权成功");

		WebUserBindVO bindVO=new WebUserBindVO();
		bindVO.setUsername(webViewUserVO.getUsername());
		bindVO.setMobile(webViewUserVO.getMobile());
		bindVO.setToken(webViewUserVO.getToken());
		bindVO.setRoleId(webViewUserVO.getRoleId());
		//TODO　需要后台指定跳转页面？
//		bindVO.setRetUrl();
		bindVO.setIconUrl(webViewUserVO.getIconUrl());
		bindVO.setUserId(String.valueOf(webViewUserVO.getUserId()));
		response.setData(bindVO);

		WbsRegisterMqVO vo = new WbsRegisterMqVO();
		vo.setAssetCustomerId(String.valueOf(webViewUserVO.getUserId()));
		vo.setUtmId(webUserBindQO.getUtmId());

		try {
			commonProducer.messageSendDelay(new MessageContent(MQConstant.WBS_REGISTER_TOPIC,
					MQConstant.WBS_REGISTER_TAG, UUID.randomUUID().toString(), vo), 1);
		} catch (MQException e) {
			logger.error("登录发送MQ失败！");
			throw new CheckException(e.getMessage());
		}
	}

	@Override
	public void wechatBind(WechatUserBindQO wechatUserBindQO, BaseResult result) {
		WechatUserBindVO bindVO=csUserClient.wechatLogin(wechatUserBindQO.getUsername(),wechatUserBindQO.getPassword());
		result.setData(bindVO);

		WbsRegisterMqVO vo = new WbsRegisterMqVO();
		vo.setAssetCustomerId(String.valueOf(bindVO.getUserId()));
		vo.setUtmId(wechatUserBindQO.getUtmId());

		try {
			commonProducer.messageSendDelay(new MessageContent(MQConstant.WBS_REGISTER_TOPIC,
					MQConstant.WBS_REGISTER_TAG, UUID.randomUUID().toString(), vo), 1);
		} catch (MQException e) {
			logger.error("登录发送MQ失败！");
			throw new CheckException(e.getMessage());
		}


	}

	@Override
	public WbsUserAuthInfo queryUserAuthInfo(String assetCustomerId) {

		WbsUserAuthInfo wbsUserAuthInfo = new WbsUserAuthInfo();

		Integer userId;
		try {
			userId = Integer.parseInt(assetCustomerId);
		} catch (NumberFormatException e) {
			logger.error("客户ID=【{}】不是INTEGER类型", assetCustomerId);
			throw new CheckException("999","客户ID【" + assetCustomerId + "】不是INTEGER类型！");
		}

		User user = userMapper.selectByPrimaryKey(userId);

		if (null == user) {
			logger.error("未找到ID=【" + assetCustomerId + "】的客户信息");
			throw new CheckException("999","未找到ID=【" + assetCustomerId + "】的客户信息");
		}

		wbsUserAuthInfo.setUserName(user.getUsername());
		wbsUserAuthInfo.setMobile(user.getMobile());
		wbsUserAuthInfo.setRegisteDatetime(formatTime(user.getRegTime()));

		return wbsUserAuthInfo;
	}

	private String formatTime(Date registTime) {
		if (registTime != null) {
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			return format1.format(registTime);
		}
		return "";
	}

	@Override
	public void authorize(WbsRegisterMqVO qo) {
		if (Strings.isNullOrEmpty(qo.getAssetCustomerId()) || Strings.isNullOrEmpty(qo.getUtmId())) {
			throw new CheckException("快速授权请求参数为空！");
		}

		try {
			commonProducer.messageSendDelay(new MessageContent(MQConstant.WBS_REGISTER_TOPIC,
					MQConstant.WBS_REGISTER_TAG, UUID.randomUUID().toString(), qo), 3);
		} catch (MQException e) {
			logger.error("授权发送MQ失败！");
			throw new CheckException(e.getMessage());
		}

	}

	@Override
	public WebUserBindVO redirect(WbsRedirectQO qo, String ipAddr) {

		WebUserBindVO vo=new WebUserBindVO();

		verifyParameters(qo);

		UserVO userVO= getCustomerFromNewBanker(qo);

		UserVO user=amUserClient.findUserById(userVO.getUserId());

		if(!user.getUsername().equals(userVO.getUsername())){
			throw new CheckException("999","汇盈查询的用户名【"+userVO.getUsername()+"】与newBanker提供的【"+user.getUsername()+"】不一致！");
		}

		WebViewUserVO webViewUserVO = loginOperationOnly(userVO,user.getUsername(),ipAddr, WbsConstants.CHANNEL_PC);
		if (webViewUserVO != null) {
			logger.info("web端登录成功 userId is :{}", webViewUserVO.getUserId());
			if (user != null && StringUtils.isNotBlank(qo.getPresetProps())) {
				logger.info("Web登录事件,神策预置属性:" + qo.getPresetProps());
				try {
					SensorsDataBean sensorsDataBean = new SensorsDataBean();
					// 将json串转换成Bean
					Map<String, Object> sensorsDataMap = JSONObject.parseObject(qo.getPresetProps(), new TypeReference<Map<String, Object>>() {
					});
					sensorsDataBean.setPresetProps(sensorsDataMap);
					sensorsDataBean.setUserId(webViewUserVO.getUserId());
					// 发送神策数据统计MQ
					commonProducer.messageSendDelay(new MessageContent(MQConstant.SENSORSDATA_LOGIN_TOPIC, UUID.randomUUID().toString(), sensorsDataBean), 2);
				} catch (Exception e) {
					logger.error(e.getMessage());
				}
			}
			//登录成功发送mq
			UserOperationLogEntityVO userOperationLogEntity = new UserOperationLogEntityVO();
			userOperationLogEntity.setOperationType(UserOperationLogConstant.USER_OPERATION_LOG_TYPE1);
			userOperationLogEntity.setIp(ipAddr);
			userOperationLogEntity.setPlatform(0);
			userOperationLogEntity.setRemark("");
			userOperationLogEntity.setOperationTime(new Date());
			userOperationLogEntity.setUserName(webViewUserVO.getUsername());
			userOperationLogEntity.setUserRole(webViewUserVO.getRoleId());
			logger.info("userOperationLogEntity发送数据==="+JSONObject.toJSONString(userOperationLogEntity));
			try {
				commonProducer.messageSend(new MessageContent(MQConstant.USER_OPERATION_LOG_TOPIC, UUID.randomUUID().toString(), userOperationLogEntity));
			} catch (MQException e) {
				logger.error("保存用户日志失败" , e);
			}
			BeanUtils.copyProperties(webViewUserVO,vo);
		} else {
			throw new CheckException(ApiResult.FAIL,"登录失败,账号或密码错误");
		}
		return vo;
	}

	private UserVO getCustomerFromNewBanker(WbsRedirectQO qo) {

		Map<String,String> parameter= Maps.newConcurrentMap();
		parameter.put("token",qo.getToken());
		parameter.put("entId",qo.getEntId());

		WbsCommonQO wbsCommonQO = new WbsCommonQO();
		wbsCommonQO.setApp_key(wbsConfig.getAppKey());
		wbsCommonQO.setName(WbsConstants.INTERFACE_NAME_SYNC_CUSTOMER);

		String dataJson= JSON.toJSONString(parameter);
		try {
			wbsCommonQO.setData(URLEncoder.encode(dataJson, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			logger.error("为数据【{}】UTF-8编码出错",dataJson);
			throw new CheckException("999","编码出错！"+e.getMessage());
		}
		wbsCommonQO.setAccess_token("");
		wbsCommonQO.setVersion("");

		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String nowTime= sdf.format(new Date());

		wbsCommonQO.setTimestamp(nowTime);

		WbsCommonExQO commonExQO=new WbsCommonExQO();
		BeanUtils.copyProperties(wbsCommonQO,commonExQO);
		commonExQO.setSign(WbsSignUtil.encrypt(wbsCommonQO,wbsConfig.getAppSecret()));

		String jsonRequest = JSONObject.toJSONString(commonExQO);

		logger.info("【{}】请求数据【{}】",WbsConstants.INTERFACE_NAME_PASSPORT_AUTHORIZE_,jsonRequest);

		String postUrl = wbsConfig.getSyncCustomerUrl();

		String content = HttpClientUtils.postJson(postUrl, jsonRequest);

		logger.info("【{}】返回数据【{}】",WbsConstants.INTERFACE_NAME_PASSPORT_AUTHORIZE_,content);

		JSONObject jasonObject = JSONObject.parseObject(content);
		Map map = jasonObject;
		if (map != null && WbsConstants.WBS_RESPONSE_STATUS_SUCCESS
				.equals(String.valueOf(map.get(WbsConstants.WBS_RESPONSE_STATUS_KEY)))) {
			UserVO userVO=new UserVO();
			userVO.setUserId(Integer.valueOf((Integer) map.get("assetCustomerId")));
			userVO.setUsername(((JSONObject) map).getString("userName"));
			return userVO;
		} else {
			logger.error("授权登录调接口返回失败！详细信息【{}】", map.get(WbsConstants.WBS_RESPONSE_ERROR_MSG_KEY));
			throw new WbsException("授权登录调接口返回失败！");
		}
	}

	/**
	 * 重定向参数检验
	 * @param qo
	 */
	private void verifyParameters(WbsRedirectQO qo) {

		if(Strings.isNullOrEmpty(qo.getType())){
			throw new CheckException("999","重定向Type为空！");
		}
		if(Strings.isNullOrEmpty(qo.getEntId()) || Strings.isNullOrEmpty(qo.getToken())){
			throw new CheckException("999","token或entId为空！");
		}

		if(RedirectTypeEnum.BORROW_TYPE.getType().equals(qo.getType())){
			if(Strings.isNullOrEmpty(qo.getBorrowNid())){
				throw new CheckException("999","重定向到标的详情页标的号为必填项！");
			}
		}

	}

	private WebViewUserVO loginOperationOnly(UserVO userVO,String loginUserName,String ip,String channel) {
		int userId = userVO.getUserId();
		// 是否禁用
		if (userVO.getStatus() == 1) {
			throw new CheckException(MsgEnum.ERR_USER_INVALID);
		}
		// 更新登录信息
		amUserClient.updateUser(userVO, ip);

		WebViewUserVO webViewUserVO = amUserClient.getWebViewUserByUserId(userId,channel);
		// 2. 缓存
		webViewUserVO = setToken(webViewUserVO);
		String accountId = webViewUserVO.getBankAccount();
		if (accountId != null && StringUtils.isNoneBlank(accountId)) {
			Map<String, String> params = new HashMap<String, String>();
			params.put("accountId", accountId);
			params.put("ip", ip);
			try {
				commonProducer.messageSend(new MessageContent(MQConstant.HYJF_TOPIC, MQConstant.SYNBALANCE_TAG, UUID.randomUUID().toString(),params));
			} catch (MQException e) {
				logger.error("同步线下充值异常:"+e.getMessage());
			}

		}
		if (channel.equals(WbsConstants.CHANNEL_WEI)) {
			String sign = SecretUtil.createToken(userId, loginUserName, accountId);
			webViewUserVO.setToken(sign);
		}
		return webViewUserVO;
	}

	private WebViewUserVO setToken(WebViewUserVO webViewUserVO){

		Integer userId=webViewUserVO.getUserId();
		String username=webViewUserVO.getUsername();

		AccessToken accessToken = new AccessToken(userId, username, Instant.now().getEpochSecond());
		String token = Token.generate(String.valueOf(userId + username + Instant.now().getEpochSecond()));

		// 1.设置页面30分钟超时 2.jwt无法删除已知非法token,redis可以做到
		RedisUtils.setObjEx(RedisConstants.USER_TOEKN_KEY + token, accessToken, 30*60);

		webViewUserVO.setToken(token);
		RedisUtils.setObjEx(RedisConstants.USERID_KEY + webViewUserVO.getUserId(), webViewUserVO, 7 * 24 * 60 * 60);
		return webViewUserVO;
	}
}
