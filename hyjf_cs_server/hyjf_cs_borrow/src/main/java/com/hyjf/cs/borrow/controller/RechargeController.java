package com.hyjf.cs.borrow.controller;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.borrow.dao.model.auto.BankCard;
import com.hyjf.am.user.dao.model.auto.BankOpenAccount;
import com.hyjf.am.user.dao.model.auto.Users;
import com.hyjf.am.user.dao.model.auto.UsersInfo;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.CustomUtil;
import com.hyjf.common.util.PropUtils;
import com.hyjf.common.validator.Validator;
import com.hyjf.cs.borrow.bean.UserDirectRechargeBean;
import com.hyjf.cs.borrow.bean.WebViewUser;
import com.hyjf.cs.borrow.service.RechargeService;
import com.hyjf.cs.borrow.util.UserDirectRechargeDefine;
import com.hyjf.cs.borrow.util.WebUtils;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.bean.BankCallResult;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import com.hyjf.pay.lib.bank.util.BankCallStatusConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户充值Controller
 * 
 * @author
 *
 */
@Controller
@RequestMapping(value = "/bank/web/user/recharge")
public class RechargeController{
	
	Logger Logger = LoggerFactory.getLogger(RechargeController.class);

	@Autowired
	private RechargeService userRechargeService;

	/**
	 *
	 * 页面充值
	 * @author sunss
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping("/page")
	public ModelAndView userAuthInves(HttpServletRequest request, HttpServletResponse response, String mobile, String money) {

		//LogUtil.startLog(UserDirectRechargeDefine.THIS_CLASS, UserDirectRechargeDefine.USER_DIRECT_RECHARGE_PAGE_ACTION);
		ModelAndView modelAndView = new ModelAndView();

		// 用户id
		WebViewUser user = WebUtils.getUser(request);
		if (user == null) {
			modelAndView = new ModelAndView(UserDirectRechargeDefine.DIRECTRE_CHARGE_ERROR_PATH);
			modelAndView.addObject("message", "登录失效，请重新登陆！");
			return modelAndView;
		}
		Users users=userRechargeService.getUsers(user.getUserId());
		if (users.getBankOpenAccount()==0) {// 未开户
			modelAndView = new ModelAndView(UserDirectRechargeDefine.DIRECTRE_CHARGE_ERROR_PATH);
			modelAndView.addObject("message", "用户未开户！");
			return modelAndView;
		}

		BankOpenAccount account = this.userRechargeService.getBankOpenAccount(user.getUserId());

		// 判断用户是否设置过交易密码
		if (users.getIsSetPassword() == 0) {// 未设置交易密码
			modelAndView = new ModelAndView(UserDirectRechargeDefine.DIRECTRE_CHARGE_ERROR_PATH);
			modelAndView.addObject("message", "用户未设置交易密码！");
			return modelAndView;
		}

		// 判断用户是否缴费授权
		//update by jijun 2018/04/09 合规接口改造一期
       /* if (users.getPaymentAuthStatus() != 1) {
            modelAndView = new ModelAndView(UserDirectRechargeDefine.DIRECTRE_CHARGE_ERROR_PATH);
            modelAndView.addObject("message", "用户未进行缴费授权！");
            return modelAndView;
        }*/

		// 根据用户ID查询用户平台银行卡信息
		BankCard bankCard = this.userRechargeService.selectBankCardByUserId(user.getUserId());
		if (bankCard == null) {
			modelAndView = new ModelAndView(UserDirectRechargeDefine.DIRECTRE_CHARGE_ERROR_PATH);
			modelAndView.addObject("message", "查询银行卡信息失败！");
			return modelAndView;
		}

		if (Validator.isNull(money)) {
			modelAndView = new ModelAndView(UserDirectRechargeDefine.DIRECTRE_CHARGE_ERROR_PATH);
			modelAndView.addObject("message", "充值金额不能为空！");
			return modelAndView;
		}
		// 充值金额校验
		if (!money.matches("-?[0-9]+.*[0-9]*")) {
			//logger.info("充值金额格式错误,充值金额:[" + money + "]");
			modelAndView = new ModelAndView(UserDirectRechargeDefine.DIRECTRE_CHARGE_ERROR_PATH);
			modelAndView.addObject("message", "充值金额格式错误！");
			return modelAndView;
		}
		//判断小数位数
		if(money.indexOf(".")>=0){
			String l = money.substring(money.indexOf(".")+1,money.length());
			if(l.length()>2){
				//logger.info("充值金额不能大于两位小数,充值金额:[" + money + "]");
				modelAndView = new ModelAndView(UserDirectRechargeDefine.DIRECTRE_CHARGE_ERROR_PATH);
				modelAndView.addObject("message", "充值值金额不能大于两位小数！");
				return modelAndView;
			}
		}

		String cardNo = bankCard.getCardNo() == null ? "" : bankCard.getCardNo();
		UsersInfo userInfo = this.userRechargeService.getUsersInfoByUserId(user.getUserId());
		String idNo = userInfo.getIdcard();
		String name = userInfo.getTruename();

		// 拼装参数 调用江西银行
		// 同步调用路径
		String retUrl = PropUtils.getSystem(CustomConstants.HYJF_WEB_URL) +
				UserDirectRechargeDefine.REQUEST_MAPPING + UserDirectRechargeDefine.RETURL_SYN_ACTION + ".do?txAmount="+money;
		// 异步调用路
		String bgRetUrl = PropUtils.getSystem(CustomConstants.HYJF_WEB_URL) +
				UserDirectRechargeDefine.REQUEST_MAPPING + UserDirectRechargeDefine.RETURL_ASY_ACTION + ".do?phone="+mobile;

		// 用户ID
		UserDirectRechargeBean directRechargeBean = new UserDirectRechargeBean();
		directRechargeBean.setTxAmount(money);
		// 身份证信息
		directRechargeBean.setIdNo(idNo);
		directRechargeBean.setName(name);
		directRechargeBean.setCardNo(cardNo);
		directRechargeBean.setMobile(mobile);
		directRechargeBean.setUserId(user.getUserId());
		directRechargeBean.setIp(CustomUtil.getIpAddr(request));
		directRechargeBean.setUserName(user.getUsername());
		// 同步 异步
		directRechargeBean.setRetUrl(retUrl);
		directRechargeBean.setNotifyUrl(bgRetUrl);
		directRechargeBean.setPlatform("0");
		directRechargeBean.setChannel(BankCallConstant.CHANNEL_PC);
		directRechargeBean.setAccountId(account.getAccount());
		String forgetPassworedUrl = CustomConstants.FORGET_PASSWORD_URL;
		directRechargeBean.setForgotPwdUrl(forgetPassworedUrl);

		try {
			modelAndView = userRechargeService.insertGetMV(directRechargeBean);
			//LogUtil.endLog(UserDirectRechargeController.class.toString(), UserDirectRechargeDefine.USER_DIRECT_RECHARGE_PAGE_ACTION);
		} catch (Exception e) {
			e.printStackTrace();
			modelAndView = new ModelAndView(UserDirectRechargeDefine.DIRECTRE_CHARGE_ERROR_PATH);
			modelAndView.addObject("message", "调用银行接口失败！");
			//LogUtil.errorLog(UserDirectRechargeController.class.toString(), UserDirectRechargeDefine.USER_DIRECT_RECHARGE_PAGE_ACTION, e);
		}

		return modelAndView;
	}

	/**
	 *
	 * 页面充值同步
	 * @author sunss
	 * @param request
	 * @param response
	 * @param bean
	 * @return
	 */
	@RequestMapping(UserDirectRechargeDefine.RETURL_SYN_ACTION)
	public ModelAndView pageReturn(HttpServletRequest request, HttpServletResponse response,
								   @ModelAttribute BankCallBean bean) {
	//	LogUtil.startLog(UserDirectRechargeDefine.THIS_CLASS, UserDirectRechargeDefine.RETURL_SYN_ACTION, "[页面充值同步回调开始]");
		ModelAndView modelAndView = new ModelAndView();
		String money = request.getParameter("txAmount");
		String frontParams = request.getParameter("frontParams");
		String isSuccess = request.getParameter("isSuccess");
		// 充值成功
		DecimalFormat df = CustomConstants.DF_FOR_VIEW;
		BigDecimal feeAmt = new BigDecimal(money);

		if(StringUtils.isBlank(bean.getRetCode())&&StringUtils.isNotBlank(frontParams)){
			JSONObject jsonParm = JSONObject.parseObject(frontParams);
			if(jsonParm.containsKey("RETCODE")){
				bean.setRetCode(jsonParm.getString("RETCODE"));
			}
		}
		bean.convert();
		// 跳转到成功连接了  说明成功了
		if (isSuccess != null && "1".equals(isSuccess)) {
			// 成功
			modelAndView = new ModelAndView(UserDirectRechargeDefine.DIRECTRE_CHARGE_SUCCESS_PATH);
			modelAndView.addObject("message", "页面充值成功");
			modelAndView.addObject("balance", df.format(feeAmt).toString());
			return modelAndView;
		}

		// 银行返回响应代码
		String retCode = StringUtils.isNotBlank(bean.getRetCode()) ? bean.getRetCode() : "";
	//	_log.info("充值retCode:"+retCode);
		if (bean!=null&& BankCallStatusConstant.RESPCODE_SUCCESS.equals(retCode)) {
			// 成功
			modelAndView = new ModelAndView(UserDirectRechargeDefine.DIRECTRE_CHARGE_SUCCESS_PATH);
			modelAndView.addObject("message", "页面充值成功");
			modelAndView.addObject("balance", df.format(feeAmt).toString());
			return modelAndView;
		} else {
			modelAndView = new ModelAndView(UserDirectRechargeDefine.DIRECTRE_CHARGE_ERROR_PATH);
			modelAndView.addObject("message", userRechargeService.getBankRetMsg(bean.getRetCode()));
			return modelAndView;
		}
	}

	/**
	 * 页面充值异步回调
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(UserDirectRechargeDefine.RETURL_ASY_ACTION)
	public BankCallResult bgreturn(HttpServletRequest request, HttpServletResponse response,
								   @ModelAttribute BankCallBean bean) {
		BankCallResult result = new BankCallResult();
		//LogUtil.startLog(UserDirectRechargeDefine.THIS_CLASS, UserDirectRechargeDefine.RETURL_ASY_ACTION, "[缴费授权异步回调开始]");
		String phone = request.getParameter("phone");
		bean.setMobile(phone);
		bean.convert();
		Integer userId = Integer.parseInt(bean.getLogUserId()); // 用户ID
		// 查询用户开户状态
		Users user = this.userRechargeService.getUsers(userId);
		Map<String, String> params = new HashMap<String, String>();
		params.put("ip", bean.getUserIP());
		params.put("mobile",bean.getMobile());
		// 成功
		if (user!=null&&bean != null && BankCallConstant.RESPCODE_SUCCESS.equals(bean.get(BankCallConstant.PARAM_RETCODE))) {
			JSONObject msg = this.userRechargeService.handleRechargeInfo(bean, params);
			// 充值成功
			if (msg != null && msg.get("error").equals("0")) {
			//	logger.info("充值成功,手机号:[" + bean.getMobile() + "],用户ID:[" + userId + "],充值金额:[" + bean.getTxAmount() + "]");
				result.setMessage("充值成功");
				result.setStatus(true);
				return result;
			} else {
				result.setMessage("充值失败");
				result.setStatus(false);
				return result;
			}
		}
	//	LogUtil.endLog(UserDirectRechargeDefine.THIS_CLASS, UserDirectRechargeDefine.RETURL_ASY_ACTION, "[用户充值完成后,回调结束]");
		result.setMessage("充值失败");
		result.setStatus(false);
		return result;
	}
}
