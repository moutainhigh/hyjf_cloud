package com.hyjf.admin.controller.manager;

import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.result.ListResult;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.service.ProtocolService;
import com.hyjf.am.response.admin.AdminProtocolResponse;
import com.hyjf.am.resquest.admin.AdminProtocolRequest;
import com.hyjf.am.vo.admin.ProtocolTemplateCommonVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 协议模板管理
 * @author：yinhui
 * @Date: 2018/8/8  15:10
 */
@Api(value = "配置中心-协议模板管理",tags ="配置中心-协议模板管理")
@RestController
@RequestMapping("/hyjf-admin/manager/protocol")
public class ProtocolController extends BaseController{

    @Autowired
    private ProtocolService protocolService;

    /**
     * 分页显示
     * @param request
     * @return
     */
    @ApiOperation(value = "配置中心-协议模板管理", notes = "配置中心-协议模板管理 分页显示")
    @RequestMapping("/search")
    public AdminResult<ListResult<ProtocolTemplateCommonVO>> search( AdminProtocolRequest request){
        AdminProtocolResponse response = new AdminProtocolResponse();
        response = protocolService.searchPage(request);
        if (response == null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }

        return new AdminResult<ListResult<ProtocolTemplateCommonVO>>(ListResult.build(response.getResultList(), response.getCount())) ;
    }

    /**
     * 迁移到查看详细画面
     * @param request
     * @return
     */
    @ApiOperation(value = "配置中心-协议模板管理", notes = "配置中心-协议模板管理 迁移到查看详细画面")
    @RequestMapping("/infoInfoAction")
    public AdminResult<ProtocolTemplateCommonVO> infoInfoAction( AdminProtocolRequest request){
        AdminProtocolResponse response = new AdminProtocolResponse();
        response = protocolService.getProtocolTemplateById(request);
        if (response == null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }

        return new AdminResult<ProtocolTemplateCommonVO>(response.getResult()) ;
    }

    /**
     * 添加协议模板
     * @param request
     * @return
     */
    @ApiOperation(value = "配置中心-协议模板管理", notes = "配置中心-协议模板管理 添加协议模板")
    @RequestMapping("/insertAction")
    public AdminProtocolResponse insertAction( AdminProtocolRequest request){
        AdminProtocolResponse response = new AdminProtocolResponse();
        protocolService.insertProtocolTemplate(request);


        return response;
    }

    /**
     * 修改协议模板
     * @param request
     * @return
     */
    @ApiOperation(value = "配置中心-协议模板管理", notes = "配置中心-协议模板管理 修改协议模板")
    @RequestMapping("/updateAction")
    public AdminProtocolResponse updateProtocolTemplate( AdminProtocolRequest request){
        AdminProtocolResponse response = new AdminProtocolResponse();
        protocolService.updateProtocolTemplate(request);
        return response;
    }

    /**
     * 删除协议模板
     * @param request
     * @return
     */
    @ApiOperation(value = "配置中心-协议模板管理", notes = "配置中心-协议模板管理 删除协议模板")
    @RequestMapping("/deleteAction")
    public AdminProtocolResponse deleteAction( AdminProtocolRequest request){
        AdminProtocolResponse response = new AdminProtocolResponse();
        protocolService.deleteProtocolTemplate(request);
        return response;
    }
}
