/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.controller.admin.exception;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.admin.AdminTransferExceptionLogResponse;
import com.hyjf.am.response.admin.TransferExceptionLogResponse;
import com.hyjf.am.resquest.admin.AdminTransferExceptionLogRequest;
import com.hyjf.am.trade.controller.BaseController;
import com.hyjf.am.trade.dao.model.auto.TransferExceptionLog;
import com.hyjf.am.trade.dao.model.customize.admin.AdminTransferExceptionLogCustomize;
import com.hyjf.am.trade.service.admin.exception.AdminTransferExceptionLogService;
import com.hyjf.am.vo.admin.AdminTransferExceptionLogCustomizeVO;
import com.hyjf.am.vo.trade.TransferExceptionLogVO;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.common.validator.Validator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author jun
 * @version AdminTransferExceptionLogController, v0.1 2018/7/10 11:19
 */
@Api(value = "异常中心-银行转账异常",description = "异常中心-银行转账异常")
@RestController
@RequestMapping("/am-trade/transferExceptionLog")
public class AdminTransferExceptionLogController extends BaseController {

    @Autowired
    private AdminTransferExceptionLogService adminTransferExceptionLogService;


    @ApiOperation(value = "银行转账异常列表", notes = "银行转账异常列表")
    @PostMapping("/getRecordList")
    public AdminTransferExceptionLogResponse getRecordList(@RequestBody AdminTransferExceptionLogRequest request){
        AdminTransferExceptionLogResponse response = new AdminTransferExceptionLogResponse();
        List<AdminTransferExceptionLogCustomize> results=adminTransferExceptionLogService.getRecordList(request);
        int count = adminTransferExceptionLogService.getCountRecord(request);
        String returnCode = "0";
        if (CollectionUtils.isNotEmpty(results)){
            response.setResultList(CommonUtils.convertBeanList(results,AdminTransferExceptionLogCustomizeVO.class));
            response.setCount(count);
            response.setRtn(returnCode);
        }
        return response;
    }


    @ApiOperation(value = "银行转账异常数据条数", notes = "银行转账异常数据条数")
    @PostMapping("/getCountRecord")
    public Integer getCountRecord(@RequestBody AdminTransferExceptionLogRequest request){
        return adminTransferExceptionLogService.getCountRecord(request);
    }


    @ApiOperation(value = "根据UUID更新admin转账异常日志", notes = "根据UUID更新admin转账异常日志")
    @PostMapping("/updateTransferExceptionLogByUUID")
    public Integer updateTransferExceptionLogByUUID(@RequestBody AdminTransferExceptionLogRequest request){
        return adminTransferExceptionLogService.updateTransferExceptionLogByUUID(request);
    }


    @ApiOperation(value = "根据UUID更新转账异常日志", notes = "根据UUID更新转账异常日志")
    @PostMapping("/updateTransferExceptionLogByUUID1")
    public Integer updateTransferExceptionLogByUUID1(@RequestBody TransferExceptionLogVO transferExceptionLog){
        return adminTransferExceptionLogService.updateTransferExceptionLogByUUID(transferExceptionLog);
    }

    @ApiOperation(value = "通过UUID获取转账异常日志", notes = "通过UUID获取转账异常日志")
    @GetMapping("/getTransferExceptionLogByUUID/{uuid}")
    public TransferExceptionLogResponse getTransferExceptionLogByUUID(@PathVariable String uuid){
        TransferExceptionLogResponse response=new TransferExceptionLogResponse();
        TransferExceptionLog transferExceptionLog=adminTransferExceptionLogService.getTransferExceptionLogByUUID(uuid);
        if (Validator.isNotNull(transferExceptionLog)){
            response.setResult(CommonUtils.convertBean(transferExceptionLog,TransferExceptionLogVO.class));
        }
        return response;
    }

    @ApiOperation(value = "处理银行转账异常", notes = "处理银行转账异常")
    @PostMapping("/transferAfter")
    public boolean transferAfter(@RequestBody JSONObject jsonObject){
        boolean ret = false;
        try {
            ret = adminTransferExceptionLogService.transferAfter(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
            ret = false;
        }finally {
            return ret;
        }


    }

}
