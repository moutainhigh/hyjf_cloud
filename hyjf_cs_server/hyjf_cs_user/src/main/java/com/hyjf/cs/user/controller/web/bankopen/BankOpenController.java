package com.hyjf.cs.user.controller.web.bankopen;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.CustomUtil;
import com.hyjf.common.util.GetOrderIdUtils;
import com.hyjf.common.util.PropUtils;
import com.hyjf.common.validator.Validator;
import com.hyjf.common.validator.ValidatorCheckUtil;
import com.hyjf.cs.user.bean.OpenAccountPageBean;
import com.hyjf.cs.user.config.SystemConfig;
import com.hyjf.cs.user.service.bankopen.BankOpenService;
import com.hyjf.cs.user.vo.BankOpenVO;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import com.hyjf.pay.lib.bank.util.BankCallUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.lang.reflect.InvocationTargetException;

/**
 * 
 * @author sunss
 *
 */
@Api(value = "web端用户开户接口")
@Controller
@RequestMapping("/web/secure/open")
public class BankOpenController {
	private static final Logger logger = LoggerFactory.getLogger(BankOpenController.class);

	@Autowired
	private BankOpenService bankOpenService;

	@Autowired
	SystemConfig systemConfig;

	@GetMapping(value = "/init")
	public String init(Model model) {
		return "bankopen/init";
	}

	/**
	 * @Description 开户
	 * @Author sunss
	 * @Version v0.1
	 * @Date 2018/6/12 10:17
	 */
    @ApiOperation(value = "web端用户开户", notes = "用户开户")
	@PostMapping(value = "/openBankAccount")
	public ModelAndView openBankAccount(@RequestHeader(value = "token", required = true) String token, @RequestBody @Valid BankOpenVO bankOpenVO, HttpServletRequest request, Model model) {
        logger.info("openBankAccount start, bankOpenVO is :{}", JSONObject.toJSONString(bankOpenVO));
		
		ModelAndView reuslt = new ModelAndView("bankopen/error");

        // 验证请求参数
        if (token == null) {
        	model.addAttribute("message", "用户未登陆，请先登陆！");
            return reuslt;
        }
        
        UserVO user = this.bankOpenService.getUsers(token);
        
        if (user == null) {
        	model.addAttribute("message", "获取用户信息失败！");
            return reuslt;
        }
        
        // 手机号
        if (StringUtils.isEmpty(bankOpenVO.getMobile())) {
            model.addAttribute("message", "手机号不能为空！");
            return reuslt;
        }
        if (StringUtils.isEmpty(bankOpenVO.getCardNo())) {
            model.addAttribute("message", "银行卡号不能为空！");
            return reuslt;
        }
        // 姓名
        if (StringUtils.isEmpty(bankOpenVO.getTrueName())) {
            model.addAttribute("message", "真实姓名不能为空！");
            return reuslt;
        }else{
            //判断真实姓名是否包含空格
            if (!ValidatorCheckUtil.verfiyChinaFormat(bankOpenVO.getTrueName())) {
                model.addAttribute("message", "真实姓名不能包含空格！");
                return reuslt;
            }
            //判断真实姓名的长度,不能超过10位
            if (bankOpenVO.getTrueName().length() > 10) {
                model.addAttribute("message", "真实姓名不能超过10位！");
                return reuslt;
            }
        }
        // 身份证号
        if (StringUtils.isEmpty(bankOpenVO.getIdNo())) {
            model.addAttribute("message", "身份证号不能为空！");
            return reuslt;
        }

        if (bankOpenVO.getIdNo().length() != 18) {
            model.addAttribute("message", "身份证号格式不正确！");
            return reuslt;
        }
        String idNo = bankOpenVO.getIdNo().toUpperCase().trim();
        bankOpenVO.setIdNo(idNo);
        //增加身份证唯一性校验
        boolean isOnly = bankOpenService.checkIdNo(idNo);
        if (!isOnly) {
            model.addAttribute("message", "身份证已存在！");
            return reuslt;
        }
        if(!Validator.isMobile(bankOpenVO.getMobile())){
            model.addAttribute("message", "手机号格式错误！");
            return reuslt;
        }
        String mobile = user.getMobile();
        if (StringUtils.isBlank(mobile)) {
            if (StringUtils.isNotBlank(bankOpenVO.getMobile())) {
                if(!bankOpenService.existUser(bankOpenVO.getMobile())){
                    mobile = bankOpenVO.getMobile();
                }else{
                    model.addAttribute("message", "用户信息错误，手机号码重复！");
                    return reuslt;
                }
            } else {
                model.addAttribute("message", "用户信息错误，未获取到用户的手机号码！");
                return reuslt;
            }
        } else {
            if (StringUtils.isNotBlank(bankOpenVO.getMobile()) && !mobile.equals(bankOpenVO.getMobile())) {
                model.addAttribute("message", "用户信息错误，用户的手机号码错误！");
                return reuslt;
            }
        }
        // 拼装参数 调用江西银行
        // 同步调用路径
        String retUrl = systemConfig.getWebHost() + "/web/secure/open/bankOpenReturn";
        // 异步调用路
        String bgRetUrl = systemConfig.getWebHost() + "/web/secure/open/bankOpenBgreturn" + "?phone="+bankOpenVO.getMobile();

        OpenAccountPageBean openBean = new OpenAccountPageBean();
        
        try {
			PropertyUtils.copyProperties(openBean, bankOpenVO);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
        
        openBean.setChannel(BankCallConstant.CHANNEL_PC);
        openBean.setUserId(user.getUserId());
        openBean.setIp(CustomUtil.getIpAddr(request));
        // 同步 异步
        openBean.setRetUrl(retUrl);
        openBean.setNotifyUrl(bgRetUrl);
        openBean.setCoinstName("汇盈金服");
        openBean.setPlatform("0");
        // 账户用途 写死
        /*00000-普通账户
        10000-红包账户（只能有一个）
        01000-手续费账户（只能有一个）
        00100-担保账户*/
        openBean.setAcctUse("00000");
        /**
         *  1：出借角色
            2：借款角色
            3：代偿角色
         */
        openBean.setIdentity("1");
        reuslt = getCallbankMV(openBean);
        //保存开户日志
        int uflag = this.bankOpenService.updateUserAccountLog(user.getUserId(), user.getUsername(), openBean.getMobile(), openBean.getOrderId(),CustomConstants.CLIENT_PC ,openBean.getTrueName(),openBean.getIdNo(),openBean.getCardNo());
        if (uflag == 0) {
            logger.info("保存开户日志失败,手机号:[" + openBean.getMobile() + "],用户ID:[" + user.getUserId() + "]");
            model.addAttribute("message", "操作失败！");
            return reuslt;
        }
        logger.info("开户end");
    

		return reuslt;
	}
	

    public ModelAndView getCallbankMV(OpenAccountPageBean openBean) {
        ModelAndView mv = new ModelAndView();
        // 根据身份证号码获取性别
        String gender = "";
        int sexInt = Integer.parseInt(openBean.getIdNo().substring(16, 17));
        if (sexInt % 2 == 0) {
            gender = "F";
        } else {
            gender = "M";
        }
        // 获取共同参数
        String bankCode = PropUtils.getSystem(BankCallConstant.BANK_BANKCODE);
        String bankInstCode = PropUtils.getSystem(BankCallConstant.BANK_INSTCODE);
        String orderDate = GetOrderIdUtils.getOrderDate();
        String txDate = GetOrderIdUtils.getTxDate();
        String txTime = GetOrderIdUtils.getTxTime();
        String seqNo = GetOrderIdUtils.getSeqNo(6);
        String idType = BankCallConstant.ID_TYPE_IDCARD;
        // 调用开户接口
        BankCallBean openAccoutBean = new BankCallBean();
        openAccoutBean.setVersion(BankCallConstant.VERSION_10);
        openAccoutBean.setTxCode(BankCallConstant.TXCODE_ACCOUNT_OPEN_PAGE);
        openAccoutBean.setInstCode(bankInstCode);
        openAccoutBean.setBankCode(bankCode);
        openAccoutBean.setTxDate(txDate);
        openAccoutBean.setTxTime(txTime);
        openAccoutBean.setSeqNo(seqNo);
        openAccoutBean.setChannel(openBean.getChannel());
        openAccoutBean.setIdType(idType);
        openAccoutBean.setIdNo(openBean.getIdNo());
        openAccoutBean.setName(openBean.getTrueName());
        openAccoutBean.setGender(gender);
        openAccoutBean.setMobile(openBean.getMobile());
        openAccoutBean.setAcctUse(openBean.getAcctUse());
        openAccoutBean.setIdentity(openBean.getIdentity());
        openAccoutBean.setRetUrl(openBean.getRetUrl());
        openAccoutBean.setNotifyUrl(openBean.getNotifyUrl());
        openAccoutBean.setCoinstName(openBean.getCoinstName());
        // 银行卡号
        openAccoutBean.setCardNo(openBean.getCardNo());
        
        // 页面调用必须传的
        String orderId = GetOrderIdUtils.getOrderId2(openBean.getUserId());
        openAccoutBean.setLogBankDetailUrl(BankCallConstant.BANK_URL_ACCOUNT_OPEN_PAGE);
        openAccoutBean.setLogOrderId(orderId);
        openAccoutBean.setLogOrderDate(orderDate);
        openAccoutBean.setLogUserId(String.valueOf(openBean.getUserId()));
        openAccoutBean.setLogRemark("外部服务接口:开户页面");
        openAccoutBean.setLogIp(openBean.getIp());
        openAccoutBean.setLogClient(Integer.parseInt(openBean.getPlatform()));
        openBean.setOrderId(orderId);
        try {
            mv = BankCallUtils.callApi(openAccoutBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mv;
    }
    
}