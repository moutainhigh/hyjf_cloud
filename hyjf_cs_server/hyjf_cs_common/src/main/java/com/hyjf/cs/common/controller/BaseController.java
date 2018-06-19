/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.common.controller;

import com.hyjf.common.enums.utils.MsgEnum;
import com.hyjf.common.exception.CheckException;
import com.hyjf.common.util.StringUtil;
import com.hyjf.common.validator.CheckUtil;
import com.hyjf.cs.common.bean.result.BaseResult;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 组合层共用Controller基类
 * @author liubin
 * @version BaseController, v0.1 2018/6/15 19:18
 */
public class BaseController {

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
        return new BaseResult<>(BaseResult.FAIL, StringUtil.getEnumMessage(MsgEnum.ERR_PARAM_TYPE));
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
        return new BaseResult<>(BaseResult.FAIL, StringUtil.getEnumMessage(MsgEnum.ERR_JSON));
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
        // 设置接口结果页信息
        result = new BaseResult<>(e.getData());
        result.setStatusInfo(BaseResult.FAIL, ex.getLocalizedMessage());
        return result;
    }
}