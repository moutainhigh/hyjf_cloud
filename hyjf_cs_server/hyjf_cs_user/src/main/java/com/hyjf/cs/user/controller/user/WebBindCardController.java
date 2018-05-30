package com.hyjf.cs.user.controller.user;

import java.text.ParseException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.cs.user.constants.BindCardError;
import com.hyjf.cs.user.result.ApiResult;
import com.hyjf.cs.user.service.BindCardService;
import com.hyjf.cs.user.util.GetCilentIP;
import com.hyjf.cs.user.vo.BindCardVO;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.util.BankCallStatusConstant;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * web端用户解绑卡接口
 * @author hesy
 */
@Api(value = "web端用户解绑卡接口")
@RestController
@RequestMapping("/web/card")
public class WebBindCardController {
	private static final Logger logger = LoggerFactory.getLogger(WebBindCardController.class);
	
	@Autowired
	BindCardService bindCardService;
	
	@ApiOperation(value = "用户绑卡", notes = "用户绑卡")
	@PostMapping(value = "/bindCard", produces = "application/json; charset=utf-8")
	public ApiResult bindCard(@RequestBody @Valid BindCardVO bindCardVO, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("绑卡开始, bindCardVO :{}", JSONObject.toJSONString(bindCardVO));
		ApiResult result = new ApiResult();
		
        Integer userId = Integer.parseInt(request.getParameter("userId"));
        String userIp = GetCilentIP.getIpAddr(request);
        
        bindCardService.checkParamBindCard(bindCardVO, userId);
        
        // 请求银行绑卡接口
        BankCallBean bankBean = null;
		try {
			bankBean = bindCardService.callBankBindCard(bindCardVO, userId, userIp);
		} catch (Exception e) {
			result.setStatus(ApiResult.STATUS_FAIL);
			result.setStatusDesc(BindCardError.BANK_CALL_ERROR.getMessage());
			logger.error("请求绑卡接口发生异常", e);
		}
        
        if(bankBean == null || !(BankCallStatusConstant.RESPCODE_SUCCESS.equals(bankBean.getRetCode()))) {
        	result.setStatus(ApiResult.STATUS_FAIL);
			result.setStatusDesc(BindCardError.BANK_CALL_ERROR.getMessage());
			logger.error("请求绑卡接口失败");
        }
        
        // 绑卡请求后业务处理
        try {
			bindCardService.updateAfterBindCard(bankBean);
		} catch (ParseException e) {
			result.setStatus(ApiResult.STATUS_FAIL);
			result.setStatusDesc(BindCardError.CARD_SAVE_ERROR.getMessage());
			logger.error("绑卡后处理异常", e);
		}
        
		return result;
	}
	
	@ApiOperation(value = "用户解绑卡", notes = "用户解绑卡")
	@PostMapping(value = "/unBindCard", produces = "application/json; charset=utf-8")
	public ApiResult unBindCard(@RequestBody @Valid BindCardVO bindCardVO, HttpServletRequest request,
			HttpServletResponse response) {
		logger.info("解绑卡开始, bindCardVO :{}", JSONObject.toJSONString(bindCardVO));
		ApiResult result = new ApiResult();
		
        Integer userId = Integer.parseInt(request.getParameter("userId"));
        String userIp = GetCilentIP.getIpAddr(request);
        
        bindCardService.checkParamUnBindCard(bindCardVO, userId);
        
        // 请求银行绑卡接口
        BankCallBean bankBean = null;
		try {
			bankBean = bindCardService.callBankUnBindCard(bindCardVO, userId);
		} catch (Exception e) {
			result.setStatus(ApiResult.STATUS_FAIL);
			result.setStatusDesc(BindCardError.BANK_CALL_ERROR.getMessage());
			logger.error("请求解绑卡接口发生异常", e);
		}
        
        if(bankBean == null || !(BankCallStatusConstant.RESPCODE_SUCCESS.equals(bankBean.getRetCode()))) {
        	result.setStatus(ApiResult.STATUS_FAIL);
			result.setStatusDesc(BindCardError.BANK_CALL_ERROR.getMessage());
			logger.error("请求解绑卡接口失败");
        }
        
        // 绑卡请求后业务处理
        try {
			bindCardService.updateAfterUnBindCard(bankBean);
		} catch (Exception e) {
			result.setStatus(ApiResult.STATUS_FAIL);
			result.setStatusDesc(BindCardError.CARD_SAVE_ERROR.getMessage());
			logger.error("解绑卡后处理异常", e);
		}
        
		return result;
	}
	
	

}

	