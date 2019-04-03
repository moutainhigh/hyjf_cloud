package com.hyjf.signatrues.mq.producer;

import com.alibaba.fastjson.JSON;
import com.hyjf.signatrues.mq.base.MessageContent;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.hyjf.common.exception.MQException;

/**
 * 通用发消息
 *
 * @author dxj
 */
@Component
@SuppressWarnings("unchecked")
public class CommonProducer {
    private static final Logger logger = LoggerFactory.getLogger(CommonProducer.class);

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    /**
     * 发送普通队列通用方法
     * @param messageContent
     * @return
     * @throws MQException
     */
    public boolean messageSend(MessageContent messageContent) throws MQException {
        try {
            SendResult sendResult = null;
            if(StringUtils.isNotEmpty(messageContent.keys)) {
                Message<?> message = MessageBuilder.withPayload(messageContent.body).setHeader(MessageConst.PROPERTY_KEYS, messageContent.keys).build();
                sendResult = rocketMQTemplate.syncSend(messageContent.topic + ":" + messageContent.tag, message);
            }else {
                sendResult = rocketMQTemplate.syncSend(messageContent.topic + ":" + messageContent.tag, messageContent.body);
            }
            if (sendResult != null && sendResult.getSendStatus() == SendStatus.SEND_OK) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("通用发消息异常。message:{}", JSON.toJSONString(messageContent), e);
            throw new MQException("mq send error", e);
        }
    }

    /**
     * 发送延时队列通用方法
     *
     * @param messageContent
     * @param delayLevel
     * @return
     * @throws MQException
     */
    public boolean messageSendDelay(MessageContent messageContent, int delayLevel) throws MQException {
        try {
            Message<?> message = MessageBuilder.withPayload(messageContent.body).setHeader(MessageConst.PROPERTY_KEYS, messageContent.keys).build();
            SendResult sendResult = rocketMQTemplate.syncSend(messageContent.topic + ":" + messageContent.tag, message, rocketMQTemplate.getProducer().getSendMsgTimeout(), delayLevel);

            if (sendResult != null && sendResult.getSendStatus() == SendStatus.SEND_OK) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            logger.error("通用延时消息发送异常。message:{}", JSON.toJSONString(messageContent), e);
            throw new MQException("mq send error", e);
        }
    }
}
