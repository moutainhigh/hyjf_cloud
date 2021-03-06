package com.hyjf.am.trade.service.admin.finance.impl;

import com.hyjf.am.trade.dao.mapper.customize.EveLogCustomizeMapper;
import com.hyjf.am.trade.dao.model.customize.EveLogCustomize;
import com.hyjf.am.trade.service.admin.finance.BankEveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by cuigq on 2018/1/22.
 */
@Service
public class BankEveServiceImpl implements BankEveService {

    @Autowired
    private EveLogCustomizeMapper eveLogMapper;

    /**
     * 根据条件查询列表总数
     *
     * @param mapParam
     * @return
     */
    @Override
    public int countRecord(Map<String, Object> mapParam) {
        Integer count = eveLogMapper.countRecord(mapParam);
        int vipcount = count.intValue();
        return vipcount;
    }

    /**
     * 根据查询条件查询列表
     *
     * @param mapParam
     * @return
     */
    @Override
    public List<EveLogCustomize> selectBankEveInfoList(Map<String, Object> mapParam) {
        List<EveLogCustomize> manageList = eveLogMapper.selectBankEveInfoList(mapParam);
        return manageList;
    }

}
