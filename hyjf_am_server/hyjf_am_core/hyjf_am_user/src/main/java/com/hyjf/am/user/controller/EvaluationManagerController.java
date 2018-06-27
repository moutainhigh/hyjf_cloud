/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.controller;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.user.EvalationResponse;
import com.hyjf.am.response.user.UserEvalationResultResponse;
import com.hyjf.am.resquest.user.EvalationRequest;
import com.hyjf.am.user.dao.model.auto.UserEvalationResult;
import com.hyjf.am.user.dao.model.customize.EvalationResultCustomize;
import com.hyjf.am.user.service.EvaluationManagerService;
import com.hyjf.am.vo.user.EvalationVO;
import com.hyjf.am.vo.user.UserEvalationResultVO;
import com.hyjf.common.paginator.Paginator;
import com.hyjf.common.util.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
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
 *          后台会员中心-用户测评
 */

@RestController
@RequestMapping("/am-user/evaluationManager")
public class EvaluationManagerController {
    @Autowired
    private EvaluationManagerService evaluationManagerService;
    private static Logger logger = LoggerFactory.getLogger(EvaluationManagerController.class);

    /**
     * 根据筛选条件查找(用户测评列表显示)
     *
     * @param request
     * @return
     */
    @RequestMapping("/selectUserEvalationResultList")
    public EvalationResponse selectUserEvalationResultList(@RequestBody @Valid EvalationRequest request) {
        logger.info("---selectUserEvalationResultList by param---  " + JSONObject.toJSON(request));
        EvalationResponse response = new EvalationResponse();
        String returnCode = Response.FAIL;
        Map<String,Object> mapParam = paramSet(request);
        int usesrCount = evaluationManagerService.countEvalationResultRecord(mapParam);
        Paginator paginator = new Paginator(request.getPaginatorPage(), usesrCount,request.getLimit());
        if(request.getLimit() ==0){
            paginator = new Paginator(request.getPaginatorPage(), usesrCount);
        }
        List<EvalationResultCustomize> userManagerCustomizeList = evaluationManagerService.selectUserEvalationResultList(mapParam,paginator.getOffset(), paginator.getLimit());
        if(usesrCount>0){
            if (!CollectionUtils.isEmpty(userManagerCustomizeList)) {
                List<EvalationVO> userVoList = CommonUtils.convertBeanList(userManagerCustomizeList, EvalationVO.class);
                response.setResultList(userVoList);
                response.setCount(usesrCount);
                returnCode = Response.SUCCESS;

            }
        }
        response.setRtn(returnCode);//代表成功
        return response;
    }

    @RequestMapping("/selectEvaluationDetailById/{userId}")
    public UserEvalationResultResponse selectEvaluationDetailById(@PathVariable String userId) {
        logger.info("---selectEvaluationDetailById ---  " + JSONObject.toJSON(userId));
        UserEvalationResultResponse response = new UserEvalationResultResponse();
        String returnCode = Response.FAIL;
        if(StringUtils.isNotBlank(userId)){
            int userIdInt = Integer.parseInt(userId);
            UserEvalationResult userEvalationResult = evaluationManagerService.selectUserEvalationResultByUserId(userIdInt);
            if(null!=userEvalationResult){
                UserEvalationResultVO userManagerDetailVO = new UserEvalationResultVO();
                BeanUtils.copyProperties(userEvalationResult, userManagerDetailVO);
                response.setResult(userManagerDetailVO);
                returnCode = Response.SUCCESS;
            }
        }
        response.setRtn(returnCode);//代表成功
        return response;
    }

    /**
     * 查询条件设置
     *
     * @param userRequest
     * @return
     */
    private Map<String, Object> paramSet(EvalationRequest userRequest) {
        Map<String, Object> mapParam = new HashMap<String, Object>();
        mapParam.put("userName", userRequest.getUserName());
        mapParam.put("realName", userRequest.getRealName());
        mapParam.put("mobile", userRequest.getMobile());
        mapParam.put("evaluationType", userRequest.getEvaluationType());
        mapParam.put("accountStatus", userRequest.getAccountStatus());
        mapParam.put("evaluationStatus", userRequest.getEvaluationStatus());
        mapParam.put("userProperty", userRequest.getUserProperty());
        return mapParam;
    }
}