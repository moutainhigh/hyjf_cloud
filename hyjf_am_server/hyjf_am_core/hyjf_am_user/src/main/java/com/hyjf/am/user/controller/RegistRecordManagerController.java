/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.user.*;
import com.hyjf.am.resquest.user.*;
import com.hyjf.am.user.dao.model.customize.*;
import com.hyjf.am.user.service.RegistRecordManagerService;
import com.hyjf.am.vo.user.*;
import com.hyjf.common.paginator.Paginator;
import com.hyjf.common.util.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nxl
 * @version UserMemberController, v0.1 2018/6/20 9:13
 *          后台会员中心-会员管理
 */

@RestController
@RequestMapping("/am-user/registRecord")
public class RegistRecordManagerController {
    @Autowired
    private RegistRecordManagerService registRecordService;
    private static Logger logger = LoggerFactory.getLogger(RegistRecordManagerController.class);

    /**
     * 根据筛选条件查找(用户管理列表显示)
     *
     * @param request
     * @return
     */
    @RequestMapping("/registRecordList")
    public RegistRecordResponse findRegistRecordList(@RequestBody @Valid RegistRcordRequest request) {
        logger.info("---findRegistRecordList by param---  " + JSONObject.toJSON(request));
        Map<String, Object> mapParam = paramSet(request);
        RegistRecordResponse response = new RegistRecordResponse();
        Integer registCount = registRecordService.countRecordTotal(mapParam);
        Paginator paginator = new Paginator(request.getPaginatorPage(), registCount,request.getLimit());
        List<RegistRecordCustomize> registRecordCustomizeList = registRecordService.selectRegistList(mapParam,paginator.getOffset(), paginator.getLimit());
        if(registCount>0){
            if (!CollectionUtils.isEmpty(registRecordCustomizeList)) {
                List<RegistRecordVO> userVoList = CommonUtils.convertBeanList(registRecordCustomizeList, RegistRecordVO.class);
                response.setResultList(userVoList);
                response.setCount(registCount);
                response.setRtn(Response.SUCCESS);//代表成功
            }
        }
        return response;
    }

    /**
     * 获取列表总数
     *
     * @param request
     * @return
     */
    @RequestMapping("/countRecordTotal")
    public UserManagerResponse countRecordTotal(@RequestBody @Valid RegistRcordRequest request) {
        logger.info("---countUserList by param---  " + JSONObject.toJSON(request));
        UserManagerResponse response = new UserManagerResponse();
        Map<String, Object> mapParam = paramSet(request);
        int usesrCount = registRecordService.countRecordTotal(mapParam);
        response.setCount(usesrCount);
        return response;
    }
    /**
     * 查询条件设置
     *
     * @param userRequest
     * @return
     */
    private Map<String, Object> paramSet(RegistRcordRequest userRequest) {
        Map<String, Object> mapParam = new HashMap<String, Object>();
        mapParam.put("regTimeStart", userRequest.getRegTimeStart());
        mapParam.put("regTimeEnd", userRequest.getRegTimeEnd());
        mapParam.put("userName", userRequest.getUserName());
        mapParam.put("mobile", userRequest.getMobile());
        mapParam.put("recommendName", userRequest.getRecommendName());
        mapParam.put("registPlat",userRequest.getRegistPlat());
        return mapParam;
    }

 }
