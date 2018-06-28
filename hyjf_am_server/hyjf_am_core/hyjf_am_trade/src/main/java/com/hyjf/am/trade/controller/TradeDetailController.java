package com.hyjf.am.trade.controller;

import com.alibaba.fastjson.JSONObject;

import com.hyjf.am.response.trade.AssetManageResponse;
import com.hyjf.am.response.trade.TenderDetailResponse;
import com.hyjf.am.resquest.trade.AssetManageBeanRequest;

import com.hyjf.am.resquest.trade.TradeDetailBeanRequest;
import com.hyjf.am.trade.dao.model.customize.trade.WebUserRechargeListCustomize;
import com.hyjf.am.trade.dao.model.customize.trade.WebUserTradeListCustomize;
import com.hyjf.am.trade.dao.model.customize.trade.WebUserWithdrawListCustomize;
import com.hyjf.am.trade.service.TradeDetailService;

import com.hyjf.am.vo.trade.tradedetail.WebUserRechargeListCustomizeVO;
import com.hyjf.am.vo.trade.tradedetail.WebUserTradeListCustomizeVO;
import com.hyjf.am.vo.trade.tradedetail.WebUserWithdrawListCustomizeVO;
import com.hyjf.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author pangchengchao
 * @version AccountTradeController, v0.1 2018/6/27 11:06
 */
@RestController
@RequestMapping("am-trade/tradedetail")
public class TradeDetailController {
    private static Logger logger = LoggerFactory.getLogger(AssetManageController.class);
    @Autowired
    private TradeDetailService tradeDetailService;

    /**
     * @Description "获取用户收支明细列表
     * @Author pangchengchao
     * @Version v0.1
     * @Date
     */
    @RequestMapping("/searchUserTradeList")
    public TenderDetailResponse searchUserTradeList(@RequestBody TradeDetailBeanRequest request){
        logger.info("request:" +JSONObject.toJSON(request));
        TenderDetailResponse response = new TenderDetailResponse();
        List<WebUserTradeListCustomize> list = tradeDetailService.searchUserTradeList(request);
        if(!CollectionUtils.isEmpty(list)){
            List<WebUserTradeListCustomizeVO> voList = CommonUtils.convertBeanList(list, WebUserTradeListCustomizeVO.class);
            response.setUserTrades(voList);
        }
        return response;
    }

    /**
     * @Description 获取用户收支明细列表数量
     * @Author pangchengchao
     * @Version v0.1
     * @Date
     */
    @RequestMapping("/countUserTradeRecordTotal")
    public TenderDetailResponse countUserTradeRecordTotal(@RequestBody TradeDetailBeanRequest request){
        logger.info("request:" +JSONObject.toJSON(request));
        TenderDetailResponse response = new TenderDetailResponse();
        int count = this.tradeDetailService.countUserTradeRecordTotal(request);

        response.setUserTradesCount(count);
        return response;
    }


    /**
     * @Description "获取用户充值记录列表
     * @Author pangchengchao
     * @Version v0.1
     * @Date
     */
    @RequestMapping("/searchUserRechargeList")
    public TenderDetailResponse searchUserRechargeList(@RequestBody TradeDetailBeanRequest request){
        logger.info("request:" +JSONObject.toJSON(request));
        TenderDetailResponse response = new TenderDetailResponse();
        List<WebUserRechargeListCustomize> list = tradeDetailService.searchUserRechargeList(request);
        if(!CollectionUtils.isEmpty(list)){
            List<WebUserRechargeListCustomizeVO> voList = CommonUtils.convertBeanList(list, WebUserRechargeListCustomizeVO.class);
            response.setRechargeList(voList);
        }
        return response;
    }
    /**
     * @Description 获取用户充值记录列表数量
     * @Author pangchengchao
     * @Version v0.1
     * @Date
     */
    @RequestMapping("/countUserRechargeRecordTotal")
    public TenderDetailResponse countUserRechargeRecordTotal(@RequestBody TradeDetailBeanRequest request){
        logger.info("request:" +JSONObject.toJSON(request));
        TenderDetailResponse response = new TenderDetailResponse();
        int count = this.tradeDetailService.countUserRechargeRecordTotal(request);

        response.setRechargeListCount(count);
        return response;
    }


    /**
     * @Description "获取用户提现记录列表
     * @Author pangchengchao
     * @Version v0.1
     * @Date
     */
    @RequestMapping("/searchUserWithdrawList")
    public TenderDetailResponse searchUserWithdrawList(@RequestBody TradeDetailBeanRequest request){
        logger.info("request:" +JSONObject.toJSON(request));
        TenderDetailResponse response = new TenderDetailResponse();
        List<WebUserWithdrawListCustomize> list = tradeDetailService.searchUserWithdrawList(request);
        if(!CollectionUtils.isEmpty(list)){
            List<WebUserWithdrawListCustomizeVO> voList = CommonUtils.convertBeanList(list, WebUserWithdrawListCustomizeVO.class);
            response.setWithdrawList(voList);
        }
        return response;
    }
    /**
     * @Description 获取用户提现记录列表数量
     * @Author pangchengchao
     * @Version v0.1
     * @Date
     */
    @RequestMapping("/countUserWithdrawRecordTotal")
    public TenderDetailResponse countUserWithdrawRecordTotal(@RequestBody TradeDetailBeanRequest request){
        logger.info("request:" +JSONObject.toJSON(request));
        TenderDetailResponse response = new TenderDetailResponse();
        int count = this.tradeDetailService.countUserWithdrawRecordTotal(request);

        response.setWithdrawListCount(count);
        return response;
    }
}