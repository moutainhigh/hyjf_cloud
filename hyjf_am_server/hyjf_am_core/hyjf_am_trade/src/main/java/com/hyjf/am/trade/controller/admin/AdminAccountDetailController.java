/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.AccountDetailResponse;
import com.hyjf.am.response.admin.AdminAccountDetailDataRepairResponse;
import com.hyjf.am.response.trade.AccountListResponse;
import com.hyjf.am.response.trade.AccountTradeResponse;
import com.hyjf.am.resquest.admin.AccountDetailRequest;
import com.hyjf.am.resquest.admin.AccountListRequest;
import com.hyjf.am.trade.dao.model.auto.AccountList;
import com.hyjf.am.trade.dao.model.auto.AccountTrade;
import com.hyjf.am.trade.dao.model.customize.admin.AdminAccountDetailCustomize;
import com.hyjf.am.trade.dao.model.customize.admin.AdminAccountDetailDataRepairCustomize;
import com.hyjf.am.trade.service.admin.AccountDetailService;
import com.hyjf.am.vo.admin.AccountDetailVO;
import com.hyjf.am.vo.admin.AdminAccountDetailDataRepairVO;
import com.hyjf.am.vo.trade.AccountTradeVO;
import com.hyjf.am.vo.trade.account.AccountListVO;
import com.hyjf.common.paginator.Paginator;
import com.hyjf.common.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nxl
 * @version AdminAccountDetailController, v0.1 2018/6/29 13:53
 */
@RestController
@RequestMapping("/am-trade/adminaccountdetail")
public class AdminAccountDetailController {

    @Autowired
    private AccountDetailService accountDetailService;

    private static Logger logger = LoggerFactory.getLogger(AdminAccountDetailController.class);

    @RequestMapping(value = "/accountdetaillist", method = RequestMethod.POST)
    public AccountDetailResponse accountDetailList(@RequestBody @Valid AccountDetailRequest request) {
        logger.info("---accountdetaillist by param---  " + JSONObject.toJSON(request));
        AccountDetailResponse response = new AccountDetailResponse();
        String returnCode = Response.FAIL;
        Map<String, Object> mapParam = paramSet(request);
        int intCountAccountDetail = accountDetailService.countAccountDetail(mapParam);
        Paginator paginator = new Paginator(request.getCurrPage(), intCountAccountDetail, request.getPageSize());
        if (request.getPageSize() == 0) {
            paginator = new Paginator(request.getCurrPage(), intCountAccountDetail);
        }
        mapParam.put("limitStart", paginator.getOffset());
        mapParam.put("limitEnd", paginator.getLimit());
        if (!request.isLimitFlg()) {
            //代表获取全部
            mapParam.put("limitStart", 0);
            mapParam.put("limitEnd", 0);
        }
        List<AdminAccountDetailCustomize> userManagerCustomizeList = accountDetailService.queryAccountDetails(mapParam);
        if (intCountAccountDetail > 0) {
            if (!CollectionUtils.isEmpty(userManagerCustomizeList)) {
                List<AccountDetailVO> userVoList = CommonUtils.convertBeanList(userManagerCustomizeList, AccountDetailVO.class);
                response.setResultList(userVoList);
                response.setRecordTotal(String.valueOf(intCountAccountDetail));
                returnCode = Response.SUCCESS;
            }
        }
        response.setRtn(returnCode);
        return response;
    }

    /**
     * 查询条件设置
     *
     * @param userRequest
     * @return
     */
    private Map<String, Object> paramSet(AccountDetailRequest userRequest) {
        Map<String, Object> mapParam = new HashMap<String, Object>();
        //
        mapParam.put("userId", userRequest.getUserId());
        //
        mapParam.put("userName", userRequest.getUsername());
        mapParam.put("referrerName", userRequest.getReferrerName());
        mapParam.put("nid", userRequest.getNid());
        mapParam.put("accountId", userRequest.getAccountId());
        mapParam.put("seqNo", userRequest.getSeqNo());
        mapParam.put("isBank", userRequest.getIsBank());
        mapParam.put("checkStatus", userRequest.getCheckStatus());
        mapParam.put("tradeStatus", userRequest.getTradeStatus());
        mapParam.put("typeSearch", userRequest.getTypeSearch());
        mapParam.put("tradeTypeSearch", userRequest.getTradeTypeSearch());
        mapParam.put("startDate", userRequest.getStartDate());
        mapParam.put("endDate", userRequest.getEndDate());
        mapParam.put("remarkSrch", userRequest.getRemark());
        //mapParam.put("limit",userRequest.getPageSize());

        return mapParam;
    }

    //查询出20170120还款后,交易明细有问题的用户ID
    @GetMapping("/queryaccountdetailerroruserlist")
    public AdminAccountDetailDataRepairResponse queryAccountDetailErrorUserList() {
        AdminAccountDetailDataRepairResponse repairResponse = new AdminAccountDetailDataRepairResponse();
        String returnCode = Response.FAIL;
        List<AdminAccountDetailDataRepairCustomize> accountDetailDataRepairCustomizeList = accountDetailService.queryAccountDetailErrorUserList();
        if (null != accountDetailDataRepairCustomizeList && accountDetailDataRepairCustomizeList.size() > 0) {
            List<AdminAccountDetailDataRepairVO> userVoList = CommonUtils.convertBeanList(accountDetailDataRepairCustomizeList, AdminAccountDetailDataRepairVO.class);
            repairResponse.setResultList(userVoList);
            returnCode = Response.SUCCESS;
        }
        repairResponse.setRtn(returnCode);
        return repairResponse;
    }

    //查询交易明细最小的id
    @GetMapping("/queryaccountdetailidbyuserid/{userId}")
    public AdminAccountDetailDataRepairResponse queryAccountDetailIdByUserId(@PathVariable String userId) {
        AdminAccountDetailDataRepairResponse repairResponse = new AdminAccountDetailDataRepairResponse();
        String returnCode = Response.FAIL;
        if (StringUtils.isNotBlank(userId)) {
            int intUserId = Integer.parseInt(userId);
            AdminAccountDetailDataRepairCustomize adminAccountDetailDataRepairCustomize = accountDetailService.queryAccountDetailIdByUserId(intUserId);
            if (null != adminAccountDetailDataRepairCustomize) {
                AdminAccountDetailDataRepairVO adminAccountDetailDataRepairVO = new AdminAccountDetailDataRepairVO();
                BeanUtils.copyProperties(adminAccountDetailDataRepairCustomize, adminAccountDetailDataRepairVO);
                repairResponse.setResult(adminAccountDetailDataRepairVO);
                returnCode = Response.SUCCESS;
            }
        }
        repairResponse.setRtn(returnCode);
        return repairResponse;
    }

    // 根据Id查询此条交易明细
    @GetMapping("/selectaccountbyid/{accountId}")
    public AccountListResponse selectAccountById(@PathVariable String accountId) {
        AccountListResponse accountListResponse = new AccountListResponse();
        String returnCode = Response.FAIL;
        if (StringUtils.isNotBlank(accountId)) {
            int intAccount = Integer.parseInt(accountId);
            AccountList accountList = accountDetailService.selectAccountById(intAccount);
            if (null != accountList) {
                AccountListVO accountListVO = new AccountListVO();
                BeanUtils.copyProperties(accountList, accountListVO);
                accountListResponse.setResult(accountListVO);
                returnCode = Response.SUCCESS;
            }
        }
        accountListResponse.setRtn(returnCode);
        return accountListResponse;
    }

    // 查询此用户的下一条交易明细
    @RequestMapping(value = "/selectnextaccountlist/{userID}/{accountId}", method = RequestMethod.POST)
    public AccountListResponse selectNextAccountList(@PathVariable String userId, @PathVariable String accountId) {
        AccountListResponse accountListResponse = new AccountListResponse();
        String returnCode = Response.FAIL;
        if (StringUtils.isNotBlank(accountId) && StringUtils.isNotBlank(userId)) {
            int intAccount = Integer.parseInt(accountId);
            int intUserId = Integer.parseInt(userId);
            AccountList accountList = accountDetailService.selectNextAccountList(intAccount, intUserId);
            if (null != accountList) {
                AccountListVO accountListVO = new AccountListVO();
                BeanUtils.copyProperties(accountList, accountListVO);
                accountListResponse.setResult(accountListVO);
                returnCode = Response.SUCCESS;
            }
        }
        accountListResponse.setRtn(returnCode);
        return accountListResponse;
    }

    // 根据查询用交易类型查询用户操作金额
    @RequestMapping(value = "/selectaccounttradebyvalue/{tradeValue}", method = RequestMethod.POST)
    public AccountTradeResponse selectAccountTradeByValue(@PathVariable String tradeValue) {
        AccountTradeResponse accountTradeResponse = new AccountTradeResponse();
        String returnCode = Response.FAIL;
        if (StringUtils.isNotBlank(tradeValue)) {
            AccountTrade accountTrade = accountDetailService.selectAccountTradeByValue(tradeValue);
            if (null != accountTrade) {
                AccountTradeVO accountTradeVO = new AccountTradeVO();
                BeanUtils.copyProperties(accountTrade, accountTradeVO);
                accountTradeResponse.setResult(accountTradeVO);
                returnCode = Response.SUCCESS;
            }
        }
        accountTradeResponse.setRtn(returnCode);
        return accountTradeResponse;
    }

    // 更新用户的交易明细
    @RequestMapping(value = "/updateaccountlist", method = RequestMethod.POST)
    public int selectAccountTradeByValue(@RequestBody @Valid AccountListRequest request) {
        return accountDetailService.updateAccountList(request);
    }

}