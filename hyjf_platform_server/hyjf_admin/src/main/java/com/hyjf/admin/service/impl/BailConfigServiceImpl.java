/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service.impl;

import com.hyjf.admin.client.AmAdminClient;
import com.hyjf.admin.common.service.BaseServiceImpl;
import com.hyjf.admin.service.BailConfigService;
import com.hyjf.am.response.IntegerResponse;
import com.hyjf.am.resquest.admin.BailConfigAddRequest;
import com.hyjf.am.resquest.admin.BailConfigRequest;
import com.hyjf.am.vo.admin.BailConfigCustomizeVO;
import com.hyjf.am.vo.admin.BailConfigInfoCustomizeVO;
import com.hyjf.am.vo.user.HjhInstConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author PC-LIUSHOUYI
 * @version BailConfigServiceImpl, v0.1 2018/9/26 16:36
 */
@Service
public class BailConfigServiceImpl extends BaseServiceImpl implements BailConfigService {

    @Autowired
    AmAdminClient amAdminClient;

    /**
     * 获取保证金配置总数
     *
     * @param request
     * @return
     */
    @Override
    public Integer selectBailConfigCount(BailConfigRequest request) {
        return amAdminClient.selectBailConfigCount(request);
    }

    /**
     * 获取保证金配置列表
     *
     * @param request
     * @return
     */
    @Override
    public List<BailConfigCustomizeVO> selectRecordList(BailConfigRequest request) {
        return amAdminClient.selectBailConfigRecordList(request);
    }
    @Override
    public IntegerResponse countBailConfigRecordList(BailConfigRequest request){
        return amAdminClient.countBailConfigRecordList(request);
    }
    /**
     * 更新当前机构可用的还款方式并返回最新保证金详情
     *
     * @param id
     * @return
     */
    @Override
    public BailConfigInfoCustomizeVO updateSelectBailConfigById(Integer id) {
        return amAdminClient.updateSelectBailConfigById(id);
    }

    /**
     * 未配置保证金的机构编号
     *
     * @return
     */
    @Override
    public List<HjhInstConfigVO> selectNoUsedInstConfigList() {
        return amAdminClient.selectNoUsedInstConfigList();
    }

    /**
     * 添加保证金配置
     *
     * @param bailConfigAddRequest
     * @return
     */
    @Override
    public boolean insertBailConfig(BailConfigAddRequest bailConfigAddRequest) {
        return amAdminClient.insertBailConfig(bailConfigAddRequest);
    }

    /**
     * 周期内发标已发额度
     *
     * @param bailConfigAddRequest
     * @return
     */
    @Override
    public String selectSendedAccountByCyc(BailConfigAddRequest bailConfigAddRequest) {
        return amAdminClient.selectSendedAccountByCyc(bailConfigAddRequest);
    }

    /**
     * 根据该机构可用还款方式更新可用授信方式
     *
     * @param instCode
     * @return
     */
    @Override
    public boolean updateBailInfoDelFlg(String instCode) {
        return amAdminClient.updateBailInfoDelFlg(instCode);
    }

    /**
     * 更新保证金配置
     *
     * @param bailConfigAddRequest
     * @return
     */
    @Override
    public boolean updateBailConfig(BailConfigAddRequest bailConfigAddRequest) {
        return amAdminClient.updateBailConfig(bailConfigAddRequest);
    }

    /**
     * 删除保证金配置
     *
     * @param bailConfigAddRequest
     * @return
     */
    @Override
    public boolean deleteBailConfig(BailConfigAddRequest bailConfigAddRequest) {
        return amAdminClient.deleteBailConfig(bailConfigAddRequest);
    }

    /**
     * 获取当前机构可用还款方式
     *
     * @param instCode
     * @return
     */
    @Override
    public List<String> selectRepayMethod(String instCode) {
        return amAdminClient.selectRepayMethod(instCode);
    }
}
