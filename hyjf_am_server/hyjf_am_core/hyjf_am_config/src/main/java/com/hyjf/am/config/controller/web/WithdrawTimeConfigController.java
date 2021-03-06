/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.config.controller.web;

import com.hyjf.am.config.controller.BaseConfigController;
import com.hyjf.am.config.service.WithdrawTimeConfigService;
import com.hyjf.am.response.config.WithdrawTimeConfigResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 提现时间配置Controller
 *
 * @author liuyang
 * @version WithdrawTimeConfigController, v0.1 2019/4/19 9:43
 */
@RestController
@RequestMapping("/am-config/withdrawTimeConfig")
public class WithdrawTimeConfigController extends BaseConfigController {

    @Autowired
    private WithdrawTimeConfigService withdrawTimeConfigService;

    /**
     * 判断某天是工作日还是节假日
     *
     * @param somedate
     * @return
     */
    @GetMapping("/checkSomedayIsWorkDateForWithdraw")
    public WithdrawTimeConfigResponse selectHolidaysConfig() {
        WithdrawTimeConfigResponse response = new WithdrawTimeConfigResponse();
        boolean flag = withdrawTimeConfigService.isWorkdateOnSomeDay();
        response.setWorkDateFlag(flag);
        return response;
    }
}
