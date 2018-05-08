/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.config.controller;

import com.hyjf.am.config.dao.model.auto.MessagePushTemplate;
import com.hyjf.am.config.service.MessagePushTemplateServcie;
import com.hyjf.am.response.config.MessagePushTemplateResponse;
import com.hyjf.am.vo.config.MessagePushTemplateVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息推送模板
 *
 * @author fuqiang
 * @version MessagePushTemplateController, v0.1 2018/5/8 10:32
 */
@RestController
@RequestMapping("/am-config/messagePushTemplate")
public class MessagePushTemplateController {

    Logger logger = LoggerFactory.getLogger(MessagePushTemplateController.class);

    @Autowired
    private MessagePushTemplateServcie templateServcie;

    /**
     * 根据tplCode查询消息推送模板
     *
     * @param tplCode
     * @return
     */
    @RequestMapping("/findMessagePushTemplateByCode/{tplCode}")
    public MessagePushTemplateResponse findMessagePushTemplateByCode(@PathVariable String tplCode) {
        logger.info("查询消息推送模板开始...");
        MessagePushTemplateResponse response = new MessagePushTemplateResponse();
        MessagePushTemplateVO messagePushTemplateVO = null;
        MessagePushTemplate messagePushTemplate = templateServcie.findMessagePushTemplateByCode(tplCode);
        if (messagePushTemplate != null) {
            messagePushTemplateVO = new MessagePushTemplateVO();
            BeanUtils.copyProperties(messagePushTemplate, messagePushTemplateVO);
        }
        logger.info("messagePushTemplateVO is :{}", messagePushTemplateVO);
        response.setResult(messagePushTemplateVO);
        return response;
    }
}
