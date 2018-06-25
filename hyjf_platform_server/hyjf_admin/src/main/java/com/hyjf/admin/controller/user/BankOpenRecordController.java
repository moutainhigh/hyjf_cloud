/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.admin.service.BankOpenRecordService;
import com.hyjf.am.resquest.user.AccountRecordRequest;
import com.hyjf.am.resquest.user.BankAccountRecordRequest;
import com.hyjf.am.vo.user.BankOpenAccountRecordVO;
import com.hyjf.common.cache.CacheUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author nxl
 * @version RegistRecordController, v0.1 2018/6/23 15:16
 */

@Api(value = "开户记录")
@RestController
@RequestMapping("/hyjf-admin/bankOpenRecord")
public class BankOpenRecordController {
    @Autowired
    public BankOpenRecordService bankOpenRecordService;


    @ApiOperation(value = "开户记录", notes = "开户记录页面初始化")
//    @RequestMapping(value = "/usersInit", method = {RequestMethod.GET, RequestMethod.POST})
    @PostMapping(value = "/bankOpenRecordInit")
    @ResponseBody
    public JSONObject userManagerInit() {
        JSONObject jsonObject = new JSONObject();
        // 用户属性
        Map<String, String> userPropertys = CacheUtil.getParamNameMap("USER_PROPERTY");
        // 注册平台
        Map<String, String> registPlat = CacheUtil.getParamNameMap("CLIENT");
        jsonObject.put("userPropertys", userPropertys);
        jsonObject.put("registPlat", registPlat);
        return jsonObject;

    }

    //会员管理列表查询
    @ApiOperation(value = "开户记录", notes = "汇付银行开户记录查询")
//    @RequestMapping(value = "/bankOpenRecordAccount", method = {RequestMethod.GET, RequestMethod.POST})
    @PostMapping(value = "/bankOpenRecordAccount")
    @ResponseBody
    public JSONObject bankOpenRecordAccount(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> map){
        JSONObject jsonObject = null;
        AccountRecordRequest accountRecordRequest =setRequese(map);
        List<BankOpenAccountRecordVO> bankOpenRecordServiceAccountRecordList =bankOpenRecordService.findAccountRecordList(accountRecordRequest);
        String status="error";
        if(null!=bankOpenRecordServiceAccountRecordList&&bankOpenRecordServiceAccountRecordList.size()>0){
            jsonObject.put("record",bankOpenRecordServiceAccountRecordList);
            status="success";
        }
        jsonObject.put("status",status);
        return jsonObject;
    }
    @ApiOperation(value = "开户记录", notes = "江西银行开户记录查询")
//    @RequestMapping(value = "/bankOpenRecordBankAccount", method = {RequestMethod.GET, RequestMethod.POST})
    @PostMapping(value = "/bankOpenRecordBankAccount")
    @ResponseBody
    public JSONObject bankOpenRecordBankAccount(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> map){
        JSONObject jsonObject = null;
        BankAccountRecordRequest registerRcordeRequest =setBankRequese(map);
        List<BankOpenAccountRecordVO> bankOpenRecordServiceAccountRecordList=bankOpenRecordService.findBankAccountRecordList(registerRcordeRequest);
        String status="error";
        if(null!=bankOpenRecordServiceAccountRecordList&&bankOpenRecordServiceAccountRecordList.size()>0){
            jsonObject.put("record",bankOpenRecordServiceAccountRecordList);
            status="success";
        }
        jsonObject.put("status",status);
        return jsonObject;
    }
    private AccountRecordRequest  setRequese(Map<String,Object> mapParam){
        AccountRecordRequest accountRecordRequest = new AccountRecordRequest();
        if(null!=mapParam&&mapParam.size()>0){
            if(mapParam.containsKey("openAccountPlat")){
                accountRecordRequest.setOpenAccountPlat(mapParam.get("openAccountPlat").toString());
            }
            if(mapParam.containsKey("userName")){
                accountRecordRequest.setUserName(mapParam.get("userName").toString());
            }
            if(mapParam.containsKey("userProperty")){
                accountRecordRequest.setUserProperty(mapParam.get("userProperty").toString());
            }
            if(mapParam.containsKey("idCard")){
                accountRecordRequest.setIdCard(mapParam.get("idCard").toString());
            }
            if(mapParam.containsKey("realName")){
                accountRecordRequest.setRealName(mapParam.get("realName").toString());
            }
            if(mapParam.containsKey("openTimeStart")){
                accountRecordRequest.setOpenTimeStart(mapParam.get("openTimeStart").toString());
            }
            if(mapParam.containsKey("openTimeEnd")){
                accountRecordRequest.setOpenTimeEnd(mapParam.get("openTimeEnd").toString());
            }
            if (mapParam.containsKey("limit")&& StringUtils.isNotBlank(mapParam.get("limit").toString())) {
                accountRecordRequest.setLimit(Integer.parseInt(mapParam.get("limit").toString()));
            }
        }
        return accountRecordRequest;
    }

    private BankAccountRecordRequest setBankRequese(Map<String,Object> mapParam){
        BankAccountRecordRequest bankAccountRecordRequest = new BankAccountRecordRequest();
        if(null!=mapParam&&mapParam.size()>0){
            if(mapParam.containsKey("customerAccount")){
                bankAccountRecordRequest.setCustomerAccount(mapParam.get("customerAccount").toString());
            }
            if(mapParam.containsKey("mobile")){
                bankAccountRecordRequest.setMobile(mapParam.get("mobile").toString());
            }
            if(mapParam.containsKey("openAccountPlat")){
                bankAccountRecordRequest.setOpenAccountPlat(mapParam.get("openAccountPlat").toString());
            }
            if(mapParam.containsKey("userName")){
                bankAccountRecordRequest.setUserName(mapParam.get("userName").toString());
            }
            if(mapParam.containsKey("idCard")){
                bankAccountRecordRequest.setIdCard(mapParam.get("idCard").toString());
            }
            if(mapParam.containsKey("realName")){
                bankAccountRecordRequest.setRealName(mapParam.get("realName").toString());
            }
            if(mapParam.containsKey("openTimeStart")){
                bankAccountRecordRequest.setOpenTimeStart(mapParam.get("openTimeStart").toString());
            }
            if(mapParam.containsKey("openTimeEnd")){
                bankAccountRecordRequest.setOpenTimeEnd(mapParam.get("openTimeEnd").toString());
            }
            if (mapParam.containsKey("limit")&& StringUtils.isNotBlank(mapParam.get("limit").toString())) {
                bankAccountRecordRequest.setLimit(Integer.parseInt(mapParam.get("limit").toString()));
            }
        }
        return bankAccountRecordRequest;
    }
}
