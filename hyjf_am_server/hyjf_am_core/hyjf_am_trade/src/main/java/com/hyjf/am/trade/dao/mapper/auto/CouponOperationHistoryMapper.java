package com.hyjf.am.trade.dao.mapper.auto;

import com.hyjf.am.trade.dao.model.auto.CouponOperationHistory;
import com.hyjf.am.trade.dao.model.auto.CouponOperationHistoryExample;
import com.hyjf.am.trade.dao.model.auto.CouponOperationHistoryWithBLOBs;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CouponOperationHistoryMapper {
    int countByExample(CouponOperationHistoryExample example);

    int deleteByExample(CouponOperationHistoryExample example);

    int deleteByPrimaryKey(String uuid);

    int insert(CouponOperationHistoryWithBLOBs record);

    int insertSelective(CouponOperationHistoryWithBLOBs record);

    List<CouponOperationHistoryWithBLOBs> selectByExampleWithBLOBs(CouponOperationHistoryExample example);

    List<CouponOperationHistory> selectByExample(CouponOperationHistoryExample example);

    CouponOperationHistoryWithBLOBs selectByPrimaryKey(String uuid);

    int updateByExampleSelective(@Param("record") CouponOperationHistoryWithBLOBs record, @Param("example") CouponOperationHistoryExample example);

    int updateByExampleWithBLOBs(@Param("record") CouponOperationHistoryWithBLOBs record, @Param("example") CouponOperationHistoryExample example);

    int updateByExample(@Param("record") CouponOperationHistory record, @Param("example") CouponOperationHistoryExample example);

    int updateByPrimaryKeySelective(CouponOperationHistoryWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(CouponOperationHistoryWithBLOBs record);

    int updateByPrimaryKey(CouponOperationHistory record);
}