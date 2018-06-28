/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.controller.api.group;

import com.alibaba.fastjson.JSON;
import com.hyjf.am.vo.user.OrganizationStructureVO;
import com.hyjf.common.enums.MsgEnum;
import com.hyjf.cs.common.bean.result.ApiResult;
import com.hyjf.cs.user.bean.OrganizationStructureRequestBean;
import com.hyjf.cs.user.controller.BaseUserController;
import com.hyjf.cs.user.service.group.ApiOrganizationStructureService;
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
import java.util.List;

/**
 * @author: sunpeikai
 * @version: ApiGroupQueryController, v0.1 2018/6/27 9:35
 */
@Api(value = "api端集团组织机构查询")
@RestController
@RequestMapping("/api/user")
public class ApiOrganizationStructureController extends BaseUserController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    ApiOrganizationStructureService apiGroupQueryService;

    /**
     * @Author: 孙沛凯
     * @Desc :集团组织机构查询
     * @Param: * @param OrganizationStructureRequestBean
     * @Date: 9:40 2018/6/27
     * @Return: * @Return List<OrganizationStructureVO>
     */
    @ApiOperation(value = "集团组织机构查询", notes = "集团组织机构查询")
    @PostMapping(value = "/syncCompanyInfo", produces = "application/json; charset=utf-8")
    public ApiResult<List<OrganizationStructureVO>> queryInfo(@RequestBody @Valid OrganizationStructureRequestBean bean){
        ApiResult<List<OrganizationStructureVO>> result = new ApiResult<>();
        log.info("queryInfo::::::::::::::bean::::::{}", JSON.toJSONString(bean));
        //校验签名
        List<OrganizationStructureVO> resultBean = apiGroupQueryService.queryInfo(bean);
        if (null != resultBean) {
            result.setData(resultBean);

        } else {
            result.setStatus(ApiResult.FAIL);
            result.setStatusDesc(MsgEnum.ERR_INSTCODE.getMsg());//机构编号错误
        }
        return result;
    }
}