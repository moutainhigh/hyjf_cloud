package com.hyjf.admin.controller.productcenter.plancenter.reinvestdetail;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.hyjf.admin.beans.request.HjhReInvestDetailRequestBean;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.result.BaseResult;
import com.hyjf.admin.common.result.ListResult;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.service.HjhReInvestDetailService;
import com.hyjf.admin.utils.exportutils.DataSet2ExcelSXSSFHelper;
import com.hyjf.admin.utils.exportutils.IValueFormatter;
import com.hyjf.am.response.admin.HjhReInvestDetailResponse;
import com.hyjf.am.resquest.admin.HjhReInvestDetailRequest;
import com.hyjf.am.vo.trade.hjh.HjhReInvestDetailVO;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.StringPool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 产品中心 --> 汇计划 --> 资金计划 -> 复投原始标的
 * @Author : huanghui
 */
@Api(value = "产品中心-汇计划-资金计划",tags ="产品中心-汇计划-资金计划-复投原始标的")
@RestController
@RequestMapping(value = "/hyjf-admin/hjhReInvestDetail")
public class HjhReInvestDetailController extends BaseController {

    @Autowired
    private HjhReInvestDetailService hjhReInvestDetailService;

    @ApiOperation(value = "复投原始标的检索下拉框", notes = "复投原始标的检索下拉框")
    @RequestMapping(value = "/dropDownBox", method = RequestMethod.GET)
    public JSONObject dropDownBox(){
        JSONObject jsonObject = new JSONObject();

        //初始投资方式List
        List<Object> investmentMethodList = new ArrayList<>();

        //初始投资方式Map
        Map<String, Object> investmentMethodListMap1 = new HashedMap();
        Map<String, Object> investmentMethodListMap2 = new HashedMap();
        Map<String, Object> investmentMethodListMap3 = new HashedMap();

        // 设置key value
        investmentMethodListMap1.put("key", 0);
        investmentMethodListMap1.put("value", "手动投标");
        investmentMethodListMap2.put("key", 1);
        investmentMethodListMap2.put("value", "预约投标");
        investmentMethodListMap3.put("key", 2);
        investmentMethodListMap3.put("value", "自动投标");

        // 将Map加入List
        investmentMethodList.add(investmentMethodListMap1);
        investmentMethodList.add(investmentMethodListMap2);
        investmentMethodList.add(investmentMethodListMap3);

        // 初始化还款方式List
        List<Object> repaymentList = new ArrayList<>();

        // 初始化还款方式Map
        Map<String, Object> repaymentMap = new HashedMap();
        Map<String, Object> repaymentMap1 = new HashedMap();
        Map<String, Object> repaymentMap2 = new HashedMap();
        Map<String, Object> repaymentMap3 = new HashedMap();
        Map<String, Object> repaymentMap4 = new HashedMap();

        repaymentMap.put("key", "按天计息，到期还本还息");
        repaymentMap.put("value", "按天计息，到期还本还息");
        repaymentMap1.put("key", "按月计息，到期还本还息");
        repaymentMap1.put("value", "按月计息，到期还本还息");
        repaymentMap2.put("key", "等额本息");
        repaymentMap2.put("value", "等额本息");
        repaymentMap3.put("key", "等额本金");
        repaymentMap3.put("value", "等额本金");
        repaymentMap4.put("key", "先息后本");
        repaymentMap4.put("value", "先息后本");

        repaymentList.add(repaymentMap);
        repaymentList.add(repaymentMap1);
        repaymentList.add(repaymentMap2);
        repaymentList.add(repaymentMap3);
        repaymentList.add(repaymentMap4);

        // Map 集
        Map<String, Object> allMap = new HashedMap();
        allMap.put("investmentMethodList", investmentMethodList);
        allMap.put("repaymentList", repaymentList);

        jsonObject.put("status", BaseResult.SUCCESS);
        jsonObject.put("statusDesc", BaseResult.SUCCESS_DESC);
        jsonObject.put("data", allMap);

        return jsonObject;
    }

    /**
     * 获取复投原始标的列表,初始化只需当前日期和planNid
     * @param requestBean
     * @return
     */
    @ApiOperation(value = "资金计划", notes = "复投详情列表")
    @PostMapping(value = "/hjhReInvest")
    public AdminResult<ListResult<HjhReInvestDetailVO>> hjhReInvestList(@RequestBody HjhReInvestDetailRequestBean requestBean){

        HjhReInvestDetailRequest reInvestDetailRequest = new HjhReInvestDetailRequest();
        BeanUtils.copyProperties(requestBean, reInvestDetailRequest);

        if (StringUtils.isEmpty(requestBean.getDate())){
            return new AdminResult<>(FAIL, "Date不能为空!");
        }

        if (StringUtils.isEmpty(requestBean.getPlanNid())){
            return new AdminResult<>(FAIL, "智投编号不能为空!");
        }

        // 初始化范湖List
        List<HjhReInvestDetailVO> returnList = new ArrayList<>();

        HjhReInvestDetailResponse investDetailResponse = this.hjhReInvestDetailService.getHjhReInvestDetailList(reInvestDetailRequest);

        if (investDetailResponse == null){
            return new AdminResult<>(FAIL, FAIL_DESC);
        }

//        if (!Response.isSuccess(reInvestDetailRequest)){
//            return new AdminResult<>(FAIL, investDetailResponse.getMessage());
//        }

        if (CollectionUtils.isNotEmpty(investDetailResponse.getResultList())){
            returnList = CommonUtils.convertBeanList(investDetailResponse.getResultList(), HjhReInvestDetailVO.class);
            return new AdminResult<ListResult<HjhReInvestDetailVO>>(ListResult.build(returnList, investDetailResponse.getCount()));
        }else {
            return new AdminResult<ListResult<HjhReInvestDetailVO>>(ListResult.build(returnList, 0));
        }
    }

    /**
     * 导出资金明细列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    /*@ApiOperation(value = "资金计划", notes = "复投详情列表导出")
    @PostMapping(value = "/exportAction")
    public void exportAction(HttpServletRequest request, HttpServletResponse response, HjhReInvestDetailRequestBean requestBean) throws Exception {

        HjhReInvestDetailRequest detailRequest = new HjhReInvestDetailRequest();
        BeanUtils.copyProperties(requestBean, detailRequest);
        // 表格sheet名称
        String sheetName = "资金明细";

        HjhReInvestDetailRequest hjhReInvestDetailCustomize = new HjhReInvestDetailRequest();

        hjhReInvestDetailCustomize.setAccedeOrderIdSrch(requestBean.getAccedeOrderIdSrch());
        hjhReInvestDetailCustomize.setBorrowNidSrch(requestBean.getBorrowNidSrch());
        hjhReInvestDetailCustomize.setBorrowStyleSrch(requestBean.getBorrowStyleSrch());
        hjhReInvestDetailCustomize.setInvestTypeSrch(requestBean.getInvestTypeSrch());
        hjhReInvestDetailCustomize.setLockPeriodSrch(requestBean.getLockPeriodSrch());
        hjhReInvestDetailCustomize.setUserNameSrch(requestBean.getUserNameSrch());
        hjhReInvestDetailCustomize.setBorrowStyleSrch(requestBean.getBorrowStyleSrch());
        hjhReInvestDetailCustomize.setInvestTypeSrch(requestBean.getInvestTypeSrch());
        hjhReInvestDetailCustomize.setPlanNid(requestBean.getPlanNid());
        hjhReInvestDetailCustomize.setDate(requestBean.getDate());
        // 取得数据
        List<HjhReInvestDetailVO> returnList = null;

        HjhReInvestDetailResponse recordList = this.hjhReInvestDetailService.getHjhReInvestDetailList(hjhReInvestDetailCustomize);

        if (recordList.getCount() > 0) {
            returnList = CommonUtils.convertBeanList(recordList.getResultList(), HjhReInvestDetailVO.class);
        }
        String fileName = URLEncoder.encode(sheetName, CustomConstants.UTF8) + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date()) + ".xls";

        String[] titles = new String[] { "序号","计划订单号","计划编号","用户名","推荐人","用户属性","借款编号","年化利率","借款期限","投资金额（元）","还款方式","投资方式","计息时间","投资时间" };
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();

        // 生成一个表格
        HSSFSheet sheet = ExportExcel.createHSSFWorkbookTitle(workbook, titles, sheetName + "_第1页");

        if (returnList != null && returnList.size() > 0) {

            int sheetCount = 1;
            int rowNum = 0;

            for (int i = 0; i < returnList.size(); i++) {
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
                    HjhReInvestDetailVO hjhReInvestDetail = returnList.get(i);

                    // 创建相应的单元格
                    Cell cell = row.createCell(celLength);

                    // 序号
                    if (celLength == 0) {
                        cell.setCellValue(i + 1);
                    }
                    // 计划订单号
                    else if (celLength == 1) {
                        cell.setCellValue(hjhReInvestDetail.getAccedeOrderId());
                    }
                    // 计划编号
                    else if (celLength == 2) {
                        cell.setCellValue(hjhReInvestDetail.getPlanNid());
                    }
                    // 用户名
                    else if (celLength == 3) {
                        cell.setCellValue(hjhReInvestDetail.getUserName());
                    }
                    // 推荐人
                    else if (celLength == 4) {
                        cell.setCellValue(hjhReInvestDetail.getInviteUserName());
                    }
                    // 用户属性
                    else if (celLength == 5) {
                        cell.setCellValue(hjhReInvestDetail.getUserAttribute());
                    }
                    // 借款编号
                    else if (celLength == 6) {
                        cell.setCellValue(hjhReInvestDetail.getBorrowNid());
                    }
                    // 年化利率
                    else if (celLength == 7) {
                        cell.setCellValue(hjhReInvestDetail.getExpectApr());
                    }
                    // 借款期限
                    else if (celLength == 8) {
                        cell.setCellValue(hjhReInvestDetail.getBorrowPeriod() + hjhReInvestDetail.getIsMonth());
                    }
                    // 投资金额（元）
                    else if (celLength == 9) {
                        cell.setCellValue(hjhReInvestDetail.getAccedeAccount());
                    }
                    // 还款方式
                    else if (celLength == 10) {
                        cell.setCellValue(hjhReInvestDetail.getBorrowStyle());
                    }
                    // 投资方式
                    else if (celLength == 11) {
                        cell.setCellValue(hjhReInvestDetail.getInvestType());
                    }
                    // 计息时间
                    else if (celLength == 12) {
                        cell.setCellValue(hjhReInvestDetail.getCountInterestTime());
                    }
                    // 投资时间
                    else if (celLength == 13) {
                        cell.setCellValue(hjhReInvestDetail.getAddTime());
                    }
                }
            }
        }
        // 导出
        ExportExcel.writeExcelFile(response, workbook, titles, fileName);

    }*/

    /**
     * 导出资金明细列表
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @ApiOperation(value = "资金计划", notes = "复投详情列表导出")
    @PostMapping(value = "/exportAction")
    public void exportAction(HttpServletRequest request, HttpServletResponse response, HjhReInvestDetailRequestBean requestBean) throws Exception {
        //sheet默认最大行数
        int defaultRowMaxCount = Integer.valueOf(systemConfig.getDefaultRowMaxCount());
        // 表格sheet名称
        String sheetName = "资金明细";
        // 文件名称
        String fileName = URLEncoder.encode(sheetName, CustomConstants.UTF8) + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date()) + ".xlsx";
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
        DataSet2ExcelSXSSFHelper helper = new DataSet2ExcelSXSSFHelper();

        HjhReInvestDetailRequest hjhReInvestDetailCustomize = new HjhReInvestDetailRequest();

        hjhReInvestDetailCustomize.setAccedeOrderIdSrch(requestBean.getAccedeOrderIdSrch());
        hjhReInvestDetailCustomize.setBorrowNidSrch(requestBean.getBorrowNidSrch());
        hjhReInvestDetailCustomize.setBorrowStyleSrch(requestBean.getBorrowStyleSrch());
        hjhReInvestDetailCustomize.setInvestTypeSrch(requestBean.getInvestTypeSrch());
        hjhReInvestDetailCustomize.setLockPeriodSrch(requestBean.getLockPeriodSrch());
        hjhReInvestDetailCustomize.setUserNameSrch(requestBean.getUserNameSrch());
        hjhReInvestDetailCustomize.setBorrowStyleSrch(requestBean.getBorrowStyleSrch());
        hjhReInvestDetailCustomize.setInvestTypeSrch(requestBean.getInvestTypeSrch());
        hjhReInvestDetailCustomize.setPlanNid(requestBean.getPlanNid());
        hjhReInvestDetailCustomize.setDate(requestBean.getDate());
        //请求第一页5000条
        hjhReInvestDetailCustomize.setPageSize(defaultRowMaxCount);
        hjhReInvestDetailCustomize.setCurrPage(1);
        // 需要输出的结果列表
        // 取得数据
        List<HjhReInvestDetailVO> resultList = null;

        HjhReInvestDetailResponse resultResponse = this.hjhReInvestDetailService.getHjhReInvestDetailList(hjhReInvestDetailCustomize);

        if (resultResponse.getCount() > 0) {
            resultList = CommonUtils.convertBeanList(resultResponse.getResultList(), HjhReInvestDetailVO.class);
        }


        Integer totalCount = resultResponse.getCount();

        int sheetCount = (totalCount % defaultRowMaxCount) == 0 ? totalCount / defaultRowMaxCount : totalCount / defaultRowMaxCount + 1;
        Map<String, String> beanPropertyColumnMap = buildMap();
        Map<String, IValueFormatter> mapValueAdapter = buildValueAdapter();
        String sheetNameTmp = sheetName + "_第1页";
        if (totalCount == 0) {

            helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter, new ArrayList());
        }else {
            helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter, resultList);
        }
        for (int i = 1; i < sheetCount; i++) {

            hjhReInvestDetailCustomize.setPageSize(defaultRowMaxCount);
            hjhReInvestDetailCustomize.setCurrPage(i + 1);
            HjhReInvestDetailResponse resultResponse2 = this.hjhReInvestDetailService.getHjhReInvestDetailList(hjhReInvestDetailCustomize);
            if (resultResponse2 != null && resultResponse2.getResultList().size()> 0) {
                sheetNameTmp = sheetName + "_第" + (i + 1) + "页";
                helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter,  resultResponse2.getResultList());
            } else {
                break;
            }
        }
        DataSet2ExcelSXSSFHelper.write2Response(request, response, fileName, workbook);
    }

    private Map<String, String> buildMap() {
        Map<String, String> map = Maps.newLinkedHashMap();
        map.put("accedeOrderId", "智投订单号");
        map.put("planNid", "智投编号");
        map.put("userName", "用户名");
        map.put("inviteUserName", "推荐人");
        map.put("userAttribute", "用户属性");
        map.put("borrowNid", "借款编号");
        map.put("expectApr", "年化利率");
        map.put("borrowPeriodView", "借款期限");
        map.put("accedeAccount", "投资金额（元）");
        map.put("borrowStyle", "还款方式");
        map.put("investType", "投资方式");
        map.put("countInterestTime", "开始计息时间");
        map.put("addTime", "投资时间");

        return map;
    }
    private Map<String, IValueFormatter> buildValueAdapter() {
        Map<String, IValueFormatter> mapAdapter = Maps.newHashMap();
        return mapAdapter;
    }

}
