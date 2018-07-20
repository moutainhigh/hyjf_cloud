/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.message.service.message;

import com.hyjf.am.resquest.message.SmsLogRequest;
import com.hyjf.cs.message.bean.mc.SmsLog;

import java.util.List;

/**
 * @author fuqiang
 * @version SmsLogService, v0.1 2018/6/23 14:23
 */
public interface SmsLogService {
    /**
     * 根据条件查询短信发送记录
     * @param request
     * @return
     */
    List<SmsLog> findSmsLog(SmsLogRequest request);
}
