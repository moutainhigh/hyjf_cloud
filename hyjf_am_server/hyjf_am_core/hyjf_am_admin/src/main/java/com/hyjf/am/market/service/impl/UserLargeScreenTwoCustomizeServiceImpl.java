package com.hyjf.am.market.service.impl;

import com.hyjf.am.market.dao.mapper.customize.market.UserLargeScreenTwoCustomizeMapper;
import com.hyjf.am.market.dao.model.auto.ScreenTwoParam;
import com.hyjf.am.market.service.UserLargeScreenTwoCustomizeService;
import com.hyjf.am.user.dao.model.auto.CustomerTaskConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Auther:dangzw
 * @Date:2019/5/6
 * @Description:用户画像-运营部投屏二数据batch获取、每日用户划转
 */
@Service
public class UserLargeScreenTwoCustomizeServiceImpl implements UserLargeScreenTwoCustomizeService {

    @Autowired
    private UserLargeScreenTwoCustomizeMapper userLargeScreenTwoCustomizeMapper;

    /**
     * 查询有效坐席
     * @return
     */
    @Override
    public List<CustomerTaskConfig> getCustomer() {
        return userLargeScreenTwoCustomizeMapper.getCustomer();
    }

    /**
     * 查询坐席下的增资、提现率
     * @return
     */
    @Override
    public List<ScreenTwoParam> getCapitalIncreaseAndCashWithdrawalRateByCustomer() {
        return userLargeScreenTwoCustomizeMapper.getCapitalIncreaseAndCashWithdrawalRateByCustomer();
    }

    /**
     * 查询运营部当前总站岗资金
     * @return
     */
    @Override
    public BigDecimal getOperNowBalance() {
        return userLargeScreenTwoCustomizeMapper.getOperNowBalance();
    }

    /**
     * 查询所有运营部用户的userId
     * @return
     */
    @Override
    public List<String> getOperationUserId() {
        return userLargeScreenTwoCustomizeMapper.getOperationUserId();
    }

    /**
     * 查询运营部用户资金明细表下的所有userId
     * @return
     */
    @Override
    public List<String> getUserOperateListUserId() {
        return userLargeScreenTwoCustomizeMapper.getUserOperateListUserId();
    }

    /**
     * 查询坐席每日待回款金额表下的所有userId
     * @return
     */
    @Override
    public List<String> getRepaymentPlan() {
        return userLargeScreenTwoCustomizeMapper.getRepaymentPlan();
    }
}
