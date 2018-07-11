package com.hyjf.cs.trade.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.resquest.market.AdsRequest;
import com.hyjf.am.resquest.trade.AppProjectListRequest;
import com.hyjf.am.resquest.trade.HjhPlanRequest;
import com.hyjf.am.vo.market.AppAdsCustomizeVO;
import com.hyjf.am.vo.trade.AppProjectListCustomizeVO;
import com.hyjf.am.vo.trade.account.AccountVO;
import com.hyjf.am.vo.trade.hjh.HjhPlanCustomizeVO;
import com.hyjf.am.vo.trade.hjh.PlanDetailCustomizeVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.cs.trade.bean.AppHomePageCustomize;
import com.hyjf.cs.trade.bean.AppModuleBean;
import com.hyjf.cs.trade.client.*;
import com.hyjf.cs.trade.config.SystemConfig;
import com.hyjf.cs.trade.service.AppHomeService;
import com.hyjf.cs.trade.util.AppHomePageDefine;
import com.hyjf.cs.trade.util.ProjectConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.tools.ant.Project;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * APP首页service
 *
 * @author zhangyk
 * @date 2018/7/5 13:45
 */
@Service
public class AppHomeServiceImpl implements AppHomeService {


    private static DecimalFormat DF_FOR_VIEW = new DecimalFormat("#,##0.00");

    @Autowired
    private AmUserClient amUserClient;

    @Autowired
    private AmAdsClient amAdsClient;

    @Autowired
    private AmBorrowClient amBorrowClient;

    @Autowired
    private AccountClient accountClient;

    @Autowired
    private CouponUserClient couponUserClient;

    @Autowired
    private WebProjectListClient webProjectListClient;

    @Autowired
    private AmHjhPlanClient amHjhPlanClient;

    @Autowired
    private SystemConfig systemConfig;

    /**
     * 获取app首页各项数据
     *
     * @author zhangyk
     * @date 2018/7/5 14:24
     */
    @Override
    public JSONObject getAppHomeData(HttpServletRequest request, String userId) {
        JSONObject info = new JSONObject();
        String platform = request.getParameter("realPlatform");
        String version = request.getParameter("version");
        String uniqueIdentifier = request.getParameter("UniqueIdentifier");
        // 是否需要查询新手标的标志
        Boolean getNewProjectFlag = false;


        if (StringUtils.isBlank(platform)) {
            platform = request.getParameter("platform");
        }
        String HOST = "";
        //新旧版本HOST获取
        if ("3.0.5".compareTo(version) <= 0) {
            HOST = systemConfig.webHost;
        } else {
            HOST = systemConfig.httpWebHost;
        }
        //首页展示的项目集合
        List<AppProjectListCustomizeVO> list = new ArrayList<>();

        info.put("warning", "市场有风险 投资需谨慎");
        //判断用户是否登录
        Boolean loginFlag = Boolean.FALSE;
        UserVO userVO = null;
        if (StringUtils.isNotBlank(userId)) {
            userVO = amUserClient.findUserById(Integer.valueOf(userId));
            if (userVO != null) {
                loginFlag = Boolean.TRUE;
            }
        }
        if (!loginFlag) {
            String type = "0";  //未注册
            info.put("userType", "0");
            info.put("totalAssets", "0.00");
            info.put("availableBalance", "0.00");
            info.put("accumulatedEarnings", "0.00");
            info.put("coupons", "0");

            //创建首页广告
            createAdPic(info, platform, type, HOST);
            info.put("adDesc", "立即注册");
            //新手标标志
            getNewProjectFlag = Boolean.TRUE;

            //获取首页项目列表
            /*this.createProjectListPage(info, version, list, HOST);*/
            this.createHjhExtensionProjectListPage(info, list, HOST);
        } else {

            //查询用户是否开户
            Integer userType = userVO.getUserType();
            if (userType == 0) {//未开户
                info.put("userType", "1");
                info.put("totalAssets", "0.00");
                info.put("availableBalance", "0.00");
                info.put("accumulatedEarnings", "0.00");
                info.put("coupons", "0");
                String type = "1";//未开户
                createAdPic(info, platform, type, HOST);
                info.put("adDesc", "立即开户");
                //获取新手标
                getNewProjectFlag = Boolean.TRUE;

                //获取首页项目列表
                this.createHjhExtensionProjectListPage(info, list, HOST);
            } else if (userType == 1) {//已开户
                info.put("userType", "2");

                //获取用户累计投资条数
                Integer count = amBorrowClient.getTotalInverestCount(userId);
                if (count <= 0 || count == null) {
                    //获取新手标
                    getNewProjectFlag = Boolean.TRUE;
                }

                //获取首页项目列表
                this.createHjhExtensionProjectListPage(info, list, HOST);
                //获取用户资产总额
                AccountVO accountVO = accountClient.getAccountByUserId(Integer.valueOf(userId));
                info.put("totalAssets", accountVO != null ? DF_FOR_VIEW.format(accountVO.getBankTotal()) : "0.00");
                //获取可用余额
                info.put("availableBalance", accountVO != null ? DF_FOR_VIEW.format(accountVO.getBankBalance()) : "0.00");
                //获取累计收益
                info.put("accumulatedEarnings", accountVO != null ? DF_FOR_VIEW.format(accountVO.getBankInterestSum()) : "0.00");
                //获取(未使用的)优惠券数量
                int couponCount = couponUserClient.getUserCouponCount(Integer.valueOf(userId), "0");
                info.put("coupons", couponCount);
                info.put("adPicUrl", "");
                info.put("adClickPicUrl", "");
            }
        }

        if (getNewProjectFlag) {
            this.createProjectNewPage(info, list, HOST);
        }
        //获取累计投资金额
        //info.put("totalInvestmentAmount", DF_FOR_VIEW.format(homePageService.selectTotalInvest())); // TODO: 2018/7/5 调用mongo
        info.put("totalInvestmentAmount","0.00");
        info.put("moduleTotal", "4");
        List<AppModuleBean> moduleList = new ArrayList<>();

        //platform 2.安卓 3.ios

        //添加首页module
        //资金存管
        createModule(moduleList, platform, "android_module1", "ios_module1", HOST);
        //美国上市
        createModule(moduleList, platform, "android_module2", "ios_module2", HOST);
        //运营数据
        createModule(moduleList, platform, "android_module3", "ios_module3", HOST);
        //关于我们
        createModule(moduleList, platform, "android_module4", "ios_module4", HOST);

        info.put("moduleList", moduleList);

        //添加顶部活动图片总数和顶部活动图片数组
        this.createBannerPage(info, platform, request, HOST);
        this.createBannerlittlePage(info,getNewProjectFlag);
        this.createPopImgPage(info, uniqueIdentifier);
        this.createForceUpdateInfo(info, platform, version, HOST);

        info.put(CustomConstants.APP_STATUS, CustomConstants.APP_STATUS_SUCCESS);
        info.put(CustomConstants.APP_STATUS_DESC, CustomConstants.APP_STATUS_DESC_SUCCESS);
        info.put(CustomConstants.APP_REQUEST,
                ProjectConstant.REQUEST_HOME + AppHomePageDefine.REQUEST_MAPPING + AppHomePageDefine.PROJECT_LIST_ACTION);
        CommonUtils.convertNullToEmptyString(info);
        return info;
    }




    /**
     * 查询首页项目列表
     * @param info
     */
    private void createHjhExtensionProjectListPage(JSONObject info,List<AppProjectListCustomizeVO> appProjectListCustomizeList, String HOST) {

        List<AppHomePageCustomize> homePageCustomizes = convertToAppHomePageCustomize(appProjectListCustomizeList,HOST);//新手标
        List<AppHomePageCustomize> homeHjhPageCustomizes=convertToAppHomePageHJHCustomize(this.createHjhExtensionListInfoPage(HOST),HOST);//计划
        if (appProjectListCustomizeList.size() == 0){
            homePageCustomizes.addAll(homeHjhPageCustomizes);
        }else {
            if(homeHjhPageCustomizes!=null&&homeHjhPageCustomizes.size()==3){
                homePageCustomizes.add(homeHjhPageCustomizes.get(0));
                homePageCustomizes.add(homeHjhPageCustomizes.get(1));
            } else {
                homePageCustomizes.addAll(homeHjhPageCustomizes);
            }

        }
        CommonUtils.convertNullToEmptyString(homePageCustomizes);
        info.put("projectTotal",homePageCustomizes.size());
        info.put("projectList", homePageCustomizes);
    }

    /**
     * 首页汇计划列表
     */
    private List<AppProjectListCustomizeVO> createHjhExtensionListInfoPage(String HOST) {

        // 构造分页信息 首页只取前三条
        HjhPlanRequest request = new HjhPlanRequest();
        request.setLimitStart(0);
        request.setLimitEnd(3);
        List<HjhPlanCustomizeVO> planList = amHjhPlanClient.getAppHomePlanList(request);
        boolean hasInvestment = false;
        //判断是否有可投资计划
        for (HjhPlanCustomizeVO hjhPlanCustomize:planList){
            if("立即加入".equals(hjhPlanCustomize.getStatusName())){
                hasInvestment = true;
                break;
            }

        }
        //无可投计划
        if(!hasInvestment){
            request.setLockFlag("1");
            planList =  amHjhPlanClient.getAppHomePlanList(request);
        }
        List<AppProjectListCustomizeVO> projectListCustomizes = convertToAppProjectList(planList,HOST);
        return projectListCustomizes;
    }


    /**
     * 适应客户端返回数据格式
     * @param planList
     * @return
     */
    private List<AppProjectListCustomizeVO> convertToAppProjectList(List<HjhPlanCustomizeVO> planList, String HOST) {
        List<AppProjectListCustomizeVO> appProjectList = null;
        String url = "";
        AppProjectListCustomizeVO appProjectListCustomize;
        if (!CollectionUtils.isEmpty(planList)) {
            appProjectList = new ArrayList<AppProjectListCustomizeVO>();
            for (HjhPlanCustomizeVO entity : planList) {
                appProjectListCustomize = new AppProjectListCustomizeVO();
                appProjectListCustomize.setStatus(entity.getStatus());
                appProjectListCustomize.setBorrowName(entity.getPlanName());
                appProjectListCustomize.setPlanApr(entity.getPlanApr());
                appProjectListCustomize.setBorrowApr(entity.getPlanApr());
                appProjectListCustomize.setPlanPeriod(entity.getPlanPeriod());
                appProjectListCustomize.setBorrowPeriod(entity.getPlanPeriod());
                appProjectListCustomize.setAvailableInvestAccount(entity.getAvailableInvestAccount());
                appProjectListCustomize.setStatusName(entity.getStatusName());
                appProjectListCustomize.setBorrowNid(entity.getPlanNid());
                String couponEnable = entity.getCouponEnable();
                if (org.apache.commons.lang.StringUtils.isEmpty(couponEnable) || "0".equals(couponEnable)) {
                    couponEnable = "0";
                } else {
                    couponEnable = "1";
                }
                appProjectListCustomize.setCouponEnable(couponEnable);
                // 项目详情url
                url = HOST + ProjectConstant.REQUEST_HOME + AppHomePageDefine.PLAN
                        + AppHomePageDefine.HJH_PLAN_DETAIL_ACTION + "?planNid='" + entity.getPlanNid() + "'";
                appProjectListCustomize.setBorrowUrl(url);
                appProjectListCustomize.setProjectType("HJH");
                appProjectListCustomize.setBorrowType("HJH");

                // 应客户端要求，返回空串
                CommonUtils.convertNullToEmptyString(appProjectListCustomize);
                appProjectList.add(appProjectListCustomize);
            }
        }
        return appProjectList;
    }


    /**
     * 适应客户端数据返回HJH
     * @param list
     * @return
     */
    private List<AppHomePageCustomize> convertToAppHomePageHJHCustomize(List<AppProjectListCustomizeVO> list, String HOST) {
        List<AppHomePageCustomize> homePageCustomizes = new ArrayList<>();
        for (AppProjectListCustomizeVO listCustomize : list) {
            AppHomePageCustomize homePageCustomize = new AppHomePageCustomize();
            homePageCustomize.setBorrowNid(listCustomize.getBorrowNid());
            homePageCustomize.setBorrowName( listCustomize.getBorrowName());
            homePageCustomize.setBorrowDesc("计划");
            homePageCustomize.setBorrowType(listCustomize.getBorrowType());
            homePageCustomize.setBorrowTheFirst(listCustomize.getBorrowApr() + "%");
            homePageCustomize.setBorrowTheFirstDesc("历史年回报率");
            homePageCustomize.setBorrowTheSecond(listCustomize.getBorrowPeriod());
            homePageCustomize.setBorrowTheSecondDesc("锁定期限");

            PlanDetailCustomizeVO planDetailCustomizeVO = amHjhPlanClient.getPlanDetailByPlanNid(listCustomize.getBorrowNid());
            String statusNameDesc = planDetailCustomizeVO != null ? planDetailCustomizeVO.getAvailableInvestAccount() : "0.00";
            if(StringUtils.isNotBlank(statusNameDesc)){
                BigDecimal openAmount = new BigDecimal(statusNameDesc);
                if(openAmount.compareTo(BigDecimal.ZERO) != 0){
                    homePageCustomize.setStatusNameDesc("额度" + DF_FOR_VIEW.format(openAmount));
                } else {
                    homePageCustomize.setStatusNameDesc("");
                }
            }

            homePageCustomize.setBorrowUrl(HOST + AppHomePageDefine.PLAN + listCustomize.getBorrowNid());
            homePageCustomize.setBorrowApr(listCustomize.getBorrowApr() + "%");
            homePageCustomize.setBorrowPeriod(listCustomize.getBorrowPeriod());
            String status = listCustomize.getStatus();
            if (listCustomize.getStatusName().equals("稍后开启")){    //1.启用  2.关闭
                // 20.立即加入  21.稍后开启
                homePageCustomize.setStatus("21");
                homePageCustomize.setStatusName("稍后开启");
            }else if(listCustomize.getStatusName().equals("立即加入")){  //1.启用  2.关闭
                homePageCustomize.setStatus("20");
                homePageCustomize.setStatusName("立即加入");
            }
            homePageCustomize.setOnTime(listCustomize.getOnTime());
            homePageCustomize.setBorrowSchedule(listCustomize.getBorrowSchedule());
            homePageCustomize.setBorrowTotalMoney(StringUtils.isBlank(listCustomize.getBorrowTotalMoney())?"0":listCustomize.getBorrowTotalMoney());
            homePageCustomizes.add(homePageCustomize);
        }
        CommonUtils.convertNullToEmptyString(homePageCustomizes);
        return homePageCustomizes;
    }



    /**
     * 适应客户端数据返回
     * @param list
     * @return
     */
    private List<AppHomePageCustomize> convertToAppHomePageCustomize(List<AppProjectListCustomizeVO> list, String HOST) {
        List<AppHomePageCustomize> homePageCustomizes = new ArrayList<>();
        for (AppProjectListCustomizeVO listCustomize : list) {
            AppHomePageCustomize homePageCustomize = new AppHomePageCustomize();
            homePageCustomize.setBorrowNid(listCustomize.getBorrowNid());
            if (listCustomize.getBorrowNid().startsWith("NEW")) {
                homePageCustomize.setBorrowName( listCustomize.getBorrowNid());
                homePageCustomize.setBorrowDesc("新手");
            } else {
                homePageCustomize.setBorrowName(listCustomize.getBorrowNid());
                homePageCustomize.setBorrowDesc("散标");
            }
            homePageCustomize.setBorrowType(listCustomize.getBorrowType());
            homePageCustomize.setBorrowTheFirst(listCustomize.getBorrowApr() + "%");
            homePageCustomize.setBorrowTheFirstDesc("历史年回报率");
            homePageCustomize.setBorrowTheSecond(listCustomize.getBorrowPeriod());
            homePageCustomize.setBorrowTheSecondDesc("项目期限");

            //定时开标
            String onTime = listCustomize.getOnTime();
            if(!("0".equals(onTime)||"".equals(onTime))){
                homePageCustomize.setStatusName(onTime);
            } else {
                homePageCustomize.setStatusName("立即投资");
            }

            PlanDetailCustomizeVO planDetailCustomizeVO = amHjhPlanClient.getPlanDetailByPlanNid(listCustomize.getBorrowNid());
            String statusNameDesc = planDetailCustomizeVO != null ? planDetailCustomizeVO.getAvailableInvestAccount() : "0.00";
            homePageCustomize.setStatusNameDesc("剩余" + DF_FOR_VIEW.format(new BigDecimal(statusNameDesc)));
            homePageCustomize.setBorrowUrl(HOST + AppHomePageDefine.BORROW + listCustomize.getBorrowNid());
            homePageCustomize.setBorrowApr(listCustomize.getBorrowApr() + "%");
            homePageCustomize.setBorrowPeriod(listCustomize.getBorrowPeriod());
            homePageCustomize.setStatus(listCustomize.getStatus());
            homePageCustomize.setOnTime(listCustomize.getOnTime());
            homePageCustomize.setBorrowSchedule(listCustomize.getBorrowSchedule());
            homePageCustomize.setBorrowTotalMoney(StringUtils.isBlank(listCustomize.getBorrowTotalMoney())?"0":listCustomize.getBorrowTotalMoney());
            homePageCustomizes.add(homePageCustomize);
        }
        return homePageCustomizes;
    }


    /**
     *
     * 获取IOS强制更新
     * @author hsy
     * @param info
     */
    private void createForceUpdateInfo(JSONObject info, String platform, String version, String HOST){
        // 从配置文件中加载配置信息
        String noticeStatus = systemConfig.noticeStatus;
        String noticeUrlIOS = systemConfig.iosNoticeRequestUrl;
        noticeUrlIOS = HOST + ProjectConstant.REQUEST_HOME + "/" + noticeUrlIOS+"?version="+version;

        boolean isNeedUpdate = false;
        if(StringUtils.isEmpty(version)){
            isNeedUpdate = true;
        }

        if(version.length()>=5){
            version = version.substring(0, 5);
        }

        if(version.compareTo("3.0.6")<0){
            isNeedUpdate = true;
        }

//       System.out.println("noticeStatus: " + noticeStatus);
        // 是否需要收到通知
        if(noticeStatus.equals("true") && StringUtils.isNotBlank(platform) && platform.equals("3") && isNeedUpdate){
            info.put("needForcedToUpdate", "1");
            info.put("forcedToUpdateUrl", noticeUrlIOS);
        }else {
            info.put("needForcedToUpdate", "0");
            info.put("forcedToUpdateUrl", "");
        }

    }

    /**
     * 查询首页banner图
     *
     * @param info
     */
    private void createBannerPage(JSONObject info, String platform, HttpServletRequest request, String HOST) {

        AdsRequest adsRequest = new AdsRequest();
        adsRequest.setLimitStart(AppHomePageDefine.BANNER_SIZE_LIMIT_START);
        adsRequest.setLimitEnd(AppHomePageDefine.BANNER_SIZE_LIMIT_END);
        adsRequest.setHost(HOST);
        String code = "";
        if (platform.equals("2")) {
            code = "android_banner";
        } else if (platform.equals("3")) {
            code = "ios_banner";
        }
        adsRequest.setCode(code);
        List<AppAdsCustomizeVO> picList =amAdsClient.getBannerList(adsRequest);
        if (picList != null && picList.size() > 0) {
            for(AppAdsCustomizeVO appAdsCustomize : picList){
                appAdsCustomize.setPicUrl(appAdsCustomize.getImage());
                // 安卓需加sign和token
                if ("2".equals(platform)) {
                    String picH5Url = appAdsCustomize.getUrl();
                    appAdsCustomize.setPicH5Url(CommonUtils.concatReturnUrl(request, picH5Url));
                } else {
                    appAdsCustomize.setPicH5Url(appAdsCustomize.getUrl());
                }
            }
            info.put("picList", picList);
            info.put("picTotal", String.valueOf(picList.size()));
        } else {
            info.put("picList", new ArrayList<AppAdsCustomizeVO>());
            info.put("picTotal", "0");
        }
    }


    /**
     *
     * 首页横幅广告
     * @param info
     */
    private void createBannerlittlePage(JSONObject info,Boolean isNewFlag) {
        AdsRequest request = new AdsRequest();
        request.setLimitStart(AppHomePageDefine.BANNER_SIZE_LIMIT_START);
        request.setLimitEnd(AppHomePageDefine.BANNER_SIZE_LIMIT_END);
        request.setHost(systemConfig.fileDomainUrl);
        request.setCode("bannerlittle");
        List<AppAdsCustomizeVO> picList = amAdsClient.getBannerList(request);
        if (picList != null && picList.size() > 0) {
            AppAdsCustomizeVO appads = picList.get(0);
            System.out.println("adImageUrl: " + appads.getImage());
            System.out.println("adImageUrlOperation: " + appads.getUrl());
            info.put("adImageUrl", appads.getImage());
            info.put("adImageUrlOperation", appads.getUrl());

            if(appads.getNewUserShow().equals("2") || (appads.getNewUserShow().equals("1") && isNewFlag)){
                info.put("adExist", "1");
            }else{
                info.put("adExist", "0");
            }
        } else {
            info.put("adExist", "0");
            info.put("adImageUrl", "");
            info.put("adImageUrlOperation", "");
        }
        //System.out.println("jsonInfoBannerLittle: " + info.toJSONString());
    }


    /**
     *
     * 首页弹窗广告图片
     * @author hsy
     * @param info
     */
    private void createPopImgPage(JSONObject info, String uniqueIdentifier) {
        AdsRequest request = new AdsRequest();
        request.setLimitStart(AppHomePageDefine.BANNER_SIZE_LIMIT_START);
        request.setLimitEnd(AppHomePageDefine.BANNER_SIZE_LIMIT_END);
        request.setHost(systemConfig.fileDomainUrl);
        request.setCode("popup");

        Integer userId = info.get("userId") == null? null: (Integer)info.get("userId");
        List<AppAdsCustomizeVO> picList = amAdsClient.getBannerList(request);
        if (picList != null && picList.size() > 0) {
            AppAdsCustomizeVO appads = picList.get(0);
            info.put("imageUrl", appads.getImage());
            info.put("imageUrlOperation", appads.getUrl());

            // TODO: 2018/7/6 查询mongo 获取用户请求次数 zyk int requestTimes = homePageService.updateCurrentDayRequestTimes(uniqueIdentifier, userId);
            if(appads.getNewUserShow().equals("2") || (appads.getNewUserShow().equals("1") && userId == null)){
                //if(requestTimes <= 1){
                if (true){
                    // 如果是今天第一次请求则显示
                    info.put("imageUrlExist", "1");
                }else{
                    // 不是今天第一次请求则不显示
                    info.put("imageUrlExist", "0");
                }

            }else{
                info.put("imageUrlExist", "0");
            }
        } else {

            info.put("imageUrlExist", "0");
            info.put("imageUrl", "");
            info.put("imageUrlOperation", "");
        }
    }

    /**
     * 创建首页module
     *
     * @param moduleList
     * @param platform
     * @param android
     * @param ios
     */
    private void createModule(List<AppModuleBean> moduleList, String platform, String android, String ios, String HOST) {
        AdsRequest adsRequest = new AdsRequest();
        adsRequest.setLimitStart(AppHomePageDefine.BANNER_SIZE_LIMIT_START);
        adsRequest.setLimitEnd(AppHomePageDefine.BANNER_SIZE_LIMIT_END);
        adsRequest.setHost(HOST);
        String code = "";
        if (platform.equals("2")) {
            code = android;
        } else if (platform.equals("3")) {
            code = ios;
        }
        adsRequest.setCode(code);
        List<AppAdsCustomizeVO> picList = amAdsClient.getBannerList(adsRequest);
        if (picList != null && picList.size() > 0) {
            AppModuleBean appModule = new AppModuleBean();
            appModule.setModuleUrl(picList.get(0).getImage());
            appModule.setModuleH5Url(picList.get(0).getUrl());
            moduleList.add(appModule);
        } else {
            AppModuleBean appModule = new AppModuleBean();
            appModule.setModuleUrl("");
            appModule.setModuleH5Url("");
            moduleList.add(appModule);
        }

    }


    /**
     * 获取新手标数据
     *
     */
    private void createProjectNewPage(JSONObject info, List<AppProjectListCustomizeVO> list, String HOST) {
        String host = HOST + ProjectConstant.REQUEST_HOME + ProjectConstant.REQUEST_MAPPING
                + ProjectConstant.PROJECT_DETAIL_ACTION;
        List<AppProjectListCustomizeVO> projectList = searchProjectNew(host);

        if (!CollectionUtils.isEmpty(projectList)) {
            AppProjectListCustomizeVO project = projectList.get(0);
            if (list.size() == 0) {
                list.add(project);
            }
            info.put("sprogExist", "1");
            info.put("sprogBorrowApr", project.getBorrowApr());
            info.put("sprogBorrowPeriod", project.getBorrowPeriod());
            String balance = project.getBorrowAccountWait();//add by cwyang 根据borrowNid 获取项目可投金额
            info.put("sprogBorrowMoney", balance);//新手标取得是可投余额不是投资总额 add by cwyang
            info.put("sprogBorrowNid", project.getBorrowNid());
            info.put("sprogBorrowUrl", project.getBorrowUrl());
            info.put("sprogTime", project.getOnTime());
        } else {
            info.put("sprogExist", "0");
            info.put("sprogBorrowApr", "");
            info.put("sprogBorrowPeriod", "");
            info.put("sprogBorrowMoney", "");
            info.put("sprogBorrowNid", "");
            info.put("sprogBorrowUrl", "");
            info.put("sprogTime", "");
        }

    }


    private List<AppProjectListCustomizeVO> searchProjectNew(String host) {
        List<AppProjectListCustomizeVO> projectList = new ArrayList<>();
        // 取得新手汇项目（投资中）
        String statusNewInvest = "15";
        AppProjectListRequest request = new AppProjectListRequest();
        request.setStatus(statusNewInvest);
        request.setType("4");
        request.setHost(host);
        // 查询首页定时发标的项目
        List<AppProjectListCustomizeVO> listNewInvest = webProjectListClient.searchAppProjectList(request);
        if (listNewInvest != null && listNewInvest.size() > 0) {
            for (int i = 0; i < listNewInvest.size(); i++) {
                AppProjectListCustomizeVO newInvest = listNewInvest.get(i);
                projectList.add(newInvest);
            }
            return projectList;
        }
        // 新手汇项目（投资中）为空
        // 取得新手汇项目（定时发标）
        String statusNewOnTime = "14";
        request.setStatus(statusNewOnTime);
        // 查询首页定时发标的项目
        List<AppProjectListCustomizeVO> listNewOnTime = webProjectListClient.searchAppProjectList(request);
        if (listNewOnTime != null && listNewOnTime.size() > 0) {
            for (int i = 0; i < listNewOnTime.size(); i++) {
                AppProjectListCustomizeVO newOnTime = listNewOnTime.get(i);
                projectList.add(newOnTime);
            }
            return projectList;
        }

        //复审
        String status = "16";
        request.setStatus(status);
        List<AppProjectListCustomizeVO> reviewList = webProjectListClient.searchAppProjectList(request);
        if (reviewList != null && reviewList.size() > 0) {
            for (int i = 0; i < reviewList.size(); i++) {
                AppProjectListCustomizeVO newOnTime = reviewList.get(i);
                projectList.add(newOnTime);
            }
            return projectList;
        }
        //还款
        status = "17";
        request.setStatus(status);
        List<AppProjectListCustomizeVO> repaymentList = webProjectListClient.searchAppProjectList(request);
        if (repaymentList != null && repaymentList.size() > 0) {
            for (int i = 0; i < repaymentList.size(); i++) {
                AppProjectListCustomizeVO newOnTime = repaymentList.get(i);
                projectList.add(newOnTime);
            }
            return projectList;
        }

        return projectList;
    }

    /**
     * 创建首页广告
     *
     * @param info
     * @param platform
     */
    private void createAdPic(JSONObject info, String platform, String type, String HOST) {
        AdsRequest adsRequest = new AdsRequest();
        adsRequest.setLimitStart(AppHomePageDefine.BANNER_SIZE_LIMIT_START);
        adsRequest.setLimitEnd(AppHomePageDefine.BANNER_SIZE_LIMIT_END);
        adsRequest.setHost(HOST);
        String code = "";
        if (platform.equals("2")) {
            if (type.equals("0")) {//未注册
                code = "android_regist_888";
            } else if (type.equals("1")) {
                code = "android_open_888";
            }
        } else if (platform.equals("3")) {
            if (type.equals("0")) {//未注册
                code = "ios_regist_888";
            } else if (type.equals("1")) {
                code = "ios_open_888";
            }
        }
        adsRequest.setCode(code);
        List<AppAdsCustomizeVO> picList = amAdsClient.getBannerList(adsRequest);
        if (!CollectionUtils.isEmpty(picList)) {
            info.put("adPicUrl", picList.get(0).getImage());
            info.put("adClickPicUrl", picList.get(0).getUrl());
        } else {
            info.put("adPicUrl", "");
            info.put("adClickPicUrl", "");
        }
    }
}