package com.hyjf.am.trade.mq.producer;

import com.hyjf.am.trade.mq.base.MessageContent;
import com.hyjf.am.trade.mq.base.Producer;
import com.hyjf.am.trade.mq.base.ProducerFieldsWrapper;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.exception.MQException;
import org.springframework.stereotype.Component;

/**
 * @Auther: walter.limeng
 * @Date: 2018/7/19 10:21
 * @Description: CouponLoansMessageProducer
 */
@Component
public class CouponLoansMessageProducer extends Producer {
    @Override
    protected ProducerFieldsWrapper getFieldsWrapper() {
        ProducerFieldsWrapper wrapper = new ProducerFieldsWrapper();
        wrapper.setGroup(MQConstant.HZT_COUPON_LOAN_GROUP);
        wrapper.setInstanceName(String.valueOf(System.currentTimeMillis()));
        return wrapper;
    }

    @Override
    public boolean messageSend(MessageContent messageContent) throws MQException {
        return super.messageSend(messageContent);
    }
}
