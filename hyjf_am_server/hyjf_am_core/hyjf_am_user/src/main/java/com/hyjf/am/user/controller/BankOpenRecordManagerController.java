/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.user.BankAccountRecordResponse;
import com.hyjf.am.resquest.user.AccountRecordRequest;
import com.hyjf.am.resquest.user.BankAccountRecordRequest;
import com.hyjf.am.user.dao.model.customize.BankOpenAccountRecordCustomize;
import com.hyjf.am.user.service.BankOpenRecordManagerService;
import com.hyjf.am.vo.user.BankOpenAccountRecordVO;
import com.hyjf.common.paginator.Paginator;
import com.hyjf.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nxl
 * @version UserMemberController, v0.1 2018/6/20 9:13
 *          后台会员中心-会员管理
 */

@RestController
@RequestMapping("/am-user/bankOpenAccountRecord")
public class BankOpenRecordManagerController {
    @Autowired
    private BankOpenRecordManagerService bankOpenRecordService;
    private static Logger logger = LoggerFactory.getLogger(BankOpenRecordManagerController.class);

    /**
     * 根据筛选条件查找(查找江西銀行開戶記錄)
     *
     * @param request
     * @return
     */
    @RequestMapping("/findBankAccountRecordList")
    public BankAccountRecordResponse findBankAccountRecordList(@RequestBody @Valid BankAccountRecordRequest request) {
        logger.info("---findBankAccountRecordList by param---  " + JSONObject.toJSON(request));
        BankAccountRecordResponse response = new BankAccountRecordResponse();
        Map<String,String> mapParam = setBankAccountRecordRequest(request);
        int countBankRecordTotal = bankOpenRecordService.countBankRecordTotal(mapParam);
        Paginator paginator = new Paginator(request.getPaginatorPage(), countBankRecordTotal,request.getLimit());
        List<BankOpenAccountRecordCustomize> bankOpenAccountRecordCustomizeList = bankOpenRecordService.selectBankAccountList(mapParam,paginator.getOffset(), paginator.getLimit());
        if (countBankRecordTotal > 0) {
            if (!CollectionUtils.isEmpty(bankOpenAccountRecordCustomizeList)) {
                List<BankOpenAccountRecordVO> userBankRecord = CommonUtils.convertBeanList(bankOpenAccountRecordCustomizeList, BankOpenAccountRecordVO.class);
                response.setResultList(userBankRecord);
                response.setCount(countBankRecordTotal);
                response.setRtn(Response.SUCCESS);//代表成功
            }
        }
        return response;
    }

    /**
     * 根据筛选条件查找(汇付银行开户记录)
     *
     * @param request
     * @return
     */
    @RequestMapping("/findAccountRecordList")
    public BankAccountRecordResponse findAccountRecordList(@RequestBody @Valid AccountRecordRequest request) {
        logger.info("---findAccountRecordList by param---  " + JSONObject.toJSON(request));
        BankAccountRecordResponse response = new BankAccountRecordResponse();
        Map<String,String> mapParam = setAccountRecordRequest(request);
        int countRecordTotal = bankOpenRecordService.countRecordTotal(mapParam);
        Paginator paginator = new Paginator(request.getPaginatorPage(), countRecordTotal,request.getLimit());
        List<BankOpenAccountRecordCustomize> accountList = bankOpenRecordService.selectAccountList(mapParam,paginator.getOffset(), paginator.getLimit());
        if (countRecordTotal > 0) {
            if (!CollectionUtils.isEmpty(accountList)) {
                List<BankOpenAccountRecordVO> bankOpenAccountRecordList = CommonUtils.convertBeanList(accountList, BankOpenAccountRecordVO.class);
                response.setResultList(bankOpenAccountRecordList);
                response.setCount(countRecordTotal);
                response.setRtn(Response.SUCCESS);//代表成功
            }
        }
        return response;
    }
    /**
     * 设置汇付参数
     * @param request
     * @return
     */
    private Map<String,String> setAccountRecordRequest(AccountRecordRequest request){
        Map<String,String> mapRaram = new HashMap<String,String>();
        mapRaram.put("openAccountPlat",request.getOpenAccountPlat());
        mapRaram.put("userName",request.getUserName());
        mapRaram.put("userProperty",request.getUserProperty());
        mapRaram.put("idCard",request.getIdCard());
        mapRaram.put("realName",request.getRealName());
        mapRaram.put("openTimeStart",request.getOpenTimeStart());
        mapRaram.put("openTimeEnd",request.getOpenTimeEnd());
        return mapRaram;

    }

    /**
     * 设置江西参数
     * @param request
     * @return
     */
    private Map<String,String>  setBankAccountRecordRequest(BankAccountRecordRequest request){
        Map<String,String> mapRaram = new HashMap<String,String>();
        mapRaram.put("openAccountPlat",request.getOpenAccountPlat());
        mapRaram.put("userName",request.getUserName());
        mapRaram.put("customerAccount",request.getCustomerAccount());
        mapRaram.put("mobile",request.getMobile());
        mapRaram.put("idCard",request.getIdCard());
        mapRaram.put("realName",request.getRealName());
        mapRaram.put("openTimeStart",request.getOpenTimeStart());
        mapRaram.put("openTimeEnd",request.getOpenTimeEnd());
        return mapRaram;
    }

}