package com.hyjf.am.vo.hgreportdata.cert;


import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @author pcc
 */
public class CertAccountListCustomizeVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1035131990766081594L;
	private Integer roleId;
	private Integer id;

	/**
	 * 交易凭证号
	 *
	 * @mbggenerated
	 */
	private String nid;

	/**
	 * 汇计划加入订单号
	 *
	 * @mbggenerated
	 */
	private String accedeOrderId;

	/**
	 * 该明细是否在前台展示
	 *
	 * @mbggenerated
	 */
	private Integer isShow;

	/**
	 * 用户id
	 *
	 * @mbggenerated
	 */
	private Integer userId;

	/**
	 * 操作金额
	 *
	 * @mbggenerated
	 */
	private BigDecimal amount;

	/**
	 * 收支类型1收入2支出3冻结
	 *
	 * @mbggenerated
	 */
	private Integer type;

	/**
	 * 交易类型
	 *
	 * @mbggenerated
	 */
	private String trade;

	/**
	 * 操作识别码 balance余额操作 frost冻结操作 await待收操作
	 *
	 * @mbggenerated
	 */
	private String tradeCode;

	/**
	 * 资金总额
	 *
	 * @mbggenerated
	 */
	private BigDecimal total;

	/**
	 * 可用金额
	 *
	 * @mbggenerated
	 */
	private BigDecimal balance;

	/**
	 * 冻结金额
	 *
	 * @mbggenerated
	 */
	private BigDecimal frost;

	/**
	 * 汇添金账户冻结金额
	 *
	 * @mbggenerated
	 */
	private BigDecimal planFrost;

	/**
	 * 待收金额
	 *
	 * @mbggenerated
	 */
	private BigDecimal await;

	/**
	 * 待还金额
	 *
	 * @mbggenerated
	 */
	private BigDecimal repay;

	private String remark;

	/**
	 * 操作员
	 *
	 * @mbggenerated
	 */
	private String operator;

	/**
	 * 操作IP
	 *
	 * @mbggenerated
	 */
	private String ip;

	/**
	 * 网站收支计算标识
	 *
	 * @mbggenerated
	 */
	private Integer web;

	/**
	 * 汇添金账户可用余额
	 *
	 * @mbggenerated
	 */
	private BigDecimal planBalance;

	/**
	 * 是否是银行的交易记录(0:否,1:是)
	 *
	 * @mbggenerated
	 */
	private Integer isBank;

	/**
	 * 电子账号
	 *
	 * @mbggenerated
	 */
	private String accountId;

	/**
	 * 银行订单日期
	 *
	 * @mbggenerated
	 */
	private Integer txDate;

	/**
	 * 银行订单时间
	 *
	 * @mbggenerated
	 */
	private Integer txTime;

	/**
	 * 银行交易流水号
	 *
	 * @mbggenerated
	 */
	private String seqNo;

	/**
	 * 银行交易订单号
	 *
	 * @mbggenerated
	 */
	private String bankSeqNo;

	/**
	 * 对账状态0：未对账 1：已对账
	 *
	 * @mbggenerated
	 */
	private Integer checkStatus;

	/**
	 * 交易状态0: 失败 1：成功
	 *
	 * @mbggenerated
	 */
	private Integer tradeStatus;

	/**
	 * 银行总资产
	 *
	 * @mbggenerated
	 */
	private BigDecimal bankTotal;

	/**
	 * 银行存管可用余额
	 *
	 * @mbggenerated
	 */
	private BigDecimal bankBalance;

	/**
	 * 银行存管冻结金额
	 *
	 * @mbggenerated
	 */
	private BigDecimal bankFrost;

	/**
	 * 银行待还本息
	 *
	 * @mbggenerated
	 */
	private BigDecimal bankWaitRepay;

	/**
	 * 银行待还本金
	 *
	 * @mbggenerated
	 */
	private BigDecimal bankWaitCapital;

	/**
	 * 银行待还利息
	 *
	 * @mbggenerated
	 */
	private BigDecimal bankWaitInterest;

	/**
	 * 银行累计收益
	 *
	 * @mbggenerated
	 */
	private BigDecimal bankInterestSum;

	/**
	 * 银行累计出借
	 *
	 * @mbggenerated
	 */
	private BigDecimal bankInvestSum;

	/**
	 * 银行待收总额
	 *
	 * @mbggenerated
	 */
	private BigDecimal bankAwait;

	/**
	 * 银行待收本金
	 *
	 * @mbggenerated
	 */
	private BigDecimal bankAwaitCapital;

	/**
	 * 银行待收利息
	 *
	 * @mbggenerated
	 */
	private BigDecimal bankAwaitInterest;

	/**
	 * 对账时间
	 *
	 * @mbggenerated
	 */
	private Integer checkDate;

	/**
	 * 到账金额
	 *
	 * @mbggenerated
	 */
	private BigDecimal checkBalance;

	/**
	 * 到账时间
	 *
	 * @mbggenerated
	 */
	private Integer accountDate;

	/**
	 * 创建时间
	 *
	 * @mbggenerated
	 */
	private Date createTime;


	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid == null ? null : nid.trim();
	}

	public String getAccedeOrderId() {
		return accedeOrderId;
	}

	public void setAccedeOrderId(String accedeOrderId) {
		this.accedeOrderId = accedeOrderId == null ? null : accedeOrderId.trim();
	}

	public Integer getIsShow() {
		return isShow;
	}

	public void setIsShow(Integer isShow) {
		this.isShow = isShow;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getTrade() {
		return trade;
	}

	public void setTrade(String trade) {
		this.trade = trade == null ? null : trade.trim();
	}

	public String getTradeCode() {
		return tradeCode;
	}

	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode == null ? null : tradeCode.trim();
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getFrost() {
		return frost;
	}

	public void setFrost(BigDecimal frost) {
		this.frost = frost;
	}

	public BigDecimal getPlanFrost() {
		return planFrost;
	}

	public void setPlanFrost(BigDecimal planFrost) {
		this.planFrost = planFrost;
	}

	public BigDecimal getAwait() {
		return await;
	}

	public void setAwait(BigDecimal await) {
		this.await = await;
	}

	public BigDecimal getRepay() {
		return repay;
	}

	public void setRepay(BigDecimal repay) {
		this.repay = repay;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark == null ? null : remark.trim();
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator == null ? null : operator.trim();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip == null ? null : ip.trim();
	}

	public Integer getWeb() {
		return web;
	}

	public void setWeb(Integer web) {
		this.web = web;
	}

	public BigDecimal getPlanBalance() {
		return planBalance;
	}

	public void setPlanBalance(BigDecimal planBalance) {
		this.planBalance = planBalance;
	}

	public Integer getIsBank() {
		return isBank;
	}

	public void setIsBank(Integer isBank) {
		this.isBank = isBank;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId == null ? null : accountId.trim();
	}

	public Integer getTxDate() {
		return txDate;
	}

	public void setTxDate(Integer txDate) {
		this.txDate = txDate;
	}

	public Integer getTxTime() {
		return txTime;
	}

	public void setTxTime(Integer txTime) {
		this.txTime = txTime;
	}

	public String getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(String seqNo) {
		this.seqNo = seqNo == null ? null : seqNo.trim();
	}

	public String getBankSeqNo() {
		return bankSeqNo;
	}

	public void setBankSeqNo(String bankSeqNo) {
		this.bankSeqNo = bankSeqNo == null ? null : bankSeqNo.trim();
	}

	public Integer getCheckStatus() {
		return checkStatus;
	}

	public void setCheckStatus(Integer checkStatus) {
		this.checkStatus = checkStatus;
	}

	public Integer getTradeStatus() {
		return tradeStatus;
	}

	public void setTradeStatus(Integer tradeStatus) {
		this.tradeStatus = tradeStatus;
	}

	public BigDecimal getBankTotal() {
		return bankTotal;
	}

	public void setBankTotal(BigDecimal bankTotal) {
		this.bankTotal = bankTotal;
	}

	public BigDecimal getBankBalance() {
		return bankBalance;
	}

	public void setBankBalance(BigDecimal bankBalance) {
		this.bankBalance = bankBalance;
	}

	public BigDecimal getBankFrost() {
		return bankFrost;
	}

	public void setBankFrost(BigDecimal bankFrost) {
		this.bankFrost = bankFrost;
	}

	public BigDecimal getBankWaitRepay() {
		return bankWaitRepay;
	}

	public void setBankWaitRepay(BigDecimal bankWaitRepay) {
		this.bankWaitRepay = bankWaitRepay;
	}

	public BigDecimal getBankWaitCapital() {
		return bankWaitCapital;
	}

	public void setBankWaitCapital(BigDecimal bankWaitCapital) {
		this.bankWaitCapital = bankWaitCapital;
	}

	public BigDecimal getBankWaitInterest() {
		return bankWaitInterest;
	}

	public void setBankWaitInterest(BigDecimal bankWaitInterest) {
		this.bankWaitInterest = bankWaitInterest;
	}

	public BigDecimal getBankInterestSum() {
		return bankInterestSum;
	}

	public void setBankInterestSum(BigDecimal bankInterestSum) {
		this.bankInterestSum = bankInterestSum;
	}

	public BigDecimal getBankInvestSum() {
		return bankInvestSum;
	}

	public void setBankInvestSum(BigDecimal bankInvestSum) {
		this.bankInvestSum = bankInvestSum;
	}

	public BigDecimal getBankAwait() {
		return bankAwait;
	}

	public void setBankAwait(BigDecimal bankAwait) {
		this.bankAwait = bankAwait;
	}

	public BigDecimal getBankAwaitCapital() {
		return bankAwaitCapital;
	}

	public void setBankAwaitCapital(BigDecimal bankAwaitCapital) {
		this.bankAwaitCapital = bankAwaitCapital;
	}

	public BigDecimal getBankAwaitInterest() {
		return bankAwaitInterest;
	}

	public void setBankAwaitInterest(BigDecimal bankAwaitInterest) {
		this.bankAwaitInterest = bankAwaitInterest;
	}

	public Integer getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Integer checkDate) {
		this.checkDate = checkDate;
	}

	public BigDecimal getCheckBalance() {
		return checkBalance;
	}

	public void setCheckBalance(BigDecimal checkBalance) {
		this.checkBalance = checkBalance;
	}

	public Integer getAccountDate() {
		return accountDate;
	}

	public void setAccountDate(Integer accountDate) {
		this.accountDate = accountDate;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}
	
}
