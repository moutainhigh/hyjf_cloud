package com.hyjf.am.user.controller;

import com.hyjf.am.response.user.UserInfoCrmResponse;
import com.hyjf.am.response.user.UserInfoResponse;
import com.hyjf.am.user.dao.model.auto.UserInfo;
import com.hyjf.am.user.dao.model.customize.crm.UserCrmInfoCustomize;
import com.hyjf.am.user.service.UserInfoService;
import com.hyjf.am.vo.user.UserInfoCrmVO;
import com.hyjf.am.vo.user.UserInfoVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiasq
 * @version UserInfoController, v0.1 2018/4/23 9:54
 */

@RestController
@RequestMapping("/am-user/userInfo")
public class UserInfoController {

	@Autowired
	private UserInfoService userInfoService;

	@RequestMapping("/findById/{userId}")
	public UserInfoResponse findUserInfoById(@PathVariable int userId) {
		UserInfoResponse response = new UserInfoResponse();
		UserInfo usersInfo = userInfoService.findUserInfoById(userId);
		if (usersInfo != null) {
			UserInfoVO userInfoVO = new UserInfoVO();
			BeanUtils.copyProperties(usersInfo, userInfoVO);
			response.setResult(userInfoVO);
		}
		return response;
	}

	@RequestMapping("/findByIdNo/{idNo}")
	public UserInfoResponse findUserInfoByIdNo(@PathVariable String idNo) {
		UserInfoResponse response = new UserInfoResponse();
		UserInfo usersInfo = userInfoService.findUserInfoByIdNo(idNo);
		if (usersInfo != null) {
			UserInfoVO userInfoVO = new UserInfoVO();
			BeanUtils.copyProperties(usersInfo, userInfoVO);
			response.setResult(userInfoVO);
		}
		return response;
	}

	@GetMapping("/selectUserInfoByNameAndCard/{truename}/{idcard}")
	public UserInfoResponse selectUserInfoByNameAndCard(@PathVariable String truename, @PathVariable String idcard) {
		UserInfoResponse response = new UserInfoResponse();
		UserInfo userInfo = userInfoService.selectUserInfoByNameAndCard(truename, idcard);
		UserInfoVO userInfoVO = new UserInfoVO();
		if (userInfo != null) {
			BeanUtils.copyProperties(userInfo, userInfoVO);
			response.setResult(userInfoVO);
		}
		return response;
	}

	/**
	 * @Description 根据用户ID查询CRM信息
	 * @Author sunss
	 * @Date 2018/6/21 17:25
	 */
	@RequestMapping("/findUserCrmInfoByUserId/{userId}")
	public UserInfoCrmResponse findUserCrmInfoByUserId(@PathVariable Integer userId) {
		UserInfoCrmResponse response = new UserInfoCrmResponse();
		UserCrmInfoCustomize usersInfo = userInfoService.findUserCrmInfoByUserId(userId);
		if (usersInfo != null) {
			UserInfoCrmVO userInfoVO = new UserInfoCrmVO();
			BeanUtils.copyProperties(usersInfo, userInfoVO);
			response.setResult(userInfoVO);
		}
		return response;
	}

}
