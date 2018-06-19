package com.hyjf.am.trade.service.impl;

import com.hyjf.am.resquest.user.BankWithdrawBeanRequest;
import com.hyjf.am.trade.dao.mapper.auto.AccountListMapper;
import com.hyjf.am.trade.dao.mapper.auto.AccountMapper;
import com.hyjf.am.trade.dao.mapper.auto.AccountwithdrawMapper;
import com.hyjf.am.trade.dao.mapper.customize.admin.AdminAccountCustomizeMapper;
import com.hyjf.am.trade.dao.model.auto.*;
import com.hyjf.am.trade.service.AccountWithdrawService;
import com.hyjf.am.vo.trade.account.AccountWithdrawVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author pangchengchao
 * @version AccountWithdrawServiceImpl, v0.1 2018/6/11 13:47
 *//*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
@Service
public class AccountWithdrawServiceImpl implements AccountWithdrawService {

    // add by nxl 添加体现状态
    // 体现状态：初始值
    private static  final int WITHDRAW_STATUS_DEFAULT = 0;
    // 提现状态:提现中
    private static final int WITHDRAW_STATUS_WAIT = 1;
    // 提现状态:失败
    private static final int WITHDRAW_STATUS_FAIL = 3;
    // 提现状态:成功
    private static final int WITHDRAW_STATUS_SUCCESS = 2;

    @Autowired
    private AccountwithdrawMapper accountWithdrawMapper;

    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private AccountListMapper accountListMapper;
    @Autowired
    private AdminAccountCustomizeMapper adminAccountCustomizeMapper;

    @Override
    public void insertAccountWithdrawLog(Accountwithdraw accountWithdraw) {
        accountWithdrawMapper.insertSelective(accountWithdraw);
    }

    @Override
    public List<Accountwithdraw> findByOrdId(String ordId) {
        AccountwithdrawExample accountWithdrawExample = new AccountwithdrawExample();
        accountWithdrawExample.createCriteria().andNidEqualTo(ordId);
        List<Accountwithdraw> listAccountWithdraw = this.accountWithdrawMapper.selectByExample(accountWithdrawExample);
        return listAccountWithdraw;
    }

    @Override
    public Accountwithdraw getAccountWithdrawByOrdId(String logOrderId) {
        AccountwithdrawExample accountWithdrawExample = new AccountwithdrawExample();
        accountWithdrawExample.createCriteria().andNidEqualTo(logOrderId).andStatusEqualTo(WITHDRAW_STATUS_SUCCESS);
        List<Accountwithdraw> list = this.accountWithdrawMapper.selectByExample(accountWithdrawExample);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public int updatUserBankWithdrawHandler(BankWithdrawBeanRequest bankRequest) {

        BigDecimal transAmt=bankRequest.getTransAmt();
        String fee=bankRequest.getFee();
        BigDecimal feeAmt=bankRequest.getFeeAmt();
        BigDecimal total=bankRequest.getTotal();
        Integer userId=bankRequest.getUserId();
        String ordId=bankRequest.getOrdId();
        int nowTime=bankRequest.getNowTime();
        String accountId=bankRequest.getAccountId();
        String ip=bankRequest.getIp();
        AccountWithdrawVO accountWithdrawVO=bankRequest.getAccountWithdrawVO();
        Accountwithdraw accountWithdraw=new Accountwithdraw();
        BeanUtils.copyProperties(accountWithdrawVO, accountWithdraw);

        AccountwithdrawExample accountWithdrawExample = new AccountwithdrawExample();
        accountWithdrawExample.createCriteria().andNidEqualTo(accountWithdraw.getNid());
        // 更新订单信息

        boolean isAccountwithdrawFlag = this.accountWithdrawMapper.updateByExampleSelective(accountWithdraw, accountWithdrawExample) > 0 ? true : false;
        if (!isAccountwithdrawFlag) {
            throw new RuntimeException("提现后,更新用户提现记录表失败!");
        }
        Account newAccount = new Account();
        // 更新账户信息
        newAccount.setUserId(userId);// 用户Id
        newAccount.setBankTotal(total); // 累加到账户总资产
        newAccount.setBankBalance(total); // 累加可用余额
        newAccount.setBankBalanceCash(total);// 江西银行可用余额
        boolean isAccountUpdateFlag = this.adminAccountCustomizeMapper.updateBankWithdrawSuccess(newAccount) > 0 ? true : false;
        if (!isAccountUpdateFlag) {
            throw new RuntimeException("提现后,更新用户Account表失败!");
        }

        // 重新获取用户信息
        Account account = this.getAccount(userId);
        // 写入收支明细
        AccountList accountList = new AccountList();
        // 重新查询用户账户信息
        accountList.setNid(ordId);
        accountList.setUserId(userId);
        accountList.setAmount(total);
        accountList.setType(2);
        accountList.setTrade("cash_success");
        accountList.setTradeCode("balance");
        accountList.setTotal(account.getTotal());
        accountList.setBalance(account.getBalance());
        accountList.setFrost(account.getFrost());
        accountList.setAwait(account.getAwait());
        accountList.setRepay(account.getRepay());
        accountList.setBankTotal(account.getBankTotal()); // 银行总资产
        accountList.setBankBalance(account.getBankBalance()); // 银行可用余额
        accountList.setBankFrost(account.getBankFrost());// 银行冻结金额
        accountList.setBankWaitCapital(account.getBankWaitCapital());// 银行待还本金
        accountList.setBankWaitInterest(account.getBankWaitInterest());// 银行待还利息
        accountList.setBankAwaitCapital(account.getBankAwaitCapital());// 银行待收本金
        accountList.setBankAwaitInterest(account.getBankAwaitInterest());// 银行待收利息
        accountList.setBankAwait(account.getBankAwait());// 银行待收总额
        accountList.setBankInterestSum(account.getBankInterestSum()); // 银行累计收益
        accountList.setBankInvestSum(account.getBankInvestSum());// 银行累计投资
        accountList.setBankWaitRepay(account.getBankWaitRepay());// 银行待还金额
        accountList.setPlanBalance(account.getPlanBalance());//汇计划账户可用余额
        accountList.setPlanFrost(account.getPlanFrost());
        // mod by liuyang 20180119 银行文件对账功能修改 start
        accountList.setSeqNo(String.valueOf(accountWithdraw.getSeqNo()));
        accountList.setTxDate(accountWithdraw.getTxDate());
        accountList.setTxTime(accountWithdraw.getTxTime());
        accountList.setBankSeqNo(accountWithdraw.getTxDate() + accountWithdraw.getTxTime() + String.valueOf(accountWithdraw.getSeqNo()));
        // mod by liuyang 20180119 银行文件对账功能修改 end
        accountList.setAccountId(accountId);
        accountList.setRemark("网站提现");
        accountList.setOperator(userId+"");
        accountList.setIp(ip);
        accountList.setIsBank(1);
        accountList.setWeb(0);
        accountList.setCheckStatus(0);// 对账状态0：未对账 1：已对账
        accountList.setTradeStatus(1);// 0失败1成功2失败
        boolean isAccountListFlag = this.accountListMapper.insertSelective(accountList) > 0 ? true : false;
        if (!isAccountListFlag) {
            throw new RuntimeException("提现成功后,插入交易明细表失败~!");
        }
        return 1;
    }

    @Override
    public void updateAccountwithdrawLog(Accountwithdraw accountwithdraw) {
        accountWithdrawMapper.updateByPrimaryKeySelective(accountwithdraw);
    }
    private Account getAccount(Integer userId) {
        AccountExample example = new AccountExample();
        AccountExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<Account> listAccount = this.accountMapper.selectByExample(example);
        if (listAccount != null && listAccount.size() > 0) {
            return listAccount.get(0);
        }
        return null;
    }
}