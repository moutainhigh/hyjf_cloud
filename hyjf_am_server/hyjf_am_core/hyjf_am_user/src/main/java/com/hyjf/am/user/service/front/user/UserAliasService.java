/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.service.front.user;

import com.hyjf.am.user.dao.model.auto.UserAlias;
import com.hyjf.am.user.service.BaseService;
import com.hyjf.am.vo.user.UserAliasVO;

import java.util.List;

/**
 * @author fuqiang
 * @version UserAliasService, v0.1 2018/5/8 10:55
 */
public interface UserAliasService extends BaseService {
    /**
     * 根据手机号查询推送别名
     *
     * @param mobile
     * @return
     */
    UserAlias findAliasByMobile(String mobile);

    /**
     * 根据手机号查询推送别名 - 批量
     *
     * @param mobiles
     * @return
     */
    List<UserAliasVO> findAliasByMobiles(List<String> mobiles);

    /**
     * 根据设备类型统计用户人数
     * @param clientAndroid
     * @return
     */
    Integer countAliasByClient(String clientAndroid);

    void clearMobileCode(Integer userId, String sign);

    /**
     * 根据userd获取设备唯一标识
     * @param userId
     * @return
     */
    UserAlias findAliasesByUserId(Integer userId);

    /**
     * 更新唯一标识
     * @param userAliasVO
     */
    int updateAliases(UserAlias userAliasVO);

    /**
     * 插入唯一标识
     * @param userAlias
     */
    int insertMobileCode(UserAlias userAlias);
}
