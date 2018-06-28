/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.resquest.user.UserChangeLogRequest;
import com.hyjf.am.resquest.user.UserManagerRequest;
import com.hyjf.am.vo.trade.CorpOpenAccountRecordVO;
import com.hyjf.am.vo.user.*;

import java.util.List;
import java.util.Map;

/**
 * @author nxl
 * @version UserCenterService, v0.1 2018/6/20 15:34
 */
public interface UserCenterService {

    JSONObject initUserManaget();
    /**
     *查找用户信息
     * @param request
     * @return
     */
    JSONObject selectUserMemberList(UserManagerRequest request);

    /**
     * 根据机构编号获取机构列表
     * @return
     */
    List<HjhInstConfigVO> selectInstConfigAll();

    /**
     * 根据用户id获取用户详情
     * @param userId
     * @return
     */
    UserManagerDetailVO selectUserDetail(String userId);

    /**
     * 根据用户id获取测评信息
     * @param userId
     * @return
     */
    UserEvalationResultVO getUserEvalationResult(String userId);
    /**
     * 根据用户id获取开户信息
     * @param userId
     * @return
     */
    UserBankOpenAccountVO selectBankOpenAccountByUserId(String userId);
    /**
     * 根据用户id获取企业用户开户信息
     * @param userId
     * @return
     */
    CorpOpenAccountRecordVO selectCorpOpenAccountRecordByUserId(String userId);
    /**
     * 根据用户id获取第三方平台绑定信息
     * @param userId
     * @return
     */
    BindUserVo selectBindeUserByUserI(String userId);

    /**
     * 根据用户id获取用户CA认证记录表
     * @param userId
     * @return
     */
    CertificateAuthorityVO selectCertificateAuthorityByUserId(String userId);

    /**
     * 根据用户id获取用户修改信息
     * @param userId
     * @return
     */
    UserManagerUpdateVO selectUserUpdateInfoByUserId (String userId);

    /**
     * 更新用户信息
     */
    int updataUserInfo( Map<String, String> map);

    /**
     * 根据用户id获取推荐人信息
     * @param userId
     * @return
     */
    UserRecommendCustomizeVO selectUserRecommendByUserId(String userId);

    /**
     * 校验手机号
     * @param userId
     * @param mobile
     * @return
     */
    int countUserByMobile(int userId, String mobile);

    /**
     * 校验推荐人
     * @param userId
     * @param recommendName
     * @return
     */
    int checkRecommend(String userId, String recommendName);
    /**
     * @Description 根据accountid调用接口查找企业信息
     */
    CompanyInfoVO queryCompanyInfoByAccoutnId(Integer userId, String accountId);
    /**
     * 根据用户id查找用户表
     * @param userId
     * @param userId
     * @return
     */
    UserVO selectUserByUserId(String userId);

    /**
     * 根据用户id查找企业信息
     * @param userId
     * @return
     */
    CompanyInfoVO selectCompanyInfoByUserId(String userId);
    /**
     * 根據accounId獲取開戶信息
     * @param accountId
     * @return
     */
    BankOpenAccountVO selectBankOpenAccountByAccountId(String accountId);

    /**
     * 保存企业信息
     * @return
     */
    JSONObject saveCompanyInfo(Map<String,String> mapParam);
    /**
     * 获取某一用户的信息修改列表
     * @param request
     * @return
     */
    List<UserChangeLogVO> selectUserChageLog(UserChangeLogRequest request);
//    /**
//     * 根据用户id查找推荐人信息
//     * @param userId
//     * @return
//     */
//    UserRecommendCustomizeVO selectUserRecommendUserId(String userId);
}
