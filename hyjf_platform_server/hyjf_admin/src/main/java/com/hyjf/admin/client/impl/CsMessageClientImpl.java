/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.client.impl;

import com.hyjf.admin.beans.request.SmsLogRequestBean;
import com.hyjf.admin.client.CsMessageClient;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.*;
import com.hyjf.am.response.message.OperationReportResponse;
import com.hyjf.am.resquest.admin.AssociatedRecordListRequest;
import com.hyjf.am.resquest.admin.HjhPlanCapitalRequest;
import com.hyjf.am.resquest.config.MessagePushErrorRequest;
import com.hyjf.am.resquest.config.MessagePushPlatStaticsRequest;
import com.hyjf.am.resquest.message.MessagePushMsgRequest;
import com.hyjf.am.resquest.message.MessagePushTemplateStaticsRequest;
import com.hyjf.am.resquest.message.OperationReportRequest;
import com.hyjf.am.resquest.message.SmsLogRequest;
import com.hyjf.am.vo.admin.*;
import com.hyjf.am.vo.datacollect.AccountWebListVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * @author zhangqingqing
 * @version AmDataCollect, v0.1 2018/6/25 10:26
 *
 * todo 这个类全部错了，都要改 ！！！！！！！！！！！！！！！！！！！！！！！！
 */
@Service
public class  CsMessageClientImpl  implements CsMessageClient {
    @Autowired
    private RestTemplate restTemplate;


    @Override
    public AccountWebListResponse queryAccountWebList(AccountWebListVO accountWebList) {
        AccountWebListResponse response = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/search/queryAccountWebList", accountWebList, AccountWebListResponse.class)
                .getBody();
        if (response != null) {
            return response;
        }
        return null;
    }

    @Override
    public String selectBorrowInvestAccount(AccountWebListVO accountWebList) {
        String response = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/search/selectBorrowInvestAccount", accountWebList, String.class)
                .getBody();
        if (response != null) {
            return response;
        }
        return null;
    }


    /**
     * 查询关联记录列表count
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public Integer getAssociatedRecordsCount(AssociatedRecordListRequest request) {
        Integer count = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/search/getassociatedrecordscount", request, Integer.class)
                .getBody();

        return count;
    }

    /**
     * 根据筛选条件查询关联记录list
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public List<AssociatedRecordListVo> getAssociatedRecordList(AssociatedRecordListRequest request) {
        AssociatedRecordListResponse response = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/search/searchassociatedrecordlist", request, AssociatedRecordListResponse.class)
                .getBody();
        if(Response.isSuccess(response)){
            return response.getResultList();
        }
        return null;
    }

    @Override
    public String getRetCode(String logOrdId) {
        String response = restTemplate.getForEntity("http://CS-MESSAGE/cs-message/search/getRetCode/" + logOrdId,String.class).getBody();
        return response;
    }

    @Override
    public OperationReportResponse getRecordList(OperationReportRequest request){
        OperationReportResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/operation_report/list",request,OperationReportResponse.class).getBody();
        return response;
    }
    @Override
    public OperationReportResponse listByRelease(OperationReportRequest request){
        OperationReportResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/operation_report/listbyrelease",request,OperationReportResponse.class).getBody();
        return response;
    }
    @Override
    public OperationReportResponse selectOperationreportCommon(String id){
        OperationReportResponse response = restTemplate.getForObject("http://CS-MESSAGE/cs-message/operation_report/selectdetail/"+id,OperationReportResponse.class);
        return response;
    }
    @Override
    public OperationReportResponse reportInfo(String id){
        OperationReportResponse response = restTemplate.getForObject("http://CS-MESSAGE/cs-message/operation_report/reportinfo/"+id,OperationReportResponse.class);
        return response;
    }
    @Override
    public OperationReportResponse delete(String id){
        OperationReportResponse response = restTemplate.getForObject("http://CS-MESSAGE/cs-message/operation_report/delete/"+id,OperationReportResponse.class);
        return response;
    }
    @Override
    public OperationReportResponse publish(OperationReportRequest request){
        OperationReportResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/operation_report/publish",request,OperationReportResponse.class).getBody();
        return response;
    }
    @Override
    public OperationReportResponse insertOrUpdateMonthAction(OperationReportRequest request){
        OperationReportResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/operation_report/insertmonthaction",request,OperationReportResponse.class).getBody();
        return response;
    }
    @Override
    public OperationReportResponse insertOrUpdateQuarterAction(OperationReportRequest request) {
        OperationReportResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/operation_report/insertquarteraction",request,OperationReportResponse.class).getBody();
        return response;
    }
    @Override
    public OperationReportResponse insertOrUpdateHalfYearAction(OperationReportRequest request) {
        OperationReportResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/operation_report/inserthalfyearaction",request,OperationReportResponse.class).getBody();
        return response;
    }
    @Override
    public OperationReportResponse insertOrUpdateYearAction(OperationReportRequest request){
        OperationReportResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/operation_report/insertyearaction",request,OperationReportResponse.class).getBody();
        return response;
    }
    @Override
    public OperationReportResponse monthPreview(OperationReportRequest request){
        OperationReportResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/operation_report/monthpreview",request,OperationReportResponse.class).getBody();
        return response;
    }

    @Override
    public OperationReportResponse yearPreview(OperationReportRequest request){
        OperationReportResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/operation_report/yearpreview",request,OperationReportResponse.class).getBody();
        return response;
    }
    @Override
    public OperationReportResponse quarterPreview(OperationReportRequest request){
        OperationReportResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/operation_report/quarterpreview",request,OperationReportResponse.class).getBody();
        return response;
    }
    @Override
    public OperationReportResponse halfPreview(OperationReportRequest request){
        OperationReportResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/operation_report/halfpreview",request,OperationReportResponse.class).getBody();
        return response;
    }

    @Override
    public SmsLogResponse smsLogList() {
        return restTemplate.getForEntity("http://CS-MESSAGE/cs-message/sms_log/list", SmsLogResponse.class).getBody();
    }

    @Override
    public SmsLogResponse findSmsLog(SmsLogRequest request) {
        return restTemplate.postForEntity("http://CS-MESSAGE/cs-message/sms_log/find", request, SmsLogResponse.class)
                .getBody();
    }

    /**
     * 获取汇计划--计划资金列表(从MongoDB读取数据)
     * @param hjhPlanCapitalRequest
     * @return
     * @Author : huanghui
     */
    @Override
    public HjhPlanCapitalResponse getPlanCapitalList(HjhPlanCapitalRequest hjhPlanCapitalRequest) {
        HjhPlanCapitalResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/hjh_plan_capital/getPlanCapitalList",
                hjhPlanCapitalRequest, HjhPlanCapitalResponse.class).getBody();
        if (response != null){
            return response;
        }
        return null;
    }

	@Override
	public MessagePushTemplateStaticsResponse selectTemplateStatics(MessagePushTemplateStaticsRequest request) {
		MessagePushTemplateStaticsResponse response = restTemplate
				.postForEntity("http://CS-MESSAGE/cs-message/messagepush_template_statics/select_template_statics",
						request, MessagePushTemplateStaticsResponse.class)
				.getBody();
		if (response != null) {
			return response;
		}
		return null;
	}

    @Override
    public MessagePushPlatStaticsResponse selectPushPlatTemplateStatics(MessagePushPlatStaticsRequest request) {
        MessagePushPlatStaticsResponse response = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/messageplat_statics/select_plat_statics",
                        request, MessagePushPlatStaticsResponse.class)
                .getBody();
        if (response != null) {
            return response;
        }
        return null;
    }

    /**
     * 获取列表记录数
     *
     * @return
     */
    @Override
    public Integer getRecordCount(MessagePushErrorRequest request) {
        MessagePushHistoryResponse response = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/msgpush/error/getRecordCount",
                        request, MessagePushHistoryResponse.class).getBody();
        if (response != null) {
            return response.getRecordTotal();
        }
        return null;
    }

    /**
     * (条件)查询 APP消息推送 异常处理 列表
     *
     * @return
     */
    @Override
    public List<MessagePushMsgHistoryVO> getRecordListT(MessagePushErrorRequest request, int limitStart, int limitEnd) {
        request.setLimitStart(limitStart);
        request.setLimitEnd(limitEnd);
        MessagePushHistoryResponse response = restTemplate
                .postForObject("http://CS-MESSAGE/cs-message/msgpush/error/getRecordListT/", request,
                        MessagePushHistoryResponse.class);
        if (response != null) {
            return response.getResultList();
        }
        return null;
    }

    /**
     * 数据修改 APP消息推送 异常处理
     * @param request
     * @return
     */
    @Override
    public MessagePushErrorResponse update(MessagePushErrorRequest request) {
        MessagePushErrorResponse response = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/msgpush/error/request",
                        request, MessagePushErrorResponse.class).getBody();
        if (response != null) {
            return response;
        }
        return null;
    }

    /**
     * 获取标签列表
     *
     * @return
     */
    @Override
    public List<MessagePushTagVO> getTagList() {
        MessagePushTagResponse response = restTemplate
                .getForObject("http://CS-MESSAGE/cs-message/msgpush/error/getTagList",
                        MessagePushTagResponse.class);
        if (response != null) {
            return response.getResultList();
        }
        return null;
    }

    /**
     * 获取单个信息
     *
     * @return
     */
    @Override
    public MessagePushMsgHistoryVO getRecord(String id) {
        MessagePushHistoryResponse response = restTemplate
                .getForObject("http://CS-MESSAGE/cs-message/msgpush/error/getRecord/" + id,
                        MessagePushHistoryResponse.class);
        if (response != null) {
            return response.getResult();
        }
        return null;
    }

    /**
     * 推送极光消息
     * @param msg
     * @return 成功返回消息id  失败返回 error
     * @author Michael
     */
    @Override
    public void sendMessage(MessagePushMsgHistoryVO msg) {
        restTemplate.postForObject("http://CS-MESSAGE/cs-message/msgpush/error/sendMessage/", msg,
                Response.class);
    }

    /**
     * 获取手动发放短信列表
     * @param request
     * @return
     */
    @Override
    public MessagePushMsgResponse selectMessagePushMsg(MessagePushMsgRequest request) {
        MessagePushMsgResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/app_message/selectmessagepushmsg",
                request,MessagePushMsgResponse.class).getBody();
        if (response != null) {
            return response;
        }
        return null;
    }

    @Override
    public MessagePushMsgResponse getMessagePushMsgById(String id) {
        MessagePushMsgResponse response = restTemplate.getForEntity("http://CS-MESSAGE/cs-message/app_message/getmessagepushmsgbyid/"+id,MessagePushMsgResponse.class).getBody();
        if (response != null) {
            return response;
        }
        return null;
    }

    @Override
    public MessagePushMsgResponse insertMessagePushMsg(MessagePushMsgVO templateVO) {
        MessagePushMsgResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/app_message/insertmessagepushmsg",
                templateVO,MessagePushMsgResponse.class).getBody();
        if (response != null) {
            return response;
        }
        return null;
    }

    @Override
    public MessagePushMsgResponse updateMessagePushMsg(MessagePushMsgRequest templateRequest) {
        MessagePushMsgResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/app_message/updatemessagepushmsg",
                templateRequest,MessagePushMsgResponse.class).getBody();
        if (response != null) {
            return response;
        }
        return null;
    }

    @Override
    public MessagePushMsgResponse deleteMessagePushMsg(MessagePushMsgRequest request) {
        MessagePushMsgResponse response = restTemplate.postForEntity("http://CS-MESSAGE/cs-message/app_message/deletemessagepushmsg",request,
                MessagePushMsgResponse.class).getBody();
        if (response != null) {
            return response;
        }
        return null;
    }

    @Override
    public SmsOntimeResponse queryTime(SmsLogRequest request) {
        SmsOntimeResponse response = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/sms_log/query_time",
                        request, SmsOntimeResponse.class)
                .getBody();
        if (response != null) {
            return response;
        }
        return null;
    }

    @Override
    public Integer queryLogCount(SmsLogRequestBean requestBean) {
        SmsLogResponse response = restTemplate
                .postForEntity("http://CS-MESSAGE/cs-message/sms_log/query_log_count",
                        requestBean, SmsLogResponse.class)
                .getBody();
        if (response != null) {
            return response.getLogCount();
        }
        return 0;
    }
}
