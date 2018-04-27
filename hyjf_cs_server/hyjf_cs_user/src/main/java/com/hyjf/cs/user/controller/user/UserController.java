package com.hyjf.cs.user.controller.user;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.cs.user.constants.LoginError;
import com.hyjf.cs.user.constants.RegisterError;
import com.hyjf.cs.user.result.ApiResult;
import com.hyjf.cs.user.service.UserService;
import com.hyjf.cs.user.util.GetCilentIP;
import com.hyjf.cs.user.vo.RegisterVO;

/**
 * @author xiasq
 * @version UserController, v0.1 2018/4/21 15:06
 */

@RestController
@RequestMapping("/api/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserService userService;

	/**
	 * 注册
	 * @param registerVO
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping(value = "/register", produces = "application/json; charset=utf-8")
	public ApiResult<UserVO> register(@RequestBody @Valid RegisterVO registerVO, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("register start, registerVO is :{}", JSONObject.toJSONString(registerVO));
		ApiResult<UserVO> result = new ApiResult<UserVO>();

		UserVO userVO = userService.register(registerVO, request, response);

		if (userVO != null) {
			logger.info("register success, userId is :{}", userVO.getUserId());
			result.setResult(userVO);
		} else {
			logger.error("register failed...");
			result.setStatus(ApiResult.STATUS_FAIL);
			result.setStatusDesc(RegisterError.REGISTER_ERROR.getMessage());
		}
		return result;
	}

	/**
	 * 登录
	 * @param loginUserName
	 * @param loginPassword
	 * @param request
	 * @return
	 */
	@PostMapping(value = "/login", produces = "application/json; charset=utf-8")
	public ApiResult<UserVO> login(@RequestParam String loginUserName,
								   @RequestParam String loginPassword,
			HttpServletRequest request) {
		logger.info("login start, loginUserName is :{}", loginUserName);
		ApiResult<UserVO> result = new ApiResult<UserVO>();
		UserVO userVO = userService.login(loginUserName, loginPassword, GetCilentIP.getIpAddr(request));

		if (userVO != null) {
			logger.info("login success, userId is :{}", userVO.getUserId());
			result.setResult(userVO);
		} else {
			logger.error("login failed...");
			result.setStatus(ApiResult.STATUS_FAIL);
			result.setStatusDesc(LoginError.USER_LOGIN_ERROR.getMessage());
		}

		return result;
	}

}
