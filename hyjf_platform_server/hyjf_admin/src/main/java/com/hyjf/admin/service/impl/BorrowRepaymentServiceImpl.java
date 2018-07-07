package com.hyjf.admin.service.impl;

import com.hyjf.admin.Utils.Page;
import com.hyjf.admin.beans.BorrowRepaymentBean;
import com.hyjf.admin.beans.DelayRepayInfoBean;
import com.hyjf.admin.client.BorrowRepaymentClient;
import com.hyjf.admin.client.HjhInstConfigClient;
import com.hyjf.admin.service.BorrowRepaymentService;
import com.hyjf.am.resquest.admin.BorrowRepaymentPlanRequest;
import com.hyjf.am.resquest.admin.BorrowRepaymentRequest;
import com.hyjf.am.vo.admin.AdminRepayDelayCustomizeVO;
import com.hyjf.am.vo.admin.BorrowRepaymentCustomizeVO;
import com.hyjf.am.vo.admin.BorrowRepaymentPlanCustomizeVO;
import com.hyjf.am.vo.trade.borrow.BorrowRepayPlanVO;
import com.hyjf.am.vo.trade.borrow.BorrowRepayVO;
import com.hyjf.am.vo.user.HjhInstConfigVO;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetDate;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.validator.GenericValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author pangchengchao
 * @version BorrowRepaymentServiceImpl, v0.1 2018/7/4 11:37
 */
@Service
public class BorrowRepaymentServiceImpl implements BorrowRepaymentService {

    @Autowired
    private HjhInstConfigClient hjhInstConfigClient;

    @Autowired
    private BorrowRepaymentClient borrowRepaymentClient;
    @Override
    public List<HjhInstConfigVO> selectHjhInstConfigByInstCode(String instCode) {
        List<HjhInstConfigVO> list = hjhInstConfigClient.selectHjhInstConfigByInstCode(instCode);
        return list;
    }

    @Override
    public BorrowRepaymentBean searchBorrowRepayment(BorrowRepaymentRequest request) {

        BorrowRepaymentBean bean=new BorrowRepaymentBean();
        Integer count = this.borrowRepaymentClient.countBorrowRepayment(request);
        Page page = Page.initPage(request.getCurrPage(), request.getPageSize());
        page.setTotal(count);
        request.setLimitStart(page.getOffset());
        request.setLimitEnd(page.getLimit());
        if (count != null && count > 0) {
            List<BorrowRepaymentCustomizeVO> recordList = this.borrowRepaymentClient.selectBorrowRepaymentList(request);
            bean.setRecordList(recordList);
            BorrowRepaymentCustomizeVO sumObject = this.borrowRepaymentClient.sumBorrowRepaymentInfo(request);
            bean.setSumObject(sumObject);
        }else{
            bean.setSumObject(new BorrowRepaymentCustomizeVO());
            bean.setRecordList(new ArrayList<BorrowRepaymentCustomizeVO>());
        }
        return bean;
    }

    @Override
    public List<BorrowRepaymentPlanCustomizeVO> exportRepayClkActBorrowRepaymentInfoList(BorrowRepaymentPlanRequest request) {
        return  this.borrowRepaymentClient.exportRepayClkActBorrowRepaymentInfoList(request);
    }

    @Override
    public List<BorrowRepaymentCustomizeVO> selectBorrowRepaymentList(BorrowRepaymentRequest request) {
        return this.borrowRepaymentClient.selectBorrowRepaymentList(request);
    }

    @Override
    public DelayRepayInfoBean getDelayRepayInfo(String borrowNid) {
        DelayRepayInfoBean bean=new DelayRepayInfoBean();
        AdminRepayDelayCustomizeVO repayDelay = this.borrowRepaymentClient.selectBorrowInfo(borrowNid);
        bean.setBorrowRepayInfo(repayDelay);
        // 单期标
        if (CustomConstants.BORROW_STYLE_ENDDAY.equals(repayDelay.getBorrowStyle()) || CustomConstants.BORROW_STYLE_END.equals(repayDelay.getBorrowStyle())) {
            BorrowRepayVO borrowRepay = this.borrowRepaymentClient.getBorrowRepayDelay(borrowNid, repayDelay.getBorrowApr(), repayDelay.getBorrowStyle());
            bean.setRepayInfo(borrowRepay);
            bean.setRepayTime(GetDate.formatDate(Long.valueOf(borrowRepay.getRepayTime()) * 1000L));
            bean.setDelayDays(borrowRepay.getDelayDays());
        } else {
            BorrowRepayPlanVO borrowRepayPlan = this.borrowRepaymentClient.getBorrowRepayPlanDelay(borrowNid, repayDelay.getBorrowApr(), repayDelay.getBorrowStyle());
            bean.setRepayInfo(borrowRepayPlan);
            bean.setRepayTime(GetDate.formatDate(Long.valueOf(borrowRepayPlan.getRepayTime()) * 1000L));
            bean.setDelayDays(borrowRepayPlan.getDelayDays());
        }
        return bean;
    }

    @Override
    public DelayRepayInfoBean updateBorrowRepayDelayDays(String borrowNid, String delayDays, String repayTime) {
        DelayRepayInfoBean bean=new DelayRepayInfoBean();
        boolean afterDayFlag = validateSignlessNum("delayDays", delayDays, 1, true);
        boolean updateFlag=false;
        if (afterDayFlag) {
            if (!(Integer.valueOf(delayDays) >= 1 && Integer.valueOf(delayDays) <= 8)) {
                updateFlag=true;
            } else {
                // 延期日8天后、应该还款的日期
                Integer lastReapyTime = Integer.valueOf(repayTime) + (8 * 24 * 3600);
                // 延期日加上输入的天数后、应该还款的日期
                Integer nowTimePlusDelayDays = Integer.valueOf(repayTime) + (Integer.valueOf(delayDays) * 24 * 3600);
                if (nowTimePlusDelayDays > lastReapyTime) {
                    String repayTimeForMsg = GetDate.formatDate(Long.valueOf(lastReapyTime) * 1000);
                    bean.setRepayTimeForMsg(repayTimeForMsg);
                    updateFlag=true;
                }
            }
        }

        if (updateFlag) {
            bean.setDelayDays(Integer.parseInt(delayDays));
            return bean;
        }

        this.borrowRepaymentClient.updateBorrowRepayDelayDays(borrowNid, delayDays);
        bean.setSuccess("success");
        return bean;
    }
    /**
     * 检查半角数字最大长度（无小数点）正整数
     *
     * @param itemname
     * @param value
     * @param required
     * @return
     */
    public static boolean validateSignlessNum( String itemname, String value, int maxLength,
                                              boolean required) {
        boolean retValue = validateMaxLength(itemname, value, maxLength, required);

        if (retValue && !StringUtils.isEmpty(value)) {
            if (!GenericValidator.isInt(value) || !NumberUtils.isNumber(value) || Integer.valueOf(value) < 0) {
                retValue = false;
            }
        }
        return retValue;
    }
    /**
     * 检查最大文字数
     *
     * @param itemname
     * @param value
     * @param maxlength
     * @param required
     * @return
     */
    public static boolean validateMaxLength(String itemname, String value, int maxlength,
                                            boolean required) {
        boolean retValue = true;
        if (required) {
            retValue = validateRequired(itemname, value);
        }

        if (retValue) {
            if (value != null && maxlength > 0) {
                try {
                    retValue = GenericValidator.maxLength(value, maxlength);
                    if (!retValue) {
                        retValue = false;
                    }
                } catch (Exception e) {
                    retValue = false;
                }
            }
        }
        return retValue;
    }
    /**
     * 必须输入项目
     *
     * @param itemname
     * @param value
     * @return true:正常 false:错误
     */
    public static boolean validateRequired( String itemname, String value) {
        if (StringUtils.isEmpty(value)) {
            return false;
        }
        return true;
    }
}