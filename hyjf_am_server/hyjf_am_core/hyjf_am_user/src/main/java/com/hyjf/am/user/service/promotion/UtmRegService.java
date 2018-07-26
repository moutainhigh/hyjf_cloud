/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.service.promotion;

import com.hyjf.am.user.dao.model.auto.UtmReg;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author fuqiang
 * @version UtmRegService, v0.1 2018/7/17 9:14
 */
public interface UtmRegService {
    /**
     * 获取utm注册列表
     * @return
     */
    List<UtmReg> getUtmRegList(Integer sourceId, String type);

    /**
     * 查询相应的app渠道主单注册数
     * @param list
     * @return
     */
    BigDecimal getRegisterAttrCount(List<Integer> list);

    /**
     * 查询相应的app渠道开户数
     * @param list
     * @return
     */
    Integer getAccountNumber(List<Integer> list, String type);
}