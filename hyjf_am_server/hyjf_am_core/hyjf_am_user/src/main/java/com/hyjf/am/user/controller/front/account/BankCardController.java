package com.hyjf.am.user.controller.front.account;

import com.hyjf.am.response.Response;
import com.hyjf.am.resquest.user.BankCardRequest;
import com.hyjf.am.user.controller.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.hyjf.am.response.user.BankCardResponse;
import com.hyjf.am.user.dao.model.auto.BankCard;
import com.hyjf.am.user.service.front.account.BankCardService;
import com.hyjf.am.vo.user.BankCardVO;
import com.hyjf.common.util.CommonUtils;

/**
 * 绑卡及解绑卡服务类
 * @author hesy
 */

@RestController
@RequestMapping("/am-user/bankCard")
public class BankCardController extends BaseController {

	@Autowired
	private BankCardService bankCardService;

	/**
	 * 查询用户已绑定的有效卡
	 * @param userId
	 * @param cardNo
	 * @return
	 */
	@RequestMapping("/getBankCard/{userId}/{bankId}")
	public BankCardResponse getBankCard(@PathVariable Integer userId, @PathVariable String bankId) {
		BankCardResponse response = new BankCardResponse();
		BankCard card = bankCardService.getBankCard(userId, bankId);
		if (card != null) {
			response.setResult(CommonUtils.convertBean(card, BankCardVO.class));
		}
		return response;
	}
	/**
	 * 查询用户已绑定的有效卡
	 * @param userId
	 * @return
	 */
	@RequestMapping("/getBankCard/{userId}")
	public BankCardResponse getBankCard(@PathVariable Integer userId) {
		BankCardResponse response = new BankCardResponse();
		BankCard card = bankCardService.getBankCard(userId);
		if (card != null) {
			response.setResult(CommonUtils.convertBean(card, BankCardVO.class));
		}
		return response;
	}

	/**
	 * 根据用户id和银行卡号查询银行卡信息
	 * @auth sunpeikai
	 * @param
	 * @return
	 */
	@PostMapping(value = "/selectBankCardByUserIdAndCardNo")
	public BankCardResponse selectBankCardByUserIdAndCardNo(@RequestBody BankCardRequest request){
		BankCardResponse response = new BankCardResponse();
		BankCard bankCard = bankCardService.selectBankCardByUserIdAndCardNo(request);
		if(bankCard != null){
			BankCardVO bankCardVO = CommonUtils.convertBean(bankCard,BankCardVO.class);
			response.setResult(bankCardVO);
			response.setRtn(Response.SUCCESS);
		}
		return response;
	}
}
