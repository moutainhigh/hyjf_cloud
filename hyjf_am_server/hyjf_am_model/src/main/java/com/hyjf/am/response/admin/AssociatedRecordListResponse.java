/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.response.admin;

import com.hyjf.am.response.Response;
import com.hyjf.am.vo.admin.AssociatedRecordListVO;

/**
 * @author: sunpeikai
 * @version: AssociatedRecordListResponse, v0.1 2018/7/5 14:30
 */
public class AssociatedRecordListResponse extends Response<AssociatedRecordListVO> {
    private long count;

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
