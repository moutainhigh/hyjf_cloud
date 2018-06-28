/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.trade.mq;

import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.exception.MQException;
import org.springframework.stereotype.Component;

/**
 * 汇计划进入锁定期和退出
 * @author PC-LIUSHOUYI
 * @version HjhQuitProducer, v0.1 2018/6/27 16:56
 */
@Component
public class HjhQuitProducer extends Producer {

    @Override
    protected ProducerFieldsWrapper getFieldsWrapper() {
    ProducerFieldsWrapper wrapper = new ProducerFieldsWrapper();
    wrapper.setGroup(MQConstant.HJH_QUIT);
    wrapper.setInstanceName(String.valueOf(System.currentTimeMillis()));
    return wrapper;
}

    @Override
    public boolean messageSend(MassageContent messageContent) throws MQException {
        return super.messageSend(messageContent);
    }
}