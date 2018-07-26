/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.service.withdraw;

import com.hyjf.am.vo.trade.BankConfigVO;
import com.hyjf.am.vo.user.AccountBankVO;
import com.hyjf.cs.common.service.BaseService;

import java.util.List;

/**
 * @author: sunpeikai
 * @version: UserWithdrawService, v0.1 2018/7/23 15:18
 */
public interface UserWithdrawService extends BaseService {
    /**
     * 根据userId获取accountBank
     * @auth sunpeikai
     * @param userId 用户id
     * @return
     */
    List<AccountBankVO> getBankCardByUserId(Integer userId);

    /**
     * 根据银行名查询银行配置
     * @auth sunpeikai
     * @param bank 银行code，例如：招商银行,CMB
     * @return
     */
    BankConfigVO getBankInfo(String bank);
}