package com.hyjf.am.trade.mq.consumer.hjh.issuerecover;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.trade.dao.model.auto.Borrow;
import com.hyjf.am.trade.dao.model.auto.BorrowInfo;
import com.hyjf.am.trade.dao.model.auto.HjhAssetBorrowtype;
import com.hyjf.am.trade.dao.model.auto.HjhPlanAsset;
import com.hyjf.am.trade.mq.base.CommonProducer;
import com.hyjf.am.trade.mq.base.MessageContent;
import com.hyjf.am.trade.service.task.issuerecover.AutoBailMessageService;
import com.hyjf.am.trade.service.task.issuerecover.AutoPreAuditMessageService;
import com.hyjf.am.trade.service.task.issuerecover.AutoRecordService;
import com.hyjf.am.vo.trade.hjh.issuerecover.AutoIssuerecoverVO;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.exception.MQException;
import org.apache.commons.lang3.StringUtils;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.UUID;

/**
 * @Auther: walter.limeng
 * @Date: 2018/7/11 19:24
 * 自动初审
 * @Description: autoPreAuditMessageConsumer
 */
@Service
@RocketMQMessageListener(topic = MQConstant.ROCKETMQ_BORROW_PREAUDIT_TOPIC, selectorExpression = "*", consumerGroup = MQConstant.ROCKETMQ_BORROW_PREAUDIT_GROUP)
public class AutoPreAuditMessageConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
    private static final Logger logger = LoggerFactory.getLogger(AutoPreAuditMessageConsumer.class);

    @Resource
    private AutoPreAuditMessageService autoPreAuditMessageService;
    @Resource
    private AutoBailMessageService autoBailMessageService;
    @Resource
    private AutoRecordService autoRecordService;
    @Resource
    private CommonProducer commonProducer;

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
            if (autoIssuerecoverVO == null || (autoIssuerecoverVO.getAssetId() == null && autoIssuerecoverVO.getBorrowNid() == null)) {
                logger.error("标的编号为空");
                return;
            }
            // --> 消息处理
            if(null != borrowNid){
                Borrow borrow = autoBailMessageService.getBorrowByBorrowNidrowNid(borrowNid);
                BorrowInfo borrowInfo = autoBailMessageService.getByBorrowNid(borrowNid);
                // 自动初审
                logger.info(borrow.getBorrowNid() + " 开始自动初审 " + borrowInfo.getInstCode());
                if (borrow == null) {
                    logger.warn(" 该资产在表里不存在！！");
                    return;
                }
                // redis 防重复检查
                String redisKey = RedisConstants.BORROW_PRE_AUDIT + borrowInfo.getInstCode() + borrow.getBorrowNid();
                boolean result = RedisUtils.tranactionSet(redisKey, 300);
                if (!result) {
                    logger.info(borrowInfo.getInstCode() + " 正在初审(redis) " + borrow.getBorrowNid());
                    return;
                }
                // 业务校验
                if (borrow.getStatus() != null && borrow.getStatus().intValue() != 1 &&
                        borrow.getVerifyStatus() != null && borrow.getVerifyStatus().intValue() != 1) {
                    logger.warn(borrow.getBorrowNid() + " 该资产状态不是初审(已审核保证金)状态");
                    return;
                }
                //判断该资产是否可以自动初审，是否关联计划
                HjhAssetBorrowtype hjhAssetBorrowType = autoRecordService.selectAssetBorrowType(borrowInfo);
                if (hjhAssetBorrowType == null || hjhAssetBorrowType.getAutoAudit() == null || hjhAssetBorrowType.getAutoAudit() != 1) {
                    logger.warn(borrow.getBorrowNid() + " 标的不能自动初审,原因自动初审未配置");
                    return;
                }

                boolean flag = autoPreAuditMessageService.updateRecordBorrow(borrow,borrowInfo);
                if (!flag) {
                    logger.error("自动初审失败！" + "[项目编号：" + borrow.getBorrowNid() + "]");
                } else {
                    if (borrow.getIsEngineUsed() == 1) {
                        // 成功后到关联计划队列
                        try {
                            JSONObject params = new JSONObject();
                            params.put("borrowNid", borrow.getBorrowNid());
                            //modify by yangchangwei 防止队列触发太快，导致无法获得本事务变泵的数据，延时级别为2 延时5秒
                            commonProducer.messageSendDelay(new MessageContent(MQConstant.ROCKETMQ_BORROW_ISSUE_TOPIC, UUID.randomUUID().toString(), params),2);
                        } catch (MQException e) {
                            logger.error("发送【关联计划队列】MQ失败...");
                        }
                    } else {
                        // 散标修改redis：借款的borrowNid,account借款总额
                        RedisUtils.set(RedisConstants.BORROW_NID+borrow.getBorrowNid(), borrow.getAccount().toString());
                    }
                }
                logger.info(borrow.getBorrowNid() + " 结束自动初审");
            }
            /*--------------upd by liushouyi HJH3 End--------------*/
            if (StringUtils.isNotBlank(autoIssuerecoverVO.getAssetId())) {
                // 资产自动初审
                logger.info(autoIssuerecoverVO.getAssetId() + " 开始自动初审 " + autoIssuerecoverVO.getInstCode());
                HjhPlanAsset hjhPlanAsset = autoPreAuditMessageService.selectPlanAsset(autoIssuerecoverVO.getAssetId(), autoIssuerecoverVO.getInstCode());
                if (hjhPlanAsset == null) {
                    logger.warn(autoIssuerecoverVO.getAssetId() + " 该资产在表里不存在！！");
                    return;
                }
                // redis 防重复检查
                String redisKeys = RedisConstants.BORROW_PRE_AUDIT + hjhPlanAsset.getInstCode() + hjhPlanAsset.getAssetId();
                boolean results = RedisUtils.tranactionSet(redisKeys, 300);
                if (!results) {
                    logger.info(hjhPlanAsset.getInstCode() + " 正在初审(redis) " + hjhPlanAsset.getAssetId());
                    return;
                }
                // 业务校验
                if (hjhPlanAsset.getStatus() != null && hjhPlanAsset.getStatus().intValue() != 5 &&
                        hjhPlanAsset.getVerifyStatus() != null && hjhPlanAsset.getVerifyStatus().intValue() == 1) {
                    logger.info(autoIssuerecoverVO.getAssetId() + " 该资产状态不是初审状态");
                    return;
                }
                //判断该资产是否可以自动初审，是否关联计划t
                HjhAssetBorrowtype hjhAssetBorrowType = autoPreAuditMessageService.selectAssetBorrowType(hjhPlanAsset);
                boolean flags = autoPreAuditMessageService.updateRecordBorrow(hjhPlanAsset, hjhAssetBorrowType);
                if (!flags) {
                    logger.error("自动初审失败！" + "[资产编号：" + hjhPlanAsset.getAssetId() + "]");
                } else {
                    // 成功后到关联计划队列
                    try {
                        JSONObject params = new JSONObject();
                        params.put("borrowNid", hjhPlanAsset.getBorrowNid());
                        //modify by yangchangwei 防止队列触发太快，导致无法获得本事务变泵的数据，延时级别为2 延时5秒
                        commonProducer.messageSendDelay(new MessageContent(MQConstant.ROCKETMQ_BORROW_ISSUE_TOPIC, UUID.randomUUID().toString(), params),2);
                    } catch (MQException e) {
                        logger.error("发送【关联计划队列】MQ失败...");
                    }
                }
                logger.info(hjhPlanAsset.getAssetId() + " 结束自动初审");
                // 如果没有return success ，consumer会重新消费该消息，直到return success
                return;
            }
        } catch (Exception e) {
            logger.error("自动初审异常！",e);
            return;
        }
        return;
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        // 设置Consumer第一次启动是从队列头部开始消费还是队列尾部开始消费
        // 如果非第一次启动，那么按照上次消费的位置继续消费
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 设置为集群消费(区别于广播消费)
        defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
        logger.info("====AutoPreAuditMessageConsumer start=====");
    }
}
