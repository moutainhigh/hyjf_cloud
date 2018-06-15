/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.trade.controller.web.projectlist;

import com.hyjf.am.resquest.trade.ProjectListRequest;
import com.hyjf.am.util.Result;
import com.hyjf.cs.trade.service.WebProjectListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * web端项目列表
 *
 * @author liuyang
 * @version WebProjectListController, v0.1 2018/6/13 10:21
 */
@Api(value = "Web端项目列表")
@RestController
@RequestMapping("/web/projectlist")
public class WebProjectListController {
    private static final Logger logger = LoggerFactory.getLogger(WebProjectListController.class);

     @Autowired
     private WebProjectListService webProjectListService;

    /**
     * 获取首页散标推荐列表
     * @param request
     * @return
     */
    @ApiOperation(value = "获取首页散标推荐列表", notes = "首页散标推荐列表")
    @PostMapping(value = "/homeBorrowProjectList", produces = "application/json; charset=utf-8")
    public Object homeBorrowProjectList(@RequestBody @Valid ProjectListRequest request){
        // controller 不做业务处理
        Result result =  webProjectListService.searchProjectList(request);
        return result;
    }




}
