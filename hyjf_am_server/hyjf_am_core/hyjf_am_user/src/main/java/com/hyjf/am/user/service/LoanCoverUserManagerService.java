/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.service;

import com.hyjf.am.resquest.user.LoanCoverUserRequest;
import com.hyjf.am.user.dao.model.auto.LoanSubjectCertificateAuthority;

import java.util.List;

/**
 * @author nxl
 * @version UserManagerService, v0.1 2018/6/20 9:47
 *          后台管理系统：会员中心->借款盖章用户
 */
public interface LoanCoverUserManagerService {

    /**
     * 根据筛选条件查找借款盖章用户
     *
     * @param lsca
     * @param limitStart
     * @param limitEnd
     * @param createStart
     * @param createEnd
     * @return
     */
    List<LoanSubjectCertificateAuthority> getRecordList(LoanCoverUserRequest lsca, int limitStart,
                                                        int limitEnd, int createStart, int createEnd);
    /**
     * 根据条件获取记录数
     * @param lsca
     * @param createStart
     * @param createEnd
     * @return
     */
    int countLoanSubjectCertificateAuthority (LoanCoverUserRequest lsca,int createStart, int createEnd);

    /**
     * 保存借款盖章用户
     *
     * @param request
     * @return
     */
    int insertLoanCoverUserRecord(LoanCoverUserRequest request);
    /**
     * 根据证件号码查找借款主体CA认证记录表
     */
    LoanSubjectCertificateAuthority selectIsExistsRecordByIdNo(String record);
    /**
     * 更新借款盖章用户
     *
     * @param request
     * @return
     */
   int updateLoanCoverUserRecord(LoanCoverUserRequest request);
    /**
     * 根据证件号码查找借款主体CA认证记录表
     */
    LoanSubjectCertificateAuthority selectIsExistsRecordById(String id);
}