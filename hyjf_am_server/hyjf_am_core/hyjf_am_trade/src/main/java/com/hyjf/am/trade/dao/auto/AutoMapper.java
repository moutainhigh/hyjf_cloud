package com.hyjf.am.trade.dao.auto;


import com.hyjf.am.trade.dao.mapper.auto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AutoMapper {

    @Autowired
    protected BorrowMapper borrowMapper;

    @Autowired
    protected BorrowInfoMapper borrowInfoMapper;

    @Autowired
    protected BorrowFinmanNewChargeMapper borrowFinmanNewChargeMapper;

    @Autowired
    protected BorrowConfigMapper borrowConfigMapper;

    @Autowired
    protected BorrowManinfoMapper borrowManinfoMapper;

    @Autowired
    protected BorrowStyleMapper borrowStyleMapper;

    @Autowired
    protected BorrowTenderTmpMapper borrowTenderTmpMapper;

    @Autowired
    protected BorrowTenderTmpinfoMapper borrowTenderTmpinfoMapper;
}
