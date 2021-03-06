/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.response.admin;

import com.hyjf.am.response.Response;
import com.hyjf.am.vo.admin.HjhLabelCustomizeVO;

/**
 * @author libin
 * @version HjhLabelCustomizeResponse.java, v0.1 2018年6月30日 下午2:36:00
 */
public class HjhLabelCustomizeResponse extends Response<HjhLabelCustomizeVO>{
    // 数据查询条数 主要用于分页情况，原子层向组合层返回
    private  Integer  count;

    public Integer getCount() {
        return count;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
}
