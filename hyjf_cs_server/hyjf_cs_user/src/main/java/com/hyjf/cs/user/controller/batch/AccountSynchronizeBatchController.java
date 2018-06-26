/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.user.controller.batch;

import com.hyjf.cs.user.service.batch.AccountSynchronizeBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wangjun
 * @version AccountSynchronizeBatchController, v0.1 2018/6/22 10:11
 */
@RestController
@RequestMapping("/cs-user")
public class AccountSynchronizeBatchController {
    private static final Logger logger = LoggerFactory.getLogger(AccountSynchronizeBatchController.class);

    @Autowired
    AccountSynchronizeBatchService accountSynchronizeBatchService;

    @RequestMapping("/batch/accountSynchronize")
    public void accountSynchronize() {
        logger.info("同步银行卡号(每日)定时任务开始....");
        accountSynchronizeBatchService.accountSynchronize();
    }

}