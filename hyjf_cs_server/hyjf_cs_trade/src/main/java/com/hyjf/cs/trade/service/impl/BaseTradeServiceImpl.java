package com.hyjf.cs.trade.service.impl;

import com.hyjf.am.vo.trade.account.AccountVO;
import com.hyjf.am.vo.trade.borrow.BorrowAndInfoVO;
import com.hyjf.am.vo.user.*;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.enums.MsgEnum;
import com.hyjf.common.exception.ReturnMessageException;
import com.hyjf.common.util.ClientConstants;
import com.hyjf.common.util.GetOrderIdUtils;
import com.hyjf.cs.common.service.BaseServiceImpl;
import com.hyjf.cs.trade.client.AmConfigClient;
import com.hyjf.cs.trade.client.AmTradeClient;
import com.hyjf.cs.trade.client.AmUserClient;
import com.hyjf.cs.trade.config.SystemConfig;
import com.hyjf.cs.trade.service.BaseTradeService;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import com.hyjf.pay.lib.bank.util.BankCallMethodConstant;
import com.hyjf.pay.lib.bank.util.BankCallStatusConstant;
import com.hyjf.pay.lib.bank.util.BankCallUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

public class BaseTradeServiceImpl extends BaseServiceImpl implements BaseTradeService {
    protected Logger logger = LoggerFactory.getLogger(getClass());


    @Autowired
    public AmUserClient amUserClient;

    @Autowired
    public AmTradeClient amTradeClient;

    @Autowired
    public AmConfigClient amConfigClient;


    /**
     * @Description 根据token查询user
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/12 10:34
     */
    @Override
    public WebViewUserVO getUserFromCache(int userId) {
        WebViewUserVO user = RedisUtils.getObj(RedisConstants.USERID_KEY+userId, WebViewUserVO.class);
        return user;
    }

    /**
     * 根据userid查询用户
     * @auther: hesy
     * @date: 2018/7/10
     */
    @Override
    public UserVO getUserByUserId(Integer userId){
        return amUserClient.findUserById(userId);
    }

    /**
     * 获取用户在银行的开户信息
     * @param userId
     * @return
     */
    @Override
    public BankOpenAccountVO getBankOpenAccount(Integer userId) {
        BankOpenAccountVO bankAccount = this.amUserClient.selectBankAccountById(userId);
        return bankAccount;
    }

    /**
     * @Description 获得江西银行的余额  调用江西银行接口
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/20 9:16
     */
    @Override
    public BigDecimal getBankBalancePay(Integer userId, String accountId) {
        BigDecimal balance = BigDecimal.ZERO;
        BankCallBean bean = new BankCallBean();
        bean.setTxCode(BankCallMethodConstant.TXCODE_BALANCE_QUERY);
        // 交易渠道
        bean.setChannel(BankCallConstant.CHANNEL_PC);
        // 电子账号
        bean.setAccountId(accountId);
        // 订单号
        bean.setLogOrderId(GetOrderIdUtils.getOrderId2(userId));
        // 订单时间(必须)格式为yyyyMMdd，例如：20130307
        bean.setLogOrderDate(GetOrderIdUtils.getOrderDate());
        bean.setLogUserId(String.valueOf(userId));
        bean.setLogRemark("电子账户余额查询");
        // 平台
        bean.setLogClient(0);
        try {
            BankCallBean resultBean = BankCallUtils.callApiBg(bean);
            if (resultBean != null && BankCallStatusConstant.RESPCODE_SUCCESS.equals(resultBean.getRetCode())) {
                balance = new BigDecimal(resultBean.getAvailBal().replace(",", ""));
                return balance;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new BigDecimal(0);
    }

    /**
     * @Description 风险测评校验
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/20 9:55
     */
    @Override
    public void checkEvaluation(UserVO user) {
        Integer userEvaluationResultFlag = user.getIsEvaluationFlag();
        if (0 == userEvaluationResultFlag) {
            throw new ReturnMessageException(MsgEnum.ERR_AMT_TENDER_NEED_RISK_ASSESSMENT);
        } else {
           /* //是否完成风险测评
            if (user.getIsEvaluationFlag()==1) {
                //测评到期日
                Long lCreate = user.getEvaluationExpiredTime().getTime();
                //当前日期
                Long lNow = new Date().getTime();
                if (lCreate <= lNow) {
                    //已过期需要重新评测
                    String message = "根据监管要求，投资前必须进行风险测评。";
                }
            } else {
                //未获取到评测数据或者评测时间
                String message = "根据监管要求，投资前必须进行风险测评。";
            }*/
        }
    }

    /**
     * 检查用户是否是新手 true 是  false 不是
     *
     * @param userId
     * @return
     */
    @Override
    public boolean checkIsNewUserCanInvest(Integer userId) {
        // 新的判断是否为新用户方法
        try {
            int total = amTradeClient.countNewUserTotal(userId);
            logger.info("获取用户投资数量 userID {} 数量 {} ",userId,total);
            if (total == 0) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e) {
            throw e;
        }
    }

    /**
     * 获取account信息
     * @auther: hesy
     * @date: 2018/7/10
     */
    @Override
    public AccountVO getAccountByUserId(Integer userId){
        return amTradeClient.getAccountByUserId(userId);
    }

    @Override
    public BankCardVO getBankCardVOByUserId(Integer userId) {
        BankCardVO bankCard = this.amUserClient.selectBankCardByUserId(userId);
        return bankCard;
    }

    /**
     * 获取前端的地址
     * @param sysConfig
     * @param platform
     * @return
     */
    public String getFrontHost(SystemConfig sysConfig, String platform) {

        Integer client = Integer.parseInt(platform);
        if (ClientConstants.WEB_CLIENT == client) {
            return sysConfig.getFrontHost();
        }
        if (ClientConstants.APP_CLIENT_IOS == client || ClientConstants.APP_CLIENT == client) {
            return sysConfig.getAppFrontHost();
        }
        if (ClientConstants.WECHAT_CLIENT == client) {
            return sysConfig.getWeiFrontHost();
        }
        return null;
    }

    /**
     * 根据borrowNid获取borrow
     * @param borrowNid
     * @return
     */
    @Override
    public BorrowAndInfoVO getBorrowByNid(String borrowNid) {
        return amTradeClient.selectBorrowByNid(borrowNid);
    }


    @Override
    public BankCardVO selectBankCardByUserId(Integer userId) {
        BankCardVO bankCard = amUserClient.selectBankCardByUserId(userId);
        return bankCard;
    }

    /**
     * 根据用户ID取得用户信息
     *
     * @param userId
     * @return
     */
    @Override
    public UserInfoVO getUsersInfoByUserId(Integer userId) {
        if (userId != null) {
            UserInfoVO usersInfo = this.amUserClient.findUsersInfoById(userId);
            return usersInfo;
        }
        return null;
    }

    @Override
    public UserVO getUsers(Integer userId) {
        UserVO users = amUserClient.findUserById(userId);
        return users;
    }


}
