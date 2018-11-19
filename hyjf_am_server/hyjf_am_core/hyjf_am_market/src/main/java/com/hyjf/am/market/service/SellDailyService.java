/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.market.service;

import com.hyjf.am.market.dao.model.auto.SellDaily;

import java.util.List;

/**
 * @author yaoyong
 * @version SellDailyService, v0.1 2018/11/16 17:54
 */
public interface SellDailyService {
    /**
     * 根据统计时间查询销售日报
     * @param dateStr
     * @return
     */
    List<SellDaily> selectDailyByDateStr(String dateStr);

    /**
     * 根据类型查询合计  type:1
     * @param formatDateStr
     * @return
     */
    SellDaily selectOCSum(String formatDateStr);

    /**
     * 根据类型查询合计  type:2
     * @param dateStr
     * @param drawOrder
     * @return
     */
    SellDaily selectPrimaryDivisionSum(String dateStr, int drawOrder);

    /**
     * 根据类型查询合计 type:3
     * @param dateStr
     * @return
     */
    SellDaily selectAllSum(String dateStr);
}
