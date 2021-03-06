package com.hyjf.cs.trade.controller.app.home;

import com.hyjf.cs.trade.service.home.AppHomeService;
import com.hyjf.cs.trade.util.HomePageDefine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * APP端首页controller
 * @author zhangyk
 * @date 2018/7/5 13:40
 */
@Api(value = "app端-首页",tags = "app端-首页")
@RestController
@RequestMapping(HomePageDefine.REQUEST_MAPPING)  // 保留原来请求路径
public class AppHomeController {


    @Autowired
    private AppHomeService appHomeService;


    /**
     * 获取首页数据接口
     * 沿用原来的接口路径
     * 原接口：
     * @return
     */
    @ApiOperation(value = "获取首页各项数据", notes = "获取首页各项数据")
    @PostMapping(value = HomePageDefine.PROJECT_LIST_ACTION, produces = "application/json; charset=utf-8")
    public Object getHomeData(HttpServletRequest request , @RequestHeader(value = "userId" , required = false )String userId){
        // controller 不做业务处理
        Object object =  appHomeService.getAppHomeData(request,userId);
        return object;
    }

}
