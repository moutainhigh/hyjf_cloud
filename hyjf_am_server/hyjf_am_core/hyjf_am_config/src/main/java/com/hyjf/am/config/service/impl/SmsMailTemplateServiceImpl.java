/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.config.service.impl;

import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.hyjf.am.config.dao.mapper.auto.SmsMailTemplateMapper;
import com.hyjf.am.config.dao.model.auto.SmsMailTemplate;
import com.hyjf.am.config.dao.model.auto.SmsMailTemplateExample;
import com.hyjf.am.config.service.SmsMailTemplateService;
import com.hyjf.am.resquest.config.MailTemplateRequest;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.constants.RedisKey;

/**
 * @author fuqiang
 * @version SmsMailTemplateServiceImpl, v0.1 2018/5/8 16:58
 */
@Service
public class SmsMailTemplateServiceImpl implements SmsMailTemplateService {

    @Autowired
    private SmsMailTemplateMapper smsMailTemplateMapper;

    @Override
    public SmsMailTemplate findSmsMailTemplateByCode(String mailCode) {
        SmsMailTemplate smsMailTemplate = RedisUtils.getObj(RedisKey.SMS_MAIL_TEMPLATE, SmsMailTemplate.class);
        if (smsMailTemplate == null) {
            SmsMailTemplateExample example = new SmsMailTemplateExample();
            example.createCriteria().andMailValueEqualTo(mailCode);
            List<SmsMailTemplate> smsMailTemplateList = smsMailTemplateMapper.selectByExample(example);
            if (!CollectionUtils.isEmpty(smsMailTemplateList)) {
                smsMailTemplate = smsMailTemplateList.get(0);
                RedisUtils.setObjEx(RedisKey.SMS_MAIL_TEMPLATE, smsMailTemplate, 24*60*60);
                return smsMailTemplate;
            }
        }
        return null;
    }

	@Override
	public List<SmsMailTemplate> findAll() {
		return smsMailTemplateMapper.selectByExample(new SmsMailTemplateExample());
	}

    @Override
    public List<SmsMailTemplate> findSmsTemplate(MailTemplateRequest request) {
        SmsMailTemplateExample example = new SmsMailTemplateExample();
        SmsMailTemplateExample.Criteria criteria = example.createCriteria();
        if (request != null) {
            if (request.getMailName() != null) {
                criteria.andMailNameEqualTo(request.getMailName());
            }
            if (request.getMailStatus() != null) {
                criteria.andMailStatusEqualTo(request.getMailStatus());
            }
            return smsMailTemplateMapper.selectByExample(example);
        }
        return null;
    }

	@Override
	public void insertMailTemplate(MailTemplateRequest request) {
		if (request != null) {
			SmsMailTemplate smsMailTemplate = new SmsMailTemplate();
			BeanUtils.copyProperties(request, smsMailTemplate);
			smsMailTemplateMapper.insert(smsMailTemplate);
		}
	}
}
