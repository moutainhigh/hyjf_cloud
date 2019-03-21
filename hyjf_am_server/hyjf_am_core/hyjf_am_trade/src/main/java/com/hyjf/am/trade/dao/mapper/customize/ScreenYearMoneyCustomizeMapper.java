/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.dao.mapper.customize;

import com.hyjf.am.vo.trade.RepaymentPlanVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author yinhui
 * @version ActivityMidauRecodCustomizeMapper, v0.1 2018/9/8 15:13
 */
public interface ScreenYearMoneyCustomizeMapper {

    /**
     * 查询加出借散标的出借信息
     * @param orderId
     * @param userId
     * @return
     */
    String queryTenderList(@Param("orderId") String orderId, @Param("userId") Integer userId);


    /**
     * 查询加入汇计划的出借信息
     * @param orderId
     * @param userId
     * @return
     */
    String queryPlanList(@Param("orderId") String orderId, @Param("userId") Integer userId);


    /**
     * 批量插入本月待回款用户数据
     * @param repaymentPlanVOS
     * @return
     */
    Integer addRepayUserList(@Param("repaymentPlanVOS") List<RepaymentPlanVO> repaymentPlanVOS);

}
