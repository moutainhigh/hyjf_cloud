package com.hyjf.cs.borrow.service.impl;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.hyjf.am.vo.message.AppMsMessage;
import com.hyjf.am.vo.message.SmsMessage;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.constants.MessageConstant;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.cs.borrow.mq.AppMessageProducer;
import com.hyjf.cs.borrow.mq.Producer;
import com.hyjf.cs.borrow.mq.SmsProducer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.borrow.dao.model.auto.*;
import com.hyjf.am.user.dao.model.auto.Users;
import com.hyjf.am.user.dao.model.auto.UsersInfo;
import com.hyjf.am.vo.borrow.*;
import com.hyjf.am.vo.user.UserInfoVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.GetOrderIdUtils;
import com.hyjf.common.validator.Validator;
import com.hyjf.cs.borrow.bean.UserDirectRechargeBean;
import com.hyjf.cs.borrow.client.RechargeClient;
import com.hyjf.cs.borrow.config.SystemConfig;
import com.hyjf.cs.borrow.service.RechargeService;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import com.hyjf.pay.lib.bank.util.BankCallMethodConstant;
import com.hyjf.pay.lib.bank.util.BankCallStatusConstant;
import com.hyjf.pay.lib.bank.util.BankCallUtils;

/**
 * 用户充值Service实现类
 * 
 * @author liuyang
 *
 */
@Service
public class RechargeServiceImpl  extends BaseServiceImpl  implements RechargeService  {

	@Autowired
	RechargeClient rechargeClient;

	@Autowired
	private PlatformTransactionManager transactionManager;

	@Autowired
	private TransactionDefinition transactionDefinition;

	@Autowired
	SystemConfig systemConfig;

	@Autowired
	AppMessageProducer appMessageProducer;
	@Autowired
	SmsProducer smsProducer;

	// 充值状态:充值中
	private static final int RECHARGE_STATUS_WAIT = 1;
	// 充值状态:失败
	private static final int RECHARGE_STATUS_FAIL = 3;
	// 充值状态:成功
	private static final int RECHARGE_STATUS_SUCCESS = 2;

	@Override
	public BankCardVO selectBankCardByUserId(Integer userId) {
		BankCardVO bankCard = rechargeClient.selectBankCardByUserId(userId);
		return bankCard;
	}

	@Override
	public BanksConfigVO getBanksConfigByBankId(Integer bankId) {
		BanksConfigVO banksConfig = rechargeClient.getBanksConfigByBankId(bankId);
		return banksConfig;
	}

	@Override
	public CorpOpenAccountRecordVO getCorpOpenAccountRecord(Integer userId) {
		CorpOpenAccountRecordVO corpOpenAccountRecord =  rechargeClient.getCorpOpenAccountRecord(userId);
		return corpOpenAccountRecord;
	}

	@Override
	public AccountVO getAccount(Integer userId) {
		AccountVO account = rechargeClient.getAccount(userId);
		return account;
	}

	@Override
	public UserVO getUsers(Integer userId) {
		UserVO users = rechargeClient.getUsers(userId);
		return users;
	}

	@Override
	public int insertRechargeInfo(BankCallBean bean) {
		int response = rechargeClient.insertRechargeInfo(bean);
		return response;
	}
	/**
	 * 充值后,回调处理
	 *
	 * @param bean
	 * @param params
	 * @return
	 */
	@Override
	public JSONObject handleRechargeInfo(BankCallBean bean, Map<String, String> params) {
		TransactionStatus txStatus = null;
		// 用户Id
		Integer userId = Integer.parseInt(bean.getLogUserId());
		// 充值订单号
		String orderId = bean.getLogOrderId();
		// 当前时间
		int nowTime = GetDate.getNowTime10();
		// 错误信息
		String errorMsg = this.getBankRetMsg(bean.getRetCode());
		// ip
		String ip = params.get("ip");
		// 交易日期
		String txDate = bean.getTxDate();
		// 交易时间
		String txTime = bean.getTxTime();
		// 交易流水号
		String seqNo = bean.getSeqNo();
		// 电子账户
		String accountId = bean.getAccountId();
		// 充值成功
		if (BankCallStatusConstant.RESPCODE_SUCCESS.equals(bean.getRetCode())) {
			// 查询用户账户,为了版本控制,必须把查询用户账户放在最前面
			AccountExample accountExample = new AccountExample();
			AccountExample.Criteria accountCriteria = accountExample.createCriteria();
			accountCriteria.andUserIdEqualTo(userId);
			AccountVO account = rechargeClient.selectByExample(accountExample);
			// 查询充值记录
			AccountRechargeExample example = new AccountRechargeExample();
			example.createCriteria().andNidEqualTo(orderId);
			AccountRechargeVO accountRecharge = rechargeClient.selectByExample(example);// 查询充值记录

			// 如果没有充值记录
			if (accountRecharge != null) {
				//add by cwyang 增加redis防重校验 2017-08-02
				boolean reslut = RedisUtils.tranactionSet("recharge_orderid" + orderId, 10);
				if(!reslut){
					return jsonMessage("充值成功", "0");
				}
				//end
				// 交易金额
				BigDecimal txAmount = bean.getBigDecimal(BankCallConstant.PARAM_TXAMOUNT);
				// 判断充值记录状态
				if (accountRecharge.getStatus() == RECHARGE_STATUS_SUCCESS) {
					// 如果已经是成功状态
					return jsonMessage("充值成功", "0");
				} else {
					// 如果不是成功状态
					try {
						// 开启事务
						txStatus = this.transactionManager.getTransaction(transactionDefinition);
						// 将数据封装更新至充值记录
						AccountRechargeExample accountRechargeExample = new AccountRechargeExample();
						accountRechargeExample.createCriteria().andNidEqualTo(orderId).andStatusEqualTo(accountRecharge.getStatus());
						accountRecharge.setFee(BigDecimal.ZERO); // 费用
						accountRecharge.setDianfuFee(BigDecimal.ZERO);// 商户垫付金额
						accountRecharge.setBalance(txAmount);// 实际到账余额
						accountRecharge.setUpdateTime(nowTime);// 更新时间
						accountRecharge.setStatus(RECHARGE_STATUS_SUCCESS);// 充值状态:0:初始,1:充值中,2:充值成功,3:充值失败
						accountRecharge.setAccountId(accountId);// 电子账户
						accountRecharge.setBankSeqNo(txDate + txTime + seqNo);// 交易流水号
						boolean isAccountRechargeFlag = this.rechargeClient.updateByExampleSelective(accountRecharge, accountRechargeExample) > 0 ? true : false;
						if (!isAccountRechargeFlag) {
							throw new Exception("充值后,回调更新充值记录表失败!" + "充值订单号:" + orderId + ".用户ID:" + userId);
						}
						Account newAccount = new Account();
						// 更新账户信息
						newAccount.setUserId(userId);// 用户Id
						newAccount.setBankTotal(txAmount); // 累加到账户总资产
						newAccount.setBankBalance(txAmount); // 累加可用余额
						newAccount.setBankBalanceCash(txAmount);// 银行账户可用户
						boolean isAccountUpdateFlag = this.rechargeClient.updateBankRechargeSuccess(newAccount) > 0 ? true : false;
						if (!isAccountUpdateFlag) {
							throw new Exception("提现后,更新用户Account表失败!");
						}

						// 重新获取用户账户信息
						account = this.getAccount(userId);
						// 生成交易明细
						AccountList accountList = new AccountList();
						accountList.setNid(orderId);
						accountList.setUserId(userId);
						accountList.setAmount(txAmount);
						accountList.setTxDate(Integer.parseInt(bean.getTxDate()));// 交易日期
						accountList.setTxTime(Integer.parseInt(bean.getTxTime()));// 交易时间
						accountList.setSeqNo(bean.getSeqNo());// 交易流水号
						accountList.setBankSeqNo((bean.getTxDate() + bean.getTxTime() + bean.getSeqNo()));
						accountList.setType(1);
						accountList.setTrade("recharge");
						accountList.setTradeCode("balance");
						accountList.setAccountId(accountId);
						accountList.setBankTotal(account.getBankTotal()); // 银行总资产
						accountList.setBankBalance(account.getBankBalance()); // 银行可用余额
						accountList.setBankFrost(account.getBankFrost());// 银行冻结金额
						accountList.setBankWaitCapital(account.getBankWaitCapital());// 银行待还本金
						accountList.setBankWaitInterest(account.getBankWaitInterest());// 银行待还利息
						accountList.setBankAwaitCapital(account.getBankAwaitCapital());// 银行待收本金
						accountList.setBankAwaitInterest(account.getBankAwaitInterest());// 银行待收利息
						accountList.setBankAwait(account.getBankAwait());// 银行待收总额
						accountList.setBankInterestSum(account.getBankInterestSum()); // 银行累计收益
						accountList.setBankInvestSum(account.getBankInvestSum());// 银行累计投资
						accountList.setBankWaitRepay(account.getBankWaitRepay());// 银行待还金额
						accountList.setPlanBalance(account.getPlanBalance());//汇计划账户可用余额
						accountList.setPlanFrost(account.getPlanFrost());
						accountList.setTotal(account.getTotal());
						accountList.setBalance(account.getBalance());
						accountList.setFrost(account.getFrost());
						accountList.setAwait(account.getAwait());
						accountList.setRepay(account.getRepay());
						accountList.setRemark("快捷充值");
						accountList.setCreateTime(nowTime);
						accountList.setBaseUpdate(nowTime);
						accountList.setOperator(userId + "");
						accountList.setIp(ip);
						accountList.setIsUpdate(0);
						accountList.setBaseUpdate(0);
						accountList.setInterest(null);
						accountList.setWeb(0);
						accountList.setIsBank(1);// 是否是银行的交易记录 0:否 ,1:是
						accountList.setCheckStatus(0);// 对账状态0：未对账 1：已对账
						accountList.setTradeStatus(1);// 成功状态
						// 插入交易明细
						boolean isAccountListUpdateFlag = this.rechargeClient.insertSelective(accountList) > 0 ? true : false;

						if (isAccountListUpdateFlag) {
							// 更新成功
							// 提交事务
							this.transactionManager.commit(txStatus);
							// 如果需要短信
							UserVO users = rechargeClient.getUsers(userId);
							// 可以发送充值短信时
							if (users != null && users.getRechargeSms() != null && users.getRechargeSms() == 0) {
								// 替换参数
								Map<String, String> replaceMap = new HashMap<String, String>();
								replaceMap.put("val_amount", txAmount.toString());
								replaceMap.put("val_fee", "0");
								UserInfoVO info = getUsersInfoByUserId(userId);
								replaceMap.put("val_name", info.getTruename().substring(0, 1));
								replaceMap.put("val_sex", info.getSex() == 2 ? "女士" : "先生");

								SmsMessage smsMessage = new SmsMessage(userId, replaceMap, null, null,
										MessageConstant.SMSSENDFORUSER, null,
										CustomConstants.PARAM_TPL_CHONGZHI_SUCCESS,
										CustomConstants.CHANNEL_TYPE_NORMAL);
								AppMsMessage appMsMessage = new AppMsMessage(userId, replaceMap, null,
										MessageConstant.APPMSSENDFORUSER, CustomConstants.JYTZ_TPL_CHONGZHI_SUCCESS);

								smsProducer.messageSend(new Producer.MassageContent(MQConstant.SMS_CODE_TOPIC,
										JSON.toJSONBytes(smsMessage)));
								appMessageProducer.messageSend(new Producer.MassageContent(MQConstant.APP_MESSAGE_TOPIC,
										JSON.toJSONBytes(appMsMessage)));
							}else{
								// 替换参数
								Map<String, String> replaceMap = new HashMap<String, String>();
								replaceMap.put("val_amount", txAmount.toString());
								replaceMap.put("val_fee", "0");
								UserInfoVO info = getUsersInfoByUserId(userId);
								replaceMap.put("val_name", info.getTruename().substring(0, 1));
								replaceMap.put("val_sex", info.getSex() == 2 ? "女士" : "先生");
								AppMsMessage appMsMessage = new AppMsMessage(userId, replaceMap, null,
										MessageConstant.APPMSSENDFORUSER, CustomConstants.JYTZ_TPL_CHONGZHI_SUCCESS);
								appMessageProducer.messageSend(new Producer.MassageContent(MQConstant.APP_MESSAGE_TOPIC,
										JSON.toJSONBytes(appMsMessage)));
							}
							return jsonMessage("充值成功!", "0");
						} else {
							// 回滚事务
							transactionManager.rollback(txStatus);
							// 查询充值交易状态
                            accountRecharge = rechargeClient.selectByExample(example);// 查询充值记录
							if (RECHARGE_STATUS_SUCCESS == accountRecharge.getStatus()) {
								return jsonMessage("充值成功!", "0");
							} else {
								// 账户数据过期，请查看交易明细 跳转中间页
								return jsonMessage("账户数据过期，请查看交易明细", "1");
							}
						}
					} catch (Exception e) {
						// 回滚事务
						transactionManager.rollback(txStatus);
						System.err.println(e);
					}
				}
			} else {
				System.out.println("充值失败,未查询到相应的充值记录." + "用户ID:" + userId + ",充值订单号:" + orderId);
				return jsonMessage("充值失败,未查询到相应的充值记录", "1");
			}
		} else {
			// 更新订单信息
			AccountRechargeExample example = new AccountRechargeExample();
			example.createCriteria().andNidEqualTo(orderId);
			AccountRechargeVO accountRecharge =this.rechargeClient.selectByExample(example);
			if (accountRecharge != null ) {
				if (RECHARGE_STATUS_WAIT == accountRecharge.getStatus()) {
					// 更新处理状态
					accountRecharge.setStatus(RECHARGE_STATUS_FAIL);// 充值状态:0:初始,1:充值中,2:充值成功,3:充值失败
					accountRecharge.setUpdateTime(nowTime);
					accountRecharge.setMessage(errorMsg);
					accountRecharge.setAccountId(accountId);// 电子账户
					accountRecharge.setBankSeqNo(txDate + txTime + seqNo);// 交易流水号
					this.rechargeClient.updateByPrimaryKeySelective(accountRecharge);
				}
			}
			return jsonMessage(errorMsg, "1");
		}
		return null;
	}


	public String getBankRetMsg(String retCode) {
		//如果错误码不为空
		if (StringUtils.isNotBlank(retCode)) {
			BankReturnCodeConfigExample example = new BankReturnCodeConfigExample();
			example.createCriteria().andRetCodeEqualTo(retCode);
			BankReturnCodeConfigVO codeConfig = this.rechargeClient.getBankReturnCodeConfig(example);
			if (codeConfig != null ) {
				String retMsg = codeConfig.getErrorMsg();
				if (StringUtils.isNotBlank(retMsg)) {
					return retMsg;
				} else {
					return "请联系客服！";
				}
			} else {
				return "请联系客服！";
			}
		} else {
			return "操作失败！";
		}
	}

	/**
	 * 拼装返回信息
	 *
	 * @param errorDesc
	 * @param status
	 * @return
	 */
	public JSONObject jsonMessage(String errorDesc, String error) {
		JSONObject jo = null;
		if (Validator.isNotNull(errorDesc)) {
			jo = new JSONObject();
			jo.put("error", error);
			jo.put("errorDesc", errorDesc);
		}
		return jo;
	}

	/**
	 * 根据用户ID取得用户信息
	 *
	 * @param userId
	 * @return
	 */
	public UserInfoVO getUsersInfoByUserId(Integer userId) {
		if (userId != null) {
			UserInfoVO usersInfo = this.rechargeClient.findUsersInfoById(userId);
			return usersInfo;
		}
		return null;
	}

	@Override
	public ModelAndView insertGetMV(UserDirectRechargeBean rechargeBean) throws Exception {
		ModelAndView mv = new ModelAndView();
		// 充值订单号
		String logOrderId = GetOrderIdUtils.getOrderId2(rechargeBean.getUserId());
		// 充值订单日期
		String orderDate = GetOrderIdUtils.getOrderDate();
		// 调用 2.3.4联机绑定卡到电子账户充值
		BankCallBean bean = new BankCallBean();
		bean.setVersion(BankCallConstant.VERSION_10);// 接口版本号
		bean.setTxCode(BankCallMethodConstant.TXCODE_DIRECT_RECHARGE_PAGE);// 交易代码
		bean.setInstCode(systemConfig.instCode);// 机构代码
		bean.setBankCode(systemConfig.bankCode);// 银行代码
		bean.setTxDate(GetOrderIdUtils.getTxDate()); // 交易日期
		bean.setTxTime(GetOrderIdUtils.getTxTime()); // 交易时间
		bean.setSeqNo(GetOrderIdUtils.getSeqNo(6));// 交易流水号
		bean.setChannel(rechargeBean.getChannel()); // 交易渠道
		bean.setAccountId(rechargeBean.getAccountId()); // 电子账号
		bean.setCardNo(rechargeBean.getCardNo()); // 银行卡号
		bean.setCurrency(BankCallConstant.CURRENCY_TYPE_RMB);
		bean.setTxAmount(rechargeBean.getTxAmount());
		bean.setIdType(BankCallConstant.ID_TYPE_IDCARD); // 证件类型
		bean.setIdNo(rechargeBean.getIdNo()); // 身份证号
		bean.setName(rechargeBean.getName()); // 姓名
		bean.setMobile(rechargeBean.getMobile()); // 手机号
		bean.setForgotPwdUrl(rechargeBean.getForgotPwdUrl());
		bean.setUserIP(rechargeBean.getIp());
		bean.setRetUrl(rechargeBean.getRetUrl());
		bean.setNotifyUrl(rechargeBean.getNotifyUrl());
		bean.setLogOrderId(logOrderId);// 订单号
		bean.setLogOrderDate(orderDate);// 充值订单日期
		bean.setLogUserId(String.valueOf(rechargeBean.getUserId()));
		bean.setLogUserName(rechargeBean.getUserName());
		bean.setLogRemark("充值页面");
		bean.setLogClient(Integer.parseInt(rechargeBean.getPlatform()));// 充值平台
		// 充值成功后跳转的url
		bean.setSuccessfulUrl(bean.getRetUrl()+"&isSuccess=1");
		// 页面调用必须传的
		bean.setLogBankDetailUrl(BankCallConstant.BANK_URL_DIRECT_RECHARGE_PAGE);
		// 插入充值记录
		int result = insertRechargeInfo(bean);
		if (result == 0) {
			throw new Exception("插入充值记录失败,userid=["+rechargeBean.getUserId()+"].accountid=["+rechargeBean.getAccountId()+"]");
		}
		// 跳转到汇付天下画面
		try {
			mv = BankCallUtils.callApi(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return mv;
	}

}
