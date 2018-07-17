/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.client.impl;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.admin.client.AmTradeClient;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.*;
import com.hyjf.am.response.trade.*;
import com.hyjf.am.resquest.admin.*;
import com.hyjf.am.resquest.trade.BankCreditEndListRequest;
import com.hyjf.am.resquest.trade.InsertBankCreditEndForCreditEndRequest;
import com.hyjf.am.resquest.trade.UpdateBorrowForAutoTenderRequest;
import com.hyjf.am.vo.admin.*;
import com.hyjf.am.vo.admin.coupon.CouponRecoverVO;
import com.hyjf.am.vo.admin.coupon.ParamName;
import com.hyjf.am.vo.bank.BankCallBeanVO;
import com.hyjf.am.vo.datacollect.AccountWebListVO;
import com.hyjf.am.vo.trade.AccountTradeVO;
import com.hyjf.am.vo.trade.BankCreditEndVO;
import com.hyjf.am.vo.trade.TransferExceptionLogVO;
import com.hyjf.am.vo.trade.account.AccountListVO;
import com.hyjf.am.vo.trade.account.AccountVO;
import com.hyjf.am.vo.trade.account.BankMerchantAccountListVO;
import com.hyjf.am.vo.trade.borrow.*;
import com.hyjf.am.vo.trade.hjh.HjhAccedeVO;
import com.hyjf.am.vo.trade.hjh.HjhDebtCreditTenderVO;
import com.hyjf.am.vo.trade.hjh.HjhDebtCreditVO;
import com.hyjf.am.vo.trade.repay.BankRepayFreezeLogVO;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.validator.Validator;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangqingqing
 * @version AmTradeClientImpl, v0.1 2018/7/5 10:48
 */
@Service
public class AmTradeClientImpl implements AmTradeClient{
    private static Logger logger = LoggerFactory.getLogger(AmTradeClientImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${am.trade.service.name}")
    private String tradeService;

    @Override
    public Integer updateByPrimaryKeySelective(MerchantAccountVO merchantAccount) {
        int cnt = restTemplate.postForEntity(tradeService+"/merchantAccount/updateByPrimaryKeySelective",merchantAccount,Integer.class).getBody();
        return cnt;
    }

    @Override
    public MerchantAccountResponse selectRecordList(MerchantAccountListRequest request) {
        MerchantAccountResponse response = restTemplate
                .postForEntity(tradeService+"/merchantAccount/selectRecordList",request, MerchantAccountResponse.class).getBody();
        if (response != null) {
            return response;
        }
        return null;
    }

    /**
     * 查询定向转账列表count
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public Integer getDirectionalTransferCount(DirectionalTransferListRequest request) {
        Integer count = restTemplate
                .postForEntity(tradeService+"/accountdirectionaltransfer/getdirectionaltransfercount", request, Integer.class)
                .getBody();

        return count;
    }

    /**
     * 根据筛选条件查询list
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public List<AccountDirectionalTransferVO> searchDirectionalTransferList(DirectionalTransferListRequest request) {
        AccountDirectionalTransferResponse response = restTemplate
                .postForEntity(tradeService+"/accountdirectionaltransfer/searchdirectionaltransferlist", request, AccountDirectionalTransferResponse.class)
                .getBody();
        if(Response.isSuccess(response)){
            return response.getResultList();
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
                .postForEntity(tradeService+"/associatedrecords/getassociatedrecordscount", request, Integer.class)
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
                .postForEntity(tradeService+"/associatedrecords/searchassociatedrecordlist", request, AssociatedRecordListResponse.class)
                .getBody();
        if(Response.isSuccess(response)){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 根据筛选条件查询绑定日志count
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public Integer getBindLogCount(BindLogListRequest request) {
        Integer count = restTemplate
                .postForEntity(tradeService+"/associatedlog/getbindlogcount", request, Integer.class)
                .getBody();

        return count;
    }

    /**
     * 根据筛选条件查询绑定日志list
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public List<BindLogVO> searchBindLogList(BindLogListRequest request) {
        BindLogResponse response = restTemplate
                .postForEntity(tradeService+"/associatedlog/searchbindloglist", request, BindLogResponse.class)
                .getBody();
        if(Response.isSuccess(response)){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 根据userId查询Account列表，按理说只能取出来一个Account，但是service需要做个数判断，填写不同的msg，所以返回List
     * @auth sunpeikai
     * @param userId 用户id
     * @return
     */
    @Override
    public List<AccountVO> searchAccountByUserId(Integer userId) {
        AccountResponse response = restTemplate
                .getForEntity(tradeService + "/customertransfer/searchaccountbyuserid/" + userId, AccountResponse.class)
                .getBody();
        if(Response.isSuccess(response)){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 向数据库的ht_user_transfer表中插入数据
     * @auth sunpeikai
     * @param request 用户转账-发起转账的参数
     * @return
     */
    @Override
    public Boolean insertUserTransfer(CustomerTransferRequest request) {
        String url = tradeService + "/customertransfer/insertusertransfer";
        Boolean response = restTemplate.postForEntity(url,request,Boolean.class).getBody();
        return response;
    }

    /**
     * 根据筛选条件查询ht_user_transfer的数据总数
     * @auth sunpeikai
     * @param request 筛选条件
     * @return
     */
    @Override
    public Integer getUserTransferCount(CustomerTransferListRequest request) {
        Integer count = restTemplate
                .postForEntity(tradeService+"/customertransfer/getusertransfercount", request, Integer.class)
                .getBody();
        return count;
    }

    /**
     * 根据筛选条件查询UserTransfer列表
     * @auth sunpeikai
     * @param request 筛选条件
     * @return
     */
    @Override
    public List<UserTransferVO> searchUserTransferList(CustomerTransferListRequest request) {
        UserTransferResponse response = restTemplate
                .postForEntity(tradeService + "/customertransfer/searchusertransferlist", request, UserTransferResponse.class).getBody();
        if(Response.isSuccess(response)){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 根据transferId查询UserTransfer
     * @auth sunpeikai
     * @param transferId ht_user_transfer表的主键id
     * @return
     */
    @Override
    public UserTransferVO searchUserTransferById(Integer transferId) {
        String url = tradeService + "/customertransfer/searchusertransferbyid/" + transferId;
        UserTransferResponse response = restTemplate.getForEntity(url,UserTransferResponse.class).getBody();
        if(Response.isSuccess(response)){
            return response.getResult();
        }
        return null;
    }


    @Override
    public List<AccountTradeVO> selectTradeTypes() {
        String url = tradeService + "/accounttrade/selectTradeTypes";
        AccountTradeResponse response = restTemplate.getForEntity(url,AccountTradeResponse.class).getBody();
        if (response != null) {
            return response.getResultList();
        }
        return null;
    }

    /**
     * 查询汇计划转让列表
     * @param request
     * @return
     */
    @Override
    public HjhDebtCreditReponse queryHjhDebtCreditList(HjhDebtCreditListRequest request) {

        HjhDebtCreditReponse response =  restTemplate.
                postForEntity(tradeService + "/adminHjhDebtCredit/getList", request, HjhDebtCreditReponse.class).
                getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response;
        }
        return null;
    }

    @Override
    public BatchBorrowRecoverReponse getBatchBorrowRecoverList(BatchBorrowRecoverRequest request) {
        BatchBorrowRecoverReponse response =  restTemplate.
                postForEntity(tradeService + "/adminBatchBorrowRecover/getList", request, BatchBorrowRecoverReponse.class).
                getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response;
        }
        return null;
    }

    @Override
    public  List<AdminCouponRepayMonitorCustomizeVO> selectRecordList(CouponRepayRequest form) {
        String url = tradeService + "/couponRepayMonitor/selectCouponRepayMonitorPage";
        AdminCouponRepayMonitorCustomizeResponse response = restTemplate.postForEntity(url,form,AdminCouponRepayMonitorCustomizeResponse.class).getBody();
        if (response != null) {
            return response.getResultList();
        }
        return null;
    }

    @Override
    public AdminCouponRepayMonitorCustomizeResponse couponRepayMonitorCreatePage(CouponRepayRequest form) {
        String url = tradeService + "/couponRepayMonitor/CouponRepayMonitorCreatePage";
        AdminCouponRepayMonitorCustomizeResponse response = restTemplate.postForEntity(url,form,AdminCouponRepayMonitorCustomizeResponse.class).getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response;
        }
        return null;
    }


    @Override
    public List<AdminCouponRepayMonitorCustomizeVO> selectInterestSum(CouponRepayRequest form) {
        String url = tradeService + "/couponRepayMonitor/selectInterestSum";
        AdminCouponRepayMonitorCustomizeResponse response = restTemplate.postForEntity(url,form,AdminCouponRepayMonitorCustomizeResponse.class).getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response.getResultList();
        }
        return null;
    }

    @Override
    public UserTransferResponse getRecordList(TransferListRequest form) {
        UserTransferResponse response = restTemplate
                .postForEntity(tradeService + "/customertransfer/getRecordList", form, UserTransferResponse.class).getBody();
        if(Response.isSuccess(response)){
            return response;
        }
        return null;
    }

    /**
     * 根据筛选条件查询平台转账count
     * @auth sunpeikai
     * @param request 筛选条件
     * @return
     */
    @Override
    public Integer getPlatformTransferCount(PlatformTransferListRequest request) {
        Integer count = restTemplate.postForEntity(tradeService + "/platformtransfer/getplatformtransfercount", request, Integer.class).getBody();
        return count;
    }

    /**
     * 根据筛选条件查询平台转账list
     * @auth sunpeikai
     * @param request 筛选条件
     * @return
     */
    @Override
    public List<AccountRechargeVO> searchPlatformTransferList(PlatformTransferListRequest request) {
        PlatformTransferResponse response = restTemplate
                .postForEntity(tradeService + "/platformtransfer/searchplatformtransferlist", request, PlatformTransferResponse.class).getBody();
        if(Response.isSuccess(response)){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 获取项目类型list,用于筛选条件展示
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public List<BorrowProjectTypeVO> selectBorrowProjectList(){
        String url = "http://AM-TRADE/am-trade/borrow_regist_exception/select_borrow_project";
        BorrowProjectTypeResponse response = restTemplate.getForEntity(url,BorrowProjectTypeResponse.class).getBody();
        if(response != null){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 获取还款方式list,用于筛选条件展示
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public List<BorrowStyleVO> selectBorrowStyleList(){
        String url = "http://AM-TRADE/am-trade/borrow_regist_exception/select_borrow_style";
        BorrowStyleResponse response = restTemplate.getForEntity(url,BorrowStyleResponse.class).getBody();
        if(response != null){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 获取标的列表count,用于前端分页显示条数
     * @auth sunpeikai
     * @param borrowRegistListRequest 筛选条件
     * @return
     */
    @Override
    public Integer getRegistCount(BorrowRegistListRequest borrowRegistListRequest){
        String url = "http://AM-TRADE/am-trade/borrow_regist_exception/get_regist_count";
        return restTemplate.postForEntity(url,borrowRegistListRequest,Integer.class).getBody();
    }

    /**
     * 获取标的备案异常列表
     * @auth sunpeikai
     * @param borrowRegistListRequest 筛选条件
     * @return
     */
    @Override
    public List <BorrowRegistCustomizeVO> selectBorrowRegistList(BorrowRegistListRequest borrowRegistListRequest){
        String url = "http://AM-TRADE/am-trade/borrow_regist_exception/select_borrow_regist_list";
        BorrowRegistCustomizeResponse response = restTemplate.postForEntity(url,borrowRegistListRequest,BorrowRegistCustomizeResponse.class).getBody();
        if(response != null){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 根据borrowNid查询出来异常标
     * @auth sunpeikai
     * @param borrowNid 借款编号
     * @return
     */
    @Override
    public BorrowVO searchBorrowByBorrowNid(String borrowNid) {
        String url = "http://AM-TRADE/am-trade/borrow_regist_exception/search_borrow_by_borrownid/" + borrowNid;
        BorrowResponse response = restTemplate.getForEntity(url,BorrowResponse.class).getBody();
        if(response != null){
            return response.getResult();
        }
        return null;
    }


    /**
     * 根据受托支付userId查询stAccountId
     * @auth sunpeikai
     * @param entrustedUserId 受托支付userId
     * @return stAccountId
     */
    @Override
    public String getStAccountIdByEntrustedUserId(Integer entrustedUserId) {
        String url = "http://AM-TRADE/am-trade/borrow_regist_exception/get_staccountid_by_entrusteduserid/" + entrustedUserId;
        String response = restTemplate.getForEntity(url,String.class).getBody();
        return response;
    }

    /**
     * 更新标
     * @auth sunpeikai
     * @param borrowVO 标信息
     * @param type 1更新标的备案 2更新受托支付标的备案
     * @return
     */
    @Override
    public boolean updateBorrowRegist(BorrowVO borrowVO,Integer type) {
        String url = "http://AM-TRADE/am-trade/borrow_regist_exception/update_borrowregist_by_type/" + type;
        Boolean response = restTemplate.postForEntity(url,borrowVO,Boolean.class).getBody();
        return response;
    }

    /**
     * 备案成功看标的是否关联计划，如果关联则更新对应资产表
     * @auth sunpeikai
     * @param borrowVO 标信息
     * @return
     */
    @Override
    public boolean updateBorrowAsset(BorrowVO borrowVO, Integer status) {
        String url = "http://AM-TRADE/am-trade/borrow_regist_exception/update_borrowasset/" + status;
        Boolean response = restTemplate.postForEntity(url,borrowVO,Boolean.class).getBody();
        return response;
    }

    /**
     * 更新账户信息
     * @auth sunpeikai
     * @param accountVO 账户信息
     * @return
     */
    @Override
    public Integer updateAccount(AccountVO accountVO) {
        String url = "http://AM-TRADE/am-trade/platformtransfer/updateaccount";
        Integer response = restTemplate.postForEntity(url,accountVO,Integer.class).getBody();
        return response;
    }

    /**
     * 插入数据
     * @auth sunpeikai
     * @param accountRechargeVO 充值表
     * @return
     */
    @Override
    public Integer insertAccountRecharge(AccountRechargeVO accountRechargeVO) {
        String url = "http://AM-TRADE/am-trade/platformtransfer/insertaccountrecharge";
        Integer response = restTemplate.postForEntity(url,accountRechargeVO,Integer.class).getBody();
        return response;
    }

    /**
     * 插入数据
     * @auth sunpeikai
     * @param accountListVO 收支明细
     * @return
     */
    @Override
    public Integer insertAccountList(AccountListVO accountListVO) {
        String url = "http://AM-TRADE/am-trade/platformtransfer/insertaccountlist";
        Integer response = restTemplate.postForEntity(url,accountListVO,Integer.class).getBody();
        return response;
    }

    /**
     * 插入数据
     * @auth sunpeikai
     * @param accountWebListVO 网站收支表
     * @return
     */
    @Override
    public Integer insertAccountWebList(AccountWebListVO accountWebListVO) {
        String url = "http://AM-TRADE/am-trade/platformtransfer/insertaccountlist";
        Integer response = restTemplate.postForEntity(url,accountWebListVO,Integer.class).getBody();
        return response;
    }

    /**
     * 根据账户id查询BankMerchantAccount
     * @auth sunpeikai
     * @param accountId 账户id
     * @return
     */
    @Override
    public BankMerchantAccountVO searchBankMerchantAccountByAccountId(Integer accountId) {
        String url = "http://AM-TRADE/am-trade/platformtransfer/searchbankmerchantaccount/" + accountId;
        BankMerchantAccountResponse response = restTemplate.getForEntity(url,BankMerchantAccountResponse.class).getBody();
        if(response != null){
            return response.getResult();
        }
        return null;
    }

    /**
     * 更新红包账户信息
     * @auth sunpeikai
     * @param bankMerchantAccountVO 红包账户信息
     * @return
     */
    @Override
    public Integer updateBankMerchantAccount(BankMerchantAccountVO bankMerchantAccountVO) {
        String url = "http://AM-TRADE/am-trade/platformtransfer/updatebankmerchantaccount";
        Integer response = restTemplate.postForEntity(url,bankMerchantAccountVO,Integer.class).getBody();
        return response;
    }

    @Override
    public BankMerchantAccountResponse selectBankMerchantAccount(BankMerchantAccountListRequest form) {
        BankMerchantAccountResponse response = restTemplate
                .postForEntity(tradeService + "/bankMerchantAccount/selectBankMerchantAccount", form, BankMerchantAccountResponse.class).getBody();
        if(Response.isSuccess(response)){
            return response;
        }
        return null;
    }

    @Override
    public BankMerchantAccountListCustomizeResponse selectBankMerchantAccountList(BankRedPacketAccountListRequest request) {
        BankMerchantAccountListCustomizeResponse response = restTemplate
                .postForEntity(tradeService + "/bankRedPacketAccount/selectBankMerchantAccountList", request, BankMerchantAccountListCustomizeResponse.class).getBody();
        if(Response.isSuccess(response)){
            return response;
        }
        return null;
    }


    /**
     * 插入数据
     * @auth sunpeikai
     * @param bankMerchantAccountListVO 红包明细表
     * @return
     */
    @Override
    public Integer insertBankMerchantAccountList(BankMerchantAccountListVO bankMerchantAccountListVO) {
        String url = "http://AM-TRADE/am-trade/platformtransfer/insertbankmerchantaccountlist";
        Integer response = restTemplate.postForEntity(url,bankMerchantAccountListVO,Integer.class).getBody();
        return response;
    }

    /**
     * 银行转账异常
     * @auth jijun
     * @param request
     * @return
     */
    @Override
    public AdminTransferExceptionLogResponse getAdminTransferExceptionLogCustomizeList(AdminTransferExceptionLogRequest request) {
        String url = "http://AM-TRADE/am-trade/transferExceptionLog/getRecordList";
        AdminTransferExceptionLogResponse response=restTemplate.postForEntity(url,request,AdminTransferExceptionLogResponse.class).getBody();
        return response;
    }

    /**
     * 银行转账异常
     * @auth jijun
     * @param request
     * @return
     */
    @Override
    public Integer getAdminTransferExceptionLogCustomizeCountRecord(AdminTransferExceptionLogRequest request) {
        String url = "http://AM-TRADE/am-trade/transferExceptionLog/getCountRecord";
        return restTemplate.postForEntity(url,request,Integer.class).getBody();
    }

    /**
     * 更新银行转账信息
     * @auth jijun
     * @param request
     * @return
     */
    @Override
    public int updateTransferExceptionLogByUUID(AdminTransferExceptionLogRequest request) {
        String url = "http://AM-TRADE/am-trade/transferExceptionLog/updateTransferExceptionLogByUUID";
        return restTemplate.postForEntity(url,request,Integer.class).getBody();
    }

    /**
     * 更新银行转账信息
     * @auth jijun
     * @param transferExceptionLog
     * @return
     */
    @Override
    public int updateTransferExceptionLogByUUID(TransferExceptionLogVO transferExceptionLog) {
        String url = "http://AM-TRADE/am-trade/transferExceptionLog/updateTransferExceptionLogByUUID1";
        return restTemplate.postForEntity(url,transferExceptionLog,Integer.class).getBody();
    }

    /**
     * 获取银行转账异常通过uuid
     * @auth jijun
     * @param uuid
     * @return
     */
    @Override
    public TransferExceptionLogVO getTransferExceptionLogByUUID(String uuid) {
        String url = "http://AM-TRADE/am-trade/transferExceptionLog/getTransferExceptionLogByUUID/"+uuid;
        TransferExceptionLogResponse response =restTemplate.getForEntity(url, TransferExceptionLogResponse.class).getBody();
        if (Validator.isNotNull(response)){
            return response.getResult();
        }
        return null;
    }

    /**
     * 获取发起账户分佣所需的详细信息
     * @auth sunpeikai
     * @param
     * @return
     */
    @Override
    public List<SubCommissionListConfigVO> searchSubCommissionListConfig() {
        String url = "http://AM-TRADE/am-trade/subcommission/searchsubcommissionlistconfig";
        SubCommissionListConfigResponse response = restTemplate.getForEntity(url,SubCommissionListConfigResponse.class).getBody();
        if(response != null){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 插入数据
     * @auth sunpeikai
     * @param subCommissionVO 平台账户分佣
     * @return
     */
    @Override
    public boolean insertSubCommission(SubCommissionVO subCommissionVO) {
        String url = "http://AM-TRADE/am-trade/subcommission/insertsubcommission";
        Boolean response = restTemplate.postForEntity(url,subCommissionVO,Boolean.class).getBody();
        return response;
    }

    /**
     * 根据订单号查询分佣数据
     * @auth sunpeikai
     * @param orderId 订单号
     * @return
     */
    @Override
    public SubCommissionVO searchSubCommissionByOrderId(String orderId) {
        String url = "http://AM-TRADE/am-trade/subcommission/searchsubcommissionbyorderid/" + orderId;
        SubCommissionResponse response = restTemplate.getForEntity(url,SubCommissionResponse.class).getBody();
        if(response != null){
            return response.getResult();
        }
        return null;
    }

    /**
     * 更新分佣数据
     * @auth sunpeikai
     * @param subCommissionVO 待更新的数据参数
     * @return
     */
    @Override
    public Integer updateSubCommission(SubCommissionVO subCommissionVO) {
        String url = "http://AM-TRADE/am-trade/subcommission/updatesubcommission";
        Integer response = restTemplate.postForEntity(url,subCommissionVO,Integer.class).getBody();
        return response;
    }

    /**
     * 根据订单号查询是否存在重复的AccountWebList数据
     * @auth sunpeikai
     * @param orderId 订单号
     * @return
     */
    @Override
    public Integer accountWebListByOrderId(String orderId) {
        String url = "http://AM-TRADE/am-trade/subcommission/accountweblistbyorderid/" + orderId;
        Integer response = restTemplate.getForEntity(url,Integer.class).getBody();
        return response;
    }

    /**
     * 根据筛选条件查询分佣数据count
     * @auth sunpeikai
     * @param request 筛选条件
     * @return
     */
    @Override
    public Integer getSubCommissionCount(SubCommissionRequest request) {
        String url = "http://AM-TRADE/am-trade/subcommission/getsubcommissioncount";
        Integer response = restTemplate.postForEntity(url,request,Integer.class).getBody();
        return response;
    }

    /**
     * 根据筛选条件查询分佣数据list
     * @auth sunpeikai
     * @param request 筛选条件
     * @return
     */
    @Override
    public List<SubCommissionVO> searchSubCommissionList(SubCommissionRequest request) {
        String url = "http://AM-TRADE/am-trade/subcommission/searchsubcommissionlist";
        SubCommissionResponse response = restTemplate.postForEntity(url,request,SubCommissionResponse.class).getBody();
        if(response != null){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 根据id删除冻结记录
     * @auther: hesy
     * @date: 2018/7/11
     */
    @Override
    public Integer deleteFreezeLogById(Integer id){
        String url = "http://AM-TRADE/am-trade/repayfreezelog/deleteby_id/" + id;
        return restTemplate.getForEntity(url, Integer.class).getBody();
    }

    /**
     * 根据orderId获取冻结记录
     * @auther: hesy
     * @date: 2018/7/11
     */
    @Override
    public BankRepayFreezeLogVO getBankFreezeLogByOrderId(String orderId){
        String url = "http://AM-TRADE/am-trade/repayfreezelog/get_logvalid_byorderid/" + orderId;
        BankRepayFreezeLogResponse response = restTemplate.getForEntity(url,BankRepayFreezeLogResponse.class).getBody();
        if(response != null){
            return response.getResult();
        }
        return null;
    }

    /**
     * 分页获取所有有效的冻结记录
     * @auther: hesy
     * @date: 2018/7/11
     */
    @Override
    public List<BankRepayFreezeLogVO> getFreezeLogValidAll(Integer limitStart, Integer limitEnd){
        Map<String,Integer> params = new HashMap<>();
        params.put("limitStart",limitStart);
        params.put("limitEnd",limitEnd);
        String url = "http://AM-TRADE/am-trade/repayfreezelog/get_logvalid_all";
        BankRepayFreezeLogResponse response = restTemplate.postForEntity(url,params,BankRepayFreezeLogResponse.class).getBody();
        if(response != null){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 有效冻结记录总数
     * @auther: hesy
     * @date: 2018/7/11
     */
    @Override
    public Integer getFreezeLogValidAllCount(){
        String url = "http://AM-TRADE/am-trade/repayfreezelog/get_logvalid_all_count";
        return restTemplate.getForEntity(url, Integer.class).getBody();
    }



    /**
     * 根据筛选条件查询银行投资撤销异常的数据count
     * @auth sunpeikai
     * @param request 筛选条件
     * @return
     */
    @Override
    public Integer getTenderCancelExceptionCount(TenderCancelExceptionRequest request) {
        String url = "http://AM-TRADE/am-trade/tendercancelexception/gettendercancelexceptioncount";
        Integer response = restTemplate.postForEntity(url,request,Integer.class).getBody();
        return response;
    }

    /**
     * 根据筛选条件查询银行投资撤销异常list
     * @auth sunpeikai
     * @param request 筛选条件
     * @return
     */
    @Override
    public List<BorrowTenderTmpVO> searchTenderCancelExceptionList(TenderCancelExceptionRequest request) {
        String url = "http://AM-TRADE/am-trade/tendercancelexception/searchtendercancelexceptionlist";
        BorrowTenderTmpResponse response = restTemplate.postForEntity(url,request,BorrowTenderTmpResponse.class).getBody();
        if(response != null){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 根据orderId查询BorrowTender
     * @auth sunpeikai
     * @param orderId 订单号
     * @return
     */
    @Override
    public List<BorrowTenderVO> searchBorrowTenderByOrderId(String orderId) {
        String url = "http://AM-TRADE/am-trade/tendercancelexception/searchborrowtenderbyorderid/" + orderId;
        BorrowTenderResponse response = restTemplate.getForEntity(url,BorrowTenderResponse.class).getBody();
        if(response != null){
            return response.getResultList();
        }
        return null;
    }

    /**
     * 根据orderId查询BorrowTenderTmp
     * @auth sunpeikai
     * @param orderId 订单号
     * @return
     */
    @Override
    public BorrowTenderTmpVO searchBorrowTenderTmpByOrderId(String orderId) {
        String url = "http://AM-TRADE/am-trade/tendercancelexception/searchborrowtendertmpbyorderid/" + orderId;
        BorrowTenderTmpResponse response = restTemplate.getForEntity(url,BorrowTenderTmpResponse.class).getBody();
        if(response != null){
            return response.getResult();
        }
        return null;
    }

    /**
     * 根据id删除BorrowTenderTmp
     * @auth sunpeikai
     * @param id 主键
     * @return
     */
    @Override
    public Integer deleteBorrowTenderTmpById(Integer id) {
        String url = "http://AM-TRADE/am-trade/tendercancelexception/deleteborrowtendertmpbyid/" + id;
        Integer response = restTemplate.getForEntity(url,Integer.class).getBody();
        return response;
    }

    /**
     * 插入数据
     * @auth sunpeikai
     * @param freezeHistoryVO 冻结历史
     * @return
     */
    @Override
    public Integer insertFreezeHistory(FreezeHistoryVO freezeHistoryVO) {
        String url = "http://AM-TRADE/am-trade/tendercancelexception/insertfreezehistory";
        Integer response = restTemplate.postForEntity(url,freezeHistoryVO,Integer.class).getBody();
        return response;
    }


    /**
     * 转账成功后续处理
     * @auth jijun
     * @param jsonObject
     * @return
     */
    @Override
    public boolean transferAfter(JSONObject jsonObject) {
        String url = "http://AM-TRADE/am-trade/transferExceptionLog/transferAfter";
        return restTemplate.postForEntity(url,jsonObject,Boolean.class).getBody();
    }



    /**
     * 根据主键获取优惠券还款记录
     * @auth jijun
     * @return
     */
    @Override
    public CouponRecoverVO getCouponRecoverByPrimaryKey(Integer id) {
        String url = "http://AM-TRADE/am-trade/coupon/getCouponRecoverByPrimaryKey/"+id;
        CouponRecoverResponse response=restTemplate.getForEntity(url,CouponRecoverResponse.class).getBody();
        if (Validator.isNotNull(response)){
            return response.getResult();
        }
        return null;
    }

    /**
     * 取得优惠券投资信息
     * @auth jijun
     * @param nid
     * @return
     */
    @Override
    public BorrowTenderCpnVO getCouponTenderInfoByNid(String nid) {
        String url="http://AM-TRADE/am-trade/coupon/getCouponTenderInfoByNid/"+nid;
        BorrowTenderCpnResponse response = restTemplate.getForEntity(url,BorrowTenderCpnResponse.class).getBody();
        if (response != null) {
            return response.getResult();
        }
        return null;
    }


    /**
     * 查询批次中心的批次列表求和
     * @param request
     * @return
     */
    @Override
    public BatchBorrowRecoverReponse getBatchBorrowCenterListSum(BatchBorrowRecoverRequest request) {
        BatchBorrowRecoverReponse response =  restTemplate.
                postForEntity(tradeService + "/adminBatchBorrowRecover/getListSum", request, BatchBorrowRecoverReponse.class).
                getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response;
        }
        return null;
    }

    /**
     * 根据筛选条件查询汇付对账count
     * @auth sunpeikai
     * @param request 筛选条件
     * @return
     */
    @Override
    public Integer getAccountExceptionCount(AccountExceptionRequest request) {
        String url="http://AM-TRADE/am-trade/accountexception/getaccountexceptioncount";
        Integer count = restTemplate.postForEntity(url,request,Integer.class).getBody();
        return count;
    }

    /**
     * 根据筛选条件查询汇付对账列表
     * @auth sunpeikai
     * @param request 筛选条件
     * @return
     */
    @Override
    public List<AccountExceptionVO> searchAccountExceptionList(AccountExceptionRequest request) {
        String url="http://AM-TRADE/am-trade/accountexception/searchaccountexceptionlist";
        AccountExceptionResponse response = restTemplate.postForEntity(url,request,AccountExceptionResponse.class).getBody();
        if (Response.isSuccess(response)) {
            return response.getResultList();
        }
        return null;
    }

    /**
     * 根据id查询AccountException
     * @auth sunpeikai
     * @param id 主键
     * @return
     */
    @Override
    public AccountExceptionVO searchAccountExceptionById(Integer id) {
        String url="http://AM-TRADE/am-trade/accountexception/searchaccountexceptionbyid/" + id;
        AccountExceptionResponse response = restTemplate.getForEntity(url,AccountExceptionResponse.class).getBody();
        if (Response.isSuccess(response)) {
            return response.getResult();
        }
        return null;
    }

    /**
     * 更新AccountException
     * @auth sunpeikai
     * @param accountExceptionVO 更新参数
     * @return
     */
    @Override
    public Integer updateAccountException(AccountExceptionVO accountExceptionVO) {
        String url="http://AM-TRADE/am-trade/accountexception/updateaccountexception";
        Integer response = restTemplate.postForEntity(url,accountExceptionVO,Integer.class).getBody();
        return response;
    }

    /**
     * 根据id删除AccountException
     * @auth sunpeikai
     * @param id 主键
     * @return
     */
    @Override
    public Integer deleteAccountExceptionById(Integer id) {
        String url="http://AM-TRADE/am-trade/accountexception/deleteaccountexceptionbyid/" + id;
        Integer response = restTemplate.getForEntity(url,Integer.class).getBody();
        return response;
    }


    /**
     * 获取提现列表数量
     * @param request
     * @return
     */
    @Override
    public int getWithdrawRecordCount(WithdrawBeanRequest request) {
        String url = "http://AM-TRADE/am-trade/accountWithdraw/getWithdrawRecordCount";
        return restTemplate.postForEntity(url,request,Integer.class).getBody();
    }

    /**
     * 获取提现列表
     * @param request
     * @return
     */
    @Override
    public WithdrawCustomizeResponse getWithdrawRecordList(WithdrawBeanRequest request) {
        String url = "http://AM-TRADE/am-trade/accountWithdraw/getWithdrawRecordList";
        WithdrawCustomizeResponse response=restTemplate.postForEntity(url,request,WithdrawCustomizeResponse.class).getBody();
        return response;
    }

    /**
     * 结束债权列表
     * @auther: hesy
     * @date: 2018/7/12
     */
    @Override
    public List<BankCreditEndVO> getCreditEndList(BankCreditEndListRequest requestBean) {
        String url = "http://AM-TRADE/am-trade/bankCreditEndController/getlist";
        BankCreditEndResponse response=restTemplate.postForEntity(url,requestBean,BankCreditEndResponse.class).getBody();
        if (Validator.isNotNull(response)){
            response.getResultList();
        }
        return null;
    }

    /**
     * 结束债权总记录数
     * @auther: hesy
     * @date: 2018/7/12
     */
    @Override
    public int getCreditEndCount(BankCreditEndListRequest requestBean) {
        String url = "http://AM-TRADE/am-trade/bankCreditEndController/getcount";
        return restTemplate.postForEntity(url,requestBean,Integer.class).getBody();
    }

    /**
     * 根据orderId获取
     * @auther: hesy
     * @date: 2018/7/12
     */
    @Override
    public BankCreditEndVO getCreditEndByOrderId(String orderId) {
        String url = "http://AM-TRADE/am-trade/bankCreditEndController/getby_orderid" + orderId;
        BankCreditEndResponse response=restTemplate.getForEntity(url,BankCreditEndResponse.class).getBody();
        if (Validator.isNotNull(response)){
            response.getResult();
        }
        return null;
    }

    /**
     * 更新结束债权记录
     * @auther: hesy
     * @date: 2018/7/12
     */
    @Override
    public int updateBankCreditEnd(BankCreditEndVO requestBean) {
        String url = "http://AM-TRADE/am-trade/bankCreditEndController/update";
        return restTemplate.postForEntity(url,requestBean,Integer.class).getBody();
    }

    /**
     * 批次恢复为初始状态
     * @auther: hesy
     * @date: 2018/7/12
     */
    @Override
    public int updateCreditEndForInitial(BankCreditEndVO requestBean) {
        String url = "http://AM-TRADE/am-trade/bankCreditEndController/update_initial";
        return restTemplate.postForEntity(url,requestBean,Integer.class).getBody();
    }

    /**
     * yangchangwei
     * 根据Borrownid 获取放款任务表
     * @param id
     * @return
     */
    @Override
    public BorrowApicronResponse getBorrowApicronByID(String id) {
        BorrowApicronResponse response = restTemplate.
                postForEntity(tradeService + "/adminBatchBorrowRecover/getRecoverApicronByID", id, BorrowApicronResponse.class).
                getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response;
        }
        return null;
    }

    /**
     * 根据creditNid查询债转信息
     * @auther: hesy
     * @date: 2018/7/12
     */
    @Override
    public HjhDebtCreditVO selectHjhDebtCreditByCreditNid(String creditNid) {
        String url = tradeService + "hjhDebtCredit/selectHjhDebtCreditByCreditNid/" + creditNid;
        HjhDebtCreditResponse response = restTemplate.getForEntity(url, HjhDebtCreditResponse.class).getBody();
        if (response != null) {
            return response.getResult();
        }
        return null;
    }

    /**
     * 银行结束债权后，更新债权表为完全承接
     * @auther: hesy
     * @date: 2018/7/12
     */
    @Override
    public int updateHjhDebtCreditForEnd(HjhDebtCreditVO hjhDebtCreditVO) {
        String url = tradeService + "hjhDebtCredit/updateHjhDebtCreditByPK";
        hjhDebtCreditVO.setCreditStatus(2);//转让状态 2完全承接
        hjhDebtCreditVO.setIsLiquidates(1);
        Response<Integer> response = restTemplate.postForEntity(url, hjhDebtCreditVO, Response.class).getBody();
        if (!Response.isSuccess(response)) {
            return 0;
        }
        return response.getResult().intValue();
    }

    /**
     * 请求结束债权（追加结束债权任务记录）
     * @auther: hesy
     * @date: 2018/7/12
     */
    @Override
    public int requestDebtEnd(HjhDebtCreditVO credit, String tenderAccountId, String tenderAuthCode) {
        String url = tradeService + "bankCreditEndController/insertBankCreditEndForCreditEnd";
        InsertBankCreditEndForCreditEndRequest request = new InsertBankCreditEndForCreditEndRequest(credit, tenderAccountId, tenderAuthCode);
        Response<Integer> response = restTemplate.postForEntity(url, request, Response.class).getBody();
        if (response == null || !Response.isSuccess(response)) {
            return 0;
        }
        return response.getResult().intValue();
    }

    /**
     *根据nid获取BorrowTender
     * @auther: hesy
     * @date: 2018/7/13
     */
    @Override
    public BorrowTenderVO getBorrowTenderByNid(String nid) {
        String url = "http://AM-TRADE/am-trade/borrowTender/getBorrowTenderByNid/"+nid;
        BorrowTenderResponse response = restTemplate.getForEntity(url,BorrowTenderResponse.class).getBody();
        if (Validator.isNotNull(response)){
            return response.getResult();
        }
        return null;
    }

    /**
     * 根据assignOrderId获取
     * @auther: hesy
     * @date: 2018/7/13
     */
    @Override
    public HjhDebtCreditTenderVO getByAssignOrderId(String assignOrderId){
        String url = "http://AM-TRADE/am-trade/hjhDebtCreditTender/getby_assignorderid/"+assignOrderId;
        HjhDebtCreditTenderResponse response = restTemplate.getForEntity(url,HjhDebtCreditTenderResponse.class).getBody();
        if (Validator.isNotNull(response)){
            return response.getResult();
        }
        return null;
    }
    /**
     * 检索汇计划加入明细列表
     * @param request
     * @return
     */
    @Override
    public AutoTenderExceptionResponse selectAccedeRecordList(AutoTenderExceptionRequest request){
        String url ="http://AM-TRADE/am-trade/autotenderexception/selectAccedeRecordList";
        AutoTenderExceptionResponse response =  restTemplate.
                postForEntity(url, request, AutoTenderExceptionResponse.class).
                getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response;
        }
        return null;
    }
    /**
     * 查询计划加入明细
     * @auther: nxl
     * @date: 2018/7/12
     * @param tenderExceptionSolveRequest
     * @return
     */
    @Override
    public HjhAccedeResponse selectHjhAccedeByParam(TenderExceptionSolveRequest tenderExceptionSolveRequest){
        String url ="http://AM-TRADE/am-trade/autotenderexception/selectHjhAccedeByParam";
        HjhAccedeResponse response =  restTemplate.
                postForEntity(url, tenderExceptionSolveRequest, HjhAccedeResponse.class).
                getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response;
        }
        return null;
    }
    /**
     * 查询计划加入明细临时表
     * @auther: nxl
     * @date: 2018/7/12
     * @param tenderExceptionSolveRequest
     * @return
     */
    @Override
    public HjhPlanBorrowTmpResponse selectBorrowJoinList(TenderExceptionSolveRequest tenderExceptionSolveRequest){
        String url ="http://AM-TRADE/am-trade/autotenderexception/selectBorrowJoinList";
        HjhPlanBorrowTmpResponse response =  restTemplate.
                postForEntity(url, tenderExceptionSolveRequest, HjhPlanBorrowTmpResponse.class).
                getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response;
        }
        return null;
    }
    /**
     * 更改加入明细状态
     * @param status
     * @param accedeId
     * @return
     */
    @Override
    public boolean updateTenderByParam(int status,int accedeId){
        String url ="http://AM-TRADE/am-trade/autotenderexception/updateTenderByParam/"+status+"/"+accedeId;
        Response<Boolean> response = restTemplate.getForEntity(url,Response.class).getBody();
        if (response != null && response.getResult()) {
            return true;
        }
        return false;
    }
    /**
     * 更新投资数据
     *
     * @return
     * @author nxl
     */
    @Override
    public boolean updateBorrowForAutoTender(BorrowVO borrow, HjhAccedeVO hjhAccede, BankCallBean bean) {
        String url = "http://AM-TRADE/am-trade/autoTenderController/updateBorrowForAutoTender";
        BankCallBeanVO bankCallBeanVO = new BankCallBeanVO();
        BeanUtils.copyProperties(bean, bankCallBeanVO);
        UpdateBorrowForAutoTenderRequest request = new UpdateBorrowForAutoTenderRequest(borrow, hjhAccede, bankCallBeanVO);
        Response response = restTemplate.postForEntity(url, request, Response.class).getBody();
        if (!Response.isSuccess(response)) {
            logger.error("[" + hjhAccede.getAccedeOrderId() + "] 银行自动投资成功后，更新投资数据失败。");
            throw new RuntimeException("银行自动投资成功后，更新投资数据失败。");
        }
        return true;
    }

    /**
     * 结束债权列表
     * @auther: hesy
     * @date: 2018/7/12
     */
    @Override
    public List<ManualReverseCustomizeVO> getManualReverseList(ManualReverseCustomizeRequest requestBean) {
        String url = "http://AM-TRADE/am-trade/manualreverse/getlist";
        ManualReverseCustomizeResponse response=restTemplate.postForEntity(url,requestBean,ManualReverseCustomizeResponse.class).getBody();
        if (Validator.isNotNull(response)){
            response.getResultList();
        }
        return null;
    }

    /**
     * 结束债权总记录数
     * @auther: hesy
     * @date: 2018/7/12
     */
    @Override
    public int getManualReverseCount(ManualReverseCustomizeRequest requestBean) {
        String url = "http://AM-TRADE/am-trade/manualreverse/getcount";
        return restTemplate.postForEntity(url,requestBean,Integer.class).getBody();
    }

    /**
     * 手动冲正更新
     * @auther: hesy
     * @date: 2018/7/14
     */
    @Override
    public Boolean updateManualReverse(ManualReverseAddRequest requestBean) {
        String url = "http://AM-TRADE/am-trade/manualreverse/update_manualreverse";
        return restTemplate.postForEntity(url,requestBean,Boolean.class).getBody();
    }



    /**
     * 分页查询平台设置账户列表
     * @return
     */
    @Override
    public MerchantAccountResponse selectMerchantAccountListByPage(AdminMerchantAccountRequest request){
        String url="http://AM-TRADE/am-trade/config/accountconfig/selectMerchantAccountListByPage";
        MerchantAccountResponse response = restTemplate.
                postForEntity(url, request, MerchantAccountResponse.class).
                getBody();
        List paramList =getParamNameList(CustomConstants.SUB_ACCOUNT_CLASS);
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            if(!CollectionUtils.isEmpty(paramList)){
                response.setParamNameList(paramList);
            }
            return response;
        }
        return null;
    }
    /**
     * 根据id查询账户平台设置
     * @return
     */
    @Override
    public MerchantAccountResponse searchAccountConfigInfo(Integer id){
        String url="http://AM-TRADE/am-trade/config/accountconfig/searchAccountConfigInfo";
        MerchantAccountResponse response = restTemplate.
                postForEntity(url, id, MerchantAccountResponse.class).
                getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response;
        }
        return null;
    }
    /**
     * 添加账户平台设置
     * @return
     */
    @Override
    public MerchantAccountResponse saveAccountConfig(AdminMerchantAccountRequest request){
        String url="http://AM-TRADE/am-trade/config/accountconfig/saveAccountConfig";
        MerchantAccountResponse response = restTemplate.
                postForEntity(url, request, MerchantAccountResponse.class).
                getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response;
        }
        return null;
    }
    /**
     * 修改账户平台设置
     * @return
     */
    @Override
    public MerchantAccountResponse updateAccountConfig(AdminMerchantAccountRequest request){
        String url="http://AM-TRADE/am-trade/config/accountconfig/updateAccountConfig";
        MerchantAccountResponse response = restTemplate.
                postForEntity(url, request, MerchantAccountResponse.class).
                getBody();
        if (response != null && Response.SUCCESS.equals(response.getRtn())) {
            return response;
        }
        return null;
    }

    /**
     * 子账户类型 查询
     * @return
     */
    @Override
    public List<ParamName> getParamNameList(String code){
        String url="http://AM-CONFIG/am-config/config/accountconfig/getParamNameList";
        List<ParamName>  response = restTemplate.postForEntity(url, code, List.class).getBody();
        if (!CollectionUtils.isEmpty(response)){
            return response;
        }
        return null;
    }

    /**
     *
     * 根据子账户名称检索
     * @param subAccountName
     * @return
     */
    @Override
    public int countAccountListInfoBySubAccountName(String ids, String subAccountName){
        Map map =new HashMap();
        map.put("ids",ids);
        map.put("subAccountName",subAccountName);
        return restTemplate.
                postForEntity("http://AM-TRADE/am-trade/config/accountconfig/countAccountListInfoBySubAccountName", map, Integer.class).getBody();
    }

    /**
     *
     * 根据子账户代号检索
     * @param subAccountCode
     * @return
     */
    @Override
    public int countAccountListInfoBySubAccountCode(String ids, String subAccountCode){
        Map map =new HashMap();
        map.put("ids",ids);
        map.put("subAccountName",subAccountCode);
        return restTemplate.
                postForEntity("http://AM-TRADE/am-trade/config/accountconfig/countAccountListInfoBySubAccountCode", map, Integer.class).getBody();
    }
}
