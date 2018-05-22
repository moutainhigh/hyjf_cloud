package com.hyjf.cs.user.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.hyjf.am.resquest.user.BankRequest;
import com.hyjf.am.resquest.user.RegisterUserRequest;
import com.hyjf.am.vo.message.SmsMessage;
import com.hyjf.am.vo.user.HjhUserAuthLogVO;
import com.hyjf.am.vo.user.HjhUserAuthVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.am.vo.user.WebViewUser;
import com.hyjf.common.constants.CommonConstant;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.constants.MessageConstant;
import com.hyjf.common.constants.RedisKey;
import com.hyjf.common.exception.MQException;
import com.hyjf.common.exception.ReturnMessageException;
import com.hyjf.common.jwt.JwtHelper;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetCode;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.MD5Utils;
import com.hyjf.common.validator.Validator;
import com.hyjf.cs.user.beans.BaseMapBean;
import com.hyjf.cs.user.client.AmBankOpenClient;
import com.hyjf.cs.user.client.AmUserClient;
import com.hyjf.cs.user.config.SystemConfig;
import com.hyjf.cs.user.constants.AuthorizedError;
import com.hyjf.cs.user.constants.LoginError;
import com.hyjf.cs.user.constants.RegisterError;
import com.hyjf.cs.user.mq.CouponProducer;
import com.hyjf.cs.user.mq.Producer;
import com.hyjf.cs.user.mq.SmsProducer;
import com.hyjf.cs.user.redis.RedisUtil;
import com.hyjf.cs.user.redis.StringRedisUtil;
import com.hyjf.cs.user.service.ActivityService;
import com.hyjf.cs.user.service.UserService;
import com.hyjf.cs.user.util.ClientConstant;
import com.hyjf.cs.user.util.GetCilentIP;
import com.hyjf.cs.user.util.ResultEnum;
import com.hyjf.cs.user.vo.RegisterVO;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.bean.BankCallResult;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import com.hyjf.pay.lib.bank.util.BankCallUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author xiasq
 * @version UserServiceImpl, v0.1 2018/4/11 9:34
 */

@Service
public class UserServiceImpl implements UserService  {
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@Autowired
	private AmUserClient amUserClient;

	@Autowired
	private AmBankOpenClient amBankOpenClient;

	@Autowired
	private ActivityService activityService;

	@Autowired
	private CouponProducer couponProducer;

	@Autowired
	private SmsProducer smsProducer;

	@Autowired
	private RedisUtil redisUtil;

	@Autowired
	private StringRedisUtil stringRedisUtil;

	@Autowired
	SystemConfig systemConfig;
	@Value("${hyjf.activity.regist.tzj.id}")
	private String activityIdTzj;
	@Value("${hyjf.activity.888.id}")
	private String activityId;

	/**
	 * 1. 必要参数检查 2. 注册 3. 注册后处理
	 * 
	 * @param registerVO
	 * @throws ReturnMessageException
	 */
	@Override
	public UserVO register(RegisterVO registerVO, HttpServletRequest request, HttpServletResponse response)
			throws ReturnMessageException {
		// 1. 参数检查
		this.registerCheckParam(registerVO);
		RegisterUserRequest registerUserRequest = new RegisterUserRequest();
		BeanUtils.copyProperties(registerVO, registerUserRequest);
		registerUserRequest.setLoginIp(GetCilentIP.getIpAddr(request));
		// 2.注册
		UserVO userVO = amUserClient.register(registerUserRequest);
		if (userVO == null) {
			throw new ReturnMessageException(RegisterError.REGISTER_ERROR);
		}

		// 3.注册后处理
		this.afterRegisterHandle(userVO);

		return userVO;
	}

	@Override
	public boolean existUser(String mobile) {
		UserVO userVO = amUserClient.findUserByMobile(mobile);
		return userVO == null ? false : true;
	}

	/**
	 *
	 * @param loginUserName
	 *            可以是手机号或者用户名
	 * @param loginPassword
	 * @param ip
	 */
	@Override
	public UserVO login(String loginUserName, String loginPassword, String ip) {
		if (checkMaxLength(loginUserName, 16) || checkMaxLength(loginUserName, 32)) {
			throw new ReturnMessageException(LoginError.USER_LOGIN_ERROR);
		}

		// 获取密码错误次数
		String errCount = stringRedisUtil.get(RedisKey.PASSWORD_ERR_COUNT + loginUserName);
		if (StringUtils.isNotBlank(errCount) && Integer.parseInt(errCount) > 6) {
			throw new ReturnMessageException(LoginError.PWD_ERROR_TOO_MANEY_ERROR);
		}
		return this.doLogin(loginUserName, loginPassword, ip);
	}

	/**
	 * 登录处理
	 * 
	 * @param loginUserName
	 * @param loginPassword
	 * @return
	 */
	private UserVO doLogin(String loginUserName, String loginPassword, String ip) {
		UserVO userVO = amUserClient.findUserByUserNameOrMobile(loginUserName);

		if (userVO == null) {
			throw new ReturnMessageException(LoginError.USER_LOGIN_ERROR);
		}

		int userId = userVO.getUserId();
		String codeSalt = userVO.getSalt();
		String passwordDb = userVO.getPassword();
		// 页面传来的密码
		String password = MD5Utils.MD5(loginPassword + codeSalt);

		if (password.equals(passwordDb)) {
			// 是否禁用
			if (userVO.getStatus() == 1) {
				throw new ReturnMessageException(LoginError.USER_INVALID_ERROR);
			}

			// 更新登录信息
			amUserClient.updateLoginUser(userId, ip);

			// 1. 登录成功将登陆密码错误次数的key删除
			stringRedisUtil.delete(RedisKey.PASSWORD_ERR_COUNT + loginUserName);

			// 2. 缓存
			String token = generatorToken(userVO.getUserId(), userVO.getUsername());
			WebViewUser webViewUser = new WebViewUser();
			BeanUtils.copyProperties(userVO,webViewUser);
			webViewUser.setToken(token);
			redisUtil.setEx(RedisKey.USER_TOKEN_REDIS + token, webViewUser, 7 * 24 * 60 * 60, TimeUnit.SECONDS);

			// 3. todo 登录时自动同步线下充值记录

			return userVO;
		} else {
			// 密码错误，增加错误次数
			stringRedisUtil.incr(RedisKey.PASSWORD_ERR_COUNT + loginUserName);
			throw new ReturnMessageException(LoginError.USER_LOGIN_ERROR);
		}
	}

	/**
	 * 字符串长度检查
	 * 
	 * @param value
	 * @param max
	 * @return
	 */
	private boolean checkMaxLength(String value, int max) {
		if (StringUtils.isEmpty(value)) {
			return true;
		}
		if (value.length() > max) {
			return true;
		}
		return false;
	}

	/**
	 * 注册参数校验
	 * 
	 * @param registerVO
	 */
	private void registerCheckParam(RegisterVO registerVO) {
		if (registerVO == null) {
			throw new ReturnMessageException(RegisterError.REGISTER_ERROR);
		}

		String mobile = registerVO.getMobilephone();
		if (StringUtils.isEmpty(mobile)) {
			throw new ReturnMessageException(RegisterError.MOBILE_IS_NOT_NULL_ERROR);
		}

		String smsCode = registerVO.getSmsCode();
		if (StringUtils.isEmpty(smsCode)) {
			throw new ReturnMessageException(RegisterError.SMSCODE_IS_NOT_NULL_ERROR);
		}

		String password = registerVO.getPassword();
		if (StringUtils.isEmpty(password)) {
			throw new ReturnMessageException(RegisterError.PASSWORD_IS_NOT_NULL_ERROR);
		}

		if (!Validator.isMobile(mobile)) {
			throw new ReturnMessageException(RegisterError.MOBILE_IS_NOT_REAL_ERROR);
		} else {
			if (existUser(mobile)) {
				throw new ReturnMessageException(RegisterError.MOBILE_EXISTS_ERROR);
			}
		}
		if (password.length() < 6 || password.length() > 16) {
			throw new ReturnMessageException(RegisterError.PASSWORD_LENGTH_ERROR);
		}

		boolean hasNumber = false;
		for (int i = 0; i < password.length(); i++) {
			if (Validator.isNumber(password.substring(i, i + 1))) {
				hasNumber = true;
				break;
			}
		}
		if (!hasNumber) {
			throw new ReturnMessageException(RegisterError.PASSWORD_NO_NUMBER_ERROR);
		}
		String regEx = "^[a-zA-Z0-9]+$";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(password);
		if (!m.matches()) {
			throw new ReturnMessageException(RegisterError.PASSWORD_FORMAT_ERROR);
		}

		String verificationType = CommonConstant.PARAM_TPL_ZHUCE;
		int cnt = amUserClient.checkMobileCode(mobile, smsCode, verificationType, CommonConstant.CLIENT_PC,
				CommonConstant.CKCODE_YIYAN, CommonConstant.CKCODE_USED);
		if (cnt == 0) {
			throw new ReturnMessageException(RegisterError.SMSCODE_INVALID_ERROR);
		}
		String reffer = registerVO.getReffer();
		if (isNotBlank(reffer) && amUserClient.countUserByRecommendName(reffer) <= 0) {
			throw new ReturnMessageException(RegisterError.REFFER_INVALID_ERROR);
		}
	}

	/**
	 * 注册后处理: 1. 登录 2. 判断投之家着陆页送券 3. 注册送188红包
	 * 
	 * @param userVO
	 */
	private void afterRegisterHandle(UserVO userVO) {
		int userId = userVO.getUserId();

		// 1. 注册成功之后登录
		String token = generatorToken(userId, userVO.getUsername());
		WebViewUser webViewUser = new WebViewUser();
		BeanUtils.copyProperties(userVO,webViewUser);
		webViewUser.setToken(token);
		redisUtil.setEx(RedisKey.USER_TOKEN_REDIS + token, webViewUser, 7 * 24 * 60 * 60, TimeUnit.SECONDS);

		// 2. 投之家用户注册送券活动
		// 活动有效期校验
		if (!activityService.checkActivityIfAvailable(activityIdTzj)) {
			// 投之家用户额外发两张加息券
			if ("touzhijia".equals(userVO.getReferrerUserName())) {
				// 发放两张加息券
				JSONObject json = new JSONObject();
				json.put("userId", userId);
				json.put("couponSource", 2);
				json.put("couponCode", "PJ2958703");
				json.put("sendCount", 2);
				json.put("activityId", Integer.parseInt(activityIdTzj));
				json.put("remark", "投之家用户注册送加息券");
				json.put("sendFlg", 0);
				try {
					couponProducer.messageSend(new Producer.MassageContent(MQConstant.REGISTER_COUPON_TOPIC,
							MQConstant.TZJ_REGISTER_INTEREST_TAG, JSON.toJSONBytes(json)));
				} catch (MQException e) {
					logger.error("投之家用户注册送券失败....userId is :" + userId, e);
				}
			}
		}

		// 3. 注册送188元新手红包
		if (!activityService.checkActivityIfAvailable(activityId)) {
			try {
				JSONObject params = new JSONObject();
				params.put("mqMsgId", GetCode.getRandomCode(10));
				params.put("userId", String.valueOf(userId));
				params.put("sendFlg", "11");
				couponProducer.messageSend(new Producer.MassageContent(MQConstant.REGISTER_COUPON_TOPIC,
						MQConstant.REGISTER_COUPON_TAG, JSON.toJSONBytes(params)));
			} catch (Exception e) {
				logger.error("注册发放888红包失败...", e);
			}

			// 短信通知用户发券成功
			SmsMessage smsMessage = new SmsMessage(userId, null, userVO.getMobile(), null,
					MessageConstant.SMSSENDFORMOBILE, null, CustomConstants.PARAM_TPL_TZJ_188HB,
					CustomConstants.CHANNEL_TYPE_NORMAL);
			try {
				smsProducer.messageSend(
						new Producer.MassageContent(MQConstant.SMS_CODE_TOPIC, JSON.toJSONBytes(smsMessage)));
			} catch (MQException e) {
				logger.error("短信发送失败...", e);
			}
		}
	}

	/**
	 * jwt生成token
	 * 
	 * @param userId
	 * @param username
	 * @return
	 */
	private String generatorToken(int userId, String username) {
		Map map = ImmutableMap.of("userId", String.valueOf(userId), "username", username, "ts",
				String.valueOf(Instant.now().getEpochSecond()));
		String token = JwtHelper.genToken(map);
		return token;
	}

	/**
	 * 自动投资、债转授权
	 * @param token
	 * @param client 0web 1wechat 2app
	 * @param type 1表示投资 2表示债转
	 * @param request
	 * @return
	 */
	@Override
	public ModelAndView userCreditAuthInves(String token,Integer client,String type,HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		String lastSrvAuthCode = request.getParameter("lastSrvAuthCode");
		String smsCode = request.getParameter("smsCode");
		WebViewUser user = (WebViewUser) redisUtil.get(token);
		//检查用户信息
		this.checkUserMessage(user,lastSrvAuthCode,smsCode);
		// 判断是否授权过  todo
		HjhUserAuthVO hjhUserAuth=amUserClient.getHjhUserAuthByUserId(user.getUserId());
		if(hjhUserAuth!=null&&hjhUserAuth.getAutoCreditStatus().intValue()==1){
			throw new ReturnMessageException(AuthorizedError.CANNOT_REPEAT_ERROR);
		}
		// 组装发往江西银行参数
		BankCallBean bean = getCommonBankCallBean(user,type,lastSrvAuthCode,smsCode);
		// 插入日志
		this.insertUserAuthLog(user, bean,client,type);
		// 跳转到汇付天下画面
		try {
			modelAndView = BankCallUtils.callApi(bean);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ReturnMessageException(AuthorizedError.CALL_BANK_ERROR);
		}
		return modelAndView;
	}

	/**
	 * 组装发往江西银行参数
	 * @param users
	 * @param type
	 * @param lastSrvAuthCode
	 * @param smsCode
	 * @return
	 */
	private BankCallBean getCommonBankCallBean(WebViewUser users,String type,String lastSrvAuthCode,String smsCode) {
		String remark = "";
		String txcode = "";
		// 同步调用路径
		String retUrl = systemConfig.getWebHost()+"/bank/user/autoplus";
		// 异步调用路
		String bgRetUrl = systemConfig.getWebHost()+"/bank/user/autoplus";
		// 构造函数已经设置
		// 版本号  交易代码  机构代码  银行代码  交易日期  交易时间  交易流水号   交易渠道
		BankCallBean bean = new BankCallBean(BankCallConstant.VERSION_10,txcode,users.getUserId(),"000002");
		if("1".equals(type)){
			remark = "投资人自动投标签约增强";
			retUrl += "/userAuthInvesReturn";
			bgRetUrl+= "/userAuthInvesBgreturn";
			bean.setTxCode(BankCallConstant.TXCODE_AUTO_BID_AUTH_PLUS);
			bean.setDeadline(GetDate.date2Str(GetDate.countDate(1,5),new SimpleDateFormat("yyyyMMdd")));
			bean.setTxAmount("1000000");
			bean.setTotAmount("1000000000");
		} else if("2".equals(type)){
			// 2.4.8.投资人自动债权转让签约增强
			remark = "投资人自动债权转让签约增强";
			retUrl += "/credituserAuthInvesReturn";
			bgRetUrl+="/credituserAuthInvesBgreturn";
			bean.setTxCode(BankCallConstant.TXCODE_AUTO_CREDIT_INVEST_AUTH_PLUSS);
		}
		bean.setLogBankDetailUrl(BankCallConstant.BANK_URL_MOBILE_PLUS);
		bean.setOrderId(bean.getLogOrderId());
		// 电子账号
		bean.setAccountId(users.getBankAccount());
		//忘记密码通知地址
		bean.setForgotPwdUrl(CustomConstants.FORGET_PASSWORD_URL);
		bean.setRetUrl(retUrl);
		bean.setNotifyUrl(bgRetUrl);
		bean.setSuccessfulUrl(retUrl+"&isSuccess=1");
		bean.setLastSrvAuthCode(lastSrvAuthCode);
		bean.setSmsCode(smsCode);
		bean.setLogRemark(remark);
		return bean;
	}

	/**
	 * 检查用户信息
	 * @param user
	 * @param lastSrvAuthCode
	 * @param smsCode
	 */
	public void checkUserMessage(WebViewUser user,String lastSrvAuthCode,String smsCode){
		if (user == null) {
			throw new ReturnMessageException(AuthorizedError.USER_LOGIN_ERROR);
		}
		// 检查数据是否完整
		if (Validator.isNull(lastSrvAuthCode) || Validator.isNull(smsCode)) {
			throw new ReturnMessageException(AuthorizedError.PARAM_ERROR);
		}
		UserVO users= amUserClient.findUserById(user.getUserId());
		if (users.getBankOpenAccount()==0) {
			throw new ReturnMessageException(AuthorizedError.NOT_REGIST_ERROR);
		}
		// 判断用户是否设置过交易密码
		if (users.getIsSetPassword() == 0) {
			throw new ReturnMessageException(AuthorizedError.NOT_SET_PWD_ERROR);
		}
	}

	/**
	 *
	 * 插入用户签约授权log
	 * @param userId
	 * @param bean
	 * @param queryType
	 * @param i
	 */
	public void insertUserAuthLog(WebViewUser user, BankCallBean bean, Integer client, String authType) {
		Date nowTime=GetDate.getNowTime();
		HjhUserAuthLogVO hjhUserAuthLog=new HjhUserAuthLogVO();
		hjhUserAuthLog.setUserId(user.getUserId());
		hjhUserAuthLog.setUserName(user.getUsername());
		hjhUserAuthLog.setOrderId(bean.getLogOrderId());
		hjhUserAuthLog.setOrderStatus(2);
		if(authType!=null&&authType.equals(BankCallConstant.QUERY_TYPE_2)){
			hjhUserAuthLog.setAuthType(4);
		}else{
			hjhUserAuthLog.setAuthType(Integer.valueOf(authType));
		}
		hjhUserAuthLog.setOperateEsb(client);
		hjhUserAuthLog.setCreateUser(user.getUserId());
		hjhUserAuthLog.setCreateTime(nowTime);
		hjhUserAuthLog.setUpdateTime(nowTime);
		hjhUserAuthLog.setUpdateUser(user.getUserId());
		hjhUserAuthLog.setDelFlg(0);
		amUserClient.insertUserAuthLog(hjhUserAuthLog);
	}

	/**
	 * 检查是否已经授权过了
	 * @param hjhUserAuth
	 * @param txcode
	 * @return
	 */
	public boolean checkIsAuth(HjhUserAuthVO hjhUserAuth , String txcode) {
		String endTime = "";
		int status = 0;
		String nowTime = GetDate.date2Str(new Date(),GetDate.yyyyMMdd);
		// 缴费授权
		if(hjhUserAuth==null){
			return false;
		}
		if (BankCallConstant.TXCODE_PAYMENT_AUTH_PAGE.equals(txcode)) {
			endTime = hjhUserAuth.getAutoPaymentEndTime();
			status = hjhUserAuth.getAutoPaymentStatus();
		}else if(BankCallConstant.TXCODE_REPAY_AUTH_PAGE.equals(txcode)){
			endTime = hjhUserAuth.getAutoRepayEndTime();
			status = hjhUserAuth.getAutoRepayStatus();
		}else if(BankCallConstant.TXCODE_AUTO_BID_AUTH_PLUS.equals(txcode)){
			endTime = hjhUserAuth.getAutoBidEndTime();
			status = hjhUserAuth.getAutoInvesStatus();
		}
		// 0是未授权
		if (status - 0 == 0 || Integer.parseInt(endTime) - Integer.parseInt(nowTime) < 0) {
			return false;
		}
		return true;
	}

	/**
	 * web自动投资授权同步回调
	 * @param token
	 * @param bean
	 * @param request
	 * @return
	 */
	@Override
	public Map<String,String> userAuthInvesReturn(String token, BankCallBean bean, HttpServletRequest request) {
		Map<String,String> resultMap = new HashMap<>();
		resultMap.put("status", "success");
		bean.convert();
		WebViewUser user = (WebViewUser) redisUtil.get(token);
		if (user == null) {
			throw new ReturnMessageException(AuthorizedError.USER_LOGIN_ERROR);
		}
		String isSuccess = request.getParameter("isSuccess");
		if (isSuccess == null || !"1".equals(isSuccess)) {
			resultMap.put("status", "fail");
		}
		logger.info("自动投资授权同步回调调用查询接口查询状态结束  结果为:" + (bean == null ? "空" : bean.getRetCode()));
		HjhUserAuthVO hjhUserAuth=amUserClient.getHjhUserAuthByUserId(user.getUserId());
		if(hjhUserAuth.getAutoCreditStatus()==0) {
			resultMap.put("typeURL", "2");
		}else {
			resultMap.put("typeURL", "0");
		}
		return resultMap;
	}

	/**
	 * web授权自动债转同步回调
	 * @param token
	 * @param bean
	 * @param request
	 * @return
	 */
	@Override
	public Map<String, String> userCreditAuthInvesReturn(String token, BankCallBean bean, HttpServletRequest request) {
		Map<String,String> resultMap = new HashMap<>();
		resultMap.put("status", "success");
		bean.convert();
		WebViewUser user = (WebViewUser) redisUtil.get(token);
		if (user == null) {
			throw new ReturnMessageException(AuthorizedError.USER_LOGIN_ERROR);
		}
		String isSuccess = request.getParameter("isSuccess");
		if (isSuccess == null || !"1".equals(isSuccess)) {
			// TODO: 2018/5/21 失败原因
			resultMap.put("status", "fail");
		}
		HjhUserAuthVO hjhUserAuth=amUserClient.getHjhUserAuthByUserId(user.getUserId());
		if(hjhUserAuth.getAutoInvesStatus()==0) {
			resultMap.put("typeURL", "1");
		}else {
			resultMap.put("typeURL", "0");
		}
		return resultMap;
	}

	/**
	 * 异步回调接口
	 * @param bean
	 * @return
	 */
	@Override
	public String userBgreturn(BankCallBean bean) {
		BankCallResult result = new BankCallResult();
		logger.info("[用户授权异步回调开始]");
		bean.convert();
		Integer userId = Integer.parseInt(bean.getLogUserId());
		// 查询用户开户状态
		UserVO user= amUserClient.findUserById(userId);
		// 成功
		if (user != null && BankCallConstant.RESPCODE_SUCCESS.equals(bean.get(BankCallConstant.PARAM_RETCODE))) {
			try {
				// 更新签约状态和日志表
				BankRequest bankRequest = new BankRequest();
				BeanUtils.copyProperties(bean,bankRequest);
				amUserClient.updateUserAuthInves(bankRequest);
			} catch (Exception e) {
				e.printStackTrace();
				logger.error("userAuthInvesBgreturn",e);
			}
		}
		logger.info("[用户授权完成后,回调结束]");
		result.setStatus(true);
		return JSONObject.toJSONString(result, true);
	}

	/**
	 * app授权自动债转、投资同步回调
	 * @param token
	 * @param bean
	 * @param userAutoType 1债转 0投资
	 * @param request
	 * @return
	 */
	@Override
	public ModelAndView userAuthCreditReturn(String token, BankCallBean bean,String userAutoType, HttpServletRequest request) {
		String sign = request.getHeader("sign");
		bean.convert();
		String isSuccess = request.getParameter("isSuccess");
		// 用户ID
		Integer userId = Integer.parseInt(bean.getLogUserId());
		HjhUserAuthVO hjhUserAuth=amUserClient.getHjhUserAuthByUserId(userId);
		if (isSuccess == null || !"1".equals(isSuccess)|| hjhUserAuth == null||hjhUserAuth.getAutoCreditStatus()!=1) {
			if (ClientConstant.INVES_AUTO_TYPE.equals(userAutoType)){
				return getErrorModelAndView(ResultEnum.USER_ERROR_204,sign,userAutoType, hjhUserAuth);
			}else {
				return getErrorModelAndView(ResultEnum.USER_ERROR_205,sign,userAutoType, hjhUserAuth);
			}
		}else {
			return getSuccessModelAndView(sign, userAutoType, hjhUserAuth);
		}
	}

	/**
	 * 组装跳转错误页面MV
	 * @param param
	 * @param sign
	 * @param type
	 * @param hjhUserAuth
	 * @return
	 */
	private ModelAndView getErrorModelAndView(ResultEnum param,String sign, String type, HjhUserAuthVO hjhUserAuth) {
		ModelAndView modelAndView = new ModelAndView("/jumpHTML");
		BaseMapBean baseMapBean = new BaseMapBean();
		baseMapBean.set(CustomConstants.APP_STATUS, param.getStatus());
		baseMapBean.set(CustomConstants.APP_STATUS_DESC, param.getStatusDesc());
		baseMapBean.set("autoInvesStatus", hjhUserAuth==null?"0":hjhUserAuth.getAutoInvesStatus()==null?"0":hjhUserAuth.getAutoInvesStatus()+ "");
		baseMapBean.set("autoCreditStatus", hjhUserAuth==null?"0":hjhUserAuth.getAutoCreditStatus()==null?"0":hjhUserAuth.getAutoCreditStatus() + "");
		baseMapBean.set("userAutoType", type);
		baseMapBean.set(CustomConstants.APP_SIGN, sign);
		baseMapBean.setCallBackAction(systemConfig.getWebHost()+"/user/setting/authorization/result/failed");
		modelAndView.addObject("callBackForm", baseMapBean);
		return modelAndView;
	}

	/**
	 * 组装跳转成功页面MV
	 * @param sign
	 * @param type
	 * @param autoInvesStatus
	 * @param autoCreditStatus
	 * @return
	 */
	private ModelAndView getSuccessModelAndView(String sign, String type, HjhUserAuthVO hjhUserAuth) {
		ModelAndView modelAndView = new ModelAndView("/jumpHTML");
		BaseMapBean baseMapBean = new BaseMapBean();
		baseMapBean.set(CustomConstants.APP_STATUS, ResultEnum.SUCCESS.getStatus());
		baseMapBean.set(CustomConstants.APP_STATUS_DESC, ResultEnum.SUCCESS.getStatusDesc());
		baseMapBean.set(CustomConstants.APP_SIGN, sign);
		baseMapBean.set("autoInvesStatus", hjhUserAuth==null?"0":hjhUserAuth.getAutoInvesStatus()==null?"0":hjhUserAuth.getAutoInvesStatus()+ "");
		baseMapBean.set("autoCreditStatus", hjhUserAuth==null?"0":hjhUserAuth.getAutoCreditStatus()==null?"0":hjhUserAuth.getAutoCreditStatus() + "");
		baseMapBean.set("userAutoType", type);
		baseMapBean.setCallBackAction(systemConfig.getWebHost()+"/user/setting/authorization/result/success");
		modelAndView.addObject("callBackForm", baseMapBean);
		return modelAndView;
	}
}