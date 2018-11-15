/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.market.mq.producer;

import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.exception.MQException;
import com.hyjf.cs.market.mq.base.MessageContent;
import com.hyjf.cs.market.mq.base.Producer;
import com.hyjf.cs.market.mq.base.ProducerFieldsWrapper;
import org.springframework.stereotype.Component;

/**
 * @author fuqiang
 * @version PcChannelStatisticsProducer, v0.1 2018/7/16 13:51
 */
@Component
public class PcChannelStatisticsAdminProducer extends Producer {
	@Override
	protected ProducerFieldsWrapper getFieldsWrapper() {
		ProducerFieldsWrapper wrapper = new ProducerFieldsWrapper();
		wrapper.setGroup(MQConstant.PC_CHANNEL_STATISTICS_ADMIN_GROUP);
		wrapper.setInstanceName(String.valueOf(System.currentTimeMillis()));
		return wrapper;
	}

	@Override
	public boolean messageSend(MessageContent messageContent) throws MQException {
		return super.messageSend(messageContent);
	}
}