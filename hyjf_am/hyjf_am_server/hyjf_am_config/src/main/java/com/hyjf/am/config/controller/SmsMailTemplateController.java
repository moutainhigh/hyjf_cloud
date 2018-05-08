/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.config.controller;

import com.hyjf.am.config.dao.model.auto.SmsMailTemplate;
import com.hyjf.am.config.service.SmsMailTemplateService;
import com.hyjf.am.response.config.SmsMailTemplateResponse;
import com.hyjf.am.vo.config.SmsMailTemplateVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fuqiang
 * @version SmsMailTemplateController, v0.1 2018/5/8 16:10
 */
@RestController
@RequestMapping("/am-config/smsMailTemplate")
public class SmsMailTemplateController {

    private Logger logger = LoggerFactory.getLogger(SmsMailTemplateController.class);

    @Autowired
    private SmsMailTemplateService smsMailTemplateService;

    @RequestMapping("/findSmsMailByCode/{mailCode}")
    public SmsMailTemplateResponse findSmsMailTemplateByCode(@PathVariable String mailCode) {
        logger.info("查询邮件模板开始...");
        SmsMailTemplateResponse response = new SmsMailTemplateResponse();
        SmsMailTemplateVO smsMailTemplateVO = null;
        SmsMailTemplate smsMailTemplate = smsMailTemplateService.findSmsMailTemplateByCode(mailCode);
        if (smsMailTemplate != null) {
            smsMailTemplateVO = new SmsMailTemplateVO();
            BeanUtils.copyProperties(smsMailTemplate, smsMailTemplateVO);
        }
        logger.info("smsMailTemplateVO is :{}", smsMailTemplateVO);
        response.setResult(smsMailTemplateVO);
        return response;
    }

}
