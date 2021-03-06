/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.service.callcenter.impl;

import com.hyjf.am.resquest.callcenter.CallCenterServiceUsersRequest;
import com.hyjf.am.resquest.callcenter.CallCenterUserInfoRequest;
import com.hyjf.am.resquest.callcenter.CallcenterAccountHuifuRequest;
import com.hyjf.am.user.dao.model.auto.BankCard;
import com.hyjf.am.user.dao.model.auto.BankCardExample;
import com.hyjf.am.user.dao.model.auto.CallcenterServiceUsers;
import com.hyjf.am.user.dao.model.auto.CallcenterServiceUsersExample;
import com.hyjf.am.user.dao.model.customize.CallcenterAccountHuifuCustomize;
import com.hyjf.am.user.dao.model.customize.CallcenterUserBaseCustomize;
import com.hyjf.am.user.service.callcenter.CallCenterBankService;
import com.hyjf.am.user.service.impl.BaseServiceImpl;
import com.hyjf.common.cache.CacheUtil;
import com.hyjf.common.util.CommonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wangjun
 * @version CallCenterBankServiceImpl, v0.1 2018/6/6 14:22
 */
@Service
public class CallCenterBankServiceImpl extends BaseServiceImpl implements CallCenterBankService {

	/**
	 * 查询江西银行绑卡
	 * @param userId
	 * @return
	 */
	@Override
	public List<BankCard> getTiedCardOfAccountBank(Integer userId){
			BankCardExample example = new BankCardExample();
			BankCardExample.Criteria cra = example.createCriteria();
			cra.andUserIdEqualTo(userId);
		List<BankCard> bankCardList = bankCardMapper.selectByExample(example);
		if(bankCardList!= null && bankCardList.size()>0){
			return bankCardList;
		}
		return null;
	}

	/**
	 * 查询呼叫中心未分配客服的用户
	 * @param callCenterUserInfoRequest
	 * @return
	 */
	@Override
	public List<CallcenterUserBaseCustomize> getNoServiceUsersList(CallCenterUserInfoRequest callCenterUserInfoRequest) {
		List<CallcenterUserBaseCustomize> CallcenterUserBaseCustomizeList = callCenterCustomizeMapper.findNoServiceUsersList(callCenterUserInfoRequest);
		return CallcenterUserBaseCustomizeList;
	}

	/**
	 * 更新客服分配状态
	 * @param callCenterServiceUsersRequest
	 * @return
	 */
	@Override
	public Integer updateRecord(CallCenterServiceUsersRequest callCenterServiceUsersRequest){
		//当前时间
		Date nowDate = new Date();
		//操作记录数
		int rowCound = 0;
		List<CallcenterServiceUsers> centerServiceUsersList =
				CommonUtils.convertBeanList(callCenterServiceUsersRequest.getCallCenterServiceUsersVOList(),CallcenterServiceUsers.class);
		for (CallcenterServiceUsers bean : centerServiceUsersList) {
			//检索条件
			CallcenterServiceUsersExample example = new CallcenterServiceUsersExample();
			example.createCriteria().andUsernameEqualTo(bean.getUsername());
			//检索
			List<CallcenterServiceUsers> list = callcenterServiceUsersMapper.selectByExample(example);
			if (list.size() > 0) {
				//更新
				bean.setUpdateTime(nowDate);//更新时间
				rowCound += this.callcenterServiceUsersMapper.updateByExampleSelective(bean, example);
			}else{
				//登陆
				bean.setInsdate(nowDate);//登陆时间
				rowCound += callcenterServiceUsersMapper.insertSelective(bean);
			}
		}
		return rowCound;
	}

	/**
	 * 查询用户基本信息
	 * @param callCenterUserInfoRequest
	 * @return
	 */
	@Override
	public List<CallcenterUserBaseCustomize> getBasicUsersList(CallCenterUserInfoRequest callCenterUserInfoRequest) {
		List<CallcenterUserBaseCustomize> CallcenterUserBaseCustomizeList = callCenterCustomizeMapper.findBasicUsersList(callCenterUserInfoRequest);
		if(!CollectionUtils.isEmpty(CallcenterUserBaseCustomizeList)){
			// 原param表改缓存取值
			Map<String, String> userRoleMap = CacheUtil.getParamNameMap("USER_ROLE");
			Map<String, String> userPropertyMap = CacheUtil.getParamNameMap("USER_PROPERTY");
			Map<String, String> accountStatusMap = CacheUtil.getParamNameMap("ACCOUNT_STATUS");
			Map<String, String> userStatusMap = CacheUtil.getParamNameMap("USER_STATUS");
			Map<String, String> registPlatMap = CacheUtil.getParamNameMap("CLIENT");
			Map<String, String> userTypeMap = CacheUtil.getParamNameMap("USER_TYPE");
			// 因为业务需求取消了51校验
            for(CallcenterUserBaseCustomize callcenterUserBaseCustomize : CallcenterUserBaseCustomizeList){
            	callcenterUserBaseCustomize.setUserRole(userRoleMap.getOrDefault(callcenterUserBaseCustomize.getUserRole(),null));
            	callcenterUserBaseCustomize.setUserProperty(userPropertyMap.getOrDefault(callcenterUserBaseCustomize.getUserProperty(),null));
            	callcenterUserBaseCustomize.setAccountStatus(accountStatusMap.getOrDefault(callcenterUserBaseCustomize.getAccountStatus(),null));
            	callcenterUserBaseCustomize.setUserStatus(userStatusMap.getOrDefault(callcenterUserBaseCustomize.getUserStatus(),null));
            	callcenterUserBaseCustomize.setRegistPlat(registPlatMap.getOrDefault(callcenterUserBaseCustomize.getRegistPlat(),null));
            	callcenterUserBaseCustomize.setUserType(userTypeMap.getOrDefault(callcenterUserBaseCustomize.getUserType(),null));
            }
		}
		return CallcenterUserBaseCustomizeList;
	}

	/**
	 * 查询用户详细信息
	 * @param callCenterUserInfoRequest
	 * @return
	 */
	@Override
	public List<CallcenterUserBaseCustomize> getUserDetailById(CallCenterUserInfoRequest callCenterUserInfoRequest) {
		List<CallcenterUserBaseCustomize> CallcenterUserBaseCustomizeList = callCenterCustomizeMapper.findUserDetailById(callCenterUserInfoRequest);
		Map<String, String> registPlatMap = CacheUtil.getParamNameMap("CLIENT");
		Map<String, String> openAccountPlatMap = CacheUtil.getParamNameMap("CLIENT");
		Map<String, String> roleMap = CacheUtil.getParamNameMap("USER_ROLE");
		Map<String, String> userPropertyMap = CacheUtil.getParamNameMap("USER_PROPERTY");
		Map<String, String> emRealtionMap = CacheUtil.getParamNameMap("USER_RELATION");
		Map<String, String> userTypeMap = CacheUtil.getParamNameMap("USER_TYPE");
		for(CallcenterUserBaseCustomize callcenterUserBaseCustomize : CallcenterUserBaseCustomizeList){
			callcenterUserBaseCustomize.setRegistPlat(registPlatMap.getOrDefault(callcenterUserBaseCustomize.getRegistPlat(),null));
			callcenterUserBaseCustomize.setOpenAccountPlat(openAccountPlatMap.getOrDefault(callcenterUserBaseCustomize.getOpenAccountPlat(),null));
			callcenterUserBaseCustomize.setRole(roleMap.getOrDefault(callcenterUserBaseCustomize.getRole(),null));
			callcenterUserBaseCustomize.setUserProperty(userPropertyMap.getOrDefault(callcenterUserBaseCustomize.getUserProperty(),null));
			callcenterUserBaseCustomize.setEmRealtion(emRealtionMap.getOrDefault(callcenterUserBaseCustomize.getEmRealtion(),null));
			callcenterUserBaseCustomize.setUserType(userTypeMap.getOrDefault(callcenterUserBaseCustomize.getUserType(),null));
		}
		return CallcenterUserBaseCustomizeList;
	}

	/**
	 * 查询汇付绑卡
	 * @param callcenterAccountHuifuRequest
	 * @return
	 */
	@Override
	public List<CallcenterAccountHuifuCustomize> getHuifuTiedcardInfo(CallcenterAccountHuifuRequest callcenterAccountHuifuRequest) {
		List<CallcenterAccountHuifuCustomize> callcenterAccountHuifuCustomizeList = callCenterAccountHuifuMapper.findHuifuTiedcardInfo(callcenterAccountHuifuRequest);
		return callcenterAccountHuifuCustomizeList;
	}

	/**
	 * 获取优惠券内容
	 * @param couponSource
	 * @return
	 */
	@Override
	public String getCouponContent(String couponSource){
		return callCenterCustomizeMapper.getCouponContent(couponSource);
	}
}
