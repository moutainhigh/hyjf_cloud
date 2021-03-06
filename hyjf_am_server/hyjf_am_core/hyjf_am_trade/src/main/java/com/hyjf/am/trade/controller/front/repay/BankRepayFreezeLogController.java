package com.hyjf.am.trade.controller.front.repay;

import com.hyjf.am.response.IntegerResponse;
import com.hyjf.am.response.trade.BankRepayFreezeLogResponse;
import com.hyjf.am.resquest.trade.BankRepayFreezeLogRequest;
import com.hyjf.am.trade.controller.BaseController;
import com.hyjf.am.trade.dao.model.auto.BankRepayFreezeLog;
import com.hyjf.am.trade.service.front.repay.BankRepayFreezeLogService;
import com.hyjf.am.vo.trade.repay.BankRepayFreezeLogVO;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.common.validator.Validator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 还款冻结表操作
 * @author hesy
 * @version BankRepayFreezeLogController, v0.1 2018/7/9 16:13
 */
@RestController
@RequestMapping("/am-trade/repayfreezelog")
public class BankRepayFreezeLogController extends BaseController {
    @Autowired
    BankRepayFreezeLogService bankRepayFreezeLogService;

    /**
     * 添加
     * @param requestBean
     * @return
     */
    @RequestMapping("/add")
    public IntegerResponse addFreezeLog(@RequestBody BankRepayFreezeLogRequest requestBean){
        return  new IntegerResponse(bankRepayFreezeLogService.insertRepayFreezeLog(requestBean));
    }

    /**
     * 根据orderId删除（逻辑删）
     * @param orderId
     * @return
     */
    @RequestMapping("/deleteby_orderid/{orderId}")
    public IntegerResponse deleteFreezeLogByOrderId(@PathVariable String orderId){
        if(StringUtils.isBlank(orderId)){
            return new IntegerResponse(0);
        }

        return new IntegerResponse(bankRepayFreezeLogService.deleteFreezeLogsByOrderId(orderId));
    }

    /**
     * 根据id删除（物理删）
     * @auther: hesy
     * @date: 2018/7/11
     */
    @RequestMapping("/deleteby_id/{id}")
    public IntegerResponse deleteFreezeLogById(@PathVariable Integer id) {
        return new IntegerResponse(bankRepayFreezeLogService.deleteFreezeLogById(id));
    }

    /**
     * 获取当前有效的冻结记录
     * @auther: hesy
     * @date: 2018/7/10
     */
    @RequestMapping("/get_logvalid/{userId}/{borrowNid}")
    public BankRepayFreezeLogResponse getFreezeLogValid(@PathVariable Integer userId, @PathVariable String borrowNid){
        BankRepayFreezeLogResponse response = new BankRepayFreezeLogResponse();
        BankRepayFreezeLog log = bankRepayFreezeLogService.getFreezeLog(userId, borrowNid);
        if (Validator.isNotNull(log)){
            response.setResult(CommonUtils.convertBean(log,BankRepayFreezeLogVO.class));
        }
        return response;
    }

    /**
     * 根据borrowNid获取当前有效的冻结记录
     * @auther: wgx
     * @date: 2018/10/16
     */
    @RequestMapping("/get_logvalid/{borrowNid}")
    public BankRepayFreezeLogResponse getFreezeLogValid(@PathVariable String borrowNid) {
        BankRepayFreezeLogResponse response = new BankRepayFreezeLogResponse();
        BankRepayFreezeLog log = bankRepayFreezeLogService.getFreezeLog(null, borrowNid);
        if (Validator.isNotNull(log)) {
            response.setResult(CommonUtils.convertBean(log, BankRepayFreezeLogVO.class));
        }
        return response;
    }

    /**
     * 根据orderId获取冻结记录
     * @auther: hesy
     * @date: 2018/7/11
     */
    @RequestMapping("/get_logvalid_byorderid/{orderId}")
    public BankRepayFreezeLogResponse getBankFreezeLogByOrderId(@PathVariable String orderId) {
        BankRepayFreezeLogResponse response = new BankRepayFreezeLogResponse();
        BankRepayFreezeLog log = bankRepayFreezeLogService.getBankFreezeLogByOrderId(orderId);
        if (Validator.isNotNull(log)){
            response.setResult(CommonUtils.convertBean(log,BankRepayFreezeLogVO.class));
        }
        return response;
    }

    /**
     * 分页获取所有有效的冻结记录
     * @param limitStart
     * @param limitEnd
     * @return
     */
    @RequestMapping("/get_logvalid_all")
    public BankRepayFreezeLogResponse getFreezeLogValidAll(@RequestParam Integer limitStart, @RequestParam Integer limitEnd){
        BankRepayFreezeLogResponse response = new BankRepayFreezeLogResponse();
        List<BankRepayFreezeLog> logList = bankRepayFreezeLogService.getFreezeLogValidAll(limitStart,limitEnd);
        if (Validator.isNotNull(logList)){
            response.setResultList(CommonUtils.convertBeanList(logList,BankRepayFreezeLogVO.class));
        }
        return response;
    }

    /**
     * 有效冻结记录总数
     * @return
     */
    @RequestMapping("/get_logvalid_all_count")
    public IntegerResponse getFreezeLogValidAllCount() {
        return new IntegerResponse(bankRepayFreezeLogService.getFreezeLogValidAllCount());
    }
}
