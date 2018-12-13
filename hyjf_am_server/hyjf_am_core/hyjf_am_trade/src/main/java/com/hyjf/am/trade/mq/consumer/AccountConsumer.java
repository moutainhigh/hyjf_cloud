package com.hyjf.am.trade.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.trade.dao.model.auto.Account;
import com.hyjf.am.trade.service.front.account.AccountService;
import com.hyjf.am.vo.trade.account.AccountVO;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xiasq
 * @version AccountConsumer, v0.1 2018/4/12 14:58
 */

@Component
public class AccountConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
    private static final Logger logger = LoggerFactory.getLogger(AccountConsumer.class);
    @Autowired
    private AccountService accountService;

    @Override
    public void onMessage(MessageExt messageExt) {
        logger.info("AccountConsumer 收到消息，开始处理....");
        MessageExt msg = messageExt;
        AccountVO accountVO = JSONObject.parseObject(msg.getBody(), AccountVO.class);

        if (accountVO != null) {
            logger.info("注册保存账户表...userId is :", accountVO.getUserId());
            Account account = new Account();
            BeanUtils.copyProperties(accountVO, account);
            accountService.insert(account);
        } else {
            logger.error("消费失败，未获取账户信息...");
            return;// ConsumeConcurrentlyStatus.RECONSUME_LATER;
        }
        // 如果没有return success ，consumer会重新消费该消息，直到return success
        return;
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        // 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
        // 如果非第一次启动，那么按照上次消费的位置继续消费
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 设置为集群消费(区别于广播消费)
        defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
        logger.info("====AccountConsumer start=====");
    }
}
