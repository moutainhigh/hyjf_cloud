package com.hyjf.cs.message.mongo.mc;

import com.hyjf.am.resquest.admin.MessagePushNoticesRequest;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetDate;
import com.hyjf.cs.message.bean.mc.MessagePushMsg;
import com.hyjf.cs.message.mongo.ic.BaseMongoDao;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author lisheng
 * @version MessagePushNoticesMongoDao, v0.1 2018/8/14 15:06
 */
@Repository
public class MessagePushMsgMongoDao extends BaseMongoDao<MessagePushMsg> {
    /**
     * 获取记录条数
     * @return
     */
    public Integer countRecordList(MessagePushNoticesRequest form){
        Criteria criteria = new Criteria();

        if (form.getTagId() != null) {
            criteria.and("tagId").equals(form.getTagId());
        }
        if (StringUtils.isNotEmpty(form.getNoticesTitleSrch())) {
            criteria.and("msgTitle").regex(form.getNoticesTitleSrch());
        }
        if (StringUtils.isNotEmpty(form.getNoticesCodeSrch())) {
            criteria.and("msgCode").regex(form.getNoticesCodeSrch());
        }
        if (StringUtils.isNotEmpty(form.getNoticesCreateUserNameSrch())) {
            criteria.and("createUserName").regex(form.getNoticesCreateUserNameSrch());
        }
        if (StringUtils.isNotEmpty(form.getNoticesTerminalSrch())) {
            criteria.and("msgTerminal").regex(form.getNoticesTerminalSrch());
        }
        if (form.getNoticesSendStatusSrch() != null) {
            criteria.and("msgSendStatus").equals(form.getNoticesSendStatusSrch());
        }

        if (StringUtils.isNotEmpty(form.getStartSendTimeSrch())) {
            Integer time = GetDate.strYYYYMMDDHHMMSS2Timestamp2(form.getStartSendTimeSrch());
            criteria.and("sendTime").gte(time);
            if (form.getEndSendTimeSrch() != null) {
                Integer time2 = GetDate.strYYYYMMDDHHMMSS2Timestamp2(form.getEndSendTimeSrch());
                criteria.lte(time2);
            }
        }else{
            if (StringUtils.isNotEmpty(form.getEndSendTimeSrch())) {
                Integer time2 = GetDate.strYYYYMMDDHHMMSS2Timestamp2(form.getEndSendTimeSrch());
                criteria.and("sendTime").lte(time2);
            }
        }


        criteria.and("msgDestinationType").equals(CustomConstants.MSG_PUSH_DESTINATION_TYPE_0);

        Query query = new Query(criteria);
        return (int)mongoTemplate.count(query,MessagePushMsg.class);
    }
    /**
     * 获取发送消息记录列表
     * @param form
     * @return
     */
    public List<MessagePushMsg> getRecordList(MessagePushNoticesRequest form, Integer offset, Integer limit){
        Criteria criteria = new Criteria();
        if (StringUtils.isNotEmpty(form.getEndSendTimeSrch())) {
            criteria.and("msgDestinationType").equals(form.getEndSendTimeSrch());
        }
        if (form.getTagId() != null) {
            criteria.and("tagId").equals(form.getTagId());
        }
        if (StringUtils.isNotEmpty(form.getNoticesTitleSrch())) {
            criteria.and("msgTitle").regex(form.getNoticesTitleSrch());
        }
        if (StringUtils.isNotEmpty(form.getNoticesCodeSrch())) {
            criteria.and("msgCode").regex(form.getNoticesCodeSrch());
        }
        if (StringUtils.isNotEmpty(form.getNoticesCreateUserNameSrch())) {
            criteria.and("createUserName").regex(form.getNoticesCreateUserNameSrch());
        }
        if (StringUtils.isNotEmpty(form.getNoticesTerminalSrch())) {
            criteria.and("msgTerminal").regex(form.getNoticesTerminalSrch());
        }
        if (form.getNoticesSendStatusSrch() != null) {
            criteria.and("msgSendStatus").equals(form.getNoticesSendStatusSrch());
        }
        if (StringUtils.isNotEmpty(form.getStartSendTimeSrch())) {
            Integer time = GetDate.strYYYYMMDDHHMMSS2Timestamp2(form.getStartSendTimeSrch());
            criteria.and("sendTime").gte(time);
            if (form.getEndSendTimeSrch() != null) {
                Integer time2 = GetDate.strYYYYMMDDHHMMSS2Timestamp2(form.getEndSendTimeSrch());
                criteria.lte(time2);
            }
        }else{
            if (StringUtils.isNotEmpty(form.getEndSendTimeSrch())) {
                Integer time2 = GetDate.strYYYYMMDDHHMMSS2Timestamp2(form.getEndSendTimeSrch());
                criteria.and("sendTime").lte(time2);
            }
        }
        criteria.and("msgDestinationType").equals(CustomConstants.MSG_PUSH_DESTINATION_TYPE_0);
        Query query = new Query(criteria);
        query.skip(offset).limit(limit);
        query.with(new Sort(Sort.Direction.DESC, "createTime"));
        return mongoTemplate.find(query,MessagePushMsg.class);
    }

    /**
     * 获取单条数据
     * @param request
     * @return
     */
    public MessagePushMsg getRecord(MessagePushNoticesRequest request){
        Criteria criteria = new Criteria();
        if (request.getNoticesSendStatusSrch() != null) {
            criteria.and("id").equals(request.getId());
        }
        Query query = new Query(criteria);
        return mongoTemplate.findOne(query,MessagePushMsg.class);
    }
    /**
     * 插入数据
     * @param request
     * @return
     */
    public void insertRecord(MessagePushNoticesRequest request){
        MessagePushMsg messagePushMsg = CommonUtils.convertBean(request, MessagePushMsg.class);
        //TODO 未获取用户名
       // AdminSystem users = (AdminSystem) session.getAttribute(CustomConstants.LOGIN_USER_INFO);
        messagePushMsg.setCreateTime(GetDate.getNowTime10());
       // messagePushMsg.setCreateUserName(users.getUsername());
        messagePushMsg.setLastupdateTime(GetDate.getNowTime10());
        //messagePushMsg.setLastupdateUserName(users.getUsername());
        mongoTemplate.save(messagePushMsg);
    }

    /**
     * 删除数据
     * @return
     */
    public void deleteRecord(Integer id){
        Query query = new Query();
        Criteria criteria = new Criteria();
        criteria.and("id").equals(id);
        query.addCriteria(criteria);
        this.del(query);
    }


    /**
     * 修改数据
     * @return
     */
    public void updateRecord(MessagePushMsg record){
        Query query = new Query();
        Criteria criteria = new Criteria();
        //criteria.and("date").is(hjhPlanCapital.getDate());
        //criteria.and("planNid").is(hjhPlanCapital.getPlanNid());
        Update update = new Update();
        update.set("lastupdateTime", GetDate.getNowTime10());
        //TODO 未获取用户名
        //update.set("lastupdateUserName", GetDate.getNowTime10());
        this.update(query, update);
    }

    @Override
    protected Class<MessagePushMsg> getEntityClass() {

        return MessagePushMsg.class;
    }
}