package com.hyjf.am.trade.dao.customize;


import com.hyjf.am.trade.dao.auto.AutoMapper;
import com.hyjf.am.trade.dao.mapper.auto.BorrowApicronMapper;
import com.hyjf.am.trade.dao.mapper.auto.BorrowRepayMapper;
import com.hyjf.am.trade.dao.mapper.auto.BorrowRepayPlanMapper;
import com.hyjf.am.trade.dao.mapper.auto.HjhAccedeMapper;
import com.hyjf.am.trade.dao.mapper.customize.trade.BorrowCustomizeMapper;
import com.hyjf.am.trade.dao.mapper.customize.trade.HjhDebtDetailCustomizeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomizeMapper extends AutoMapper {

    @Autowired
    protected BorrowCustomizeMapper borrowCustomizeMapper;

    @Autowired
    protected HjhAccedeMapper hjhAccedeMapper;

    @Autowired
    protected HjhDebtDetailCustomizeMapper hjhDebtDetailCustomizeMapper;

    @Autowired
    protected BorrowApicronMapper borrowApicronMapper;

    @Autowired
    protected BorrowRepayPlanMapper borrowRepayPlanMapper;

    @Autowired
    protected BorrowRepayMapper borrowRepayMapper;
}