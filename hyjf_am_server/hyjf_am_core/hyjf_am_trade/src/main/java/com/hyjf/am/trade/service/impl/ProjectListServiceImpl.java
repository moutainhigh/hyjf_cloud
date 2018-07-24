/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import com.hyjf.am.trade.dao.model.customize.trade.*;
import com.hyjf.am.vo.trade.CreditListVO;
import com.hyjf.am.vo.trade.ProjectCustomeDetailVO;
import com.hyjf.am.vo.trade.WechatHomeProjectListVO;
import com.hyjf.am.vo.trade.hjh.HjhPlanVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyjf.am.resquest.trade.CreditListRequest;
import com.hyjf.am.resquest.trade.ProjectListRequest;
import com.hyjf.am.trade.dao.mapper.customize.trade.WebProjectListCustomizeMapper;
import com.hyjf.am.trade.service.ProjectListService;

/**
 * Web端项目列表Service实现类
 *
 * @author liuyang
 * @version ProjectListServiceImpl, v0.1 2018/6/13 11:38
 */
@Service
public class ProjectListServiceImpl implements ProjectListService {
    @Autowired
    private WebProjectListCustomizeMapper webProjectListCustomizeMapper;

    /**
     * 获取标的列表
     * @param request
     * @return
     */
    @Override
    public List<WebProjectListCustomize> searchProjectList(@Valid ProjectListRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        // 项目类型
        String projectType = request.getProjectType();
        // 项目子类型
        String borrowClass = request.getBorrowClass();
        // 分页起始
        Integer limitStart = request.getLimitStart();
        // 分页结束
        Integer limitEnd = request.getLimitEnd();
        params.put("projectType", projectType);
        params.put("borrowClass", borrowClass);
        params.put("limitStart", limitStart);
        params.put("limitEnd", limitEnd);

        return webProjectListCustomizeMapper.searchProjectList(params);
    }

    /**
     * 获取标的列表件数
     *
     * @param request
     * @return
     */
    @Override
    public int countProjectList(@Valid ProjectListRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        // 项目类型
        String projectType = request.getProjectType();
        // 项目子类型
        String borrowClass = request.getBorrowClass();
        // 分页起始
        Integer limitStart = request.getLimitStart();
        // 分页结束
        Integer limitEnd = request.getLimitEnd();
        params.put("projectType", projectType);
        params.put("borrowClass", borrowClass);
        params.put("limitStart",limitStart);
        params.put("limitEnd", limitEnd);
        return webProjectListCustomizeMapper.countProjectList(params);
    }


    /**
     * Web端获取标的详情
     * @author zhangyk
     * @date 2018/6/23 13:56
     */
    @Override
    public ProjectCustomeDetailVO getProjectDetail(@Valid Map map) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("borrowNid",map.get("borrowNid"));
        return  webProjectListCustomizeMapper.getProjectDetail(map);
    }


    /**
     * @desc  债转列表count
     * @author zhangyk
     * @date 2018/6/19 15:58
     */
    @Override
    public int countCreditList(@Valid CreditListRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        // 获取项目期限
        params.put("borrowPeriodMin", request.getBorrowPeriodMin());
        params.put("borrowPeriodMax", request.getBorrowPeriodMax());
        // 获取项目收益
        params.put("borrowAprMin", request.getBorrowAprMin());
        params.put("borrowAprMax", request.getBorrowAprMax());
        // 获取折价比例排序
        params.put("discountSort", request.getDiscountSort());
        // 获取期限排序
        params.put("termSort", request.getTermSort());
        // 获取金额排序
        params.put("capitalSort",request.getCapitalSort());
        // 进度排序
        params.put("inProgressSort",request.getInProgressSort());
        int count = webProjectListCustomizeMapper.countCreditList(params);
        return count;
    }


    /**
     * @desc  获取债转列表数据list
     * @author zhangyk
     * @date 2018/6/19 16:03
     */
    @Override
    public List<CreditListVO> searchCreditList(@Valid CreditListRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        // 获取项目期限
        params.put("borrowPeriodMin", request.getBorrowPeriodMin());
        params.put("borrowPeriodMax", request.getBorrowPeriodMax());
        // 获取项目收益
        params.put("borrowAprMin", request.getBorrowAprMin());
        params.put("borrowAprMax", request.getBorrowAprMax());
        // 获取折价比例排序
        params.put("discountSort", request.getDiscountSort());
        // 获取期限排序
        params.put("termSort", request.getTermSort());
        // 获取金额排序
        params.put("capitalSort",request.getCapitalSort());
        // 进度排序
        params.put("inProgressSort",request.getInProgressSort());
        params.put("limitStart",request.getLimitStart());
        params.put("limitEnd", request.getLimitEnd());
        List<CreditListVO> list = webProjectListCustomizeMapper.searchCreditList(params);
        return list;
    }


    /**
     * Web端获取计划专区上部统计数据
     * @author zhangyk
     * @date 2018/6/21 15:42
     */
    @Override
    public Map<String, Object> searchPlanData() {
        return webProjectListCustomizeMapper.searchPlanData();
    }

    /**
     * Web端获取计划专区计划列表count
     * @author zhangyk
     * @date 2018/6/21 15:51
     */
    @Override
    public int countWebPlanList(ProjectListRequest request) {
        Map<String,Object> params = new HashMap<>();
        params.put("limitStart",request.getLimitStart());
        params.put("limitEnd",request.getLimitEnd());
        return webProjectListCustomizeMapper.countWebPlanList(params);
    }


    /**
     * Web端获取计划专区计划列表list
     * @author zhangyk
     * @date 2018/6/21 15:53
     */
    @Override
    public List<HjhPlanCustomize> searchWebPlanList(ProjectListRequest request) {
        Map<String,Object> params = new HashMap<>();
        params.put("limitStart",request.getLimitStart());
        params.put("limitEnd",request.getLimitEnd());
        return webProjectListCustomizeMapper.searchWebPlanList(params);
    }

    /**
     * web端获取计划基本详情
     * @author zhangyk
     * @date 2018/7/14 18:08
     */
    @Override
    public PlanDetailCustomize getPlanDetail(String planNid){
        return webProjectListCustomizeMapper.getPlanDetail(planNid);
    }


    // ----------------------------------------web end ----------------------------------------------------
    // ----------------------------------------app start --------------------------------------------------

    /**
     * app端获取散标投资count
     * @author zhangyk
     * @date 2018/6/20 16:13
     */
    @Override
    public int countAppProjectList(@Valid ProjectListRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        // 项目类型
        String projectType = request.getProjectType();
        // 项目子类型
        String borrowClass = request.getBorrowClass();
        // 分页起始
        Integer limitStart = request.getLimitStart();
        // 分页结束
        Integer limitEnd = request.getLimitEnd();
        params.put("projectType", projectType);
        params.put("borrowClass", borrowClass);
        return webProjectListCustomizeMapper.countAppProject(params);
    }

    /**
     * app端获取散标投资数据list
     * @author zhangyk
     * @date 2018/6/20 16:11
     */
    @Override
    public List<AppProjectListCustomize> searchAppProjectList(@Valid ProjectListRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        // 项目类型
        String projectType = request.getProjectType();
        // 项目子类型
        String borrowClass = request.getBorrowClass();
        // 分页起始
        Integer limitStart = request.getLimitStart();
        // 分页结束
        Integer limitEnd = request.getLimitEnd();
        params.put("projectType", projectType);
        params.put("borrowClass", borrowClass);
        params.put("limitStart",limitStart);
        params.put("limitEnd", limitEnd);
        return webProjectListCustomizeMapper.searchAppProjectList(params);
    }

    @Override
    public int countAppCreditList(@Valid ProjectListRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        // 项目类型
        String projectType = request.getProjectType();
        // 项目子类型
        String borrowClass = request.getBorrowClass();
        params.put("projectType", projectType);
        params.put("borrowClass", borrowClass);
        params.put("creditStatus",request.getCreditStatus());
        return webProjectListCustomizeMapper.countAppCredit(params);
    }

    @Override
    public List<WebProjectListCustomize> searchAppCreditList(@Valid ProjectListRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        // 项目类型
        String projectType = request.getProjectType();
        // 项目子类型
        String borrowClass = request.getBorrowClass();
        // 分页起始
        Integer limitStart = request.getLimitStart();
        // 分页结束
        Integer limitEnd = request.getLimitEnd();
        params.put("projectType", projectType);
        params.put("borrowClass", borrowClass);
        params.put("limitStart",limitStart);
        params.put("limitEnd", limitEnd);
        params.put("creditStatus",request.getCreditStatus());
        return webProjectListCustomizeMapper.searchAppCreditList(params);
    }

    @Override
    public int countAppPlanList(@Valid ProjectListRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        // 项目类型
        String projectType = request.getProjectType();
        // 项目子类型
        String borrowClass = request.getBorrowClass();
        // 分页起始
        Integer limitStart = request.getLimitStart();
        // 分页结束
        Integer limitEnd = request.getLimitEnd();
        params.put("isHome",request.getIsHome());
        params.put("projectType", projectType);
        params.put("borrowClass", borrowClass);
        return webProjectListCustomizeMapper.countAppPlanList(params);
    }

    @Override
    public List<HjhPlanVO> searchAppPlanList(@Valid ProjectListRequest request) {
        Map<String, Object> params = new HashMap<String, Object>();
        // 项目类型
        String projectType = request.getProjectType();
        // 项目子类型
        String borrowClass = request.getBorrowClass();
        // 分页起始
        Integer limitStart = request.getLimitStart();
        // 分页结束
        Integer limitEnd = request.getLimitEnd();
        params.put("isHome",request.getIsHome());
        params.put("projectType", projectType);
        params.put("borrowClass", borrowClass);
        params.put("limitStart",limitStart);
        params.put("limitEnd", limitEnd);
        return webProjectListCustomizeMapper.searchAppPlanList(params);
    }




    // ----------------------------------------app end ----------------------------------------------------


    //  -----------------------------------------wechat start ----------------------------------------------


    /**
     * 查询微信端首页产品列表
     * @author zhangyk
     * @date 2018/7/24 13:45
     */
    @Override
    public List<WechatHomeProjectListVO> searchWechatProjectList(Map<String, Object> params) {
        return  webProjectListCustomizeMapper.searchWechatProjectList(params);
    }

    /**
     * 微信端加载两条计划稍后开启
     * @author zhangyk
     * @date 2018/7/24 14:30
     */
    @Override
    public List<WechatHomeProjectListVO> selectHomeHjhOpenLaterList() {
        return webProjectListCustomizeMapper.selectHomeHjhOpenLaterList();
    }

    /**
     * 首页无可投散标加载两条还款中和复审中记录
     * @author zhangyk
     * @date 2018/7/24 14:33
     */
    @Override
    public List<WechatHomeProjectListVO> selectHomeRepaymentsProjectList() {
        return webProjectListCustomizeMapper.selectHomeRepaymentsProjectList();
    }


    //  -----------------------------------------wechat end   -----------------------------------------------
}
