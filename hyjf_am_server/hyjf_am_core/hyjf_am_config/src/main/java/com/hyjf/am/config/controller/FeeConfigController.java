package com.hyjf.am.config.controller;

import com.hyjf.am.config.dao.model.auto.FeeConfig;
import com.hyjf.am.config.service.FeeConfigService;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.AdminFeeConfigResponse;
import com.hyjf.am.response.config.FeeConfigResponse;
import com.hyjf.am.resquest.admin.AdminFeeConfigRequest;
import com.hyjf.am.vo.config.FeeConfigVO;
import com.hyjf.common.util.CommonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author xiasq
 * @version FeeConfigController, v0.1 2018/6/22 14:28
 */
@RestController
@RequestMapping("/am-config/feeConfig")
public class FeeConfigController extends BaseConfigController{

    @Autowired
    private FeeConfigService feeConfigService;
    
    /**
     * 获取用户的提现费率
     * @return
     */
    @GetMapping("/getFeeConfig/{bankCode}")
    public FeeConfigResponse getFeeConfig(@PathVariable String bankCode){
        FeeConfigResponse response = new FeeConfigResponse();
        List<FeeConfig> feeConfigs = feeConfigService.getFeeConfigs(bankCode);
        if(CollectionUtils.isNotEmpty(feeConfigs)){
            List<FeeConfigVO> feeConfigVOList=CommonUtils.convertBeanList(feeConfigs,FeeConfigVO.class);
            response.setResultList(feeConfigVOList);
        }
        return response;
    }


    /**
     * 添加手续费配置
     * @param adminRequest
     */
    @RequestMapping("/insert")
    public AdminFeeConfigResponse insertFeeConfig(@RequestBody AdminFeeConfigRequest adminRequest) {
        AdminFeeConfigResponse  response =new AdminFeeConfigResponse();
        int result =this.feeConfigService.insertFeeConfig(adminRequest);
        if(result > 0 ){
            response.setRtn(Response.SUCCESS);
            return response;
        }
        response.setRtn(Response.FAIL);
        response.setMessage(Response.FAIL_MSG);
        return response;
    }

    /**
     * 修改版本配置
     * @param adminRequest
     */
    @RequestMapping("/update")
    public AdminFeeConfigResponse updateFeeConfig(@RequestBody AdminFeeConfigRequest adminRequest) {
        AdminFeeConfigResponse  response =new AdminFeeConfigResponse();
        int result =this.feeConfigService.updateFeeConfig(adminRequest);
        if(result > 0 ){
            response.setRtn(Response.SUCCESS);
            return response;
        }
        response.setRtn(Response.FAIL);
        response.setMessage(Response.FAIL_MSG);
        return response;
    }

    /**
     * 删除版本配置
     * @param adminRequest
     */
    @RequestMapping("/delete")
    public AdminFeeConfigResponse deleteFeeConfig(@RequestBody AdminFeeConfigRequest adminRequest) {
        AdminFeeConfigResponse  response =new AdminFeeConfigResponse();
        if(adminRequest.getId() != null){
            this.feeConfigService.deleteFeeConfig(adminRequest.getId());
            response.setRtn(Response.SUCCESS);
            return response;
        }
     return null;
    }

}
