/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.message.mq.consumer;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.vo.trade.CalculateInvestInterestVO;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.cs.message.bean.ic.CalculateInvestInterest;
import com.hyjf.cs.message.bean.ic.TotalInvestAndInterestEntity;
import com.hyjf.cs.message.mongo.ic.CalculateInvestInterestDao;
import com.hyjf.cs.message.mongo.ic.TotalInvestAndInterestMongoDao;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description 网站累计出借追加 、  修改mongodb运营数据
 * @Author sunss
 * @Date 2018/7/7 15:16
 */
@Service
@RocketMQMessageListener(topic = MQConstant.STATISTICS_CALCULATE_INVEST_INTEREST_TOPIC, selectorExpression = "*", consumerGroup = MQConstant.STATISTICS_CALCULATE_INVEST_INTEREST_GROUP)
public class CalculateInvestInterestConsumer implements RocketMQListener<MessageExt>, RocketMQPushConsumerLifecycleListener {
    Logger logger = LoggerFactory.getLogger(getClass());

    private static int MAX_RECONSUME_TIME = 3;

    @Autowired
    private CalculateInvestInterestDao calculateInvestInterestDao;
    @Autowired
    private TotalInvestAndInterestMongoDao totalInvestAndInterestMongoDao;

    @Override
    public void onMessage(MessageExt  message) {
        logger.info("CalculateInvestInterestConsumer 收到消息，开始处理....msgId is :{}", message.getMsgId());
         JSONObject data = JSONObject.parseObject(message.getBody(), JSONObject.class);

            // 网站累计投资累加
            if (MQConstant.STATISTICS_CALCULATE_INVEST_SUM_TAG.equals(message.getTags())) {
                BigDecimal tenderSum = (BigDecimal) data.get("tenderSum");
                Integer nowTime = (Integer) data.get("nowTime");
                Query query = new Query();
                Update update = new Update();
                update.inc("tenderSum", tenderSum).set("updateTime", nowTime);
                calculateInvestInterestDao.update(query, update);
            }

            // 网站累计收益累加
            else if (MQConstant.STATISTICS_CALCULATE_INTEREST_SUM_TAG.equals(message.getTags())) {
                BigDecimal interestSum = (BigDecimal) data.get("interestSum");
                Integer nowTime = (Integer) data.get("nowTime");
                Query query = new Query();
                Update update = new Update();
                update.inc("interestSum", interestSum).set("updateTime", nowTime);
                calculateInvestInterestDao.update(query, update);
            }
            // 平台数据更新
            else if (MQConstant.STATISTICS_CALCULATE_INTEREST_UPDATE_TAG.equals(message.getTags())) {
                CalculateInvestInterestVO investInterestVO = JSONObject.parseObject(message.getBody(), CalculateInvestInterestVO.class);
                CalculateInvestInterest investInterest = new CalculateInvestInterest();
                BeanUtils.copyProperties(investInterestVO, investInterest);
                Query query = new Query();
                Update update = new Update();
                update.set("sevenDayTenderSum", investInterest.getSevenDayTenderSum()).set("sevenDayInterestSum", investInterest.getSevenDayInterestSum()).set("borrowZeroOne", investInterest.getBorrowZeroOne()).set("borrowOneThree", investInterest.getBorrowOneThree()).set("borrowThreeSix", investInterest.getBorrowThreeSix()).set("borrowSixTwelve", investInterest.getBorrowSixTwelve()).set("borrowTwelveUp", investInterest.getBorrowTwelveUp()).set("investOneDown", investInterest.getInvestOneDown()).set("investOneFive", investInterest.getInvestOneFive()).set("investFiveTen", investInterest.getInvestFiveTen()).set("investTenFifth", investInterest.getInvestTenFifth()).set("investFifthUp", investInterest.getInvestFifthUp()).set("updateTime", investInterest.getUpdateTime());
                calculateInvestInterestDao.update(query, update);
            }

            // 更新t_total_invest_and_interest- 运营数据用，考虑和上面的表合并（）
            else {
                if (data.containsKey("money")) {
                    BigDecimal money = new BigDecimal(data.get("money").toString());
                    // 已收利息
                    BigDecimal recoverInterestAmount = (BigDecimal) data.get("recoverInterestAmount");
                    Integer type = (Integer) data.get("type");
                    if (type == null) {
                        logger.error("传入参数错误！type为空！");
                        return ;
                    }
                    // 投资增加交易总额
                    if (type.equals(1)) {
                        TotalInvestAndInterestEntity entity = totalInvestAndInterestMongoDao.findOne(new Query());
                        // 第一次插入
                        if (entity == null) {
                            entity = new TotalInvestAndInterestEntity();
                        }
                        entity.setTotalInvestAmount(entity.getTotalInvestAmount().add(money == null ? BigDecimal.ZERO : money));
                        entity.setTotalInvestNum(entity.getTotalInvestNum() + 1);
                        logger.info("运营数据type=1, entity is :{}", entity);
                        // save没有插入，有则更新
                        totalInvestAndInterestMongoDao.save(entity);
                    } else if (type.equals(2)) {
                        // 还款添加收益
                        // 累计收益(实时)
                        //BigDecimal totalInterestAmount = operationDataService.countTotalInterestAmount();
                        BigDecimal totalInterestAmount = recoverInterestAmount == null ? BigDecimal.ZERO : recoverInterestAmount;
                        logger.info("已收收益： {}", totalInterestAmount.toString());

                        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
                        TotalInvestAndInterestEntity entity = totalInvestAndInterestMongoDao.findOne(new Query());
                        // 第一次插入
                        if (entity == null) {
                            entity = new TotalInvestAndInterestEntity();
                        }
                        entity.setTotalInterestAmount(entity.getTotalInterestAmount().add(totalInterestAmount));
                        if (!CollectionUtils.isEmpty(list)) {
                            Map<String, Object> map = list.get(0);
                            BigDecimal interestTotal = (BigDecimal) map.get("interest_total");
                            entity.setHjhTotalInterestAmount(interestTotal);
                        }
                        logger.info("运营数据type=2, entity is :{}", entity);
                        // save没有插入，有则更新
                        totalInvestAndInterestMongoDao.save(entity);
                    } else if (type.equals(3)) {  // 计划
                        // 查询计划数据
                        TotalInvestAndInterestEntity entity = totalInvestAndInterestMongoDao.findOne(new Query());
                        if (entity == null) {
                            entity = new TotalInvestAndInterestEntity();
                        }
                        entity.setTotalInvestAmount(entity.getTotalInvestAmount().add(money == null ? BigDecimal.ZERO : money));
                        entity.setTotalInvestNum(entity.getTotalInvestNum() + 1);
                        entity.setHjhTotalInvestAmount(entity.getHjhTotalInvestAmount().add(money == null ? BigDecimal.ZERO : money.divide(new BigDecimal(10000))));
                        entity.setHjhTotalInvestNum(entity.getHjhTotalInvestNum() + 1);
                        logger.info("运营数据type=3, entity is :{}", entity);
                        // save没有插入，有则更新
                        totalInvestAndInterestMongoDao.save(entity);
                    }
                }
            }

    }
    @Override
    public void prepareStart(DefaultMQPushConsumer defaultMQPushConsumer) {
        // 如果非第一次启动，那么按照上次消费的位置继续消费
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        // 设置为集群消费(区别于广播消费)
        defaultMQPushConsumer.setMessageModel(MessageModel.CLUSTERING);
        //设置最大重试次数
        defaultMQPushConsumer.setMaxReconsumeTimes(MAX_RECONSUME_TIME);
        logger.info("====FddCertificateConsumer consumer=====");
    }


}
