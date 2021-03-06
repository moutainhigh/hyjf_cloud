/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.service.batch;

/**
 * @author: sunpeikai
 * @version: UserPortraitBatchService, v0.1 2018/6/28 18:42
 */
public interface UserPortraitBatchService {
    /**
     * 更新用户画像 99:更新三个月的用户画像,else:更新昨日登录的用户画像
     * @auth sunpeikai
     * @param
     * @return
     */
    void userPortraitBatch(int flag);
}
