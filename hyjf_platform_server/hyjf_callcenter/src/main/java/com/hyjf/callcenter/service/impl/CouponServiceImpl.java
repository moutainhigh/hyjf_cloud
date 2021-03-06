/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.callcenter.service.impl;

import com.hyjf.am.resquest.callcenter.CallCenterBaseRequest;
import com.hyjf.am.vo.callcenter.CallCenterCouponBackMoneyVO;
import com.hyjf.am.vo.callcenter.CallCenterCouponTenderVO;
import com.hyjf.am.vo.callcenter.CallCenterCouponUserVO;
import com.hyjf.callcenter.beans.CouponBackMoneyBean;
import com.hyjf.callcenter.beans.CouponBean;
import com.hyjf.callcenter.beans.CouponTenderBean;
import com.hyjf.callcenter.beans.ResultListBean;
import com.hyjf.callcenter.client.AmMarketClient;
import com.hyjf.callcenter.client.AmTradeClient;
import com.hyjf.callcenter.client.AmUserClient;
import com.hyjf.callcenter.result.BaseResultBean;
import com.hyjf.callcenter.service.CouponService;
import com.hyjf.common.cache.CacheUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @author wangjun
 * @version CouponServiceImpl, v0.1 2018/6/19 11:40
 */
@Service
public class CouponServiceImpl implements CouponService {
    @Autowired
    AmTradeClient amTradeClient;

    @Autowired
    AmUserClient amUserClient;

    @Autowired
    AmMarketClient amMarketClient;
    /**
     * 查询优惠券
     * @param centerBaseRequest
     * @return ResultListBean
     * @author wangjun
     */
    @Override
    public ResultListBean selectCouponUserList(CallCenterBaseRequest centerBaseRequest){
        ResultListBean result = new ResultListBean();
        List<CallCenterCouponUserVO> list = amTradeClient.selectCouponUserList(centerBaseRequest);
        if(!CollectionUtils.isEmpty(list)){
            for (CallCenterCouponUserVO recordBean : list) {
                CouponBean returnBean = new CouponBean();
                setUpCouponBean(recordBean);

                //检索bean→返回bean
                BeanUtils.copyProperties(recordBean, returnBean);
                //用户名
                returnBean.setUserName(centerBaseRequest.getUserName());
                //手机号
                returnBean.setMobile(centerBaseRequest.getMobile());

                //获取优惠券内容(2：活动发放优惠券  3：vip礼包)
                if(recordBean.getCouponSourceNum().equals(2)){
                    returnBean.setCouponContent(amMarketClient.getCouponContent(recordBean.getActivityId()));
                } else if(recordBean.getCouponSourceNum().equals(3)){
                    returnBean.setCouponContent(amUserClient.getCouponContent(recordBean.getCouponCode()));
                }
                result.getDataList().add(returnBean);
            }
            result.statusMessage(ResultListBean.STATUS_SUCCESS, ResultListBean.STATUS_SUCCESS_DESC);
        }else {
            result.statusMessage(BaseResultBean.STATUS_FAIL,"该用户没有优惠券信息！");
            return result;
        }

        return result;
    }

    /**
     * 查询优惠券使用（直投产品）
     * @param centerBaseRequest
     * @return ResultListBean
     * @author wangjun
     */
    @Override
    public ResultListBean selectCouponTenderList(CallCenterBaseRequest centerBaseRequest){
        ResultListBean result = new ResultListBean();
        List<CallCenterCouponTenderVO> list =  amTradeClient.selectCouponTenderList(centerBaseRequest);
        if(!CollectionUtils.isEmpty(list)){
            for (CallCenterCouponTenderVO recordBean : list) {
                CouponTenderBean returnBean = new CouponTenderBean();
                setUpCouponTenderBean(recordBean);

                //检索bean→返回bean
                BeanUtils.copyProperties(recordBean, returnBean);
                //用户名
                returnBean.setUserName(centerBaseRequest.getUserName());
                //手机号
                returnBean.setMobile(centerBaseRequest.getMobile());
                result.getDataList().add(returnBean);
            }
            result.statusMessage(ResultListBean.STATUS_SUCCESS, ResultListBean.STATUS_SUCCESS_DESC);
        }else {
            result.statusMessage(BaseResultBean.STATUS_FAIL,"该用户没有优惠券使用记录！");
            return result;
        }

        return result;
    }

    /**
     * 查询优惠券回款（直投产品）
     * @param centerBaseRequest
     * @return List<CallCenterCouponBackMoneyVO>
     * @author wangjun
     */
    @Override
    public ResultListBean selectCouponBackMoneyList(CallCenterBaseRequest centerBaseRequest){
        ResultListBean result = new ResultListBean();
        List<CallCenterCouponBackMoneyVO> recordList = this.amTradeClient.selectCouponBackMoneyList(centerBaseRequest);
        if (recordList == null) {
            result.statusMessage(BaseResultBean.STATUS_FAIL,"该用户没有优惠券回款记录！");
            return result;
        }

        //编辑返回信息
        for (CallCenterCouponBackMoneyVO recordBean : recordList) {
            CouponBackMoneyBean returnBean = new CouponBackMoneyBean();
            //检索bean→返回bean
            BeanUtils.copyProperties(recordBean, returnBean);
            //用户名
            returnBean.setUserName(centerBaseRequest.getUserName());
            //手机号
            returnBean.setMobile(centerBaseRequest.getMobile());
            result.getDataList().add(returnBean);
        }

        result.statusMessage(ResultListBean.STATUS_SUCCESS, ResultListBean.STATUS_SUCCESS_DESC);
        return result;
    }


    private void setUpCouponBean(CallCenterCouponUserVO recordBean) {
        // 操作平台
        Map<String, String> clients = CacheUtil.getParamNameMap("CLIENT");
        recordBean.setProjectType(createProjectTypeString(recordBean.getProjectType()));
        recordBean.setCouponSystem(createCouponSystemString(recordBean.getCouponSystem(),clients));
    }

    private void setUpCouponTenderBean(CallCenterCouponTenderVO recordBean) {
        // 操作平台
        Map<String, String> clients = CacheUtil.getParamNameMap("CLIENT");
        recordBean.setProjectType(createProjectTypeString(recordBean.getProjectType()));
        recordBean.setCouponSystem(createCouponSystemString(recordBean.getCouponSystem(),clients));
    }


    private String createProjectTypeString(String projectType) {
        String projectString = "";
        if (projectType.indexOf("-1") != -1) {
            projectString = "不限";
        } else {
            //勾选汇直投，尊享汇，融通宝
            if (projectType.indexOf("1")!=-1&&projectType.indexOf("4")!=-1&&projectType.indexOf("6")!=-1) {
                projectString = projectString + "散标,";
            }
            //勾选汇直投  未勾选尊享汇，融通宝
            if (projectType.indexOf("1")!=-1&&projectType.indexOf("4")==-1&&projectType.indexOf("6")==-1) {
                projectString = projectString + "散标,";
            }
            //勾选汇直投，融通宝  未勾选尊享汇
            if(projectType.indexOf("1")!=-1&&projectType.indexOf("4")==-1&&projectType.indexOf("6")!=-1){
                projectString = projectString + "散标,";
            }
            //勾选汇直投，选尊享汇 未勾选融通宝
            if(projectType.indexOf("1")!=-1&&projectType.indexOf("4")!=-1&&projectType.indexOf("6")==-1){
                projectString = projectString + "散标,";
            }
            //勾选尊享汇，融通宝  未勾选直投
            if(projectType.indexOf("1")==-1&&projectType.indexOf("4")!=-1&&projectType.indexOf("6")!=-1){
                projectString = projectString + "散标,";
            }
            //勾选尊享汇  未勾选直投，融通宝
            if(projectType.indexOf("1")==-1&&projectType.indexOf("4")!=-1&&projectType.indexOf("6")==-1){
                projectString = projectString + "散标,";
            }
            //勾选尊享汇  未勾选直投，融通宝
            if(projectType.indexOf("1")==-1&&projectType.indexOf("4")==-1&&projectType.indexOf("6")!=-1){
                projectString = projectString + "散标,";
            }

            if (projectType.indexOf("3")!=-1) {
                projectString = projectString + "新手,";
            }
            if (projectType.indexOf("5")!=-1) {
                projectString = projectString + "智投,";
            }
        }
        projectString = StringUtils.removeEnd(projectString, ",");
        return projectString;
    }

    private String createCouponSystemString(String couponSystem, Map<String, String> clients) {
        String clientString = "";

        // 被选中操作平台
        String clientSed[] = StringUtils.split(couponSystem, ",");
        for (int i = 0; i < clientSed.length; i++) {
            if ("-1".equals(clientSed[i])) {
                clientString = clientString + "全部平台";
                break;
            } else {
                for (String key : clients.keySet()) {
                    if (clientSed[i].equals(key)) {
                        if (i != 0 && clientString.length() != 0) {
                            clientString = clientString + "、";
                        }
                        clientString = clientString + clients.get(key);
                    }
                }
            }
        }
        return clientString.replace("Android、iOS", "APP");
    }
}
