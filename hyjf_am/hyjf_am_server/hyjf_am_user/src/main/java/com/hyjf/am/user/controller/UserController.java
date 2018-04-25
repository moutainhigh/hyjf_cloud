package com.hyjf.am.user.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.user.UserResponse;
import com.hyjf.am.resquest.user.RegisterUserRequest;
import com.hyjf.am.user.dao.model.auto.Users;
import com.hyjf.am.user.service.UserService;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.exception.ServiceException;

/**
 * @author xiasq
 * @version UserController, v0.1 2018/1/21 22:37
 */

@RestController
@RequestMapping("/am-user/user")
public class UserController {
	private static Logger logger = LoggerFactory.getLogger(UserController.class);
	@Autowired
	private UserService userService;

	@PostMapping("/register")
	public UserResponse register(@RequestBody @Valid RegisterUserRequest userRequest) {
		logger.info("user register:" + JSONObject.toJSONString(userRequest));
		UserResponse userResponse = new UserResponse();
		try {
			Users user = userService.register(userRequest);
			if (user == null) {
				userResponse.setRtn(UserResponse.USER_EXISTS);
				userResponse.setMessage(UserResponse.USER_EXISTS_MSG);
				logger.info("user register ,user " + userRequest.getMobilephone() + " has exists");
			} else {
				UserVO userVO = new UserVO();
				BeanUtils.copyProperties(user, userVO);
				userResponse.setResult(userService.assembleUserVO(userVO));
			}
		} catch (ServiceException e) {
			userResponse.setRtn(UserResponse.FAIL);
			userResponse.setMessage(UserResponse.FAIL_MSG);
			logger.error("user register error", e);
		}
		return userResponse;
	}

	/**
	 * 根据userId查询
	 * 
	 * @param userId
	 * @return
	 */
	@RequestMapping("/findById/{userId}")
	public UserResponse findUserByUserId(@PathVariable int userId) {
		logger.info("findUserByUserId run...userId is :{}", userId);
		UserResponse response = new UserResponse();
		Users user = userService.findUserByUserId(userId);
		if (user != null) {
			UserVO userVO = new UserVO();
			BeanUtils.copyProperties(user, userVO);
			response.setResult(userVO);
		}
		return response;
	}

	/**
	 * 根据mobile查找用户
	 * 
	 * @param mobile
	 * @return
	 */
	@RequestMapping("/findByMobile/{mobile}")
	public UserResponse findUserByMobile(@PathVariable String mobile) {
		logger.info("findUserByMobile...mobile is :{}", mobile);
		UserResponse response = new UserResponse();
		Users user = userService.findUserByMobile(mobile);
		if (user != null) {
			UserVO userVO = new UserVO();
			BeanUtils.copyProperties(user, userVO);
			response.setResult(userVO);
		}
		return response;
	}

	/**
	 * 根据username 或者 mobile查询用户
	 * 
	 * @param condition
	 * @return
	 */
	@RequestMapping("/findByCondition/{condition}")
	public UserResponse findUserByCondition(@PathVariable String condition) {
		logger.info("findUserByCondition run...condition is :{}", condition);
		UserResponse response = new UserResponse();
		Users user = userService.findUserByUsernameOrMobile(condition);
		if (user != null) {
			UserVO userVO = new UserVO();
			BeanUtils.copyProperties(user, userVO);
			response.setResult(userService.assembleUserVO(userVO));
		}
		return response;
	}

	/**
	 * 根据推荐人手机号或userId 查询推荐人
	 * 
	 * @param reffer
	 * @return
	 */
	@RequestMapping("/findReffer/{reffer}")
	public UserResponse findUserByRecommendName(@PathVariable String reffer) {
		logger.info("findUserByRecommendName run...reffer is :{}", reffer);
		UserResponse response = new UserResponse();
		Users user = userService.findUserByRecommendName(reffer);
		if (user != null) {
			UserVO userVO = new UserVO();
			BeanUtils.copyProperties(user, userVO);
			response.setResult(userVO);
		}
		return response;
	}

	@RequestMapping("/updateLoginUser/}")
	public void updateLoginUser(@RequestParam int userId, @RequestParam String ip) {
		logger.info("updateLoginUser run...userId is :{}, ip is :{}", userId, ip);
		userService.updateLoginUser(userId, ip);
	}

}
