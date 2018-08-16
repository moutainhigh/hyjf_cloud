/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.front.borrow;

import com.hyjf.am.resquest.trade.CreditListRequest;
import com.hyjf.am.resquest.trade.ProjectListRequest;
import com.hyjf.am.trade.dao.model.auto.BorrowCredit;
import com.hyjf.am.trade.dao.model.customize.AppProjectInvestListCustomize;
import com.hyjf.am.trade.dao.model.customize.AppProjectListCustomize;
import com.hyjf.am.trade.dao.model.customize.AppTenderCreditInvestListCustomize;
import com.hyjf.am.trade.dao.model.customize.HjhPlanCustomize;
import com.hyjf.am.trade.dao.model.customize.PlanDetailCustomize;
import com.hyjf.am.trade.dao.model.customize.WebProjectListCustomize;
import com.hyjf.am.vo.trade.CreditListVO;
import com.hyjf.am.vo.trade.ProjectCustomeDetailVO;
import com.hyjf.am.vo.trade.WechatHomeProjectListVO;
import com.hyjf.am.vo.trade.hjh.HjhPlanVO;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;


/**
 * Web端项目列表Service
 * @author liuyang
 * @version ProjectListService, v0.1 2018/6/13 11:37
 */
public interface ProjectListService {


    /**
     * Web端获取项目列表
     * @param request
     * @return
     */
    List<WebProjectListCustomize> searchProjectList(@Valid ProjectListRequest request);

    /**
     * Web端获取项目列表件数
     * @param request
     * @return
     */
    int countProjectList(@Valid ProjectListRequest request);

    /**
     * Web端获取标的详情
     * @author zhangyk
     * @date 2018/6/23 13:55
     */
    ProjectCustomeDetailVO getProjectDetail(@Valid Map map);

    /**
     * Web端获取债转列表count
     * @param request
     * @return
     */
    int countCreditList(@Valid CreditListRequest request);

    /**
     * Web端获取债转列表list
     * @author zhangyk
     * @date 2018/6/19 16:00
     */
    List<CreditListVO> searchCreditList(@Valid CreditListRequest request);

    /**
     * Web端获取计划专区上部统计数据
     * @author zhangyk
     * @date 2018/6/21 15:42
     */
    Map<String,Object>  searchPlanData();

    /**
     * Web端获取计划专区计划列表count
     * @author zhangyk
     * @date 2018/6/21 15:51
     */
    int countWebPlanList(ProjectListRequest request);

    /**
     * Web端获取计划专区计划列表list
     * @author zhangyk
     * @date 2018/6/21 15:51
     */
    List<HjhPlanCustomize> searchWebPlanList(ProjectListRequest request);


    /**
     * web端获取计划基本详情
     * @author zhangyk
     * @date 2018/7/14 18:08
     */
    PlanDetailCustomize getPlanDetail(String planNid);



    // --------------------------web end --------------------------------------------------
    //---------------------------app start ------------------------------------------------
    /**
     * app端获取散标投资count
     * @author zhangyk
     * @date 2018/6/20 16:11
     */
     int  countAppProjectList(@Valid ProjectListRequest request);

    /**
     * app端获取散标投资数据list
     * @author zhangyk
     * @date 2018/6/20 16:11
     */
    List<AppProjectListCustomize> searchAppProjectList(@Valid ProjectListRequest request);

    /**
     * app端获取散标投资count
     * @author zhangyk
     * @date 2018/6/20 16:11
     */
    int  countAppCreditList(@Valid ProjectListRequest request);

    /**
     * app端获取散标投资数据list
     * @author zhangyk
     * @date 2018/6/20 16:11
     */
    List<WebProjectListCustomize> searchAppCreditList(@Valid ProjectListRequest request);

    /**
     * app获取计划列表count
     * @author zhangyk
     * @date 2018/6/22 10:27
     */
    int countAppPlanList(@Valid ProjectListRequest request);

    /**
     * app获取计划列表list
     * @author zhangyk
     * @date 2018/6/22 10:27
     */
    List<HjhPlanVO>  searchAppPlanList(@Valid ProjectListRequest request);



    /*
     * 散表投资记录数
     */
    int countProjectInvestRecordTotal(Map<String,Object> params);

    /**
     * 获取散标投资记录
     * @param params
     * @return
     */
    List<AppProjectInvestListCustomize> selectProjectInvestList(Map<String,Object> params);

    /**
     * app端债转承接记录数
     * @param params
     * @return
     */
    int countTenderCreditInvestRecordTotal(Map<String,Object> params);

    /**
     * 债转承接记录列表
     * @param params
     * @return
     */
    List<AppTenderCreditInvestListCustomize> searchTenderCreditInvestList(Map<String,Object> params);

    List<BorrowCredit> selectBorrowCreditByNid(String transferId);

    // --------------------------app end --------------------------------------------------

    // -----------------------------wechat  start ------------------------------------------
    /**
     * 查询微信端首页产品列表
     * @author zhangyk
     * @date 2018/7/24 13:45
     */
    List<WechatHomeProjectListVO> searchWechatProjectList(Map<String,Object> params);

    /**
     * 微信端加载两条计划稍后开启
     * @author zhangyk
     * @date 2018/7/24 14:30
     */
    List<WechatHomeProjectListVO> selectHomeHjhOpenLaterList();

    /**
     * 首页无可投散标加载两条还款中和复审中记录
     * @author zhangyk
     * @date 2018/7/24 14:33
     */
    List<WechatHomeProjectListVO> selectHomeRepaymentsProjectList();


    // -----------------------------wechat end  ---------------------------------------------
}