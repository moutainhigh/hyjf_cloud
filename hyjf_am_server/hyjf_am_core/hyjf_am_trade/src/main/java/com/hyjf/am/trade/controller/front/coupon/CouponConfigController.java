/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.controller.front.coupon;

import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.CouponConfigCustomizeResponse;
import com.hyjf.am.response.admin.CouponRecoverResponse;
import com.hyjf.am.response.admin.TransferExceptionLogResponse;
import com.hyjf.am.response.trade.CouponConfigResponse;
import com.hyjf.am.response.trade.CouponTenderCustomizeResponse;
import com.hyjf.am.response.trade.coupon.CouponResponse;
import com.hyjf.am.resquest.admin.CouponConfigRequest;
import com.hyjf.am.resquest.trade.TransferExceptionLogWithBLOBsVO;
import com.hyjf.am.trade.controller.BaseController;
import com.hyjf.am.trade.dao.model.auto.CouponConfig;
import com.hyjf.am.trade.dao.model.customize.trade.CouponConfigCustomize;
import com.hyjf.am.trade.service.front.coupon.CouponConfigService;
import com.hyjf.am.vo.admin.CouponConfigCustomizeVO;
import com.hyjf.am.vo.admin.coupon.CouponRecoverVO;
import com.hyjf.am.vo.trade.TransferExceptionLogVO;
import com.hyjf.am.vo.trade.account.AccountVO;
import com.hyjf.am.vo.trade.coupon.CouponConfigVO;
import com.hyjf.am.vo.trade.coupon.CouponTenderCustomizeVO;
import com.hyjf.common.paginator.Paginator;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.common.util.GetDate;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yaoy
 * @version CouponConfigController, v0.1 2018/6/19 19:20
 */
@RestController
@RequestMapping("/am-trade/couponConfig")
public class CouponConfigController extends BaseController {
    @Autowired
    private CouponConfigService couponConfigService;

    /**
     * 根据优惠券编码查询优惠券配置信息
     * @param couponCode
     * @return
     */
    @RequestMapping("/selectCouponConfig/{couponCode}")
    public CouponConfigResponse selectCouponConfig(@PathVariable String couponCode) {
        CouponConfigResponse response = new CouponConfigResponse();
        CouponConfig couponConfig = couponConfigService.selectCouponConfig(couponCode);
        if (couponConfig != null) {
            CouponConfigVO couponConfigVO = new CouponConfigVO();
            BeanUtils.copyProperties(couponConfig, couponConfigVO);
            response.setResult(couponConfigVO);
        }
        return response;
    }

    /**
     * 后台admin优惠券发行列表
     *
     * @param couponConfigRequest
     * @return
     */
    @PostMapping("/getRecordList")
    public CouponConfigCustomizeResponse getRecordList(@RequestBody @Valid CouponConfigRequest couponConfigRequest) {
        CouponConfigCustomizeResponse response = new CouponConfigCustomizeResponse();
        String returnCode = Response.FAIL;
        Map<String, Object> mapParam = paramSet(couponConfigRequest);
        int recordTotal = couponConfigService.countRecord(mapParam);
        if (recordTotal > 0) {
            Paginator paginator = new Paginator(couponConfigRequest.getPaginatorPage(), recordTotal);
            List<CouponConfigCustomize> recordList = couponConfigService.getRecordList(mapParam, paginator.getOffset(), paginator.getLimit());
            if (!CollectionUtils.isEmpty(recordList)) {
                List<CouponConfigCustomizeVO> cvo = CommonUtils.convertBeanList(recordList, CouponConfigCustomizeVO.class);
                response.setResultList(cvo);
                response.setCount(recordTotal);
                response.setRtn(Response.SUCCESS);
            }
            return response;
        }
        return new CouponConfigCustomizeResponse();
    }

    /**
     * 通过id查询优惠券信息
     * @param couponConfigRequest
     * @return
     */
    @PostMapping("/getCouponConfig")
    public CouponConfigResponse getCouponConfig(@RequestBody @Valid CouponConfigRequest couponConfigRequest) {
        CouponConfigResponse ccr = new CouponConfigResponse();
        if (!StringUtils.isEmpty(couponConfigRequest.getId())) {
            CouponConfig ccf = couponConfigService.getCouponConfig(Integer.parseInt(couponConfigRequest.getId()));
            if (ccf != null) {
                CouponConfigVO configVO = new CouponConfigVO();
                BeanUtils.copyProperties(ccf, configVO);
                ccr.setResult(configVO);
            }
            return ccr;
        }
        return ccr;
    }


    /**
     * 保存优惠券配置信息
     * @param configRequest
     * @return
     */
    @PostMapping("/saveCouponConfig")
    public CouponConfigResponse saveCouponConfig(@RequestBody @Valid CouponConfigRequest configRequest) {
        CouponConfigResponse ccr = new CouponConfigResponse();
        try {
            if (StringUtils.isNotEmpty(configRequest.getId())) {
                CouponConfig couponConfig = new CouponConfig();
                BeanUtils.copyProperties(configRequest, couponConfig);
                Map<String, Object> resultMap = couponConfigService.saveCouponConfig(couponConfig);
                if ((Boolean) resultMap.get("success")) {
                    return ccr;
                } else {
                    ccr.setRtn("failed");
                    ccr.setMessage((String) resultMap.get("msg"));
                    return ccr;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ccr.setMessage("校验失败");
        return ccr;
    }


    /**
     * 插入优惠券配置信息
     * @param couponConfigRequest
     * @return
     */
    @PostMapping("/insertCouponConfig")
    public CouponConfigResponse insertAction(@RequestBody @Valid CouponConfigRequest couponConfigRequest) {
        CouponConfigResponse ccr = new CouponConfigResponse();
        try {
            CouponConfig couponConfig = new CouponConfig();
            BeanUtils.copyProperties(couponConfigRequest, couponConfig);
            Map<String, Object> resultMap = couponConfigService.insertAction(couponConfig);
            if ((Boolean) resultMap.get("success")) {
                return ccr;
            } else {
                ccr.setRtn("failed");
                ccr.setMessage((String) resultMap.get("msg"));
                return ccr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ccr.setMessage("校验失败");
        return ccr;
    }

    /**
     * 根据id删除优惠券信息
     * @param couponConfigRequest
     * @return
     */
    @PostMapping("/deleteCouponConfig")
    public CouponConfigResponse deleteAction(@RequestBody @Valid CouponConfigRequest couponConfigRequest) {
        CouponConfigResponse response = new CouponConfigResponse();
        try {
            if (StringUtils.isNotEmpty(couponConfigRequest.getId())) {
                Map<String, Object> resultMap = couponConfigService.deleteCouponConfig(Integer.parseInt(couponConfigRequest.getId()));
                if ((Boolean) resultMap.get("success")) {
                    return response;
                } else {
                    response.setRtn("failed");
                    response.setMessage((String) resultMap.get("msg"));
                    return response;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setMessage("校验失败");
        return response;
    }

    /**
     * 根据id获取要修改的优惠券信息
     * @param couponConfigRequest
     * @return
     */
    @PostMapping("/getAuditInfo")
    public CouponConfigResponse getAuditInfo(@RequestBody @Valid CouponConfigRequest couponConfigRequest) {
        CouponConfigResponse ccr = new CouponConfigResponse();
        if (!StringUtils.isEmpty(couponConfigRequest.getId())) {
            CouponConfig ccf = couponConfigService.getCouponConfig(Integer.parseInt(couponConfigRequest.getId()));
            if (ccf != null) {
                CouponConfigVO configVO = new CouponConfigVO();
                BeanUtils.copyProperties(ccf, configVO);
                ccr.setResult(configVO);
            }
            return ccr;
        }
        return ccr;
    }


    /**
     * 修改优惠券信息
     * @param request
     * @return
     */
    @PostMapping("/updateAuditInfo")
    public CouponConfigResponse updateAuditInfo(@RequestBody @Valid CouponConfigRequest request) {
        CouponConfigResponse configResponse = new CouponConfigResponse();
        CouponConfig couponConfig = new CouponConfig();
        long nowTime = System.currentTimeMillis() / 1000;
        couponConfig.setAuditUser(request.getAuditUser());
        couponConfig.setUpdateUserId(Integer.parseInt(request.getAuditUser()));
        couponConfig.setAuditTime((int)nowTime);
        couponConfig.setUpdateTime(GetDate.getTimestamp(nowTime));
        Map<String,Object> map = couponConfigService.saveCouponConfig(couponConfig);
        if ((Boolean) map.get("success")) {
            return configResponse;
        } else {
            configResponse.setRtn("failed");
            configResponse.setMessage((String) map.get("msg"));
            return configResponse;
        }
    }


    /**
     * 根据优惠券编号查询已发行数量
     * @param couponCode
     * @return
     */
    @GetMapping("/checkCouponSendExcess/{couponCode}")
    public CouponConfigCustomizeResponse checkCouponSendExcess(@PathVariable String couponCode) {
        CouponConfigCustomizeResponse response = new CouponConfigCustomizeResponse();
        int remain = couponConfigService.checkCouponSendExcess(couponCode);
        if (remain > 0) {
            response.setCount(remain);
            return response;
        }
        response.setMessage(Response.FAIL_MSG);
        return response;
    }


    /**
     * 查询条件设置
     *
     * @param couponConfigRequest
     * @return
     */
    private Map<String, Object> paramSet(CouponConfigRequest couponConfigRequest) {
        Map<String, Object> map = new HashMap<>();
        map.put("couponName", couponConfigRequest.getCouponName());
        map.put("couponCode", couponConfigRequest.getCouponCode());
        map.put("couponType", couponConfigRequest.getCouponType());
        map.put("status", couponConfigRequest.getStatus());
        map.put("timeStartSrch", couponConfigRequest.getTimeStartSrch());
        map.put("timeEndSrch", couponConfigRequest.getTimeEndSrch());
        return map;
    }

    /**
     * @Author walter.limeng
     * @Description  根据订单ID查询对象
     * @Date 14:19 2018/7/17
     * @Param ordId
     * @return 
     */
    @RequestMapping("/getcouponconfigbyorderid/{ordId}")
    public CouponConfigResponse getCouponConfigByOrderId(@PathVariable String ordId) {
        CouponConfigResponse response = new CouponConfigResponse();
        CouponConfigVO couponConfigVO = couponConfigService.getCouponConfigByOrderId(ordId);
        response.setResult(couponConfigVO);
        return response;
    }

    /**
     * @Author walter.limeng
     * @Description  更新还款期
     * @Date 14:15 2018/7/17
     * @Param tenderNid 投资订单编号
     * @Param currentRecoverFlg 0:非还款期，1;还款期
     * @Param period 期数
     * @return
     */
    @RequestMapping("/crrecoverperiod/{tenderNid}/{currentRecoverFlg}/{period}")
    public CouponResponse crRecoverPeriod(@PathVariable String tenderNid,@PathVariable Integer currentRecoverFlg,@PathVariable Integer period) {
        CouponResponse response = new CouponResponse();
        Integer count = couponConfigService.crRecoverPeriod(tenderNid,currentRecoverFlg,period);
        response.setTotalRecord(count);
        return response;
    }
    
    /**
     * @Author walter.limeng
     * @Description  根据nid 取得体验金收益期限
     * @Date 14:33 2018/7/17
     * @Param tenderNid
     * @return 
     */
    @RequestMapping("/getcouponprofittime/{tenderNid}")
    public CouponConfigResponse getCouponProfitTime(@PathVariable String tenderNid) {
        CouponConfigResponse response = new CouponConfigResponse();
        Integer count = couponConfigService.getCouponProfitTime(tenderNid);
        response.setCount(count);
        return response;
    }

    /**
     * @Author walter.limeng
     * @Description  保存CouponRecover
     * @Date 14:44 2018/7/17
     * @Param CouponRecoverVO
     * @return
     */
    @RequestMapping("/insertcouponrecover")
    public CouponConfigResponse insertCouponRecover(@RequestBody CouponRecoverVO cr) {
        CouponConfigResponse response = new CouponConfigResponse();
        Integer count = couponConfigService.insertCouponRecover(cr);
        response.setCount(count);
        return response;
    }

    /**
     * @Author walter.limeng
     * @Description  保存CouponRecover
     * @Date 14:44 2018/7/17
     * @Param CouponRecoverVO
     * @return
     */
    @RequestMapping("/updateloanstenderhjh")
    public CouponConfigResponse updateOfLoansTenderHjh(@RequestBody AccountVO account) {
        CouponConfigResponse response = new CouponConfigResponse();
        Integer count = couponConfigService.updateOfLoansTenderHjh(account);
        response.setCount(count);
        return response;
    }

    /**
     * @Author walter.limeng
     * @Description  根据orderId查询所有待还款优惠券
     * @Date 17:02 2018/7/17
     * @Param orderId
     * @return
     */
    @RequestMapping("/getcoupontenderlisthjh/{orderId}")
    public CouponTenderCustomizeResponse getCouponTenderListHjh(@PathVariable String orderId) {
        CouponTenderCustomizeResponse response = new CouponTenderCustomizeResponse();
        List<CouponTenderCustomizeVO> list = couponConfigService.getCouponTenderListHjh(orderId);
        response.setResultList(list);
        return response;
    }

    /**
     * @Author walter.limeng
     * @Description  更新couponRecover对象
     * @Date 9:35 2018/7/18
     * @Param CouponRecoverVO
     * @return
     */
    @RequestMapping("/updatecouponrecoverhjh")
    public CouponRecoverResponse updateCouponRecover(@RequestBody CouponRecoverVO cr) {
        CouponRecoverResponse response = new CouponRecoverResponse();
        couponConfigService.updateCouponRecover(cr);
        response.setResult(cr);
        return response;
    }

    /**
     * @Author walter.limeng
     * @Description  根据recoverId查询交易记录
     * @Date 9:44 2018/7/18
     * @Param recoverId
     * @return
     */
    @RequestMapping("/selectbyrecoverid/{recoverId}")
    public TransferExceptionLogResponse selectByRecoverId(@PathVariable Integer recoverId) {
        TransferExceptionLogResponse response = new TransferExceptionLogResponse();
        List<TransferExceptionLogVO> list = couponConfigService.selectByRecoverId(recoverId);
        response.setResultList(list);
        return response;
    }
    /**
     * @Author walter.limeng
     * @Description  新增交易日志数据
     * @Date 9:56 2018/7/18
     * @Param transferExceptionLog
     * @return
     */
    @RequestMapping("/insertTransferexloghjh")
    public TransferExceptionLogResponse insertTransferExLog(@RequestBody TransferExceptionLogWithBLOBsVO transferExceptionLog) {
        TransferExceptionLogResponse response = new TransferExceptionLogResponse();
        Integer flag = couponConfigService.insertTransferExLog(transferExceptionLog);
        response.setFlag(flag);
        return response;
    }

    /**
     * @Author walter.limeng
     * @Description  根据borrowNid查询所有优惠券散标还款
     * @Date 16:47 2018/7/18
     * @Param borrowNid
     * @return
     */
    @RequestMapping("/getcoupontenderlist/{borrowNid}")
    public CouponTenderCustomizeResponse getCouponTenderList(@PathVariable String borrowNid) {
        CouponTenderCustomizeResponse response = new CouponTenderCustomizeResponse();
        List<CouponTenderCustomizeVO> list = couponConfigService.getCouponTenderList(borrowNid);
        response.setResultList(list);
        return response;
    }

    /**
     * @Author walter.limeng
     * @Description  更新还款期
     * @Date 17:09 2018/7/18
     * @Param tenderNid
     * @Param period
     * @return
     */
    @RequestMapping("/updaterecoverperiod/{tenderNid}/{period}")
    public CouponRecoverResponse updateRecoverPeriod(@PathVariable String tenderNid,@PathVariable Integer period) {
        CouponRecoverResponse response = new CouponRecoverResponse();
        Integer flag = couponConfigService.updateRecoverPeriod(tenderNid,period);
        response.setRecordTotal(flag);
        return response;
    }

    @RequestMapping("/selectCouponInterestTotal/{userId}")
    public String selectCouponInterestTotal(@PathVariable Integer userId){
        String total = couponConfigService.selectCouponInterestTotal(userId);
        return total;
    }

    @RequestMapping("/selectCouponReceivedInterestTotal/{userId}")
    public String selectCouponReceivedInterestTotal(@PathVariable Integer userId){
        String total = couponConfigService.selectCouponReceivedInterestTotal(userId);
        return total;
    }


    /**
     * 获取admin优惠券发放配置
     *
     * @param request
     * @return
     */
    @PostMapping("/adminCouponConfig")
    public CouponConfigCustomizeResponse getCouponConfigList(@RequestBody CouponConfigRequest request) {
        CouponConfigCustomizeResponse response = new CouponConfigCustomizeResponse();
        //加载优惠券配置列表
        CouponConfigCustomize configCustomize = new CouponConfigCustomize();
        configCustomize.setStatus(request.getStatus());
        configCustomize.setLimitStart(-1);
        configCustomize.setLimitEnd(-1);
        List<CouponConfigCustomize> couponConfigCustomizes = couponConfigService.getCouponConfigList(request);
        if (!CollectionUtils.isEmpty(couponConfigCustomizes)) {
            List<CouponConfigCustomizeVO> couponConfigCustomizeVOS = CommonUtils.convertBeanList(couponConfigCustomizes, CouponConfigCustomizeVO.class);
            response.setResultList(couponConfigCustomizeVOS);
        }
        return response;
    }
}