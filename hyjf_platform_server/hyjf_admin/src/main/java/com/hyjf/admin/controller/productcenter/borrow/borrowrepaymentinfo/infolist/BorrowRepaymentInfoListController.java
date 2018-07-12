package com.hyjf.admin.controller.productcenter.borrow.borrowrepaymentinfo.infolist;

import com.hyjf.admin.beans.BorrowRepaymentInfoBean;
import com.hyjf.admin.beans.BorrowRepaymentInfoListBean;
import com.hyjf.admin.beans.request.BorrowRepaymentInfoListRequestBean;
import com.hyjf.admin.beans.request.BorrowRepaymentInfoRequsetBean;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.util.ExportExcel;
import com.hyjf.admin.common.util.ShiroConstants;
import com.hyjf.admin.interceptor.AuthorityAnnotation;
import com.hyjf.admin.service.BorrowRepaymentInfoListService;
import com.hyjf.admin.service.BorrowRepaymentInfoService;
import com.hyjf.am.resquest.admin.BorrowRepaymentInfoListRequset;
import com.hyjf.am.resquest.admin.BorrowRepaymentInfoRequset;
import com.hyjf.am.vo.admin.BorrowRepaymentInfoCustomizeVO;
import com.hyjf.am.vo.admin.BorrowRepaymentInfoListCustomizeVO;
import com.hyjf.am.vo.user.HjhInstConfigVO;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.StringPool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author pangchengchao
 * @version BorrowRepaymentInfoListController, v0.1 2018/7/10 9:30
 */

@Api(value = "产品中心-汇直投-还款明细列表")
@RestController
@RequestMapping("/borrow/borrowrepaymentinfo/infolist")
public class BorrowRepaymentInfoListController {
    @Autowired
    private BorrowRepaymentInfoListService borrowRepaymentInfoListService;
    /** 查看权限 */
    public static final String PERMISSIONS = "borrowrepayment";
    /**
     * 画面初始化
     *
     * @param request
     */
    @ApiOperation(value = "还款明细列表", notes = "还款明细列表页面查询初始化")
    @PostMapping(value = "/searchAction")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    public AdminResult<BorrowRepaymentInfoListBean> searchAction(HttpServletRequest request, @RequestBody @Valid BorrowRepaymentInfoListRequestBean form) {
        BorrowRepaymentInfoListRequset copyForm=new BorrowRepaymentInfoListRequset();
        BeanUtils.copyProperties(form, copyForm);
        BorrowRepaymentInfoListBean bean = borrowRepaymentInfoListService.selectBorrowRepaymentInfoListList(copyForm);
        List<HjhInstConfigVO> hjhInstConfigList = this.borrowRepaymentInfoListService.selectHjhInstConfigByInstCode("-1");
        bean.setHjhInstConfigList(hjhInstConfigList);
        AdminResult<BorrowRepaymentInfoListBean> result=new AdminResult<BorrowRepaymentInfoListBean> ();
        result.setData(bean);
        return result;
    }


    /**
     * @Description 数据导出--还款计划
     * @Author pangchengchao
     * @Version v0.1
     * @Date
     */
    @ApiOperation(value = "数据导出--还款详情导出数据", notes = "带条件导出EXCEL")
    @PostMapping(value = "/exportAction")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_EXPORT)
    public void exportAction(HttpServletRequest request, HttpServletResponse response, @RequestBody @Valid BorrowRepaymentInfoListRequestBean form) throws Exception {
        BorrowRepaymentInfoListRequset copyForm=new BorrowRepaymentInfoListRequset();
        BeanUtils.copyProperties(form, copyForm);

        // 表格sheet名称
        String sheetName = "还款详情导出数据";
        // 文件名称
        String fileName = sheetName + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date()) + CustomConstants.EXCEL_EXT;
        // 查询
        List<BorrowRepaymentInfoListCustomizeVO> resultList = this.borrowRepaymentInfoListService.selectExportBorrowRepaymentInfoListList(copyForm);
        // 列头
        String[] titles = new String[] { "借款编号", "资产来源", "借款人ID","借款人用户名", "借款标题", "项目类型", "借款期限",
                "年化收益", "借款金额", "借到金额", "还款方式", "还款期次", "投资人用户名", "投资人ID", "投资金额", "应还本金",
                "应还利息", "应还本息", "还款服务费", "提前天数", "少还利息", "延期天数", "延期利息", "逾期天数", "逾期利息",
                "应还总额", "还款订单号", "实还总额", "还款状态", "实际还款日期", "应还日期" };
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = ExportExcel.createHSSFWorkbookTitle(workbook, titles, sheetName + "_第1页");

        if ( resultList!= null && resultList.size() > 0) {

            int sheetCount = 1;
            int rowNum = 0;

            for (int i = 0; i < resultList.size(); i++) {
                rowNum++;
                if (i != 0 && i % 60000 == 0) {
                    sheetCount++;
                    sheet = ExportExcel.createHSSFWorkbookTitle(workbook, titles, (sheetName + "_第" + sheetCount + "页"));
                    rowNum = 1;
                }
                // 新建一行
                Row row = sheet.createRow(rowNum);
                // 循环数据
                for (int celLength = 0; celLength < titles.length; celLength++) {
                    BorrowRepaymentInfoListCustomizeVO record = resultList.get(i);

                    // 创建相应的单元格
                    Cell cell = row.createCell(celLength);

                    // 借款编号
                    if (celLength == 0) {
                        cell.setCellValue(record.getBorrowNid());
                    }
                    // 资产来源
                    if (celLength == 1) {
                        cell.setCellValue(record.getInstName());
                    }
                    // 借款人ID
                    else if (celLength == 2) {
                        cell.setCellValue(record.getUserId());
                    }
                    // 借款人用户名
                    else if (celLength == 3) {
                        cell.setCellValue(record.getBorrowUserName());
                    }
                    // 借款标题
                    else if (celLength == 4) {
                        cell.setCellValue(record.getBorrowName());
                    }
                    // 项目类型
                    else if (celLength == 5) {
                        cell.setCellValue(record.getProjectTypeName());
                    }
                    // 借款期限
                    else if (celLength == 6) {
                        cell.setCellValue(record.getBorrowPeriod() + "个月");
                    }
                    // 年化收益
                    else if (celLength == 7) {
                        cell.setCellValue(record.getBorrowApr() + "%");
                    }
                    // 借款金额
                    else if (celLength == 8) {
                        cell.setCellValue("".equals(record.getBorrowAccount()) ? 0 : Double.valueOf(record.getBorrowAccount()));
                    }
                    // 借到金额
                    else if (celLength == 9) {
                        cell.setCellValue("".equals(record.getBorrowAccountYes()) ? 0 : Double.valueOf(record.getBorrowAccountYes()));
                    }
                    // 还款方式
                    else if (celLength == 10) {
                        cell.setCellValue(record.getRepayType());
                    }
                    // 还款期次
                    else if (celLength == 11) {
                        cell.setCellValue("第" + record.getRecoverPeriod() + "期");
                    }
                    // 投资人用户名
                    else if (celLength == 12) {
                        cell.setCellValue(record.getRecoverUserName());
                    }
                    // 投资人ID
                    else if (celLength == 13) {
                        cell.setCellValue(record.getRecoverUserId());
                    }
                    // 投资金额
                    else if (celLength == 14) {
                        cell.setCellValue("".equals(record.getRecoverTotal()) ? 0 : Double.valueOf(record.getRecoverTotal()));
                    }
                    // 应还本金
                    else if (celLength == 15) {
                        cell.setCellValue("".equals(record.getRecoverCapital()) ? 0 : Double.valueOf(record.getRecoverCapital()));
                    }
                    // 应还利息
                    else if (celLength == 16) {
                        cell.setCellValue("".equals(record.getRecoverInterest()) ? 0 : Double.valueOf(record.getRecoverInterest()));
                    }
                    // 应还本息
                    else if (celLength == 17) {
                        cell.setCellValue("".equals(record.getRecoverAccount()) ? 0 : Double.valueOf(record.getRecoverAccount()));
                    }
                    // 管理费
                    else if (celLength == 18) {
                        cell.setCellValue("".equals(record.getRecoverFee()) ? 0 : Double.valueOf(record.getRecoverFee()));
                    }
                    // 提前天数
                    else if (celLength == 19) {
                        cell.setCellValue(record.getChargeDays());
                    }
                    // 少还利息
                    else if (celLength == 20) {
                        cell.setCellValue(StringUtils.isNotBlank(record.getChargeInterest()) ? record.getChargeInterest() : "0");
                    }
                    // 延期天数
                    else if (celLength == 21) {
                        cell.setCellValue(record.getDelayDays());
                    }
                    // 延期利息
                    else if (celLength == 22) {
                        cell.setCellValue(StringUtils.isNotBlank(record.getDelayInterest()) ? record.getDelayInterest() : "0");
                    }
                    // 逾期天数
                    else if (celLength == 23) {
                        cell.setCellValue(record.getLateDays());
                    }
                    // 逾期利息
                    else if (celLength == 24) {
                        cell.setCellValue(StringUtils.isNotBlank(record.getLateInterest()) ? record.getLateInterest() : "0");
                    }
                    // 应还总额
                    else if (celLength == 25) {
                        cell.setCellValue(StringUtils.isNotBlank(record.getRecoverAccount()) ? record.getRecoverAccount() : "0");
                    }
                    // 还款订单号
                    else if (celLength == 26) {
                        cell.setCellValue(record.getNid());
                    }
                    // 实还总额
                    else if (celLength == 27) {
                        cell.setCellValue(StringUtils.isNotBlank(record.getRecoverAccountYes()) ? record.getRecoverAccountYes() : "0");
                    }
                    // 还款状态
                    else if (celLength == 28) {
                        cell.setCellValue(record.getRepayType());
                    }
                    // 实际还款日期
                    else if (celLength == 29) {
                        cell.setCellValue(record.getRecoverActionTime());
                    }
                    // 应还日期
                    else if (celLength == 30) {
                        cell.setCellValue(record.getRecoverLastTime());
                    }
                }
            }
        }
        // 导出
        ExportExcel.writeExcelFile(response, workbook, titles, fileName);
    }
}
