package com.hyjf.admin.service.impl;

import com.hyjf.admin.Utils.Page;
import com.hyjf.admin.beans.BorrowRepaymentInfoBean;
import com.hyjf.admin.client.BorrowRepaymentClient;
import com.hyjf.admin.client.BorrowRepaymentInfoClient;
import com.hyjf.admin.client.HjhInstConfigClient;
import com.hyjf.admin.service.BorrowRepaymentInfoService;
import com.hyjf.am.resquest.admin.BorrowRepaymentInfoRequset;
import com.hyjf.am.vo.admin.BorrowRepaymentInfoCustomizeVO;
import com.hyjf.am.vo.user.HjhInstConfigVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author pangchengchao
 * @version BorrowRepaymentInfoServiceImpl, v0.1 2018/7/7 14:23
 */
@Service
public class BorrowRepaymentInfoServiceImpl implements BorrowRepaymentInfoService {

    @Autowired
    private HjhInstConfigClient hjhInstConfigClient;

    @Autowired
    private BorrowRepaymentInfoClient borrowRepaymentInfoClient;
    @Override
    public List<HjhInstConfigVO> selectHjhInstConfigByInstCode(String instCode) {
        List<HjhInstConfigVO> list = hjhInstConfigClient.selectHjhInstConfigByInstCode(instCode);
        return list;
    }

    @Override
    public BorrowRepaymentInfoBean selectBorrowRepaymentInfoListForView(BorrowRepaymentInfoRequset request) {
        // 默认当天
        Date endDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        request.setYesTimeStartSrch(simpleDateFormat.format(DateUtils.addDays(endDate, 0)));
        request.setYesTimeEndSrch(simpleDateFormat.format(DateUtils.addDays(endDate, 0)));
        //modify by cwyang 搜索条件存在标的号时，不加时间限制  20180510
        if(StringUtils.isNotBlank(request.getBorrowNid())){
            request.setYesTimeStartSrch(null);
            request.setYesTimeEndSrch(null);
        }

        if(request.getYesTimeStartSrch() != null&&!"".equals(request.getYesTimeStartSrch())) {
            Date date;
            try {
                date = simpleDateFormat.parse(request.getYesTimeStartSrch());

                request.setYesTimeStartSrch(String.valueOf(date.getTime()/1000));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        if(request.getYesTimeEndSrch() != null&&!"".equals(request.getYesTimeStartSrch())) {
            Date date;
            try {
                date = simpleDateFormat.parse(request.getYesTimeEndSrch());
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                cal.add(Calendar.DATE, 1);

                request.setYesTimeEndSrch(String.valueOf(cal.getTime().getTime()/1000));
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        BorrowRepaymentInfoBean bean=new BorrowRepaymentInfoBean();
        Integer count = this.borrowRepaymentInfoClient.countBorrowRepaymentInfo(request);
        Page page = Page.initPage(request.getCurrPage(), request.getPageSize());
        page.setTotal(count);
        request.setLimitStart(page.getOffset());
        request.setLimitEnd(page.getLimit());
        bean.setTotal(count);
        if (count != null && count > 0) {
            // 将列表查询与导出查询独立区分
            List<BorrowRepaymentInfoCustomizeVO> recordList = this.borrowRepaymentInfoClient.selectBorrowRepaymentInfoListForView(request);
            bean.setRecordList(recordList);
            BorrowRepaymentInfoCustomizeVO sumObject = this.borrowRepaymentInfoClient.sumBorrowRepaymentInfo(request);
            bean.setSumObject(sumObject);
        }

        return bean;
    }

    @Override
    public List<BorrowRepaymentInfoCustomizeVO> selectBorrowRepaymentList(BorrowRepaymentInfoRequset copyForm) {
        return this.borrowRepaymentInfoClient.selectBorrowRepaymentInfoList(copyForm);
    }
}