/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.front.repay.impl;

import com.hyjf.am.trade.dao.mapper.auto.BorrowRecoverPlanMapper;
import com.hyjf.am.trade.dao.model.auto.BorrowRecoverPlan;
import com.hyjf.am.trade.dao.model.auto.BorrowRecoverPlanExample;
import com.hyjf.am.trade.service.front.repay.BorrowRecoverPlanService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 还款计划
 * @author hesy
 * @version BorrowRecoverPlanServiceImpl, v0.1 2018/6/26 18:15
 */
@Service
public class BorrowRecoverPlanServiceImpl implements BorrowRecoverPlanService {

    @Resource
    BorrowRecoverPlanMapper borrowRecoverPlanMapper;

    /**
     * 根据id检索BorrowRecoverPlan
     * @param id
     * @return
     */
    @Override
    public BorrowRecoverPlan selectRecoverPlanById(Integer id){
        return borrowRecoverPlanMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<BorrowRecoverPlan> selectRecoverPlan(String borrowNid, Integer period){
        BorrowRecoverPlanExample example = new BorrowRecoverPlanExample();
        BorrowRecoverPlanExample.Criteria crt = example.createCriteria();
        crt.andBorrowNidEqualTo(borrowNid);
        crt.andRecoverPeriodEqualTo(period);
        List<BorrowRecoverPlan> borrowRecovers = borrowRecoverPlanMapper.selectByExample(example);
        return borrowRecovers;
    }


    @Override
    public List<BorrowRecoverPlan> selectRecoverPlanListByTenderNid(String tenderNid) {
        BorrowRecoverPlanExample example = new BorrowRecoverPlanExample();
        BorrowRecoverPlanExample.Criteria criteria = example.createCriteria();
        criteria.andNidEqualTo(tenderNid);
        return borrowRecoverPlanMapper.selectByExample(example);
    }

    @Override
    public List<BorrowRecoverPlan> selectBorrowRecoverPlanListByNid(String nid) {
        BorrowRecoverPlanExample example = new BorrowRecoverPlanExample();
        example.createCriteria().andNidEqualTo(nid);
        example.setOrderByClause("recover_period asc");
        List<BorrowRecoverPlan> borrowRecoverPlanList = this.borrowRecoverPlanMapper.selectByExample(example);
        if (null != borrowRecoverPlanList && borrowRecoverPlanList.size() > 0) {
            return borrowRecoverPlanList;
        }
        return null;
    }
}
