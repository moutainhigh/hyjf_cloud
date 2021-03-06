/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.controller.content;

import com.hyjf.admin.beans.BorrowCommonImage;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.result.ListResult;
import com.hyjf.admin.common.util.ShiroConstants;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.interceptor.AuthorityAnnotation;
import com.hyjf.admin.service.ContentAdsService;
import com.hyjf.admin.service.ContentArticleService;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.ContentArticleResponse;
import com.hyjf.am.resquest.config.ContentArticleRequest;
import com.hyjf.am.vo.admin.CategoryVO;
import com.hyjf.am.vo.config.ContentArticleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 文章管理
 *
 * @author yinhui
 * @version ContentArticleController, v0.1 2018/7/17 17:04
 */
@Api(tags = "内容中心-文章管理")
@RestController
@RequestMapping(value = "/hyjf-admin/content/contentarticle")
public class ContentArticleController extends BaseController {

    @Autowired
    private ContentArticleService contentArticleService;

    @Autowired
    private ContentAdsService contentAdsService;

    /** 权限 */
    public static final String PERMISSIONS = "contentarticle";

    @ApiOperation(value = "文章管理-条件列表查询", notes = "文章管理-条件列表查询")
    @RequestMapping(value = "/searchaction",method = RequestMethod.POST)
    @AuthorityAnnotation(key = PERMISSIONS, value = {ShiroConstants.PERMISSION_VIEW , ShiroConstants.PERMISSION_SEARCH})
    public AdminResult<ListResult<ContentArticleVO>> searchAction(@RequestBody ContentArticleRequest requestBean) {
        logger.info("查询内容中心-文章管理-条件列表查询开始......");
        ContentArticleResponse response = contentArticleService.searchAction(requestBean);
        if (response == null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<>(ListResult.build(response.getResultList(), response.getCount()));
    }

    @ApiOperation(value = "文章管理-添加", notes = "文章管理-添加")
    @RequestMapping(value ="/insert",method = RequestMethod.POST)
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_ADD )
    public AdminResult add(@RequestBody ContentArticleRequest requestBean) {
        ContentArticleResponse response = contentArticleService.inserAction(requestBean);
        if (response == null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<>(ListResult.build(response.getResultList(), response.getCount()));
    }

    @ApiOperation(value = "文章管理-修改根据id查找所需要数据", notes = "文章管理-修改根据id查找所需要数据")
    @RequestMapping(value ="/infoaction",method = RequestMethod.POST)
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_SEARCH )
    public AdminResult infoAction(@RequestBody ContentArticleRequest requestBean) {

        if (StringUtils.isEmpty(requestBean.getIds())) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        Integer id = Integer.valueOf(requestBean.getIds());

        ContentArticleResponse response = contentArticleService.infoAction(id);
        if (response == null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<>(response.getResult());
    }

    @ApiOperation(value = "文章管理-修改", notes = "文章管理-修改")
    @RequestMapping(value ="/update",method = RequestMethod.POST)
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_MODIFY )
    public AdminResult update(@RequestBody ContentArticleRequest requestBean) {
        ContentArticleResponse response = contentArticleService.updateAction(requestBean);
        if (response == null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<>(ListResult.build(response.getResultList(), response.getCount()));
    }

    @ApiOperation(value = "文章管理-删除", notes = "文章管理-删除")
    @RequestMapping(value = "/delete/{id}",method = RequestMethod.POST)
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_DELETE )
    public AdminResult delete(@PathVariable Integer id) {
        ContentArticleResponse response = contentArticleService.deleteById(id);
        if (response == null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!Response.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<>(ListResult.build(response.getResultList(), response.getCount()));
    }

    @ApiOperation(value = "文章管理-下拉框类别", notes = "文章管理-下拉框类别")
    @GetMapping("/putcategory")
    public AdminResult putCategory() {
        Map<String, List<CategoryVO>> map = new HashMap<String, List<CategoryVO>>();
        List<CategoryVO> response = contentArticleService.putCategory();
        map.put("categoryList",response);
        return new AdminResult<>(map);
    }

    @ApiOperation(value = "资料上传", notes = "资料上传")
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    @AuthorityAnnotation(key = PERMISSIONS, value = {ShiroConstants.PERMISSION_ADD , ShiroConstants.PERMISSION_MODIFY} )
    public  AdminResult<LinkedList<BorrowCommonImage>> uploadFile(HttpServletRequest request) throws Exception {
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
}
