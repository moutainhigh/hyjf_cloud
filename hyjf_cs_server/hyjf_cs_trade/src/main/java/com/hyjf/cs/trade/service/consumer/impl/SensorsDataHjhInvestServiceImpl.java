/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.trade.service.consumer.impl;

import com.hyjf.am.resquest.trade.SensorsDataBean;
import com.hyjf.am.vo.trade.borrow.BorrowTenderCpnVO;
import com.hyjf.am.vo.trade.coupon.CouponConfigVO;
import com.hyjf.am.vo.trade.coupon.CouponRealTenderVO;
import com.hyjf.am.vo.trade.coupon.CouponTenderVO;
import com.hyjf.am.vo.trade.coupon.CouponUserVO;
import com.hyjf.am.vo.trade.hjh.HjhAccedeVO;
import com.hyjf.am.vo.trade.hjh.HjhPlanVO;
import com.hyjf.am.vo.user.SpreadsUserVO;
import com.hyjf.am.vo.user.UserDepartmentInfoCustomizeVO;
import com.hyjf.am.vo.user.UserInfoVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.calculate.CalculatesUtil;
import com.hyjf.common.util.calculate.InterestInfo;
import com.hyjf.cs.common.service.BaseServiceImpl;
import com.hyjf.cs.trade.client.AmTradeClient;
import com.hyjf.cs.trade.client.AmUserClient;
import com.hyjf.cs.trade.config.SystemConfig;
import com.hyjf.cs.trade.service.consumer.SensorsDataHjhInvestService;
import com.sensorsdata.analytics.javasdk.SensorsAnalytics;
import com.sensorsdata.analytics.javasdk.exceptions.InvalidArgumentException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 神策数据统计:计划出借相关Service实现类
 *
 * @author liuyang
 * @version SensorsDataHjhInvestServiceImpl, v0.1 2018/10/23 9:44
 */
@Service
public class SensorsDataHjhInvestServiceImpl extends BaseServiceImpl implements SensorsDataHjhInvestService {

    @Autowired
    private AmUserClient amUserClient;

    @Autowired
    private AmTradeClient amTradeClient;

    @Autowired
    private SystemConfig systemConfig;

    /**
     * 发送神策数据
     *
     * @param sensorsDataBean
     * @throws IOException
     * @throws InvalidArgumentException
     */
    @Override
    public void sendSensorsData(SensorsDataBean sensorsDataBean) throws IOException, InvalidArgumentException {
        // log文件存放位置
        String logFilePath = systemConfig.getSensorsDataLogPath();
        // 初始化神策SDK
        SensorsAnalytics sa = new SensorsAnalytics(new SensorsAnalytics.ConcurrentLoggingConsumer(logFilePath + "sensorsData.log"));

        // 加入订单号
        String orderId = sensorsDataBean.getOrderId();
        if (StringUtils.isBlank(orderId)) {
            logger.error("加入订单号为空.");
            return;
        }
        // 事件类型
        String eventCode = sensorsDataBean.getEventCode();
        if (StringUtils.isBlank(eventCode)) {
            logger.error("事件类型为空.");
            return;
        }
        // 根据计划订单号查询计划加入订单
        HjhAccedeVO hjhAccede = this.amTradeClient.getHjhAccede(orderId);

        if (hjhAccede == null) {
            logger.error("根据加入订单号查询计划加入订单不存在,计划加入订单号:[" + orderId + "].");
            return;
        }

        // 出借人用户ID
        Integer userId = hjhAccede.getUserId();

        // 计划编号
        String planNid = hjhAccede.getPlanNid();

        // 根据计划编号查询计划详情
        HjhPlanVO hjhPlan = this.amTradeClient.getHjhPlan(planNid);

        if (hjhPlan == null) {
            logger.info("根据计划编号查询计划信息不存在,计划加入订单号:[" + orderId + "],计划编号:[" + planNid + "].");
            return;
        }
        // 预置属性
        Map<String, Object> presetProps = sensorsDataBean.getPresetProps();
        // 事件属性
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.putAll(presetProps);
        // 订单号
        properties.put("order_id", orderId);
        // 项目名称
        properties.put("project_name", StringUtils.isBlank(hjhPlan.getPlanName()) ? "" : hjhPlan.getPlanName());
        // 项目编号
        properties.put("project_id", planNid);
        // 项目期限
        if (hjhPlan.getIsMonth() == 0) {
            properties.put("project_duration", hjhPlan.getLockPeriod());
        } else {
            properties.put("project_duration", hjhPlan.getLockPeriod());
        }
        // 期限单位
        if (hjhPlan.getIsMonth() == 0) {
            properties.put("duration_unit", "天");
        } else {
            properties.put("duration_unit", "月");
        }

        // 平台类型
        if (hjhAccede.getClient() == 0) {
            properties.put("PlatformType", "PC");
        } else if (hjhAccede.getClient() == 1) {
            properties.put("PlatformType", "wechat");
        } else if (hjhAccede.getClient() == 2) {
            properties.put("PlatformType", "Android");
        } else if (hjhAccede.getClient() == 3) {
            properties.put("PlatformType", "iOS");
        }

        // 出借金额
        properties.put("tender_amount", hjhAccede.getAccedeAccount());
        // 历史回报率
        properties.put("project_apr", hjhAccede.getExpectApr().divide(new BigDecimal("100")));
        // 历史回报金额
        properties.put("expect_income", hjhAccede.getShouldPayInterest());
        // 支付方式
        properties.put("pay_method", "余额支付");

        // 获取出借时推荐人部门等信息
        // 根据用户ID 查询用户推荐人信息
        SpreadsUserVO spreadsUsers = this.amUserClient.querySpreadsUsersByUserId(userId);
        // 用户没有推荐人
        if (spreadsUsers == null) {
            // 注册时邀请人
            properties.put("inviter", "");
        } else {
            // 推荐人用户ID
            Integer spreadsUserId = spreadsUsers.getSpreadsUserId();
            // 推荐人用户名
            UserVO spreadsUser = this.amUserClient.findUserById(spreadsUserId);
            // 注册时邀请人
            properties.put("inviter", spreadsUser == null ? "" : spreadsUser.getUsername());
        }
        // 用户信息
        UserInfoVO usersInfo = this.amUserClient.getUserInfo(userId);
        // 用户属性
        if (usersInfo.getAttribute() == 0) {
            // 当前用户属性
            properties.put("attribute", "无主单");
        } else if (usersInfo.getAttribute() == 1) {
            // 当前用户属性
            properties.put("attribute", "有主单");
        } else if (usersInfo.getAttribute() == 2) {
            // 当前用户属性
            properties.put("attribute", "线下员工");
        } else if (usersInfo.getAttribute() == 3) {
            // 当前用户属性
            properties.put("attribute", "线上员工");
        }

        // 根据用户ID 查询用户部门信息
        UserDepartmentInfoCustomizeVO userDepartmentInfoCustomize = this.amUserClient.queryUserDepartmentInfoByUserId(userId);
        if (userDepartmentInfoCustomize == null) {
            // 分公司
            properties.put("regionName", "");
            // 分部
            properties.put("branchName", "");
            // 团队
            properties.put("departmentName", "");
        } else {
            // 注册时分公司
            properties.put("regionName", StringUtils.isBlank(userDepartmentInfoCustomize.getRegionName()) ? "" : userDepartmentInfoCustomize.getRegionName());
            // 注册时分部
            properties.put("branchName", StringUtils.isBlank(userDepartmentInfoCustomize.getBranchName()) ? "" : userDepartmentInfoCustomize.getBranchName());
            // 注册时团队
            properties.put("departmentName", StringUtils.isBlank(userDepartmentInfoCustomize.getDepartmentName()) ? "" : userDepartmentInfoCustomize.getDepartmentName());
        }

        // 根据加入订单号查询此笔加入是否使用优惠券
        CouponRealTenderVO couponRealTender = this.amTradeClient.selectCouponRealTenderByOrderId(orderId);
        if (couponRealTender != null) {
            // 优惠券出借ID
            String couponTenderId = couponRealTender.getCouponTenderId();
            logger.info("此笔加入使用了优惠券:优惠券出借ID:[" + couponTenderId + "],计划加入订单号:[" + orderId + "].");

            // 根据优惠券出借ID查询优惠券出借
            CouponTenderVO couponTender = this.amTradeClient.selectCouponTenderByCouponTenderId(couponTenderId);
            if (couponTender == null) {
                logger.error("根据优惠券出借ID获取优惠券出借情况失败,优惠券出借ID:[" + couponTenderId + "].");
                return;
            }
            // 优惠券ID
            Integer couponGrantId = couponTender.getCouponGrantId();

            // 根据优惠券ID查询优惠券信息
            CouponUserVO couponUser = this.amTradeClient.selectCouponUserById(couponGrantId);
            if (couponUser == null) {
                logger.error("根据优惠券ID查询优惠券信息失败,优惠券ID:[" + couponGrantId + "]");
                return;
            }
            // 优惠券编号
            String couponCode = couponUser.getCouponCode();
            // 获取优惠券配置信息
            CouponConfigVO couponConfig = this.amTradeClient.selectCouponConfig(couponCode);

            if (couponConfig == null) {
                logger.error("根据优惠券编号查询优惠券配置信息失败,优惠券信息:[" + couponCode + "].");
                return;
            }
            // 根据优惠券出借订单号查询优惠券出借信息
            BorrowTenderCpnVO borrowTenderCpn = this.amTradeClient.selectBorrowTenderCpnByCouponTenderId(couponRealTender.getCouponTenderId());

            if (borrowTenderCpn == null) {
                logger.error("根据优惠券出借订单号查询优惠券出借信息失败,优惠券出借订单号:[" + couponRealTender.getCouponTenderId() + "].");
                return;
            }

            // 优惠券类型
            if (couponConfig.getCouponType() == 3) {
                properties.put("coupon_type", "代金券");
            } else if (couponConfig.getCouponType() == 2) {
                properties.put("coupon_type", "加息券");
            } else {
                properties.put("coupon_type", "体验金");
            }

            // 利息
            BigDecimal interestTender = BigDecimal.ZERO;

            InterestInfo interestInfo = null;
            // 月利率(算出管理费用[上限])
            BigDecimal borrowMonthRate = BigDecimal.ZERO;
            // 月利率(算出管理费用[下限])
            BigDecimal borrowManagerScaleEnd = BigDecimal.ZERO;
            // 差异费率
            BigDecimal differentialRate = BigDecimal.ZERO;
            // 初审时间
            int borrowVerifyTime = 0;
            // 借款成功时间
            Integer borrowSuccessTime = hjhAccede.getCountInterestTime() == 0 ? GetDate.getNowTime10() : hjhAccede.getCountInterestTime();
            // 项目类型
            Integer projectType = 0;
            // 还款方式
            String borrowStyle = hjhPlan.getBorrowStyle();

            Integer borrowPeriod = hjhPlan.getLockPeriod();
            // 是否月标(true:月标, false:天标)
            boolean isMonth = CustomConstants.BORROW_STYLE_PRINCIPAL.equals(borrowStyle) || CustomConstants.BORROW_STYLE_MONTH.equals(borrowStyle)
                    || CustomConstants.BORROW_STYLE_ENDMONTH.equals(borrowStyle);
            //汇计划只支持按天和按月
            if (!borrowStyle.equals("endday")) {
                borrowStyle = "end";
            }
            try {
                // 体验金
                if (couponConfig.getCouponType() == 1) {
                    String tenderNid = borrowTenderCpn.getNid();
                    // 取得体验金收益期限
                    Integer couponProfitTime = this.amTradeClient.getCouponProfitTime(tenderNid);
                    // 计算体验金收益
                    BigDecimal interest = this.getInterestTYJ(borrowTenderCpn.getAccount(), hjhPlan.getExpectApr(), couponProfitTime);
                    // 体验金按项目期限还款
                    if (couponConfig.getRepayTimeConfig() == 1) {
                        // 计算利息
                        interestInfo = CalculatesUtil.getInterestInfo(borrowTenderCpn.getAccount(), hjhPlan.getLockPeriod(), hjhPlan.getExpectApr(), borrowStyle, borrowSuccessTime,
                                borrowMonthRate, borrowManagerScaleEnd, projectType, differentialRate, borrowVerifyTime);

                        // 体验金的项目如果是分期
                        if (isMonth) {
                            List<InterestInfo> listMonthly = interestInfo.getListMonthly();
                            // 取得最后一次分期的还款时间作为体验金的还款时间
                            interestInfo.setRepayTime(listMonthly.get(listMonthly.size() - 1).getRepayTime());
                        }
                    } else {
                        // 体验金按收益期限还款
                        interestInfo = new InterestInfo();
                        Integer repayTime = GetDate.countDate(borrowSuccessTime, 5, couponProfitTime);
                        interestInfo.setRepayTime(repayTime);
                    }

                    // 体验金收益
                    interestInfo.setRepayAccountInterest(interest);
                    interestTender = interestInfo.getRepayAccountInterest(); // 利息

                } else if (couponConfig.getCouponType() == 2) {
                    // 加息券
                    // 计算利息
                    interestInfo = CalculatesUtil.getInterestInfo(borrowTenderCpn.getAccount(), borrowPeriod, couponConfig.getCouponQuota(), borrowStyle,
                            borrowSuccessTime, borrowMonthRate, borrowManagerScaleEnd, projectType, differentialRate, borrowVerifyTime);
                    interestTender = interestInfo.getRepayAccountInterest(); // 利息
                } else if (couponConfig.getCouponType() == 3) {
                    // 代金券
                    // 计算利息
                    interestInfo = CalculatesUtil.getInterestInfo(borrowTenderCpn.getAccount(), borrowPeriod, hjhPlan.getExpectApr(), borrowStyle, borrowSuccessTime,
                            borrowMonthRate, borrowManagerScaleEnd, projectType, differentialRate, borrowVerifyTime);

                    interestTender = interestInfo.getRepayAccountInterest(); // 利息
                }
            } catch (Exception e) {
                logger.error(e.getMessage());
                logger.error("计算优惠券的利息错误,订单号:" + orderId + "].");
            }

            // 返红包比例
            properties.put("award_apr", BigDecimal.ZERO);
            // 预计加息金额
            properties.put("expect_add_apr_income", interestTender);
            // 项目期限限制
            properties.put("project_duration_limit", createProjectExpiration(couponConfig.getProjectExpirationLength(),
                    couponConfig.getProjectExpirationLengthMin(), couponConfig.getProjectExpirationLengthMax(), couponConfig.getProjectExpirationType(), hjhPlan.getIsMonth()));
            // 适用出借产品
            properties.put("product_suitable", createProjectTypeString(couponConfig.getProjectType()));
            // 出借金额限制
            properties.put("tender_amount_limit", createCouponTenderMoney(couponConfig.getTenderQuotaType(), couponConfig.getTenderQuotaMin(), couponConfig.getTenderQuotaMax(), couponConfig.getTenderQuota()));
            properties.put("coupon_createtime", couponUser.getCreateTime());
            properties.put("coupon_endtime", GetDate.getDateTimeMyTime(couponUser.getEndTime()));
            // 是否用券
            properties.put("use_coupon", true);
            // 优惠券编号
            properties.put("coupon_id", couponUser.getCouponUserCode());

            // 体验金面额
            if (couponConfig.getCouponType() == 1) {
                properties.put("coupon_denomination", couponConfig.getCouponQuota());
            }

            // 加息比例
            if (couponConfig.getCouponType() == 2) {
                properties.put("add_apr", couponConfig.getCouponQuota().divide(new BigDecimal("100")));
            }
            // 代金券面额
            if (couponConfig.getCouponType() == 3) {
                properties.put("expect_project_award_income", couponConfig.getCouponQuota());
            }

        } else {
            logger.info("此笔加入未使用优惠券");
            // 返红包比例
            properties.put("award_apr", BigDecimal.ZERO);
            // expect_add_apr_income
            properties.put("expect_add_apr_income", BigDecimal.ZERO);
            // 预计返红包金额
            properties.put("expect_project_award_income", BigDecimal.ZERO);
            // 项目期限限制
            properties.put("project_duration_limit", "");
            // 适用出借产品
            properties.put("product_suitable", "");
            // 出借金额限制
            properties.put("tender_amount_limit", "");
            // 是否用券
            properties.put("use_coupon", false);
            // 优惠券编号
            properties.put("coupon_id", "");
            // 体验金面额
            properties.put("coupon_denomination", BigDecimal.ZERO);
            // 加息比例
            properties.put("add_apr", BigDecimal.ZERO);
        }

        // 调用神策track事件
        sa.track(String.valueOf(userId), true, "submit_intelligent_invest", properties);
        sa.shutdown();
    }


    /**
     * 计算体验金收益
     *
     * @param account
     * @param borrowApr
     * @param couponProfitTime
     * @return
     */
    private BigDecimal getInterestTYJ(BigDecimal account, BigDecimal borrowApr, Integer couponProfitTime) {
        BigDecimal interest = BigDecimal.ZERO;
        // 保留两位小数（去位）
        // 应回款=体验金面值*出借标的年化/365*收益期限（体验金发行配置）
        interest = account.multiply(borrowApr.divide(new BigDecimal(100), 6, BigDecimal.ROUND_HALF_UP))
                .divide(new BigDecimal(365), 6, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal(couponProfitTime))
                .setScale(2, BigDecimal.ROUND_DOWN);
        return interest;
    }


    /**
     * 获取优惠券项目期限限定信息
     *
     * @param projectExpirationLength
     * @param projectExpirationLengthMin
     * @param projectExpirationLengthMax
     * @param projectExpirationType
     * @param isMonth
     * @return
     */
    private String createProjectExpiration(Integer projectExpirationLength, Integer projectExpirationLengthMin, Integer projectExpirationLengthMax, Integer projectExpirationType, Integer isMonth) {
        // 验证项目期限
        if (isMonth == 0) {
            if (projectExpirationType == 1) {
                return "适用" + projectExpirationLength + "个月的项目";
            } else if (projectExpirationType == 3) {
                return "适用≥" + projectExpirationLength + "个月的项目";
            } else if (projectExpirationType == 4) {
                return "适用≤" + projectExpirationLength + "个月的项目";
            } else if (projectExpirationType == 2) {
                return "适用" + projectExpirationLengthMin + "个月~" + projectExpirationLengthMax + "个月的项目";
            } else if (projectExpirationType == 0) {
                return "不限";
            }
        } else {
            if (projectExpirationType == 1) {
                return "适用" + projectExpirationLength + "个月的项目";
            } else if (projectExpirationType == 3) {
                return "适用≥" + projectExpirationLength + "个月的项目";
            } else if (projectExpirationType == 4) {
                return "适用≤" + projectExpirationLength + "个月的项目";
            } else if (projectExpirationType == 2) {
                return "适用" + projectExpirationLengthMin + "个月~" + projectExpirationLengthMax + "个月的项目";
            } else if (projectExpirationType == 0) {
                return "不限";
            }
        }
        return "不限";
    }


    /**
     * 优惠券项目限定
     *
     * @param projectType
     * @return
     */
    private String createProjectTypeString(String projectType) {
        String projectString = "";
        if (projectType.indexOf("-1") != -1) {
            projectString = "不限";
        } else {
            // 勾选汇直投，尊享汇，融通宝
            if (projectType.indexOf("1") != -1 && projectType.indexOf("4") != -1 && projectType.indexOf("7") != -1) {
                projectString = projectString + "散标,";
            }
            //勾选汇直投  未勾选尊享汇，融通宝
            if (projectType.indexOf("1") != -1 && projectType.indexOf("4") == -1 && projectType.indexOf("7") == -1) {
                projectString = projectString + "散标,";
            }
            //勾选汇直投，融通宝  未勾选尊享汇
            if (projectType.indexOf("1") != -1 && projectType.indexOf("4") == -1 && projectType.indexOf("7") != -1) {
                projectString = projectString + "散标,";
            }
            //勾选汇直投，选尊享汇 未勾选融通宝
            if (projectType.indexOf("1") != -1 && projectType.indexOf("4") != -1 && projectType.indexOf("7") == -1) {
                projectString = projectString + "散标,";
            }
            //勾选尊享汇，融通宝  未勾选直投
            if (projectType.indexOf("1") == -1 && projectType.indexOf("4") != -1 && projectType.indexOf("7") != -1) {
                projectString = projectString + "散标,";
            }
            //勾选尊享汇  未勾选直投，融通宝
            if (projectType.indexOf("1") == -1 && projectType.indexOf("4") != -1 && projectType.indexOf("7") == -1) {
                projectString = projectString + "散标,";
            }
            //勾选尊享汇  未勾选直投，融通宝
            if (projectType.indexOf("1") == -1 && projectType.indexOf("4") == -1 && projectType.indexOf("7") != -1) {
                projectString = projectString + "散标,";
            }

            if (projectType.indexOf("3") != -1) {
                projectString = projectString + "新手,";
            }

            if (projectType.indexOf("6") != -1) {
                projectString = projectString + "智投,";
            }
            projectString = org.apache.commons.lang3.StringUtils.removeEnd(projectString, ",");
        }
        return projectString;
    }


    /**
     * 获取优惠券出借金额限定
     *
     * @param tenderQuotaType
     * @param tenderQuotaMin
     * @param tenderQuotaMax
     * @param tenderQuota
     * @return
     */
    private String createCouponTenderMoney(Integer tenderQuotaType, Integer tenderQuotaMin, Integer tenderQuotaMax, Integer tenderQuota) {
        if (tenderQuotaType == 0 || tenderQuotaType == null) {
            return ("出借金额不限");
        } else if (tenderQuotaType == 1) {
            String tenderQuotaMinStr = tenderQuotaMin + "";
            if (tenderQuotaMin >= 10000 && tenderQuotaMin % 10000 == 0) {
                tenderQuotaMinStr = tenderQuotaMin / 10000 + "万";
            }
            String tenderQuotaMaxStr = tenderQuotaMax + "";
            if (tenderQuotaMax >= 10000 && tenderQuotaMax == 0) {
                tenderQuotaMaxStr = tenderQuotaMax / 10000 + "万";
            }
            return (tenderQuotaMinStr + "元~" + tenderQuotaMaxStr + "元可用");

        } else if (tenderQuotaType == 2) {
            String tenderQuotaStr = tenderQuota + "";
            if (tenderQuota >= 10000 && tenderQuota % 10000 == 0) {
                tenderQuotaStr = tenderQuota / 10000 + "万";
            }
            return ("满" + tenderQuotaStr + "元可用");
        }
        return "";
    }
}
