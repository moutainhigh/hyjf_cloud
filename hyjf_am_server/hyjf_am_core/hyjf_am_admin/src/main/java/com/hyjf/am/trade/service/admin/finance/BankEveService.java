package com.hyjf.am.trade.service.admin.finance;


import java.util.List;
import java.util.Map;

import com.hyjf.am.trade.dao.model.customize.EveLogCustomize;

/**
 * 银行卡接口
 * @author zdj
 */
public interface BankEveService {

    /**
     * 查询银行账务明细
     * @param mapParam
     */
    int countRecord(Map<String, Object> mapParam);

    /**
     * 查询银行账务明细
     * @param mapParam
     */
    List<EveLogCustomize> selectBankEveInfoList(Map<String, Object> mapParam, int offset, int limit);


}
