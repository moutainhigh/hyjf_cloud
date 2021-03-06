package com.hyjf.am.trade.service.admin.finance;


import com.hyjf.am.trade.dao.model.customize.EveLogCustomize;

import java.util.List;
import java.util.Map;

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
    List<EveLogCustomize> selectBankEveInfoList(Map<String, Object> mapParam);


}
