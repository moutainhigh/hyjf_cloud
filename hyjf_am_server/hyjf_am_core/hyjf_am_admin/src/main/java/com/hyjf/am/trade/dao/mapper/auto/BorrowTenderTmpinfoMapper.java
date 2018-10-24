package com.hyjf.am.trade.dao.mapper.auto;

import com.hyjf.am.trade.dao.model.auto.BorrowTenderTmpinfo;
import com.hyjf.am.trade.dao.model.auto.BorrowTenderTmpinfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BorrowTenderTmpinfoMapper {
    int countByExample(BorrowTenderTmpinfoExample example);

    int deleteByExample(BorrowTenderTmpinfoExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(BorrowTenderTmpinfo record);

    int insertSelective(BorrowTenderTmpinfo record);

    List<BorrowTenderTmpinfo> selectByExample(BorrowTenderTmpinfoExample example);

    BorrowTenderTmpinfo selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") BorrowTenderTmpinfo record, @Param("example") BorrowTenderTmpinfoExample example);

    int updateByExample(@Param("record") BorrowTenderTmpinfo record, @Param("example") BorrowTenderTmpinfoExample example);

    int updateByPrimaryKeySelective(BorrowTenderTmpinfo record);

    int updateByPrimaryKey(BorrowTenderTmpinfo record);
}