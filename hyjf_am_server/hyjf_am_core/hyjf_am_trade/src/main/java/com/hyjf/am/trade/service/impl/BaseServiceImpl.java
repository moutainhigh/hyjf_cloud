/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.hyjf.am.resquest.admin.UnderLineRechargeRequest;
import com.hyjf.am.trade.dao.customize.CustomizeMapper;
import com.hyjf.am.trade.dao.model.auto.*;
import com.hyjf.am.trade.service.BaseService;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.GetOrderIdUtils;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import com.hyjf.pay.lib.bank.util.BankCallMethodConstant;
import com.hyjf.pay.lib.bank.util.BankCallStatusConstant;
import com.hyjf.pay.lib.bank.util.BankCallUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

/**
 * 资金服务:BaseService实现类
 *
 * @author liuyang
 * @version BaseServiceImpl, v0.1 2018/6/27 9:33
 */
public class BaseServiceImpl extends CustomizeMapper implements BaseService {
    protected Logger logger = LoggerFactory.getLogger(getClass());
    /**
     * 根据标的编号检索标的信息
     *
     * @param borrowNid
     * @return
     */
    @Override
    public Borrow getBorrow(String borrowNid) {
        BorrowExample example = new BorrowExample();
        BorrowExample.Criteria criteria = example.createCriteria();
        criteria.andBorrowNidEqualTo(borrowNid);
        List<Borrow> list = this.borrowMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据标的编号检索标的借款详情
     * @param borrowNid
     * @return
     */
    @Override
    public BorrowInfo getBorrowInfoByNid(String borrowNid) {
        BorrowInfoExample example = new BorrowInfoExample();
        BorrowInfoExample.Criteria cra = example.createCriteria();
        cra.andBorrowNidEqualTo(borrowNid);
        List<BorrowInfo> list=this.borrowInfoMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取用户的账户信息
     *
     * @param userId
     * @return 获取用户的账户信息
     */
    @Override
    public Account getAccount(Integer userId) {
        AccountExample example = new AccountExample();
        AccountExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<Account> listAccount = this.accountMapper.selectByExample(example);
        if (listAccount != null && listAccount.size() > 0) {
            return listAccount.get(0);
        }
        return null;
    }

    /**
     * 取得本库冗余的用户信息
     * @param userId
     * @return
     */
    @Override
    public RUser getRUser(Integer userId) {
        RUserExample example = new RUserExample();
        RUserExample.Criteria cra = example.createCriteria();
        cra.andUserIdEqualTo(userId);
        List<RUser> list=this.rUserMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    /**
     * 取得本库冗余的用户信息
     * @param userName
     * @return
     */
    @Override
    public RUser getRUser(String userName) {
        RUserExample example = new RUserExample();
        RUserExample.Criteria cra = example.createCriteria();
        cra.andUsernameEqualTo(userName);
        List<RUser> list=this.rUserMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    /**
     * 取得本库冗余的推荐人信息
     * @param userId
     * @return
     */
    @Override
    public RUser getRefUser(Integer userId) {
        return rUserCustomizeMapper.selectRefUserInfoByUserId(userId);
    }

    /**
     * 汇计划全部流程用更新用户的账户表
     * @param hjhProcessFlg
     * @param userId
     * @param amount
     * @param interest
     * @return
     */
    @Override
    public Boolean updateAccountForHjh(String hjhProcessFlg, Integer userId, BigDecimal amount, BigDecimal interest) {
        //更新用户的账户表
        Account accountBean = new Account();
        accountBean.setUserId(userId);
        switch (hjhProcessFlg) {
            case CustomConstants.HJH_PROCESS_B:
                //计划订单-自动投标
            case CustomConstants.HJH_PROCESS_BF:
                //计划订单-自动投标/复投
                //amount=自动投标金额=b
                accountBean.setPlanBalance(amount.negate()); // 汇计划可用余额  -b
                accountBean.setPlanFrost(amount); // 汇计划冻结金额 +b
                break;

            case CustomConstants.HJH_PROCESS_D:
                //计划订单-自动承接(投资)
            case CustomConstants.HJH_PROCESS_DF:
                //计划订单-自动承接(复投)
                //amount=自动投标金额=d
                accountBean.setPlanBalance(amount.negate()); // 汇计划可用余额  -d
                break;

            case CustomConstants.HJH_PROCESS_F:
                //计划订单锁定期-债权回款（承接和还款，要复投）
                //amount=回款总额=f
                accountBean.setPlanBalance(amount); // 汇计划可用余额  +f
                break;
            case CustomConstants.HJH_PROCESS_H:
                //汇计划清算-债权回款（承接和还款，不复投）
                //amount=回款总额=h
                accountBean.setPlanFrost(amount); // 汇计划冻结金额 +h
                break;
            default:
                break;
        }

        Boolean accountFlag = this.adminAccountCustomizeMapper.updateAccountForHjhProcess(accountBean) > 0 ? true : false;
        if (!accountFlag) {
            throw new RuntimeException("用户账户信息表更新失败");
        }

        return accountFlag;
    }

    /**
     * 汇计划重算更新汇计划加入明细表
     * @param hjhProcessFlg
     * @param id
     * @param amount
     * @param interest
     * @return
     */
    @Override
    public Boolean updateHjhAccedeForHjh(String hjhProcessFlg, Integer id, BigDecimal amount, BigDecimal interest, BigDecimal serviceFee) {
        //更新用户的账户表
        HjhAccede hjhAccede = new HjhAccede();

        //获取当前时间
        hjhAccede.setUpdateTime(GetDate.getDate());
        hjhAccede.setUpdateUser(1);
        hjhAccede.setId(id);
        switch (hjhProcessFlg) {
            case CustomConstants.HJH_PROCESS_B:
                //计划订单-自动投标
                //amount=自动投标金额=b
                hjhAccede.setAlreadyInvest(amount);// 计划订单已投资金额 +b
            case CustomConstants.HJH_PROCESS_BF:
                //计划订单-自动复投
                //amount=自动投标金额=b
                hjhAccede.setAvailableInvestAccount(amount.negate()); // 计划订单可用余额  -b
                hjhAccede.setFrostAccount(amount); // 计划订单冻结金额 +b
                // add 汇计划三期 计划订单投资笔数累加 liubin 20180515 start
                hjhAccede.setInvestCounts(1);// 投资笔数 +1
                // add 汇计划三期 计划订单投资笔数累加 liubin 20180515 end
                break;

            case CustomConstants.HJH_PROCESS_D:
                //计划订单-自动承接(投资)
                //amount=自动投标金额=d
                hjhAccede.setAlreadyInvest(amount);// 计划订单已投资金额 +d
            case CustomConstants.HJH_PROCESS_DF:
                //计划订单-自动承接(投资/复投)
                //amount=自动投标金额=d
                hjhAccede.setAvailableInvestAccount(amount.negate()); // 计划订单可用余额  -d
                // add 汇计划三期 计划订单投资笔数累加 liubin 20180515 start
                hjhAccede.setInvestCounts(1); // 投资笔数 +1
                // add 汇计划三期 计划订单投资笔数累加 liubin 20180515 end
                break;
            case CustomConstants.HJH_PROCESS_F:
                //计划订单锁定期-债权回款（承接和还款，要复投）
                //amount=回款总额=f
                hjhAccede.setAvailableInvestAccount(amount); // 计划订单可用余额  +f
                // add 汇计划三期 汇计划自动投资(收债转服务费) liubin 20180515 start
                hjhAccede.setLqdServiceFee(serviceFee); // 债转服务费累计
                // add 汇计划三期 汇计划自动投资(收债转服务费) liubin 20180515 end
                break;
            case CustomConstants.HJH_PROCESS_H:
                //汇计划清算-债权回款（承接和还款，不复投）
                //amount=回款总额=h
                hjhAccede.setFrostAccount(amount); // 计划订单冻结金额 +h
                // add 汇计划三期 汇计划自动投资(收债转服务费) liubin 20180515 start
                hjhAccede.setLqdServiceFee(serviceFee); // 债转服务费累计
                // add 汇计划三期 汇计划自动投资(收债转服务费) liubin 20180515 end
                break;
            default:
                break;
        }

        Boolean accountFlag = this.hjhPlanCustomizeMapper.updateHjhAccedeForHjhProcess(hjhAccede) > 0 ? true : false;
        if (!accountFlag) {
            throw new RuntimeException("用户账户信息表更新失败");
        }

        return accountFlag;
    }

    /**
     * 获取借款人总的还款表数据
     * @auther: hesy
     * @date: 2018/7/9
     */
    @Override
    public BorrowRepay getBorrowRepay(String borrowNid) {
        BorrowRepayExample borrowRepayExample = new BorrowRepayExample();
        BorrowRepayExample.Criteria borrowRepayCrt = borrowRepayExample.createCriteria();
        borrowRepayCrt.andBorrowNidEqualTo(borrowNid);
        List<BorrowRepay> borrowRepays = borrowRepayMapper.selectByExample(borrowRepayExample);
        if (borrowRepays != null && borrowRepays.size() == 1) {
            return borrowRepays.get(0);
        } else {
            return null;
        }
    }

    @Override
    public List<BorrowRecover> getBorrowRecover(String borrowNid) {
        BorrowRecoverExample example = new BorrowRecoverExample();
        BorrowRecoverExample.Criteria crt = example.createCriteria();
        crt.andBorrowNidEqualTo(borrowNid);
        List<BorrowRecover> borrowRecovers = borrowRecoverMapper.selectByExample(example);
        return borrowRecovers;
    }

    @Override
    public List<BorrowRecoverPlan> getBorrowRecoverPlan(String borrowNid, int period) {
        BorrowRecoverPlanExample example = new BorrowRecoverPlanExample();
        BorrowRecoverPlanExample.Criteria crt = example.createCriteria();
        crt.andBorrowNidEqualTo(borrowNid);
        crt.andRecoverPeriodEqualTo(period);
        List<BorrowRecoverPlan> borrowRecovers = borrowRecoverPlanMapper.selectByExample(example);
        return borrowRecovers;
    }

    /**
     * 获取某一期的还款计划
     * @auther: hesy
     * @date: 2018/7/9
     */
    @Override
    public BorrowRepayPlan getRepayPlan(String borrowNid, int period) {
        BorrowRepayPlanExample borrowRepayPlanExample = new BorrowRepayPlanExample();
        BorrowRepayPlanExample.Criteria borrowRepayPlanCrt = borrowRepayPlanExample.createCriteria();
        borrowRepayPlanCrt.andBorrowNidEqualTo(borrowNid);
        borrowRepayPlanCrt.andRepayPeriodEqualTo(period);
        List<BorrowRepayPlan> borrowRepayPlans = borrowRepayPlanMapper.selectByExample(borrowRepayPlanExample);
        if (borrowRepayPlans != null && borrowRepayPlans.size() == 1) {
            return borrowRepayPlans.get(0);
        } else {
            return null;
        }
    }

    /**
     * 债转投资记录获取
     * @auther: hesy
     * @date: 2018/8/7
     */
    @Override
    public CreditTender getCreditTender(String assignNid) {
        CreditTenderExample example = new CreditTenderExample();
        CreditTenderExample.Criteria crt = example.createCriteria();
        crt.andAssignNidEqualTo(assignNid);
        List<CreditTender> creditTenderList = this.creditTenderMapper.selectByExample(example);
        if (creditTenderList != null && creditTenderList.size() == 1) {
            return creditTenderList.get(0);
        }
        return null;
    }

    /**
     * 计划类债转
     * 根据承接订单号获取承接记录
     * @param assignNid
     * @return
     */
    @Override
    public HjhDebtCreditTender getHjhDebtCreditTender(String assignNid) {
        HjhDebtCreditTenderExample example = new HjhDebtCreditTenderExample();
        HjhDebtCreditTenderExample.Criteria crt = example.createCriteria();
        crt.andAssignOrderIdEqualTo(assignNid);
        List<HjhDebtCreditTender> creditTenderList = this.hjhDebtCreditTenderMapper.selectByExample(example);
        if (creditTenderList != null && creditTenderList.size() == 1) {
            return creditTenderList.get(0);
        }
        return null;
    }

    /**
     * 获取系统配置
     * @param configCd
     * @return
     */
    @Override
    public String getBorrowConfig(String configCd) {
        BorrowConfig borrowConfig = this.borrowConfigMapper.selectByPrimaryKey(configCd);
        return borrowConfig.getConfigValue();
    }

    /**
     * 根据电子账号查询用户在江西银行的可用余额
     *
     * @param accountId
     * @return
     */
    @Override
    public BigDecimal getBankBalance(Integer userId, String accountId) {
        // 账户可用余额
        BigDecimal balance = BigDecimal.ZERO;
        BankCallBean bean = new BankCallBean();
        bean.setVersion(BankCallConstant.VERSION_10);// 版本号
        bean.setTxCode(BankCallMethodConstant.TXCODE_BALANCE_QUERY);// 交易代码
        bean.setTxDate(GetOrderIdUtils.getTxDate()); // 交易日期
        bean.setTxTime(GetOrderIdUtils.getTxTime()); // 交易时间
        bean.setSeqNo(GetOrderIdUtils.getSeqNo(6));// 交易流水号
        bean.setChannel(BankCallConstant.CHANNEL_PC); // 交易渠道
        bean.setAccountId(accountId);// 电子账号
        bean.setLogOrderId(GetOrderIdUtils.getOrderId2(userId));// 订单号
        bean.setLogOrderDate(GetOrderIdUtils.getOrderDate());// 订单时间(必须)格式为yyyyMMdd，例如：20130307
        bean.setLogUserId(String.valueOf(userId));
        bean.setLogRemark("电子账户余额查询");
        bean.setLogClient(0);// 平台
        try {
            BankCallBean resultBean = BankCallUtils.callApiBg(bean);
            if (resultBean != null && BankCallStatusConstant.RESPCODE_SUCCESS.equals(resultBean.getRetCode())) {
                balance = new BigDecimal(resultBean.getAvailBal().replace(",", ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return balance;
    }

    /**
     * 根据借款编号获取该机构的审核配置
     *
     * @param borrowNid
     * @return
     */
    @Override
    public HjhAssetBorrowtype selectAssetBorrowType(String borrowNid) {
        BorrowInfo borrowInfo = this.getBorrowInfoByNid(borrowNid);
        HjhAssetBorrowtypeExample example = new HjhAssetBorrowtypeExample();
        example.createCriteria().andInstCodeEqualTo(borrowInfo.getInstCode()).andAssetTypeEqualTo(borrowInfo.getAssetType());
        List<HjhAssetBorrowtype> list = this.hjhAssetBorrowtypeMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 判断是否属于线下充值类型.
     * 	优先从Redis中取数据,当Redis中的数据为空时,从数据表中读取数据
     * @param tranType
     * @return
     * @Author : huanghui
     */
    @Override
    public boolean getIsRechargeTransType(String tranType) {
        //从Redis获取线下充值类型List
        String codeStringList = RedisUtils.get(RedisConstants.UNDER_LINE_RECHARGE_TYPE);
        JSONArray redisCodeList = JSONArray.parseArray(codeStringList);

        if (StringUtils.isBlank(codeStringList) || redisCodeList.size() <= 0){
            logger.info(this.getClass().getName(), "---------------------------线下充值类型Redis为空!-------------------------");

            UnderLineRechargeRequest request = new UnderLineRechargeRequest();
            UnderLineRechargeExample example = new UnderLineRechargeExample();
            UnderLineRechargeExample.Criteria criteria = example.createCriteria();

            // 启用状态的
            criteria.andStatusEqualTo(0);

            List<UnderLineRecharge> codeList = this.underLineRechargeMapper.selectByExample(example);
            if (codeList.isEmpty()){
                logger.info(this.getClass().getName(), "---------------------------线下充值类型数据库未配置!-------------------------");
                return false;
            }else {
                for (UnderLineRecharge code : codeList){
                    if (code.getCode().equals(tranType)){
                        return true;
                    }else {
                        continue;
                    }
                }
            }
        }else {

            for(Object code : redisCodeList) {
                if (code.equals(tranType)){
                    return true;
                }else {
                    continue;
                }
            }
        }
        return false;
    }
}
