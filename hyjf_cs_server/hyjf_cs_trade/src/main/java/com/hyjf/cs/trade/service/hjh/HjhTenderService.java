package com.hyjf.cs.trade.service.hjh;

import com.hyjf.am.resquest.trade.TenderRequest;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.cs.trade.bean.TenderInfoResult;
import com.hyjf.cs.trade.service.BaseTradeService;

import java.util.Map;

/**
 * @Description 加入计划业务操作
 * @Author sunss
 * @Version v0.1
 * @Date 2018/6/19 9:46
 */
public interface HjhTenderService extends BaseTradeService {

    /**
     * @Description 检查加入计划的参数
     * @Author sunss
     * @Version v0.1
     * @Date 2018/6/19 9:47
     */
    public WebResult<Map<String, Object>> joinPlan(TenderRequest request);

    /**
     * 根据投资项目id历史回报
     * @param tender
     * @return
     */
    WebResult<TenderInfoResult> getInvestInfo(TenderRequest tender);
}