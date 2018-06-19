package com.hyjf.am.trade.controller;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.trade.AccountwithdrawResponse;
import com.hyjf.am.resquest.user.BankWithdrawBeanRequest;
import com.hyjf.am.trade.dao.model.auto.Accountwithdraw;
import com.hyjf.am.trade.service.AccountWithdrawService;
import com.hyjf.am.vo.trade.account.AccountWithdrawVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pangchengchao
 * @version AccountWithdrawController, v0.1 2018/6/11 11:56
 *//*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
@RestController
@RequestMapping("/am-trade/accountWithdraw")
public class AccountWithdrawController {
    private static Logger logger = LoggerFactory.getLogger(AccountWithdrawController.class);

    @Autowired
    private AccountWithdrawService accountWithdrawService;
    /**
     * @Description 插入用户提现记录
     * @Author pangchengchao
     * @Version v0.1
     * @Date
     */
    @RequestMapping("/insertAccountWithdrawLog")
    public void insertAccountWithdrawLog(@RequestBody Accountwithdraw accountWithdraw){
        logger.info("insertAccountWithdrawLog:" + JSONObject.toJSONString(accountWithdraw));
        accountWithdrawService.insertAccountWithdrawLog(accountWithdraw);
    }
    /**
     * @Description 根据订单号查询用户提现记录
     * @Author pangchengchao
     * @Version v0.1
     * @Date
     */
    @RequestMapping("/findByOrdId/{ordId}")
    public AccountwithdrawResponse findByOrdId(@PathVariable String ordId){
        logger.info("findByOrdId:" + ordId);
        AccountwithdrawResponse response = new AccountwithdrawResponse();
        List<Accountwithdraw> accountWithdrawList = accountWithdrawService.findByOrdId(ordId);
        List<AccountWithdrawVO> accountWithdrawVoList=null;
        if(!CollectionUtils.isEmpty(accountWithdrawList)){
            accountWithdrawVoList=new ArrayList<>(accountWithdrawList.size());
            for (Accountwithdraw accountWithdraw:accountWithdrawList) {
                AccountWithdrawVO vo=new AccountWithdrawVO();
                BeanUtils.copyProperties(accountWithdraw,vo);;
                accountWithdrawVoList.add(vo);
            }
        }
        response.setResultList(accountWithdrawVoList);
        return response;
    }
    /**
     * @Description
     * @Author pangchengchao
     * @Version v0.1
     * @Date
     */
    @RequestMapping("/getAccountWithdrawByOrdId/{logOrderId}")
    public AccountwithdrawResponse getAccountWithdrawByOrdId(@PathVariable String logOrderId){
        logger.info("getAccountWithdrawByOrdId:" + logOrderId);
        AccountwithdrawResponse response = new AccountwithdrawResponse();
        Accountwithdraw accountWithdraw = accountWithdrawService.getAccountWithdrawByOrdId(logOrderId);
        if (accountWithdraw != null) {
            AccountWithdrawVO accountWithdrawVO = new AccountWithdrawVO();
            BeanUtils.copyProperties(accountWithdraw, accountWithdrawVO);
            response.setResult(accountWithdrawVO);
        }
        return response;

    }


    /**
     * @Description 插入用户提现记录
     * @Author pangchengchao
     * @Version v0.1
     * @Date
     */
    @RequestMapping("/updatUserBankWithdrawHandler")
    public int updatUserBankWithdrawHandler(@RequestBody BankWithdrawBeanRequest bankWithdrawBeanRequest){
        logger.info("updatUserBankWithdrawHandler:" + JSONObject.toJSONString(bankWithdrawBeanRequest));
        try {
            return accountWithdrawService.updatUserBankWithdrawHandler(bankWithdrawBeanRequest);
        } catch (Exception e){
            return 0;
        }

    }
    /**
     * @Description 修改用户提现记录
     * @Author pangchengchao
     * @Version v0.1
     * @Date
     */
    @RequestMapping("/updateAccountwithdrawLog")
    public void updateAccountwithdrawLog(@RequestBody Accountwithdraw accountwithdraw){
        logger.info("updateAccountwithdrawLog:" + JSONObject.toJSONString(accountwithdraw));
        accountWithdrawService.updateAccountwithdrawLog(accountwithdraw);
    }
}