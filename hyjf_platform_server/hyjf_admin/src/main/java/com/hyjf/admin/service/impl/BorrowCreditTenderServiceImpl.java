package com.hyjf.admin.service.impl;

import com.hyjf.admin.Utils.Page;
import com.hyjf.admin.beans.BorrowCreditRepayInfoResultBean;
import com.hyjf.admin.beans.BorrowCreditTenderResultBean;
import com.hyjf.admin.beans.request.BorrowCreditRepayRequest;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.util.ExportExcel;
import com.hyjf.admin.service.BorrowCreditTenderService;
import com.hyjf.am.response.trade.BorrowCreditRepayResponse;
import com.hyjf.am.response.trade.BorrowCreditTenderResponse;
import com.hyjf.am.resquest.admin.BorrowCreditRepayAmRequest;
import com.hyjf.am.vo.trade.borrow.BorrowCreditRepayInfoVO;
import com.hyjf.am.vo.trade.borrow.BorrowCreditRepayVO;
import com.hyjf.common.enums.MsgEnum;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.StringPool;
import com.hyjf.common.validator.CheckUtil;
import com.hyjf.cs.common.service.BaseClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class BorrowCreditTenderServiceImpl implements BorrowCreditTenderService {

    @Autowired
    private BaseClient baseClient;

    public static final String HZR_PREFIX = "HZR";

    public static final String BASE_URL = "http://AM-TRADE/am-trade/creditTender";

    public static final String LIST_URL = BASE_URL + "/getList";

    public static final String COUNT_URL = BASE_URL + "/getCount";

    public static final String SUM_URL = BASE_URL + "/getSum";

    public static final String REPAY_COUNT_URL = BASE_URL + "/getRepayCount";

    public static final String REPAY_LIST_URL = BASE_URL + "/getRepayList";

    public static final String REPAY_SUM_URL = BASE_URL + "/getRepaySum";

    /**
     * 查询还款信息列表
     *
     * @author zhangyk
     * @date 2018/7/11 14:34
     */
    @Override
    public AdminResult getBorrowCreditRepayList(BorrowCreditRepayRequest request) {
        AdminResult result = new AdminResult();
        BorrowCreditTenderResultBean bean = new BorrowCreditTenderResultBean();
        Page page = Page.initPage(request.getCurrPage(), request.getPageSize());
        BorrowCreditRepayAmRequest req = CommonUtils.convertBean(request, BorrowCreditRepayAmRequest.class);
        req.setLimitStart(page.getOffset());
        req.setLimitEnd(page.getLimit());
        BorrowCreditTenderResponse response = baseClient.postExe(COUNT_URL, req, BorrowCreditTenderResponse.class);
        Integer count = response.getCount();
        if (count > 0) {
            response = baseClient.postExe(LIST_URL, req, BorrowCreditTenderResponse.class);
            List<BorrowCreditRepayVO> list = response.getResultList();
            bean.setRecordList(list);
            response = baseClient.postExe(SUM_URL, req, BorrowCreditTenderResponse.class);
            bean.setSumData(response.getSumData());
        }
        bean.setTotal(count);
        result.setData(bean);
        return result;
    }

    @Override
    public void exprotBorrowCreditRepayList(BorrowCreditRepayRequest request, HttpServletResponse response) {
        BorrowCreditRepayAmRequest req = CommonUtils.convertBean(request, BorrowCreditRepayAmRequest.class);

        String sheetName = "还款信息列表";
        String[] titles = new String[]{"承接人", "债转编号", "出让人", "项目编号", "订单号", "应收本金", "应收利息", "应收本息", "已收本息", "还款服务费", "还款状态", "债权承接时间", "下次还款时间"};
        String fileName = sheetName + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date())
                + CustomConstants.EXCEL_EXT;

        // 导出列表不需要分页,扩大数据查询范围，使失效
        BorrowCreditTenderResponse res = baseClient.postExe(LIST_URL, req, BorrowCreditTenderResponse.class);
        List<BorrowCreditRepayVO> list = res.getResultList();
        if (CollectionUtils.isNotEmpty(list)) {
            // 特殊处理数据
            for (BorrowCreditRepayVO vo : list) {
                if ("0".equals(vo.getStatus())) {
                    vo.setStatus("还款中");
                } else {
                    vo.setStatus("已还款");
                }
                vo.setCreditNid(HZR_PREFIX + vo.getCreditNid());
            }
        }

        exportExcel(sheetName, fileName, titles, list, response);
    }

    @Override
    public AdminResult getBorrowCreditRepayInfoList(BorrowCreditRepayRequest request) {
        AdminResult result = new AdminResult();
        BorrowCreditRepayInfoResultBean bean = new BorrowCreditRepayInfoResultBean();
        CheckUtil.check(StringUtils.isNotBlank(request.getAssignNid()), MsgEnum.ERR_OBJECT_REQUIRED, "订单号");
        Page page = Page.initPage(request.getCurrPage(), request.getPageSize());
        BorrowCreditRepayAmRequest req = CommonUtils.convertBean(request, BorrowCreditRepayAmRequest.class);
        BorrowCreditRepayResponse response = baseClient.postExe(REPAY_COUNT_URL, req, BorrowCreditRepayResponse.class);
        Integer count = response.getCount();
        if (count > 0) {
            req.setLimitStart(page.getOffset());
            req.setLimitEnd(page.getLimit());
            response = baseClient.postExe(REPAY_LIST_URL, req, BorrowCreditRepayResponse.class);
            List<BorrowCreditRepayInfoVO> list = response.getResultList();
            bean.setRecordList(list);
            response = baseClient.postExe(REPAY_SUM_URL, req, BorrowCreditRepayResponse.class);
            Map<String, Object> sumVo = response.getSumData();
            bean.setSumData(sumVo);
        }
        bean.setTotal(count);
        result.setData(bean);
        return result;
    }


    // 生成excel
    private void exportExcel(String sheetName, String fileName, String[] titles, List<BorrowCreditRepayVO> resultList, HttpServletResponse response) {
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = ExportExcel.createHSSFWorkbookTitle(workbook, titles, sheetName + "_第1页");
        if (resultList != null && resultList.size() > 0) {
            int sheetCount = 1;
            int rowNum = 0;
            for (int i = 0; i < resultList.size(); i++) {
                rowNum++;
                if (i != 0 && i % 60000 == 0) {
                    sheetCount++;
                    sheet = ExportExcel
                            .createHSSFWorkbookTitle(workbook, titles, (sheetName + "_第" + sheetCount + "页"));
                    rowNum = 1;
                }

                // 新建一行
                Row row = sheet.createRow(rowNum);
                // 循环数据
                for (int celLength = 0; celLength < titles.length; celLength++) {
                    BorrowCreditRepayVO creditRepay = resultList.get(i);

                    // 创建相应的单元格
                    Cell cell = row.createCell(celLength);


                    // 承接人
                    if (celLength == 0) {
                        cell.setCellValue(creditRepay.getUserName());
                    }

                    // 债转编号
                    else if (celLength == 1) {
                        cell.setCellValue(creditRepay.getCreditNid());
                    }
                    // 出让人
                    else if (celLength == 2) {
                        cell.setCellValue(creditRepay.getCreditUserName());
                    }

                    // 项目编号
                    else if (celLength == 3) {
                        cell.setCellValue(creditRepay.getBidNid());
                    }
                    // 订单号
                    else if (celLength == 4) {
                        cell.setCellValue(creditRepay.getAssignNid());
                    }
                    // 应收本金
                    else if (celLength == 5) {
                        cell.setCellValue(creditRepay.getAssignCapital());
                    }
                    // 应收利息
                    else if (celLength == 6) {
                        cell.setCellValue(creditRepay.getAssignInterest());
                    }
                    // 应收本息
                    else if (celLength == 7) {
                        cell.setCellValue(creditRepay.getAssignAccount());
                    }
                    // 已收本息
                    else if (celLength == 8) {
                        cell.setCellValue(creditRepay.getAssignRepayAccount());
                    }
                    // 还款服务费
                    else if (celLength == 9) {
                        cell.setCellValue(creditRepay.getCreditFee());
                    }
                    // 还款状态
                    else if (celLength == 10) {
                        cell.setCellValue(creditRepay.getStatus());
                    }
                    // 债权承接时间
                    else if (celLength == 11) {
                        cell.setCellValue(creditRepay.getAddTime());
                    }
                    // 下次还款时间
                    else if (celLength == 12) {
                        cell.setCellValue(creditRepay.getAssignRepayNextTime());
                    }
                }
            }
        }
        // 导出
        ExportExcel.writeExcelFile(response, workbook, titles, fileName);

    }


}