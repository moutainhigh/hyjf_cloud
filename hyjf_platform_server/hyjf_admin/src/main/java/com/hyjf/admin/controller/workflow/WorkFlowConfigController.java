package com.hyjf.admin.controller.workflow;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.util.ShiroConstants;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.interceptor.AuthorityAnnotation;
import com.hyjf.admin.service.workflow.WorkFlowConfigService;
import com.hyjf.am.response.BooleanResponse;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.AdminRoleResponse;
import com.hyjf.am.response.admin.WorkFlowConfigResponse;
import com.hyjf.am.response.admin.WorkFlowUserResponse;
import com.hyjf.am.resquest.admin.WorkFlowConfigRequest;
import com.hyjf.am.resquest.config.AdminRoleRequest;
import com.hyjf.am.vo.admin.WorkFlowNodeVO;
import com.hyjf.am.vo.admin.WorkFlowUserVO;
import com.hyjf.am.vo.admin.WorkFlowVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author xiehuili on 2019/4/12.
 */
@Api(tags = "工作流---业务流程配置")
@RestController
@RequestMapping("/hyjf-admin/workflow/bussinessflow")
public class WorkFlowConfigController  extends BaseController {


    //权限名称
    private static final String PERMISSIONS = "bussinessflow";

    @Autowired
    private WorkFlowConfigService workFlowConfigService;

    @ApiOperation(value = "查询业务流程", notes = "查询业务流程")
    @PostMapping("/init")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    public AdminResult selectWorkFlowConfigList(HttpServletRequest request, @RequestBody WorkFlowConfigRequest adminRequest) {
        logger.info("工作流查询查询业务流程配置..." + JSONObject.toJSONString(adminRequest));
        WorkFlowConfigResponse response = workFlowConfigService.selectWorkFlowConfigList(adminRequest);
        logger.debug("工作流查询查询业务流程配置..." + JSONObject.toJSONString(response));
        if(response==null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<WorkFlowConfigResponse>(response) ;
    }

    @ApiOperation(value = "添加业务流程", notes = "添加业务流程")
    @PostMapping("/insert")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_ADD)
    public AdminResult insertWorkFlowConfig( HttpServletRequest request,@RequestBody WorkFlowVO workFlowVO) {
        logger.info("工作流添加业务流程配置..." + JSONObject.toJSONString(workFlowVO));
        workFlowVO.setUpdateUser(this.getUser(request).getTruename());
//         workFlowVO.setUpdateUser("管理员");
        //校验请求参数
        String errorMsg = validation(workFlowVO);
        if(!StringUtils.isBlank(errorMsg)){
            logger.debug("工作流添加业务流程配置参数校验不合法，错误消息为" + errorMsg);
            return new AdminResult<>(FAIL, errorMsg);
        }

        //添加业务流程
        BooleanResponse response =workFlowConfigService.insertWorkFlowConfig(workFlowVO);
        logger.debug("工作流查询查询业务流程配置..." + JSONObject.toJSONString(response));
        if(response==null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<BooleanResponse>(response) ;
    }

    @ApiOperation(value = "业务流程详情页面", notes = "业务流程详情页面")
    @PostMapping("/info")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_INFO)
    public AdminResult selectWorkFlowConfigInfo( HttpServletRequest request,@RequestBody WorkFlowVO workFlowVO) {
        logger.info("业务流程详情页面，业务流程id为：" + workFlowVO.getId());
        if(null == workFlowVO.getId()){
            return new AdminResult<>(FAIL, "业务流程id不能为空");
        }
        //查询业务流程详情页面
        WorkFlowConfigResponse response =workFlowConfigService.selectWorkFlowConfigInfo(workFlowVO.getId());
        logger.debug("查询业务流程详情页面，response：" + JSONObject.toJSONString(response));
        if(response==null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<WorkFlowConfigResponse>(response) ;
    }

    @ApiOperation(value = "修改业务流程", notes = "修改业务流程")
    @PostMapping("/update")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_UPDATE)
    public AdminResult updateWorkFlowConfig( HttpServletRequest request,@RequestBody WorkFlowVO workFlowVO) {
        logger.info("工作流修改业务流程配置..." + JSONObject.toJSONString(workFlowVO));
        workFlowVO.setUpdateUser(this.getUser(request).getTruename());
//        workFlowVO.setUpdateUser("管理员");
        //校验请求参数
        if(null == workFlowVO.getId()){
            logger.debug("工作流修改业务流程配置参数校验不合法，错误消息为:业务流程的id不能为空" );
            return new AdminResult<>(FAIL, "业务流程的id不能为空");
        }
        String errorMsg = validation(workFlowVO);
        if(!StringUtils.isBlank(errorMsg)){
            logger.debug("工作流修改业务流程配置参数校验不合法，错误消息为" + errorMsg);
            return new AdminResult<>(FAIL, errorMsg);
        }

        //修改业务流程
        BooleanResponse response =workFlowConfigService.updateWorkFlowConfig(workFlowVO);
        logger.debug("工作流查询查询业务流程配置..." + JSONObject.toJSONString(response));
        if(response==null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<BooleanResponse>(response) ;
    }

    @ApiOperation(value = "删除业务流程", notes = "删除业务流程")
    @PostMapping("/delete")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_DELETE)
    public AdminResult deleteWorkFlowConfig( HttpServletRequest request,@RequestBody WorkFlowVO workFlowVO) {
        logger.info("工作流删除业务流程配置..." + workFlowVO.getId());
        //校验请求参数
        if(null == workFlowVO.getId()){
            logger.debug("工作流删除业务流程配置参数校验不合法，错误消息为:业务流程的id不能为空" );
            return new AdminResult<>(FAIL, "业务流程的id不能为空");
        }

        //修改业务流程
        BooleanResponse response =workFlowConfigService.deleteWorkFlowConfigById(workFlowVO.getId());
        logger.debug("工作流查询查询业务流程配置..." + JSONObject.toJSONString(response));
        if(response==null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<BooleanResponse>(response) ;
    }

    @ApiOperation(value = "校验业务流程是否存在", notes = "校验业务流程是否存在")
    @PostMapping("/exist")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    public AdminResult selectByBussinessId(HttpServletRequest request, @RequestBody WorkFlowConfigRequest adminRequest) {
        logger.info("校验业务流程是否存在，业务名称的id:" + adminRequest.getBusinessId());
        if(null ==  adminRequest.getBusinessId()){
            return new AdminResult<>(FAIL, "业务名称的id不能是空");
        }
        BooleanResponse response = workFlowConfigService.selectWorkFlowConfigByBussinessId(adminRequest);
        logger.debug("工作流查询查询业务流程配置..." + JSONObject.toJSONString(response));
        if(response==null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<BooleanResponse>(response) ;
    }

    @ApiOperation(value = "查询邮件预警人", notes = "查询邮件预警人")
    @PostMapping("/selectUser")
    public AdminResult selectUser( @RequestBody WorkFlowUserVO workFlowUserVO) {
        logger.info("查询邮件预警人，userName:" + workFlowUserVO.getUsername());
        WorkFlowUserResponse response = workFlowConfigService.selectUser(workFlowUserVO);
        logger.debug("工作流查询查询业务流程配置..." + JSONObject.toJSONString(response));
        if(response==null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<WorkFlowUserResponse>(response) ;
    }

    @ApiOperation(value = "查询所有角色", notes = "查询所有角色")
    @PostMapping("/role")
    public AdminResult selectWorkFlowRoleList(@RequestBody AdminRoleRequest form) {
        logger.info("工作流查询所有角色..." );
        AdminRoleResponse response = workFlowConfigService.selectWorkFlowRoleList();
        logger.debug("工作流查询所有角色..." + JSONObject.toJSONString(response));
        if(response==null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<AdminRoleResponse>(response) ;
    }

    /**
     * 校验请求参数
     * @param workFlowVO
     * @return
     */
    public String validation(WorkFlowVO workFlowVO){
        if(null == workFlowVO){
            return "业务流程参数不能为空";
        }
        String businessName = workFlowVO.getBusinessName();
        Integer businessId = workFlowVO.getBusinessId();
        Integer auditFlag = workFlowVO.getAuditFlag();
        if(null == businessId){
            return "业务名称不能为空";
        }
        if(null == auditFlag){
            return "是否需要审核标识不能为空";
        }
        if(auditFlag.intValue() == 1){
            List<WorkFlowNodeVO> list = workFlowVO.getFlowNodes();
            if(CollectionUtils.isEmpty(list)){
                return "需要审核的时候，流程节点不能为空";
            }
            for(int i=0;i<list.size();i++){
                if(null == list.get(i).getAuditUser()||list.get(i).getAuditUser().length==0 ){
                    return "需要审核的时候，流程节点审核人不能为空";
                }
            }
            String mailWarningUser = workFlowVO.getMailWarningUser();
            if(StringUtils.isBlank(mailWarningUser)){
                return "需要审核的时候，邮件预警人不能为空";
            }
            if(mailWarningUser.length()>150){
                return "需要审核的时候，邮件预警人长度不能超过150个字符";
            }
            //判断邮件预警人是否有邮箱
            String[] mailWarnings = mailWarningUser.split(";");
            for(int i=0;i<mailWarnings.length;i++){
                String mailWarning=mailWarnings[i];
                int j =mailWarning.lastIndexOf(",");
                String mail = mailWarning.substring(j+1);
                if(StringUtils.isBlank(mail)){
                    return "需要审核的时候，邮件预警人邮箱不能为空";
                }
            }
        }
        return "";
    }
}
