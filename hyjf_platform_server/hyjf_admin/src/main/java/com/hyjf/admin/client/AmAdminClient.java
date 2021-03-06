package com.hyjf.admin.client;

import com.hyjf.admin.beans.request.AppPushManageRequestBean;
import com.hyjf.admin.beans.request.DadaCenterCouponRequestBean;
import com.hyjf.admin.beans.request.PlatformCountRequestBean;
import com.hyjf.am.bean.admin.LockedConfig;
import com.hyjf.am.response.*;
import com.hyjf.am.response.admin.*;
import com.hyjf.am.response.admin.locked.LockedUserMgrResponse;
import com.hyjf.am.response.admin.promotion.ChannelReconciliationResponse;
import com.hyjf.am.response.admin.promotion.PlatformUserCountCustomizeResponse;
import com.hyjf.am.response.admin.vip.content.CustomerTaskConfigVOResponse;
import com.hyjf.am.response.admin.vip.content.ScreenConfigVOResponse;
import com.hyjf.am.response.config.*;
import com.hyjf.am.response.market.AppBannerResponse;
import com.hyjf.am.response.trade.BorrowApicronResponse;
import com.hyjf.am.response.trade.DataSearchCustomizeResponse;
import com.hyjf.am.response.trade.RepayResponse;
import com.hyjf.am.response.trade.STZHWhiteListResponse;
import com.hyjf.am.response.user.ChannelStatisticsDetailResponse;
import com.hyjf.am.resquest.admin.*;
import com.hyjf.am.resquest.admin.locked.LockedeUserListRequest;
import com.hyjf.am.resquest.config.*;
import com.hyjf.am.resquest.market.AppBannerRequest;
import com.hyjf.am.resquest.trade.DataSearchRequest;
import com.hyjf.am.resquest.trade.ScreenDataBean;
import com.hyjf.am.resquest.user.ChannelStatisticsDetailRequest;
import com.hyjf.am.vo.admin.*;
import com.hyjf.am.vo.admin.coupon.DataCenterCouponCustomizeVO;
import com.hyjf.am.vo.admin.locked.LockedUserInfoVO;
import com.hyjf.am.vo.config.ParamNameVO;
import com.hyjf.am.vo.config.SubmissionsVO;
import com.hyjf.am.vo.market.AdsVO;
import com.hyjf.am.vo.trade.OperationReportJobVO;
import com.hyjf.am.vo.trade.RepaymentPlanVO;
import com.hyjf.am.vo.trade.borrow.BorrowStyleVO;
import com.hyjf.am.vo.trade.repay.BankRepayOrgFreezeLogVO;
import com.hyjf.am.vo.user.CustomerTaskConfigVO;
import com.hyjf.am.vo.user.HjhInstConfigVO;
import com.hyjf.am.vo.user.ScreenConfigVO;
import com.hyjf.am.vo.user.UtmPlatVO;
import com.hyjf.pay.lib.duiba.sdk.VirtualResult;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author tanyy
 * @version AmAdminClient, v0.1 2018/8/19 12:44
 */
public interface AmAdminClient {

    /**
     * 按照省份统计出借人的分布
     * @param date 上个月的最后一天
     */
    List<OperationReportJobVO> getTenderCityGroupByList(Date date);

    /**
     * 业绩总览
     */
    List<OperationReportJobVO> getPerformanceSum();
    /**
     * 按照性别统计出借人的分布
     * @param date 上个月的最后一天
     */
    List<OperationReportJobVO>  getTenderSexGroupByList(Date date);
    /**
     * 充值金额、充值笔数
     *
     * @param intervalMonth 今年间隔月份
     */
    List<OperationReportJobVO> getRechargeMoneyAndSum(int intervalMonth);
    /**
     * 渠道分析 ，成交笔数
     *
     * @param intervalMonth 今年间隔月份
     * @return
     */
    List<OperationReportJobVO> getCompleteCount(int intervalMonth);
    /**
     * 用户分析 - 性别分布
     *
     * @param intervalMonth 今年间隔月份
     * @return
     */
    List<OperationReportJobVO> getSexDistribute( int intervalMonth);

    /**
     * 用户分析 - 年龄分布
     *
     * @param intervalMonth 今年间隔月份
     * @return
     */
    List<OperationReportJobVO> getAgeDistribute( int intervalMonth);

    /**
     * 用户分析 - 金额分布
     *
     * @param intervalMonth 今年间隔月份
     * @return
     */
    List<OperationReportJobVO> getMoneyDistribute( int intervalMonth);
    /**
     * 当月、季、半年、全年业绩  下面的  成交金额,根据月份计算
     *
     * @param startMonth 开始月份
     * @param endMonth   结束月份
     * @return
     */
    List<OperationReportJobVO> getMonthDealMoney(int startMonth,int endMonth);

    /**
     * 大赢家，收益最高
     *
     * @param intervalMonth 今年间隔月份
     * @return
     */
    List<OperationReportJobVO> getOneInterestsMost(int intervalMonth);
    /**
     * 超活跃，出借笔数最多
     *
     * @param intervalMonth 今年间隔月份
     * @return
     */
    List<OperationReportJobVO> getOneInvestMost(int intervalMonth);
    /**
     * 借款期限
     *
     * @param intervalMonth 今年间隔月份
     * @return
     */
    List<OperationReportJobVO> getBorrowPeriod(int intervalMonth);
    /**
     * 十大出借人
     *
     * @param intervalMonth 今年间隔月份
     * @return
     */
    List<OperationReportJobVO> getTenMostMoney( int intervalMonth);

    /**
     * 今年这个时候到手收益 和 去年这个时候到手收益 和  出借利率
     *
     * @param intervalMonth 今年间隔月份
     * @param startMonth    去年开始月份
     * @param endMonth      去年结束月份
     * @return
     */
    List<OperationReportJobVO> getRevenueAndYield(int intervalMonth,int startMonth,int endMonth);
    /**
     * 按月统计交易笔数
     * @param beginDate 统计月的第一天
     * @param endDate	统计月的最后一天
     * @return
     */
    int getTradeCountByMonth(Date beginDate,Date endDate);

    /**
     * 获取截至日期的出借金额
     */
    double getInvestLastDate(Date date);

    /**
     * 统计出借人总数，截至日期为上个月的最后一天
     * @param date 上个月的最后一天
     * @return
     */
    int getTenderCount(Date date);

    /**
     * 借贷笔数
     */
    int getLoanNum(Date date);
    /**
     * 按月统计平台的交易总额
     *
     * @param beginDate
     *            统计月的第一天
     * @param endDate
     *            统计月的最后一天
     * @return
     */
    BigDecimal getAccountByMonth(Date beginDate, Date endDate);
    /**
     *出借人按照年龄分布 返回符合条件所有用户
     *
     * @param date 上个月的最后一天
     * @return
     */
    List<OperationReportJobVO>  getTenderAgeByRangeList(Date date);
    /**
     * count PC统计明细列表
     *
     * @return
     */
     IntegerResponse countList(ChannelStatisticsDetailRequest request);
    /**
     * 平均满标时间
     * @param date 统计月的最后一天
     * @return
     */
    float getFullBillAverageTime(Date date);
    /**
     * 统计所有待偿金额，截至日期为上个月的最后一天
     * @param date 上个月的最后一天
     * @return
     */
    BigDecimal getRepayTotal(Date date);
    /**
     * 获取所有渠道
     * @param type 类型:app,pc
     * @return
     */
    List<UtmVO> selectUtmPlatList(String type);
    /**
     * 访问数
     * @param sourceId 账户推广平台
     * @param type 类型: pc,app
     * @return
     */
    Integer getAccessNumber(Integer sourceId, String type);
    /**
     * 注册数
     * @param sourceId 账户推广平台
     * @param type 类型: pc,app
     * @return
     */
    Integer getRegistNumber(Integer sourceId, String type);

    /**
     * 开户数
     * @param sourceId
     * @param type 类型: pc,app
     * @return
     */
    Integer getOpenAccountNumber(Integer sourceId, String type);

    /**
     * 出借人数
     * @param sourceId
     * @param type 类型: pc,app
     * @return
     */
    Integer getTenderNumber(Integer sourceId, String type);

    /**
     * 累计充值
     * @param sourceId
     * @param type 类型: pc,app
     * @return
     */
    BigDecimal getCumulativeRecharge(Integer sourceId, String type);

    /**
     * 汇直投出借金额
     * @param sourceId
     * @param type 类型: pc,app
     * @return
     */
    BigDecimal getHztTenderPrice(Integer sourceId, String type);

    /**
     * 汇消费出借金额
     * @param sourceId
     * @param type 类型: pc,app
     * @return
     */
    BigDecimal getHxfTenderPrice(Integer sourceId, String type);

    /**
     * 汇天利出借金额
     * @param sourceId
     * @param type 类型: pc,app
     * @return
     */
    BigDecimal getHtlTenderPrice(Integer sourceId, String type);

    /**
     * 汇添金出借金额
     * @param sourceId
     * @param type 类型: pc,app
     * @return
     */
    BigDecimal getHtjTenderPrice(Integer sourceId, String type);

    /**
     * 汇金理财出借金额
     * @param sourceId
     * @param type 类型: pc,app
     * @return
     */
    BigDecimal getRtbTenderPrice(Integer sourceId, String type);

    /**
     * 汇转让出借金额
     * @param sourceId
     * @param type 类型: pc,app
     * @return
     */
    BigDecimal getHzrTenderPrice(Integer sourceId, String type);

    /**
     * 根据条件查询PC统计明细
     *
     * @param request
     * @return
     */
    ChannelStatisticsDetailResponse searchAction(ChannelStatisticsDetailRequest request);

    /**
     * 获取app渠道列表
     */
    public List<UtmPlatVO> getPCUtm();

    /**
     * 获取用户/机构信息
     * @param requestBean
     * @return
     */
    STZHWhiteListResponse getUserByUserName(STZHWhiteListRequestBean requestBean);

    /**
     * 还款方式下拉列表
     *
     * @param
     * @return
     * @author wangjun
     * yangchangwei 迁移至amadminClient
     */
    List<BorrowStyleVO> selectCommonBorrowStyleList();


    /**
     * 查询汇计划转让列表
     *
     * @param request
     * @return
     * yangchangwei
     */
    HjhDebtCreditReponse queryHjhDebtCreditList(HjhDebtCreditListRequest request);

    /**
     * 查询手续费分账count
     * @auth sunpeikai
     * @param
     * @return
     */
    int getPoundageCount(PoundageListRequest request);
    /**
     * 查询手续费分账list
     * @auth sunpeikai
     * @param
     * @return
     */
    List<PoundageCustomizeVO> searchPoundageList(PoundageListRequest request);

    /**
     * 根据用户名查询分账名单是否存在
     * @author xiehuili
     * @param adminRequest
     * @return
     */
    public AdminSubConfigResponse subconfig(AdminSubConfigRequest adminRequest);

    /**
     * 获取手续费分账数额总计
     * @auth sunpeikai
     * @param
     * @return
     */
    PoundageCustomizeVO getPoundageSum(PoundageListRequest request);

    /**
     * 根据id查询手续费分账信息
     * @auth sunpeikai
     * @param
     * @return
     */
    PoundageCustomizeVO getPoundageById(Integer id);

    /**
     * 审核-更新poundage表
     * @auth sunpeikai
     * @param
     * @return
     */
    Integer updatePoundage(PoundageCustomizeVO poundageCustomizeVO);

    /**
     * 批次中心-批次放款导出记录总数
     * @param request
     * @return
     */
    int getBatchBorrowRecoverCount(BatchBorrowRecoverRequest request);


    int getBatchBorrowRecoverLogCount(BatchBorrowRecoverRequest request);

    BatchBorrowRecoverLogReponse getBatchBorrowRecoverLogList(BatchBorrowRecoverRequest request);

    /**
     * 查询批次中心-批次放款列表
     * @param request
     * @return
     */
    BatchBorrowRecoverReponse getBatchBorrowRecoverList(BatchBorrowRecoverRequest request);

    /**
     * 查询批次中心的批次列表求和
     *
     * @param request
     * @return
     */
    BatchBorrowRecoverReponse getBatchBorrowCenterListSum(BatchBorrowRecoverRequest request);

    /**
     * yangchangwei
     * 根据id 获取放款任务表
     *
     * @param apicronID
     * @return
     */
    BorrowApicronResponse getBorrowApicronByID(String apicronID);

    /**
     * 查询权限数量
     * @auth sunpeikai
     * @param
     * @return
     */
    int getPermissionsCount(AdminPermissionsRequest request);

    /**
     * 查询权限列表
     * @auth sunpeikai
     * @param
     * @return
     */
    List<AdminPermissionsVO> searchPermissionsList(AdminPermissionsRequest request);

    /**
     * 检查数据库是否已存在该权限
     * @auth sunpeikai
     * @param
     * @return
     */
    boolean isExistsPermission(AdminPermissionsVO adminPermissionsVO);

    /**
     * 插入权限
     * @auth sunpeikai
     * @param
     * @return
     */
    int insertPermission(AdminPermissionsVO adminPermissionsVO);

    /**
     * 修改权限
     * @auth sunpeikai
     * @param
     * @return
     */
    int updatePermission(AdminPermissionsVO adminPermissionsVO);

    /**
     * 根据uuid查询权限
     * @auth sunpeikai
     * @param
     * @return
     */
    AdminPermissionsVO searchPermissionByUuid(String uuid);

    /**
     * 删除权限
     * @auth sunpeikai
     * @param
     * @return
     */
    int deletePermission(String uuid);

    /**
     * 资产来源
     * yangchangwei
     * @return
     */
    List<HjhInstConfigVO> selectHjhInstConfigList();

    /**
     * 查询数据字典总数
     * @auth sunpeikai
     * @param
     * @return
     */
    int getParamNamesCount(AdminParamNameRequest request);

    /**
     * 查询数据字典列表
     * @auth sunpeikai
     * @param
     * @return
     */
    List<ParamNameVO> searchParamNamesList(AdminParamNameRequest request);

    /**
     * 检查paramName是否存在
     * @auth sunpeikai
     * @param
     * @return
     */
    boolean isExistsParamName(ParamNameVO paramNameVO);

    /**
     * 添加数据字典
     * @auth sunpeikai
     * @param
     * @return
     */
    int insertParamName(ParamNameVO paramNameVO);

    /**
     * 更新数据字典
     * @auth sunpeikai
     * @param
     * @return
     */
    int updateParamName(ParamNameVO paramNameVO);

    /**
     * 根据联合主键查询数据字典
     * @auth sunpeikai
     * @param
     * @return
     */
    ParamNameVO searchParamNameByKey(ParamNameVO paramNameVO);

    /**
     * 删除数据字典
     * @auth sunpeikai
     * @param
     * @return
     */
    int deleteParamName(ParamNameVO paramNameVO);

    /**
     * 同步数据字典至redis
     * @auth wgx
     * @return
     */
    boolean syncParam();

    /**
     * 查询手续费分账配置
     * @auth sunpeikai
     * @param
     * @return
     */
    PoundageLedgerVO getPoundageLedgerById(int id);

    /**
     * 手续费分账详细信息总数
     * @auth sunpeikai
     * @param
     * @return
     */
    int getPoundageDetailCount(AdminPoundageDetailRequest request);

    /**
     * 手续费分账详细信息列表
     * @auth sunpeikai
     * @param
     * @return
     */
    List<PoundageDetailVO> searchPoundageDetailList(AdminPoundageDetailRequest request);


	List<DataCenterCouponCustomizeVO> getRecordListDJ(DataCenterCouponCustomizeVO dataCenterCouponCustomize);

    /**
     * 获取加息券列表个数
     * @param
     * @return
     */
    int getCountDJ();

    /**
     * 获取加息券列表个数
     * @param
     * @return
     */
    int getCountJX();

	List<DataCenterCouponCustomizeVO> getRecordListJX(DataCenterCouponCustomizeVO dataCenterCouponCustomize);


	String getActivityTitle(Integer activityId);


	List<DataCenterCouponCustomizeVO> getDataCenterCouponList(DadaCenterCouponRequestBean requestBean, String type);


	PlatformCountCustomizeResponse searchAction(PlatformCountRequestBean requestBean);


    PlatformUserCountCustomizeResponse searchRegistAcount(PlatformCountRequestBean requestBean);

    /**
     * PC统计明细散标列表查询
     * @param request
     * @return
     */
    ChannelReconciliationResponse selectPcChannelReconciliationRecord(ChannelReconciliationRequest request);

    /**
     * PC统计明细计划列表查询
     * @param request
     * @return
     */
    ChannelReconciliationResponse selectPcChannelReconciliationRecordHjh(ChannelReconciliationRequest request);

    /**
     * APP统计明细散标列表查询
     * @param request
     * @return
     */
    ChannelReconciliationResponse selectAppChannelReconciliationRecord(ChannelReconciliationRequest request);

    /**
     * APP统计明细计划列表查询
     * @param request
     * @return
     */
    ChannelReconciliationResponse selectAppChannelReconciliationRecordHjh(ChannelReconciliationRequest request);

    /**
     * APP统计明细计划列表查询
     * @param request
     * @return
     */
    ChannelReconciliationResponse selectAppChannelReconciliationRecordHjhCount(ChannelReconciliationRequest request);

    /**
     * APP统计明细计划列表数量查询
     * @param request
     * @return
     */
    ChannelReconciliationResponse selectAppChannelReconciliationCount(ChannelReconciliationRequest request);
    /**
     * 获取app渠道列表
     * @return
     */
    List<UtmPlatVO> getAppUtm();

    /**
     * 获取版本管理详情
     * @param request
     * @return
     */
    SubmissionsVO getSubmissionsRecord(SubmissionsRequest request);

     SubmissionsResponse findSubmissionsList(SubmissionsRequest form);

     SubmissionsResponse updateSubmissionsStatus(SubmissionsRequest form);

     SubmissionsResponse exportSubmissionsList(SubmissionsRequest form);


     VersionConfigBeanResponse searchList(VersionConfigBeanRequest request);

     VersionConfigBeanResponse searchInfo(VersionConfigBeanRequest request);

     VersionConfigBeanResponse insertInfo(VersionConfigBeanRequest request);

     VersionConfigBeanResponse updateInfo(VersionConfigBeanRequest request);

     VersionConfigBeanResponse deleteInfo(VersionConfigBeanRequest request);
    /**
     * 获取广告管理列表数据
     * @param appBorrowImageRequest
     * @return
     */
    AppBorrowImageResponse searchList(AppBorrowImageRequest appBorrowImageRequest);
    /**
     *获取广告管理列表获取详情
     * @param appBorrowImageRequest
     * @return
     */
    AppBorrowImageResponse searchInfo(AppBorrowImageRequest appBorrowImageRequest);
    /**
     *插入广告管理列表
     * @param appBorrowImageRequest
     * @return
     */
    AppBorrowImageResponse insertInfo(AppBorrowImageRequest appBorrowImageRequest);
    /**
     *修改广告管理列表
     * @param appBorrowImageRequest
     * @return
     */
    AppBorrowImageResponse updateInfo(AppBorrowImageRequest appBorrowImageRequest);
    /**
     *删除广告管理列表
     * @param appBorrowImageRequest
     * @return
     */
    AppBorrowImageResponse deleteInfo(AppBorrowImageRequest appBorrowImageRequest);

    AppBannerResponse findAppBannerList(AppBannerRequest request);

    AppBannerResponse insertAppBannerList(AdsVO adsVO);

    AppBannerResponse updateAppBannerList(AdsVO adsVO);

    AppBannerResponse updateAppBannerStatus(AdsVO adsVO);

    AppBannerResponse deleteAppBanner(AdsVO adsVO);
    /**
     * 根据id获取广告
     * @param adsVO
     * @return
     */
    AppBannerResponse getRecordById(AdsVO adsVO);


    /**
     * 获取保证金配置总数
     *
     * @param request
     * @return
     */
    Integer selectBailConfigCount(BailConfigRequest request);

    /**
     * 获取保证金配置列表
     *
     * @param request
     * @return
     */
    List<BailConfigCustomizeVO> selectBailConfigRecordList(BailConfigRequest request);
    IntegerResponse countBailConfigRecordList(BailConfigRequest request);

    /**
     * 获取锁定账户列表
     * @auth cuiguangqiang
     * @param request
     * @param isFront：是否前台
     * @return
     */
    LockedUserMgrResponse getLockedUserList(LockedeUserListRequest request, boolean isFront);

    /**
     * 解锁账户
     * @param vo
     * @param isFront
     * @return
     */
    BooleanResponse unlock(LockedUserInfoVO vo, boolean isFront);

    /**
     * 前台用户锁定配置
     * @return
     */
    LockedConfig.Config getFrontLockedCfg();

    /**
     * 后台用户锁定配置
     * @return
     */
    LockedConfig.Config getAdminLockedCfg();

    /**
     * 保存前台用户锁定配置
     * @param webConfig
     * @return
     */
    BooleanResponse saveFrontConfig(LockedConfig.Config webConfig);


    /**
     * 保存后台用户锁定配置
     * @param adminConfig
     * @return
     */
    BooleanResponse saveAdminConfig(LockedConfig.Config adminConfig);

    /**
     * 更新当前机构可用的还款方式并返回最新保证金详情
     *
     * @param id
     * @return
     */
    BailConfigInfoCustomizeVO updateSelectBailConfigById(Integer id);

    /**
     * 未配置保证金的机构编号
     *
     * @return
     */
    List<HjhInstConfigVO> selectNoUsedInstConfigList();

    /**
     * 添加保证金配置
     *
     * @param bailConfigAddRequest
     * @return
     */
    boolean insertBailConfig(BailConfigAddRequest bailConfigAddRequest);

    /**
     * 周期内发标已发额度
     *
     * @param bailConfigAddRequest
     * @return
     */
    String selectSendedAccountByCyc(BailConfigAddRequest bailConfigAddRequest);

    /**
     * 根据该机构可用还款方式更新可用授信方式
     *
     * @param instCode
     * @return
     */
    boolean updateBailInfoDelFlg(String instCode);

    /**
     * 更新保证金配置
     *
     * @param bailConfigAddRequest
     * @return
     */
    boolean updateBailConfig(BailConfigAddRequest bailConfigAddRequest);

    /**
     * 删除保证金配置
     *
     * @param bailConfigAddRequest
     * @return
     */
    boolean deleteBailConfig(BailConfigAddRequest bailConfigAddRequest);

    /**
     * 获取当前机构可用还款方式
     * 
     * @param instCode
     * @return
     */
    List<String> selectRepayMethod(String instCode);

    /**
     * 获取保证金配置日志总数
     *
     * @param request
     * @return
     */
    Integer selectBailConfigLogCount(BailConfigLogRequest request);

    /**
     * 获取保证金配置日志列表
     *
     * @param request
     * @return
     */
    List<BailConfigLogCustomizeVO> selectBailConfigLogList(BailConfigLogRequest request);

    /**
     * 查询异常标的总件数
     *
     * @param request
     * @return
     */
    Integer selectAssetExceptionCount(AssetExceptionRequest request);

    /**
     * 查询异常标的列表
     *
     * @param request
     * @return
     */
    List<AssetExceptionCustomizeVO> selectAssetExceptionList(AssetExceptionRequest request);

    /**
     * 导出异常标的列表
     *
     * @param request
     * @return
     */
    List<AssetExceptionCustomizeVO> exportAssetExceptionList(AssetExceptionRequest request);

    /**
     * 插入异常标的并更新保证金
     *
     * @param assetExceptionRequest
     * @return
     */
    boolean insertAssetException(AssetExceptionRequest assetExceptionRequest);

    /**
     * 项目编号是否存在
     *
     * @param borrowNid
     * @return
     */
    String isExistsBorrow(String borrowNid);

    /**
     * 删除异常标的
     *
     * @param assetExceptionRequest
     * @return
     */
    boolean deleteAssetException(AssetExceptionRequest assetExceptionRequest);

    /**
     * 修改异常标的
     *
     * @param assetExceptionRequest
     * @return
     */
    boolean updateAssetException(AssetExceptionRequest assetExceptionRequest);

    /**
     * 处理平台转账
     * @auth sunpeikai
     * @param
     * @return
     */
    int updateHandRechargeRecord(PlatformTransferRequest platformTransferRequest);

    /**
     * 查询邮件配置列表数据
     *
     * @return
     */
    EmailRecipientResponse getRecordList(EmailRecipientRequest recipientRequest);
    /**
     * 查询邮件配置列表数据详情
     *
     * @return
     */
    EmailRecipientResponse getRecordById(EmailRecipientRequest recipientRequest);
    /**
     * 修改邮件配置
     * @return
     */
    EmailRecipientResponse updateEmailRecipient(EmailRecipientRequest recipientRequest);
    /**
     * 禁用邮件配置状态
     * @return
     */
    EmailRecipientResponse forbiddenAction(EmailRecipientRequest recipientRequest);
    /**
     * 添加邮件配置
     * @return
     */
    EmailRecipientResponse insertAction(EmailRecipientRequest recipientRequest);


    /**
     * 江西银行卡异常count
     * @auth sunpeikai
     * @param
     * @return
     */
    int getBindCardExceptionCount(BindCardExceptionRequest request);

    /**
     * 江西银行卡异常列表
     * @auth sunpeikai
     * @param
     * @return
     */
    List<BindCardExceptionCustomizeVO> searchBindCardExceptionList(BindCardExceptionRequest request);

    /**
     * 异常中心-江西银行卡异常-更新银行卡
     * @auth sunpeikai
     * @param
     * @return
     */
    void updateBindCard(BindCardExceptionRequest request);

    List<BankRepayFreezeOrgCustomizeVO> getBankRepayFreezeOrgList(RepayFreezeOrgRequest request);

    Integer getBankRepayFreezeOrgCount(RepayFreezeOrgRequest request);

    Integer deleteOrgFreezeLogById(Integer id);

    Integer deleteOrgFreezeLog(String orderId);

    List<BankRepayOrgFreezeLogVO> getBankRepayOrgFreezeLogList(String orderId, String borrowNid);

    /**
     * 移动客户端 - App 推送管理 列表
     * @param requestBean
     * @return
     * @Author : huanghui
     */
    AppPushManageResponse getPushManageList(AppPushManageRequestBean requestBean);

    /**
     * 移动客户端 - App 推送管理 添加
     * @param requestBean
     * @return
     * @Author : huanghui
     */
    AppPushManageResponse insterPushManage(AppPushManageRequestBean requestBean);

    /**
     * 移动客户端 - App 推送管理 更新
     * @param requestBean
     * @return
     * @Author : huanghui
     */
    boolean updatePushManage(AppPushManageRequestBean requestBean);

    /**
     * 移动客户端 - App 推送管理 删除
     * @param id
     * @return
     * @Author : huanghui
     */
    boolean deletePushManage(Integer id);

    /**
     * 根据ID获取单条记录的详细信息
     * @param id
     * @return
     * @Author: huanghui
     */
    AppPushManageResponse getAppPushManageInfoById(Integer id);

    /**
     * 根据ID 更新单条记录的状态
     * @param requestBean
     * @return
     * @Author : huanghui
     */
    boolean updatePushManageStatusById(AppPushManageRequestBean requestBean);

    /**
     * 查询千乐渠道散标数据
     * @return
     */
    DataSearchCustomizeResponse querySanList(DataSearchRequest dataSearchRequest);

    /**
     * 查询千乐渠道计划数据
     * @return
     */
    DataSearchCustomizeResponse queryPlanList(DataSearchRequest dataSearchRequest);
    /**
     * 查询千乐渠道全部数据
     * @return
     */
    DataSearchCustomizeResponse queryQianleList(DataSearchRequest dataSearchRequest);


    /**
     * 查询短信加固数据
     *
     * @param request
     * @return
     * @author xiehuili
     */
    SmsConfigResponse initSmsConfig(SmsConfigRequest request);


    /**
     * 节假日配置-列表查询
     * @param request
     * @return
     */
    AdminHolidaysConfigResponse getHolidaysConfigRecordList(AdminHolidaysConfigRequest request);

    /**
     * 节假日配置-画面迁移(含有id更新，不含有id添加)
     * @param request
     * @return
     */
    AdminHolidaysConfigResponse getInfoList(AdminHolidaysConfigRequest request);

    /**
     *节假日配置-添加活动信息
     * @param request
     * @return
     */
    AdminHolidaysConfigResponse insertHolidays(AdminHolidaysConfigRequest request);

    /**
     *节假日配置-修改活动维护信息
     * @param request
     * @return
     */
    AdminHolidaysConfigResponse updateHolidays(AdminHolidaysConfigRequest request);


    /**
     * 校验千乐验证码
     * @param mobile
     * @param code
 return
     */
    int onlyCheckMobileCode(String mobile, String code);


    int saveSmsCode(String mobile, String checkCode, String validCodeType, Integer status, String platform);

    /**
     * 查询app渠道统计数据
     * @param request
     * @return
     */
    AppUtmRegResponse getstatisticsList(AppChannelStatisticsDetailRequest request);
    /**
     * 导出app渠道统计数据
     * @param request
     * @return
     */
    AppUtmRegResponse exportStatisticsList(AppChannelStatisticsDetailRequest request);

    /**
     * 查询邀请明细列表
     * @param naMiMarketingRequest
     * @return
     */
    NaMiMarketingResponse getNaMiMarketingList(NaMiMarketingRequest naMiMarketingRequest);

    /**
     *业绩返现列表
     * @param naMiMarketingRequest
     * @return
     */
    NaMiMarketingResponse getPerformanceList(NaMiMarketingRequest naMiMarketingRequest);

    /**
     * 业绩返现详情列表
     * @param request
     * @return
     */
    NaMiMarketingResponse getPerformancInfo(NaMiMarketingRequest request);


    /**
     * 查询邀请人返现明细 列表
     * @param naMiMarketingRequest
     * @return
     */
    NaMiMarketingResponse  selectNaMiMarketingRefferList(NaMiMarketingRequest naMiMarketingRequest);

    IntegerResponse selectNaMiMarketingRefferCount(NaMiMarketingRequest request);
    IntegerResponse selectNaMiMarketingRefferTotalCount(NaMiMarketingRequest request);
    /**
     * 查询邀请人返现统计 列表
     * @param naMiMarketingRequest
     * @return
     */
    NaMiMarketingResponse selectNaMiMarketingRefferTotalList(NaMiMarketingRequest naMiMarketingRequest);

    NaMiMarketingResponse selectMonthList();

    /**
     * 根据条件查询符合条件的sourceId集合
     * @param sourceType
     * @return
     */
    List<Integer> searchUserIdList(int sourceType);

    /**
     * 纳觅返现活动-修改加入时间
     * @param borrowNid
     * @param nowTime
     */
    void updateJoinTime(String borrowNid, Integer nowTime);

    /**
     * 纳觅返现活动-活动有效期校验
     * @param activityId
     * @return
     */
    StringResponse checkActivityIfAvailable(Integer activityId);

    /**
     * 纳觅返现活动-保存返现数据
     * @param returnCashRequest
     */
    IntegerResponse saveReturnCash(ReturnCashRequest returnCashRequest);

    /**
     * 查询汇计划转让列表的求和
     * add by cwyang 2019-01-24
     * @param request
     * @return
     */
    MapResponse queryHjhDebtCreditTotal(HjhDebtCreditListRequest request);

    /**
     * 根据创建日期查询该天导入aleve的条数
     *
     * @param dualDate
     * @return
     */
    Integer countAleveByDualDate(String dualDate);

    /**
     * 根据创建日期查询该天导入eve的条数
     *
     * @param dualDate
     * @return
     */
    Integer countEveByDualDate(String dualDate);

    /**
     * 大屏运营部数据配置列表查询
     * @param request
     * @return
     */
    ScreenConfigVOResponse getScreenConfigList(ScreenConfigRequest request);

    /**
     * 大屏运营部数据配置数据新增
     * @param screenConfigVO
     * @return
     */
    int addScreenConfig(ScreenConfigVO screenConfigVO);

    /**
     * 大屏运营部数据配置数据详情
     * @param screenConfigVO
     * @return
     */
    ScreenConfigVO screenConfigInfo(ScreenConfigVO screenConfigVO);

    /**
     * 大屏运营部数据配置数据编辑/启用/禁用
     * @param screenConfigVO
     * @return
     */
    int updateScreenConfig(ScreenConfigVO screenConfigVO);

    /**
     * 坐席月任务配置列表查询
     * @param request
     * @return
     */
    CustomerTaskConfigVOResponse getCustomerTaskConfigList(CustomerTaskConfigRequest request);

    /**
     * 坐席月任务配置数据新增
     * @return
     */
    int addCustomerTaskConfig(CustomerTaskConfigVO customerTaskConfigVO);

    /**
     * 坐席月任务配置数据详情
     * @param customerTaskConfigVO
     * @return
     */
    CustomerTaskConfigVO customerTaskConfigInfo(CustomerTaskConfigVO customerTaskConfigVO);
    /**
     * 坐席月任务配置数据编辑/启用/禁用
     * @param customerTaskConfigVO
     * @return
     */
    int updateCustomerTaskConfig(CustomerTaskConfigVO customerTaskConfigVO);

    /**
     * 查询本月待回款用户信息
     * @return
     */
    RepayResponse findRepayUser(Integer startTime, Integer endTime, Integer currPage, Integer pageSize);

    /**
     *批量添加待回款用户信息
     * @param resultList
     */
    void addRepayUserList(List<RepaymentPlanVO> resultList);

    /**
     * 查询本月待会款是否已经统计
     * @return
     */
    IntegerResponse countRepayUserList();
    /**
     * 查询工作流配置
     * @param adminRequest
     * @return
     */
    WorkFlowConfigResponse selectWorkFlowConfigList(WorkFlowConfigRequest adminRequest);
    /**
     * 添加工作流配置
     * @param workFlowVO
     * @return
     */
    BooleanResponse insertWorkFlowConfig(WorkFlowVO workFlowVO);

    /**
     * 查询业务流程详情页面
     * @param id
     * @return
     */
    WorkFlowConfigResponse selectWorkFlowConfigInfo(int id);
    /**
     * 校验业务id是否存在
     * @param request
     * @return
     */
    BooleanResponse selectWorkFlowConfigByBussinessId(WorkFlowConfigRequest request);
    /**
     * 修改工作流配置业务流程
     * @param workFlowVO
     * @return
     */
    BooleanResponse updateWorkFlowConfig(WorkFlowVO workFlowVO);
    /**
     *  删除工作流配置业务流程
     * @param id
     * @return
     */
    BooleanResponse deleteWorkFlowConfigById(int id);
    /**
     *  查询邮件预警通知人
     * @param workFlowUserVO
     * @return
     */
    WorkFlowUserResponse selectUser(WorkFlowUserVO workFlowUserVO);

    List<WorkFlowVO> updateStatusBusinessName();

    boolean updateFlowStatus(Integer businessId);

    /**
     * 查询所有业务流程节点的用户
     * @return
     */
    List<WorkFlowUserVO> findWorkFlowNodeUserEmailAll();
    /**
     * 工作流查询所有用户角色
     * @return
     */
    AdminRoleResponse selectWorkFlowRoleList();

    /**
     * @Author walter.limeng
     * @Description //投屏数据修复，获取2019年3月1号，2号，3号的充值数据
     * @Date 15:24 2019-04-10
     * @Param [startIndex 开始标识, endIndex 结束表示]
     * @return java.util.List<com.hyjf.am.resquest.trade.ScreenDataBean>
     **/
    List<ScreenDataBean> getRechargeList(Integer startIndex, Integer endIndex);
    /**
     * @Author walter.tyy
     * @Description //投屏数据修复，获取2019年4月1号，2号，3号的计划退出回款
     * @Date 10:12 2019-04-11
     * @Param [paramMap]
     * @return java.util.List<com.hyjf.am.resquest.trade.ScreenDataBean>
     **/
    List<ScreenDataBean> getPlanRepayList(Integer startIndex, Integer endIndex);
    /**
     * @Author walter.tyy
     * @Description //投屏数据修复，获取2019年4月1号，2号，3号的散标承接
     * @Date 10:12 2019-04-11
     * @Param [paramMap]
     * @return java.util.List<com.hyjf.am.resquest.trade.ScreenDataBean>
     **/
    List<ScreenDataBean> getCreditTenderList(Integer startIndex, Integer endIndex);
    /**
     * @Author walter.tyy
     * @Description //投屏数据修复，获取2019年4月1号，2号，3号的计划投资
     * @Date 10:12 2019-04-11
     * @Param [paramMap]
     * @return java.util.List<com.hyjf.am.resquest.trade.ScreenDataBean>
     **/
    List<ScreenDataBean> getPlanTenderList(Integer startIndex, Integer endIndex);
    /**
     * @Author walter.limeng
     * @Description //投屏数据修复，获取2019年3月1号，2号，3号的提现数据
     * @Date 15:24 2019-04-10
     * @Param [startIndex 开始标识, endIndex 结束表示]
     * @return java.util.List<com.hyjf.am.resquest.trade.ScreenDataBean>
     **/
    List<ScreenDataBean> getWithdrawList(Integer startIndex, Integer endIndex);

    /**
     * @Author walter.limeng
     * @Description //投屏数据修复，获取2019年3月1号，2号，3号的还款成功数据
     * @Date 15:24 2019-04-10
     * @Param [startIndex 开始标识, endIndex 结束表示]
     * @return java.util.List<com.hyjf.am.resquest.trade.ScreenDataBean>
     **/
    List<ScreenDataBean> getBorrowRecoverList(Integer startIndex, Integer endIndex);

    /**
     * @Author walter.limeng
     * @Description //投屏数据修复，获取2019年3月1号，2号，3号的散标投资数据
     * @Date 15:24 2019-04-10
     * @Param [startIndex 开始标识, endIndex 结束表示]
     * @return java.util.List<com.hyjf.am.resquest.trade.ScreenDataBean>
     **/
    List<ScreenDataBean> getBorrowTenderList(Integer startIndex, Integer endIndex);

    /**
     * 五一活动获取竞猜列表
     * @param request
     * @return
     */
    ActivityUserGuessResponse getGuessList(ActivityUserGuessRequest request);

    /**
     * 五一活动获取奖励领取列表
     * @param rewardRequest
     * @return
     */
    ActivityUserRewardResponse getRewardList(ActivityUserRewardRequest rewardRequest);
    /**
     * 查询累计年华投资
     * @param newYearNineteenRequestBean
     * @return
     */
    NewYearActivityResponse selectInvestList(NewYearNineteenRequestBean newYearNineteenRequestBean);

    BorrowRepayInfoCurrentResponse getRepayInfoCurrentData(BorrowRepayInfoCurrentRequest requestBean);

    BorrowRepayInfoCurrentExportResponse getRepayInfoCurrentExportData(BorrowRepayInfoCurrentRequest requestBean);

    Integer getRepayInfoCurrentExportCount(BorrowRepayInfoCurrentRequest requestBean);

    /**
     * 查询累计年华投资
     * @param duibaOrderRequest
     * @return
     */
    DuibaOrderResponse findOrderList(DuibaOrderRequest duibaOrderRequest);

    /**
     * 同步处理中的订单信息（订单列表）
     *
     * @param orderId
     * @return
     */
    String orderSynchronization(Integer orderId);

    /**
     * 根据兑吧订单的兑吧订单号查询用户订单信息并发放优惠卷
     *
     * @param orderNum
     * @return
     */
    VirtualResultResponse selectCouponUserById(String orderNum);

    /**
     * 兑吧兑换结果通知接口（失败时设置订单无效）
     *
     * @param orderNum
     * @return
     */
    String activation(String orderNum, String errorMessage);

    /**
     * 兑吧兑换结果通知接口（成功设置优惠卷有效，更新虚拟商品充值状态为完成）
     *
     * @param orderNum
     * @return
     */
    String success(String orderNum);
}
