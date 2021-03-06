package com.hyjf.admin.controller.promotion;

import com.hyjf.admin.beans.BorrowCommonImage;
import com.hyjf.admin.beans.request.LandingManagerRequestBean;
import com.hyjf.admin.beans.vo.DropDownVO;
import com.hyjf.admin.beans.vo.TemplateConfigCustomizeVO;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.result.ListResult;
import com.hyjf.admin.common.util.ShiroConstants;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.interceptor.AuthorityAnnotation;
import com.hyjf.admin.service.ContentAdsService;
import com.hyjf.admin.service.LandingManagerService;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.user.TemplateConfigResponse;
import com.hyjf.am.resquest.user.LandingManagerRequest;
import com.hyjf.am.vo.config.AdminSystemVO;
import com.hyjf.am.vo.user.TemplateConfigVO;
import com.hyjf.common.cache.CacheUtil;
import com.hyjf.common.util.CommonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author walter.nxl
 * @version LandingManageController, v0.1 2019/6/18 11:17
 */
@Api(tags = "推广中心-着陆页管理")
@RestController
@RequestMapping("/hyjf-admin/promotion/landing")
public class LandingManageController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(LandingManageController.class);
    /**
     * 查看权限
     */
    public static final String PERMISSIONS = "landing";

    @Autowired
    private LandingManagerService landingManagerService;
    @Autowired
    private ContentAdsService contentAdsService;

    @ApiOperation(value = "页面初始化", notes = "初始化下拉列表")
    @PostMapping("/init")
     @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    public AdminResult utmListInit(HttpServletRequest request, HttpServletResponse response) {
        // 模板类型
        Map<String, String> tempType = CacheUtil.getParamNameMap("TEMP_TYPE");
        List<DropDownVO> listUserRoles = com.hyjf.admin.utils.ConvertUtils.convertParamMapToDropDown(tempType);
        AdminResult adminResult = new AdminResult();
        adminResult.setData(listUserRoles);
        return adminResult;
    }

    //会员管理列表查询
    @ApiOperation(value = "着陆页管理列表查询", notes = "着陆页管理列表查询")
    @PostMapping(value = "/landinglist")
    @ResponseBody
     @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    public AdminResult<ListResult<TemplateConfigCustomizeVO>> getUserslist(@RequestBody LandingManagerRequestBean landingManagerRequestBean, HttpServletRequest request) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, String> tempType = CacheUtil.getParamNameMap("TEMP_TYPE");
        LandingManagerRequest landingManagerRequest = new LandingManagerRequest();
        BeanUtils.copyProperties(landingManagerRequestBean, landingManagerRequest);
        //模板名称模糊查找
        landingManagerRequest.setVague(true);
        TemplateConfigResponse templateConfigResponse = landingManagerService.selectTempConfigList(landingManagerRequest);
        if (null==templateConfigResponse||!Response.isSuccess(templateConfigResponse)) {
            return new AdminResult<>(FAIL, templateConfigResponse.getMessage());
        }
        List<TemplateConfigVO> templateConfigVOList = templateConfigResponse.getResultList();
        if (null != templateConfigVOList && templateConfigVOList.size() > 0) {
            for (TemplateConfigVO templateConfigVO : templateConfigVOList) {
                if (templateConfigVO.getStatus() == 1) {
                    templateConfigVO.setStatusStr("启用");
                } else {
                    templateConfigVO.setStatusStr("关闭");
                }
                String strCreate = sdf.format(templateConfigVO.getCreateTime());
                templateConfigVO.setCreateTimeStr(strCreate);
                templateConfigVO.setTempTypeStr(tempType.getOrDefault(templateConfigVO.getTempType().toString(), null));
            }
        }
        List<TemplateConfigCustomizeVO> templateConfigCustomizeVOS = CommonUtils.convertBeanList(templateConfigVOList, TemplateConfigCustomizeVO.class);
        return new AdminResult<ListResult<TemplateConfigCustomizeVO>>(ListResult.build(templateConfigCustomizeVOS, templateConfigResponse.getCount()));
    }

    @ApiOperation(value = "画面迁移(含有id更新，不含有id添加)", notes = "画面迁移(含有id更新，不含有id添加)")
    @PostMapping("/selectTemplateById")
    @ResponseBody
    @AuthorityAnnotation(key = PERMISSIONS, value = {ShiroConstants.PERMISSION_ADD, ShiroConstants.PERMISSION_MODIFY})
    public AdminResult<TemplateConfigCustomizeVO> selectTemplateById(HttpServletRequest request, HttpServletResponse response, @RequestBody LandingManagerRequestBean landingManagerRequestBean) {
        TemplateConfigCustomizeVO templateConfigCustomizeVO = new TemplateConfigCustomizeVO();
        //设置默认结果页图片和跳转路径
        templateConfigCustomizeVO.setLayerImg("https://wechat.hyjf.com/static/images/modal-success-default.png");
        templateConfigCustomizeVO.setJumtUrl("/user/open");
        if (null != landingManagerRequestBean.getId()) {
            TemplateConfigResponse templateConfigResponse = landingManagerService.selectTemplateById(landingManagerRequestBean.getId());
            if (!Response.isSuccess(templateConfigResponse)) {
                return new AdminResult<>(FAIL, templateConfigResponse.getMessage());
            }
            BeanUtils.copyProperties(templateConfigResponse.getResult(), templateConfigCustomizeVO);

        }
        return new AdminResult<TemplateConfigCustomizeVO>(templateConfigCustomizeVO);
    }

    @ApiOperation(value = "添加或修改信息", notes = "添加或修改信息")
    @PostMapping("/insertOrUpdateTemplate")
    @AuthorityAnnotation(key = PERMISSIONS, value = {ShiroConstants.PERMISSION_ADD, ShiroConstants.PERMISSION_MODIFY})
    public AdminResult insertOrUpdateTemplate(HttpServletRequest request, HttpServletResponse response, @RequestBody LandingManagerRequestBean landingManagerRequestBean) {
        //根据id判断，如存在，则为修改，如不存在，则为新增
        LandingManagerRequest landingManagerRequest = new LandingManagerRequest();
        AdminSystemVO adminSystemVO = this.getUser(request);
        int intFlg = 0;
        if (null != landingManagerRequestBean.getId()) {
            //修改
            TemplateConfigResponse templateConfigResponse = landingManagerService.selectTemplateById(landingManagerRequestBean.getId());
            if (!Response.isSuccess(templateConfigResponse)) {
                return new AdminResult<>(FAIL, templateConfigResponse.getMessage());
            }
        }
        BeanUtils.copyProperties(landingManagerRequestBean, landingManagerRequest);
        landingManagerRequest.setLoginUserId(adminSystemVO.getId());
        intFlg = landingManagerService.updateOrInsertTemplate(landingManagerRequest);
        if (intFlg <= 0) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        return new AdminResult<>();
    }

    @ApiOperation(value = "删除&状态修改信息", notes = "删除&状态修改信息")
    @PostMapping("/deleteTemplate")
    @AuthorityAnnotation(key = PERMISSIONS, value = {ShiroConstants.PERMISSION_DELETE, ShiroConstants.PERMISSION_MODIFY})
    public AdminResult deleteTemplate(HttpServletRequest request, HttpServletResponse response, @RequestBody LandingManagerRequestBean landingManagerRequestBean) {
        AdminResult adminResult = new AdminResult();
        if (null != landingManagerRequestBean.getId()) {
            if (null != landingManagerRequestBean.getStatus()) {
                //如果有状态代表是状态修改
                AdminSystemVO adminSystemVO = this.getUser(request);
                LandingManagerRequest landingManagerRequest = new LandingManagerRequest();
                TemplateConfigResponse templateConfigResponse = landingManagerService.selectTemplateById(landingManagerRequestBean.getId());
                TemplateConfigVO templateConfigVO = templateConfigResponse.getResult();
                //设置状态1启用，0禁用
                templateConfigVO.setStatus(landingManagerRequestBean.getStatus());
                BeanUtils.copyProperties(templateConfigVO, landingManagerRequest);
                landingManagerRequest.setLoginUserId(adminSystemVO.getId());
                int intFlg = landingManagerService.updateOrInsertTemplate(landingManagerRequest);
                if (intFlg <= 0) {
                    return new AdminResult<>(FAIL, FAIL_DESC);
                }
            } else {
                 TemplateConfigResponse flag = landingManagerService.deleteTemplate(landingManagerRequestBean.getId());
                if (!Response.isSuccess(flag)) {
                    adminResult.setStatus(AdminResult.FAIL);
                    adminResult.setStatusDesc(flag.getMessage());
                }
            }
        } else {
            adminResult.setStatus(AdminResult.FAIL);
            adminResult.setStatusDesc("删除异常！");
        }
        return adminResult;
    }

    @ApiOperation(value = "图片上传", notes = "图片上传")
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @AuthorityAnnotation(key = PERMISSIONS, value = {ShiroConstants.PERMISSION_ADD , ShiroConstants.PERMISSION_MODIFY} )
    public AdminResult<LinkedList<BorrowCommonImage>> uploadFile(HttpServletRequest request) throws Exception {
        AdminResult<LinkedList<BorrowCommonImage>> adminResult = new AdminResult<>();
        try {
            LinkedList<BorrowCommonImage> borrowCommonImages = contentAdsService.uploadFile(request);
            adminResult.setData(borrowCommonImages);
            adminResult.setStatus(SUCCESS);
            adminResult.setStatusDesc(SUCCESS_DESC);
            return adminResult;
        } catch (Exception e) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
    }

    @ApiOperation(value = "模板名称校验", notes = "模板名称校验")
    @PostMapping("/checkTempName")
    public AdminResult checkTempName(@RequestBody LandingManagerRequestBean landingManagerRequestBean){
        LandingManagerRequest landingManagerRequest = new LandingManagerRequest();
        BeanUtils.copyProperties(landingManagerRequestBean, landingManagerRequest);
        //根据名字查询
        landingManagerRequest.setVague(false);
        TemplateConfigResponse templateConfigResponse = landingManagerService.selectTempConfigList(landingManagerRequest);
        if (null==templateConfigResponse||!Response.isSuccess(templateConfigResponse)) {
            return new AdminResult<>(FAIL, templateConfigResponse.getMessage());
        }
        List<TemplateConfigVO> templateConfigVOList = templateConfigResponse.getResultList();
        if(CollectionUtils.isNotEmpty(templateConfigVOList)){
            //如果存在，返回信息
            return new AdminResult<>(FAIL, "模板名称不能重复");
        }
        return new AdminResult<>(SUCCESS, SUCCESS_DESC);
    }
}
