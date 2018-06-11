package com.hyjf.am.trade.dao.mapper.auto;

import com.hyjf.am.trade.dao.model.auto.HjhUserAuthLog;
import com.hyjf.am.trade.dao.model.auto.HjhUserAuthLogExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface HjhUserAuthLogMapper {
    int countByExample(HjhUserAuthLogExample example);

    int deleteByExample(HjhUserAuthLogExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(HjhUserAuthLog record);

    int insertSelective(HjhUserAuthLog record);

    List<HjhUserAuthLog> selectByExample(HjhUserAuthLogExample example);

    HjhUserAuthLog selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") HjhUserAuthLog record, @Param("example") HjhUserAuthLogExample example);

    int updateByExample(@Param("record") HjhUserAuthLog record, @Param("example") HjhUserAuthLogExample example);

    int updateByPrimaryKeySelective(HjhUserAuthLog record);

    int updateByPrimaryKey(HjhUserAuthLog record);
}