/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.service.front.user;

import com.hyjf.am.response.Response;
import com.hyjf.am.resquest.user.AdminUserRecommendRequest;
import com.hyjf.am.resquest.user.UpdCompanyRequest;
import com.hyjf.am.resquest.user.UserManagerUpdateRequest;
import com.hyjf.am.user.dao.model.auto.*;
import com.hyjf.am.user.dao.model.customize.*;
import com.hyjf.am.user.service.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author nxl
 * @version UserManagerService, v0.1 2018/6/20 9:47
 *          后台管理系统：会员中心->会员管理
 */
public interface UserManagerService extends BaseService {

    /**
     * 根据筛选条件查找会员列表
     * @param mapParam
     * @param limitStart
     * @param limitEnd
     * @return
     */
    List<UserManagerCustomize> selectUserMemberList(Map<String, Object> mapParam,int limitStart, int limitEnd);

    /**
     * 根据条件查询用户管理总数
     *
     * @return
     */
    int countUserRecord(Map<String, Object> mapParam);

    /**
     * 根据用户id获取用户详情
     *
     * @param userId
     * @return
     */
    UserManagerDetailCustomize selectUserDetailById(int userId);

    /**
     * 根据用户id获取开户信息
     *
     * @param userId
     * @return
     */
    UserBankOpenAccountCustomize selectBankOpenAccountByUserId(int userId);

    /**
     * 根据用户id获取企业用户开户信息
     *
     * @param userId
     * @return
     */
    CorpOpenAccountRecord selectCorpOpenAccountRecordByUserId(int userId);

    /**
     * 根据用户id获取第三方平台绑定信息
     *
     * @param userId
     * @return
     */
    BindUser selectBindUserByUserId(int userId);

    /**
     * 根据用户id获取用户CA认证记录表
     *
     * @param userId
     * @return
     */
    CertificateAuthority selectCertificateAuthorityByUserId(int userId);

    /**
     * 根据用户id获取用户修改信息
     *
     * @param userId
     * @return
     */
    UserManagerUpdateCustomize selectUserUpdateInfoByUserId(int userId);

    /**
     * 更新用户信息
     *
     * @param request
     * @return
     */
    int updataUserInfo(UserManagerUpdateRequest request);

    /**
     * 校验手机号
     *
     * @param userId
     * @param mobile
     * @return
     */
    int countUserByMobile(int userId, String mobile);

    /**
     * 统计手机号
     * @param mobile
     * @return
     */
    int countByMobile(String mobile);

    /**
     * 校验推荐人
     *
     * @param userId
     * @param recommendName
     * @return
     */
    int selectCheckRecommend(int userId, String recommendName);

    /**
     * 根据用户id查找用户表
     *
     * @param userId
     * @param userId
     * @return
     */
    User selectUserByUserId(int userId);

    /**
     * 根据用户List id查找用户表
     *
     * @param userId
     * @param userId
     * @return
     */
    List<User> selectUserByListUserId (List userId);

    BankOpenAccount selectBankOpenAccountByAccountId(String accountId);

    /**
     * 更新企业用户开户记录
     *
     * @param corpOpenAccountRecord
     * @return
     */
    int updateCorpOpenAccountRecord(CorpOpenAccountRecord corpOpenAccountRecord);

    /**
     * 插入企业用户开户记录
     *
     * @param corpOpenAccountRecord
     * @return
     */
    int insertCorpOpenAccountRecord(CorpOpenAccountRecord corpOpenAccountRecord);

    /**
     * 单表查询开户信息
     *
     * @return
     */
    BankOpenAccount queryBankOpenAccountByUserId(int userId);

    BankOpenAccount queryBankOpenAccountByUserName(String userName);

    /**
     * 更新开户信息
     *
     * @param request
     * @return
     */
    int updateBankOpenAccount(BankOpenAccount request);

    /**
     * 插入开户信息
     *
     * @param request
     * @return
     */
    int insertBankOpenAccount(BankOpenAccount request);

    /**
     * 更新用户信息表
     *
     * @param userInfo
     * @return
     */
    int updateUserInfoByUserInfo(UserInfo userInfo);



    /**
     * 更新用户表
     *
     * @param user
     * @return
     */
    int updateUser(User user);



    /**
     * 获取某一用户的信息修改列表
     * @param mapParam
     * @return
     */
    List<UserChangeLog> queryChangeLogList(Map<String,Object> mapParam);
    /**
     * 获取推荐人姓名查找用户
     * @param recommendName
     * @return
     */
    User selectUserByRecommendName(String recommendName);

    /**
     * 根据用户id获取推荐信息
     * @param userId
     * @return
     */
    UserRecommendCustomize searchUserRecommend(int userId);

    /**
     * 修改推荐人信息
     * @param request
     * @return
     */
    int updateUserRe(AdminUserRecommendRequest request);
    /**
     * 修改用户身份证
     * @param request
     * @return
     */
    int updateUserIdCard(AdminUserRecommendRequest request);
    /**
     * 保存企业开户信息
     * @param updCompanyRequest
     * @return
     */
    Response saveCompanyInfo(UpdCompanyRequest updCompanyRequest);

	Integer getUserIdByBind(int bindUniqueId, int bindPlatformId);

    BindUser getUsersByUniqueId(Integer bindUniqueId, Integer bind_platform_id);

    Boolean bindThirdUser(Integer userId, Integer bindUniqueId, Integer bindPlatformId);

	String getBindUniqueIdByUserId(int userId, int bindPlatformId);
    /**
     * 根据关联关系查询OA表的内容,得到部门的线上线下属性
     * @param userId
     * @return
     */
    List<UserUpdateParamCustomize> queryUserAndDepartment(int userId);
    /**
     * 获取全部用户信息
     * @return
     */
    List<User> selectAllUser();
    /**
     * 查询此段时间的用户推荐人的修改记录
     * @param userId
     * @param repairStartDate
     * @param repairEndDate
     * @return
     */
    List<SpreadsUserLog> searchSpreadUsersLogByDate(Integer userId, String repairStartDate, String repairEndDate);
    /**
     * 查找员工信息(状态)
     * @param userId
     * @return
     */
     EmployeeCustomize selectEmployeeInfoByUserId(Integer userId);
    /**
     * 根据用户id获取离职信息
     * @param userId
     * @return
     */
    AdminEmployeeLeaveCustomize selectUserLeaveByUserId(Integer userId);
}
