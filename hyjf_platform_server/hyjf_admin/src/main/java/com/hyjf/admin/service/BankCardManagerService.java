/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service;

import com.hyjf.am.resquest.user.BankCardManagerRequest;
import com.hyjf.am.resquest.user.RegistRcordRequest;
import com.hyjf.am.vo.trade.BanksConfigVO;
import com.hyjf.am.vo.user.BankcardManagerVO;
import com.hyjf.am.vo.user.RegistRecordVO;

import java.util.List;

/**
 * @author nxl
 * @version UserCenterService, v0.1 2018/6/20 15:34
 */
public interface BankCardManagerService {

    /**
     * 获取银行列表
     *
     * @return
     */
    List<BanksConfigVO> selectBankConfigList();

    /**
     *  根据筛选条件查找汇付银行卡信息总数
     * @param request 筛选条件
     * @return
     */
    int countBankCardList(BankCardManagerRequest request);

    /**
     *  根据筛选条件查找汇付银行卡信息列表
     * @param request 筛选条件
     * @return
     */
     List<BankcardManagerVO> selectBankCardList(BankCardManagerRequest request, int limitStart, int limit);

    /**
     * 根据筛选条件查找江西银行卡信息列表
     * @return
     */
    List<BankcardManagerVO> selectNewBankCardList (BankCardManagerRequest request);


}
