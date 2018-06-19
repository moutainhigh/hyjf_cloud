/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.service.regist;

import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.exception.ReturnMessageException;
import com.hyjf.cs.user.service.BaseUserService;
import com.hyjf.cs.user.vo.RegisterVO;

/**
 * @author zhangqingqing
 * @version RegistService, v0.1 2018/6/11 15:09
 */
public interface RegistService extends BaseUserService {



    void registerCheckParam(int client, RegisterVO registerVO);

    boolean existUser(String mobile);

    /**
     * 注册
     * @param registerVO
     * @param ip
     * @return
     * @throws ReturnMessageException
     */
    UserVO register(RegisterVO registerVO, String ip)
            throws ReturnMessageException;

    /**
     * api端注册
     * @param registerVO
     * @param ipAddr
     * @return
     */
    UserVO apiRegister(RegisterVO registerVO, String ipAddr);

    /**
     * 检查活动是否有效
     * @param activityId
     * @return
     */
    boolean checkActivityIfAvailable(Integer activityId);

    int updateCheckMobileCode(String mobile, String code, String validCodeType, String clientPc, Integer ckcodeYiyan, Integer ckcodeYiyan1);

    int countUserByRecommendName(String recommend);
}
