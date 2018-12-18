package com.hyjf.am.user.mq.consumer;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.user.dao.model.auto.AppUtmReg;
import com.hyjf.am.user.service.front.user.AppUtmRegService;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.validator.Validator;

/**
 * @author xiasq
 * @version AppUtmRegConsumer, v0.1 2018/4/12 14:58
 */

@Service
@RocketMQMessageListener(topic = MQConstant.APP_CHANNEL_STATISTICS_DETAIL_TOPIC, selectorExpression = "*", consumerGroup = MQConstant.APP_CHANNEL_STATISTICS_DETAIL_GROUP)
public class AppUtmRegConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
	private static final Logger logger = LoggerFactory.getLogger(AppUtmRegConsumer.class);

	@Autowired
	private AppUtmRegService appUtmRegService;
	
	
	@Override
	public void onMessage(MessageExt  message) {
		logger.info("AppChannelStatisticsDetailConsumer 收到消息，开始处理....msgs is :{}", new String(message.getBody()));

		MessageExt msg = message;

		try {

			// 开户更新
			if (MQConstant.APP_CHANNEL_STATISTICS_DETAIL_UPDATE_TAG.equals(msg.getTags())) {
				Integer userId = JSONObject.parseObject(msg.getBody(), Integer.class);
				if (userId == null) {
					logger.error("参数错误，userId is null...");
					return ;
				}
				// 更新AppChannelStatisticsDetailDao开户时间
				AppUtmReg entity = appUtmRegService.findByUserId(userId);
				if (entity != null) {
					entity.setOpenAccountTime(new Date());
					appUtmRegService.update(entity);
				}
			} else if (MQConstant.APP_CHANNEL_STATISTICS_DETAIL_SAVE_TAG.equals(msg.getTags())) {
				logger.info("app渠道统计保存消息....");
				AppUtmReg entity = JSONObject.parseObject(msg.getBody(),
						AppUtmReg.class);
				if (entity != null) {
	                logger.info("entity: {}", JSONObject.toJSONString(entity));
					appUtmRegService.insert(entity);
				}
			} else if (MQConstant.APP_CHANNEL_STATISTICS_DETAIL_CREDIT_TAG.equals(msg.getTags())
					|| MQConstant.APP_CHANNEL_STATISTICS_DETAIL_INVEST_TAG.equals(msg.getTags())) {
				JSONObject entity = JSONObject.parseObject(msg.getBody(), JSONObject.class);
				logger.info("entity: {}", entity.toJSONString());
				if (Validator.isNotNull(entity)) {
					boolean investFlag = entity.getBooleanValue("investFlag");
					// 不是首投
					if (!investFlag) {
						Integer userId = entity.getInteger("userId");
						AppUtmReg appUtmReg = appUtmRegService.findByUserId(userId);
						BigDecimal accountDecimal = entity.getBigDecimal("accountDecimal")==null?BigDecimal.ZERO:entity.getBigDecimal("accountDecimal");
	                    BigDecimal invest = appUtmReg.getCumulativeInvest() == null? BigDecimal.ZERO: appUtmReg.getCumulativeInvest();
	                    appUtmReg.setCumulativeInvest(invest.add(accountDecimal));
	                    appUtmRegService.update(appUtmReg);
					} else {
					    // 首投
	                    Integer userId = entity.getInteger("userId");
	                    AppUtmReg appUtmReg = appUtmRegService.findByUserId(userId);
						BigDecimal accountDecimal = entity.getBigDecimal("accountDecimal")==null?BigDecimal.ZERO:entity.getBigDecimal("accountDecimal");
						String projectType = entity.getString("projectType");
						Integer investTime = entity.getInteger("investTime");
						String investProjectPeriod = entity.getString("investProjectPeriod");
	                    appUtmReg.setCumulativeInvest(accountDecimal);
	                    appUtmReg.setInvestAmount(accountDecimal);
	                    appUtmReg.setInvestProjectType(projectType);
	                    appUtmReg.setFirstInvestTime(investTime);
	                    appUtmReg.setInvestProjectPeriod(investProjectPeriod);
	                    appUtmRegService.update(appUtmReg);
					}
				}
			}
			
		} catch (Exception e) {
			logger.error("消费处理异常",e);
		}
	
	
	}

	@Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
//		defaultMQPushConsumer.setInstanceName(String.valueOf(System.currentTimeMillis()));
//		defaultMQPushConsumer.setConsumerGroup(MQConstant.APP_CHANNEL_STATISTICS_DETAIL_GROUP);
//		// 订阅指定MyTopic下tags等于MyTag
//		defaultMQPushConsumer.subscribe(MQConstant.APP_CHANNEL_STATISTICS_DETAIL_TOPIC, "*");
		// 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
		// 如果非第一次启动，那么按照上次消费的位置继续消费
		defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
		// 设置为集群消费(区别于广播消费)
		defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
		defaultMQPushConsumer.setMaxReconsumeTimes(3);
//		defaultMQPushConsumer.registerMessageListener(new MessageListener());
		logger.info("====AppChannelStatisticsDetail consumer=====");
	}
}
