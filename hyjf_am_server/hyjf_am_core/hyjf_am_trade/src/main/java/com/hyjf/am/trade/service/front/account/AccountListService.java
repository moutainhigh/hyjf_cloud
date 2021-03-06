package com.hyjf.am.trade.service.front.account;

import com.hyjf.am.resquest.ApiTransactionDetailsRequest;
import com.hyjf.am.trade.dao.model.auto.AccountList;
import com.hyjf.am.trade.dao.model.customize.ApiTransactionDetailsCustomize;
import com.hyjf.am.vo.trade.account.AccountListVO;
import com.hyjf.am.vo.trade.account.AccountVO;

import java.util.List;

/**
 * @author jijun
 * @version AccountService, v0.1 2018/06/16
 */
public interface AccountListService {

	int addAccountList(AccountList convertBean);

    AccountList countAccountListByOrdId(String ordId, String type);

    Integer insertAccountListSelective(AccountListVO accountListVO);

    Integer updateOfPlanRepayAccount( AccountVO accountVO);

    AccountList countAccountListByOrderId(String orderId);

    int insertAccountList(AccountList accountList);

    /**
     * @Author walter.limeng
     * @Description  根据nid和trade查询收支明细
     * @Date 10:11 2018/7/18
     * @Param nid
     * @Param trade
     * @return
     */
    Integer countByNidAndTrade(String nid, String trade);

    /**
     * 第三方交易明细查询
     * @param detailsRequest
     * @return
     * @Author : huanghui
     */
    List<ApiTransactionDetailsCustomize> selectTransactionDetails(ApiTransactionDetailsRequest detailsRequest);
}
