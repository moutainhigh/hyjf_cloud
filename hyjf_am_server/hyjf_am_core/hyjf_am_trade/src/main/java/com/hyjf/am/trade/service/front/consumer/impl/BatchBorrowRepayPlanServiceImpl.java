package com.hyjf.am.trade.service.front.consumer.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.bean.fdd.FddGenerateContractBean;
import com.hyjf.am.trade.config.SystemConfig;
import com.hyjf.am.trade.dao.model.auto.*;
import com.hyjf.am.trade.mq.base.CommonProducer;
import com.hyjf.am.trade.mq.base.MessageContent;
import com.hyjf.am.trade.service.CommisionComputeService;
import com.hyjf.am.trade.service.front.consumer.BatchBorrowRepayPlanService;
import com.hyjf.am.trade.service.impl.BaseServiceImpl;
import com.hyjf.am.vo.datacollect.AccountWebListVO;
import com.hyjf.am.vo.message.AppMsMessage;
import com.hyjf.am.vo.message.SmsMessage;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.constants.FddGenerateContractConstant;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.constants.MessageConstant;
import com.hyjf.common.exception.MQException;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetCode;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.GetOrderIdUtils;
import com.hyjf.common.validator.Validator;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import com.hyjf.pay.lib.bank.util.BankCallConstant;
import com.hyjf.pay.lib.bank.util.BankCallUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 自动扣款(还款服务)
 *
 * @author dxj
 * 
 */
@Service
public class BatchBorrowRepayPlanServiceImpl extends BaseServiceImpl implements BatchBorrowRepayPlanService {

	/** 等待 */
	private static final String TYPE_WAIT = "wait";
	/** 完成 */
	private static final String TYPE_YES = "yes";
	/** 部分完成 */
	private static final String TYPE_WAIT_YES = "wait_yes";

	/** 用户ID */
	private static final String VAL_USERID = "userId";
	/**汇计划名称*/
	private static final String VAL_HJH_TITLE = "val_hjh_title";
	/**预期收益*/
	private static final String VAL_INTEREST = "val_interest";
	/**预期退出时间*/
	private static final String VAL_DATE = "val_date";
	/** 用户名 */
	private static final String VAL_NAME = "val_name";
	/** 性别 */
	private static final String VAL_SEX = "val_sex";
	/** 放款金额 */
	private static final String VAL_AMOUNT = "val_amount";
    
	@Autowired
	private CommonProducer commonProducer;

    @Autowired
    SystemConfig systemConfig;
	
	@Autowired
	CommisionComputeService commisionComputeService;

	@Override
	public List<BorrowApicron> getBorrowApicronList(Integer apiType) {
		BorrowApicronExample example = new BorrowApicronExample();
		BorrowApicronExample.Criteria criteria = example.createCriteria();
		criteria.andApiTypeEqualTo(apiType);
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(0);
		statusList.add(6);
		criteria.andStatusNotIn(statusList);
		criteria.andPlanNidIsNotNull();
		criteria.andFailTimesLessThanOrEqualTo(2);
		example.setOrderByClause(" id asc ");
		List<BorrowApicron> list = this.borrowApicronMapper.selectByExample(example);
		return list;
	}

	@Override
	public boolean updateBorrowApicron(BorrowApicron apicron, int status) throws Exception {
		
		String borrowNid = apicron.getBorrowNid();
		apicron.setStatus(status);
		apicron.setUpdateTime(new Date());
		boolean apicronFlag = this.borrowApicronMapper.updateByPrimaryKeySelective(apicron) > 0 ? true : false;
		if (!apicronFlag) {
			throw new Exception("更新还款任务失败。[项目编号：" + borrowNid + "]");
		}
		
		Borrow borrow = this.getBorrow(borrowNid);
		borrow.setRepayStatus(status);
		boolean borrowFlag = this.borrowMapper.updateByPrimaryKey(borrow) > 0 ? true : false;
		if (!borrowFlag) {
			throw new Exception("更新borrow失败。[项目编号：" + borrowNid + "]");
		}
		return borrowFlag;
	}

	@Override
	public List<BorrowTender> getBorrowTenderList(String borrowNid) {
		BorrowTenderExample example = new BorrowTenderExample();
		BorrowTenderExample.Criteria criteria = example.createCriteria();
		criteria.andBorrowNidEqualTo(borrowNid);
		List<Integer> statusList = new ArrayList<Integer>();
		statusList.add(0);
		statusList.add(2);
		criteria.andStatusIn(statusList);
		example.setOrderByClause(" id asc ");
		List<BorrowTender> list = this.borrowTenderMapper.selectByExample(example);
		return list;
	}

	@Override
	public Account getAccountByUserId(Integer userId) {
		AccountExample accountExample = new AccountExample();
		accountExample.createCriteria().andUserIdEqualTo(userId);
		List<Account> listAccount = this.accountMapper.selectByExample(accountExample);
		if (listAccount != null && listAccount.size() > 0) {
			return listAccount.get(0);
		}
		return null;
	}

	@Override
	public BankCallBean batchQuery(BorrowApicron apicron) {

		// 获取共同参数
		String batchNo = apicron.getBatchNo();// 还款请求批次号
		String batchTxDate = String.valueOf(apicron.getTxDate());// 还款请求日期
		int userId = apicron.getUserId();
		String channel = BankCallConstant.CHANNEL_PC;

		String logOrderId = GetOrderIdUtils.getOrderId2(userId);
		String orderDate = GetOrderIdUtils.getOrderDate();
		String txDate = GetOrderIdUtils.getTxDate();
		String txTime = GetOrderIdUtils.getTxTime();
		String seqNo = GetOrderIdUtils.getSeqNo(6);
		// 调用还款接口
		BankCallBean repayBean = new BankCallBean();
		repayBean.setVersion(BankCallConstant.VERSION_10);// 接口版本号
		repayBean.setTxCode(BankCallConstant.TXCODE_BATCH_QUERY);// 消息类型(批量还款)
		repayBean.setTxDate(txDate);
		repayBean.setTxTime(txTime);
		repayBean.setSeqNo(seqNo);
		repayBean.setChannel(channel);
		repayBean.setBatchNo(batchNo);
		repayBean.setBatchTxDate(batchTxDate);
		repayBean.setLogUserId(String.valueOf(apicron.getUserId()));
		repayBean.setLogOrderId(logOrderId);
		repayBean.setLogOrderDate(orderDate);
		repayBean.setLogRemark("批次状态查询");
		repayBean.setLogClient(0);
		BankCallBean queryResult = BankCallUtils.callApiBg(repayBean);
		if (queryResult != null && StringUtils.isNotBlank(queryResult.getRetCode())) {
			String retCode = queryResult.getRetCode();
			logger.info(apicron.getBorrowNid()+" 计划还款批次状态查询返回  "+retCode+"  "+queryResult.getRetMsg());
			if (BankCallConstant.RESPCODE_SUCCESS.equals(retCode)) {
				return queryResult;
			}
		}
	
		return null;
	}
	
	
	/**
	 * 根据主键从主库查询apicron 表
	 * 这里不加select是想直接从主库查询
	 * @param id
	 * @return
	 */
	@Override
	public BorrowApicron selApiCronByPrimaryKey(int id) {
		return borrowApicronMapper.selectByPrimaryKey(id);
	}

	@Override
	public boolean updateRecover(BorrowApicron apicron, Borrow borrow, BorrowRecover borrowRecover) throws Exception {
		int periodNow = apicron.getPeriodNow();
		// 还款方式
		String borrowStyle = borrow.getBorrowStyle();
		String borrowNid = borrow.getBorrowNid();
		int tenderUserId = borrowRecover.getUserId();
		String tenderOrderId = borrowRecover.getNid();
		// 是否月标(true:月标, false:天标)
		boolean isMonth = CustomConstants.BORROW_STYLE_PRINCIPAL.equals(borrowStyle) || CustomConstants.BORROW_STYLE_MONTH.equals(borrowStyle)
				|| CustomConstants.BORROW_STYLE_ENDMONTH.equals(borrowStyle);
		if (isMonth) {
			// 取得分期还款计划表
			BorrowRecoverPlan borrowRecoverPlan = getBorrowRecoverPlan(borrowNid, periodNow, tenderUserId, tenderOrderId);
			if (Validator.isNull(borrowRecoverPlan)) {
				throw new RuntimeException("分期还款计划表数据不存在。[借款编号：" + borrowNid + "]，" + "[投资订单号：" + tenderOrderId + "]，" + "[期数：" + periodNow + "]");
			}else {
				borrowRecoverPlan.setRecoverStatus(2);
				boolean flag = this.borrowRecoverPlanMapper.updateByPrimaryKeySelective(borrowRecoverPlan) > 0 ? true : false;
				if (!flag) {
					throw new Exception("更新相应的还款明细失败！项目编号:" + borrowNid + "]");
				}
			}
		} else {
			borrowRecover.setRecoverStatus(2);
			boolean flag = this.borrowRecoverMapper.updateByPrimaryKeySelective(borrowRecover) > 0 ? true : false;
			if (!flag) {
				throw new Exception("更新相应的还款明细失败！项目编号:" + borrowNid + "]");
			}
		}
		return true;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Map requestRepay(BorrowApicron apicron) {

		int repayUserId = apicron.getUserId();// 还款用户userId
		String borrowNid = apicron.getBorrowNid();// 项目编号
		Integer periodNow = apicron.getPeriodNow();// 当前期数
		int isApicronRepayOrgFlag = Validator.isNull(apicron.getIsRepayOrgFlag()) ? 0 : apicron.getIsRepayOrgFlag();// 是否是担保机构还款
		// 取得借款人账户信息
		Account repayAccount = this.getAccountByUserId(repayUserId);
		if (repayAccount == null) {
			throw new RuntimeException("借款人账户不存在。[用户ID：" + repayUserId + "]," + "[借款编号：" + borrowNid + "]");
		}
		// 借款人在汇付的账户信息
		Account repayBankAccount = this.getAccount(repayUserId);
		if (repayBankAccount == null) {
			throw new RuntimeException("借款人未开户。[用户ID：" + repayUserId + "]," + "[借款编号：" + borrowNid + "]");
		}
		String repayAccountId = repayBankAccount.getAccountId();// 借款人相应的银行账号
		// 取得借款详情
		Borrow borrow = this.getBorrow(borrowNid);
		if (borrow == null) {
			throw new RuntimeException("借款详情不存在。[用户ID：" + repayUserId + "]," + "[借款编号：" + borrowNid + "]");
		}
		String borrowStyle = borrow.getBorrowStyle();
		// 标的是否可用担保机构还款
		int isRepayOrgFlag = 0;
		if (isApicronRepayOrgFlag == 1) {
			BorrowInfo borrowInfo = this.getBorrowInfoByNid(borrowNid);
			isRepayOrgFlag = Validator.isNull(borrowInfo.getIsRepayOrgFlag()) ? 0 : borrowInfo.getIsRepayOrgFlag();
		}
		// 是否月标(true:月标, false:天标)
		boolean isMonth = CustomConstants.BORROW_STYLE_PRINCIPAL.equals(borrowStyle) || CustomConstants.BORROW_STYLE_MONTH.equals(borrowStyle)
				|| CustomConstants.BORROW_STYLE_ENDMONTH.equals(borrowStyle);
		// 取得投资详情列表
		List<BorrowRecover> listRecovers = this.getBorrowRecoverList(borrowNid,apicron);
		// 是否有待还款记录
		if (listRecovers == null || listRecovers.size() == 0) {
			//return false;
			throw new RuntimeException("还款记录表数据不存在。[借款编号：" + borrowNid + "]，" + "[期数：" + periodNow + "]"); 
		}
		// 还款请求列表
		List<BorrowRecover> recoverList = new ArrayList<BorrowRecover>();
		// 取得投资详情列表
		List<BorrowRecoverPlan> recoverPlanList = new ArrayList<BorrowRecoverPlan>();
		// 查询此笔债转承接的还款情况
		List<HjhDebtCreditRepay> creditRepayList = new ArrayList<HjhDebtCreditRepay>();
		try {
			/** 循环投资详情列表 */
			for (int i = 0; i < listRecovers.size(); i++) {
				// 获取还款信息
				BorrowRecover borrowRecover = listRecovers.get(i);
				// 投资订单号
				String tenderOrderId = borrowRecover.getNid();
				// 分期的还款信息
				BorrowRecoverPlan borrowRecoverPlan = null;
				BigDecimal recoverPlanAccount = BigDecimal.ZERO;
				BigDecimal sumCreditAccount = BigDecimal.ZERO;
				if (isMonth) {
					// 取得分期还款计划表
					borrowRecoverPlan = this.getBorrowRecoverPlan(borrowNid, periodNow, borrowRecover.getUserId(), tenderOrderId);
					if (Validator.isNull(borrowRecoverPlan)) {
						throw new RuntimeException("分期还款计划表数据不存在。[借款编号：" + borrowNid + "]，" + "[投资订单号：" + tenderOrderId + "]，" + "[期数：" + periodNow + "]");
					}
				 	recoverPlanAccount = borrowRecoverPlan.getRecoverAccount();
					// 保存还款订单号
					if (StringUtils.isBlank(borrowRecoverPlan.getRepayOrderId())) {
						// 设置还款订单号
						borrowRecoverPlan.setRepayOrderId(GetOrderIdUtils.getOrderId2(borrowRecoverPlan.getUserId()));
						// 设置还款时间
						borrowRecoverPlan.setRepayOrderDate(GetOrderIdUtils.getOrderDate());
						// 更新还款信息
						boolean recoverPlanFlag = this.updateBorrowRecoverPlan(borrowRecoverPlan);
						if (!recoverPlanFlag) {
							throw new RuntimeException("添加还款订单号，更新borrow_recover_plan表失败" + "，[投资订单号：" + tenderOrderId + "]");
						}
						// 设置还款订单号
						borrowRecover.setRepayOrdid(borrowRecoverPlan.getRepayOrderId());
						// 设置还款时间
						borrowRecover.setRepayOrddate(borrowRecoverPlan.getRepayOrderDate());
						// 更新还款信息
						boolean flag = this.updateBorrowRecover(borrowRecover);
						if (!flag) {
							throw new RuntimeException("添加还款订单号，更新borrow_recover表失败" + "，[投资订单号：" + tenderOrderId + "]");
						}
					}
					
				}
				// [endday: 按天计息, end:按月计息]
				else {
					// 保存还款订单号
					if (StringUtils.isBlank(borrowRecover.getRepayOrdid())) {
						// 设置还款订单号
						borrowRecover.setRepayOrdid(GetOrderIdUtils.getOrderId2(borrowRecover.getUserId()));
						// 设置还款时间
						borrowRecover.setRepayOrddate(GetOrderIdUtils.getOrderDate());
						// 更新还款信息
						boolean flag = this.updateBorrowRecover(borrowRecover);
						if (!flag) {
							throw new RuntimeException("添加还款订单号，更新borrow_recover表失败" + "，[投资订单号：" + tenderOrderId + "]");
						}
					}
				}
				if (borrowRecover.getCreditAmount().compareTo(BigDecimal.ZERO) > 0) {
					// 查询此笔债转承接的还款情况
					List<HjhDebtCreditRepay> subCreditRepayList = this.selectCreditRepay(borrowNid, tenderOrderId, periodNow,1);
					if (subCreditRepayList != null && subCreditRepayList.size() > 0) {
						for (int j = 0; j < subCreditRepayList.size(); j++) {
							HjhDebtCreditRepay creditRepay = subCreditRepayList.get(j);
							if (StringUtils.isBlank(creditRepay.getCreditRepayOrderId())) {
								// 设置还款订单号
								creditRepay.setCreditRepayOrderId(GetOrderIdUtils.getOrderId2(creditRepay.getUserId()));
								// 设置还款时间
								creditRepay.setCreditRepayOrderDate(GetOrderIdUtils.getOrderDate());
								// 更新还款信息
								boolean flag = this.hjhDebtCreditRepayMapper.updateByPrimaryKeySelective(creditRepay) > 0 ? true : false;
								if (!flag) {
									throw new RuntimeException("添加还款订单号，更新credit_repay表失败" + "，[投资订单号：" + tenderOrderId + "]");
								}
							}
							sumCreditAccount = sumCreditAccount.add(creditRepay.getRepayAccount());
							if (isMonth && Validator.isNotNull(borrowRecoverPlan)) {
								borrowRecoverPlan.setRecoverInterestWait(borrowRecoverPlan.getRecoverInterestWait().subtract(creditRepay.getRepayInterest()));
								borrowRecoverPlan.setRecoverCapitalWait(borrowRecoverPlan.getRecoverCapitalWait().subtract(creditRepay.getRepayCapital()));
								borrowRecoverPlan.setRecoverAccountWait(borrowRecoverPlan.getRecoverAccountWait().subtract(creditRepay.getRepayAccount()));
								borrowRecoverPlan.setRecoverFee(borrowRecoverPlan.getRecoverFee().subtract(creditRepay.getManageFee()));
								borrowRecoverPlan.setChargeInterest(borrowRecoverPlan.getChargeInterest().subtract(creditRepay.getRepayAdvanceInterest()));
								borrowRecoverPlan.setDelayInterest(borrowRecoverPlan.getDelayInterest().subtract(creditRepay.getRepayDelayInterest()));
								borrowRecoverPlan.setLateInterest(borrowRecoverPlan.getLateInterest().subtract(creditRepay.getRepayLateInterest()));
							} else {
								borrowRecover.setRecoverInterestWait(borrowRecover.getRecoverInterestWait().subtract(creditRepay.getRepayInterest()));
								borrowRecover.setRecoverCapitalWait(borrowRecover.getRecoverCapitalWait().subtract(creditRepay.getRepayCapital()));
								borrowRecover.setRecoverAccountWait(borrowRecover.getRecoverAccountWait().subtract(creditRepay.getRepayAccount()));
								borrowRecover.setRecoverFee(borrowRecover.getRecoverFee().subtract(creditRepay.getManageFee()));
								borrowRecover.setChargeInterest(borrowRecover.getChargeInterest().subtract(creditRepay.getRepayAdvanceInterest()));
								borrowRecover.setDelayInterest(borrowRecover.getDelayInterest().subtract(creditRepay.getRepayDelayInterest()));
								borrowRecover.setLateInterest(borrowRecover.getLateInterest().subtract(creditRepay.getRepayLateInterest()));
							}
							creditRepayList.add(creditRepay);
						}
						// 判断是否完全承接 add by cwyang 
						boolean overFlag = isOverUndertake(borrowRecover,recoverPlanAccount,sumCreditAccount,isMonth);
						//如果是完全承接，不添加相应的出让人还款记录
						if (overFlag) {
							if (isMonth) {
								recoverPlanList.add(borrowRecoverPlan);
							} else {
								recoverList.add(borrowRecover);
							}
						}
					}
				} else {
					// 未债转部分添加到还款结果列表
					if (isMonth) {
						recoverPlanList.add(borrowRecoverPlan);
					} else {
						recoverList.add(borrowRecover);
					}
				}
			}
			BankCallBean repayResult = null;
			boolean delFlag = false;
			Map resultMap = null;
			// 调用相应的批次还款接口
			if (isMonth) {
				// 如果是垫付机构还款
				if (isRepayOrgFlag == 1 && isApicronRepayOrgFlag == 1) {
					// 调用相应的担保机构还款接口
					resultMap = this.requestOrgRepay(borrow, repayAccountId, apicron, null, recoverPlanList, creditRepayList);
				} else {
					// 调用相应的非担保机构还款接口
					resultMap = this.requestRepay(borrow, repayAccountId, apicron, null, recoverPlanList, creditRepayList);
				}
			} else {
				// 如果是垫付机构还款
				if (isRepayOrgFlag == 1 && isApicronRepayOrgFlag == 1) {
					// 调用相应的担保机构还款接口
					resultMap = this.requestOrgRepay(borrow, repayAccountId, apicron, recoverList, null, creditRepayList);
				} else {
					// 调用相应的非担保机构还款接口
					resultMap = this.requestRepay(borrow, repayAccountId, apicron, recoverList, null, creditRepayList);
				}
			}
			repayResult = (BankCallBean) resultMap.get("result");
			delFlag = (boolean) resultMap.get("delFlag");
			Map map = new HashMap<>();
			map.put("delFlag", delFlag);
			if(Validator.isNull(repayResult) || StringUtils.isBlank(repayResult.getReceived()) 
					|| !BankCallConstant.RECEIVED_SUCCESS.equals(repayResult.getReceived())){
				map.put("result", false);
			} else {
				map.put("result", true);
			}
			return map;
		} catch (Exception e) {
			logger.error("计划还款中发生系统", e);
			throw new RuntimeException();
			
		}
	}

	/**
	 * 判断是否完全承接  true:未完全承接
	 * @param borrowRecover
	 * @param recoverPlanCapital
	 * @param isMonth
	 * @return
	 */
	private boolean isOverUndertake(BorrowRecover borrowRecover,BigDecimal recoverPlanCapital, BigDecimal creditSumCapital, boolean isMonth) {
		if (isMonth) {//分期标的
			//分期待还金额 大于 承接待还金额
			if (recoverPlanCapital.compareTo(creditSumCapital) > 0) {
				return true;
			}
		}else{
			if (borrowRecover.getRecoverCapital().compareTo(borrowRecover.getCreditAmount()) > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 查询还款请求明细，并且进行更新操作
	 * 
	 * @param apicron
	 * @return
	 */
	@Override
	public boolean reapyBatchDetailsUpdate(BorrowApicron apicron) {

		String borrowNid = apicron.getBorrowNid();// 項目编号
		Borrow borrow = this.getBorrow(borrowNid);
		BorrowInfo borrowInfo = this.getBorrowInfoByNid(borrowNid);
		// 调用批次查询接口查询批次返回结果
		List<BankCallBean> resultBeans = this.queryBatchDetails(apicron);
		// 还款成功后后续操作
		try {
			boolean repayFlag = this.debtRepays(apicron, borrow, borrowInfo, resultBeans);
			if (repayFlag) {
				try {
					boolean borrowFlag = ((BatchBorrowRepayPlanService)AopContext.currentProxy()).updateBorrowStatus(apicron, borrow, borrowInfo);
					if (borrowFlag) {
						return true;
					}
				} catch (Exception e) {
					logger.error("计划还款中发生系统", e);
				}
			}
		} catch (Exception e1) {
			logger.error("还款中发生系统", e1);
		}
		return false;

	}

	/**
	 * 更新相应的债转还款记录
	 * 
	 * @param apicron
	 * 
	 * @param creditRepay
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean updateCreditRepay(BorrowApicron apicron, HjhDebtCreditRepay creditRepay) throws Exception {
		// 更新投资详情表
		creditRepay.setRepayStatus(2);// 状态 0未还款1已还款2还款失败
		int flag = this.hjhDebtCreditRepayMapper.updateByPrimaryKeySelective(creditRepay);
		if (flag > 0) {
			throw new Exception("债转还款记录(credit_repay)更新失败!" + "[投资订单号：" + creditRepay.getAssignOrderId() + "]" + ",期数：" + creditRepay.getRepayPeriod());
		}
		return true;
	}

	/***
	 * 查询相应的债转还款记录
	 * 
	 * @param borrowNid
	 * @param tenderOrderId
	 * @param periodNow
	 * @param flag
	 * @return
	 */
	private List<HjhDebtCreditRepay> selectCreditRepay(String borrowNid, String tenderOrderId, Integer periodNow, Integer flag) {

		HjhDebtCreditRepayExample example = new HjhDebtCreditRepayExample(); 
		//		DebtCreditRepayExample example = new DebtCreditRepayExample();
		HjhDebtCreditRepayExample.Criteria crt = example.createCriteria();
		crt.andBorrowNidEqualTo(borrowNid);
		crt.andInvestOrderIdEqualTo(tenderOrderId);
		crt.andRepayPeriodEqualTo(periodNow);
		if (flag == 1) {//还款申请查询
			crt.andRepayStatusEqualTo(0);
			crt.andDelFlagEqualTo(0);
			crt.andRepayAccountGreaterThanOrEqualTo(BigDecimal.ZERO);
		}else{
			crt.andRepayStatusNotEqualTo(1);
			crt.andDelFlagEqualTo(0);
			crt.andRepayAccountGreaterThanOrEqualTo(BigDecimal.ZERO);
		}
		List<HjhDebtCreditRepay> creditRepayList = this.hjhDebtCreditRepayMapper.selectByExample(example);
		return creditRepayList;
	}

	/**
	 * 
	 * @param borrowRecoverPlan
	 * @return
	 */
	private boolean updateBorrowRecoverPlan(BorrowRecoverPlan borrowRecoverPlan) {
		boolean flag = this.borrowRecoverPlanMapper.updateByPrimaryKeySelective(borrowRecoverPlan) > 0 ? true : false;
		return flag;
	}

	private BorrowRecoverPlan getBorrowRecoverPlan(String borrowNid, Integer periodNow, Integer userId, String tenderOrderId) {

		BorrowRecoverPlanExample example = new BorrowRecoverPlanExample();
		BorrowRecoverPlanExample.Criteria crt = example.createCriteria();
		crt.andBorrowNidEqualTo(borrowNid);
		crt.andRecoverPeriodEqualTo(periodNow);
		crt.andUserIdEqualTo(userId);
		crt.andNidEqualTo(tenderOrderId);
		List<BorrowRecoverPlan> borrowRecoverPlans = this.borrowRecoverPlanMapper.selectByExample(example);
		if (borrowRecoverPlans != null && borrowRecoverPlans.size() == 1) {
			return borrowRecoverPlans.get(0);
		}
		return null;
	}

	/**
	 * 调用机构垫付还款
	 * 
	 * @param borrow
	 * @param borrowAccountId
	 * @param apicron
	 * @param borrowRecoverList
	 * @param borrowRecoverPlanList
	 * @param creditRepayList
	 * @return
	 * @throws Exception
	 */
	/*private BankCallBean requestOrgRepay(Borrow borrow, String repayAccountId, BorrowApicron apicron, List<BorrowRecover> borrowRecoverList, List<BorrowRecoverPlan> borrowRecoverPlanList,
			List<CreditRepay> creditRepayList) throws Exception {
		int txCounts = 0;// 交易笔数
		BigDecimal txAmountSum = BigDecimal.ZERO;// 交易总金额
		BigDecimal repayAmountSum = BigDecimal.ZERO;// 交易总金额
		BigDecimal serviceFeeSum = BigDecimal.ZERO;// 交易总服务费
		JSONArray subPacksJson = new JSONArray();
		if (creditRepayList != null && creditRepayList.size() > 0) {
			for (int i = 0; i < creditRepayList.size(); i++) {
				// 还款信息s
				CreditRepay creditRepay = creditRepayList.get(i);
				int assignUserId = creditRepay.getUserId();// 投资用户userId
				String repayOrderId = creditRepay.getCreditRepayOrderId();// 还款订单号
				String assignOrderId = creditRepay.getAssignNid();// 承接订单号
				CreditTender creditTender = this.getCreditTender(assignOrderId);// 查询原始承接记录
				BigDecimal orgTxAmount = creditTender.getAssignCapital();// 原始承接金额
				BigDecimal txAmount = creditRepay.getAssignRepayAccount();// 交易金额
				BigDecimal txCapAmount = creditRepay.getAssignRepayCapital();// 垫付本金
				BigDecimal txIntAmount = creditRepay.getAssignRepayInterest();// 交易利息
				BigDecimal serviceFee = creditRepay.getManageFee();// 还款管理费
				repayAmountSum = repayAmountSum.add(txAmount).add(txIntAmount).add(serviceFee);
				txAmountSum = txAmountSum.add(txAmount);// 交易总金额
				serviceFeeSum = serviceFeeSum.add(serviceFee);// 总服务费
				Account bankOpenAccount = this.getAccount(assignUserId);
				if (Validator.isNotNull(bankOpenAccount) && StringUtils.isNotBlank(bankOpenAccount.getAccountId())) {
					String accountId = bankOpenAccount.getAccountId();// 银行账户
					// 结果集对象
					JSONObject subJson = new JSONObject();
					subJson.put(BankCallConstant.PARAM_ORDERID, repayOrderId);// 订单号
					subJson.put(BankCallConstant.PARAM_TXAMOUNT, txAmount.toString());// 交易金额
					subJson.put(BankCallConstant.PARAM_TXCAPAMOUT, txCapAmount.toString());// 垫付本金
					subJson.put(BankCallConstant.PARAM_TXINTAMOUNT, txIntAmount.toString());// 垫付利息
					subJson.put(BankCallConstant.PARAM_FORACCOUNTID, accountId);
					subJson.put(BankCallConstant.PARAM_ORGORDERID, assignOrderId);
					subJson.put(BankCallConstant.PARAM_ORGTXAMOUNT, orgTxAmount.toString());
					subPacksJson.add(subJson);
				} else {
					throw new Exception("还款时未查询到用户的银行开户信息。[用户userId：" + assignUserId + "]");
				}
			}
			txCounts = txCounts + creditRepayList.size();
		}
		if (borrowRecoverList != null && borrowRecoverList.size() > 0) {
			for (int i = 0; i < borrowRecoverList.size(); i++) {
				// 还款信息s
				BorrowRecover borrowRecover = borrowRecoverList.get(i);
				int tenderUserId = borrowRecover.getUserId();// 投资用户userId
				String repayOrderId = borrowRecover.getRepayOrdid();// 还款订单号
				String tenderOrderId = borrowRecover.getNid();// 承接订单号
				BorrowTender borrowTender = this.getBorrowTender(tenderOrderId);// 查询原始的投资记录
				BigDecimal orgTxAmount = borrowTender.getAccountId();// 原始投资金额
				BigDecimal txAmount = borrowRecover.getRecoverAccountWait();// 交易金额
				BigDecimal txCapAmount = borrowRecover.getRecoverCapitalWait();// 垫付本金
				BigDecimal txIntAmount = borrowRecover.getRecoverInterestWait();// 交易利息
				BigDecimal serviceFee = borrowRecover.getRecoverFee();// 还款管理费
				repayAmountSum = repayAmountSum.add(txAmount).add(txIntAmount).add(serviceFee);
				txAmountSum = txAmountSum.add(txAmount);// 交易总金额
				serviceFeeSum = serviceFeeSum.add(serviceFee);// 总服务费
				Account bankOpenAccount = this.getAccount(tenderUserId);
				if (Validator.isNotNull(bankOpenAccount) && StringUtils.isNotBlank(bankOpenAccount.getAccountId())) {
					String accountId = bankOpenAccount.getAccountId();// 银行账户
					// 结果集对象
					JSONObject subJson = new JSONObject();
					subJson.put(BankCallConstant.PARAM_ORDERID, repayOrderId);// 订单号
					subJson.put(BankCallConstant.PARAM_TXAMOUNT, txAmount.toString());// 交易金额
					subJson.put(BankCallConstant.PARAM_TXCAPAMOUT, txCapAmount.toString());// 垫付本金
					subJson.put(BankCallConstant.PARAM_TXINTAMOUNT, txIntAmount.toString());// 垫付利息
					subJson.put(BankCallConstant.PARAM_FORACCOUNTID, accountId);
					subJson.put(BankCallConstant.PARAM_ORGORDERID, tenderOrderId);
					subJson.put(BankCallConstant.PARAM_ORGTXAMOUNT, orgTxAmount.toString());
					subPacksJson.add(subJson);
				} else {
					throw new Exception("还款时未查询到用户的银行开户信息。[用户userId：" + tenderUserId + "]");
				}
			}
			txCounts = txCounts + borrowRecoverList.size();
		} else {
			if (borrowRecoverPlanList != null && borrowRecoverPlanList.size() > 0) {
				for (int i = 0; i < borrowRecoverPlanList.size(); i++) {
					// 还款信息s
					BorrowRecoverPlan borrowRecoverPlan = borrowRecoverPlanList.get(i);
					int tenderUserId = borrowRecoverPlan.getUserId();// 投资用户userId
					String repayOrderId = borrowRecoverPlan.getRepayOrderId();// 还款订单号
					String tenderOrderId = borrowRecoverPlan.getNid();// 承接订单号
					BorrowTender borrowTender = this.getBorrowTender(tenderOrderId);// 查询原始的投资记录
					BigDecimal orgTxAmount = borrowTender.getAccountId();// 原始投资金额
					BigDecimal txAmount = borrowRecoverPlan.getRecoverAccountWait();// 交易金额
					BigDecimal txCapAmount = borrowRecoverPlan.getRecoverCapitalWait();// 垫付本金
					BigDecimal txIntAmount = borrowRecoverPlan.getRecoverInterestWait();// 交易利息
					BigDecimal serviceFee = borrowRecoverPlan.getRecoverFee();// 还款管理费
					repayAmountSum = repayAmountSum.add(txAmount).add(txIntAmount).add(serviceFee);
					txAmountSum = txAmountSum.add(txAmount);// 交易总金额
					serviceFeeSum = serviceFeeSum.add(serviceFee);// 总服务费
					Account bankOpenAccount = this.getAccount(tenderUserId);
					if (Validator.isNotNull(bankOpenAccount) && StringUtils.isNotBlank(bankOpenAccount.getAccountId())) {
						String accountId = bankOpenAccount.getAccountId();// 银行账户
						// 结果集对象
						JSONObject subJson = new JSONObject();
						subJson.put(BankCallConstant.PARAM_ORDERID, repayOrderId);// 订单号
						subJson.put(BankCallConstant.PARAM_TXAMOUNT, txAmount.toString());// 交易金额
						subJson.put(BankCallConstant.PARAM_TXCAPAMOUT, txCapAmount.toString());// 垫付本金
						subJson.put(BankCallConstant.PARAM_TXINTAMOUNT, txIntAmount.toString());// 垫付利息
						subJson.put(BankCallConstant.PARAM_FORACCOUNTID, accountId);
						subJson.put(BankCallConstant.PARAM_ORGORDERID, tenderOrderId);
						subJson.put(BankCallConstant.PARAM_ORGTXAMOUNT, orgTxAmount.toString());
						subPacksJson.add(subJson);
					} else {
						throw new Exception("还款时未查询到用户的银行开户信息。[用户userId：" + tenderUserId + "]");
					}
				}
				txCounts = txCounts + borrowRecoverPlanList.size();
			}
		}
		if (apicron.getFailTimes() == 0) {
			apicron.setBatchAmount(repayAmountSum);
			apicron.setBatchCounts(txCounts);
			apicron.setBatchServiceFee(serviceFeeSum);
			apicron.setSucAmount(BigDecimal.ZERO);
			apicron.setSucCounts(0);
			apicron.setFailAmount(BigDecimal.ZERO);
			apicron.setFailCounts(0);
		}
		apicron.setTxAmount(txAmountSum);
		apicron.setTxCounts(txCounts);
		apicron.setServiceFee(serviceFeeSum);
		apicron.setData(" ");
		BankCallBean repayResult = this.requestOrgRepay(apicron, repayAccountId, subPacksJson);
		return repayResult;
	}*/

	/**
	 *  非担保机构还款
	 * 
	 * @param borrow
	 * @param borrowAccountId
	 * @param apicron
	 * @param borrowRecoverList
	 * @param borrowRecoverPlanList
	 * @param creditRepayList
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map requestRepay(Borrow borrow, String borrowAccountId, BorrowApicron apicron, List<BorrowRecover> borrowRecoverList, List<BorrowRecoverPlan> borrowRecoverPlanList,
			List<HjhDebtCreditRepay> creditRepayList) throws Exception {
		String borrowNid = borrow.getBorrowNid();
		boolean delFlag = false;
		Map map = new HashMap<>();
		int txCounts = 0;// 交易笔数
		BigDecimal txAmountSum = BigDecimal.ZERO;// 交易总金额
		BigDecimal repayAmountSum = BigDecimal.ZERO;// 交易总金额
		BigDecimal serviceFeeSum = BigDecimal.ZERO;// 交易总服务费
		JSONArray subPacksJson = new JSONArray();// 拼接结果
		// 债转还款结果
		if (creditRepayList != null && creditRepayList.size() > 0) {
			// 遍历债权还款记录
			for (int i = 0; i < creditRepayList.size(); i++) {
				// 还款信息
				HjhDebtCreditRepay creditRepay = creditRepayList.get(i);
				JSONObject subJson = new JSONObject();// 结果集对象
				int assignUserId = creditRepay.getUserId();// 承接用户userId
				String creditRepayOrderId = creditRepay.getCreditRepayOrderId();// 债转还款订单号
				BigDecimal txAmount = creditRepay.getRepayCapital();// 交易金额
				BigDecimal intAmount = creditRepay.getRepayInterest().add(creditRepay.getRepayAdvanceInterest()).add(creditRepay.getRepayDelayInterest()).add(creditRepay.getRepayLateInterest());// 交易利息
				BigDecimal serviceFee = creditRepay.getManageFee();// 还款管理费
				logger.info("------------------还款开始，标的：" + borrowNid + ",任务表变更金额,增加金额：" + txAmount + "+" + intAmount+"+"+serviceFee + "，原始金额：" + repayAmountSum + ",债转订单号：" + creditRepay.getCreditNid());
				repayAmountSum = repayAmountSum.add(txAmount).add(intAmount).add(serviceFee);
				txAmountSum = txAmountSum.add(txAmount);// 交易总金额
				serviceFeeSum = serviceFeeSum.add(serviceFee);// 总服务费
				String authCode = creditRepay.getAuthCode();// 投资授权码
				// 承接用户的开户信息
				Account bankOpenAccount = this.getAccount(assignUserId);
				// 判断承接用户开户信息
				if (Validator.isNotNull(bankOpenAccount) && StringUtils.isNotBlank(bankOpenAccount.getAccountId())) {
					String accountId = bankOpenAccount.getAccountId();// 银行账户
					subJson.put(BankCallConstant.PARAM_ACCOUNTID, borrowAccountId);// 融资人账号
					subJson.put(BankCallConstant.PARAM_ORDERID, creditRepayOrderId);// 订单号
					subJson.put(BankCallConstant.PARAM_TXAMOUNT, txAmount.toString());// 交易金额
					subJson.put(BankCallConstant.PARAM_INTAMOUNT, intAmount.toString());// 交易金额
					subJson.put(BankCallConstant.PARAM_TXFEEOUT, serviceFee.toString());
					subJson.put(BankCallConstant.PARAM_FORACCOUNTID, accountId);
					subJson.put(BankCallConstant.PARAM_PRODUCTID, borrowNid);
					subJson.put(BankCallConstant.PARAM_AUTHCODE, authCode);
					subPacksJson.add(subJson);
				} else {
					throw new Exception("还款时未查询到用户的银行开户信息。[用户userId：" + assignUserId + "]");
				}
			}
			txCounts = txCounts + creditRepayList.size();
		}
		// 判断标的的放款信息
		if (borrowRecoverList != null && borrowRecoverList.size() > 0) {
			// 遍历标的放款信息
			for (int i = 0; i < borrowRecoverList.size(); i++) {
				// 获取不分期的放款信息
				BorrowRecover borrowRecover = borrowRecoverList.get(i);
				JSONObject subJson = new JSONObject();// 结果集对象
				int tenderUserId = borrowRecover.getUserId();// 投资用户userId
				String recoverRepayOrderId = borrowRecover.getRepayOrdid();// 还款订单号
				BigDecimal txAmount = borrowRecover.getRecoverCapitalWait();// 交易金额
				BigDecimal intAmount = borrowRecover.getRecoverInterestWait().add(borrowRecover.getChargeInterest()).add(borrowRecover.getDelayInterest()).add(borrowRecover.getLateInterest());// 交易利息
				BigDecimal serviceFee = borrowRecover.getRecoverFee();// 还款管理费
				logger.info("------------------还款开始，标的：" + borrowNid + ",任务表变更金额,增加金额：" + txAmount + "+" + intAmount+"+"+serviceFee + "，原始金额：" + repayAmountSum + ",投资订单号：" + borrowRecover.getNid());
				txAmountSum = txAmountSum.add(txAmount);// 交易总金额
				repayAmountSum = repayAmountSum.add(txAmount).add(intAmount).add(serviceFee);
				serviceFeeSum = serviceFeeSum.add(serviceFee);// 总服务费
				String authCode = borrowRecover.getAuthCode();// 投资授权码
				// 投资用户的开户信息
				Account bankOpenAccount = this.getAccount(tenderUserId);
				// 判断投资用户开户信息
				if (Validator.isNotNull(bankOpenAccount) && StringUtils.isNotBlank(bankOpenAccount.getAccountId())) {
					String accountId = bankOpenAccount.getAccountId();// 银行账户
					subJson.put(BankCallConstant.PARAM_ACCOUNTID, borrowAccountId);// 融资人账号
					subJson.put(BankCallConstant.PARAM_ORDERID, recoverRepayOrderId);// 订单号
					subJson.put(BankCallConstant.PARAM_TXAMOUNT, txAmount.toString());// 交易金额
					subJson.put(BankCallConstant.PARAM_INTAMOUNT, intAmount.toString());// 交易金额
					subJson.put(BankCallConstant.PARAM_TXFEEOUT, serviceFee.toString());
					subJson.put(BankCallConstant.PARAM_FORACCOUNTID, accountId);
					subJson.put(BankCallConstant.PARAM_PRODUCTID, borrowNid);
					subJson.put(BankCallConstant.PARAM_AUTHCODE, authCode);
					subPacksJson.add(subJson);
				} else {
					throw new Exception("还款时未查询到用户的银行开户信息。[用户userId：" + tenderUserId + "]");
				}
			}
			txCounts = txCounts + borrowRecoverList.size();
		} else {
			if (borrowRecoverPlanList != null && borrowRecoverPlanList.size() > 0) {
				for (int i = 0; i < borrowRecoverPlanList.size(); i++) {
					// 获取分期的放款信息
					BorrowRecoverPlan borrowRecoverPlan = borrowRecoverPlanList.get(i);
					JSONObject subJson = new JSONObject();// 结果集对象
					int tenderUserId = borrowRecoverPlan.getUserId();// 投资用户userId
					String recoverPlanRepayOrderId = borrowRecoverPlan.getRepayOrderId();
					BigDecimal txAmount = borrowRecoverPlan.getRecoverCapitalWait();// 交易金额
					BigDecimal intAmount = borrowRecoverPlan.getRecoverInterestWait().add(borrowRecoverPlan.getChargeInterest()).add(borrowRecoverPlan.getDelayInterest())
							.add(borrowRecoverPlan.getLateInterest());// 交易利息
					BigDecimal serviceFee = borrowRecoverPlan.getRecoverFee();// 还款管理费
					logger.info("------------------还款开始，标的：" + borrowNid + ",任务表变更金额,增加金额：" + txAmount + "+" + intAmount+"+"+serviceFee + "，原始金额：" + repayAmountSum + ",投资订单号：" + borrowRecoverPlan.getNid());
					repayAmountSum = repayAmountSum.add(txAmount).add(intAmount).add(serviceFee);
					txAmountSum = txAmountSum.add(txAmount);// 交易总金额
					serviceFeeSum = serviceFeeSum.add(serviceFee);// 总服务费
					String authCode = borrowRecoverPlan.getAuthCode();// 投资授权码
					// 投资用户的开户信息
					Account bankOpenAccount = this.getAccount(tenderUserId);
					// 判断投资用户开户信息
					if (Validator.isNotNull(bankOpenAccount) && StringUtils.isNotBlank(bankOpenAccount.getAccountId())) {
						String accountId = bankOpenAccount.getAccountId();// 银行账户
						subJson.put(BankCallConstant.PARAM_ACCOUNTID, borrowAccountId);// 融资人账号
						subJson.put(BankCallConstant.PARAM_ORDERID, recoverPlanRepayOrderId);// 订单号
						subJson.put(BankCallConstant.PARAM_TXAMOUNT, txAmount.toString());// 交易金额
						subJson.put(BankCallConstant.PARAM_INTAMOUNT, intAmount.toString());// 交易金额
						subJson.put(BankCallConstant.PARAM_TXFEEOUT, serviceFee.toString());
						subJson.put(BankCallConstant.PARAM_FORACCOUNTID, accountId);
						subJson.put(BankCallConstant.PARAM_PRODUCTID, borrowNid);
						subJson.put(BankCallConstant.PARAM_AUTHCODE, authCode);
						subPacksJson.add(subJson);
					} else {
						throw new Exception("还款时未查询到用户的银行开户信息。[用户userId：" + tenderUserId + "]");
					}
				}
				txCounts = txCounts + borrowRecoverPlanList.size();
			}
		}
		// 拼接相应的还款参数
		if (apicron.getFailTimes() == 0) {
			apicron.setBatchAmount(repayAmountSum);
			apicron.setBatchCounts(txCounts);
			apicron.setBatchServiceFee(serviceFeeSum);
			apicron.setSucAmount(BigDecimal.ZERO);
			apicron.setSucCounts(0);
			apicron.setFailAmount(repayAmountSum);
			apicron.setFailCounts(txCounts);
		}
		apicron.setServiceFee(serviceFeeSum);
		apicron.setTxAmount(txAmountSum);
		apicron.setTxCounts(txCounts);
		apicron.setData(" ");
 		Map resultMap = this.requestRepay(apicron, subPacksJson);
		BankCallBean repayResult = (BankCallBean) resultMap.get("result");
		delFlag = (boolean) resultMap.get("delFlag");
		try {
			if (Validator.isNotNull(repayResult)) {
				String received = repayResult.getReceived();
				if (StringUtils.isNotBlank(received)) {
					if (BankCallConstant.RECEIVED_SUCCESS.equals(received)) {
						map.put("result", repayResult);
						map.put("delFlag", delFlag);
						return map;
					} else {
						throw new Exception("更新放款任务为放款请求失败失败。[用户ID：" + apicron.getUserId() + "]," + "[借款编号：" + borrowNid + "]");
					}
				} else {
					throw new Exception("放款请求失败。[用户ID：" + apicron.getUserId() + "]," + "[借款编号：" + borrowNid + "]");
				}
			} else {
				throw new Exception("放款请求失败。[用户ID：" + apicron.getUserId()  + "]," + "[借款编号：" + borrowNid + "]");
			}
		} catch (Exception e) {
			logger.info("-----------------------------------放款请求失败,错误信息:" + e.getMessage());
		}
		map.put("result", repayResult);
		map.put("delFlag", delFlag);
		return map;
	}

	/**
	 * 自动扣款（还款）(调用汇付天下接口)
	 *
	 * @param apicron
	 * @return
	 */
	private Map requestRepay(BorrowApicron apicron, JSONArray subPacksJson) {
		Map map = new HashMap<>();
		boolean delFalg = false;
		int userId = apicron.getUserId();// 还款用户userId
		String borrowNid = apicron.getBorrowNid();// 项目编号
		// 还款子参数
		String subPacks = subPacksJson.toJSONString();
		// 获取共同参数
		String notifyUrl = systemConfig.getRepayVerifyUrl();
		String retNotifyURL = systemConfig.getRepayResultUrl();
		String channel = BankCallConstant.CHANNEL_PC;
		for (int i = 0; i < 3; i++) {
			try {
				String logOrderId = GetOrderIdUtils.getOrderId2(apicron.getUserId());
				String orderDate = GetOrderIdUtils.getOrderDate();
				String batchNo = GetOrderIdUtils.getBatchNo();// 获取还款批次号
				String txDate = GetOrderIdUtils.getTxDate();
				String txTime = GetOrderIdUtils.getTxTime();
				String seqNo = GetOrderIdUtils.getSeqNo(6);
				apicron.setBatchNo(batchNo);
				apicron.setTxDate(Integer.parseInt(txDate));
				apicron.setTxTime(Integer.parseInt(txTime));
				apicron.setSeqNo(Integer.parseInt(seqNo));
				apicron.setBankSeqNo(txDate + txTime + seqNo);
				// 更新任务API状态为进行中
				boolean apicronFlag = this.updateBorrowApicron(apicron, CustomConstants.BANK_BATCH_STATUS_SENDING);
				if (!apicronFlag) {
					throw new Exception("更新还款任务为进行中失败。[用户ID：" + userId + "]," + "[借款编号：" + borrowNid + "]");
				}
				// 调用还款接口
				BankCallBean repayBean = new BankCallBean();
				repayBean.setVersion(BankCallConstant.VERSION_10);// 接口版本号
				repayBean.setTxCode(BankCallConstant.TXCODE_BATCH_REPAY);// 消息类型(批量还款)
				repayBean.setTxDate(txDate);
				repayBean.setTxTime(txTime);
				repayBean.setSeqNo(seqNo);
				repayBean.setChannel(channel);
				repayBean.setBatchNo(apicron.getBatchNo());
				repayBean.setTxAmount(String.valueOf(apicron.getTxAmount()));
				repayBean.setTxCounts(String.valueOf(apicron.getTxCounts()));
				repayBean.setNotifyURL(notifyUrl);
				repayBean.setRetNotifyURL(retNotifyURL);
				repayBean.setSubPacks(subPacks);
				repayBean.setLogUserId(String.valueOf(userId));
				repayBean.setLogOrderId(logOrderId);
				repayBean.setLogOrderDate(orderDate);
				repayBean.setLogRemark("批次还款请求");
				repayBean.setLogClient(0);
				logger.info("计划批次还款请求开始！");
				BankCallBean repayResult = BankCallUtils.callApiBg(repayBean);
				if (Validator.isNotNull(repayResult)) {
					String received = StringUtils.isNotBlank(repayResult.getReceived()) ? repayResult.getReceived() : "";
					if (BankCallConstant.RECEIVED_SUCCESS.equals(received)) {
						try {
							// 更新任务API状态
							boolean apicronResultFlag = this.updateBorrowApicron(apicron, CustomConstants.BANK_BATCH_STATUS_SENDED);
							if (apicronResultFlag) {
								map.put("result", repayResult);
								map.put("delFlag", delFalg);
								return map;
							}
						} catch (Exception e) {
							logger.info("------------------------标的号:" + borrowNid + ",还款请求成功后,变更任务状态异常!");
						}
						map.put("result", repayResult);
						map.put("delFlag", true);
						return map;
					} else {
						continue;
					}
				} else {
					continue;
				}
			} catch (Exception e) {
				logger.error("计划还款中发生系统", e);
			}
		}
		map.put("result", null);
		map.put("delFlag", delFalg);
		return map;
	}


	/**
	 * 自动还款
	 *
	 * @throws Exception
	 */
	private boolean debtRepays(BorrowApicron apicron, Borrow borrow, BorrowInfo borrowInfo, List<BankCallBean> resultBeans) throws Exception {

		/** 基本变量 */
		String borrowNid = apicron.getBorrowNid();// 借款编号
		int periodNow = apicron.getPeriodNow();// 当前还款期数
		String borrowStyle = borrow.getBorrowStyle();
		int borrowPeriod = borrow.getBorrowPeriod();// 项目期数
		// 是否月标(true:月标, false:天标)
		boolean isMonth = CustomConstants.BORROW_STYLE_PRINCIPAL.equals(borrowStyle) || CustomConstants.BORROW_STYLE_MONTH.equals(borrowStyle)
				|| CustomConstants.BORROW_STYLE_ENDMONTH.equals(borrowStyle);
		// 剩余还款期数
		Integer periodNext = borrowPeriod - periodNow;
		boolean apicronFlag = this.updateBorrowApicron(apicron, CustomConstants.BANK_BATCH_STATUS_DOING);
		if (!apicronFlag) {
			throw new Exception("更新borrowApicron表失败，" + "[银行唯一订单号：" + apicron.getBankSeqNo() + "]");
		}
		if (Validator.isNotNull(resultBeans) && resultBeans.size() > 0) {
			Map<String, JSONObject> repayResults = new HashMap<String, JSONObject>();
			for (int i = 0; i < resultBeans.size(); i++) {
				BankCallBean resultBean = resultBeans.get(i);
				String subPacks = resultBean.getSubPacks();
				if (StringUtils.isNotBlank(subPacks)) {
					JSONArray repayDetails = JSONObject.parseArray(subPacks);
					for (int j = 0; j < repayDetails.size(); j++) {
						JSONObject repayDetail = repayDetails.getJSONObject(j);
						String repayOrderId = repayDetail.getString(BankCallConstant.PARAM_ORDERID);
						repayResults.put(repayOrderId, repayDetail);
					}
				}
			}
			// 取得投资详情列表
			List<BorrowRecover> borrowRecoverList = this.getBorrowRecoverList(borrowNid,apicron);
			if (borrowRecoverList != null && borrowRecoverList.size() > 0) {
				// 遍历进行还款
				for (int i = 0; i < borrowRecoverList.size(); i++) {
					// 投资明细
					BorrowRecover borrowRecover = borrowRecoverList.get(i);
					String tenderOrderId = borrowRecover.getNid();// 投资订单号
					BigDecimal recoverPlanAccount = BigDecimal.ZERO;
					BigDecimal sumCreditAccount = BigDecimal.ZERO;
					if (isMonth) {
						BorrowRecoverPlanExample example = new BorrowRecoverPlanExample();
						example.createCriteria().andNidEqualTo(tenderOrderId).andRecoverPeriodEqualTo(periodNow);
						List<BorrowRecoverPlan> planList = this.borrowRecoverPlanMapper.selectByExample(example);
						BorrowRecoverPlan planInfo = planList.get(0);
						recoverPlanAccount = planInfo.getRecoverAccount();
					}
					String repayOrderId = borrowRecover.getRepayOrdid();// 还款订单号
					BigDecimal creditAmount = borrowRecover.getCreditAmount();// 债转金额
					if(1 == apicron.getIsAllrepay()){//一次性结清
						repayOrderId = getAllRepayOrdid(borrowNid,periodNow,borrowRecover.getNid());
					}
					JSONObject repayDetail = repayResults.get(repayOrderId);
					// 如果发生了债转，处理相应的债转还款
					if (creditAmount.compareTo(BigDecimal.ZERO) > 0) {
						logger.info("=================标的：" + borrowNid + ",发生债转");
						List<HjhDebtCreditRepay> creditRepayList = this.selectCreditRepay(borrowNid, tenderOrderId, periodNow,0);
						if (creditRepayList != null && creditRepayList.size() > 0) {
							logger.info("=================标的：" + borrowNid + ",发生债转，获取债转还款数量：" + creditRepayList.size());
							boolean creditRepayAllFlag = true;
							boolean creditEndAllFlag = true;
							BigDecimal sumCreditCapital = BigDecimal.ZERO;
							for (int j = 0; j < creditRepayList.size(); j++) {
								HjhDebtCreditRepay creditRepay = creditRepayList.get(j);
								sumCreditAccount = sumCreditAccount.add(creditRepay.getRepayAccount());
								sumCreditCapital = sumCreditCapital.add(creditRepay.getRepayCapital());//累积承接本金
								String assignOrderId = creditRepay.getAssignOrderId();
								int assignUserId = creditRepay.getUserId();
								String creditRepayOrderId = creditRepay.getCreditRepayOrderId();
								JSONObject assignRepayDetail = repayResults.get(creditRepayOrderId);
								if (Validator.isNull(assignRepayDetail)) {
									logger.info("银行端未查詢到相应的还款明细!" + "[投资订单号：" + tenderOrderId + "]");
									continue;
								}
								try {
									String txState = assignRepayDetail.getString(BankCallConstant.PARAM_TXSTATE);// 交易状态
									// 如果处理状态为成功
									if (txState.equals(BankCallConstant.BATCH_TXSTATE_TYPE_SUCCESS)) {
										// 调用债转还款
										boolean creditRepayFlag = ((BatchBorrowRepayPlanService)AopContext.currentProxy()).updateCreditRepay(apicron, borrow, borrowInfo, borrowRecover, creditRepay, assignRepayDetail);
										if (creditRepayFlag) {
											if (!isMonth || (isMonth && periodNext == 0)) {
												// 结束债权
												boolean debtOverFlag = this.requestDebtEnd(creditRepay.getUserId(), assignRepayDetail,assignOrderId,borrow);
												if (debtOverFlag) {
													// 更新相应的债权状态为结束
													boolean debtStatusFlag = this.updateDebtStatus(creditRepay);
													if (!debtStatusFlag) {
														creditEndAllFlag = false;
														throw new Exception("更新相应的债转为债权结束失败!" + "[承接用户：" + assignUserId + "]" + "[承接订单号：" + assignOrderId + "]" + "[还款期数：" + periodNow + "]");
													}
												} else {
													throw new Exception("结束债权失败!" + "[承接用户：" + assignUserId + "]" + "[承接订单号：" + assignOrderId + "]" + "[还款期数：" + periodNow + "]");
												}
											}
										} else {
											creditRepayAllFlag = false;
											throw new Exception("更新相应的债转还款失败!" + "[承接用户：" + assignUserId + "]" + "[承接订单号：" + assignOrderId + "]" + "[还款期数：" + periodNow + "]");
										}
									} else {
										creditRepayAllFlag = false;
										// 更新投资详情表
										boolean borrowTenderFlag = ((BatchBorrowRepayPlanService)AopContext.currentProxy()).updateCreditRepay(apicron, creditRepay);
										if (!borrowTenderFlag) {
											throw new Exception("更新相应的creditrepay失败!" + "[承接订单号：" + assignOrderId + "]" + "[还款期数：" + periodNow + "]");
										}
									}
								} catch (Exception e) {
									logger.error("计划还款中发生系统", e);
                                    creditRepayAllFlag = false;
									continue;
								}
							}
							if (creditRepayAllFlag) {
								logger.info("开始判断是否为完全承接!" + "[投资订单号：" + tenderOrderId + "],原始金额：" + recoverPlanAccount + "，债转金额：" + sumCreditAccount);
								// 判断是否完全承接 add by cwyang
								boolean overFlag = isOverUndertake(borrowRecover,recoverPlanAccount,sumCreditAccount,isMonth);
								if (overFlag) {
									logger.info("完全承接!" + "[投资订单号：" + tenderOrderId + "],原始金额：" + recoverPlanAccount + "，债转金额：" + sumCreditAccount);
									// 如果不是全部债转
									if (Validator.isNull(repayDetail)) {
										logger.info("银行端未查詢到相应的还款明细!" + "[投资订单号：" + tenderOrderId + "]");
										continue;
									}
									String txState = repayDetail.getString(BankCallConstant.PARAM_TXSTATE);// 交易状态
									// 如果处理状态为成功
									if (txState.equals(BankCallConstant.BATCH_TXSTATE_TYPE_SUCCESS)) {
										try {
											boolean tenderRepayFlag = ((BatchBorrowRepayPlanService)AopContext.currentProxy()).updateTenderRepay(apicron, borrow, borrowInfo, borrowRecover, repayDetail, true, sumCreditCapital);
											if (tenderRepayFlag) {
												if (!isMonth || (isMonth && periodNext == 0)) {
													// 结束债权
													if (creditEndAllFlag) {
														boolean debtOverFlag = this.requestDebtEnd(borrowRecover.getUserId(), repayDetail,borrowRecover.getNid(),borrow);
														if (debtOverFlag) {
															// 更新相应的债权状态为结束
															boolean debtStatusFlag = this.updateDebtStatus(borrowRecover, isMonth);
															if (!debtStatusFlag) {
																throw new Exception("更新相应的投资为债权结束失败!" + "[投资订单号：" + tenderOrderId + "]" + "[还款期数：" + periodNow + "]");
															}
														} else {
															throw new Exception("结束债权失败!" + "[投资订单号：" + tenderOrderId + "]" + "[还款期数：" + periodNow + "]");
														}
													}
												}
											} else {
												throw new Exception("还款失败!" + "[投资订单号：" + tenderOrderId + "]");
											}
										} catch (Exception e) {
											logger.error("计划还款中发生系统", e);
											continue;
										}
									} else {
										try {
											// 更新投资详情表
											boolean recoverFlag = ((BatchBorrowRepayPlanService)AopContext.currentProxy()).updateRecover(apicron, borrow, borrowRecover);
											if (!recoverFlag) {
												throw new Exception("还款失败!" + "[投资订单号：" + tenderOrderId + "]");
											}
										} catch (Exception e) {
											logger.error("计划还款中发生系统", e);
											continue;
										}
									}
								} else {
									logger.info("非完全承接!" + "[投资订单号：" + tenderOrderId + "],原始金额：" + recoverPlanAccount + "，债转金额：" + sumCreditAccount);
									try {
										boolean tenderRepayFlag = ((BatchBorrowRepayPlanService)AopContext.currentProxy()).updateTenderRepayStatus(apicron, borrow, borrowRecover);
										if (!tenderRepayFlag) {
											throw new Exception("更新相应的还款信息失败!" + "[投资订单号：" + tenderOrderId + "]");
										}
									} catch (Exception e) {
										logger.error("计划还款中发生系统", e);
										continue;
									}
								}
							} else {
								continue;
							}
						}
					} else {
						if (Validator.isNull(repayDetail)) {
							logger.info("银行端未查詢到相应的还款明细!" + "[投资订单号：" + tenderOrderId + "]");
							continue;
						}
						String txState = repayDetail.getString(BankCallConstant.PARAM_TXSTATE);// 交易状态
						// 如果处理状态为成功
						if (txState.equals(BankCallConstant.BATCH_TXSTATE_TYPE_SUCCESS)) {
							try {
								boolean tenderRepayFlag = ((BatchBorrowRepayPlanService)AopContext.currentProxy()).updateTenderRepay(apicron, borrow, borrowInfo, borrowRecover, repayDetail, false, null);
								if (tenderRepayFlag) {
									if (!isMonth || (isMonth && periodNext == 0)) {
										boolean debtOverFlag = this.requestDebtEnd(borrowRecover.getUserId(), repayDetail,borrowRecover.getNid(),borrow);
										if (debtOverFlag) {
											// 更新相应的债权状态为结束
											boolean debtStatusFlag = this.updateDebtStatus(borrowRecover, isMonth);
											if (!debtStatusFlag) {
												throw new Exception("更新相应的投资为债权结束失败!" + "[投资订单号：" + tenderOrderId + "]" + "[还款期数：" + periodNow + "]");
											}
										} else {
											throw new Exception("结束债权失败!" + "[投资订单号：" + tenderOrderId + "]" + "[还款期数：" + periodNow + "]");
										}
									}
								} else {
									throw new Exception("还款失败!" + "[投资订单号：" + tenderOrderId + "]");
								}
							} catch (Exception e) {
								logger.error("计划还款中发生系统", e);
								continue;
							}
						} else {
							try {
								// 更新投资详情表
								boolean recoverFlag = ((BatchBorrowRepayPlanService)AopContext.currentProxy()).updateRecover(apicron, borrow, borrowRecover);
								if (!recoverFlag) {
									throw new Exception("还款失败!" + "[投资订单号：" + tenderOrderId + "]");
								}
							} catch (Exception e) {
								logger.error("计划还款中发生系统", e);
								continue;
							}
						}
					}
				}
			} else {
				logger.info("未查询到相应的还款记录，项目编号:" + borrowNid + "]");
				return true;
			}
		} else {
			throw new Exception("银行交易明细查询失败！，项目编号:" + borrowNid + "]");
		}
		return true;
	}

	/**
	 * 更新相应的原始投资为债权结束
	 * 
	 * @param borrowRecover
	 * @param isMonth
	 * @return
	 */
	private boolean updateDebtStatus(BorrowRecover borrowRecover, boolean isMonth) {
		borrowRecover.setDebtStatus(1);
		return this.borrowRecoverMapper.updateByPrimaryKeySelective(borrowRecover) > 0 ? true : false;
	}

	/**
	 * 获得一次性结清的当期还款的还款订单号
	 * @param borrowNid
	 * @param periodNow
	 * @param nid
	 * @return
	 */
	private String getAllRepayOrdid(String borrowNid, int periodNow, String nid) {

		BorrowRecoverPlanExample example = new BorrowRecoverPlanExample();
		example.createCriteria().andBorrowNidEqualTo(borrowNid).andRecoverPeriodEqualTo(periodNow).andNidEqualTo(nid);
		List<BorrowRecoverPlan> recoverPlans = this.borrowRecoverPlanMapper.selectByExample(example);
		String repayOrderId = recoverPlans.get(0).getRepayOrderId();
		return repayOrderId;
	}

	/**
	 * 更新相应的债转还款为债权结束
	 * 
	 * @param creditRepay
	 * @return
	 */
	private boolean updateDebtStatus(HjhDebtCreditRepay creditRepay) {

		int userId = creditRepay.getUserId();
		String assignNid = creditRepay.getAssignOrderId();
		HjhDebtCreditRepay repay = new HjhDebtCreditRepay();
		repay.setDebtStatus(1);
		HjhDebtCreditRepayExample example = new HjhDebtCreditRepayExample();
		example.createCriteria().andUserIdEqualTo(userId).andAssignOrderIdEqualTo(assignNid);
		boolean flag = this.hjhDebtCreditRepayMapper.updateByExampleSelective(repay, example ) > 0 ? true : false;
		return flag;
	}


	/**
	 * 结束相应的债权
	 * 
	 * @param repayDetail
	 * @param orgOrderId
	 * @param orgOrderId
	 * @return
	 */
	private boolean requestDebtEnd(Integer userId, JSONObject repayDetail,String orgOrderId, Borrow borrow) {

		String accountId = repayDetail.getString(BankCallConstant.PARAM_FORACCOUNTID);// 投资人账户
		String forAccountId = repayDetail.getString(BankCallConstant.PARAM_ACCOUNTID);// 借款人账户
		String productId = repayDetail.getString(BankCallConstant.PARAM_PRODUCTID);// 项目编号
		String authCode = repayDetail.getString(BankCallConstant.PARAM_AUTHCODE);// 投资授权码
//		String orderId = repayDetail.getString(BankCallConstant.PARAM_ORDERID);// 原始投资订单号
		// 获取共同参数
		String channel = BankCallConstant.CHANNEL_PC;
		// 查询相应的债权状态
		BankCallBean debtQuery = this.debtStatusQuery(userId, accountId, orgOrderId);
		if (Validator.isNotNull(debtQuery)) {
			String queryRetCode = StringUtils.isNotBlank(debtQuery.getRetCode()) ? debtQuery.getRetCode() : "";
			if (BankCallConstant.RESPCODE_SUCCESS.equals(queryRetCode)) {
				String state = StringUtils.isNotBlank(debtQuery.getState()) ? debtQuery.getState() : "";
				if (StringUtils.isNotBlank(state)) {
					if (state.equals("4")) {
						return true;
					} else if (state.equals("2")) {
						try {
							String logOrderId = GetOrderIdUtils.getOrderId2(userId);
							String orderDate = GetOrderIdUtils.getOrderDate();
							String txDate = GetOrderIdUtils.getTxDate();
							String txTime = GetOrderIdUtils.getTxTime();
							String seqNo = GetOrderIdUtils.getSeqNo(6);
							// 调用还款接口
//							BankCallBean debtEndBean = new BankCallBean();
//							debtEndBean.setVersion(BankCallConstant.VERSION_10);// 接口版本号
//							debtEndBean.setTxCode(BankCallConstant.TXCODE_CREDIT_END);// 消息类型(批量还款)
//							debtEndBean.setInstCode(instCode);// 机构代码
//							debtEndBean.setBankCode(bankCode);
//							debtEndBean.setTxDate(txDate);
//							debtEndBean.setTxTime(txTime);
//							debtEndBean.setSeqNo(seqNo);
//							debtEndBean.setChannel(channel);
//							debtEndBean.setAccountId(accountId);
//							debtEndBean.setOrderId(logOrderId);
//							debtEndBean.setForAccountId(forAccountId);
//							debtEndBean.setProductId(productId);
//							debtEndBean.setAuthCode(authCode);
//							debtEndBean.setLogUserId(String.valueOf(userId));
//							debtEndBean.setLogOrderId(logOrderId);
//							debtEndBean.setLogOrderDate(orderDate);
//							debtEndBean.setLogRemark("结束债权请求");
//							debtEndBean.setLogClient(0);
//							BankCallBean repayResult = BankCallUtils.callApiBg(debtEndBean);
//							if (Validator.isNotNull(repayResult)) {
//								String retCode = StringUtils.isNotBlank(repayResult.getRetCode()) ? repayResult.getRetCode() : "";
//								if (BankCallConstant.RESPCODE_SUCCESS.equals(retCode)) {
//									return true;
//								} else {
//									continue;
//								}
//							} else {
//								continue;
//							}
							
							logger.info(productId+" 计划还款结束债权  借款人: "+borrow.getUserId()+"-"+forAccountId+" 投资人: "+userId+"-"+accountId+" 授权码: "+authCode+" 原始订单号: "+orgOrderId);

							// 垫付机构还款时,结束无法结束债权
							Integer borrowUserId = borrow.getUserId();
							// 根据用户ID查询借款人用户电子账户号
							Account borrowUserAccount = this.getAccount(borrowUserId);
							if(borrowUserAccount==null){
								logger.info("获取借款人电子账户号失败:借款人ID:["+borrowUserId+"].");
								return false;
							}
							// 借款人电子账户号
							forAccountId = borrowUserAccount.getAccountId();
							BankCreditEnd record = new BankCreditEnd();
							record.setUserId(borrow.getUserId());
//							record.setUsername(borrowRecover);
							record.setTenderUserId(userId);
//							record.setTenderUsername(tenderUsername);
							record.setAccountId(forAccountId);
							record.setTenderAccountId(accountId);
							record.setOrderId(logOrderId);
							record.setBorrowNid(productId);
							record.setAuthCode(authCode);
							record.setCreditEndType(3); // 结束债权类型（1:还款，2:散标债转，3:计划债转）'
							record.setStatus(0);
							record.setOrgOrderId(orgOrderId);
							
							int nowTime = GetDate.getNowTime10();
							record.setCreateUser(userId);
//							record.setCreateTime(nowTime);
							record.setUpdateUser(userId);
//							record.setUpdateTime(nowTime);
							
							this.bankCreditEndMapper.insertSelective(record);
							return true;
						} catch (Exception e) {
							logger.error("计划还款中发生系统", e);
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * 查询相应的债权的状态
	 * 
	 * @param userId
	 * @param accountId
	 * @param orderId
	 * @return
	 */
	private BankCallBean debtStatusQuery(int userId, String accountId, String orderId) {
		// 获取共同参数
		String channel = BankCallConstant.CHANNEL_PC;
		// 查询相应的债权状态
		for (int i = 0; i < 3; i++) {
			try {
				String logOrderId = GetOrderIdUtils.getOrderId2(userId);
				String orderDate = GetOrderIdUtils.getOrderDate();
				String txDate = GetOrderIdUtils.getTxDate();
				String txTime = GetOrderIdUtils.getTxTime();
				String seqNo = GetOrderIdUtils.getSeqNo(6);
				// 调用还款接口
				BankCallBean debtEndBean = new BankCallBean();
				debtEndBean.setVersion(BankCallConstant.VERSION_10);// 接口版本号
				debtEndBean.setTxCode(BankCallConstant.TXCODE_BID_APPLY_QUERY);// 消息类型(批量还款)
				debtEndBean.setTxDate(txDate);
				debtEndBean.setTxTime(txTime);
				debtEndBean.setSeqNo(seqNo);
				debtEndBean.setChannel(channel);
				debtEndBean.setAccountId(accountId);
				debtEndBean.setOrgOrderId(orderId);
				debtEndBean.setLogUserId(String.valueOf(userId));
				debtEndBean.setLogOrderId(logOrderId);
				debtEndBean.setLogOrderDate(orderDate);
				debtEndBean.setLogRemark("结束债权请求");
				debtEndBean.setLogClient(0);
				BankCallBean statusResult = BankCallUtils.callApiBg(debtEndBean);
				if (Validator.isNotNull(statusResult)) {
					String retCode = StringUtils.isNotBlank(statusResult.getRetCode()) ? statusResult.getRetCode() : "";
					if (BankCallConstant.RESPCODE_SUCCESS.equals(retCode)) {
						return statusResult;
					} else {
						continue;
					}
				} else {
					continue;
				}
			} catch (Exception e) {
				logger.error("计划还款中发生系统", e);
			}
		}
		return null;
	}

	@Override
	public boolean updateTenderRepayStatus(BorrowApicron apicron, Borrow borrow, BorrowRecover borrowRecover) throws Exception {

		logger.info("-----------还款开始---" + apicron.getBorrowNid() + "---------");
		/** 还款信息 */
		// 当前时间
		int nowTime = GetDate.getNowTime10();
		// 借款编号
		String borrowNid = apicron.getBorrowNid();
		// 还款期数
		Integer periodNow = apicron.getPeriodNow();
		String repayBatchNo = apicron.getBatchNo();

		/** 标的基本数据 */

		// 还款期数
		Integer borrowPeriod = Validator.isNull(borrow.getBorrowPeriod()) ? 1 : borrow.getBorrowPeriod();
		// 还款方式
		String borrowStyle = borrow.getBorrowStyle();

		/** 投资人数据 */
		// 投资订单号
		String tenderOrderId = borrowRecover.getNid();
		// 投资人用户ID
		Integer tenderUserId = borrowRecover.getUserId();
		// 还款时间
		int recoverTime = borrowRecover.getRecoverTime();
		// 还款订单号
		String repayOrderId = borrowRecover.getRepayOrdid();

		/** 基本变量 */
		// 剩余还款期数
		Integer periodNext = borrowPeriod - periodNow;
		// 分期还款计划表
		BorrowRecoverPlan borrowRecoverPlan = null;
		// 是否月标(true:月标, false:天标)
		boolean isMonth = CustomConstants.BORROW_STYLE_PRINCIPAL.equals(borrowStyle) || CustomConstants.BORROW_STYLE_MONTH.equals(borrowStyle)
				|| CustomConstants.BORROW_STYLE_ENDMONTH.equals(borrowStyle);
		// [principal: 等额本金, month:等额本息, month:等额本息, endmonth:先息后本]
		if (isMonth) {
			// 取得分期还款计划表
			borrowRecoverPlan = getBorrowRecoverPlan(borrowNid, periodNow, tenderUserId, tenderOrderId);
			if (Validator.isNull(borrowRecoverPlan)) {
				throw new Exception("分期还款计划表数据不存在。[借款编号：" + borrowNid + "]，" + "[投资订单号：" + tenderOrderId + "]，" + "[期数：" + periodNow + "]");
			}else {
				// 还款订单号
				repayOrderId = borrowRecoverPlan.getRepayOrderId();
				// 应还款时间
				recoverTime = borrowRecoverPlan.getRecoverTime();
			}
		}
		// [endday: 按天计息, end:按月计息]
		else {
			// 还款订单号
			repayOrderId = borrowRecover.getRepayOrdid();
			// 还款时间
			recoverTime = borrowRecover.getRecoverTime();
		}

		// 更新还款明细表
		// 分期并且不是最后一期
		if (borrowRecoverPlan != null && Validator.isNotNull(periodNext) && periodNext > 0) {
			borrowRecover.setRecoverStatus(0); // 未还款
			// 取得分期还款计划表下一期的还款
			BorrowRecoverPlan borrowRecoverPlanNext = getBorrowRecoverPlan(borrowNid, periodNow + 1, tenderUserId, tenderOrderId);
			borrowRecover.setRecoverTime(null==borrowRecoverPlanNext?null:borrowRecoverPlanNext.getRecoverTime()); // 计算下期时间
			borrowRecover.setRecoverType(TYPE_WAIT);
		} else {
			borrowRecover.setRecoverStatus(1); // 已还款
			borrowRecover.setRecoverYestime(nowTime); // 实际还款时间
			borrowRecover.setRecoverTime(recoverTime);
			borrowRecover.setRecoverType(TYPE_YES);
		}
		// 分期时
		if (borrowRecoverPlan != null) {
			borrowRecover.setRecoverPeriod(periodNext);
		}
		borrowRecover.setRepayBatchNo(repayBatchNo);
		boolean borrowRecoverFlag = this.borrowRecoverMapper.updateByPrimaryKeySelective(borrowRecover) > 0 ? true : false;
		if (!borrowRecoverFlag) {
			throw new Exception("还款明细(huiyingdai_borrow_recover)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
		}
		if (borrowRecover.getCreditAmount().compareTo(BigDecimal.ZERO) > 0) {
			// 查询相应的债权转让
			List<HjhDebtCredit> borrowCredits = this.getBorrowCredit(tenderOrderId, periodNow - 1);
			if (borrowCredits != null && borrowCredits.size() > 0) {
				for (int i = 0; i < borrowCredits.size(); i++) {
					// 获取相应的债转记录
					HjhDebtCredit borrowCredit = borrowCredits.get(i);
					// 债转编号
					String creditNid = borrowCredit.getCreditNid();
					// 债转状态
					if (borrowRecoverPlan != null && Validator.isNotNull(periodNext) && periodNext > 0) {
						borrowCredit.setRepayStatus(1);
					} else {
						borrowCredit.setRepayStatus(2);
						// 债转最后还款时间
						borrowCredit.setCreditRepayYesTime(isMonth ? 0 : nowTime);
					}
					// 债转还款期
					borrowCredit.setRepayPeriod(periodNow);
					// 债转最近还款时间
					borrowCredit.setCreditRepayLastTime(nowTime);
					// 更新债转总表
					boolean borrowCreditFlag = this.hjhDebtCreditMapper.updateByPrimaryKeySelective(borrowCredit) > 0 ? true : false;
					// 债转总表更新成功
					if (!borrowCreditFlag) {
						throw new Exception("债转记录(hyjf_hjh_debt_credit)更新失败！" + "[承接订单号：" + creditNid + "]");
					}
				}
			}
		}
		// 分期时
		if (Validator.isNotNull(borrowRecoverPlan)) {
			// 更新还款计划表
			borrowRecoverPlan.setRepayBatchNo(repayBatchNo);
			borrowRecoverPlan.setRecoverStatus(1);
			borrowRecoverPlan.setRecoverYestime(String.valueOf(nowTime));
			borrowRecoverPlan.setRecoverType(TYPE_YES);
			boolean borrowRecoverPlanFlag = this.borrowRecoverPlanMapper.updateByPrimaryKeySelective(borrowRecoverPlan) > 0 ? true : false;
			if (!borrowRecoverPlanFlag) {
				throw new Exception("还款分期计划表(huiyingdai_borrow_recover_plan)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
			}
			// 更新总的还款计划
			BorrowRepayPlan borrowRepayPlan = getBorrowRepayPlan(borrowNid, periodNow);
			if (Validator.isNotNull(borrowRepayPlan)) {
				borrowRepayPlan.setRepayType(TYPE_WAIT_YES);
//				borrowRepayPlan.setRepayDays("0");
//				borrowRepayPlan.setRepayStep(4);
				borrowRepayPlan.setRepayActionTime(String.valueOf(nowTime));
				borrowRepayPlan.setRepayStatus(1);
				borrowRepayPlan.setRepayYestime(nowTime);
				boolean borrowRepayPlanFlag = this.borrowRepayPlanMapper.updateByPrimaryKeySelective(borrowRepayPlan) > 0 ? true : false;
				if (!borrowRepayPlanFlag) {
					throw new Exception("还款分期计划表(huiyingdai_borrow_repay_plan)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
				}
			} else {
				throw new Exception("还款分期计划表(huiyingdai_borrow_repay_plan)查询失败！" + "[投资订单号：" + tenderOrderId + "]");
			}

		}
		logger.info("-----------还款结束---" + apicron.getBorrowNid() + "---------" + repayOrderId);
		return true;
	}

	private List<HjhDebtCredit> getBorrowCredit(String tenderOrderId, Integer periodNow) {
		
		HjhDebtCreditExample example = new HjhDebtCreditExample();
		example.createCriteria().andInvestOrderIdEqualTo(tenderOrderId).andRepayPeriodEqualTo(periodNow);
		List<HjhDebtCredit> borrowCredits = this.hjhDebtCreditMapper.selectByExample(example);
		return borrowCredits;
	}

	/**
	 * 原始投资还款
	 * @param apicron
	 * @param borrow
	 * @param borrowInfo
	 * @param borrowRecover
	 * @param repayDetail
	 * @param isCredit
	 * @param sumCreditCapital
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean updateTenderRepay(BorrowApicron apicron, Borrow borrow, BorrowInfo borrowInfo, BorrowRecover borrowRecover, JSONObject repayDetail, boolean isCredit, BigDecimal sumCreditCapital) throws Exception {

		logger.info("-----------还款开始---" + apicron.getBorrowNid() + "---------");

		/** 还款信息 */
		// 当前时间
		int nowTime = GetDate.getNowTime10();
		// 借款编号
		String borrowNid = apicron.getBorrowNid();
		// 还款人ID(借款人或代付机构)
		Integer repayUserId = apicron.getUserId();
		// 还款人用户名
		String repayUserName = apicron.getUserName();
		// 是否是担保机构还款
		int isApicronRepayOrgFlag = Validator.isNull(apicron.getIsRepayOrgFlag()) ? 0 : apicron.getIsRepayOrgFlag();
		// 还款期数
		Integer periodNow = apicron.getPeriodNow();
		String repayBatchNo = apicron.getBatchNo();
		int txDate = Validator.isNotNull(apicron.getTxDate()) ? apicron.getTxDate() : 0;// 批次时间yyyyMMdd
		int txTime = Validator.isNotNull(apicron.getTxTime()) ? apicron.getTxTime() : 0;// 批次时间hhmmss
		String seqNo = Validator.isNotNull(apicron.getSeqNo()) ? String.valueOf(apicron.getSeqNo()) : null;// 流水号
		String bankSeqNo = Validator.isNotNull(apicron.getBankSeqNo()) ? String.valueOf(apicron.getBankSeqNo()) : null;// 银行唯一订单号

		String orderId = repayDetail.getString(BankCallConstant.PARAM_ORDERID);// 还款订单号
		BigDecimal txAmount = repayDetail.getBigDecimal(BankCallConstant.PARAM_TXAMOUNT);// 操作金额
		String forAccountId = repayDetail.getString(BankCallConstant.PARAM_FORACCOUNTID);// 投资人银行账户
		/** 标的基本数据 */
		// 标的是否可以担保机构还款
		int isRepayOrgFlag = Validator.isNull(borrowInfo.getIsRepayOrgFlag()) ? 0 : borrowInfo.getIsRepayOrgFlag();
		// 还款期数
		Integer borrowPeriod = Validator.isNull(borrow.getBorrowPeriod()) ? 1 : borrow.getBorrowPeriod();
		// 还款方式
		String borrowStyle = borrow.getBorrowStyle();
		/** 投资人数据 */
		// 投资订单号
		String tenderOrderId = borrowRecover.getNid();
		// 投资人用户ID
		Integer tenderUserId = borrowRecover.getUserId();
		// 还款时间
		int recoverTime = borrowRecover.getRecoverTime();
		// 还款订单号
		String repayOrderId = borrowRecover.getRepayOrdid();
		//计划加入订单号
		String accedeOrderId = borrowRecover.getAccedeOrderId();
		/** 基本变量 */

		// 剩余还款期数
		Integer periodNext = borrowPeriod - periodNow;
		// 取得还款详情
		BorrowRepay borrowRepay = getBorrowRepayAsc(borrowNid, apicron);
		// 投资信息
		BorrowTender borrowTender = getBorrowTender(tenderOrderId);
		// 投资用户开户信息
		Account tenderBankAccount = this.getAccount(tenderUserId);
		// 投资用户银行账户
		String tenderAccountId = tenderBankAccount.getAccountId();
		// 应收管理费
		BigDecimal recoverFee = BigDecimal.ZERO;

		// 已还款管理费
		BigDecimal recoverFeeYes = BigDecimal.ZERO;

		// 待还款本息
		BigDecimal recoverAccountWait = BigDecimal.ZERO;
		// 待还款本金
		BigDecimal recoverCapitalWait = BigDecimal.ZERO;
		// 待还款利息
		BigDecimal recoverInterestWait = BigDecimal.ZERO;
		// 延期天数
		Integer lateDays = 0;
		// 逾期利息
		BigDecimal lateInterest = BigDecimal.ZERO;
		// 延期天数
		Integer delayDays = 0;
		// 延期利息
		BigDecimal delayInterest = BigDecimal.ZERO;
		// 提前天数
		Integer chargeDays = 0;
		// 提前还款少还利息
		BigDecimal chargeInterest = BigDecimal.ZERO;

		// 还款本息(实际)
		BigDecimal repayAccount = BigDecimal.ZERO;
		// 还款本金(实际)
		BigDecimal repayCapital = BigDecimal.ZERO;
		// 还款利息(实际)
		BigDecimal repayInterest = BigDecimal.ZERO;
		// 管理费
		BigDecimal manageFee = BigDecimal.ZERO;
		// 分期还款计划表
		BorrowRecoverPlan borrowRecoverPlan = null;
		// 是否月标(true:月标, false:天标)
		boolean isMonth = CustomConstants.BORROW_STYLE_PRINCIPAL.equals(borrowStyle) || CustomConstants.BORROW_STYLE_MONTH.equals(borrowStyle)
				|| CustomConstants.BORROW_STYLE_ENDMONTH.equals(borrowStyle);
		// [principal: 等额本金, month:等额本息, month:等额本息, endmonth:先息后本]
		if (isMonth) {
			// 取得分期还款计划表
			borrowRecoverPlan = getBorrowRecoverPlan(borrowNid, periodNow, tenderUserId, tenderOrderId);
			if (Validator.isNull(borrowRecoverPlan)) {
				throw new Exception("分期还款计划表数据不存在。[借款编号：" + borrowNid + "]，" + "[投资订单号：" + tenderOrderId + "]，" + "[期数：" + periodNow + "]");
			}else{
				//是否先息后本
//			boolean isStyle = CustomConstants.BORROW_STYLE_ENDMONTH.equals(borrowStyle);
				// 还款订单号
				repayOrderId = borrowRecoverPlan.getRepayOrderId();
				// 应还款时间
				recoverTime = borrowRecoverPlan.getRecoverTime();

				// 应收管理费
				recoverFee = borrowRecoverPlan.getRecoverFee();

				// 已还款管理费
				recoverFeeYes = borrowRecoverPlan.getRecoverFeeYes();

				// 待还款本息
				recoverAccountWait = borrowRecoverPlan.getRecoverAccountWait();

				// 待还款本金
				recoverCapitalWait = borrowRecoverPlan.getRecoverCapitalWait();
				// 应还款利息
				recoverInterestWait = borrowRecoverPlan.getRecoverInterestWait();
				// 逾期天数
				lateDays = borrowRecoverPlan.getLateDays();
				// 逾期利息
				lateInterest = borrowRecoverPlan.getLateInterest();
				// 延期天数
				delayDays = borrowRecoverPlan.getDelayDays();
				// 延期利息
				delayInterest = borrowRecoverPlan.getDelayInterest();
				// 提前天数
				chargeDays = borrowRecoverPlan.getChargeDays();

				// 提前还款少还利息
				chargeInterest = borrowRecoverPlan.getChargeInterest().subtract(borrowRecoverPlan.getRepayChargeInterest());

				// 实际还款本息
				repayAccount = recoverAccountWait.add(lateInterest).add(delayInterest).add(chargeInterest);
				// 实际还款本金
				repayCapital = recoverCapitalWait;
				// 实际还款利息
				repayInterest = recoverInterestWait.add(lateInterest).add(delayInterest).add(chargeInterest);
				// 还款管理费
				manageFee = recoverFee.subtract(recoverFeeYes);
			}

		}
		// [endday: 按天计息, end:按月计息]
		else {
			// 还款订单号
			repayOrderId = borrowRecover.getRepayOrdid();
			// 还款时间
			recoverTime = borrowRecover.getRecoverTime();
			// 管理费
			recoverFee = borrowRecover.getRecoverFee();
			// 已还款管理费
			recoverFeeYes = borrowRecover.getRecoverFeeYes();

			// 待还款本息
			recoverAccountWait = borrowRecover.getRecoverAccountWait();
			// 待还款本金
			recoverCapitalWait = borrowRecover.getRecoverCapitalWait();
			// 应还款利息
			recoverInterestWait = borrowRecover.getRecoverInterestWait();

			// 逾期天数
			lateDays = borrowRecover.getLateDays();
			// 逾期利息
			lateInterest = borrowRecover.getLateInterest();
			// 延期天数
			delayDays = borrowRecover.getDelayDays();
			// 延期利息
			delayInterest = borrowRecover.getDelayInterest();
			// 提前天数
			chargeDays = borrowRecover.getChargeDays();
			// 提前还款少还利息
			chargeInterest = borrowRecover.getChargeInterest().subtract(borrowRecover.getRepayChargeInterest());
			// 实际还款本息
			repayAccount = recoverAccountWait.add(lateInterest).add(delayInterest).add(chargeInterest);
			// 实际还款本金
			repayCapital = recoverCapitalWait;
			// 实际还款利息
			repayInterest = recoverInterestWait.add(lateInterest).add(delayInterest).add(chargeInterest);
			// 还款管理费
			manageFee = recoverFee.subtract(recoverFeeYes);
		}
		logger.info("逾期还款利息(borrowRecover.getLateInterest())：{}，还款订单号：{}", lateInterest, repayOrderId);
		// 判断该收支明细是否存在时,跳出本次循环
		if (countAccountListByNid(repayOrderId)) {
			return true;
		}
		//redis
		//判断是否清算期3天内还款
		logger.info("---------------------还款标的:" + borrowNid + ",订单号:" + accedeOrderId + ",开始处理订单资金-----------");
		//更新计划加入订单号的资金
		HjhAccedeExample accedeExample = new HjhAccedeExample();
		accedeExample.createCriteria().andAccedeOrderIdEqualTo(accedeOrderId);
		List<HjhAccede> accedeList = this.hjhAccedeMapper.selectByExample(accedeExample);
		HjhAccede hjhAccede = accedeList.get(0);
		
		BigDecimal frostAccount = hjhAccede.getFrostAccount();
		BigDecimal availableInvestAccount = hjhAccede.getAvailableInvestAccount();
		logger.info("---------------------还款标的:" + borrowNid + ",订单号:" + accedeOrderId + ",处理前冻结金额：" + frostAccount + "-----------");
		logger.info("---------------------还款标的:" + borrowNid + ",订单号:" + accedeOrderId + ",处理前订单可用金额：" + availableInvestAccount + "-----------");
		HjhRepayExample repayExample = new HjhRepayExample();
		repayExample.createCriteria().andAccedeOrderIdEqualTo(accedeOrderId);

		HjhAccede paramInfo = new HjhAccede();
		paramInfo.setId(hjhAccede.getId());
		String dateStr = "";
		if(hjhAccede.getEndDate() != null){
			dateStr = GetDate.dateToString(hjhAccede.getEndDate());
		}
		logger.info("---------------------还款标的:" + borrowNid + ",订单号:" + accedeOrderId + ",结束时间:" + dateStr + "-----------");
//		logger.info("---------------------还款标的:" + borrowNid + ",订单号:" + accedeOrderId + ",是否冻结:" + isForstTime(hjhAccede.getEndDate()) + "-----------");
		if (hjhAccede.getEndDate() != null && isForstTime(hjhAccede.getEndDate())) {
			logger.info("---------------------还款标的:" + borrowNid + ",订单号:" + accedeOrderId + ",开始增加冻结金额,原冻结金额:"+ frostAccount +",增加金额:" + repayAccount + "-----------");
			paramInfo.setFrostAccount(repayAccount);//计划冻结金额
			List<HjhRepay> repayList = this.hjhRepayMapper.selectByExample(repayExample);
			HjhRepay hjhRepay = repayList.get(0);
			HjhRepay repayParam = new HjhRepay();
			repayParam.setId(hjhRepay.getId());
			repayParam.setRepayTotal(repayAccount);
			repayParam.setPlanRepayCapital(repayCapital);
			repayParam.setPlanRepayInterest(repayInterest);
			repayParam.setRepayAlready(repayAccount);
			this.hjhPlanCustomizeMapper.updateHjhRepayForHjhRepay(repayParam);
			logger.info("---------------------还款标的:" + borrowNid + ",订单号:" + accedeOrderId + ",开始增加还款,原回款金额:"+ hjhRepay.getRepayAlready() +",增加金额:" + repayAccount + "-----------");
		}else{
			logger.info("---------------------还款标的:" + borrowNid + ",订单号:" + accedeOrderId + ",开始增加可用金额-----------");
			paramInfo.setAvailableInvestAccount(repayAccount);//计划订单可用金额
		}
		this.hjhPlanCustomizeMapper.updateHjhAccedeForHjhProcess(paramInfo);
		
		// 更新账户信息(投资人)
		Account tenderAccount = new Account();
		tenderAccount.setUserId(tenderUserId);
		logger.info("---------------------还款标的:" + borrowNid + ",订单号:" + accedeOrderId + ",账户金额结束时间:" + dateStr + "-----------");
//		logger.info("---------------------还款标的:" + borrowNid + ",订单号:" + accedeOrderId + ",账户金额是否冻结:" + isForstTime(hjhAccede.getEndDate()) + "-----------");
		if (hjhAccede.getEndDate() != null && isForstTime(hjhAccede.getEndDate())) {
			tenderAccount.setPlanFrost(repayAccount);//汇计划冻结金额
		}else{
			tenderAccount.setPlanBalance(repayAccount);//汇计划可用金额
		}
		tenderAccount.setBankBalanceCash(repayAccount);// 投资人银行可用余额
		boolean investAccountFlag = this.adminAccountCustomizeMapper.updateOfRepayPlanTender(tenderAccount) > 0 ? true : false;
		if (!investAccountFlag) {
			throw new Exception("投资人资金记录(huiyingdai_account)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
		}
		// 取得账户信息(投资人)
		tenderAccount = this.getAccountByUserId(borrowTender.getUserId());
		if (Validator.isNull(tenderAccount)) {
			throw new Exception("投资人账户信息不存在。[投资人ID：" + borrowTender.getUserId() + "]，" + "[投资订单号：" + tenderOrderId + "]");
		}
		// 写入收支明细
		AccountList accountList = new AccountList();
		accountList.setNid(repayOrderId); // 还款订单号
		accountList.setUserId(tenderUserId); // 投资人
		accountList.setAmount(repayAccount); // 投资总收入
		/** 银行相关 */
		accountList.setAccountId(tenderAccountId);
		accountList.setBankAwait(tenderAccount.getBankAwait());
		accountList.setBankAwaitCapital(tenderAccount.getBankAwaitCapital());
		accountList.setBankAwaitInterest(tenderAccount.getBankAwaitInterest());
		accountList.setBankBalance(tenderAccount.getBankBalance());
		accountList.setBankFrost(tenderAccount.getBankFrost());
		accountList.setBankInterestSum(tenderAccount.getBankInterestSum());
		accountList.setBankInvestSum(tenderAccount.getBankInvestSum());
		accountList.setBankTotal(tenderAccount.getBankTotal());
		accountList.setBankWaitCapital(tenderAccount.getBankWaitCapital());
		accountList.setBankWaitInterest(tenderAccount.getBankWaitInterest());
		accountList.setBankWaitRepay(tenderAccount.getBankWaitRepay());
		accountList.setPlanBalance(tenderAccount.getPlanBalance());
		accountList.setPlanFrost(tenderAccount.getPlanFrost());
		accountList.setIsShow(1);//不显示交易明细
		// 如果是机构垫付还款
		if (isRepayOrgFlag == 1 && isApicronRepayOrgFlag == 1) {
			if (forAccountId.equals(tenderAccountId) && repayOrderId.equals(orderId) && txAmount.compareTo(repayAccount) == 0) {
				accountList.setCheckStatus(1);
			} else {
				accountList.setCheckStatus(0);
			}
		} else {
			if (forAccountId.equals(tenderAccountId) && repayOrderId.equals(orderId) && txAmount.compareTo(repayCapital) == 0) {
				accountList.setCheckStatus(1);
			} else {
				accountList.setCheckStatus(0);
			}
		}
		accountList.setTradeStatus(1);// 交易状态 0:失败 1:成功
		accountList.setIsBank(1);
		accountList.setTxDate(txDate);
		accountList.setTxTime(txTime);
		accountList.setSeqNo(seqNo);
		accountList.setBankSeqNo(bankSeqNo);
		/** 非银行相关 */
		if (hjhAccede.getEndDate() != null && isForstTime(hjhAccede.getEndDate())) {
			accountList.setType(3); // 冻结
		}else{
			accountList.setType(1); // 1收入
		}
		if (hjhAccede.getEndDate() != null && isForstTime(hjhAccede.getEndDate())) {
			accountList.setTrade("hjh_repay_frost"); // 收到还款冻结
		}else{
			accountList.setTrade("hjh_repay_balance"); // 收到还款复投
		}
		accountList.setTradeCode("balance"); // 余额操作
		accountList.setTotal(tenderAccount.getTotal()); // 投资人资金总额
		accountList.setBalance(tenderAccount.getBalance()); // 投资人可用金额
		accountList.setPlanFrost(tenderAccount.getPlanFrost());// 汇添金冻结金额
		accountList.setPlanBalance(tenderAccount.getPlanBalance());// 汇添金可用金额
		accountList.setFrost(tenderAccount.getFrost()); // 投资人冻结金额
		accountList.setAwait(tenderAccount.getAwait()); // 投资人待收金额
//		accountList.setCreateTime(nowTime); // 创建时间
//		accountList.setBaseUpdate(nowTime); // 更新时间
		accountList.setOperator(CustomConstants.OPERATOR_AUTO_REPAY); // 操作者
		accountList.setRemark(borrowNid);
		accountList.setIp(borrow.getAddIp()); // 操作IP
//		accountList.setIsUpdate(0);
//		accountList.setBaseUpdate(0);
//		accountList.setInterest(BigDecimal.ZERO); // 利息
		accountList.setWeb(0); // PC
		boolean investAccountListFlag = this.accountListMapper.insertSelective(accountList) > 0 ? true : false;
		if (!investAccountListFlag) {
			throw new Exception("收支明细(huiyingdai_account_list)写入失败！" + "[投资订单号：" + tenderOrderId + "]");
		}
		// 更新还款明细表
		// 分期并且不是最后一期
		if (borrowRecoverPlan != null && Validator.isNotNull(periodNext) && periodNext > 0) {
			borrowRecover.setRecoverStatus(0); // 未还款
			// 取得分期还款计划表下一期的还款
			BorrowRecoverPlan borrowRecoverPlanNext = getBorrowRecoverPlan(borrowNid, periodNow + 1, tenderUserId, tenderOrderId);
			borrowRecover.setRecoverTime(null==borrowRecoverPlanNext?null:borrowRecoverPlanNext.getRecoverTime()); // 计算下期时间
			borrowRecover.setRecoverType(TYPE_WAIT);
		} else {
			borrowRecover.setRecoverStatus(1); // 已还款
			borrowRecover.setRecoverYestime(nowTime); // 实际还款时间
			borrowRecover.setRecoverTime(recoverTime);
			borrowRecover.setRecoverType(TYPE_YES);
		}
		// 分期时
		if (borrowRecoverPlan != null) {
			borrowRecover.setRecoverPeriod(periodNext);
		}
		borrowRecover.setRepayBatchNo(repayBatchNo);
		borrowRecover.setRecoverAccountYes(borrowRecover.getRecoverAccountYes().add(repayAccount));
		borrowRecover.setRecoverCapitalYes(borrowRecover.getRecoverCapitalYes().add(repayCapital));
		borrowRecover.setRecoverInterestYes(borrowRecover.getRecoverInterestYes().add(repayInterest));
		borrowRecover.setRecoverAccountWait(borrowRecover.getRecoverAccountWait().subtract(recoverAccountWait));
		borrowRecover.setRecoverCapitalWait(borrowRecover.getRecoverCapitalWait().subtract(recoverCapitalWait));
		borrowRecover.setRecoverInterestWait(borrowRecover.getRecoverInterestWait().subtract(recoverInterestWait));
		borrowRecover.setRepayChargeInterest(borrowRecover.getRepayChargeInterest().add(chargeInterest));
		borrowRecover.setRepayDelayInterest(borrowRecover.getRepayDelayInterest().add(delayInterest));
		borrowRecover.setRepayLateInterest(borrowRecover.getRepayLateInterest().add(lateInterest));
		if (lateInterest.compareTo(BigDecimal.ZERO) == 1) {
			logger.info("原逾期还款利息：{},增加：{}", borrowRecover.getRepayLateInterest(), lateInterest);
		}
		borrowRecover.setRecoverFeeYes(borrowRecover.getRecoverFeeYes().add(manageFee));
		borrowRecover.setWeb(2); // 写入网站收支
		logger.info("更新放款明细类：{}",JSON.toJSONString(borrowRecover));
		boolean borrowRecoverFlag = this.borrowRecoverMapper.updateByPrimaryKeySelective(borrowRecover) > 0 ? true : false;
		if (!borrowRecoverFlag) {
			throw new Exception("放款明细(huiyingdai_borrow_recover)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
		}
		if (borrowRecover.getCreditAmount().compareTo(BigDecimal.ZERO) > 0) {
			// 查询相应的债权转让
			List<HjhDebtCredit> borrowCredits = this.getBorrowCredit(tenderOrderId, periodNow - 1);
			if (borrowCredits != null && borrowCredits.size() > 0) {
				for (int i = 0; i < borrowCredits.size(); i++) {
					// 获取相应的债转记录
					HjhDebtCredit borrowCredit = borrowCredits.get(i);
					// 债转编号
					String creditNid = borrowCredit.getCreditNid();
					// 债转状态
					if (borrowRecoverPlan != null && Validator.isNotNull(periodNext) && periodNext > 0) {
						borrowCredit.setRepayStatus(0);
					} else {
						borrowCredit.setRepayStatus(1);
						// 债转最后还款时间
						borrowCredit.setCreditRepayYesTime(isMonth ? 0 : nowTime);
					}
					// 债转还款期
					borrowCredit.setRepayPeriod(periodNow);
					// 债转最近还款时间
					borrowCredit.setCreditRepayLastTime(nowTime);
					// 更新债转总表
					boolean borrowCreditFlag = this.hjhDebtCreditMapper.updateByPrimaryKeySelective(borrowCredit) > 0 ? true : false;
					// 债转总表更新成功
					if (!borrowCreditFlag) {
						throw new Exception("债转记录(hyjf_hjh_debt_credit)更新失败！" + "[承接订单号：" + creditNid + "]");
					}
				}
			}
		}
		// 更新总的还款明细
		borrowRepay.setRepayAccountAll(borrowRepay.getRepayAccountAll().add(repayAccount).add(manageFee));
		borrowRepay.setRepayAccountYes(borrowRepay.getRepayAccountYes().add(repayAccount));
		borrowRepay.setRepayCapitalYes(borrowRepay.getRepayCapitalYes().add(repayCapital));
		borrowRepay.setRepayInterestYes(borrowRepay.getRepayInterestYes().add(repayInterest));
		borrowRepay.setLateDays(lateDays);
		borrowRepay.setLateInterest(borrowRepay.getLateInterest().add(lateInterest));
		borrowRepay.setDelayDays(delayDays);
		borrowRepay.setDelayInterest(borrowRepay.getDelayInterest().add(delayInterest));
		borrowRepay.setChargeDays(chargeDays);
		borrowRepay.setChargeInterest(borrowRepay.getChargeInterest().add(chargeInterest));
		// 用户是否提前还款
		borrowRepay.setAdvanceStatus(borrowRecover.getAdvanceStatus());
		// 还款来源
		if (isRepayOrgFlag == 1 && isApicronRepayOrgFlag == 1) {
			// 还款来源（1、借款人还款，2、机构垫付，3、保证金垫付）
			borrowRepay.setRepayMoneySource(2);
		} else {
			borrowRepay.setRepayMoneySource(1);
		}
		// 实际还款人（借款人、垫付机构、保证金）的用户ID
		borrowRepay.setRepayUserId(repayUserId);
		// 实际还款人（借款人、垫付机构、保证金）的用户名
		borrowRepay.setRepayUsername(repayUserName);
		boolean borrowRepayFlag = this.borrowRepayMapper.updateByPrimaryKeySelective(borrowRepay) > 0 ? true : false;
		if (!borrowRepayFlag) {
			throw new Exception("总的还款明细表(huiyingdai_borrow_repay)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
		}
		// 更新借款表
//		borrow = this.getBorrowByNid(borrowNid);
		Borrow newBrrow = new Borrow();
		newBrrow.setId(borrow.getId());
		BigDecimal borrowManager = borrow.getBorrowManager() == null ? BigDecimal.ZERO : new BigDecimal(borrow.getBorrowManager());
		newBrrow.setBorrowManager(borrowManager.add(manageFee).toString());
		newBrrow.setRepayAccountYes(borrow.getRepayAccountYes().add(repayAccount)); // 总还款利息
		newBrrow.setRepayAccountCapitalYes(borrow.getRepayAccountCapitalYes().add(repayCapital)); // 总还款本金
		newBrrow.setRepayAccountInterestYes(borrow.getRepayAccountInterestYes().add(repayInterest)); // 总还款利息
		newBrrow.setRepayAccountWait(borrow.getRepayAccountWait().subtract(recoverAccountWait)); // 未还款总额
		newBrrow.setRepayAccountCapitalWait(borrow.getRepayAccountCapitalWait().subtract(recoverCapitalWait)); // 未还款本金
		newBrrow.setRepayAccountInterestWait(borrow.getRepayAccountInterestWait().subtract(recoverInterestWait)); // 未还款利息
		newBrrow.setRepayFeeNormal(borrow.getRepayFeeNormal().add(manageFee));
		boolean borrowFlag = this.borrowMapper.updateByPrimaryKeySelective(newBrrow) > 0 ? true : false;
		if (!borrowFlag) {
			throw new Exception("借款详情(huiyingdai_borrow)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
		}
		// 更新投资表
		borrowTender.setRecoverAccountYes(borrowTender.getRecoverAccountYes().add(repayAccount));
		borrowTender.setRecoverAccountCapitalYes(borrowTender.getRecoverAccountCapitalYes().add(repayCapital));
		borrowTender.setRecoverAccountInterestYes(borrowTender.getRecoverAccountInterestYes().add(repayInterest));
		borrowTender.setRecoverAccountWait(borrowTender.getRecoverAccountWait().subtract(recoverAccountWait));
		borrowTender.setRecoverAccountCapitalWait(borrowTender.getRecoverAccountCapitalWait().subtract(recoverCapitalWait));
		borrowTender.setRecoverAccountInterestWait(borrowTender.getRecoverAccountInterestWait().subtract(recoverInterestWait));
		boolean borrowTenderFlag = borrowTenderMapper.updateByPrimaryKeySelective(borrowTender) > 0 ? true : false;
		if (!borrowTenderFlag) {
			throw new Exception("投资表(huiyingdai_borrow_tender)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
		}
		HjhDebtDetail debtDetail = this.getDebtDetail(tenderOrderId, periodNow, tenderUserId);
		//更新债权明细表
		// 更新DebtDetail表
		debtDetail .setRepayActionTime(nowTime);
		// 已还款
		debtDetail.setRepayStatus(1);
		// 账户管理费
		debtDetail.setManageFee(manageFee);
		// 已还本金
		debtDetail.setRepayCapitalYes(repayCapital);
		// 账户服务费
//				debtDetail.setServiceFee(big);
		// 已还利息
		debtDetail.setRepayInterestYes(repayInterest);
		// 未收本金
		debtDetail.setRepayCapitalWait(BigDecimal.ZERO);
		// 未收利息
		debtDetail.setRepayInterestWait(BigDecimal.ZERO);
		// 提前还款状态
		debtDetail.setAdvanceStatus(borrowRecover.getAdvanceStatus());
		// 提前还款天数
		debtDetail.setAdvanceDays(borrowRepay.getChargeDays());
		// 提前还款利息
		debtDetail.setAdvanceInterest(chargeInterest);
		// 延期天数
		debtDetail.setDelayDays(borrowRecover.getDelayDays());
		// 延期利息
		debtDetail.setDelayInterest(delayInterest);
		// 逾期天数
		debtDetail.setLateDays(borrowRecover.getLateDays());
		// 逾期利息
		debtDetail.setLateInterest(lateInterest);
		// 还款订单号
		debtDetail.setRepayOrderId(repayOrderId);
		// 还款日期
		debtDetail.setRepayOrderDate(borrowRecover.getRepayOrddate());
		// 债权更新时间
//		debtDetail.setUpdateTime(nowTime);
		//结束债权
		debtDetail.setDelFlag(1);
		// 到期公允价值
		debtDetail.setExpireFairValue(BigDecimal.ZERO);
		
		int flag = this.hjhDebtDetailMapper.updateByPrimaryKey(debtDetail);
		if (flag > 0) {
			logger.info("---------------------更新hjh_debt_detail信息完成,投资订单号:"  + tenderOrderId);
		}
		// 分期时
		if (Validator.isNotNull(borrowRecoverPlan)) {
			// 更新还款计划表
			borrowRecoverPlan.setRepayBatchNo(repayBatchNo);
			borrowRecoverPlan.setRecoverStatus(1);
			borrowRecoverPlan.setRecoverYestime(String.valueOf(nowTime));
			borrowRecoverPlan.setRecoverAccountYes(borrowRecoverPlan.getRecoverAccountYes().add(repayAccount));
			borrowRecoverPlan.setRecoverCapitalYes(borrowRecoverPlan.getRecoverCapitalYes().add(repayCapital));
			borrowRecoverPlan.setRecoverInterestYes(borrowRecoverPlan.getRecoverInterestYes().add(repayInterest));
			borrowRecoverPlan.setRecoverAccountWait(borrowRecoverPlan.getRecoverAccountWait().subtract(recoverAccountWait));
			borrowRecoverPlan.setRecoverCapitalWait(borrowRecoverPlan.getRecoverCapitalWait().subtract(recoverCapitalWait));
			borrowRecoverPlan.setRecoverInterestWait(borrowRecoverPlan.getRecoverInterestWait().subtract(recoverInterestWait));
			borrowRecoverPlan.setRepayChargeInterest(borrowRecoverPlan.getRepayChargeInterest().add(chargeInterest));
			borrowRecoverPlan.setRepayDelayInterest(borrowRecoverPlan.getRepayDelayInterest().add(delayInterest));
			borrowRecoverPlan.setRepayLateInterest(borrowRecoverPlan.getRepayLateInterest().add(lateInterest));
			borrowRecoverPlan.setRecoverFeeYes(borrowRecoverPlan.getRecoverFeeYes().add(manageFee));
			borrowRecoverPlan.setRecoverType(TYPE_YES);
			boolean borrowRecoverPlanFlag = this.borrowRecoverPlanMapper.updateByPrimaryKeySelective(borrowRecoverPlan) > 0 ? true : false;
			if (!borrowRecoverPlanFlag) {
				throw new Exception("还款分期计划表(huiyingdai_borrow_recover_plan)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
			}
			// 更新总的还款计划
			BorrowRepayPlan borrowRepayPlan = getBorrowRepayPlan(borrowNid, periodNow);
			if (Validator.isNotNull(borrowRepayPlan)) {
				borrowRepayPlan.setRepayType(TYPE_WAIT_YES);
//				borrowRepayPlan.setRepayDays("0");
//				borrowRepayPlan.setRepayStep(4);
				borrowRepayPlan.setRepayActionTime(String.valueOf(nowTime));
				borrowRepayPlan.setRepayStatus(1);
				borrowRepayPlan.setRepayYestime(nowTime);
				borrowRepayPlan.setRepayAccountAll(borrowRepayPlan.getRepayAccountAll().add(repayAccount).add(manageFee));
				borrowRepayPlan.setRepayAccountYes(borrowRepayPlan.getRepayAccountYes().add(repayAccount));
				borrowRepayPlan.setRepayCapitalYes(borrowRepayPlan.getRepayCapitalYes().add(repayCapital));
				borrowRepayPlan.setRepayInterestYes(borrowRepayPlan.getRepayInterestYes().add(repayInterest));
				borrowRepayPlan.setLateDays(lateDays);
				borrowRepayPlan.setLateInterest(borrowRepayPlan.getLateInterest().add(lateInterest));
				borrowRepayPlan.setDelayDays(delayDays);
				borrowRepayPlan.setDelayInterest(borrowRepayPlan.getDelayInterest().add(delayInterest));
				borrowRepayPlan.setChargeDays(chargeDays);
				borrowRepayPlan.setChargeInterest(borrowRepayPlan.getChargeInterest().add(chargeInterest));
				// 用户是否提前还款
				borrowRepayPlan.setAdvanceStatus(borrowRecoverPlan.getAdvanceStatus());
				// 还款来源
				if (isRepayOrgFlag == 1 && isApicronRepayOrgFlag == 1) {
					// 还款来源（1、借款人还款，2、机构垫付，3、保证金垫付）
					borrowRepayPlan.setRepayMoneySource(2);
				} else {
					borrowRepayPlan.setRepayMoneySource(1);
				}
				// 实际还款人（借款人、垫付机构、保证金）的用户ID
				borrowRepayPlan.setRepayUserId(repayUserId);
				// 实际还款人（借款人、垫付机构、保证金）的用户名
				borrowRepayPlan.setRepayUsername(repayUserName);
				boolean borrowRepayPlanFlag = this.borrowRepayPlanMapper.updateByPrimaryKeySelective(borrowRepayPlan) > 0 ? true : false;
				if (!borrowRepayPlanFlag) {
					throw new Exception("还款分期计划表(huiyingdai_borrow_repay_plan)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
				}
				
			} else {
				throw new Exception("还款分期计划表(huiyingdai_borrow_repay_plan)查询失败！" + "[投资订单号：" + tenderOrderId + "]");
			}

		}
		
		
		// 管理费大于0时,插入网站收支明细
		if (manageFee.compareTo(BigDecimal.ZERO) > 0) {

			// 插入网站收支明细记录
			AccountWebListVO accountWebList = new AccountWebListVO();
			accountWebList.setOrdid(borrowTender.getNid() + "_" + periodNow);// 订单号
			accountWebList.setBorrowNid(borrowNid); // 投资编号
			accountWebList.setUserId(repayUserId); // 借款人
			accountWebList.setAmount(Double.valueOf(manageFee.toString())); // 管理费
			accountWebList.setType(CustomConstants.TYPE_IN); // 类型1收入,2支出
			accountWebList.setTrade(CustomConstants.TRADE_REPAYFEE); // 管理费
			accountWebList.setTradeType(CustomConstants.TRADE_REPAYFEE_NM); // 账户管理费
			accountWebList.setRemark(borrowNid); // 投资编号
			accountWebList.setCreateTime(nowTime);
			accountWebList.setFlag(1);
			//网站首支明细队列
			try {
				logger.info("发送收支明细---" + repayUserId + "---------" + manageFee);
				commonProducer.messageSend(new MessageContent(MQConstant.ACCOUNT_WEB_LIST_TOPIC, UUID.randomUUID().toString(), accountWebList));
            } catch (MQException e) {
                logger.error("计划还款中发生系统", e);
            }
			
		}
		apicron.setSucAmount(apicron.getSucAmount().add(repayAccount.add(manageFee)));
		apicron.setSucCounts(apicron.getSucCounts() + 1);
		apicron.setFailAmount(apicron.getFailAmount().subtract(repayAccount.add(manageFee)));
		apicron.setFailCounts(apicron.getFailCounts() - 1);
		boolean apicronSuccessFlag = this.borrowApicronMapper.updateByPrimaryKeySelective(apicron) > 0 ? true : false;
		if (!apicronSuccessFlag) {
			throw new Exception("批次放款记录(borrowApicron)更新失败!" + "[项目编号：" + borrowNid + "]");
		}
		logger.info("还款结束---" + apicron.getBorrowNid() + "---------" + repayOrderId);
		logger.info("还款标的:" + borrowNid + ",订单号:" + accedeOrderId + ",判断复投结束时间:" + dateStr + "-----------");
//		logger.info("---------------------还款标的:" + borrowNid + ",订单号:" + accedeOrderId + ",是否复投.是否冻结:" + isForstTime(hjhAccede.getEndDate()) + "-----------");
		return true;
	}
	
	
	

	/**
	 * 判断还款是否需要放到订单冻结金额中
	 * @param endDate
	 * @return
	 */
	private boolean isForstTime(Date endDate){
		Date nowDate = new Date();
		Date countDate = GetDate.countDate(endDate,5, -3);
		long time = nowDate.getTime();
		long countTime = countDate.getTime();//计划结束日前3天
		if (time >= countTime) {
			return true;
		}
		return false;
	}
	
	

	private void updatePlanData(HjhAccede hjhAccede, HjhRepay hjhRepay) {
		BigDecimal waitTotal = hjhAccede.getShouldPayTotal();//计划加入待收总额
		BigDecimal waitCaptical = hjhAccede.getWaitCaptical();//计划加入待收本金
		BigDecimal waitInterest = hjhAccede.getWaitInterest();//计划加入待收利息
		//计划实收金额
		BigDecimal repayTotal = hjhRepay.getRepayTotal();
		BigDecimal repayCapital = hjhRepay.getPlanRepayCapital();
		BigDecimal repayInterest = hjhRepay.getPlanRepayInterest();
		String planNid = hjhAccede.getPlanNid();
		HjhPlanExample example = new HjhPlanExample();
		example.createCriteria().andPlanNidEqualTo(planNid);
		List<HjhPlan> planList = this.hjhPlanMapper.selectByExample(example);
		if (planList != null && planList.size() > 0) {
			HjhPlan hjhPlan = planList.get(0);
			hjhPlan.setRepayWaitAll(hjhPlan.getRepayWaitAll().subtract(waitTotal));
			hjhPlan.setPlanWaitCaptical(hjhPlan.getPlanWaitCaptical().subtract(waitCaptical));
			hjhPlan.setPlanWaitInterest(hjhPlan.getPlanWaitInterest().subtract(waitInterest));
			hjhPlan.setRepayTotal(hjhPlan.getRepayTotal().add(repayTotal));
			hjhPlan.setPlanRepayCapital(hjhPlan.getPlanRepayCapital().add(repayCapital));
			hjhPlan.setPlanRepayInterest(hjhPlan.getPlanRepayInterest().add(repayInterest));
			int count = this.hjhPlanMapper.updateByPrimaryKey(hjhPlan);
			if (count > 0) {
				logger.info("===============计划加入订单:" + hjhAccede.getAccedeOrderId() + "对应的计划:" + planNid + "相关待还应收金额维护成功!待还减少:" + waitTotal + ",应收增加:" + repayTotal);
			}
		}
	}

	private void couponRepay(HjhAccede hjhAccede) {
		// add by cwyang 优惠券还款请求加入到消息队列 start
        Map<String, String> params = new HashMap<String, String>();
        params.put("mqMsgId", GetCode.getRandomCode(10));
        // 借款项目编号
        params.put("orderId", hjhAccede.getAccedeOrderId());

		try {
			logger.info("发送优惠券还款队列---" + hjhAccede.getAccedeOrderId());
			commonProducer.messageSend(new MessageContent(MQConstant.HJH_COUPON_REPAY_TOPIC, UUID.randomUUID().toString(), params));
        } catch (MQException e) {
            logger.error("计划还款中发生系统", e);
        }
		//核对请求参数
//        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGES_NAME, RabbitMQConstants.ROUTINGKEY_COUPONREPAY_HJH, JSONObject.toJSONString(params));
        // add by cwyang 优惠券还款请求加入到消息队列 end
	}

	/**
	 *  计划退出推送消息
	 * 
	 * @param hjhRepay
	 * @author Administrator
	 */
	private void sendMessage(HjhRepay hjhRepay) {
		int userId = hjhRepay.getUserId();
		BigDecimal amount = hjhRepay.getRepayTotal();
		String planNid = hjhRepay.getPlanNid();
		HjhPlanExample example = new HjhPlanExample();
		example.createCriteria().andPlanNidEqualTo(planNid);
		List<HjhPlan> planList = this.hjhPlanMapper.selectByExample(example);
		HjhPlan hjhPlan = planList.get(0);
		String planName = hjhPlan.getPlanName();
		Map<String, String> msg = new HashMap<String, String>();
		msg.put(VAL_AMOUNT, amount.toString());// 待收金额
		msg.put(VAL_USERID, String.valueOf(userId));
		msg.put(VAL_HJH_TITLE, planName);
		
		if (Validator.isNotNull(msg.get(VAL_USERID)) && Validator.isNotNull(msg.get(VAL_AMOUNT)) && new BigDecimal(msg.get(VAL_AMOUNT)).compareTo(BigDecimal.ZERO) > 0) {
			// 推送消息队列
			AppMsMessage smsMessage = new AppMsMessage(Integer.valueOf(msg.get(VAL_USERID)), msg, null, MessageConstant.APP_MS_SEND_FOR_USER, CustomConstants.JYTZ_PLAN_REPAY_SUCCESS);
			try {
				commonProducer.messageSend(new MessageContent(MQConstant.APP_MESSAGE_TOPIC, String.valueOf(userId),
						smsMessage));
			} catch (MQException e) {
				logger.error("发送app消息失败..", e);
			}
			
		}
		
	}
	
	/**
	 *  发送短信(计划还款成功)
	 *
	 * @param hjhRepay
	 */
	private void sendSms(HjhRepay hjhRepay) {
		int userId = hjhRepay.getUserId();
		String planNid = hjhRepay.getPlanNid();
		HjhPlanExample example = new HjhPlanExample();
		example.createCriteria().andPlanNidEqualTo(planNid);
		List<HjhPlan> planList = this.hjhPlanMapper.selectByExample(example);
		HjhPlan hjhPlan = planList.get(0);
		String planName = hjhPlan.getPlanName();
		String userName = hjhRepay.getUserName();
		BigDecimal repayTotal = hjhRepay.getRepayTotal();
		Map<String, String> msg = new HashMap<String, String>();
		msg.put(VAL_NAME, userName);
		msg.put(VAL_AMOUNT, repayTotal.toString());
		msg.put(VAL_USERID, String.valueOf(userId));
		msg.put(VAL_HJH_TITLE, planName);
		
		logger.info("userid=" + msg.get(VAL_USERID) + ";开始发送短信,待收金额" + msg.get(VAL_AMOUNT));
		
		SmsMessage smsMessage = new SmsMessage(Integer.valueOf(msg.get(VAL_USERID)), msg, null, null, MessageConstant.SMS_SEND_FOR_USER, null, CustomConstants.PARAM_TPL_REPAY_HJH_SUCCESS,
				CustomConstants.CHANNEL_TYPE_NORMAL);
		try {
			commonProducer.messageSend(new MessageContent(MQConstant.SMS_CODE_TOPIC, String.valueOf(userId), smsMessage));
		} catch (MQException e2) {
			logger.error("发送邮件失败..", e2);
		}
	}

	/**
	 * 退出计划更新还款信息表
	 * @param hjhAccede
	 */
	private HjhRepay updateHjhLastRepayInfo(HjhAccede hjhAccede) {
		String accedeOrderId = hjhAccede.getAccedeOrderId();
		HjhRepayExample example = new HjhRepayExample();
		example.createCriteria().andAccedeOrderIdEqualTo(accedeOrderId);
		List<HjhRepay> repayList = this.hjhRepayMapper.selectByExample(example);
		if (repayList != null && repayList.size() > 0) {
			HjhRepay hjhRepay = repayList.get(0);
			hjhRepay.setRepayStatus(2);//已回款
			hjhRepay.setOrderStatus(7);//已退出
			hjhRepay.setRepayActualTime(GetDate.getNowTime10());
//			hjhRepay.setUpdateTime(GetDate.getNowTime10());
			hjhRepay.setWaitTotal(BigDecimal.ZERO);
			int count = this.hjhRepayMapper.updateByPrimaryKey(hjhRepay);
			if (count > 0) {
				logger.info("=============cwyang 汇计划退出还款信息更新成功!,加入订单号:" + accedeOrderId + ",还款信息总还款金额:" + hjhRepay.getRepayTotal());
			}
			return hjhRepay;
		}
		return null;
	}

	private int getTxDate(){
		Date date = new Date();        
		SimpleDateFormat format = new SimpleDateFormat();
		//调整相应格式 yyyy-年 MM-月  dd-日 HH-时 mm-分 ss-秒
		format.applyPattern("yyyyMMdd");
		String day = format.format(date);
		return  Integer.parseInt(day);
	}
	
	private int getTxTime(){
		Date date = new Date();        
		SimpleDateFormat format = new SimpleDateFormat();
		//调整相应格式 yyyy-年 MM-月  dd-日 HH-时 mm-分 ss-秒
		format.applyPattern("HHmmss");
		String day = format.format(date);
		return  Integer.parseInt(day);
	}
	/**
	 * 变更计划投资账户金额
	 * @param hjhAccede
	 * @param hjhRepay
	 */
	private void updatePlanTenderAccount(HjhAccede hjhAccede, HjhRepay hjhRepay) {
		BigDecimal repayTotal = hjhRepay.getRepayTotal();
//		BigDecimal repayCapital = hjhRepay.getPlanRepayCapital();
//		BigDecimal repayInterest = hjhRepay.getPlanRepayInterest();
		BigDecimal waitTotal = hjhAccede.getWaitTotal();//计划加入待收总额
		BigDecimal waitCaptical = hjhAccede.getWaitCaptical();//计划加入待收本金
		BigDecimal waitInterest = hjhAccede.getWaitInterest();//计划加入待收利息
		BigDecimal accountForst = hjhAccede.getFrostAccount();//账户实际收到冻结金额
		BigDecimal availableInvestAccount = hjhAccede.getAvailableInvestAccount();//剩余订单可用金额
		accountForst = accountForst.add(availableInvestAccount);//剩余订单可用金额也放到账户金额中
		BigDecimal accountInterest = accountForst.subtract(hjhAccede.getAccedeAccount());//计算账户订单利息
		//实际年化收益率
		BigDecimal reallyApr = getAccedeReallyApr(accountForst,hjhAccede.getAccedeAccount(),hjhAccede.getPlanNid(),hjhAccede.getLockPeriod(),hjhAccede.getAccedeOrderId());
		BigDecimal lqdServiceFee = hjhAccede.getLqdServiceFee();//清算服务费
		//获得投资服务费率
		BigDecimal tenderFeeRate = getTenderFeeRate(lqdServiceFee,hjhAccede.getAccedeAccount());
		//计划加入明细金额变更
		hjhAccede.setActualApr(reallyApr);
		hjhAccede.setWaitTotal(new BigDecimal(0));
		hjhAccede.setWaitCaptical(new BigDecimal(0));
		hjhAccede.setWaitInterest(new BigDecimal(0));
		hjhAccede.setOrderStatus(7);//已退出
		hjhAccede.setReceivedTotal(accountForst);//已收总额
		hjhAccede.setReceivedInterest(accountInterest);//已收利息
		hjhAccede.setReceivedCapital(hjhAccede.getAccedeAccount());//已收本金
		hjhAccede.setAcctualPaymentTime(GetDate.getNowTime10());//实际回款时间
		hjhAccede.setFrostAccount(BigDecimal.ZERO);//计划订单冻结金额更新
		hjhAccede.setAvailableInvestAccount(BigDecimal.ZERO);//计划订单可用金额
		hjhAccede.setFairValue(accountForst);//订单退出时重新计算公允价值
		hjhAccede.setInvestServiceApr(tenderFeeRate);//更新投资服务费率
		hjhAccede.setLqdProgress(BigDecimal.ONE);//已退出时更新清算进度为 100%
		int count = this.hjhAccedeMapper.updateByPrimaryKey(hjhAccede);
		if (count > 0) {
			logger.info("================cwyang 计划退出更新计划加入明细成功!计划加入订单号: " + hjhAccede.getAccedeOrderId());
		}
		
		HjhRepay newRepay = hjhRepayMapper.selectByPrimaryKey(hjhRepay.getId());
		newRepay.setRepayTotal(accountForst);
		newRepay.setRepayAlready(accountForst);
		newRepay.setActualRevenue(accountInterest);//实际收益
		newRepay.setActualPayTotal(accountForst);//实际回款总额
		newRepay.setPlanRepayCapital(hjhAccede.getAccedeAccount());
		newRepay.setPlanRepayInterest(accountInterest);
		this.hjhRepayMapper.updateByPrimaryKey(newRepay);
		//账户金额变更
		Integer userId = hjhAccede.getUserId();
		Account bankOpenAccount = this.getAccount(userId);
		AccountExample example = new AccountExample();
		example.createCriteria().andUserIdEqualTo(userId);
		List<Account> accountLists = this.accountMapper.selectByExample(example);
		if (accountLists !=null && accountLists.size() > 0) {
			Account account = accountLists.get(0);
			BigDecimal total = accountForst.subtract(waitTotal);//冻结金额 - 已还总额
			repayTotal = accountForst;
			//明细数据取值
			BigDecimal detailTotal = account.getBankTotal().add(total);
			BigDecimal detailBalance = account.getBankBalance().add(repayTotal);
			BigDecimal detailPlanBalance = account.getPlanBalance().subtract(availableInvestAccount);
			BigDecimal detailPlanFrost = account.getPlanFrost().subtract(repayTotal.subtract(availableInvestAccount));
			account.setBankTotal(total);
			account.setBankBalance(repayTotal);
			account.setPlanAccountWait(waitTotal);
			account.setPlanCapitalWait(waitCaptical);
			account.setPlanInterestWait(waitInterest);
			account.setBankInterestSum(repayTotal.subtract(waitCaptical));
			account.setPlanFrost(repayTotal.subtract(availableInvestAccount));
			account.setPlanBalance(availableInvestAccount);
			boolean investaccountFlag = this.adminAccountCustomizeMapper.updateOfPlanRepayAccount(account) > 0 ? true : false;
			if (investaccountFlag) {
				logger.info("====================cwyang 计划退出更新账户资金成功,计划加入订单号: " + hjhAccede.getAccedeOrderId());
			}
			AccountList accountList = new AccountList();
			/** 投资人银行相关 */
			accountList.setBankAwait(account.getBankAwait());
			accountList.setBankAwaitCapital(account.getBankAwaitCapital());
			accountList.setBankAwaitInterest(account.getBankAwaitInterest());
			accountList.setBankBalance(detailBalance);
			accountList.setBankFrost(account.getBankFrost());
			accountList.setBankInterestSum(account.getBankInterestSum());
			accountList.setBankInvestSum(account.getBankInvestSum());
			accountList.setBankTotal(detailTotal);
			accountList.setBankWaitCapital(account.getBankWaitCapital());
			accountList.setBankWaitInterest(account.getBankWaitInterest());
			accountList.setBankWaitRepay(account.getBankWaitRepay());
			accountList.setAccountId(bankOpenAccount.getAccountId());
			accountList.setCheckStatus(1);
			accountList.setTradeStatus(1);
			accountList.setIsBank(1);
			accountList.setTxDate(getTxDate());
			accountList.setTxTime(getTxTime());
			accountList.setAccedeOrderId(hjhAccede.getAccedeOrderId());
			/** 投资人非银行相关 */
			accountList.setUserId(userId); // 投资人
			accountList.setAmount(repayTotal); // 投资本金
			accountList.setType(1); // 1收入
			accountList.setTrade("hjh_quit"); // 退出计划
			accountList.setTradeCode("balance"); // 余额操作
			accountList.setTotal(account.getTotal()); // 投资人资金总额
			accountList.setBalance(BigDecimal.ZERO); // 投资人银行可用金额
			accountList.setFrost(account.getFrost()); // 投资人冻结金额
			accountList.setAwait(account.getAwait()); // 投资人待收金额
			accountList.setOperator(CustomConstants.OPERATOR_AUTO_LOANS); // 操作者
			accountList.setRemark("计划结束");
			accountList.setPlanBalance(detailPlanBalance);
			accountList.setPlanFrost(detailPlanFrost);
			accountList.setWeb(0); // PC
			int insertCount = this.accountListMapper.insertSelective(accountList);
			if (insertCount > 0) {
				logger.info("===================cwyang 插入汇计划加入明细成功!加入订单号:" + hjhAccede.getAccedeOrderId());
			}
			
		}

		// 退出计划累加统计数据
		logger.info("退出计划累加统计数据...");
		JSONObject params1 = new JSONObject();
		params1.put("interestSum", accountInterest);
		try {
			commonProducer
					.messageSend(new MessageContent(MQConstant.STATISTICS_CALCULATE_INVEST_INTEREST_TOPIC,
							MQConstant.STATISTICS_CALCULATE_INTEREST_SUM_TAG, UUID.randomUUID().toString(),
							params1));
		} catch (MQException e) {
			logger.error("退出计划累加统计数", e);
		}

		// 更新运营数据计划收益
		logger.info("退出计划更新运营数据计划收益...");
		JSONObject params = new JSONObject();
		params.put("type", 2);// 计划收益
		params.put("recoverInterestAmount", accountInterest);
        try {
			commonProducer.messageSend(new MessageContent(MQConstant.STATISTICS_CALCULATE_INVEST_INTEREST_TOPIC, UUID.randomUUID().toString(), params));
        }catch (MQException e){
            logger.error("发送运营数据更新MQ失败,计划还款订单:" + hjhAccede.getAccedeOrderId());
        }
	}

	/**
	 * 获得投资服务费率
	 *
	 * @param lqdServiceFee
	 * 清算服务费
	 * @param accedeAccount
	 * 订单加入金额
	 * @return
	 */
	private BigDecimal getTenderFeeRate(BigDecimal lqdServiceFee, BigDecimal accedeAccount) {
		if(lqdServiceFee.compareTo(BigDecimal.ZERO) > 0){
			BigDecimal lqdRate = lqdServiceFee.divide(accedeAccount, 4, BigDecimal.ROUND_DOWN);
			return lqdRate;
		}
		return BigDecimal.ZERO;
	}

	/**
	 * 获得订单的实际年化收益率
	 * @param accountForst
	 * @param accedeAccount
	 * @param planNid
	 * @param lockPeriod
	 * @param accedeOrderId
	 * @return
	 */
	private BigDecimal getAccedeReallyApr(BigDecimal accountForst, BigDecimal accedeAccount, String planNid, Integer lockPeriod, String accedeOrderId) {
		HjhPlanExample example = new HjhPlanExample();
		example.createCriteria().andPlanNidEqualTo(planNid);
		List<HjhPlan> hjhPlans = this.hjhPlanMapper.selectByExample(example);
		BigDecimal reallyApr = new BigDecimal(0);
		if(hjhPlans != null && hjhPlans.size() == 1){
			HjhPlan hjhPlan = hjhPlans.get(0);
			Integer isMonth = hjhPlan.getIsMonth();
			if(1 == isMonth){//按月计息
				reallyApr = (accountForst.subtract(accedeAccount)).divide(accedeAccount,8,BigDecimal.ROUND_DOWN)
						.divide(new BigDecimal(lockPeriod),8,BigDecimal.ROUND_DOWN)
						.multiply(new BigDecimal(12)).multiply(new BigDecimal(100));
			}else if(0 == isMonth){//按日计息
				reallyApr = (accountForst.subtract(accedeAccount)).divide(accedeAccount,8,BigDecimal.ROUND_DOWN)
						.divide(new BigDecimal(lockPeriod),8,BigDecimal.ROUND_DOWN)
						.multiply(new BigDecimal(360)).multiply(new BigDecimal(100));
			}
		}
		reallyApr = reallyApr.setScale(4,BigDecimal.ROUND_DOWN);
		logger.info("--------------------开始计算订单实际年化收益率，订单号:" + accedeOrderId + ",accountForst：" + accountForst
				+ ",accedeAccount:" + accedeAccount + ",lockperiod:" + lockPeriod + ",实际年化收益率：" + reallyApr.toString());
		return reallyApr;
	}

	/**
	 *  债转承接还款
	 * @param apicron
	 * @param borrow
	 * @param borrowInfo
	 * @param borrowRecover
	 * @param creditRepay
	 * @param assignRepayDetail
	 * @return
	 * @throws Exception
	 */
	@Override
	public boolean updateCreditRepay(BorrowApicron apicron, Borrow borrow, BorrowInfo borrowInfo, BorrowRecover borrowRecover, HjhDebtCreditRepay creditRepay, JSONObject assignRepayDetail) throws Exception {

		logger.info("------债转还款承接部分开始---承接订单号：" + creditRepay.getCreditNid() + "---------");

		/** 还款信息 */
		// 当前时间
		int nowTime = GetDate.getNowTime10();
		// 借款编号
		String borrowNid = apicron.getBorrowNid();
		// 还款人(借款人或垫付机构)ID
		Integer repayUserId = apicron.getUserId();
		// 还款人用户名
		String repayUserName = apicron.getUserName();
		// 当前期数
		Integer periodNow = apicron.getPeriodNow();
		// 是否是担保机构还款
		int isApicronRepayOrgFlag = Validator.isNull(apicron.getIsRepayOrgFlag()) ? 0 : apicron.getIsRepayOrgFlag();
		String repayBatchNo = apicron.getBatchNo();
		int txDate = Validator.isNotNull(apicron.getTxDate()) ? apicron.getTxDate() : 0;// 批次时间yyyyMMdd
		int txTime = Validator.isNotNull(apicron.getTxTime()) ? apicron.getTxTime() : 0;// 批次时间hhmmss
		String seqNo = Validator.isNotNull(apicron.getSeqNo()) ? String.valueOf(apicron.getSeqNo()) : null;// 流水号
		String bankSeqNo = Validator.isNotNull(apicron.getBankSeqNo()) ? String.valueOf(apicron.getBankSeqNo()) : null;// 银行唯一订单号

		String orderId = assignRepayDetail.getString(BankCallConstant.PARAM_ORDERID);// 还款订单号
		BigDecimal txAmount = assignRepayDetail.getBigDecimal(BankCallConstant.PARAM_TXAMOUNT);// 操作金额
		String forAccountId = assignRepayDetail.getString(BankCallConstant.PARAM_FORACCOUNTID);// 投资人银行账户
		/** 标的基本数据 */
		// 标的是否可用担保机构还款
		int isRepayOrgFlag = Validator.isNull(borrowInfo.getIsRepayOrgFlag()) ? 0 : borrowInfo.getIsRepayOrgFlag();
		// 项目总期数
		Integer borrowPeriod = Validator.isNull(borrow.getBorrowPeriod()) ? 1 : borrow.getBorrowPeriod();
		// 还款方式
		String borrowStyle = borrow.getBorrowStyle();

		/** 还款信息 */
		// 投资订单号
		String tenderOrderId = borrowRecover.getNid();
		// 投资人用户ID
		Integer tenderUserId = borrowRecover.getUserId();

		/** 债权还款信息 */
		// 债权编号
		String creditNid = Validator.isNull(creditRepay.getCreditNid()) ? null : creditRepay.getCreditNid();
		// 承接订单号
		String assignNid = creditRepay.getAssignOrderId();
		// 承接用户userId
		int assignUserId = creditRepay.getUserId();
		// 还款订单号
		String repayOrderId = creditRepay.getCreditRepayOrderId();
		// 还款本息(实际)
		BigDecimal assignAccount = creditRepay.getRepayAccount();
		// 还款本金(实际)
		BigDecimal assignCapital = creditRepay.getRepayCapital();
		// 还款利息(实际)
		BigDecimal assignInterest = creditRepay.getRepayInterest();
		// 管理费
		BigDecimal assignManageFee = creditRepay.getManageFee();
		// 提前还款少还利息
		BigDecimal chargeInterest = creditRepay.getRepayAdvanceInterest();
		// 延期利息
		BigDecimal delayInterest = creditRepay.getRepayDelayInterest();
		// 逾期利息
		BigDecimal lateInterest = creditRepay.getRepayLateInterest();
		logger.info("逾期还款利息(creditRepay.getRepayLateInterest())：{}，还款订单号：{}", lateInterest, repayOrderId);
		// 还款本息(实际)
		BigDecimal repayAccount = assignAccount.add(lateInterest).add(delayInterest).add(chargeInterest);
		// 还款本金(实际)
		BigDecimal repayCapital = assignCapital;
		// 还款利息(实际)
		BigDecimal repayInterest = assignInterest.add(lateInterest).add(delayInterest).add(chargeInterest);
		// 管理费
		BigDecimal manageFee = assignManageFee;

		/** 基本变量 */
		// 剩余还款期数
		Integer periodNext = borrowPeriod - periodNow;
		// 取得还款详情
		BorrowRepay borrowRepay = getBorrowRepayAsc(borrowNid, apicron);
		// 投资信息
		BorrowTender borrowTender = getBorrowTender(tenderOrderId);
		// 查询相应的债权转让
		HjhDebtCredit borrowCredit = this.getBorrowCredit(creditNid);
		// 投资用户开户信息
		Account assignBankAccount = this.getAccount(assignUserId);
		// 投资用户银行账户
		String assignAccountId = assignBankAccount.getAccountId();
		// 查询相应的债权承接记录
		HjhDebtCreditTender creditTender = this.getCreditTenderHjh(assignNid);
		if (Validator.isNull(creditTender)) {
			throw new Exception("投资人未开户。[用户ID：" + repayUserId + "]，" + "[投资订单号：" + tenderOrderId + "]");
		}
		// 是否月标(true:月标, false:天标)
		boolean isMonth = CustomConstants.BORROW_STYLE_PRINCIPAL.equals(borrowStyle) || CustomConstants.BORROW_STYLE_MONTH.equals(borrowStyle)
				|| CustomConstants.BORROW_STYLE_ENDMONTH.equals(borrowStyle);
		//获得债转详情
		HjhDebtDetail debtDetail = this.getDebtDetail(assignNid, periodNow,assignUserId);
		// 分期还款计划表
		BorrowRecoverPlan borrowRecoverPlan = null;
		// 判断该收支明细是否存在时,跳出本次循环
		if (countCreditAccountListByNid(repayOrderId)) {
			return true;
		}
		//承接计划订单号
		String assignPlanOrderId = creditRepay.getAssignPlanOrderId();
		HjhAccedeExample accedeExample = new HjhAccedeExample();
		accedeExample.createCriteria().andAccedeOrderIdEqualTo(assignPlanOrderId);
		List<HjhAccede> accedeList = this.hjhAccedeMapper.selectByExample(accedeExample);
		HjhAccede hjhAccede = accedeList.get(0);
		BigDecimal frostAccount = hjhAccede.getFrostAccount();
		BigDecimal availableInvestAccount = hjhAccede.getAvailableInvestAccount();
		logger.info("---------------------还款标的:" + borrowNid + ",承接订单号："+ creditRepay.getCreditNid() +"订单号:" + assignPlanOrderId + ",处理前冻结金额：" + frostAccount + "-----------");
		logger.info("---------------------还款标的:" + borrowNid + ",承接订单号："+ creditRepay.getCreditNid() +"订单号:" + assignPlanOrderId + ",处理前订单可用金额：" + availableInvestAccount + "-----------");
		HjhAccede paramInfo = new HjhAccede();
		paramInfo.setId(hjhAccede.getId());

		String dateStr = "";
		if(hjhAccede.getEndDate() != null){
			GetDate.dateToString(hjhAccede.getEndDate());
		}
		logger.info("---------------------还款标的:" + borrowNid + ",承接订单号："+ creditRepay.getCreditNid() +"订单号:" + assignPlanOrderId + ",结束时间:" + dateStr + "-----------");
		if (hjhAccede.getEndDate() != null && isForstTime(hjhAccede.getEndDate())) {
			logger.info("---------------------还款标的:" + borrowNid + ",承接订单号："+ creditRepay.getCreditNid() +"订单号:" + assignPlanOrderId + ",开始增加冻结金额,原冻结金额:"+ hjhAccede.getFrostAccount() +",增加金额:" + repayAccount + "-----------");
			HjhRepayExample repayExample = new HjhRepayExample();
			repayExample.createCriteria().andAccedeOrderIdEqualTo(assignPlanOrderId);
			List<HjhRepay> repayList = this.hjhRepayMapper.selectByExample(repayExample);
			HjhRepay hjhRepay = repayList.get(0);
			HjhRepay repayParam = new HjhRepay();
			repayParam.setId(hjhRepay.getId());
			paramInfo.setFrostAccount(repayAccount);//计划冻结金额
			repayParam.setRepayTotal(repayAccount);
			repayParam.setPlanRepayCapital(repayCapital);
			repayParam.setPlanRepayInterest(repayInterest);
			repayParam.setRepayAlready(repayAccount);
			this.hjhPlanCustomizeMapper.updateHjhRepayForHjhRepay(repayParam);
			logger.info("---------------------还款标的:" + borrowNid + ",承接订单号:" + creditRepay.getCreditNid() + "订单号:" + assignPlanOrderId + ",开始增加还款,原回款金额:"+ hjhRepay.getRepayAlready() +",增加金额:" + repayAccount + "-----------");
		}else{
			logger.info("---------------------还款标的:" + borrowNid + ",承接订单号："+ creditRepay.getCreditNid() +"订单号:" + assignPlanOrderId + ",开始增加可用金额-----------");
			paramInfo.setAvailableInvestAccount(repayAccount);//计划订单可用金额
		}
		this.hjhPlanCustomizeMapper.updateHjhAccedeForHjhProcess(paramInfo);
		
		// 债转的下次还款时间
		int creditRepayNextTime = creditRepay.getAssignRepayNextTime();
		// 更新账户信息(投资人)
		Account assignUserAccount = new Account();
		assignUserAccount.setUserId(assignUserId);
		if (hjhAccede.getEndDate() != null && isForstTime(hjhAccede.getEndDate())) {
			assignUserAccount.setPlanFrost(repayAccount);//汇计划冻结金额
		}else{
			assignUserAccount.setPlanBalance(repayAccount);//汇计划可用金额
		}
		assignUserAccount.setBankBalanceCash(repayAccount);// 投资人银行可用余额
		boolean investAccountFlag = this.adminAccountCustomizeMapper.updateOfRepayPlanTender(assignUserAccount) > 0 ? true : false;
		if (!investAccountFlag) {
			throw new Exception("承接人资金记录(huiyingdai_account)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
		}
		// 取得承接人账户信息
		assignUserAccount = this.getAccountByUserId(creditRepay.getUserId());
		if (Validator.isNull(assignAccount)) {
			throw new Exception("承接人账户信息不存在。[投资人ID：" + borrowTender.getUserId() + "]，" + "[投资订单号：" + tenderOrderId + "]");
		}
		// 写入收支明细
		AccountList accountList = new AccountList();
		accountList.setNid(repayOrderId); // 还款订单号
		accountList.setUserId(assignUserId); // 投资人
		accountList.setAmount(repayAccount); // 投资总收入
		/** 银行相关 */
		accountList.setAccountId(assignAccountId);
		accountList.setBankAwait(assignUserAccount.getBankAwait());
		accountList.setBankAwaitCapital(assignUserAccount.getBankAwaitCapital());
		accountList.setBankAwaitInterest(assignUserAccount.getBankAwaitInterest());
		accountList.setBankBalance(assignUserAccount.getBankBalance());
		accountList.setBankFrost(assignUserAccount.getBankFrost());
		accountList.setBankInterestSum(assignUserAccount.getBankInterestSum());
		accountList.setBankInvestSum(assignUserAccount.getBankInvestSum());
		accountList.setBankTotal(assignUserAccount.getBankTotal());
		accountList.setBankWaitCapital(assignUserAccount.getBankWaitCapital());
		accountList.setBankWaitInterest(assignUserAccount.getBankWaitInterest());
		accountList.setBankWaitRepay(assignUserAccount.getBankWaitRepay());
		// 如果是机构垫付还款
		if (isRepayOrgFlag == 1 && isApicronRepayOrgFlag == 1) {
			if (forAccountId.equals(assignAccountId) && repayOrderId.equals(orderId) && txAmount.compareTo(repayAccount) == 0) {
				accountList.setCheckStatus(1);
			} else {
				accountList.setCheckStatus(0);
			}
		} else {
			if (forAccountId.equals(assignAccountId) && repayOrderId.equals(orderId) && txAmount.compareTo(repayCapital) == 0) {
				accountList.setCheckStatus(1);
			} else {
				accountList.setCheckStatus(0);
			}
		}
		accountList.setTradeStatus(1);// 交易状态 0:失败 1:成功
		accountList.setIsBank(1);
		accountList.setTxDate(txDate);
		accountList.setTxTime(txTime);
		accountList.setSeqNo(seqNo);
		accountList.setBankSeqNo(bankSeqNo);
		/** 非银行相关 */
		if (hjhAccede.getEndDate() != null && isForstTime(hjhAccede.getEndDate())) {
			accountList.setType(3); // 冻结
		}else{
			accountList.setType(1); // 1收入
		}
		
		if (hjhAccede.getEndDate() != null && isForstTime(hjhAccede.getEndDate())) {
			accountList.setTrade("credit_tender_recover_forst"); // 投资成功冻结
		}else{
			accountList.setTrade("credit_tender_recover_yes"); // 投资成功
		}
		accountList.setTradeCode("balance"); // 余额操作
		accountList.setTotal(assignUserAccount.getTotal()); // 投资人资金总额
		accountList.setBalance(assignUserAccount.getBalance()); // 投资人可用金额
		accountList.setPlanFrost(assignUserAccount.getPlanFrost());// 汇添金冻结金额
		accountList.setPlanBalance(assignUserAccount.getPlanBalance());// 汇添金可用金额
		accountList.setFrost(assignUserAccount.getFrost()); // 投资人冻结金额
		accountList.setAwait(assignUserAccount.getAwait()); // 投资人待收金额

		accountList.setOperator(CustomConstants.OPERATOR_AUTO_REPAY); // 操作者
		accountList.setRemark(borrowNid);
		accountList.setIp(borrow.getAddIp()); // 操作IP
//		accountList.setIsUpdate(0);
//		accountList.setBaseUpdate(0);
//		accountList.setInterest(BigDecimal.ZERO); // 利息
		accountList.setWeb(0); // PC
		accountList.setIsShow(1);//投资人不显示
		boolean assignAccountListFlag = this.accountListMapper.insertSelective(accountList) > 0 ? true : false;
		if (!assignAccountListFlag) {
			throw new Exception("收支明细(huiyingdai_account_list)写入失败！" + "[承接订单号：" + assignNid + "]");
		}
		// 更新相应的债转投资表
		// 债转已还款总额
		creditTender.setRepayAccountYes(creditTender.getRepayAccountYes().add(repayAccount));
		// 债转已还款本金
		creditTender.setRepayCapitalYes(creditTender.getRepayCapitalYes().add(repayCapital));
		// 债转已还款利息
		creditTender.setRepayInterestYes(creditTender.getRepayInterestYes().add(repayInterest));
		// 债转最近还款时间
		creditTender.setAssignRepayLastTime(!isMonth ? nowTime : 0);
		// 债转下次还款时间
		creditTender.setAssignRepayNextTime(!isMonth ? 0 : creditRepayNextTime);
		// 债转还款状态
		if (isMonth) {
			// 取得分期还款计划表
			borrowRecoverPlan = getBorrowRecoverPlan(borrowNid, periodNow, tenderUserId, tenderOrderId);
			if (borrowRecoverPlan == null) {
				throw new Exception("分期还款计划表数据不存在。[借款编号：" + borrowNid + "]，" + "[承接订单号：" + creditRepay.getAssignOrderId() + "]" + "[期数：" + periodNow + "]");
			}
			// 债转状态
			if (borrowRecoverPlan != null && Validator.isNotNull(periodNext) && periodNext > 0) {
				creditTender.setStatus(0);
			} else {
				creditTender.setStatus(1);
				// 债转最后还款时间
				creditTender.setAssignRepayYesTime(nowTime);
			}
		} else {
			creditTender.setStatus(1);
			// 债转最后还款时间
			creditTender.setAssignRepayYesTime(nowTime);
		}
		// 债转还款期
		creditTender.setRepayPeriod(periodNow);
		boolean creditTenderFlag = this.hjhDebtCreditTenderMapper.updateByPrimaryKeySelective(creditTender) > 0 ? true : false;
		if (!creditTenderFlag) {
			throw new Exception("债转投资表(hyjf_hjh_credit_tender)更新失败！" + "[承接订单号：" + creditRepay.getAssignOrderId() + "]");
		}
		creditRepay.setReceiveAccountYes(creditRepay.getReceiveAccountYes().add(repayAccount));
		creditRepay.setReceiveCapitalYes(creditRepay.getReceiveCapitalYes().add(repayCapital));
		creditRepay.setReceiveInterestYes(creditRepay.getReceiveInterestYes().add(repayInterest));
		creditRepay.setAssignRepayLastTime(nowTime);
		creditRepay.setAssignRepayYesTime(nowTime);
		creditRepay.setManageFee(manageFee);
		creditRepay.setRepayStatus(1);
		boolean creditRepayFlag = this.hjhDebtCreditRepayMapper.updateByPrimaryKeySelective(creditRepay) > 0 ? true : false;
		if (!creditRepayFlag) {
			throw new Exception("承接人还款表(hyjf_hjh_credit_repay)更新失败！" + "[债转编号：" + creditRepay.getCreditNid() + "]");
		}
		// 债转总表数据更新
		// 更新债转已还款总额
		borrowCredit.setRepayAccount(borrowCredit.getRepayAccount().add(repayAccount));
		// 更新债转已还款本金
		borrowCredit.setRepayCapital(borrowCredit.getRepayCapital().add(repayCapital));
		// 更新债转已还款利息
		borrowCredit.setRepayInterest(borrowCredit.getRepayInterest().add(repayInterest));
		// 债转下次还款时间
		borrowCredit.setCreditRepayNextTime(isMonth ? creditRepayNextTime : 0);
		if (borrowCredit.getCreditStatus() == 0) {
			borrowCredit.setCreditStatus(1);
		}
		// 更新债转总表
		boolean borrowCreditFlag = this.hjhDebtCreditMapper.updateByPrimaryKeySelective(borrowCredit) > 0 ? true : false;
		logger.info("======================标的号：" + borrowNid + ",债转编号：" + creditNid + ",更新hyjf_hjh_debt_credit表完成！");
		// 债转总表更新成功
		if (!borrowCreditFlag) {
			throw new Exception("债转记录(huiyingdai_borrow_credit)更新失败！" + "[承接订单号：" + creditRepay.getCreditNid() + "]");
		}
		//更新债权明细表
		// 更新DebtDetail表
		debtDetail.setRepayActionTime(nowTime);
		// 已还款
		debtDetail.setRepayStatus(1);
		// 账户管理费
		debtDetail.setManageFee(manageFee);
		// 已还本金
		debtDetail.setRepayCapitalYes(repayCapital);
		// 账户服务费
//		debtDetail.setServiceFee(big);
		// 已还利息
		debtDetail.setRepayInterestYes(repayInterest);
		// 未收本金
		debtDetail.setRepayCapitalWait(BigDecimal.ZERO);
		// 未收利息
		debtDetail.setRepayInterestWait(BigDecimal.ZERO);
		// 提前还款状态
		debtDetail.setAdvanceStatus(creditRepay.getAdvanceStatus());
		// 提前还款天数
		debtDetail.setAdvanceDays(creditRepay.getAdvanceDays());
		// 提前还款利息
		debtDetail.setAdvanceInterest(chargeInterest);
		// 延期天数
		debtDetail.setDelayDays(creditRepay.getDelayDays());
		// 延期利息
		debtDetail.setDelayInterest(delayInterest);
		// 逾期天数
		debtDetail.setLateDays(creditRepay.getLateDays());
		// 逾期利息
		debtDetail.setLateInterest(lateInterest);
		// 还款订单号
		debtDetail.setRepayOrderId(repayOrderId);
		// 还款日期
		debtDetail.setRepayOrderDate(creditRepay.getCreditRepayOrderDate());
		// 债权更新时间
//		debtDetail.setUpdateTime(nowTime);
		//结束债权
		debtDetail.setDelFlag(1);
		// 到期公允价值
		debtDetail.setExpireFairValue(BigDecimal.ZERO);
		
		int flag = this.hjhDebtDetailMapper.updateByPrimaryKey(debtDetail);
		if (flag > 0) {
			logger.info("---------------------更新hjh_debt_detail信息完成,投资订单号:"  + tenderOrderId);
		}
		
		// 更新还款表（不分期）
		borrowRecover.setRepayBatchNo(repayBatchNo);
		borrowRecover.setRecoverAccountYes(borrowRecover.getRecoverAccountYes().add(repayAccount)); // 已还款总额
		// 已还款本金
		borrowRecover.setRecoverCapitalYes(borrowRecover.getRecoverCapitalYes().add(repayCapital));
		// 已还款利息
		borrowRecover.setRecoverInterestYes(borrowRecover.getRecoverInterestYes().add(repayInterest));
		// 待还金额
		borrowRecover.setRecoverAccountWait(borrowRecover.getRecoverAccountWait().subtract(assignAccount));
		// 待还本金
		borrowRecover.setRecoverCapitalWait(borrowRecover.getRecoverCapitalWait().subtract(assignCapital));
		// 待还利息
		borrowRecover.setRecoverInterestWait(borrowRecover.getRecoverInterestWait().subtract(assignInterest));
		// 已还款提前还款利息
		borrowRecover.setRepayChargeInterest(borrowRecover.getRepayChargeInterest().add(chargeInterest));
		// 已还款延期还款利息
		borrowRecover.setRepayDelayInterest(borrowRecover.getRepayDelayInterest().add(delayInterest));
		// 已还款逾期还款利息
		borrowRecover.setRepayLateInterest(borrowRecover.getRepayLateInterest().add(lateInterest));
		if (lateInterest.compareTo(BigDecimal.ZERO) == 1) {
			logger.info("原逾期还款利息：{},增加：{}", borrowRecover.getRepayLateInterest(), lateInterest);
		}
		// 已还款管理费
		borrowRecover.setRecoverFeeYes(borrowRecover.getRecoverFeeYes().add(manageFee));
		// 更新还款表
		boolean creditBorrowRecoverFlag = this.borrowRecoverMapper.updateByPrimaryKeySelective(borrowRecover) > 0 ? true : false;
		if (!creditBorrowRecoverFlag) {
			throw new Exception("投资人还款表(huiyingdai_borrow_recover)更新失败！" + "[债转编号：" + creditRepay.getCreditNid() + "]");
		}
		// 更新总的还款明细
		borrowRepay.setRepayAccountAll(borrowRepay.getRepayAccountAll().add(repayAccount).add(manageFee));
		borrowRepay.setRepayAccountYes(borrowRepay.getRepayAccountYes().add(repayAccount));
		borrowRepay.setRepayCapitalYes(borrowRepay.getRepayCapitalYes().add(repayCapital));
		borrowRepay.setRepayInterestYes(borrowRepay.getRepayInterestYes().add(repayInterest));
		// 逾期天数
		borrowRepay.setLateRepayDays(borrowRecover.getLateDays());
		// 提前天数
		borrowRepay.setChargeDays(borrowRecover.getChargeDays());
		// 延期天数
		borrowRepay.setDelayDays(borrowRecover.getDelayDays());
		// 用户是否提前还款
		borrowRepay.setAdvanceStatus(borrowRecover.getAdvanceStatus());
		// 还款来源
		if (isRepayOrgFlag == 1 && isApicronRepayOrgFlag == 1) {
			// 还款来源（1、借款人还款，2、机构垫付，3、保证金垫付）
			borrowRepay.setRepayMoneySource(2);
		} else {
			borrowRepay.setRepayMoneySource(1);
		}
		// 实际还款人（借款人、垫付机构、保证金）的用户ID
		borrowRepay.setRepayUserId(repayUserId);
		// 实际还款人（借款人、垫付机构、保证金）的用户名
		borrowRepay.setRepayUsername(repayUserName);
		boolean borrowRepayFlag = this.borrowRepayMapper.updateByPrimaryKeySelective(borrowRepay) > 0 ? true : false;
		if (!borrowRepayFlag) {
			throw new Exception("总的还款明细表(huiyingdai_borrow_repay)更新失败！" + "[项目编号：" + borrowNid + "]");
		}
		// 如果分期
		if (isMonth) {
			// 更新还款表（分期）
			// 已还款总额
			borrowRecoverPlan.setRepayBatchNo(repayBatchNo);
			borrowRecoverPlan.setRecoverAccountYes(borrowRecoverPlan.getRecoverAccountYes().add(repayAccount));
			// 已还款本金
			borrowRecoverPlan.setRecoverCapitalYes(borrowRecoverPlan.getRecoverCapitalYes().add(repayCapital));
			// 已还款利息
			borrowRecoverPlan.setRecoverInterestYes(borrowRecoverPlan.getRecoverInterestYes().add(repayInterest));
			// 待还金额
			borrowRecoverPlan.setRecoverAccountWait(borrowRecoverPlan.getRecoverAccountWait().subtract(assignAccount));
			logger.info("--------------------------标的号：" + borrowNid + ",债转还款记录任务表金额，原金额：" + borrowRecoverPlan.getRecoverAccountWait() + ",扣减金额：" + assignAccount);
			// 待还本金
			borrowRecoverPlan.setRecoverCapitalWait(borrowRecoverPlan.getRecoverCapitalWait().subtract(assignCapital));
			// 待还利息
			borrowRecoverPlan.setRecoverInterestWait(borrowRecoverPlan.getRecoverInterestWait().subtract(assignInterest));
			// 已还款提前还款利息
			borrowRecoverPlan.setRepayChargeInterest(borrowRecoverPlan.getRepayChargeInterest().add(chargeInterest));
			// 已还款延期还款利息
			borrowRecoverPlan.setRepayDelayInterest(borrowRecoverPlan.getRepayDelayInterest().add(delayInterest));
			// 已还款逾期还款利息
			borrowRecoverPlan.setRepayLateInterest(borrowRecoverPlan.getRepayLateInterest().add(lateInterest));
			// 已还款管理费
			borrowRecoverPlan.setRecoverFeeYes(borrowRecoverPlan.getRecoverFeeYes().add(manageFee));
			// 更新还款计划表
			boolean borrowRecoverPlanFlag = this.borrowRecoverPlanMapper.updateByPrimaryKeySelective(borrowRecoverPlan) > 0 ? true : false;
			if (!borrowRecoverPlanFlag) {
				throw new Exception("分期还款计划表更新失败。[借款编号：" + borrowNid + "]，" + "[承接订单号：" + creditRepay.getAssignOrderId() + "]" + "[期数：" + periodNow + "]");
			}
			// 更新总的还款计划
			BorrowRepayPlan borrowRepayPlan = getBorrowRepayPlan(borrowNid, periodNow);
			if (borrowRepayPlan != null) {
				// 还款总额
				borrowRepayPlan.setRepayAccountAll(borrowRepayPlan.getRepayAccountAll().add(repayAccount).add(manageFee));
				// 已还金额
				borrowRepayPlan.setRepayAccountYes(borrowRepayPlan.getRepayAccountYes().add(repayAccount));
				// 已还利息
				borrowRepayPlan.setRepayInterestYes(borrowRepayPlan.getRepayInterestYes().add(repayInterest));
				// 已还本金
				borrowRepayPlan.setRepayCapitalYes(borrowRepayPlan.getRepayCapitalYes().add(repayCapital));
				// 逾期天数
				borrowRepayPlan.setLateRepayDays(borrowRecoverPlan.getLateDays());
				// 提前天数
				borrowRepayPlan.setChargeDays(borrowRecoverPlan.getChargeDays());
				// 延期天数
				borrowRepayPlan.setDelayDays(borrowRecoverPlan.getDelayDays());
				// 用户是否提前还款
				borrowRepayPlan.setAdvanceStatus(borrowRecoverPlan.getAdvanceStatus());
				// 还款来源
				if (isRepayOrgFlag == 1 && isApicronRepayOrgFlag == 1) {
					// 还款来源（1、借款人还款，2、机构垫付，3、保证金垫付）
					borrowRepayPlan.setRepayMoneySource(2);
				} else {
					borrowRepayPlan.setRepayMoneySource(1);
				}
				// 实际还款人（借款人、垫付机构、保证金）的用户ID
				borrowRepayPlan.setRepayUserId(repayUserId);
				// 实际还款人（借款人、垫付机构、保证金）的用户名
				borrowRepayPlan.setRepayUsername(repayUserName);
				boolean borrowRepayPlanFlag = this.borrowRepayPlanMapper.updateByPrimaryKeySelective(borrowRepayPlan) > 0 ? true : false;
				if (!borrowRepayPlanFlag) {
					throw new Exception("还款计划表(huiyingdai_borrow_repay_plan)更新失败！" + "[承接订单号：" + creditRepay.getAssignOrderId() + "]" + "[期数：" + periodNow + "]");
				}
			} else {
				throw new Exception("还款计划表(huiyingdai_borrow_repay_plan)查询失败！" + "[承接订单号：" + creditRepay.getAssignOrderId() + "]" + "[期数：" + periodNow + "]");
			}
		}
		// 更新借款表
//		borrow = getBorrowByNid(borrowNid);
		Borrow newBrrow = new Borrow();
		newBrrow.setId(borrow.getId());
		BigDecimal borrowManager = borrow.getBorrowManager() == null ? BigDecimal.ZERO : new BigDecimal(borrow.getBorrowManager());
		newBrrow.setBorrowManager(borrowManager.add(manageFee).toString());
		// 总还款利息
		newBrrow.setRepayAccountYes(borrow.getRepayAccountYes().add(repayAccount));
		// 总还款利息
		newBrrow.setRepayAccountInterestYes(borrow.getRepayAccountInterestYes().add(repayInterest));
		// 总还款本金
		newBrrow.setRepayAccountCapitalYes(borrow.getRepayAccountCapitalYes().add(repayCapital));
		// 未还款总额
		newBrrow.setRepayAccountWait(borrow.getRepayAccountWait().subtract(assignAccount));
		// 未还款本金
		newBrrow.setRepayAccountCapitalWait(borrow.getRepayAccountCapitalWait().subtract(assignCapital));
		// 未还款利息
		newBrrow.setRepayAccountInterestWait(borrow.getRepayAccountInterestWait().subtract(assignInterest));
		// 项目的管理费
		newBrrow.setRepayFeeNormal(borrow.getRepayFeeNormal().add(manageFee));
		boolean borrowFlag = this.borrowMapper.updateByPrimaryKeySelective(newBrrow) > 0 ? true : false;
		if (!borrowFlag) {
			throw new Exception("借款详情(huiyingdai_borrow)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
		}
		// 更新投资表
		// 已还款金额
		borrowTender.setRecoverAccountYes(borrowTender.getRecoverAccountYes().add(repayAccount));
		// 已还款利息
		borrowTender.setRecoverAccountInterestYes(borrowTender.getRecoverAccountInterestYes().add(repayInterest));
		// 已还款本金
		borrowTender.setRecoverAccountCapitalYes(borrowTender.getRecoverAccountCapitalYes().add(repayCapital));
		// 待还金额
		borrowTender.setRecoverAccountWait(borrowTender.getRecoverAccountWait().subtract(assignAccount));
		// 待还本金
		borrowTender.setRecoverAccountCapitalWait(borrowTender.getRecoverAccountCapitalWait().subtract(assignCapital));
		// 待还利息
		borrowTender.setRecoverAccountInterestWait(borrowTender.getRecoverAccountInterestWait().subtract(assignInterest));
		boolean borrowTenderFlag = borrowTenderMapper.updateByPrimaryKeySelective(borrowTender) > 0 ? true : false;
		if (!borrowTenderFlag) {
			throw new Exception("投资表(huiyingdai_borrow_tender)更新失败！" + "[投资订单号：" + tenderOrderId + "]");
		}
		// 管理费大于0时,插入网站收支明细
		if (manageFee.compareTo(BigDecimal.ZERO) > 0) {
			
			AccountWebListVO accountWebList = new AccountWebListVO();
			accountWebList.setOrdid(creditRepay.getAssignOrderId() + "_" + periodNow);// 订单号
			accountWebList.setBorrowNid(borrowNid); // 投资编号
			accountWebList.setUserId(repayUserId); // 借款人
			accountWebList.setAmount(Double.valueOf(manageFee.toString())); // 管理费
			accountWebList.setType(CustomConstants.TYPE_IN); // 类型1收入,2支出
			accountWebList.setTrade(CustomConstants.TRADE_REPAYFEE); // 管理费
			accountWebList.setTradeType(CustomConstants.TRADE_REPAYFEE_NM); // 账户管理费
			accountWebList.setRemark(borrowNid); // 投资编号
			accountWebList.setCreateTime(nowTime);
			accountWebList.setFlag(1);

			//网站首支明细队列
			try {
				logger.info("发送credit收支明细---" + repayUserId + "---------" + manageFee);
				commonProducer.messageSend(new MessageContent(MQConstant.ACCOUNT_WEB_LIST_TOPIC, UUID.randomUUID().toString(), accountWebList));
            } catch (MQException e) {
                logger.error("计划还款中发生系统", e);
            }
			
		}
		apicron.setSucAmount(apicron.getSucAmount().add(repayAccount.add(manageFee)));
		apicron.setSucCounts(apicron.getSucCounts() + 1);
		apicron.setFailAmount(apicron.getFailAmount().subtract(repayAccount.add(manageFee)));
		apicron.setFailCounts(apicron.getFailCounts() - 1);
		boolean apicronSuccessFlag = this.borrowApicronMapper.updateByPrimaryKeySelective(apicron) > 0 ? true : false;
		if (!apicronSuccessFlag) {
			throw new Exception("批次还款记录(borrowApicron)更新失败!" + "[项目编号：" + borrowNid + "]");
		}
		logger.info("------债转还款承接部分完成---承接订单号：" + borrowCredit.getCreditNid() + "---------还款订单号" + repayOrderId);
//		logger.info("---------------------还款标的:" + borrowNid + ",订单号:" + assignPlanOrderId + ",是否可复投:" + !isForstTime(hjhAccede.getEndDate()) + "-----------");
		return true;
	}

	
	/**
	 * 获得债权明细记录
	 * @param tenderOrderId
	 * @param periodNow
	 * @param assignUserId 
	 * @return
	 */
	private HjhDebtDetail getDebtDetail(String tenderOrderId, Integer periodNow, int assignUserId) {
		
		HjhDebtDetailExample example = new HjhDebtDetailExample();
		example.createCriteria().andOrderIdEqualTo(tenderOrderId).andRepayPeriodEqualTo(periodNow).andStatusEqualTo(1).andUserIdEqualTo(assignUserId);
		List<HjhDebtDetail> detailList = this.hjhDebtDetailMapper.selectByExample(example );
		if (detailList != null && detailList.size() > 0) {
			return detailList.get(0);
		}
		
		return null;
		
	}

	/**
	 * 根据承接订单号获取汇转让信息
	 * 
	 * @param creditNid
	 * @return
	 */
	private HjhDebtCredit getBorrowCredit(String creditNid) {
		HjhDebtCreditExample example = new HjhDebtCreditExample();
		HjhDebtCreditExample.Criteria crt = example.createCriteria();
		crt.andCreditNidEqualTo(creditNid);
		List<HjhDebtCredit> borrowCreditList = this.hjhDebtCreditMapper.selectByExample(example);
		if (borrowCreditList != null && borrowCreditList.size() == 1) {
			return borrowCreditList.get(0);
		}
		return null;
	}

	/**
	 * 获取承接信息
	 * 
	 * @param assignNid
	 * @return
	 */
	private HjhDebtCreditTender getCreditTenderHjh(String assignNid) {
		
		HjhDebtCreditTenderExample example = new HjhDebtCreditTenderExample();
		example.createCriteria().andAssignOrderIdEqualTo(assignNid);
		List<HjhDebtCreditTender> creditTenderList = this.hjhDebtCreditTenderMapper.selectByExample(example);
		if (creditTenderList != null && creditTenderList.size() == 1) {
			return creditTenderList.get(0);
		}
		return null;
	}

	/**
	 * 更新还款完成状态
	 * 
	 * @param borrow
	 *
	 * @param apicron
	 * @param apicron
	 * @throws Exception
	 */
	@Override
	public boolean updateBorrowStatus(BorrowApicron apicron, Borrow borrow, BorrowInfo borrowInfo) throws Exception {

		int nowTime = GetDate.getNowTime10();
		String borrowNid = borrow.getBorrowNid();
		int borrowUserId = borrow.getUserId();
		String borrowStyle = borrow.getBorrowStyle();// 项目还款方式
		int borrowId = borrow.getId();// 标的记录主键
		// 标的是否可用担保机构还款
		int isRepayOrgFlag = Validator.isNull(borrowInfo.getIsRepayOrgFlag()) ? 0 : borrowInfo.getIsRepayOrgFlag();
		apicron = this.borrowApicronMapper.selectByPrimaryKey(apicron.getId());
		int repayUserId = apicron.getUserId();
		int periodNow = apicron.getPeriodNow();
		int repayCount = apicron.getTxCounts();// 放款总笔数
		int txDate = Validator.isNotNull(apicron.getTxDate()) ? apicron.getTxDate() : 0;// 批次时间yyyyMMdd
		int txTime = Validator.isNotNull(apicron.getTxTime()) ? apicron.getTxTime() : 0;// 批次时间hhmmss
		String seqNo = Validator.isNotNull(apicron.getSeqNo()) ? String.valueOf(apicron.getSeqNo()) : null;// 流水号
		String bankSeqNo = Validator.isNotNull(apicron.getBankSeqNo()) ? String.valueOf(apicron.getBankSeqNo()) : null;// 银行唯一订单号
		// 是否是担保机构还款
		int isApicronRepayOrgFlag = Validator.isNull(apicron.getIsRepayOrgFlag()) ? 0 : apicron.getIsRepayOrgFlag();
		// 还款人账户信息
		Account repayBankAccount = this.getAccount(repayUserId);
		String repayAccountId = repayBankAccount.getAccountId();
		String apicronNid = apicron.getNid();
		logger.info("-----------还款完成，更新状态开始---" + borrowNid + "---------【还款期数】" + periodNow);
		// 还款期数
		Integer borrowPeriod = Validator.isNull(borrow.getBorrowPeriod()) ? 1 : borrow.getBorrowPeriod();
		// 剩余还款期数
		Integer periodNext = borrowPeriod - periodNow;
		// 是否月标(true:月标, false:天标)
		boolean isMonth = CustomConstants.BORROW_STYLE_PRINCIPAL.equals(borrowStyle) || CustomConstants.BORROW_STYLE_MONTH.equals(borrowStyle)
				|| CustomConstants.BORROW_STYLE_ENDMONTH.equals(borrowStyle);
		int failCount = 0;
		int repayStatus = 0;
		int status = 4;
		String repayType = TYPE_WAIT;
		int repayYesTime = 0;
		// 标的总表信息
		Borrow newBorrow = new Borrow();
		if (isMonth) {
			// 查询recover
			BorrowRecoverPlanExample recoverPlanExample = new BorrowRecoverPlanExample();
			recoverPlanExample.createCriteria().andBorrowNidEqualTo(borrowNid).andRecoverPeriodEqualTo(periodNow).andRecoverStatusNotEqualTo(1);
			failCount = this.borrowRecoverPlanMapper.countByExample(recoverPlanExample);
			// 如果还款全部完成
			if (failCount == 0) {
				if (periodNext == 0) {
					repayType = TYPE_WAIT_YES;
					repayStatus = 1;
					repayYesTime = nowTime;
					status = 5;
				}
				// 还款总表
				BorrowRepay borrowRepay = this.getBorrowRepayAsc(borrowNid, apicron);
				borrowRepay.setRepayType(repayType);
				borrowRepay.setRepayStatus(repayStatus); // 已还款
//				borrowRepay.setRepayDays("0");
//				borrowRepay.setRepayStep(4);
				borrowRepay.setRepayPeriod(isMonth ? periodNext : 1);
				borrowRepay.setRepayActionTime(nowTime);// 实际还款时间
				// 分期的场合，根据借款编号和还款期数从还款计划表中取得还款时间
				BorrowRepayPlanExample example = new BorrowRepayPlanExample();
				BorrowRepayPlanExample.Criteria repayPlanCriteria = example.createCriteria();
				repayPlanCriteria.andBorrowNidEqualTo(borrowNid);
				repayPlanCriteria.andRepayPeriodEqualTo(periodNow + 1);
				List<BorrowRepayPlan> replayPlan = borrowRepayPlanMapper.selectByExample(example);
				if (replayPlan.size() > 0) {
					BorrowRepayPlan borrowRepayPlanNext = replayPlan.get(0);
					if (borrowRepayPlanNext != null) {
						// 取得下期还款时间
						int repayTime = borrowRepayPlanNext.getRepayTime();
						// 设置下期还款时间
						borrowRepay.setRepayTime(repayTime);
						// 设置下期还款时间
						newBorrow.setRepayNextTime(repayTime);
					}
				} else {
					// 还款成功最后时间
					borrowRepay.setRepayYestime(repayYesTime);
				}
				// 更新相应的还款计划表
				BorrowRepayPlan borrowRepayPlan = getBorrowRepayPlan(borrowNid, periodNow);
				if (Validator.isNull(borrowRepayPlan)) {
					throw new Exception("未查询到相应的分期还款borrowRepayPlan记录，项目编号：" + borrowNid + ",还款期数：" + periodNow);
				}
				borrowRepayPlan.setRepayType(TYPE_WAIT_YES);
//				borrowRepayPlan.setRepayDays("0");
//				borrowRepayPlan.setRepayStep(4);
				borrowRepayPlan.setRepayActionTime(String.valueOf(nowTime));
				borrowRepayPlan.setRepayStatus(1);
				borrowRepayPlan.setRepayYestime(nowTime);
				boolean borrowRepayPlanFlag = this.borrowRepayPlanMapper.updateByPrimaryKeySelective(borrowRepayPlan) > 0 ? true : false;
				if (!borrowRepayPlanFlag) {
					throw new Exception("更新相应的分期还款borrowRepayPlan记录失败，项目编号：" + borrowNid + ",还款期数：" + periodNow);
				}
				// 更新BorrowRepay
				BorrowRepayExample repayExample = new BorrowRepayExample();
				repayExample.createCriteria().andBorrowNidEqualTo(borrowNid);
				boolean borrowRepayFlag = this.borrowRepayMapper.updateByExampleSelective(borrowRepay, repayExample) > 0 ? true : false;
				if (!borrowRepayFlag) {
					throw new Exception("更新相应的分期还款borrowRepay记录失败，项目编号：" + borrowNid + ",还款期数：" + periodNow);
				}
				Account repayUserAccount = this.getAccountByUserId(repayUserId);
				BigDecimal repayAccount = borrowRepayPlan.getRepayAccountYes();
				/*BigDecimal repayCapital = borrowRepayPlan.getRepayCapitalYes();*/
				BigDecimal manageFee = borrowRepayPlan.getRepayFee();
				BigDecimal repayAccountWait = borrowRepayPlan.getRepayAccount();
				BigDecimal repayCapitalWait = borrowRepayPlan.getRepayCapital();
				BigDecimal repayInterestWait = borrowRepayPlan.getRepayInterest();
				// 如果是机构垫付还款
				if (isRepayOrgFlag == 1 && isApicronRepayOrgFlag == 1) {
					// 更新垫付机构的Account表的待还款金额
					Account newRepayUserAccount = new Account();
					newRepayUserAccount.setUserId(repayUserAccount.getUserId());
					newRepayUserAccount.setBankTotal(repayAccount.add(manageFee));//add by cwyang 资金总额减少
					newRepayUserAccount.setBankFrost(repayAccount.add(manageFee));
					newRepayUserAccount.setBankAwait(BigDecimal.ZERO);
					newRepayUserAccount.setBankAwaitCapital(BigDecimal.ZERO);
					newRepayUserAccount.setBankWaitRepay(BigDecimal.ZERO);
					newRepayUserAccount.setBankFrostCash(repayAccount.add(manageFee));
					boolean repayUserFlag = this.adminAccountCustomizeMapper.updateOfRepayOrgUser(newRepayUserAccount) > 0 ? true : false;
					if (!repayUserFlag) {
						throw new Exception("垫付机构账户(huiyingdai_account)更新失败！" + "[借款人用户ID：" + borrowUserId + "]");
					}
					//modify by cwyang 借款人账户还款成功后减去管理费+本金+利息
					BigDecimal waitRepay = repayCapitalWait.add(manageFee).add(repayInterestWait);
					// modify by cwyang 调整对应的金额变化
					Account borrowUserAccount = new Account();
					borrowUserAccount.setUserId(borrowUserId);
					borrowUserAccount.setBankTotal(BigDecimal.ZERO);
					borrowUserAccount.setBankFrost(BigDecimal.ZERO);
					borrowUserAccount.setBankWaitRepay(waitRepay);
					borrowUserAccount.setBankWaitCapital(repayCapitalWait);
					borrowUserAccount.setBankWaitInterest(repayInterestWait);
					borrowUserAccount.setBankWaitRepayOrg(BigDecimal.ZERO);
					borrowUserAccount.setBankFrostCash(BigDecimal.ZERO);
					boolean borrowUserFlag = this.adminAccountCustomizeMapper.updateOfRepayBorrowUser(borrowUserAccount) > 0 ? true : false;
					if (!borrowUserFlag) {
						throw new Exception("借款人账户(huiyingdai_account)更新失败！" + "[借款人用户ID：" + borrowUserId + "]");
					}
				} else {
					Account newRepayUserAccount = new Account();
					newRepayUserAccount.setUserId(repayUserId);
					newRepayUserAccount.setBankTotal(repayAccount.add(manageFee));
					newRepayUserAccount.setBankFrost(repayAccount.add(manageFee));
					newRepayUserAccount.setBankWaitRepay(repayAccountWait.add(manageFee));
					newRepayUserAccount.setBankWaitCapital(repayCapitalWait);
					newRepayUserAccount.setBankWaitInterest(repayInterestWait);
					newRepayUserAccount.setBankWaitRepayOrg(BigDecimal.ZERO);
					newRepayUserAccount.setBankFrostCash(repayAccount.add(manageFee));
					boolean repayUserFlag = this.adminAccountCustomizeMapper.updateOfRepayBorrowUser(newRepayUserAccount) > 0 ? true : false;
					if (!repayUserFlag) {
						throw new Exception("借款人账户(huiyingdai_account)更新失败！" + "[借款人用户ID：" + borrowUserId + "]");
					}
				}
				// 插入还款交易明细
				repayUserAccount = this.getAccountByUserId(repayUserId);
				// 插入借款人的收支明细表
				AccountList repayAccountList = new AccountList();
				repayAccountList.setBankAwait(repayUserAccount.getBankAwait());
				repayAccountList.setBankAwaitCapital(repayUserAccount.getBankAwaitCapital());
				repayAccountList.setBankAwaitInterest(repayUserAccount.getBankAwaitInterest());
				repayAccountList.setBankBalance(repayUserAccount.getBankBalance());
				repayAccountList.setBankFrost(repayUserAccount.getBankFrost());
				repayAccountList.setBankInterestSum(repayUserAccount.getBankInterestSum());
				repayAccountList.setBankTotal(repayUserAccount.getBankTotal());
				repayAccountList.setBankWaitCapital(repayUserAccount.getBankWaitCapital());
				repayAccountList.setBankWaitInterest(repayUserAccount.getBankWaitInterest());
				repayAccountList.setBankWaitRepay(repayUserAccount.getBankWaitRepay());
				repayAccountList.setPlanBalance(repayUserAccount.getPlanBalance());//汇计划账户可用余额
				repayAccountList.setPlanFrost(repayUserAccount.getPlanFrost());
				repayAccountList.setAccountId(repayAccountId);
				repayAccountList.setTxDate(txDate);
				repayAccountList.setTxTime(txTime);
				repayAccountList.setSeqNo(seqNo);
				repayAccountList.setBankSeqNo(bankSeqNo);
				repayAccountList.setCheckStatus(1);
				repayAccountList.setTradeStatus(1);
				repayAccountList.setIsBank(1);
				// 非银行相关
				repayAccountList.setNid(apicronNid); // 交易凭证号生成规则BorrowNid_userid_期数
				repayAccountList.setUserId(repayUserId); // 借款人id
				repayAccountList.setAmount(borrowRepayPlan.getRepayAccountYes().add(borrowRepayPlan.getRepayFee())); // 操作金额
				repayAccountList.setType(2); // 收支类型1收入2支出3冻结
				repayAccountList.setTrade("repay_success"); // 交易类型
				repayAccountList.setTradeCode("balance"); // 操作识别码
				repayAccountList.setTotal(repayUserAccount.getTotal()); // 资金总额
				repayAccountList.setBalance(repayUserAccount.getBalance()); // 可用金额
				repayAccountList.setFrost(repayUserAccount.getFrost()); // 冻结金额
				repayAccountList.setAwait(repayUserAccount.getAwait()); // 待收金额
				repayAccountList.setRepay(repayUserAccount.getRepay()); // 待还金额
//				repayAccountList.setCreateTime(nowTime); // 创建时间
				repayAccountList.setOperator(CustomConstants.OPERATOR_AUTO_REPAY); // 操作员
				repayAccountList.setRemark(borrowNid);
				repayAccountList.setIp(""); // 操作IP
//				repayAccountList.setBaseUpdate(0);
				Integer isAllrepay = apicron.getIsAllrepay() == null ? 0 : apicron.getIsAllrepay();
				if(0 == isAllrepay){//非一次性还款时插入资金明细
					boolean repayAccountListFlag = this.accountListMapper.insertSelective(repayAccountList) > 0 ? true : false;
					if (!repayAccountListFlag) {
						throw new Exception("插入还款人还款款交易明細accountList表失败，项目编号:" + borrowNid + "]");
					}
				}
				
				// 非一次性还款和一次性还款最后一期才更新
				if(0 == isAllrepay || (1 == isAllrepay && periodNext == 0)){
					// 更新Borrow
					newBorrow.setRepayFullStatus(repayStatus);
					newBorrow.setRepayStatus(CustomConstants.BANK_BATCH_STATUS_SUCCESS);
					newBorrow.setStatus(status);
					BorrowExample borrowExample = new BorrowExample();
					borrowExample.createCriteria().andIdEqualTo(borrowId);
					boolean borrowFlag = this.borrowMapper.updateByExampleSelective(newBorrow, borrowExample) > 0 ? true : false;
					if (!borrowFlag) {
						throw new Exception("不分期还款成功后，更新相应的borrow表失败," + "项目编号:" + borrowNid + ",还款期数：" + periodNow);
					}
					
				}
				
				BorrowApicronExample apicronExample = new BorrowApicronExample();
				apicronExample.createCriteria().andIdEqualTo(apicron.getId()).andStatusEqualTo(apicron.getStatus());
				apicron.setStatus(CustomConstants.BANK_BATCH_STATUS_SUCCESS);
				apicron.setUpdateTime(new Date());
				boolean apicronFlag = this.borrowApicronMapper.updateByExampleSelective(apicron, apicronExample) > 0 ? true : false;
				if (!apicronFlag) {
					throw new Exception("更新还款任务失败。[项目编号：" + borrowNid + "]");
				}

				if(1 == isAllrepay){//计划一次性还款判断是否整个标的还款，还款后新增交易明细 add by cwyang 2018-5-21
					boolean result = isLastAllRepay(apicron);
					BigDecimal sum = getRepayPlanAccountSum(borrowNid);
					if(result){
						AccountList repayAllAccountList = new AccountList();
						repayAllAccountList.setBankAwait(repayUserAccount.getBankAwait());
						repayAllAccountList.setBankAwaitCapital(repayUserAccount.getBankAwaitCapital());
						repayAllAccountList.setBankAwaitInterest(repayUserAccount.getBankAwaitInterest());
						repayAllAccountList.setBankBalance(repayUserAccount.getBankBalance());
						repayAllAccountList.setBankFrost(repayUserAccount.getBankFrost());
						repayAllAccountList.setBankInterestSum(repayUserAccount.getBankInterestSum());
						repayAllAccountList.setBankTotal(repayUserAccount.getBankTotal());
						repayAllAccountList.setBankWaitCapital(repayUserAccount.getBankWaitCapital());
						repayAllAccountList.setBankWaitInterest(repayUserAccount.getBankWaitInterest());
						repayAllAccountList.setBankWaitRepay(repayUserAccount.getBankWaitRepay());
						repayAllAccountList.setPlanBalance(repayUserAccount.getPlanBalance());//汇计划账户可用余额
						repayAllAccountList.setPlanFrost(repayUserAccount.getPlanFrost());
						repayAllAccountList.setAccountId(repayAccountId);
						repayAllAccountList.setTxDate(txDate);
						repayAllAccountList.setTxTime(txTime);
						repayAllAccountList.setSeqNo(seqNo);
						repayAllAccountList.setBankSeqNo(bankSeqNo);
						repayAllAccountList.setCheckStatus(1);
						repayAllAccountList.setTradeStatus(1);
						repayAllAccountList.setIsBank(1);
						// 非银行相关
						repayAllAccountList.setNid(apicronNid); // 交易凭证号生成规则BorrowNid_userid_期数
						repayAllAccountList.setUserId(repayUserId); // 借款人id
						repayAllAccountList.setAmount(sum); // 操作金额
						repayAllAccountList.setType(2); // 收支类型1收入2支出3冻结
						repayAllAccountList.setTrade("repay_success"); // 交易类型
						repayAllAccountList.setTradeCode("balance"); // 操作识别码
						repayAllAccountList.setTotal(repayUserAccount.getTotal()); // 资金总额
						repayAllAccountList.setBalance(repayUserAccount.getBalance()); // 可用金额
						repayAllAccountList.setFrost(repayUserAccount.getFrost()); // 冻结金额
						repayAllAccountList.setAwait(repayUserAccount.getAwait()); // 待收金额
						repayAllAccountList.setRepay(repayUserAccount.getRepay()); // 待还金额
//						repayAllAccountList.setCreateTime(nowTime); // 创建时间
						repayAllAccountList.setOperator(CustomConstants.OPERATOR_AUTO_REPAY); // 操作员
						repayAllAccountList.setRemark(borrowNid);
						repayAllAccountList.setIp(""); // 操作IP
//						repayAllAccountList.setBaseUpdate(0);
						boolean repayAccountListFlag = this.accountListMapper.insertSelective(repayAllAccountList) > 0 ? true : false;
						if (!repayAccountListFlag) {
							throw new Exception("计划标的插入还款人还款款交易明細accountList表失败，项目编号:" + borrowNid + "]");
						}
					}

				}

				// add by hsy 优惠券还款请求加入到消息队列 start
                Map<String, String> params = new HashMap<String, String>();
                params.put("mqMsgId", GetCode.getRandomCode(10));
                // 借款项目编号
                params.put("borrowNid", borrowNid);
                // 当前期
                params.put("periodNow", String.valueOf(periodNow));
                
                // 核对参数
        		try {
        			logger.info("发送优惠券还款队列---" + borrowNid);
					commonProducer.messageSend(new MessageContent(MQConstant.HZT_COUPON_REPAY_TOPIC, UUID.randomUUID().toString(), params));
                } catch (MQException e) {
                    logger.error("计划还款中发生系统", e);
                }
        		
                // 优惠券还款请求
//                rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGES_COUPON, RabbitMQConstants.ROUTINGKEY_COUPONREPAY, JSONObject.toJSONString(params));
                // add by hsy 优惠券还款请求加入到消息队列 end
                
                
				// insert by zhangjp 增加优惠券还款区分 start
//				CommonSoaUtils.couponRepay(borrowNid, periodNow);
				// insert by zhangjp 增加优惠券还款区分 end
				
				//add by cwyang 更新汇计划资产状态
				updatePlanAsset(borrowNid, status);
				//add by cwyang 更新汇计划相关机构的风险准备金
				//判断是否最后一期
				String instCode = borrowInfo.getInstCode();
				//modify by cwyang 根据配置好的保证金配置进行保证金回滚 迁移by liushouyi
				Integer repayCapitalType = borrow.getRepayCapitalType();
				//回滚标识位：0 到期回滚、1 分期回滚、 2 不回滚
				if ("0".equals(repayCapitalType.toString())) {
					if (periodNext == 0) {
						updateInstitutionData(borrow, borrowInfo);
					}
				} else if ("1".equals(repayCapitalType.toString()) && !"10000000".equals(instCode)){
					//分期回滚
					updateInstitutionDataMonth(instCode,repayCapitalWait,borrowNid);
				}
				try { 
					this.sendSmsForManager(borrowNid);
				} catch (Exception e) {
					logger.error("计划还款中发生系统", e);
				}
			} else if (failCount == repayCount) {
				// 更新Borrow 
				newBorrow.setStatus(status);
				newBorrow.setRepayStatus(CustomConstants.BANK_BATCH_STATUS_FAIL);
				BorrowExample borrowExample = new BorrowExample();
				borrowExample.createCriteria().andIdEqualTo(borrowId);
				boolean borrowFlag = this.borrowMapper.updateByExampleSelective(newBorrow, borrowExample) > 0 ? true : false;
				if (!borrowFlag) {
					throw new Exception("更新borrow表失败，项目编号:" + borrowNid + "]");
				}
				BorrowApicronExample example = new BorrowApicronExample();
				example.createCriteria().andIdEqualTo(apicron.getId()).andStatusEqualTo(apicron.getStatus());
				apicron.setStatus(CustomConstants.BANK_BATCH_STATUS_FAIL);
				apicron.setUpdateTime(new Date());
				boolean apicronFlag = this.borrowApicronMapper.updateByExampleSelective(apicron, example) > 0 ? true : false;
				if (!apicronFlag) {
					throw new Exception("更新状态为(放款成功)失败，项目编号:" + borrowNid + "]");
				}
			} else {
				// 更新Borrow
				newBorrow.setStatus(status);
				newBorrow.setRepayStatus(CustomConstants.BANK_BATCH_STATUS_PART_FAIL);
				BorrowExample borrowExample = new BorrowExample();
				borrowExample.createCriteria().andIdEqualTo(borrowId);
				boolean borrowFlag = this.borrowMapper.updateByExampleSelective(newBorrow, borrowExample) > 0 ? true : false;
				if (!borrowFlag) {
					throw new Exception("更新borrow表失败，项目编号:" + borrowNid + "]");
				}
				BorrowApicronExample example = new BorrowApicronExample();
				example.createCriteria().andIdEqualTo(apicron.getId()).andStatusEqualTo(apicron.getStatus());
				apicron.setStatus(CustomConstants.BANK_BATCH_STATUS_PART_FAIL);
				apicron.setUpdateTime(new Date());
				boolean apicronFlag = this.borrowApicronMapper.updateByExampleSelective(apicron, example) > 0 ? true : false;
				if (!apicronFlag) {
					throw new Exception("更新状态为(放款成功)失败，项目编号:" + borrowNid + "]");
				}
			}
		} else {
			// 查询recover
			BorrowRecoverExample recoverExample = new BorrowRecoverExample();
			recoverExample.createCriteria().andBorrowNidEqualTo(borrowNid).andRecoverStatusNotEqualTo(1);
			failCount = this.borrowRecoverMapper.countByExample(recoverExample);
			logger.info("======================cwyang 标的:" + borrowNid + "还款失败笔数:" + failCount);
			if (failCount == 0) {
				repayType = TYPE_WAIT_YES;
				repayStatus = 1;
				repayYesTime = nowTime;
				status = 5;
				// 还款总表
				BorrowRepay borrowRepay = this.getBorrowRepayAsc(borrowNid, apicron);
				borrowRepay.setRepayType(repayType);
				borrowRepay.setRepayStatus(repayStatus); // 已还款
//				borrowRepay.setRepayDays("0");
//				borrowRepay.setRepayStep(4);
				borrowRepay.setRepayPeriod(isMonth ? periodNext : 1);
				borrowRepay.setRepayActionTime(nowTime);// 实际还款时间
				borrowRepay.setRepayYestime(repayYesTime);// 还款成功最后时间
				// 更新BorrowRepay
				boolean repayFlag = this.borrowRepayMapper.updateByPrimaryKeySelective(borrowRepay) > 0 ? true : false;
				if (!repayFlag) {
					throw new Exception("不分期还款成功后，更新相应的borrowrepay表失败," + "项目编号:" + borrowNid);
				}
				Account repayUserAccount = this.getAccountByUserId(repayUserId);
				BigDecimal repayAccount = borrowRepay.getRepayAccountYes();
				//BigDecimal repayCapital = borrowRepay.getRepayCapitalYes();
				BigDecimal manageFee = borrowRepay.getRepayFee();
				BigDecimal repayAccountWait = borrowRepay.getRepayAccount();
				BigDecimal repayCapitalWait = borrowRepay.getRepayCapital();
				BigDecimal repayInterestWait = borrowRepay.getRepayInterest();
				// 如果是机构垫付还款
				if (isRepayOrgFlag == 1 && isApicronRepayOrgFlag == 1) {
					// 更新垫付机构的Account表的待还款金额
					// 此处为机构垫付临时逻辑
					Account newRepayUserAccount = new Account();
					newRepayUserAccount.setUserId(repayUserAccount.getUserId());
					newRepayUserAccount.setBankTotal(repayAccount.add(manageFee));//add by cwyang 资金总额减少
					newRepayUserAccount.setBankFrost(repayAccount.add(manageFee));
					newRepayUserAccount.setBankAwait(BigDecimal.ZERO);
					newRepayUserAccount.setBankAwaitCapital(BigDecimal.ZERO);
					newRepayUserAccount.setBankWaitRepay(BigDecimal.ZERO);
					newRepayUserAccount.setBankFrostCash(repayAccount.add(manageFee));
					boolean repayUserFlag = this.adminAccountCustomizeMapper.updateOfRepayOrgUser(newRepayUserAccount) > 0 ? true : false;
					if (!repayUserFlag) {
						throw new Exception("垫付机构账户(huiyingdai_account)更新失败！" + "[借款人用户ID：" + borrowUserId + "]");
					}
					Account borrowUserAccount = new Account();
					borrowUserAccount.setUserId(borrowUserId);
					borrowUserAccount.setBankTotal(BigDecimal.ZERO);
					borrowUserAccount.setBankFrost(BigDecimal.ZERO);
					borrowUserAccount.setBankWaitRepay(repayAccountWait.add(manageFee));
					borrowUserAccount.setBankWaitCapital(repayCapitalWait);
					borrowUserAccount.setBankWaitInterest(repayInterestWait);
					borrowUserAccount.setBankWaitRepayOrg(BigDecimal.ZERO);
					borrowUserAccount.setBankFrostCash(BigDecimal.ZERO);
					boolean borrowUserFlag = this.adminAccountCustomizeMapper.updateOfRepayBorrowUser(borrowUserAccount) > 0 ? true : false;
					if (!borrowUserFlag) {
						throw new Exception("借款人账户(huiyingdai_account)更新失败！" + "[借款人用户ID：" + borrowUserId + "]");
					}
				} else {
					Account newRepayUserAccount = new Account();
					newRepayUserAccount.setUserId(repayUserId);
					newRepayUserAccount.setBankTotal(repayAccount.add(manageFee));
					newRepayUserAccount.setBankFrost(repayAccount.add(manageFee));
					newRepayUserAccount.setBankWaitRepay(repayAccountWait.add(manageFee));
					newRepayUserAccount.setBankWaitCapital(repayCapitalWait);
					newRepayUserAccount.setBankWaitInterest(repayInterestWait);
					newRepayUserAccount.setBankWaitRepayOrg(BigDecimal.ZERO);
					newRepayUserAccount.setBankFrostCash(repayAccount.add(manageFee));
					boolean repayUserFlag = this.adminAccountCustomizeMapper.updateOfRepayBorrowUser(newRepayUserAccount) > 0 ? true : false;
					if (!repayUserFlag) {
						throw new Exception("借款人账户(huiyingdai_account)更新失败！" + "[借款人用户ID：" + borrowUserId + "]");
					}
				}
				// 插入还款交易明细
				repayUserAccount = this.getAccountByUserId(repayUserId);
				// 插入借款人的收支明细表(原复审业务)
				AccountList accountList = new AccountList();
				accountList.setBankAwait(repayUserAccount.getBankAwait());
				accountList.setBankAwaitCapital(repayUserAccount.getBankAwaitCapital());
				accountList.setBankAwaitInterest(repayUserAccount.getBankAwaitInterest());
				accountList.setBankBalance(repayUserAccount.getBankBalance());
				accountList.setBankFrost(repayUserAccount.getBankFrost());
				accountList.setBankInterestSum(repayUserAccount.getBankInterestSum());
				accountList.setBankTotal(repayUserAccount.getBankTotal());
				accountList.setBankWaitCapital(repayUserAccount.getBankWaitCapital());
				accountList.setBankWaitInterest(repayUserAccount.getBankWaitInterest());
				accountList.setBankWaitRepay(repayUserAccount.getBankWaitRepay());
				accountList.setAccountId(repayAccountId);
				accountList.setTxDate(txDate);
				accountList.setTxTime(txTime);
				accountList.setSeqNo(seqNo);
				accountList.setBankSeqNo(bankSeqNo);
				accountList.setCheckStatus(1);
				accountList.setTradeStatus(1);
				accountList.setIsBank(1);
				// 非银行相关
				accountList.setNid(apicronNid); // 交易凭证号生成规则BorrowNid_userid_期数
				accountList.setUserId(repayUserId); // 借款人id
				accountList.setAmount(borrowRepay.getRepayAccountYes().add(borrowRepay.getRepayFee())); // 操作金额
				accountList.setType(2); // 收支类型1收入2支出3冻结
				accountList.setTrade("repay_success"); // 交易类型
				accountList.setTradeCode("balance"); // 操作识别码
				accountList.setTotal(repayUserAccount.getTotal()); // 资金总额
				accountList.setBalance(repayUserAccount.getBalance()); // 可用金额
				accountList.setFrost(repayUserAccount.getFrost()); // 冻结金额
				accountList.setAwait(repayUserAccount.getAwait()); // 待收金额
				accountList.setRepay(repayUserAccount.getRepay()); // 待还金额
//				accountList.setCreateTime(nowTime); // 创建时间
				accountList.setOperator(CustomConstants.OPERATOR_AUTO_REPAY); // 操作员
				accountList.setRemark(borrowNid);
				accountList.setIp(""); // 操作IP
//				accountList.setBaseUpdate(0);
				boolean repayAccountListFlag = this.accountListMapper.insertSelective(accountList) > 0 ? true : false;
				if (!repayAccountListFlag) {
					throw new Exception("插入还款人还款款交易明細accountList表失败，项目编号:" + borrowNid + "]");
				}
				// 标的总表信息
				Borrow newBrrow = new Borrow();
				newBrrow.setRepayFullStatus(repayStatus);
				newBrrow.setRepayStatus(CustomConstants.BANK_BATCH_STATUS_SUCCESS);
				newBrrow.setStatus(status);
				BorrowExample borrowExample = new BorrowExample();
				borrowExample.createCriteria().andIdEqualTo(borrowId);
				boolean borrowFlag = this.borrowMapper.updateByExampleSelective(newBrrow, borrowExample) > 0 ? true : false;
				if (!borrowFlag) {
					throw new Exception("不分期还款成功后，更新相应的borrow表失败," + "项目编号:" + borrowNid);
				}
				BorrowApicronExample apicronExample = new BorrowApicronExample();
				apicronExample.createCriteria().andIdEqualTo(apicron.getId()).andStatusEqualTo(apicron.getStatus());
				apicron.setStatus(CustomConstants.BANK_BATCH_STATUS_SUCCESS);
				apicron.setUpdateTime(new Date());
				boolean apicronFlag = this.borrowApicronMapper.updateByExampleSelective(apicron, apicronExample) > 0 ? true : false;
				if (!apicronFlag) {
					throw new Exception("更新还款任务失败。[项目编号：" + borrowNid + "]");
				}
				
                // add by hsy 优惠券还款请求加入到消息队列 start
                Map<String, String> params = new HashMap<String, String>();
                params.put("mqMsgId", GetCode.getRandomCode(10));
                // 借款项目编号
                params.put("borrowNid", borrowNid);
                // 当前期
                params.put("periodNow", String.valueOf(periodNow));
                
                //核对参数
        		try {
        			logger.info("发送优惠券还款队列---" + borrowNid);
					commonProducer.messageSend(new MessageContent(MQConstant.HZT_COUPON_REPAY_TOPIC, UUID.randomUUID().toString(), params));
                } catch (MQException e) {
                    logger.error("计划还款中发生系统", e);
                }
//                rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGES_COUPON, RabbitMQConstants.ROUTINGKEY_COUPONREPAY, JSONObject.toJSONString(params));
                // add by hsy 优惠券还款请求加入到消息队列 end
                
				// insert by zhangjp 增加优惠券还款区分 start
//				CommonSoaUtils.couponRepay(borrowNid, periodNow);
				// insert by zhangjp 增加优惠券还款区分 end
				
				//add by cwyang 变更资产表对应状态
				updatePlanAsset(borrowNid,status);
				//add by cwyang 更新汇计划相关机构的风险准备金
				//判断标的是否分期，并且是否是最后一期
				Integer repayCapitalType = borrow.getRepayCapitalType();
				if("0".equals(repayCapitalType.toString())){
					updateInstitutionData(borrow, borrowInfo);
				}
				try {
					this.sendSmsForManager(borrowNid);
				} catch (Exception e) {
					logger.error("计划还款中发生系统", e);
				}
			} else if (failCount == repayCount) {
				// 更新Borrow
				newBorrow.setStatus(status);
				newBorrow.setRepayStatus(CustomConstants.BANK_BATCH_STATUS_FAIL);
				BorrowExample borrowExample = new BorrowExample();
				borrowExample.createCriteria().andIdEqualTo(borrowId);
				boolean borrowFlag = this.borrowMapper.updateByExampleSelective(newBorrow, borrowExample) > 0 ? true : false;
				if (!borrowFlag) {
					throw new Exception("更新borrow表失败，项目编号:" + borrowNid + "]");
				}
				BorrowApicronExample example = new BorrowApicronExample();
				example.createCriteria().andIdEqualTo(apicron.getId()).andStatusEqualTo(apicron.getStatus());
				apicron.setStatus(CustomConstants.BANK_BATCH_STATUS_FAIL);
				apicron.setUpdateTime(new Date());
				boolean apicronFlag = this.borrowApicronMapper.updateByExampleSelective(apicron, example) > 0 ? true : false;
				if (!apicronFlag) {
					throw new Exception("更新状态为(放款成功)失败，项目编号:" + borrowNid + "]");
				}
			} else {
				// 更新Borrow
				newBorrow.setStatus(status);
				newBorrow.setRepayStatus(CustomConstants.BANK_BATCH_STATUS_PART_FAIL);
				BorrowExample borrowExample = new BorrowExample();
				borrowExample.createCriteria().andIdEqualTo(borrowId);
				boolean borrowFlag = this.borrowMapper.updateByExampleSelective(newBorrow, borrowExample) > 0 ? true : false;
				if (!borrowFlag) {
					throw new Exception("更新borrow表失败，项目编号:" + borrowNid + "]");
				}
				BorrowApicronExample example = new BorrowApicronExample();
				example.createCriteria().andIdEqualTo(apicron.getId()).andStatusEqualTo(apicron.getStatus());
				apicron.setStatus(CustomConstants.BANK_BATCH_STATUS_PART_FAIL);
				apicron.setUpdateTime(new Date());
				boolean apicronFlag = this.borrowApicronMapper.updateByExampleSelective(apicron, example) > 0 ? true : false;
				if (!apicronFlag) {
					throw new Exception("更新状态为(放款成功)失败，项目编号:" + borrowNid + "]");
				}
			}
		}
		logger.info("-----------还款完成，更新状态完成---" + borrowNid + "---------【还款期数】" + periodNow);
		return true;
	}

	private BigDecimal getRepayPlanAccountSum(String borrowNid) {
		BorrowApicronExample apicronExample = new BorrowApicronExample();
		apicronExample.createCriteria().andBorrowNidEqualTo(borrowNid).andIsAllrepayEqualTo(1);
		List<BorrowApicron> apicrons = this.borrowApicronMapper.selectByExample(apicronExample);
		List periodList = new ArrayList();
		for (BorrowApicron apicron: apicrons) {
			periodList.add(apicron.getPeriodNow());
		}
		BorrowRepayPlanExample example = new BorrowRepayPlanExample();
		example.createCriteria().andBorrowNidEqualTo(borrowNid).andRepayPeriodIn(periodList);
		List<BorrowRepayPlan> repayPlans = this.borrowRepayPlanMapper.selectByExample(example);
		BigDecimal sumAccount = new BigDecimal(0);
		if(repayPlans != null && repayPlans.size() >0){
			for (int i = 0; i < repayPlans.size(); i++) {
				sumAccount = sumAccount.add(repayPlans.get(i).getRepayAccountYes().add(repayPlans.get(i).getRepayFee()));
			}
		}
		return sumAccount;
	}

	/**
	 * 判断是否一次性还款都已还款成功
	 * @param apicron
	 * @return
	 */
	private boolean isLastAllRepay(BorrowApicron apicron) {

		String borrowNid = apicron.getBorrowNid();
		BorrowApicronExample examle = new BorrowApicronExample();
		examle.createCriteria().andBorrowNidEqualTo(borrowNid).andApiTypeEqualTo(1).andStatusNotEqualTo(6);
		List<BorrowApicron> borrowApicrons = this.borrowApicronMapper.selectByExample(examle);
		if(borrowApicrons != null && borrowApicrons.size() > 0){
			return  false;
		}
		return true;
	}
    /**
     * 分期保证金回滚
     * @param instCode
     * @param repayCapitalWait
     * @param borrowNid
     */
    private void updateInstitutionDataMonth(String instCode, BigDecimal repayCapitalWait, String borrowNid) {
        //回滚扣减掉的风险准备金
//        String key = RedisConstants.CAPITAL_TOPLIMIT_+instCode;
//        boolean flag = redisAddstrack(key, repayCapitalWait.toString());
//        if (flag) {
			// 更新发标额度余额(增加)和在贷额度（减少） 在数据库更新，防止数据并发 add by cwyang 20180801
			HashMap map = new HashMap();
			map.put("amount",repayCapitalWait);
			map.put("instCode",instCode);
			this.hjhBailConfigCustomizeMapper.updateRepayInstitutionAmount(map);
			logger.info("=================标的分期还款后机构风险准备金增加成功,标的号:" + borrowNid + ",机构编号:" + instCode + ",增加金额:" + repayCapitalWait);
			HjhBailConfigExample bailExample = new HjhBailConfigExample();
			bailExample.createCriteria().andInstCodeEqualTo(instCode);
			List<HjhBailConfig> hjhBailConfigs = this.hjhBailConfigMapper.selectByExample(bailExample);
			if(hjhBailConfigs != null && hjhBailConfigs.size() > 0){
				HjhBailConfig hjhBailConfig = hjhBailConfigs.get(0);
				BigDecimal remainMarkLine = hjhBailConfig.getRemainMarkLine();
				BigDecimal loanBalance = hjhBailConfig.getLoanBalance();
				BigDecimal repayedCapital = hjhBailConfig.getRepayedCapital();
				logger.info("=================标的分期还款后机构风险准备金增加成功,标的号:" + borrowNid + ",机构编号:" + instCode
						+ ",发标额度余额:" + remainMarkLine + ",在贷余额：" + loanBalance + ",已还本金：" + repayedCapital);
			}
//        }
    }

	/**
	 * 整个标的的保证金回滚
	 * @param borrow
	 * @param borrowInfo
	 */
	private void updateInstitutionData(Borrow borrow, BorrowInfo borrowInfo) {
		logger.info("===================cwyang标的还款后机构风险准备金开始回滚,标的号:" + borrow.getBorrowNid());
		//  根据标的号获得机构编号
		//机构为不是汇盈金服的标的才会回滚风险准备金,为了区分散标推入计划
		if (!"10000000".equals(borrowInfo.getInstCode())) {
			String instCode = borrowInfo.getInstCode();
			BigDecimal account = borrow.getAccount();
			//回滚扣减掉的风险准备金
//			String key = RedisConstants.CAPITAL_TOPLIMIT_+instCode;
//			boolean flag = redisAddstrack(key, account.toString());
//			if (flag) {				// 更新发标额度余额和在贷额度 在数据库更新，防止数据并发
				HashMap map = new HashMap();
				map.put("amount",account);
				map.put("instCode",instCode);
				this.hjhBailConfigCustomizeMapper.updateRepayInstitutionAmount(map);
				logger.info("=================标的还款后机构风险准备金增加成功,标的号:" + borrow.getBorrowNid() + ",机构编号:" + instCode + ",增加金额:" + account);
				HjhBailConfigExample bailExample = new HjhBailConfigExample();
				bailExample.createCriteria().andInstCodeEqualTo(instCode);
				List<HjhBailConfig> hjhBailConfigs = this.hjhBailConfigMapper.selectByExample(bailExample);
				if(hjhBailConfigs != null && hjhBailConfigs.size() > 0){
					HjhBailConfig hjhBailConfig = hjhBailConfigs.get(0);
					BigDecimal remainMarkLine = hjhBailConfig.getRemainMarkLine();
					BigDecimal loanBalance = hjhBailConfig.getLoanBalance();
					BigDecimal repayedCapital = hjhBailConfig.getRepayedCapital();
					logger.info("=================标的还款后机构风险准备金增加成功,标的号:" + borrow.getBorrowNid() + ",机构编号:" + instCode
							+ ",发标额度余额:" + remainMarkLine + ",在贷余额：" + loanBalance + ",已还本金：" + repayedCapital);
				}
//			}
		}
		
		
	}
	
	/**
	 * 并发情况下保证设置一个值
	 * @param key
	 * @param value
	 */
	private boolean redisAddstrack(String key,String value){
		JedisPool poolNew = RedisUtils.getPool();
		Jedis jedis = poolNew.getResource();
		boolean result = false;
		
		try {
			while ("OK".equals(jedis.watch(key))) {
				List<Object> results = null;
				
				String balance = jedis.get(key);
				BigDecimal bal = new BigDecimal(balance);
				BigDecimal val = new BigDecimal(value);
				
				Transaction tx = jedis.multi();
				String valbeset = bal.add(val).toString();
				tx.set(key, valbeset);
				results = tx.exec();
				if (results == null || results.isEmpty()) {
					jedis.unwatch();
				} else {
					String ret = (String) results.get(0);
					if (ret != null && ret.equals("OK")) {
						// 成功后
						result = true;
						break;
					} else {
						jedis.unwatch();
					}
				}
			}
		}catch (Exception e){
			logger.info("抛出异常:[{}]",e);
		}finally {
			RedisUtils.returnResource(poolNew,jedis);
		}
		
		return result;
	}


	/**
	 * 变更资产表对应状态
	 * @param borrowNid
	 * @param status
	 */
	private void updatePlanAsset(String borrowNid, int status) {
		int assetStatus = 0;
		//判断还款是否分期
		if (4 == status){//分期还款标的
			assetStatus = 11;//还款中
		}else if(5 == status){
			assetStatus = 12;//还款完成
		}
		HjhPlanAssetExample example = new HjhPlanAssetExample();
		example.createCriteria().andBorrowNidEqualTo(borrowNid);
		List<HjhPlanAsset> planAssetList = this.hjhPlanAssetMapper.selectByExample(example);
		if (planAssetList != null && planAssetList.size() > 0) {
			HjhPlanAsset hjhPlanAsset = planAssetList.get(0);
			hjhPlanAsset.setStatus(assetStatus);//已还款
			int count = this.hjhPlanAssetMapper.updateByPrimaryKey(hjhPlanAsset);
			if (count > 0) {
				logger.info("====================cwyang 变更对应资产表状态完成!资产标的号:" + borrowNid);
			}
		}
	}
	
	
	private void sendSmsForManager(String borrowNid) {
		// 发送成功短信
		Map<String, String> replaceStrs = new HashMap<String, String>();
		replaceStrs.put("val_title", borrowNid);
		replaceStrs.put("val_time", GetDate.formatTime());
		SmsMessage smsMessage =
                new SmsMessage(null, replaceStrs, null, null, MessageConstant.SMS_SEND_FOR_MANAGER, null,
                		CustomConstants.PARAM_TPL_HUANKUAN_SUCCESS, CustomConstants.CHANNEL_TYPE_NORMAL);
        try {
			commonProducer.messageSend(new MessageContent(MQConstant.SMS_CODE_TOPIC, UUID.randomUUID().toString(), smsMessage));
        } catch (MQException e2) {
            logger.error("发送短信失败..", e2);
        }
	}

	/**
	 * 更新相应的还款信息总表
	 * 
	 * @param borrowRecover
	 * @return
	 */
	private boolean updateBorrowRecover(BorrowRecover borrowRecover) {
		boolean flag = this.borrowRecoverMapper.updateByPrimaryKeySelective(borrowRecover) > 0 ? true : false;
		return flag;
	}

	/**
	 * 查询相应的还款详情
	 * 
	 * @param apicron
	 * @return
	 */
	private List<BankCallBean> queryBatchDetails(BorrowApicron apicron) {

		int txCounts = apicron.getTxCounts();// 总交易笔数
		String batchTxDate = String.valueOf(apicron.getTxDate());
		String batchNo = apicron.getBatchNo();// 批次号
		int pageSize = 50;// 每页笔数
		int pageTotal = txCounts / pageSize + 1;// 总页数
		List<BankCallBean> results = new ArrayList<BankCallBean>();
		// 获取共同参数
//		String bankCode = PropUtils.getSystem(BankCallConstant.BANK_BANKCODE);
//		String instCode = PropUtils.getSystem(BankCallConstant.BANK_INSTCODE);
		String channel = BankCallConstant.CHANNEL_PC;
		logger.info(apicron.getBorrowNid()+" 批次明细查询 "+batchNo+"  页数:"+pageTotal+" ,总数: "+txCounts);
		
		for (int i = 1; i <= pageTotal; i++) {
			// 循环三次查询结果
			for (int j = 0; j < 3; j++) {
				String logOrderId = GetOrderIdUtils.getOrderId2(apicron.getUserId());
				String orderDate = GetOrderIdUtils.getOrderDate();
				String txDate = GetOrderIdUtils.getTxDate();
				String txTime = GetOrderIdUtils.getTxTime();
				String seqNo = GetOrderIdUtils.getSeqNo(6);
				BankCallBean repayBean = new BankCallBean();
				repayBean.setVersion(BankCallConstant.VERSION_10);// 接口版本号
				repayBean.setTxCode(BankCallConstant.TXCODE_BATCH_DETAILS_QUERY);// 消息类型(批量还款)
//				repayBean.setInstCode(instCode);// 机构代码
//				repayBean.setBankCode(bankCode);
				repayBean.setTxDate(txDate);
				repayBean.setTxTime(txTime);
				repayBean.setSeqNo(seqNo);
				repayBean.setChannel(channel);
				repayBean.setBatchTxDate(batchTxDate);
				repayBean.setBatchNo(batchNo);
				repayBean.setType(BankCallConstant.DEBT_BATCH_TYPE_ALL);
				repayBean.setPageNum(String.valueOf(i));
				repayBean.setPageSize(String.valueOf(pageSize));
				repayBean.setLogUserId(String.valueOf(apicron.getUserId()));
				repayBean.setLogOrderId(logOrderId);
				repayBean.setLogOrderDate(orderDate);
				repayBean.setLogRemark("查询批次交易明细！");
				repayBean.setLogClient(0);
				// 调用还款接口
				BankCallBean repayResult = BankCallUtils.callApiBg(repayBean);
				if (Validator.isNotNull(repayResult)) {
					String retCode = StringUtils.isNotBlank(repayResult.getRetCode()) ? repayResult.getRetCode() : "";
					logger.info(apicron.getBorrowNid()+" 批次明细查询 "+batchNo+"  第"+i+" 页,返回码: "+retCode);
					if (BankCallConstant.RESPCODE_SUCCESS.equals(retCode)) {
						results.add(repayResult);
						break;
					} else {
						continue;
					}
				} else {
					continue;
				}
			}
		}
		return results;
	}

	@Override
	public BorrowApicron getBorrowApicron(Integer id) {
		BorrowApicron apicron = this.borrowApicronMapper.selectByPrimaryKey(id);
		return apicron;
	}

	/**
	 * 取得还款明细列表
	 *
	 * @return
	 */
	@Override
	public List<BorrowRecover> getBorrowRecoverList(String borrowNid,BorrowApicron apicron) {
		BorrowRecoverExample example = new BorrowRecoverExample();
		BorrowRecoverExample.Criteria criteria = example.createCriteria();
		criteria.andBorrowNidEqualTo(borrowNid);
		// 一次性还款的情况，因为最后一期会更新导致前期更新不到位 by dxj&wanggongxi
		if(apicron.getIsAllrepay().intValue() == 0) {
			criteria.andRecoverStatusNotEqualTo(1); // 0初始 1还款成功 2还款失败
		}
		
		example.setOrderByClause(" id asc ");
		List<BorrowRecover> list = this.borrowRecoverMapper.selectByExample(example);
		return list;
	}

	/**
	 * 取得借款列表
	 *
	 * @return
	 */
	private BorrowTender getBorrowTender(String tenderOrderId) {
		BorrowTenderExample example = new BorrowTenderExample();
		example.createCriteria().andNidEqualTo(tenderOrderId);
		List<BorrowTender> borrowTenders = this.borrowTenderMapper.selectByExample(example);
		if (borrowTenders != null && borrowTenders.size() == 1) {
			return borrowTenders.get(0);
		}
		return null;
	}

	/**
	 * 判断该收支明细是否存在
	 *
	 * @param nid
	 * @return
	 */
	private boolean countAccountListByNid(String nid) {
		AccountListExample accountListExample = new AccountListExample();
		accountListExample.createCriteria().andNidEqualTo(nid).andTradeEqualTo("hjh_repay_balance");
		return this.accountListMapper.countByExample(accountListExample) > 0 ? true : false;
	}

	/**
	 * 判断该收支明细是否存在
	 *
	 * @return
	 */
	private boolean countCreditAccountListByNid(String nid) {
		AccountListExample accountListExample = new AccountListExample();
		accountListExample.createCriteria().andNidEqualTo(nid).andTradeEqualTo("credit_tender_recover_yes");
		return this.accountListMapper.countByExample(accountListExample) > 0 ? true : false;
	}

	/**
	 * 取得总的还款计划表
	 *
	 * @param borrowNid
	 * @param period
	 * @return
	 */
	private BorrowRepayPlan getBorrowRepayPlan(String borrowNid, Integer period) {

		BorrowRepayPlanExample example = new BorrowRepayPlanExample();
		example.createCriteria().andBorrowNidEqualTo(borrowNid).andRepayPeriodEqualTo(period);
		List<BorrowRepayPlan> list = this.borrowRepayPlanMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	

	@Override
	public void updateQuitRepayInfo(String accedeOrderId) {
		HjhAccedeExample accedeExample = new HjhAccedeExample();
		accedeExample.createCriteria().andAccedeOrderIdEqualTo(accedeOrderId);
		List<HjhAccede> accedeList = this.hjhAccedeMapper.selectByExample(accedeExample);
		boolean isLastBorrow = true;
		HjhAccede hjhAccede = null;
		if (accedeList != null && accedeList.size() > 0) {
			hjhAccede = accedeList.get(0);
		}else{
			logger.info("-----------订单号" + accedeOrderId + ",未查询到匹配加入订单!");
			return;
		}
		Integer completeFlag = hjhAccede.getCreditCompleteFlag();//重新获取是否清算完成标识
		if(1 != completeFlag){
			logger.info("-----------订单号" + accedeOrderId + ",未清算完成，暂不退出计划!");
			return;
		}
		boolean isQuit = checkIsRepayQuit(hjhAccede);
		if (!isQuit) {
			logger.info("-----------订单号" + accedeOrderId + ",不符合退出条件,暂不退出计划!");
			return;
		}
		//根据计划订单号判断是否为计划最后一次放款
		BorrowTenderExample example = new BorrowTenderExample();
		example.createCriteria().andAccedeOrderIdEqualTo(accedeOrderId);
		List<BorrowTender> tenderList = this.borrowTenderMapper.selectByExample(example);
		//根据计划订单号获得所有投资标的并且投资总额 = 计划加入总额
		//判断是否除本标的外还存在复审中的标的,如果没有则确定是最后一笔放款
		List<String> borrowList = new ArrayList<>();
		if (tenderList != null && tenderList.size() > 0) {
			for (int i = 0; i < tenderList.size(); i++) {
				BorrowTender borrowTender = tenderList.get(i);
				borrowList.add(borrowTender.getBorrowNid());
			}
		}
		//判断原始投资是否还款或被清算
		for (int i = 0; i < borrowList.size(); i++) {
			String borrowNid = borrowList.get(i);
			BorrowExample borrowExample = new BorrowExample();
			borrowExample.createCriteria().andBorrowNidEqualTo(borrowNid);
			List<Borrow> borrowLists = this.borrowMapper.selectByExample(borrowExample);
			if (borrowLists != null && borrowLists.size() > 0) {
				Borrow borrow = borrowLists.get(0);
				Integer status = borrow.getStatus();//获得标的状态
				if (status < 5) {//存在其他标的未还款
						//判断剩余待清算金额是否全部债转
					boolean result = isDebtCredite(borrowNid,accedeOrderId);
					if (!result) {
						isLastBorrow = false;
					}
					
				}
			}
		}
		//判断订单内债权是否被完全承接
		boolean isEnd = isDebtEnd(accedeOrderId);
		if (!isEnd) {
			isLastBorrow = false;
		}
		if (isLastBorrow) {//最后一次还款,退出计划
			//退出计划更新还款信息表
			HjhRepay hjhRepay = updateHjhLastRepayInfo(hjhAccede);
			//计划相关金额更新
			updatePlanData(hjhAccede,hjhRepay);
			//更新计划账户金额
			updatePlanTenderAccount(hjhAccede,hjhRepay);
//			//发送计划还款短信
//			sendSms(hjhRepay);
//			//发送计划退出通知
//			sendMessage(hjhAccede);
			// 重新获取hjhRepay
			hjhRepay = this.hjhRepayMapper.selectByPrimaryKey(hjhRepay.getId());
			//发送计划还款短信
			sendSms(hjhRepay);
			//发送计划退出通知
			sendMessage(hjhRepay);

			try {
				couponRepay(hjhAccede);
			} catch (Exception e) {
				logger.error("计划还款中发生系统", e);
				logger.info("============优惠券还款请求失败");
			}
		}
			
	}
	

	/**
	 * 订单内债权是否全部退出
	 * @param accedeOrderId
	 * @return
	 */
	private boolean isDebtEnd(String accedeOrderId) {
		
		HjhDebtDetailExample example = new HjhDebtDetailExample();
		example.createCriteria().andPlanOrderIdEqualTo(accedeOrderId);
		List<HjhDebtDetail> detailList = this.hjhDebtDetailMapper.selectByExample(example );
		if (detailList != null && detailList.size() > 0) {
			for (int i = 0; i < detailList.size(); i++) {
				Integer delFlag = detailList.get(i).getDelFlag();
				String orderId = detailList.get(i).getOrderId();
				if (0 == delFlag) {//存在未清算的债权
					logger.info("--------------订单号:" + accedeOrderId + ",存在未清算的债权,债权订单号:" + orderId);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 判断剩余待清算金额是否全部债转
	 * @param borrowNid
	 * @param accedeOrderId
	 * @return
	 */
	private boolean isDebtCredite(String borrowNid, String accedeOrderId) {
		 
		HjhDebtCreditExample example = new HjhDebtCreditExample();
		example.createCriteria().andPlanOrderIdEqualTo(accedeOrderId).andBorrowNidEqualTo(borrowNid).andCreditStatusNotEqualTo(3);
		List<HjhDebtCredit> debtCreditList = this.hjhDebtCreditMapper.selectByExample(example);
		if (debtCreditList != null && debtCreditList.size() > 0) {
			HjhDebtCredit hjhDebtCredit = debtCreditList.get(0);
			Integer creditStatus = hjhDebtCredit.getCreditStatus();
			if (2 == creditStatus) {//全部承接
				return true;
			}
		}
		return false;
	}


	/**
	 * 校验计划订单是否符合退出计划的条件
	 * @param hjhAccede
	 * @return
	 */
	private boolean checkIsRepayQuit(HjhAccede hjhAccede) {
		
		//判断是否到清算时间
		Date endDate = hjhAccede.getEndDate();
		long endTime = endDate.getTime();
		String accedeOrderId = hjhAccede.getAccedeOrderId();
		Date nowDate = new Date();
		long nowTime = nowDate.getTime();
		if (nowTime < endTime) {
			logger.info("-------------订单号:" + accedeOrderId + "未到清算时间!");
			return false;
		}
		
		//判断是否存在债转未承接的项目
		HjhDebtCreditExample example = new HjhDebtCreditExample();
		example.createCriteria().andPlanOrderIdEqualTo(accedeOrderId);
		List<HjhDebtCredit> debtCreditList = this.hjhDebtCreditMapper.selectByExample(example);
		if (debtCreditList != null && debtCreditList.size() > 0) {
			for (int i = 0; i < debtCreditList.size(); i++) {
				HjhDebtCredit debtCredit = debtCreditList.get(i);
				String borrowNid = debtCredit.getBorrowNid();
				boolean flag = isBorrowRepay(borrowNid);
				if (flag) {
					return false;
				}
				Integer creditStatus = debtCredit.getCreditStatus();
				if (1 == creditStatus || 0 == creditStatus) {//存在债转未被全部承接
					logger.info("-------------订单号:" + accedeOrderId + ",存在未被全部承接的债转!,债转编号:" + debtCredit.getCreditNid());
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 判断标的是否还款
	 * @param borrowNid
	 * @return
	 */
	private boolean isBorrowRepay(String borrowNid) {
		
		BorrowApicronExample example = new BorrowApicronExample();
		example.createCriteria().andBorrowNidEqualTo(borrowNid).andApiTypeEqualTo(1).andStatusNotEqualTo(6);
		List<BorrowApicron> list = this.borrowApicronMapper.selectByExample(example);
		if (list!= null && list.size() > 0) {
			return true;
		}
		return false;
	}

	@Override
	public void updateLockRepayInfo(String accedeOrderId) {
		//判断是否为计划最后一次复审
		//根据计划订单号判断是否为计划最后一次放款
		BorrowTenderExample example = new BorrowTenderExample();
		// 汇计划二期 复投的标的不进行进入锁定期操作 
		example.createCriteria().andAccedeOrderIdEqualTo(accedeOrderId).andTenderTypeEqualTo(0);
		List<BorrowTender> tenderList = this.borrowTenderMapper.selectByExample(example);
			//根据计划订单号获得所有投资标的并且投资总额 = 计划加入总额
				//判断是否除本标的外还存在复审中的标的,如果没有则确定是最后一笔放款
		List<String> borrowList = new ArrayList<>();
		if (tenderList != null && tenderList.size() > 0) {
			for (int i = 0; i < tenderList.size(); i++) {
				borrowList.add(tenderList.get(i).getBorrowNid());
			}
		}
		HjhAccedeExample accedeExample = new HjhAccedeExample();
		accedeExample.createCriteria().andAccedeOrderIdEqualTo(accedeOrderId);
		List<HjhAccede> accedeList = this.hjhAccedeMapper.selectByExample(accedeExample);
		boolean isLastBorrow = true;
		HjhAccede hjhAccede = null;
		//汇计划最小投资额
		BigDecimal tenderMin = CustomConstants.HJH_RETENDER_MIN_ACCOUNT;
		if (accedeList != null && accedeList.size() > 0) {
			hjhAccede = accedeList.get(0);
			logger.info("==============cwyang 订单号:" + hjhAccede.getAccedeOrderId() + "的计划金额为:" + hjhAccede.getAccedeAccount());
			logger.info("==============cwyang 订单号:" + hjhAccede.getAccedeOrderId() + "的已投金额为:" + hjhAccede.getAlreadyInvest());
			if (hjhAccede.getAccedeAccount().compareTo(hjhAccede.getAlreadyInvest()) > 0) {//计划未投满
				isLastBorrow = false;
				logger.info("=================cwyang 订单号:" + hjhAccede.getAccedeOrderId() + "计划未投满!");
				// 获得计划可用金额 如果计划可用金额小于一个阀值的话,通过可用余额+已投金额如果 = 加入金额的话则计划加入金额用光,可以进入计划锁定
				BigDecimal availableInvestAccount = hjhAccede.getAvailableInvestAccount();//订单可用金额
				if (availableInvestAccount.compareTo(tenderMin) < 0) {
					if (availableInvestAccount.add(hjhAccede.getAlreadyInvest()).compareTo(hjhAccede.getAccedeAccount()) == 0) {
						isLastBorrow = true;
						logger.info("=================cwyang 订单号:" + hjhAccede.getAccedeOrderId() + "可用金额小于阀值,可以准备进入锁定期!");
					}
				}
			}
			for (int i = 0; i < borrowList.size(); i++) {
				String borrowNid = borrowList.get(i);
				Borrow borrow = this.getBorrow(borrowNid);
				Integer status = borrow.getStatus();
				if (4 > status) {
					isLastBorrow = false;
					logger.info("=================cwyang 订单号:" + hjhAccede.getAccedeOrderId() + "投资标的未放款，标的号："+ borrowNid );

				}
			}
		}
		if (isLastBorrow) {//是最后一次放款复审,订单进入锁定期
			logger.info("-----------订单:" + hjhAccede.getAccedeOrderId() + ",进入锁定期!");
			// 更新预期收益
			updateHjhAccedeInterest(hjhAccede);
			//获得变更数据后的订单信息
			hjhAccede = getNewAccede(hjhAccede);
			//更新加入明细表的相关时间
			updateAccedeDate(hjhAccede);
			//更新还款信息表
			updateHjhPlanRepayInfo(hjhAccede);
			//推送消息
			loanSendMessage(hjhAccede);
			//汇计划二期修改清算时间 
			updateEndDate(hjhAccede);
			// 更新资金明细   Account+AccountList
			updateUserAccount(hjhAccede);
			//推送消息
			sendHjhLockMessage(hjhAccede);
			//发送短信
			sendSms(hjhAccede);
			try {
				//开始计算提成 add by cwyang 2018-5-24 汇计划3期由batch工程挪至此处处理
				commisionCompute(hjhAccede);
			}catch (Exception e){
				logger.error("计划还款中发生系统", e);
				logger.info("=================提成发放失败,计划加入订单号:" + hjhAccede.getAccedeOrderId());
			}
			try {
				//生成并签署加入计划投资服务协议 add by cwyang 2018-2-26
				sendPlanContract(hjhAccede.getUserId(),hjhAccede.getAccedeOrderId(),hjhAccede.getQuitTime(),hjhAccede.getCountInterestTime(),hjhAccede.getWaitTotal());
				logger.info("=================计划进入锁定期优惠券放款开始=，订单号："+hjhAccede.getAccedeOrderId()+"=============");
				//优惠券放款
				couponLoan(hjhAccede);
				//双十二活动
				logger.info("=================计划进入锁定期优惠券放款完毕=，订单号："+hjhAccede.getAccedeOrderId()+"=============");
//				actBalloonTender(hjhAccede);
			} catch (Exception e) {
				logger.info("=================优惠券放款失败,投资订单号:" + hjhAccede.getAccedeOrderId());
			}
		}
	}

	/**
	 * 开始计算订单提成
	 * @param hjhAccede
	 */
	private void commisionCompute(HjhAccede hjhAccede) {
		logger.info("----------开始生成计划订单的提成计算，订单号：" + hjhAccede.getAccedeOrderId());
		//TODO: 应该是三期的提成服务
//		commisionComputeService.commisionCompute(hjhAccede);
		logger.info("----------生成计划订单的提成计算完毕，订单号：" + hjhAccede.getAccedeOrderId());

	}
	/**
	 * 获得优先处理的还款队列
	 * @param borrowNid
	 * @return
	 */
	@Override
	public BorrowApicron getRepayPeriodSort(String borrowNid) {
		BorrowApicronExample example = new BorrowApicronExample();
		example.createCriteria().andBorrowNidEqualTo(borrowNid).andApiTypeEqualTo(1).andStatusNotEqualTo(6);
		example.setOrderByClause("period_now");
		List<BorrowApicron> apicronList = this.borrowApicronMapper.selectByExample(example);
		if (apicronList != null && apicronList.size() > 0){
			BorrowApicron borrowApicron = apicronList.get(0);
			return borrowApicron;
		}
		return null;
	}


	/**
	 *  生成并签署计划加入协议
	 * @param userId
	 * @param accedeOrderId
	 * @param waitTotal
	 */
	private void sendPlanContract(Integer userId, String accedeOrderId, Integer quitTime, Integer countInterestTime, BigDecimal waitTotal) {

		logger.info("-------------加入订单号:" + accedeOrderId + ",开始生成计划加入协议！----------");
		try {
			FddGenerateContractBean bean = new FddGenerateContractBean();
			bean.setOrdid(accedeOrderId);
			bean.setTransType(FddGenerateContractConstant.PROTOCOL_TYPE_PLAN);
			bean.setTenderUserId(userId);
			bean.setSignDate(GetDate.getDataString(GetDate.date_sdf));//签署日期
			bean.setPlanStartDate(GetDate.getDateMyTimeInMillis(countInterestTime));
			bean.setPlanEndDate(GetDate.getDateMyTimeInMillis(quitTime));
			bean.setTenderInterestFmt(waitTotal.toString());
			
			//协议确认队列
//			rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGES_NAME, RabbitMQConstants.ROUTINGKEY_GENERATE_CONTRACT, JSONObject.toJSONString(bean));

			commonProducer.messageSend(new MessageContent(MQConstant.FDD_TOPIC,MQConstant.FDD_GENERATE_CONTRACT_TAG, UUID.randomUUID().toString(), bean));
			
		}catch (Exception e){
			logger.error("计划还款中发生系统", e);
			logger.info("-------------userid:" + userId + ",生成计划加入协议失败！----------");

		}
	}

	/**
	 *  计划锁定推送消息
	 * @param hjhAccede
	 */
	private void sendHjhLockMessage(HjhAccede hjhAccede) {
		int userId = hjhAccede.getUserId();
		BigDecimal amount = hjhAccede.getAccedeAccount();
		BigDecimal waitInterest = hjhAccede.getWaitInterest();
		Date endDate = hjhAccede.getEndDate();
		Date countDate = GetDate.countDate(endDate, 5, 3);//最迟退出时间
		String endDateStr = GetDate.dateToString2(countDate);
		String planNid = hjhAccede.getPlanNid();
		HjhPlanExample example = new HjhPlanExample();
		example.createCriteria().andPlanNidEqualTo(planNid);
		List<HjhPlan> planList = this.hjhPlanMapper.selectByExample(example);
		HjhPlan hjhPlan = planList.get(0);
		String planName = hjhPlan.getPlanName();
		Map<String, String> msg = new HashMap<String, String>();
		msg.put(VAL_AMOUNT, amount.toString());// 待收金额
		msg.put(VAL_USERID, String.valueOf(userId));
		msg.put(VAL_HJH_TITLE, planName);
		msg.put(VAL_INTEREST, waitInterest.toString());
		msg.put(VAL_DATE, endDateStr);
		
		if (Validator.isNotNull(msg.get(VAL_USERID)) && Validator.isNotNull(msg.get(VAL_AMOUNT)) && new BigDecimal(msg.get(VAL_AMOUNT)).compareTo(BigDecimal.ZERO) > 0) {
			//发送app消息队列，需要根据userid取真实用户
			AppMsMessage smsMessage = new AppMsMessage(Integer.valueOf(msg.get(VAL_USERID)), msg, null, MessageConstant.APP_MS_SEND_FOR_USER, CustomConstants.JYTZ_PLAN_LOCK_SUCCESS);
			try {
				commonProducer.messageSend(new MessageContent(MQConstant.APP_MESSAGE_TOPIC, String.valueOf(userId),
						smsMessage));
			} catch (MQException e) {
				logger.error("发送app消息失败..", e);
			}
		}
		
	}

	/**
	 * 获得更新后的计划订单信息
	 * @param hjhAccede
	 * @return
	 */
	private HjhAccede getNewAccede(HjhAccede hjhAccede) {
		
		HjhAccedeExample example = new HjhAccedeExample();
		example.createCriteria().andIdEqualTo(hjhAccede.getId());
		List<HjhAccede> accedeList = this.hjhAccedeMapper.selectByExample(example );
		return accedeList.get(0);
	}

	/**
	 * 更新还款信息表
	 * @param hjhAccede
	 */
	private void updateHjhPlanRepayInfo(HjhAccede hjhAccede) {
		
//		BigDecimal planWaitCaptical = new BigDecimal(0);
//		BigDecimal planWaitInterest = new BigDecimal(0);
//		BigDecimal waitTotal = new BigDecimal(0);
		BigDecimal serviceFee = new BigDecimal(0);
		String accedeOrderId = hjhAccede.getAccedeOrderId();//计划加入订单号
		logger.info("=============cwyang 开始更新汇计划相关还款信息,加入订单号:" + accedeOrderId);
		BorrowRecoverExample example = new BorrowRecoverExample();
		example.createCriteria().andAccedeOrderIdEqualTo(accedeOrderId);
		List<BorrowRecover> borrowRecoverList = this.borrowRecoverMapper.selectByExample(example);
		
		if (borrowRecoverList != null && borrowRecoverList.size() > 0) {
			for (int i = 0; i < borrowRecoverList.size(); i++) {
				BorrowRecover borrowRecover = borrowRecoverList.get(i);
//				planWaitCaptical = planWaitCaptical.add(borrowRecover.getRecoverCapital());
//				planWaitInterest = planWaitInterest.add(borrowRecover.getRecoverInterest());
//				waitTotal = waitTotal.add(borrowRecover.getRecoverAccount());
				logger.info("=============cwyang 开始更新汇计划相关还款信息,加入订单号:" + accedeOrderId + ",第" + i + "次投资金额: " + borrowRecover.getRecoverAccount());
				serviceFee = serviceFee.add(borrowRecover.getRecoverServiceFee());
			}
		}
		logger.info("=============cwyang 开始更新汇计划相关还款信息,加入订单号:" + accedeOrderId + ",待还款金额: " + hjhAccede.getShouldPayTotal());
		HjhRepay repay = new HjhRepay();
		repay.setAccedeOrderId(accedeOrderId);
		repay.setPlanNid(hjhAccede.getPlanNid());
		repay.setLockPeriod(hjhAccede.getLockPeriod());
		repay.setUserId(hjhAccede.getUserId());
		repay.setUserName(hjhAccede.getUserName());
		repay.setUserAttribute(hjhAccede.getUserAttribute());
		repay.setAccedeAccount(hjhAccede.getAccedeAccount());
		repay.setRepayInterest(hjhAccede.getShouldPayInterest());
		repay.setRepayCapital(hjhAccede.getShouldPayCapital());
		repay.setRepayStatus(0);//未回款
		repay.setRepayWait(hjhAccede.getShouldPayTotal()); //待回款
		repay.setRepayAlready(new BigDecimal(0));
		repay.setPlanRepayCapital(new BigDecimal(0));
		repay.setPlanRepayInterest(new BigDecimal(0));
		repay.setRepayTotal(new BigDecimal(0));
		repay.setRepayShould(hjhAccede.getShouldPayTotal());//应还金额
		repay.setOrderStatus(3);
		repay.setRepayShouldTime(hjhAccede.getQuitTime());//计划应还时间
		repay.setPlanWaitCaptical(hjhAccede.getShouldPayCapital());//待还本金
		repay.setPlanWaitInterest(hjhAccede.getShouldPayInterest());//待还利息
		repay.setWaitTotal(hjhAccede.getShouldPayTotal());//待还总额
		repay.setServiceFee(serviceFee);//服务费
//		repay.setCreateTime(GetDate.getNowTime10());
		repay.setCreateUser(hjhAccede.getUserId());
		HjhRepayExample repayExample = new HjhRepayExample();
		repayExample.createCriteria().andAccedeOrderIdEqualTo(accedeOrderId);
		List<HjhRepay> repayList = hjhRepayMapper.selectByExample(repayExample);
		if (repayList != null && repayList.size() > 0) {
			logger.info("===============cwyang 汇计划还款信息表已插入!");
		}else{
			int count = this.hjhRepayMapper.insertSelective(repay);
			if (count > 0) {
				logger.info("============cwyang 汇计划还款信息表插入完成!计划加入订单号:" + accedeOrderId);
			}
		}
//		hjhAccede.setWaitTotal(waitTotal);
	}
	
	private void couponLoan(HjhAccede hjhAccede) {
		// add by cwyang 优惠券放款请求加入到消息队列 start
        Map<String, String> params = new HashMap<String, String>();
        params.put("mqMsgId", GetCode.getRandomCode(10));
        // 借款项目编号
        params.put("orderId", hjhAccede.getAccedeOrderId());
        
        //TODO: 优惠券还款 ,为什么会跟放款有关系
		try {
			logger.info("发送计划优惠券放款---" + hjhAccede.getAccedeOrderId());
			commonProducer.messageSend(new MessageContent(MQConstant.HJH_COUPON_LOAN_TOPIC, UUID.randomUUID().toString(), params));
        } catch (MQException e) {
            logger.error("计划还款中发生系统", e);
        }
//        rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGES_NAME, RabbitMQConstants.ROUTINGKEY_COUPONLOANS_HJH, JSONObject.toJSONString(params));
        // add by cwyang 优惠券放款请求加入到消息队列 end
	}
	
	/**
	 * 双十二活动发送mq任务
	 */
//	private void actBalloonTender(HjhAccede hjhAccede) {
//		if(checkActivityStatus(actId) == 3){
//			Map<String, String> params = new HashMap<String, String>();
//			params.put("mqMsgId", GetCode.getRandomCode(10));
//			// 借款项目编号
//			params.put("tenderNid", hjhAccede.getAccedeOrderId());
//			params.put("tenderType", "1");
//			rabbitTemplate.convertAndSend(RabbitMQConstants.EXCHANGES_NAME, RabbitMQConstants.ROUTINGKEY_ACT_BALLOON, JSONObject.toJSONString(params));
//			logger.info("【双十二活动】发送mq任务，消息内容：" + JSONObject.toJSONString(params));
//		}else{
//			logger.info("当前双十二活动为处于运行中状态 actId:" + actId);
//		}
//	}


	
	/**
	 * 更新加入明细表的相关时间
	 * @param hjhAccede
	 */
	private void updateAccedeDate(HjhAccede hjhAccede) {

		int nowTime = GetDate.getNowTime10();
		//锁定期
		int lockTime = 0;
		int lastPaymentTime = 0;
		String planNid = hjhAccede.getPlanNid();
		HjhPlanExample example = new HjhPlanExample();
		example.createCriteria().andPlanNidEqualTo(planNid);
		List<HjhPlan> planList = this.hjhPlanMapper.selectByExample(example);
		if (planList != null && planList.size() > 0) {
			HjhPlan hjhPlan = planList.get(0);
			if ("endday".equals(hjhPlan.getBorrowStyle())) {//锁定期为日
				lockTime = GetDate.getDayStart10(GetDate.countDate(new Date() ,5,hjhPlan.getLockPeriod()-1));
				lastPaymentTime = GetDate.getDayStart10(GetDate.countDate(new Date() ,5,hjhPlan.getLockPeriod()+2));
			}else{
				lockTime = GetDate.getDayStart10(GetDate.countDate(new Date() ,2,hjhPlan.getLockPeriod()));
				lastPaymentTime = GetDate.getDayStart10(GetDate.countDate(GetDate.countDate(new Date() ,2,hjhPlan.getLockPeriod()), 5, +3));
			}
		}
		hjhAccede.setQuitTime(lockTime);
		hjhAccede.setLastPaymentTime(lastPaymentTime);
		hjhAccede.setOrderStatus(3);//锁定中
		hjhAccede.setCountInterestTime(nowTime);
		int count = this.hjhAccedeMapper.updateByPrimaryKey(hjhAccede);
		if (count > 0) {
			logger.info("============cwyang 更新退出时间和最后还款时间成功,计划加入订单号:" + hjhAccede.getAccedeOrderId());
		}
	}
	/**
	 * 推送消息
	 * 
	 * @param hjhAccede
	 * @author Administrator
	 */
	private void loanSendMessage(HjhAccede hjhAccede) {
		int userId = hjhAccede.getUserId();
		BigDecimal amount = hjhAccede.getWaitTotal();
		Map<String, String> msg = new HashMap<String, String>();
		msg.put(VAL_AMOUNT, amount.toString());// 待收金额
		msg.put(VAL_USERID, String.valueOf(userId));
		if (Validator.isNotNull(msg.get(VAL_USERID)) && Validator.isNotNull(msg.get(VAL_AMOUNT)) && new BigDecimal(msg.get(VAL_AMOUNT)).compareTo(BigDecimal.ZERO) > 0) {
			
			//发送app消息队列，需要根据userid取真实用户
			AppMsMessage smsMessage = new AppMsMessage(Integer.valueOf(msg.get(VAL_USERID)), msg, null, MessageConstant.APP_MS_SEND_FOR_USER, CustomConstants.JYTZ_PLAN_TOUZI_SUCCESS);
			try {
				commonProducer.messageSend(new MessageContent(MQConstant.APP_MESSAGE_TOPIC, String.valueOf(userId),
						smsMessage));
			} catch (MQException e) {
				logger.error("发送app消息失败..", e);
			}
		}
		
	}
	
	/**
	 *  发送短信(计划投资成功)
	 *
	 * @param hjhAccede
	 */
	private void sendSms(HjhAccede hjhAccede) {
		int userId = hjhAccede.getUserId();
		String planNid = hjhAccede.getPlanNid();
		BigDecimal waitInterest = hjhAccede.getWaitInterest();
		Date endDate = hjhAccede.getEndDate();
		Date countDate = GetDate.countDate(endDate, 5, 3);//最迟退出时间
		String endDateStr = GetDate.dateToString2(countDate);
		HjhPlanExample example = new HjhPlanExample();
		example.createCriteria().andPlanNidEqualTo(planNid);
		List<HjhPlan> planList = this.hjhPlanMapper.selectByExample(example);
		HjhPlan hjhPlan = planList.get(0);
		String planName = hjhPlan.getPlanName();
		BigDecimal waitTotal = hjhAccede.getAccedeAccount();
		Map<String, String> msg = new HashMap<String, String>();
		msg.put(VAL_AMOUNT, waitTotal.toString());
		msg.put(VAL_USERID, String.valueOf(userId));
		msg.put(VAL_HJH_TITLE, planName);
		msg.put(VAL_INTEREST, waitInterest.toString());
		msg.put(VAL_DATE, endDateStr);
		
		logger.info("userid=" + msg.get(VAL_USERID) + ";开始发送短信,待收金额" + msg.get(VAL_AMOUNT));
		SmsMessage smsMessage = new SmsMessage(Integer.valueOf(msg.get(VAL_USERID)), msg, null, null, MessageConstant.SMS_SEND_FOR_USER, null, CustomConstants.PARAM_TPL_TOUZI_HJH_SUCCESS,
				CustomConstants.CHANNEL_TYPE_NORMAL);
		try {
			commonProducer.messageSend(new MessageContent(MQConstant.SMS_CODE_TOPIC, String.valueOf(userId), smsMessage));
		} catch (MQException e2) {
			logger.error("发送短信失败..", e2);
		}
	}
	
	/**
	 * 
	 * 修改清算时间
	 * @author sunss
	 * @param hjhAccede
	 */
	private void updateEndDate(HjhAccede hjhAccede) {
	    logger.info("============ 开始更新清算时间,计划加入订单号:" + hjhAccede.getAccedeOrderId());
	    // 汇计划二期
        Date end_date = null;
        //锁定期
        String planNid = hjhAccede.getPlanNid();
        HjhPlanExample example = new HjhPlanExample();
        example.createCriteria().andPlanNidEqualTo(planNid);
        List<HjhPlan> planList = this.hjhPlanMapper.selectByExample(example);
        if (planList != null && planList.size() > 0) {
            HjhPlan hjhPlan = planList.get(0);
            if (hjhPlan.getIsMonth()-0==0) {//锁定期为日
                // 清算日期 10.1加入  7天计划 存的是10.7  清算日期为10.7  8  9
                end_date = GetDate.countDate(new Date(), 5,(hjhPlan.getLockPeriod()-1));
            }else{
                // 清算日期  2.1加入的  1个月计划  存的是3.1   清算日期是3.1  3.2  3.3
                end_date = GetDate.countDate(new Date(), 2,hjhPlan.getLockPeriod());
            }
        }
        hjhAccede.setEndDate(end_date);
        int count = this.hjhAccedeMapper.updateByPrimaryKey(hjhAccede);
        if (count > 0) {
            logger.info("============ 更新清算时间成功,计划加入订单号:" + hjhAccede.getAccedeOrderId());
        }else{
            logger.info("============ 更新清算时间失败,计划加入订单号:" + hjhAccede.getAccedeOrderId());
        }
    }
	
	// 更新资金明细   Account+AccountList 
	private void updateUserAccount(HjhAccede hjhAccede) {
	    logger.info("============开始更新资金明细WaitInterest:["+hjhAccede.getWaitInterest()+"]，[投资人ID：" + hjhAccede.getUserId() + "]，" + "[投资订单号：" + hjhAccede.getAccedeOrderId() + "]");
	    Account accountTender = new Account();
        accountTender.setUserId(hjhAccede.getUserId());
        accountTender.setBankTotal(hjhAccede.getWaitInterest());
        accountTender.setPlanAccountWait(hjhAccede.getWaitTotal());
        accountTender.setPlanInterestWait(hjhAccede.getWaitInterest());
        accountTender.setPlanCapitalWait(hjhAccede.getWaitCaptical());
        boolean investaccountFlag = this.adminAccountCustomizeMapper.updateBankTotalForLockPlan(accountTender) > 0 ? true : false;
        if (!investaccountFlag) {
            throw new RuntimeException("投资人资金记录(huiyingdai_account)更新失败!" + "[投资订单号：" + hjhAccede.getAccedeOrderId() + "]");
        }
        // 取得账户信息(投资人)
        accountTender = this.getAccountByUserId(hjhAccede.getUserId());
        if (Validator.isNull(accountTender)) {
            throw new RuntimeException("投资人账户信息不存在。[投资人ID：" + hjhAccede.getUserId() + "]，" + "[投资订单号：" + hjhAccede.getAccedeOrderId() + "]");
        }
        Account tenderOpenAccount = this.getAccount(hjhAccede.getUserId());
        String txDate = GetOrderIdUtils.getTxDate();
        String txTime = GetOrderIdUtils.getTxTime();
        int nowTime = GetDate.getNowTime10();
        // 写入收支明细
        AccountList accountList = new AccountList();
        /** 投资人银行相关 */
        accountList.setBankAwait(accountTender.getBankAwait());
        accountList.setBankAwaitCapital(accountTender.getBankAwaitCapital());
        accountList.setBankAwaitInterest(accountTender.getBankAwaitInterest());
        accountList.setBankBalance(accountTender.getBankBalance());
        accountList.setBankFrost(accountTender.getBankFrost());
        accountList.setBankInvestSum(accountTender.getBankInvestSum());
        accountList.setBankTotal(accountTender.getBankTotal());
        accountList.setBankWaitCapital(accountTender.getBankWaitCapital());
        accountList.setBankWaitInterest(accountTender.getBankWaitInterest());
        accountList.setBankWaitRepay(accountTender.getBankWaitRepay());
        accountList.setAccountId(tenderOpenAccount.getAccountId());
        accountList.setCheckStatus(0);
        accountList.setTradeStatus(1);
        accountList.setIsBank(1);
        accountList.setTxDate(Integer.parseInt(txDate));
        accountList.setTxTime(Integer.parseInt(txTime));
        /** 投资人非银行相关 */
        accountList.setNid(hjhAccede.getAccedeOrderId()); // 投资订单号
        accountList.setUserId(hjhAccede.getUserId()); // 投资人
        //accountList.setAmount(tenderAccount); // 投资本金
        accountList.setType(1); // 收支类型1收入2支出3冻结
        accountList.setTrade("hjh_lock"); // 投资成功
        accountList.setTradeCode("balance"); // 余额操作
        accountList.setTotal(accountTender.getTotal()); // 投资人资金总额
        accountList.setBalance(accountTender.getBalance()); // 投资人可用金额
        accountList.setFrost(accountTender.getFrost()); // 投资人冻结金额
        accountList.setAwait(accountTender.getAwait()); // 投资人待收金额
//        accountList.setCreateTime(nowTime); // 创建时间
//        accountList.setBaseUpdate(nowTime); // 更新时间
        accountList.setOperator(CustomConstants.OPERATOR_AUTO_LOANS); // 操作者
        accountList.setRemark(hjhAccede.getAccedeOrderId());
//        accountList.setIsUpdate(0);
//        accountList.setBaseUpdate(0);
//        accountList.setInterest(BigDecimal.ZERO); // 利息
        accountList.setWeb(0); // PC
        accountList.setPlanFrost(accountTender.getPlanFrost());
        accountList.setPlanBalance(accountTender.getPlanBalance());
        accountList.setIsShow(1);
        boolean tenderAccountListFlag = this.accountListMapper.insertSelective(accountList) > 0 ? true : false;
        if (!tenderAccountListFlag) {
            throw new RuntimeException("投资人收支明细(huiyingdai_account_list)写入失败!" + "[投资订单号：" + hjhAccede.getAccedeOrderId() + "]");
        }
        logger.info("============结束更新资金明细bank_total:["+accountTender.getBankTotal()+"]，[投资人ID：" + hjhAccede.getUserId() + "]，" + "[投资订单号：" + hjhAccede.getAccedeOrderId() + "]");
    }
	
	 // 计算预期收益
    private BigDecimal updateHjhAccedeInterest(HjhAccede hjhAccede) {
        // 计算需要的参数
        BigDecimal account = hjhAccede.getAccedeAccount(); // 计划加入金额
        Integer borrowPeriod = hjhAccede.getLockPeriod(); // 锁定期
        BigDecimal apr = BigDecimal.ZERO; // 年利率
        // 返回值
        BigDecimal interest = BigDecimal.ZERO;

        String planNid = hjhAccede.getPlanNid();
        HjhPlanExample example = new HjhPlanExample();
        example.createCriteria().andPlanNidEqualTo(planNid);
        List<HjhPlan> planList = this.hjhPlanMapper.selectByExample(example);
        if (planList != null && planList.size() > 0) {
            HjhPlan hjhPlan = planList.get(0);
            apr = hjhPlan.getExpectApr().divide(new BigDecimal(100));
            if (hjhPlan.getIsMonth() - 0 == 0) {// 锁定期为日
                interest = account.multiply(apr).multiply(new BigDecimal(borrowPeriod)).divide(new BigDecimal(360), 16, BigDecimal.ROUND_DOWN);

            } else {
                interest = account.multiply(apr).multiply(new BigDecimal(borrowPeriod)).divide(new BigDecimal(12), 16, BigDecimal.ROUND_DOWN);
            }
        }
        interest = interest.setScale(2, BigDecimal.ROUND_DOWN);
        
        hjhAccede.setWaitInterest(interest); // 待收收益
        hjhAccede.setWaitCaptical(account); // 待收本金
        hjhAccede.setWaitTotal(interest.add(account)); // 待收总额
        boolean tenderFlag = this.batchHjhAccedeCustomizeMapper.updateInterest(hjhAccede) > 0 ? true : false;
        if (!tenderFlag) {
            throw new RuntimeException("更新(hyjf_hjh_accede)写入失败!" + "[投资订单号：" + hjhAccede.getAccedeOrderId() + "]");
        }
        logger.info("============更新(hyjf_hjh_accede)待收收益，待收本金，待收总额成功:interest：["+interest+"]，account：["+account+"]   WaitTotal：["+interest.add(account)+"][投资人ID：" + hjhAccede.getUserId() + "]，" + "[投资订单号：" + hjhAccede.getAccedeOrderId() + "]");
        return interest;
    }

	/**
	 * 取得还款信息
	 *
	 * @return
	 */
	private BorrowRepay getBorrowRepayAsc(String borrowNid, BorrowApicron apicron) {

		BorrowRepayExample example = new BorrowRepayExample();
		BorrowRepayExample.Criteria criteria = example.createCriteria();
		criteria.andBorrowNidEqualTo(borrowNid);

		// 一次性还款的情况，因为最后一期会更新导致前期更新不到位 by dxj&wanggongxi
		if(apicron.getIsAllrepay().intValue() == 0) {
			criteria.andRepayStatusEqualTo(0); // 0初始 1还款成功 2还款失败
		}
		
		example.setOrderByClause(" id asc ");
		List<BorrowRepay> list = this.borrowRepayMapper.selectByExample(example);
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;
	}

	/**
	 * 批次代偿（合规要求）
	 * @author wgx
	 * @date 2018/10/18
	 */
	private Map requestOrgRepay(BorrowApicron apicron, JSONArray subPacksJson) {
		Map map = new HashMap<>();
		boolean delFalg = false;
		int userId = apicron.getUserId();// 还款用户userId
		String borrowNid = apicron.getBorrowNid();// 项目编号
		// 还款子参数
		String subPacks = subPacksJson.toJSONString();
		// 获取共同参数
		String notifyUrl = systemConfig.getRepayVerifyUrl();
		String retNotifyURL = systemConfig.getRepayResultUrl();
		String channel = BankCallConstant.CHANNEL_PC;
		for (int i = 0; i < 3; i++) {
			try {
				String logOrderId = GetOrderIdUtils.getOrderId2(apicron.getUserId());
				String orderDate = GetOrderIdUtils.getOrderDate();
				String batchNo = GetOrderIdUtils.getBatchNo();// 获取还款批次号
				String txDate = GetOrderIdUtils.getTxDate();
				String txTime = GetOrderIdUtils.getTxTime();
				String seqNo = GetOrderIdUtils.getSeqNo(6);
				apicron.setBatchNo(batchNo);
				apicron.setTxDate(Integer.parseInt(txDate));
				apicron.setTxTime(Integer.parseInt(txTime));
				apicron.setSeqNo(Integer.parseInt(seqNo));
				apicron.setBankSeqNo(txDate + txTime + seqNo);
				// 更新任务API状态为进行中
				boolean apicronFlag = this.updateBorrowApicron(apicron, CustomConstants.BANK_BATCH_STATUS_SENDING);
				if (!apicronFlag) {
					throw new Exception("【计划批次代偿】更新还款任务为进行中失败。[用户ID：" + userId + "]," + "[借款编号：" + borrowNid + "]");
				}
				// 调用还款接口
				BankCallBean repayBean = new BankCallBean();
				repayBean.setVersion(BankCallConstant.VERSION_10);// 接口版本号
				repayBean.setTxCode(BankCallConstant.TXCODE_BATCH_SUBST_REPAY);// 消息类型(批次代偿)
				repayBean.setTxDate(txDate);
				repayBean.setTxTime(txTime);
				repayBean.setSeqNo(seqNo);
				repayBean.setChannel(channel);
				repayBean.setBatchNo(apicron.getBatchNo());// 批次号
				repayBean.setTxAmount(String.valueOf(apicron.getTxAmount()));// 交易金额
				repayBean.setTxCounts(String.valueOf(apicron.getTxCounts()));// 交易笔数
				repayBean.setNotifyURL(notifyUrl);// 后台通知连接
				repayBean.setRetNotifyURL(retNotifyURL);// 业务结果通知
				repayBean.setSubPacks(subPacks);// 请求数组
				repayBean.setLogUserId(String.valueOf(userId));
				repayBean.setLogOrderId(logOrderId);
				repayBean.setLogOrderDate(orderDate);
				repayBean.setLogRemark("批次还款请求");
				repayBean.setLogClient(0);
				BankCallBean repayResult = BankCallUtils.callApiBg(repayBean);
				if (Validator.isNotNull(repayResult)) {
					String received = StringUtils.isNotBlank(repayResult.getReceived()) ? repayResult.getReceived() : "";
					if (BankCallConstant.RECEIVED_SUCCESS.equals(received)) {
						try {
							// 更新任务API状态
							boolean apicronResultFlag = this.updateBorrowApicron(apicron, CustomConstants.BANK_BATCH_STATUS_SENDED);
							if (apicronResultFlag) {
								map.put("result", repayResult);
								map.put("delFlag", delFalg);
								return map;
							}
						} catch (Exception e) {
							logger.info("【计划批次代偿】-----------------------------------[用户ID：" + userId + "]," + "[借款编号：" + borrowNid + "],还款请求成功后,变更任务状态异常!");
						}
						map.put("result", repayResult);
						map.put("delFlag", true);
						return map;
					} else {
						continue;
					}
				} else {
					continue;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		map.put("result", null);
		map.put("delFlag", delFalg);
		return map;
	}

	/**
	 * 调用机构垫付还款
	 * @author wgx
	 * @date 2018/10/18
	 */
	private Map requestOrgRepay(Borrow borrow, String orgAccountId, BorrowApicron apicron, List<BorrowRecover> borrowRecoverList, List<BorrowRecoverPlan> borrowRecoverPlanList,
								List<HjhDebtCreditRepay> creditRepayList) throws Exception {
		String borrowNid = borrow.getBorrowNid();
		boolean delFlag = false;
		Map map = new HashMap<>();
		int txCounts = 0;// 交易笔数
		BigDecimal txAmountSum = BigDecimal.ZERO;// 交易总金额
		BigDecimal repayAmountSum = BigDecimal.ZERO;// 交易总金额
		BigDecimal serviceFeeSum = BigDecimal.ZERO;// 交易总服务费
		JSONArray subPacksJson = new JSONArray();// 拼接结果
		// 债转还款结果
		if (creditRepayList != null && creditRepayList.size() > 0) {
			// 遍历债权还款记录
			for (int i = 0; i < creditRepayList.size(); i++) {
				// 还款信息
				HjhDebtCreditRepay creditRepay = creditRepayList.get(i);
				JSONObject subJson = new JSONObject();// 结果集对象
				int assignUserId = creditRepay.getUserId();// 承接用户userId
				String creditRepayOrderId = creditRepay.getCreditRepayOrderId();// 债转还款订单号
				BigDecimal txAmount = creditRepay.getRepayCapital();// 交易金额
				BigDecimal intAmount = creditRepay.getRepayInterest().add(creditRepay.getRepayAdvanceInterest()).add(creditRepay.getRepayDelayInterest()).add(creditRepay.getRepayLateInterest());// 交易利息
				BigDecimal serviceFee = creditRepay.getManageFee();// 还款管理费
				logger.info("【计划批次代偿】------------------还款开始，标的：" + borrowNid + ",任务表变更金额,增加金额：" + txAmount + "+" + intAmount+"+"+serviceFee + "，原始金额：" + repayAmountSum + ",债转订单号：" + creditRepay.getCreditNid());
				repayAmountSum = repayAmountSum.add(txAmount).add(intAmount).add(serviceFee);
				txAmountSum = txAmountSum.add(txAmount);// 交易总金额
				serviceFeeSum = serviceFeeSum.add(serviceFee);// 总服务费
				String authCode = creditRepay.getAuthCode();// 投资授权码
				// 承接用户的开户信息
				Account bankOpenAccount = this.getAccount(assignUserId);
				// 判断承接用户开户信息
				if (Validator.isNotNull(bankOpenAccount) && StringUtils.isNotBlank(bankOpenAccount.getAccountId())) {
					subJson = getSubJson(subJson, orgAccountId, borrowNid, creditRepayOrderId, txAmount, intAmount, serviceFee, authCode, bankOpenAccount);
					subPacksJson.add(subJson);
				} else {
					throw new Exception("【计划批次代偿】还款时未查询到用户的银行开户信息。[用户userId：" + assignUserId + "]");
				}
			}
			txCounts = txCounts + creditRepayList.size();
		}
		// 判断标的的放款信息
		if (borrowRecoverList != null && borrowRecoverList.size() > 0) {
			// 遍历标的放款信息
			for (int i = 0; i < borrowRecoverList.size(); i++) {
				// 获取不分期的放款信息
				BorrowRecover borrowRecover = borrowRecoverList.get(i);
				JSONObject subJson = new JSONObject();// 结果集对象
				int tenderUserId = borrowRecover.getUserId();// 投资用户userId
				String recoverRepayOrderId = borrowRecover.getRepayOrdid();// 还款订单号
				BigDecimal txAmount = borrowRecover.getRecoverCapitalWait();// 交易金额
				BigDecimal intAmount = borrowRecover.getRecoverInterestWait().add(borrowRecover.getChargeInterest()).add(borrowRecover.getDelayInterest()).add(borrowRecover.getLateInterest());// 交易利息
				BigDecimal serviceFee = borrowRecover.getRecoverFee();// 还款管理费
				logger.info("【计划批次代偿】------------------还款开始，标的：" + borrowNid + ",任务表变更金额,增加金额：" + txAmount + "+" + intAmount+"+"+serviceFee + "，原始金额：" + repayAmountSum + ",投资订单号：" + borrowRecover.getNid());
				txAmountSum = txAmountSum.add(txAmount);// 交易总金额
				repayAmountSum = repayAmountSum.add(txAmount).add(intAmount).add(serviceFee);
				serviceFeeSum = serviceFeeSum.add(serviceFee);// 总服务费
				String authCode = borrowRecover.getAuthCode();// 投资授权码
				// 投资用户的开户信息
				Account bankOpenAccount = this.getAccount(tenderUserId);
				// 判断投资用户开户信息
				if (Validator.isNotNull(bankOpenAccount) && StringUtils.isNotBlank(bankOpenAccount.getAccountId())) {
					subJson = getSubJson(subJson, orgAccountId, borrowNid, recoverRepayOrderId, txAmount, intAmount, serviceFee, authCode, bankOpenAccount);
					subPacksJson.add(subJson);
				} else {
					throw new Exception("【计划批次代偿】还款时未查询到用户的银行开户信息。[用户userId：" + tenderUserId + "]");
				}
			}
			txCounts = txCounts + borrowRecoverList.size();
		} else {
			if (borrowRecoverPlanList != null && borrowRecoverPlanList.size() > 0) {
				for (int i = 0; i < borrowRecoverPlanList.size(); i++) {
					// 获取分期的放款信息
					BorrowRecoverPlan borrowRecoverPlan = borrowRecoverPlanList.get(i);
					JSONObject subJson = new JSONObject();// 结果集对象
					int tenderUserId = borrowRecoverPlan.getUserId();// 投资用户userId
					String recoverPlanRepayOrderId = borrowRecoverPlan.getRepayOrderId();
					BigDecimal txAmount = borrowRecoverPlan.getRecoverCapitalWait();// 交易金额
					BigDecimal intAmount = borrowRecoverPlan.getRecoverInterestWait().add(borrowRecoverPlan.getChargeInterest()).add(borrowRecoverPlan.getDelayInterest())
							.add(borrowRecoverPlan.getLateInterest());// 交易利息
					BigDecimal serviceFee = borrowRecoverPlan.getRecoverFee();// 还款管理费
					logger.info("【计划批次代偿】------------------还款开始，标的：" + borrowNid + ",任务表变更金额,增加金额：" + txAmount + "+" + intAmount+"+"+serviceFee + "，原始金额：" + repayAmountSum + ",投资订单号：" + borrowRecoverPlan.getNid());
					repayAmountSum = repayAmountSum.add(txAmount).add(intAmount).add(serviceFee);
					txAmountSum = txAmountSum.add(txAmount);// 交易总金额
					serviceFeeSum = serviceFeeSum.add(serviceFee);// 总服务费
					String authCode = borrowRecoverPlan.getAuthCode();// 投资授权码
					// 投资用户的开户信息
					Account bankOpenAccount = this.getAccount(tenderUserId);
					// 判断投资用户开户信息
					if (Validator.isNotNull(bankOpenAccount) && StringUtils.isNotBlank(bankOpenAccount.getAccountId())) {
						subJson = getSubJson(subJson, orgAccountId, borrowNid, recoverPlanRepayOrderId, txAmount, intAmount, serviceFee, authCode, bankOpenAccount);
						subPacksJson.add(subJson);
					} else {
						throw new Exception("【计划批次代偿】还款时未查询到用户的银行开户信息。[用户userId：" + tenderUserId + "]");
					}
				}
				txCounts = txCounts + borrowRecoverPlanList.size();
			}
		}
		// 拼接相应的还款参数
		if (apicron.getFailTimes() == 0) {
			apicron.setBatchAmount(repayAmountSum);
			apicron.setBatchCounts(txCounts);
			apicron.setBatchServiceFee(serviceFeeSum);
			apicron.setSucAmount(BigDecimal.ZERO);
			apicron.setSucCounts(0);
			apicron.setFailAmount(repayAmountSum);
			apicron.setFailCounts(txCounts);
		}
		apicron.setServiceFee(serviceFeeSum);
		apicron.setTxAmount(txAmountSum);
		apicron.setTxCounts(txCounts);
		apicron.setData(" ");
		Map resultMap = this.requestOrgRepay(apicron, subPacksJson);
		BankCallBean repayResult = (BankCallBean) resultMap.get("result");
		delFlag = (boolean) resultMap.get("delFlag");
		try {
			if (Validator.isNotNull(repayResult)) {
				String received = repayResult.getReceived();
				if (StringUtils.isNotBlank(received)) {
					if (BankCallConstant.RECEIVED_SUCCESS.equals(received)) {
						map.put("result", repayResult);
						map.put("delFlag", delFlag);
						return map;
					} else {
						throw new Exception("【计划批次代偿】更新放款任务为放款请求失败失败。[用户ID：" + apicron.getUserId() + "]," + "[借款编号：" + borrowNid + "]");
					}
				} else {
					throw new Exception("【计划批次代偿】放款请求失败。[用户ID：" + apicron.getUserId() + "]," + "[借款编号：" + borrowNid + "]");
				}
			} else {
				throw new Exception("【计划批次代偿】放款请求失败。[用户ID：" + apicron.getUserId()  + "]," + "[借款编号：" + borrowNid + "]");
			}
		} catch (Exception e) {
			logger.info("【计划批次代偿】-----------------------------------放款请求失败,错误信息:" + e.getMessage());
		}
		map.put("result", repayResult);
		map.put("delFlag", delFlag);
		return map;
	}
	/**
	 * 批次代偿请求数组
	 * @author wgx
	 * @date 2018/10/18
	 */
	private JSONObject getSubJson(JSONObject subJson, String accountId, String borrowNid, String orderId, BigDecimal txAmount, BigDecimal intAmount, BigDecimal serviceFee, String authCode, Account bankOpenAccount) {
		String forAccountId = bankOpenAccount.getAccountId();// 银行账户
		subJson.put(BankCallConstant.PARAM_ACCOUNTID, accountId);           // 存管子账户             accountId    担保户
		subJson.put(BankCallConstant.PARAM_ORDERID, orderId);               // 订单号                 orderId      由P2P生成，必须保证唯一
		subJson.put(BankCallConstant.PARAM_TXAMOUNT, txAmount.toString());  // 交易金额               txAmount     代偿本金
		subJson.put(BankCallConstant.PARAM_RISKAMOUNT, "");                 // 风险准备金             riskAmount   暂不做使用，请送空
		subJson.put(BankCallConstant.PARAM_INTAMOUNT, intAmount.toString());// 交易利息               intAmount
		subJson.put(BankCallConstant.PARAM_TXFEEIN, "0.00");                // 收款手续费             txFeeIn      向投资人收取的手续费
		subJson.put(BankCallConstant.PARAM_FINEAMOUNT, "");                 // 罚息金额               fineAmount   暂不做使用，请送空
		subJson.put(BankCallConstant.PARAM_FORACCOUNTID, forAccountId);     // 对手存管子账户         forAccountId 投资人账号
		subJson.put(BankCallConstant.PARAM_PRODUCTID, borrowNid);           // 标的号                 productId    投资人投标成功的标的号
		subJson.put(BankCallConstant.PARAM_AUTHCODE, authCode);             // 授权码                 authCode     投资人投标成功的授权号
		subJson.put(BankCallConstant.PARAM_SEQFLAG, "");                    // 流水是否经过借款人标志 seqFlag      担保人资金是否过借款人标志。1：经过;空：不经过；
		subJson.put(BankCallConstant.PARAM_TXFEEOUT, serviceFee.toString());// 转出方手续费金额       txFeeOut     扣收担保人手续费
		return subJson;
	}
}
