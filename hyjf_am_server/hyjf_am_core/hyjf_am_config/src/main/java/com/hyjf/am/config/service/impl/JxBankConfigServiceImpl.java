/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.config.service.impl;

import com.hyjf.am.config.dao.mapper.auto.JxBankConfigMapper;
import com.hyjf.am.config.dao.model.auto.JxBankConfig;
import com.hyjf.am.config.dao.model.auto.JxBankConfigExample;
import com.hyjf.am.config.service.JxBankConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wangjun
 * @version JxBankConfigServiceImpl, v0.1 2018/7/25 16:10
 */
@Service
public class JxBankConfigServiceImpl implements JxBankConfigService {
    @Autowired
    JxBankConfigMapper jxBankConfigMapper;
    /**
     * 获取江西银行配置（快捷支付）
     */
    @Override
    public List<JxBankConfig> getQuickPaymentJxBankConfig(){
        JxBankConfigExample example = new JxBankConfigExample();
        JxBankConfigExample.Criteria cra = example.createCriteria();
        //支持快捷支付
        cra.andQuickPaymentEqualTo(1);
        //未删除
        cra.andDelFlagEqualTo(0);
        return jxBankConfigMapper.selectByExample(example);
    }

    /**
     * 根据bankId获取江西银行配置
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public List<JxBankConfig> getJxBankConfigByBankId(Integer bankId) {
        JxBankConfigExample example = new JxBankConfigExample();
        JxBankConfigExample.Criteria cra = example.createCriteria();
        //bankId
        cra.andBankIdEqualTo(bankId);
        //未删除
        cra.andDelFlagEqualTo(0);
        return jxBankConfigMapper.selectByExample(example);
    }
}
