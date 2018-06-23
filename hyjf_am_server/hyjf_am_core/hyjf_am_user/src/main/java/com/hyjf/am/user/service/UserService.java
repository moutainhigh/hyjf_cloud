package com.hyjf.am.user.service;

import com.hyjf.am.resquest.user.BankRequest;
import com.hyjf.am.resquest.user.RegisterUserRequest;
import com.hyjf.am.resquest.user.UsersContractRequest;
import com.hyjf.am.user.dao.model.auto.*;
import com.hyjf.am.vo.user.EvalationVO;
import com.hyjf.am.vo.user.UserEvalationResultVO;
import com.hyjf.common.exception.MQException;
import com.hyjf.common.exception.ServiceException;

import java.util.List;
import java.util.Map;

/**
 * @author xiasq
 * @version UserService, v0.1 2018/1/21 22:42
 */
public interface UserService {
	/**
	 * 注册
	 * @param userRequest
	 * @return
	 * @throws ServiceException
	 */
	User register(RegisterUserRequest userRequest) throws MQException;

	/**
	 * 获取用户
	 * @param userId
	 * @return
	 */
	User findUserByUserId(int userId);

	/**
	 * 获取用户
	 * @param userId
	 * @return
	 */
	UserInfo findUsersInfo(int userId);

	/**
	 * 生成唯一用户id
	 * 
	 * @param mobile
	 * @return
	 */
	String generateUniqueUsername(String mobile);

	/**
	 * 根据手机号查找用户信息
	 * 
	 * @param mobile
	 * @return
	 */
	User findUserByMobile(String mobile);

	/**
	 * 根据username或者mobiel查询用户
	 * @param condition
	 * @return
	 */
	User findUserByUsernameOrMobile(String condition);

	/**
	 * 根据推荐人手机号或userId查询推荐人
	 * 
	 * @param reffer
	 * @return
	 */
	User findUserByRecommendName(String reffer);

	void updateLoginUser(int userId, String ip);

	HjhUserAuth getHjhUserAuthByUserId(Integer userId);

	void insertSelective(HjhUserAuthLog hjhUserAuthLog);

	HjhUserAuthLog selectByExample(String orderId);

	int updateByPrimaryKeySelective(HjhUserAuthLog record);

	int insertSelective(HjhUserAuth record);

	int updateByPrimaryKeySelective(HjhUserAuth record);

    void updateUserAuthInves(BankRequest bean);

	/**
	 * 修改用户表By主键
	 * @param record
	 * @return int
	 */
	public int updateUserById(User record);

	UserEvalationResult selectUserEvalationResultByUserId(Integer userId);

    void deleteUserEvalationResultByUserId(Integer userId);

    AccountChinapnr getAccountChinapnr(Integer userId);

	int updateUserContact(UsersContractRequest record);

	UserContact selectUserContact(Integer userId);

	boolean checkEmailUsed(String email);

	void insertEmailBindLog(UserBindEmailLog log);

	UserBindEmailLog getUserBindEmail(Integer userid);

	void updateBindEmail(Integer userId, String email);

    UserLoginLog selectByPrimaryKey(Integer userId);

	/**
	 * 根据垫付机构用户名检索垫付机构用户
	 * @param repayOrgName
	 * @return
	 */
	List<User> selectUserByUsername(String repayOrgName);

	Evalation getEvalationByCountScore(short countScore);

    UserEvalationResult insertUserEvalationResult(List<String> answerList, List<String> questionList,
                                                  EvalationVO evalation, int countScore, Integer userId, UserEvalationResultVO oldUserEvalationResult);

	List<Evalation> getEvalationRecord();

    int isCompAccount(Integer userId);


    /**
     * 根据userId查询推广链接注册
     * @param userId
     * @return
     */
    UtmReg findUtmRegByUserId(Integer userId);

    /**
     * 更新渠道用户首次投资信息
     * @param bean
     * @return
     */
    void updateFirstUtmReg(Map<String,Object> bean);
}
