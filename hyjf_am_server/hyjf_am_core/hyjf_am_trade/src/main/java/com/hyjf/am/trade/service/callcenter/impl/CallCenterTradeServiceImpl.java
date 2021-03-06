/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.callcenter.impl;

import com.hyjf.am.resquest.callcenter.*;
import com.hyjf.am.trade.dao.mapper.customize.*;
import com.hyjf.am.trade.dao.model.auto.RUser;
import com.hyjf.am.trade.dao.model.customize.*;
import com.hyjf.am.trade.service.callcenter.CallCenterTradeService;
import com.hyjf.common.cache.CacheUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wangjun
 * @version CallCenterTradeServiceImpl, v0.1 2018/6/11 17:52
 */
@Service
public class CallCenterTradeServiceImpl implements CallCenterTradeService {
    @Autowired
    private CallCenterRepaymentDetailCustomizeMapper callCenterRepaymentDetailCustomizeMapper;

    @Autowired
    private CallCenterAccountDetailCustomizeMapper callCenterAccountDetailCustomizeMapper;

    @Autowired
    private CallcenterRechargeCustomizeMapper callcenterRechargeCustomizeMapper;

    @Autowired
    private CallcenterWithdrawCustomizeMapper callcenterWithdrawCustomizeMapper;
    
    @Autowired
    private CallcenterHztInvestCustomizeMapper callcenterHztInvestCustomizeMapper;
    
    @Autowired
    private CallcenterBorrowCreditCustomizeMapper callcenterBorrowCreditCustomizeMapper;
    
    @Autowired
    private CallcenterBorrowCreditTenderCustomizeMapper callcenterBorrowCreditTenderCustomizeMapper;

    @Autowired
    private CallCenterCouponUserCustomizeMapper callCenterCouponUserCustomizeMapper;

    @Autowired
    private RUserCustomizeMapper rUserCustomizeMapper;

    @Autowired
    private CallCenterBankAccountManageMapper callCenterBankAccountManageMapper;
    /**
     *
     * 按照用户名/手机号查询还款明细（直投产品，含承接的债权）
     * @author wangjun
     * @param callCenterBaseRequest
     * @return
     */
    @Override
    public List<CallCenterHztRepaymentDetailCustomize> getHztRepaymentDetailList(CallCenterBaseRequest callCenterBaseRequest) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("userId", callCenterBaseRequest.getUserId());
        paraMap.put("limitStart", callCenterBaseRequest.getLimitStart());
        paraMap.put("limitEnd", callCenterBaseRequest.getLimitEnd());
        return callCenterRepaymentDetailCustomizeMapper.getHztRepaymentDetailList(paraMap);
    }
    /**
     *
     * 按照用户名/手机号查询还款明细（汇添金）
     * @author wangjun
     * @param callCenterBaseRequest
     * @return
     */
    @Override
    public List<CallCenterHtjRepaymentDetailCustomize> getHtjRepaymentDetailList(CallCenterBaseRequest callCenterBaseRequest) {
        Map<String, Object> paraMap = new HashMap<String, Object>();
        paraMap.put("userId", callCenterBaseRequest.getUserId());
        paraMap.put("limitStart", callCenterBaseRequest.getLimitStart());
        paraMap.put("limitEnd", callCenterBaseRequest.getLimitEnd());
        return callCenterRepaymentDetailCustomizeMapper.getHtjRepaymentDetailList(paraMap);
    }

    /**
     *
     * 查询资金明细
     * @author wangjun
     * @param callCenterAccountDetailRequest
     * @return
     */
    @Override
    public List<CallCenterAccountDetailCustomize> queryAccountDetails(CallCenterAccountDetailRequest callCenterAccountDetailRequest) {
        return callCenterAccountDetailCustomizeMapper.queryAccountDetails(callCenterAccountDetailRequest);
    }

    /**
     *
     * 测评获取冻结金额和代收本经明细
     * @author wenxin
     * @param userId
     * @return
     */
    @Override
    public CallCenterAccountDetailCustomize queryAccountEvalDetail(Integer userId) {
        CallCenterAccountDetailRequest callCenterAccountDetailRequest = new CallCenterAccountDetailRequest();
        callCenterAccountDetailRequest.setUserId(userId);
        return callCenterAccountDetailCustomizeMapper.queryAccountEvalDetail(callCenterAccountDetailRequest);
    }

    /**
     *
     * 查询充值明细
     * @author wangjun
     * @param centerBaseRequest
     * @return
     */
    @Override
    public List<CallCenterRechargeCustomize> queryRechargeList(CallCenterBaseRequest centerBaseRequest) {
        List<CallCenterRechargeCustomize> list = callcenterRechargeCustomizeMapper.queryRechargeList(centerBaseRequest);
       if(!CollectionUtils.isEmpty(list)){
            Map<String, String> statusMap = CacheUtil.getParamNameMap("RECHARGE_STATUS");
            Map<String, String> bankMap = CacheUtil.getParamNameMap("BANK_TYPE");
            for(CallCenterRechargeCustomize callCenterRechargeCustomize : list){
                callCenterRechargeCustomize.setStatus(statusMap.getOrDefault(callCenterRechargeCustomize.getStatus(),null));
                callCenterRechargeCustomize.setIsBank(bankMap.getOrDefault(callCenterRechargeCustomize.getIsBank(),null));
            }
        }
        return list;
    }

    /**
     * 查询提现明细
     * @author wangjun
     * @param centerBaseRequest
     * @return
     */
    @Override
    public List<CallCenterWithdrawCustomize> getWithdrawRecordList(CallCenterBaseRequest centerBaseRequest) {
        List<CallCenterWithdrawCustomize> list = callcenterWithdrawCustomizeMapper.getWithdrawRecordList(centerBaseRequest);
        if(!CollectionUtils.isEmpty(list)){
            Map<String, String> clientMap = CacheUtil.getParamNameMap("CLIENT");
            for(CallCenterWithdrawCustomize callCenterWithdrawCustomize : list){
                callCenterWithdrawCustomize.setClientStr(clientMap.getOrDefault(callCenterWithdrawCustomize.getClientStr(),null));
            }
        }
        return list;
    }
    
    /**
     * 查询出借明细(汇直投)
     * @author libin
     * @param centerBaseRequest
     * @return
     */
	@Override
	public List<CallcenterHztInvestCustomize> getBorrowInvestList(CallcenterHztInvestRequest callcenterHztInvestRequest) {
		List<CallcenterHztInvestCustomize> list = callcenterHztInvestCustomizeMapper.getBorrowInvestList(callcenterHztInvestRequest);
		if(!CollectionUtils.isEmpty(list)){
			Map<String, String> operatingDeckMap = CacheUtil.getParamNameMap("CLIENT");
			Map<String, String> tenderUserAttributeMap = CacheUtil.getParamNameMap("USER_PROPERTY");
			Map<String, String> investTypeMap = CacheUtil.getParamNameMap("INVEST_TYPE");
           for(CallcenterHztInvestCustomize callcenterHztInvestCustomize : list){
        	   callcenterHztInvestCustomize.setOperatingDeck(operatingDeckMap.getOrDefault(callcenterHztInvestCustomize.getOperatingDeck(),null));
        	   callcenterHztInvestCustomize.setTenderUserAttribute(tenderUserAttributeMap.getOrDefault(callcenterHztInvestCustomize.getTenderUserAttribute(),null));
        	   callcenterHztInvestCustomize.setInvestType(investTypeMap.getOrDefault(callcenterHztInvestCustomize.getInvestType(),null)); 
            }
		}
		return list;
	}
	
    /**
     * 查询转让信息
     * @author libin
     * @param centerBaseRequest
     * @return
     */
	@Override
	public List<CallCenterBorrowCreditCustomize> getBorrowCreditList(SrchTransferInfoRequest srchTransferInfoRequest) {
		List<CallCenterBorrowCreditCustomize> list = callcenterBorrowCreditCustomizeMapper.getBorrowCreditList(srchTransferInfoRequest);
		return list;
	}
	
    /**
     * 查询承接债权信息
     * @author libin
     * @param centerBaseRequest
     * @return
     */
	@Override
	public List<CallCenterBorrowCreditCustomize> getBorrowCreditTenderList(
			SrchTransferInfoRequest srchTransferInfoRequest) {
		List<CallCenterBorrowCreditCustomize> list = callcenterBorrowCreditTenderCustomizeMapper.getBorrowCreditTenderList(srchTransferInfoRequest);
		return list;
	}

    /**
     * 查询优惠券
     * @author wangjun
     * @param centerBaseRequest
     * @return
     */
    @Override
    public List<CallCenterCouponUserCustomize> getUserCouponInfoList(CallCenterBaseRequest centerBaseRequest) {
        return callCenterCouponUserCustomizeMapper.selectCouponUserList(centerBaseRequest);
    }

    /**
     * 查询优惠券使用（直投产品）
     * @author wangjun
     * @param centerBaseRequest
     * @return
     */
    @Override
    public List<CallCenterCouponTenderCustomize> getUserCouponTenderList(CallCenterBaseRequest centerBaseRequest) {
        List<CallCenterCouponTenderCustomize> list = callCenterCouponUserCustomizeMapper.selectCouponTenderList(centerBaseRequest);
        if(!CollectionUtils.isEmpty(list)){
            Map<String, String> clientMap = CacheUtil.getParamNameMap("CLIENT");
            Map<String, String> couponReciveMap = CacheUtil.getParamNameMap("COUPON_RECIVE_STATUS");
            for(CallCenterCouponTenderCustomize callCenterCouponTenderCustomize : list){
                callCenterCouponTenderCustomize.setOperatingDeck(clientMap.getOrDefault(callCenterCouponTenderCustomize.getOperatingDeck(),null));
                callCenterCouponTenderCustomize.setReceivedFlg(couponReciveMap.getOrDefault(callCenterCouponTenderCustomize.getReceivedFlg(),null));
            }
        }
        return list;
    }

    /**
     * 查询优惠券回款（直投产品）
     * @author wangjun
     * @param centerBaseRequest
     * @return
     */
    @Override
    public List<CallCenterCouponBackMoneyCustomize> getUserCouponBackMoneyList(CallCenterBaseRequest centerBaseRequest) {
        List<CallCenterCouponBackMoneyCustomize> list = callCenterCouponUserCustomizeMapper.selectCouponBackMoneyList(centerBaseRequest);
        if(!CollectionUtils.isEmpty(list)){
            Map<String, String> couponReciveMap = CacheUtil.getParamNameMap("COUPON_RECIVE_STATUS");
            for(CallCenterCouponBackMoneyCustomize callCenterCouponBackMoneyCustomize : list){
                callCenterCouponBackMoneyCustomize.setReceivedFlg(couponReciveMap.getOrDefault(callCenterCouponBackMoneyCustomize.getReceivedFlg(),null));
            }
        }
        return list;
    }
	@Override
	public List<CallcenterHtjInvestCustomize> getHtjBorrowInvestList(
			CallcenterHtjInvestRequest callcenterHtjInvestRequest) {
		List<CallcenterHtjInvestCustomize> list = callcenterHztInvestCustomizeMapper.getHtjBorrowInvestList(callcenterHtjInvestRequest);
		if(!CollectionUtils.isEmpty(list)){
			Map<String, String> operatingDeckMap = CacheUtil.getParamNameMap("CLIENT");
           for(CallcenterHtjInvestCustomize callcenterHtjInvestCustomize : list){
        	   callcenterHtjInvestCustomize.setPlatform(operatingDeckMap.getOrDefault(callcenterHtjInvestCustomize.getPlatform(),null));
            }
		}
		return list;
	}

    /**
     * 根据用户ID查询推荐人信息
     * @param userId
     * @return
     */
    @Override
    public RUser getRefereerInfoByUserId(Integer userId){
        return rUserCustomizeMapper.selectRefUserInfoByUserId(userId);
    }

    /**
     * 查询账户余额
     * @param callCenterBankAccountManageRequest
     * @return List<CallCenterCouponBackMoneyCustomize>
     * @author libin
     */
    @Override
    public List<CallCenterBankAccountManageCustomize> queryAccountInfos(CallCenterBankAccountManageRequest callCenterBankAccountManageRequest) {
        List<CallCenterBankAccountManageCustomize> list = callCenterBankAccountManageMapper.queryAccountInfos(callCenterBankAccountManageRequest);
        return list;
    }
}
