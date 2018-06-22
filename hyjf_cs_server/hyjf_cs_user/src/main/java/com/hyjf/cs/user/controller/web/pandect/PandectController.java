/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.controller.web.pandect;

import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.enums.MsgEnum;
import com.hyjf.common.validator.CheckUtil;
import com.hyjf.cs.common.bean.result.WebResult;
import com.hyjf.cs.user.service.pandect.PandectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhangqingqing
 * @version PandectController, v0.1 2018/6/21 14:31
 */
@Api(value = "web端授权状态查询")
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/web/user")
public class PandectController {
    private static final Logger logger = LoggerFactory.getLogger(PandectController.class);

    @Autowired
    PandectService pandectService;

    /**
     * 账户总览
     *
     * @param
     * @return
     */
    @ApiOperation(value = "账户总览", notes = "账户总览")
    @PostMapping(value = "/pandect")
    public WebResult pandect(@RequestHeader(value = "token") String token) {
        WebResult<Map<String,Object>> result = new WebResult<>();
        Map<String,Object> map = new HashMap<>();
        UserVO user = pandectService.getUsers(token);
        CheckUtil.check(user!=null, MsgEnum.ERR_USER_NOT_LOGIN);
        map = pandectService.pandect(user);
        result.setData(map);
        return result;
    }
}
