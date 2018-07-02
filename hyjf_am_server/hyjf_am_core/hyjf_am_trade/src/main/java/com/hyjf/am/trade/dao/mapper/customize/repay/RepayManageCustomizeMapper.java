package com.hyjf.am.trade.dao.mapper.customize.repay;

import com.hyjf.am.vo.trade.repay.RepayListCustomizeVO;

import java.util.List;
import java.util.Map;

/**
 * @author hesy
 * @version RepayManageCustomizeMapper, v0.1 2018/6/27 15:49
 */
public interface RepayManageCustomizeMapper {
    List<RepayListCustomizeVO> selectRepayList(Map<String,Object> paraMap);

    Integer selectRepayCount(Map<String,Object> paraMap);

    List<RepayListCustomizeVO> selectOrgRepayList(Map<String,Object> paraMap);

    Integer selectOrgRepayCount(Map<String,Object> paraMap);
}
