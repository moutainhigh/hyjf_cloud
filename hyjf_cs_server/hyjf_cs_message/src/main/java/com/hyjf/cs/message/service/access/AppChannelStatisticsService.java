package com.hyjf.cs.message.service.access;

import java.util.List;

import com.hyjf.am.resquest.admin.AppChannelStatisticsRequest;
import com.hyjf.am.vo.datacollect.AppAccesStatisticsVO;

/**
 * @author：yinhui
 * @Date: 2018/10/22  16:39
 */
public interface AppChannelStatisticsService {

    List<AppAccesStatisticsVO> getAppAccesStatisticsVO(AppChannelStatisticsRequest request);

//    List<AppUtmRegVO> getAppChannelStatisticsDetailVO(AppChannelStatisticsRequest request);
}
