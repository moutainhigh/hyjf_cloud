package com.hyjf.cs.user.mq.producer;

import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.exception.MQException;
import com.hyjf.cs.user.mq.base.MessageContent;
import com.hyjf.cs.user.mq.base.Producer;
import com.hyjf.cs.user.mq.base.ProducerFieldsWrapper;
import org.springframework.stereotype.Component;

/**
 * @author xiasq
 * @version AccountProducer, v0.1 2018/4/12 15:09
 */

@Component
public class AccountProducer extends Producer {

	@Override
	protected ProducerFieldsWrapper getFieldsWrapper() {
		ProducerFieldsWrapper wrapper = new ProducerFieldsWrapper();
		wrapper.setGroup(MQConstant.ACCOUNT_GROUP);
		wrapper.setInstanceName(String.valueOf(System.currentTimeMillis()));
		return wrapper;
	}

	@Override
	public boolean messageSend(MessageContent messageContent) throws MQException {
		return super.messageSend(messageContent);
	}
}
