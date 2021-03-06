package com.hyjf.admin.service;

import com.hyjf.am.response.IntegerResponse;
import com.hyjf.am.response.admin.AdminBankRechargeConfigResponse;
import com.hyjf.am.resquest.admin.AdminBankRechargeConfigRequest;
import com.hyjf.am.vo.config.BankRechargeLimitConfigVO;
import com.hyjf.am.vo.trade.BankConfigVO;

import java.util.List;

/**
 * @author by xiehuili on 2018/7/19.
 */
public interface BankRechargeService {
    /**
     * 查询快捷充值限额列表
     * @param adminRequest
     * @return
     */
    public AdminBankRechargeConfigResponse bankRechargeInit(AdminBankRechargeConfigRequest adminRequest);
    /**
     * 查询快捷充值限额详情页面
     * @param adminRequest
     * @return
     */
    public AdminBankRechargeConfigResponse selectBankRechargeConfigInfo(AdminBankRechargeConfigRequest adminRequest);

    /**
     * 编辑保存快捷充值限额
     * @return
     */
    public AdminBankRechargeConfigResponse saveBankRechargeConfig(AdminBankRechargeConfigRequest req);

    /**
     * 修改快捷充值限额
     * @return
     */
    public AdminBankRechargeConfigResponse updateBankRechargeConfig(AdminBankRechargeConfigRequest req);

    /**
     * 删除快捷充值限额
     * @return
     */
    public AdminBankRechargeConfigResponse deleteBankRechargeConfig(AdminBankRechargeConfigRequest req);

    /**
     * 检查银行卡是否重复
     * @return
     */
    public IntegerResponse bankIsExists(AdminBankRechargeConfigRequest adminRequest);

    /**
     * 查询快捷充值限额列表
     * @param adminRequest
     * @return
     */
    public List<BankRechargeLimitConfigVO> exportRecordList(BankRechargeLimitConfigVO adminRequest);

    /**
     * 查询快捷充值限额列表
     * @return
     */
    public List<BankConfigVO> getBankRecordList();
}
