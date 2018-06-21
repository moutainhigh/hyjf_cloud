package com.hyjf.am.trade.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.trade.AccountRechargeResponse;
import com.hyjf.am.response.trade.AccountResponse;
import com.hyjf.am.resquest.user.BankAccountBeanRequest;
import com.hyjf.am.resquest.user.BankRequest;
import com.hyjf.am.trade.dao.model.auto.Account;
import com.hyjf.am.trade.dao.model.auto.AccountList;
import com.hyjf.am.trade.dao.model.auto.AccountRecharge;
import com.hyjf.am.trade.dao.model.auto.AccountRechargeExample;
import com.hyjf.am.trade.service.AccountService;
import com.hyjf.am.trade.service.RechargeService;
import com.hyjf.am.vo.trade.account.AccountRechargeVO;
import com.hyjf.am.vo.trade.account.AccountVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("am-trade/trade")
public class RechargeController {
    private static Logger logger = LoggerFactory.getLogger(RechargeController.class);

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    AccountService accountService;

    /**
     *
     * @param userId
     * @return
     */
    @GetMapping("/getAccount/{userId}")
    public AccountResponse getAccount(@PathVariable(value = "userId") Integer userId){
        logger.info("getAccount...param is :{}", JSONObject.toJSONString(userId));
        AccountResponse response = new AccountResponse();
        Account account = accountService.getAccount(userId);
        if(null != account){
            AccountVO accountVO = new AccountVO();
            BeanUtils.copyProperties(account,accountVO);
            response.setResult(accountVO);
        }
        return response;
    }

    /**
     * 查询充值记录
     * @param
     * @return
     */
    @GetMapping("/selectByOrderId/{orderId}")
    public AccountRechargeResponse selectByOrderId(@PathVariable String orderId){
        logger.info("selectByOrderId...param is :{}", JSONObject.toJSONString(orderId));
        AccountRechargeExample example = new AccountRechargeExample();
        example.createCriteria().andNidEqualTo(orderId);
        AccountRechargeResponse response = new AccountRechargeResponse();
        AccountRecharge accountRecharge = rechargeService.selectByExample(example);
        if (accountRecharge != null){
            AccountRechargeVO accountRechargeVO = new AccountRechargeVO();
            BeanUtils.copyProperties(accountRecharge,accountRechargeVO);
            response.setResult(accountRechargeVO);
        }
        return response;
    }

    /**
     *
     * @param newAccount
     * @return
     */
    @PostMapping("/updateBankRechargeSuccess")
    public int updateBankRechargeSuccess(@RequestBody Account newAccount){
        logger.info("updateBankRechargeSuccess...param is :{}", JSONObject.toJSONString(newAccount));
        int isAccountUpdateFlag = rechargeService.updateBankRechargeSuccess(newAccount);
        return isAccountUpdateFlag;
    }

    /**
     * 插入交易明细
     * @param accountList
     * @return
     */
    @ApiOperation(value = " 插入交易明细", notes = " 插入交易明细")
    @PostMapping("/insertSelective")
    public int insertSelective(@RequestBody AccountList accountList){
        logger.info("insertSelective...param is :{}", JSONObject.toJSONString(accountList));
        int isAccountListUpdateFlag = rechargeService.insertSelective(accountList);
        return isAccountListUpdateFlag;
    }

    /**
     *
     * @param accountRecharge
     */
    @PutMapping("/updateByPrimaryKeySelective")
    public void updateByPrimaryKeySelective(@RequestBody AccountRecharge accountRecharge){
        logger.info("updateByPrimaryKeySelective...param is :{}", JSONObject.toJSONString(accountRecharge));
        this.rechargeService.updateByPrimaryKeySelective(accountRecharge);
    }

    @GetMapping("/selectByOrdId/{ordId}")
    public int selectByOrdId(@PathVariable String ordId){
        logger.info("selectByOrdId...param is :{}", JSONObject.toJSONString(ordId));
        int ret = this.rechargeService.selectByOrdId(ordId);
        return ret;
    }

    @PostMapping("/insertSelectiveBank")
    public int insertSelectiveBank(@RequestBody BankRequest bankRequest){
        logger.info("insertSelectiveBank...param is :{}", JSONObject.toJSONString(bankRequest));
        int ret = rechargeService.insertSelective(bankRequest);
        return ret;
    }

    @PostMapping("/updateBanks")
    public boolean updateBanks(@RequestBody BankAccountBeanRequest bankAccountBeanRequest) {
        logger.info("updateBanks...param is :{}", JSONObject.toJSONString(bankAccountBeanRequest));
        AccountRechargeVO accountRecharge =  bankAccountBeanRequest.getAccountRecharge();
        String ip =  bankAccountBeanRequest.getIp();
        boolean flag = rechargeService.updateBanks(accountRecharge,ip);
        return flag;
    }
}