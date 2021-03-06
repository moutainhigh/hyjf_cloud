/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.service.front.userlargescreen;

import com.hyjf.am.response.trade.ScreenTransferResponse;
import com.hyjf.am.response.user.UserCustomerTaskConfigResponse;
import com.hyjf.am.response.user.UserScreenConfigResponse;
import com.hyjf.am.resquest.admin.UserLargeScreenRequest;
import com.hyjf.am.vo.api.MonthDataStatisticsVO;
import com.hyjf.am.vo.screen.ScreenTransferVO;

import java.util.List;

/**
 * @author tanyy
 * @version UserLargeScreenService, v0.1 2019/3/18 14:28
 */
public interface UserLargeScreenService {
    /**
     * 大屏幕运营部配置获取
     * @return
     */
    UserScreenConfigResponse getScreenConfig(UserLargeScreenRequest request);
    /**
     * 坐席月任务配置
     * @return
     */
    UserCustomerTaskConfigResponse getCustomerTaskConfig(UserLargeScreenRequest request);

    /**
     * 所有坐席和坐席下用户查询
     * @return
     */
    List<MonthDataStatisticsVO> getCurrentOwnersAndUserIds();

    /**
     * @Author walter.limeng
     * @Description //获取投屏采集的所有的用户信息
     * @Date 16:17 2019-05-29
     * @Param [userList]
     * @return com.hyjf.am.response.trade.ScreenTransferResponse
     **/
    ScreenTransferResponse getScreenTransferData(List<ScreenTransferVO> userList);
}
