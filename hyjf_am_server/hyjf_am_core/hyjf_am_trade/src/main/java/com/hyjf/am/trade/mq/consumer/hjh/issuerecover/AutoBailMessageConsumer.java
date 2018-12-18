package com.hyjf.am.trade.mq.consumer.hjh.issuerecover;/**
 * @Auther: Walter
 * @Date: 2018/7/11 18:52
 * @Description:
 */

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.trade.dao.model.auto.Borrow;
import com.hyjf.am.trade.dao.model.auto.BorrowInfo;
import com.hyjf.am.trade.dao.model.auto.HjhAssetBorrowtype;
import com.hyjf.am.trade.mq.base.CommonProducer;
import com.hyjf.am.trade.mq.base.MessageContent;
import com.hyjf.am.trade.service.task.issuerecover.AutoBailMessageService;
import com.hyjf.am.trade.service.task.issuerecover.AutoRecordService;
import com.hyjf.am.vo.trade.hjh.issuerecover.AutoIssuerecoverVO;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.exception.MQException;
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

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Auther: walter.limeng
 * @Date: 2018/7/11 18:52
 * @Description: AutoBailMessageConsumer
 * 保证金审核
 */
@Service
@RocketMQMessageListener(topic = MQConstant.ROCKETMQ_BORROW_BAIL_TOPIC, selectorExpression = "*", consumerGroup = MQConstant.ROCKETMQ_BORROW_BAIL_GROUP)
public class AutoBailMessageConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
    private static final Logger logger = LoggerFactory.getLogger(AutoBailMessageConsumer.class);

    @Resource
    private AutoBailMessageService autoBailMessageService;
    @Resource
    private CommonProducer commonProducer;
    @Autowired
    private AutoRecordService autoRecordService;

    @Override
    public void onMessage(MessageExt messageExt) {
        logger.info("AutoSendMessageConsumer 收到消息，开始处理....");
        MessageExt msg = messageExt;
        String borrowNid = null;
        try {
            AutoIssuerecoverVO autoIssuerecoverVO = JSONObject.parseObject(msg.getBody(), AutoIssuerecoverVO.class);
            // 计划加入号
            borrowNid = autoIssuerecoverVO.getBorrowNid();
            // 计划加入号为空
            if (null == borrowNid) {
                logger.error("标的编号为空");
                return;
            }

            // --> 消息处理
            Borrow borrow = autoBailMessageService.getBorrowByBorrowNidrowNid(borrowNid);
            BorrowInfo borrowInfo = autoBailMessageService.getByBorrowNid(borrowNid);
            try {

                if(borrow == null){
                    logger.info(borrow.getBorrowNid()+" 该资产在表里不存在！！");
                    return;
                }
                // 自动审核保证金
                logger.info(borrow.getBorrowNid()+" 开始自动审核保证金 "+ borrowInfo.getInstCode());
                // redis 防重复检查
                String redisKey = RedisConstants.BORROW_BAIL + borrowInfo.getInstCode()+borrow.getBorrowNid();
                boolean result = RedisUtils.tranactionSet(redisKey, 300);
                if(!result){
                    logger.info(borrowInfo.getInstCode()+" 正在自动审核保证金(redis) "+borrow.getBorrowNid());
                    return;
                }
                // 业务校验
                if(borrow.getStatus() != null && borrow.getStatus().intValue() == 1 &&
                        borrow.getVerifyStatus() != null && borrow.getVerifyStatus().intValue() == 0){
                    logger.info(borrow.getBorrowNid()+" 该标的状态为审核保证金状态、开始自动审核");
                } else {
                    logger.warn(borrow.getBorrowNid()+" 该标的状态不是审核保证金状态");
                    return;
                }
                //判断该资产是否可以自动审核保证金
                HjhAssetBorrowtype hjhAssetBorrowType = autoRecordService.selectAssetBorrowType(borrowInfo);
                boolean flag = autoBailMessageService.updateRecordBorrow(borrowInfo,borrow,hjhAssetBorrowType);
                if (!flag) {
                    logger.error("自动审核保证金失败！" + "[资产编号：" + borrow.getBorrowNid() + "]");
                }else{
                    try {
                        JSONObject params = new JSONObject();
                        params.put("borrowNid", borrow.getBorrowNid());
                        commonProducer.messageSend(new MessageContent(MQConstant.ROCKETMQ_BORROW_PREAUDIT_TOPIC, UUID.randomUUID().toString(), params));
                    } catch (MQException e) {
                        logger.error("发送【初审队列】MQ失败...");
                    }
                    logger.info(borrow.getBorrowNid()+" 成功发送到初审队列");
                }
                logger.info(borrow.getBorrowNid()+" 结束自动审核保证金");

            } catch (Exception e) {
                logger.error("自动审核保证金异常！",e);
            }
            // 如果没有return success ，consumer会重新消费该消息，直到return success
            return;
        } catch (Exception e) {
            logger.error("自动审核保证金异常！",e);
            return;
        }
        // finally中最好不要用return
            /*finally {
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }*/
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        // 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
        // 如果非第一次启动，那么按照上次消费的位置继续消费
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 设置为集群消费(区别于广播消费)
        defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
        logger.info("====AutoBailMessageConsumer start=====");
    }
}
