package com.hyjf.cs.trade.client;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.resquest.trade.MyInviteListRequest;
import com.hyjf.am.vo.trade.BankReturnCodeConfigVO;
import com.hyjf.am.vo.trade.CorpOpenAccountRecordVO;
import com.hyjf.am.vo.user.*;

import java.util.List;
import java.util.Map;

/**
 * @Description 
 * @Author pangchengchao
 * @Version v0.1
 * @Date  
 */
public interface AmUserClient {

	UserVO findUserById(int userId);

	UserInfoVO findUsersInfoById(int userId);

    CorpOpenAccountRecordVO selectCorpOpenAccountRecordByUserId(Integer userId);

	BankReturnCodeConfigVO getBankReturnCodeConfig(String retCode);

	/**
	 * @Description 查询用户授权状态
	 * @Author sunss
	 * @Version v0.1
	 * @Date 2018/6/19 11:52
	 */
	HjhUserAuthVO getHjhUserAuthVO(Integer userId);

	/**
	 * @Description 根据userId查询开户信息
	 * @Author sunss
	 * @Version v0.1
	 * @Date 2018/6/19 15:32
	 */
    BankOpenAccountVO selectBankAccountById(Integer userId);

    /**
     * @Description 根据userId查询推荐人
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/20 16:11
     */
	UserVO getSpreadsUsersByUserId(Integer userId);

	/**
	 * @Description 根据用户ID查询查询CRM
	 * @Author sunss
	 * @Version v0.1
	 * @Date 2018/6/20 18:13
	 */
	UserInfoCrmVO queryUserCrmInfoByUserId(int userId);

	/**
	 *	用户详细信息 add by jijun 20180622
	 * @param userId
	 * @return
	 */
	UserInfoCustomizeVO queryUserInfoCustomizeByUserId(Integer userId);

	/**
	 *
	 * @param userId
	 * @return
	 */
	SpreadsUserVO querySpreadsUsersByUserId(Integer userId);

	/**
	 * 获取员工信息
	 * @param userId
	 * @return
	 */
	EmployeeCustomizeVO selectEmployeeByUserId(Integer userId);

	/**
	 * 修改用户对象
	 * @param user
	 * @return
	 */
    boolean updateByPrimaryKeySelective(UserVO user);

    /**
     * 根据userId查询用户推广链接注册
     * @param userId
     * @return
     */
    UtmRegVO findUtmRegByUserId(Integer userId);

    /**
     * 更新huiyingdai_utm_reg的首投信息
     * @param params
     * @return
     */
    boolean updateFirstUtmReg(Map<String,Object> params);

	List<MyInviteListCustomizeVO> selectMyInviteList(MyInviteListRequest requestBean);

	/**
	 * 判断是否51老客户
	 * @param userId
	 * @return
	 */
    boolean checkIs51UserCanInvest(Integer userId);

    /**
     * 更新用户投资信息
     * @param para
     */
    boolean updateUserInvestFlag(JSONObject para);

    /**
     * 插入VIPuser
     * @param para
     */
	boolean insertVipUserTender(JSONObject para);
}
