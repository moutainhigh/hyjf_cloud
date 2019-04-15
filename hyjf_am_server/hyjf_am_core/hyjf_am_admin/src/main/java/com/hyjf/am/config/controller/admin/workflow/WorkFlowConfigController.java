package com.hyjf.am.config.controller.admin.workflow;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.config.controller.BaseConfigController;
import com.hyjf.am.config.service.WorkFlowConfigService;
import com.hyjf.am.response.BooleanResponse;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.WorkFlowConfigResponse;
import com.hyjf.am.resquest.admin.WorkFlowConfigRequest;
import com.hyjf.am.vo.admin.WorkFlowVO;
import com.hyjf.common.paginator.Paginator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author xiehuili on 2019/4/12.
 */
@RestController
@RequestMapping("/am-admin/workflow/bussinessflow")
public class WorkFlowConfigController extends BaseConfigController {

    @Autowired
    private WorkFlowConfigService workFlowConfigService;

    /**
     * 工作流查询查询业务流程配置
     * @param adminRequest
     * @return
     */
    @PostMapping("/init")
    public WorkFlowConfigResponse selectWorkFlowConfigList(@RequestBody WorkFlowConfigRequest adminRequest) {
        logger.info("工作流查询查询业务流程配置..." + JSONObject.toJSON(adminRequest));
        WorkFlowConfigResponse response = new WorkFlowConfigResponse();
        int count = workFlowConfigService.countWorkFlowConfigList(adminRequest);
        if(count>0){
            Paginator paginator = new Paginator(adminRequest.getCurrPage(), count, adminRequest.getPageSize());
            List<WorkFlowVO> workFlowVOS =workFlowConfigService.selectWorkFlowConfigList(adminRequest, paginator.getOffset(), paginator.getLimit());
            if(!CollectionUtils.isEmpty(workFlowVOS)){
                response.setResultList(workFlowVOS);
            }
            response.setCount(count);
        }
        logger.debug("工作流查询查询业务流程配置..." + JSONObject.toJSON(response));
        return response;
    }

    /**
     * 添加业务流程
     * @param workFlowVO
     * @return
     */
    @PostMapping("/insert")
    public BooleanResponse insertWorkFlowConfig( @RequestBody WorkFlowVO workFlowVO) {
        logger.info("添加业务流程..." + JSONObject.toJSON(workFlowVO));
        BooleanResponse response = new BooleanResponse();

        //校验业务id对应的业务流程是否存在
        WorkFlowConfigRequest adminRequest = new WorkFlowConfigRequest();
        adminRequest.setBusinessId(workFlowVO.getBusinessId());
        int count = workFlowConfigService.countWorkFlowConfigList(adminRequest);
        if(count > 0){
            response.setRtn(Response.FAIL);
            response.setMessage("该业务id对应的业务流程已经存在，请修改");
            logger.debug("该业务id对应的业务流程已经存在，请修改" );
            return response;
        }

        //添加业务流程
        int flag = workFlowConfigService.insertWorkFlowConfig(workFlowVO);
        if(flag ==0){
            response.setRtn(Response.FAIL);
            response.setMessage("添加业务流程失败");
            logger.debug("添加业务流程失败" );
            return response;
        }
        response.setRtn(Response.SUCCESS);
        response.setMessage(Response.SUCCESS_MSG);
        return response;
    }
}
