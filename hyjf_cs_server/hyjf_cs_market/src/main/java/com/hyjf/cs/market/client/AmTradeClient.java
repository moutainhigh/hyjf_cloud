package com.hyjf.cs.market.client;

import com.hyjf.am.response.trade.DataSearchCustomizeResponse;
import com.hyjf.am.resquest.trade.DataSearchRequest;
import com.hyjf.am.vo.datacollect.OperationReportEntityVO;
import com.hyjf.am.vo.datacollect.TzjDayReportVO;
import com.hyjf.am.vo.trade.DataSearchCustomizeVO;
import com.hyjf.am.vo.trade.TenderCityCountVO;
import com.hyjf.am.vo.trade.TenderSexCountVO;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author xiasq
 * @version AmUserClient, v0.1 2018/7/6 11:04
 */
public interface AmTradeClient {

	/**
	 * 查询投之家每日充值人数、投资人数 、投资金额、首投金额、首投人数、复投人数
	 * 
	 * @param tzjUserIds
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	TzjDayReportVO queryTradeDataOnToday(Set<Integer> tzjUserIds, Date startTime, Date endTime);

	/**
	 * 新充人数 新投人数
	 * 
	 * @param registerUserIds
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	TzjDayReportVO queryTradeNewDataOnToday(Set<Integer> registerUserIds, Date startTime, Date endTime);

	/**
	 * 投资人按照地域分布
	 * @param lastDay 一个月的最后一天
	 * @return
	 */
	List<TenderCityCountVO> getTenderCityGroupBy(Date lastDay);

	/**
	 * 投资人按照性别分布
	 * @param lastDay
	 * @return
	 */
	List<TenderSexCountVO> getTenderSexGroupBy(Date lastDay);

	/**
	 *
	 * @param date 上个月的最后一天
	 * @param firstAge  年龄下限
	 * @param endAge	年龄上限
	 * @return
	 */
	int getTenderAgeByRange(Date date,int firstAge,int endAge);

	/**
	 * 统计前一个月的数据
	 * @param i 时间戳
	 * @return
	 */
	OperationReportEntityVO getOperationReport(int i);

	/**
	 * 月交易金额
	 * @param firstDay 月第一天
	 * @param lastDay 月最后一天
	 * @return
	 */
	BigDecimal getAccountByMonth(Date firstDay, Date lastDay);

	/**
	 * 月交易笔数
	 * @param firstDay 月第一天
	 * @param lastDay 月最后一天
	 * @return
	 */
	int getTradeCountByMonth(Date firstDay, Date lastDay);

	/**
	 * 借贷笔数
	 * @param lastDay
	 * @return
	 */
	int getLoanNum(Date lastDay);

	/**
	 * 总投资金额
	 * @param lastDay 月最后一天
	 * @return
	 */
	double getInvestLastDate(Date lastDay);

	/**
	 * 总投资人数
	 * @param lastDay 月最后一天
	 * @return
	 */
	int getTenderCount(Date lastDay);

	/**
	 * 平均满标时间
	 * @param lastDay 月最后一天
	 * @return
	 */
	float getFullBillAverageTime(Date lastDay);

	/**
	 * 代偿金额
	 * @param lastDay 月最后一天
	 * @return
	 */
	BigDecimal getRepayTotal(Date lastDay);

	/**
	 * 累计借款人（定义：系统累计到现在进行过发表的底层借款人数量）
	 * @return
	 */
	Integer countBorrowUser();

	/**
	 * 当前借款人（定义：当前有尚未结清债权的底层借款人数量）
	 * @return
	 */
	Integer countCurrentBorrowUser();

	/**
	 * 当前投资人（定义：当前代还金额不为0的用户数量）
	 * @return
	 */
	Integer countCurrentTenderUser();

	/**
	 * 代还总金额
	 * @param lastDay
	 * @return
	 */
	BigDecimal sumBorrowUserMoney(Date lastDay);

	/**
	 * 前十大借款人待还金额
	 * @return
	 */
	BigDecimal sumBorrowUserMoneyTopTen();

	/**
	 * 最大单一借款人待还金额
	 * @return
	 */
	BigDecimal sumBorrowUserMoneyTopOne();

	/**
	 * 查询千乐渠道散标数据
	 * @return
	 */
	DataSearchCustomizeResponse querySanList(DataSearchRequest dataSearchRequest);
}
