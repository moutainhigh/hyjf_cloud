/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.message.mongo.mc;

import com.hyjf.am.resquest.message.MessagePushMsgRequest;
import com.hyjf.common.util.GetDate;
import com.hyjf.cs.message.bean.mc.MessagePushMsg;
import com.hyjf.cs.message.mongo.ic.BaseMongoDao;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author yaoyong
 * @version MessagePushMessageDao, v0.1 2018/8/15 16:41
 */
@Repository
public class MessagePushMessageDao extends BaseMongoDao<MessagePushMsg> {
    @Override
    protected Class<MessagePushMsg> getEntityClass() {
        return MessagePushMsg.class;
    }

    /**
     * 查询手动发放消息列表
     * @param request
     * @return
     */
    public List<MessagePushMsg> selectMessagePushMsg(MessagePushMsgRequest request) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (request.getStartDateSrch() != null) {
            Date startTime = GetDate.stringToDate2(request.getStartDateSrch());
            criteria.and("createTime").gte((int) (startTime.getTime() / 1000));
        }
        if (request.getEndDateSrch() != null) {
            Date endTime = GetDate.stringToDate2(request.getEndDateSrch());
            criteria.and("createTime").lte((int) (endTime.getTime() / 1000));
        }
        if (request.getTagId() != null) {
            criteria.and("tagId").regex(request.getTagId().toString());
        }
        int currPage = request.getCurrPage();
        int pageSize = request.getPageSize();
        int limitStart = (currPage - 1) * pageSize;
        int limitEnd = limitStart + pageSize;
        query.addCriteria(criteria);
        query.skip(limitStart).limit(limitEnd);
        return mongoTemplate.find(query, getEntityClass());
    }

    /**
     * 根据id查询手动发放消息
     * @param id
     * @return
     */
    public MessagePushMsg getMessagePushMsgById(Integer id) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("id").is(id);
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query,getEntityClass());
    }

}
