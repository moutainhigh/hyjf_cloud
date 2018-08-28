/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.controller.admin.finance;

import com.hyjf.am.response.IntegerResponse;
import com.hyjf.am.response.StringResponse;
import com.hyjf.am.response.admin.BankAccountManageCustomizeResponse;
import com.hyjf.am.resquest.admin.BankAccountManageRequest;
import com.hyjf.am.trade.controller.BaseController;
import com.hyjf.am.trade.dao.model.customize.BankAccountManageCustomize;
import com.hyjf.am.trade.service.admin.finance.BankAccountManageService;
import com.hyjf.am.vo.admin.AdminBankAccountCheckCustomizeVO;
import com.hyjf.am.vo.admin.BankAccountManageCustomizeVO;
import com.hyjf.am.vo.trade.account.AccountVO;
import com.hyjf.common.paginator.Paginator;
import com.hyjf.common.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author PC-LIUSHOUYI
 * @version BankAccountManageController, v0.1 2018/6/29 13:55
 */

@Api(value = "银行账户管理")
@RestController
@RequestMapping("/am-trade/bank_account_manage")
public class BankAccountManageController extends BaseController {

    @Autowired
    BankAccountManageService bankAccountManageService;

    /**
     * @Author: liushouyi
     * @Desc :查询总件数
     */
    @ApiOperation(value = "银行账户管理查询总件数")
    @PostMapping("/update_account")
    public IntegerResponse updateAccount(AccountVO accountVO) {
        return new IntegerResponse(bankAccountManageService.updateAccount(accountVO));
    }

    /**
     * @Author: liushouyi
     * @Desc :查询总件数
     */
    @ApiOperation(value = "银行账户管理查询总件数")
    @PostMapping("/update_account_check")
    public StringResponse updateAccountCheck(AdminBankAccountCheckCustomizeVO adminBankAccountCheckCustomizeVO) {
        return new StringResponse(bankAccountManageService.updateAccountCheck(adminBankAccountCheckCustomizeVO));
    }

    /**
     * @Author: liushouyi
     * @Desc :查询总件数
     */
    @ApiOperation(value = "银行账户管理查询总件数")
    @PostMapping("/query_account_count")
    public IntegerResponse queryAccountCount(BankAccountManageRequest bankAccountManageRequest) {
        return new IntegerResponse(bankAccountManageService.queryAccountCount(bankAccountManageRequest));
    }

    /**
     * @Author: liushouyi
     * @Desc :查询列表数据
     */
    @ApiOperation(value = "银行账户管理查询列表")
    @PostMapping("/query_account_infos")
    public BankAccountManageCustomizeResponse queryAccountInfos(@RequestBody BankAccountManageRequest bankAccountManageRequest) {
        BankAccountManageCustomizeResponse response = new BankAccountManageCustomizeResponse();
        Integer recordTotal = bankAccountManageService.queryAccountCount(bankAccountManageRequest);
        Paginator paginator = new Paginator(bankAccountManageRequest.getCurrPage(), recordTotal,bankAccountManageRequest.getPageSize());
        bankAccountManageRequest.setLimitStart(paginator.getOffset());
        bankAccountManageRequest.setLimitEnd(paginator.getLimit());

        List<BankAccountManageCustomize> bankAccountManageCustomizes = bankAccountManageService.queryAccountInfos(bankAccountManageRequest);
        if (null != bankAccountManageCustomizes && bankAccountManageCustomizes.size() > 0) {
            List<BankAccountManageCustomizeVO> bankAccountManageCustomizeVOS = CommonUtils.convertBeanList(bankAccountManageCustomizes, BankAccountManageCustomizeVO.class);
            response.setResultList(bankAccountManageCustomizeVOS);
        }
        return response;
    }
}
