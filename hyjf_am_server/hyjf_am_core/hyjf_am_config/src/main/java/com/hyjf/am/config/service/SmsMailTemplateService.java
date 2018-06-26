/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.config.service;

import com.hyjf.am.config.dao.model.auto.SmsMailTemplate;
import com.hyjf.am.resquest.config.MailTemplateRequest;

import java.util.List;

/**
 * @author fuqiang
 * @version SmsMailTemplateService, v0.1 2018/5/8 16:57
 */
public interface SmsMailTemplateService {
    /**
     * 查询邮件模板
     * @param mailCode
     * @return
     */
    SmsMailTemplate findSmsMailTemplateByCode(String mailCode);

    /**
     * 查询所有邮件模板
     *
     * @return
     */
    List<SmsMailTemplate> findAll();

    /**
     * 根据条件查询邮件模板
     *
     * @param request
     * @return
     */
    List<SmsMailTemplate> findSmsTemplate(MailTemplateRequest request);

    /**
     * 添加短信模板
     *
     * @param request
     */
    void insertMailTemplate(MailTemplateRequest request);
}
