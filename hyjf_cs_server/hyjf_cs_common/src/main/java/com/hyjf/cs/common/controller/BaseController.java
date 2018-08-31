/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.common.controller;

import com.hyjf.am.bean.result.BaseResult;
import com.hyjf.common.enums.MsgEnum;
import com.hyjf.common.exception.AMException;
import com.hyjf.common.exception.CheckException;
import com.hyjf.common.exception.ReturnMessageException;
import com.hyjf.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 组合层共用Controller基类
 * @author liubin
 * @version BaseController, v0.1 2018/6/15 19:18
 */
public class BaseController {
    protected static final Logger logger = LoggerFactory.getLogger(BaseController.class);

    // 联调阶段方便前端用错误信息，调试完成后要删除。
    private String getMessageForTest(Exception ex){
        return "---《"+ex.getMessage()+"》";
    }

    /**
     * 组合层Exception错误异常处理（共用）
     * @param request
     * @param response
     * @param ex
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public BaseResult<?> defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        logger.error("组合层发生异常(CS)", ex);
        return new BaseResult<>(MsgEnum.ERR_SYSTEM.getCode(), StringUtil.getEnumMessage(MsgEnum.ERR_SYSTEM) + getMessageForTest(ex));
    }

    /**
     * 原子层Exception错误异常处理（共用）
     * @param request
     * @param response
     * @param ex
     * @return
     */
    @ExceptionHandler(value = AMException.class)
    @ResponseBody
    public BaseResult<?> amExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        logger.error("原子层发生异常(AM)", ex);
        return new BaseResult<>(MsgEnum.ERR_AM_SYSTEM.getCode(), StringUtil.getEnumMessage(MsgEnum.ERR_AM_SYSTEM) + getMessageForTest(ex));
    }

    /**
     * 传入参数类型错误异常处理
     * @param request
     * @param response
     * @param ex
     * @return
     * @author liubin
     */
    @ExceptionHandler(BindException.class)
    @ResponseBody
    public BaseResult<?> bindExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        return new BaseResult<>(MsgEnum.ERR_PARAM_TYPE.getCode(), StringUtil.getEnumMessage(MsgEnum.ERR_PARAM_TYPE));
    }

    /**
     * 传入JSON错误异常处理
     * @param request
     * @param response
     * @param ex
     * @return
     * @author liubin
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public BaseResult<?> httpMessageNotReadableExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        return new BaseResult<>(MsgEnum.ERR_JSON.getCode(), StringUtil.getEnumMessage(MsgEnum.ERR_JSON));
    }

    /**
     * 传入信息验证错误异常处理
     * @param request
     * @param response
     * @param ex
     * @return
     * @author liubin
     */
    @ExceptionHandler(CheckException.class)
    @ResponseBody
    public BaseResult<?> CheckExceptionHandler(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        CheckException e = (CheckException)ex;
        BaseResult<?> result = new BaseResult<>(e.getData());
        result.setStatusInfo(e.getCode(), ex.getLocalizedMessage());
        return result;
    }

    @ExceptionHandler(value = ReturnMessageException.class)
    @ResponseBody
    public BaseResult defaultReturnErrorHandler(HttpServletRequest req, ReturnMessageException e) {
        BaseResult result = new BaseResult<>();
        result.setStatusInfo(e.getError().getCode(), e.getError().getMsg());
        return result;
    }

    /**
     *
     * 特殊字符编码
     * @author pangchengchao
     * @return
     * @throws Exception
     */
    public String strEncode(String str) {
        try {
            str = URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }


}
