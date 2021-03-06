package com.hyjf.cs.trade.service.myproject;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.resquest.trade.AssetManageBeanRequest;
import com.hyjf.am.vo.trade.assetmanage.AppAlreadyRepayListCustomizeVO;
import com.hyjf.am.vo.trade.assetmanage.AppTenderCreditRecordListCustomizeVO;
import com.hyjf.am.vo.trade.assetmanage.CurrentHoldObligatoryRightListCustomizeVO;
import com.hyjf.cs.common.bean.result.AppResult;
import com.hyjf.cs.trade.bean.TenderBorrowCreditCustomize;
import com.hyjf.cs.trade.service.BaseTradeService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author pangchengchao
 * @version AssetManageService, v0.1 2018/6/20 17:31
 */
public interface AppMyProjectService extends BaseTradeService {

    boolean isAllowChannelAttorn(Integer userId);

    int selectCurrentHoldObligatoryRightListTotal(AssetManageBeanRequest params);

    List<CurrentHoldObligatoryRightListCustomizeVO> selectAppCurrentHoldObligatoryRightList(AssetManageBeanRequest params);

    int countAlreadyRepayListRecordTotal(AssetManageBeanRequest params);

    List<AppAlreadyRepayListCustomizeVO> selectAppAlreadyRepayList(AssetManageBeanRequest params);

    int countCreditRecord(AssetManageBeanRequest params);

    List<AppTenderCreditRecordListCustomizeVO> searchAppCreditRecordList(AssetManageBeanRequest params);

    Integer selectTenderToCreditListCount(AssetManageBeanRequest params);

    JSONObject getMyProjectDetail(String borrowNid, HttpServletRequest request, String userId) ;
    
    /**
     * @author libin
     * App端:发送短信验证码(ajax请求)短信验证码数据保存(取自web)
     */
    AppResult sendCreditCode(HttpServletRequest request, Integer userId);
    
	/**
     * 债转提交保存
     * @author libin
     * @param request
     * @param userId
     * @return
     */
    JSONObject saveTenderToCredit(TenderBorrowCreditCustomize request, Integer userId);


    /**
     * 我的债转详情
     * @author zhangyk
     * @date 2018/8/30 13:54
     */
    JSONObject getMyCreditDetail(String transfId, HttpServletRequest request, Integer userId);

    /**
     * 已持有债权列表去转让接口
     * @author zhangyk
     * @date 2018/9/12 13:55
     */
    JSONObject tenderToCreditDetail(HttpServletRequest request , Integer userId);
}
