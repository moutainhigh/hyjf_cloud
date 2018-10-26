/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.controller.finance.merchant.transfer;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.hyjf.admin.beans.response.TransferResponse;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.util.ExportExcel;
import com.hyjf.admin.config.SystemConfig;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.service.CustomerTransferService;
import com.hyjf.admin.service.TransferService;
import com.hyjf.admin.utils.ConvertUtils;
import com.hyjf.admin.utils.exportutils.DataSet2ExcelSXSSFHelper;
import com.hyjf.admin.utils.exportutils.IValueFormatter;
import com.hyjf.am.response.admin.MerchantAccountResponse;
import com.hyjf.am.response.trade.account.MerchantTransferResponse;
import com.hyjf.am.resquest.admin.MerchantTransferListRequest;
import com.hyjf.am.vo.config.AdminSystemVO;
import com.hyjf.am.vo.trade.account.MerchantTransferVO;
import com.hyjf.common.cache.CacheUtil;
import com.hyjf.common.util.*;
import com.hyjf.pay.lib.chinapnr.ChinapnrBean;
import com.hyjf.pay.lib.chinapnr.util.ChinaPnrConstant;
import com.hyjf.pay.lib.chinapnr.util.ChinapnrUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhangqingqing
 * @version TransferController, v0.1 2018/7/6 17:51
 */

@Api(value = "资金中心-平台账户-子账户间转账",tags = "资金中心-平台账户-子账户间转账")
@RestController
@RequestMapping("/hyjf-admin/finance/transfer")
public class TransferController extends BaseController {

    @Autowired
    TransferService transferService;
    @Autowired
    CustomerTransferService customerTransferService;

    @Autowired
    SystemConfig systemConfig;
    /**
     * 用户转账列表
     *
     * @param
     * @param form
     * @return
     */
    @ApiOperation(value = "用户转账列表", notes = "用户转账列表")
    @PostMapping(value = "/transferList")
    public AdminResult init(@RequestBody MerchantTransferListRequest form) {
        TransferResponse response = new TransferResponse();
        Map<String, String> transferStatus = CacheUtil.getParamNameMap("TRANSFER_STATUS");
        Map<String, String> transferTypes = CacheUtil.getParamNameMap("TRANSFER_TYPE");
        // 转账状态
        response.setTransferStatus(ConvertUtils.convertParamMapToDropDown(transferStatus));
        // 交易类型
        response.setTransferTypes(ConvertUtils.convertParamMapToDropDown(transferTypes));

        MerchantAccountResponse merchantAccounts = transferService.selectMerchantAccountList(null);
        response.setMerchantAccountListOut(merchantAccounts.getResultList());
        response.setMerchantAccountListIn(merchantAccounts.getResultList());
        MerchantTransferResponse recordList = transferService.selectMerchantTransfer(form);
        if(recordList == null||recordList.getRecordTotal()==0) {
            new AdminResult(response);
        }
        response.setUserTransferVOList(recordList.getResultList());
        response.setTotal(recordList.getRecordTotal());
        return new AdminResult(response);
    }

    /**
     * 校验用户转账的结果
     *
     * @param param
     * @return 进入用户详情页面
     */
    @ApiOperation(value = "校验用户转账的结果", notes = "校验用户转账的结果")
    @ApiImplicitParam(name = "param", value = "{outAccountId:String,transferAmount:String}", dataType = "Map")
    @PostMapping(value = "/checkTransfer")
    public AdminResult checkTransfer(@RequestBody Map<String, String> param) {
        String outAccountId = param.get("outAccountId");
        String transferAmount = param.get("transferAmount");
        JSONObject ret = new JSONObject();
      this.transferService.checkMerchantTransfer(outAccountId,transferAmount, ret);
        return new AdminResult();
    }


    /**
     * 初始化用户转账画面
     *
     * @param request
     * @return 进入用户详情页面
     */
    @ApiOperation(value = "初始化用户转账画面")
    @PostMapping("/addTransfer")
    public AdminResult addTransfer(HttpServletRequest request, @RequestBody MerchantTransferListRequest form) {
        AdminResult result = new AdminResult();
        JSONObject ret = this.transferService.checkMerchantTransferParam(form);
        Map<String,Object> map = new HashMap<>();
        if (ret.containsKey("99")) {
            map.put("merchanttransferForm", form);
            // 子账户列表
            MerchantAccountResponse merchantAccountListOut = transferService.selectMerchantAccountList(0);
            MerchantAccountResponse merchantAccountListIn = transferService.selectMerchantAccountList(1);
            map.put("merchantAccountListOut", merchantAccountListOut);
            map.put("merchantAccountListIn", merchantAccountListIn);
            result.setData(map);
            return result;
        } else {
            // 生成订单
            String orderId = GetOrderIdUtils.getOrderId2(Integer.valueOf(form.getOutAccountId()));
            AdminSystemVO adminSystem = getUser(request);
            form.setCreateUserId(Integer.parseInt(adminSystem.getId()));
            form.setCreateUserName(adminSystem.getUsername());
            form.setOrderId(orderId);
            boolean flag = this.transferService.insertTransfer(form);
            if (flag) {
                //调用商户转账接口进行转账
                String custId=systemConfig.getMerCustId();
                ChinapnrBean chinapnrBean = new ChinapnrBean();
                // 接口版本号
                chinapnrBean.setVersion(ChinaPnrConstant.VERSION_10);
                // 消息类型(主动投标)
                chinapnrBean.setCmdId(ChinaPnrConstant.CMDID_TRANSFER);
                // 订单号(必须)
                chinapnrBean.setOrdId(orderId);
                // 出账客户号
                chinapnrBean.setOutCustId(custId);
                // 出账子账户
                chinapnrBean.setOutAcctId(form.getOutAccountCode());
                // 交易金额(必须)
                chinapnrBean.setTransAmt(CustomUtil.formatAmount(form.getTransferAmount().toString()));
                // 入账客户号
                chinapnrBean.setInCustId(custId);
                //入账子账户
                chinapnrBean.setInAcctId(form.getInAccountCode());
                // log用户
                chinapnrBean.setLogUserId(form.getCreateUserId());
                // 日志类型
                chinapnrBean.setType(ChinaPnrConstant.CMDID_TRANSFER);
                try {
                    // 发送请求获取结果
                    ChinapnrBean resultBean = ChinapnrUtil.callApiBg(chinapnrBean);
                    String respCode = resultBean == null ? "" : resultBean.getRespCode();
                    if (ChinaPnrConstant.RESPCODE_SUCCESS.equals(respCode)) {
                        boolean afterFlag = this.transferService.updateMerchantTransfer(orderId,1,null)>0?true:false;
                        if(afterFlag){
                            //转账成功
                            logger.info("转账成功,订单号："+orderId);
                            map.put("success","success");
                            result.setData(map);
                        }else{
                            //转账成功，更新状态失败
                           // ValidatorFieldCheckUtil.validateSpecialError(modelAndView, "transferAmount", "merchant.transfer.success","更新转账成功状态失败!");
                            map.put("99","更新转账成功状态失败");
                            map.put("merchanttransferForm", form);
                            result.setData(map);
                            logger.info("更新转账成功状态失败,订单号："+orderId);
                        }
                    } else {
                        String  respDesc = "";
                        if(resultBean != null&&StringUtils.isNotEmpty(resultBean.getRespDesc())){
                            respDesc = URLDecoder.decode(resultBean.getRespDesc(), "UTF-8");
                        }
                        boolean afterFlag = this.transferService.updateMerchantTransfer(orderId,2,"转账失败，失败原因:"+respDesc)>0?true:false;
                        if(afterFlag){
                            logger.info("转账失败,订单号："+orderId);
                        }else{
                            logger.info("更新转账失败状态失败,订单号："+orderId);
                        }
//                        ValidatorFieldCheckUtil.validateSpecialError(modelAndView, "transferAmount", "merchant.transfer.fail","调用子账户转账接口失败!");
//                        modelAndView.addObject("merchanttransferForm", form);
                        map.put("99","调用子账户转账接口失败");
                        map.put("merchanttransferForm", form);
                        result.setData(map);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    boolean afterFlag = this.transferService.updateMerchantTransfer(orderId,2,"调用子账户转账接口异常")>0?true:false;
                    if(afterFlag){
                        logger.info("调用子账户转账接口异常，转账失败,订单号："+orderId);
                    }else{
                        logger.info("调用子账户转账接口异常，更新转账失败状态失败,订单号："+orderId);
                    }
//                    ValidatorFieldCheckUtil.validateSpecialError(modelAndView, "transferAmount", "merchant.transfer.exception","调用子账户转账接口异常!");
//                    modelAndView.addObject("merchanttransferForm", form);
                    map.put("99","调用子账户转账接口异常");
                    map.put("merchanttransferForm", form);
                    result.setData(map);
                }
            } else {
                logger.info("数据预插入失败,订单号："+orderId);
//                ValidatorFieldCheckUtil.validateSpecialError(modelAndView, "transferAmount", "merchant.transfer.error","数据预插入失败!");
//                modelAndView.addObject("merchanttransferForm", form);
                map.put("99","数据预插入失败");
                map.put("merchanttransferForm", form);
                result.setData(map);
            }
        }
        return result;
    }


    /**
     * 根据业务需求导出相应的表格 此处暂时为可用情况 缺陷： 1.无法指定相应的列的顺序， 2.无法配置，excel文件名，excel sheet名称
     * 3.目前只能导出一个sheet 4.列的宽度的自适应，中文存在一定问题
     * 5.根据导出的业务需求最好可以在导出的时候输入起止页码，因为在大数据量的情况下容易造成卡顿
     * @param form
     * @param response
     * @throws Exception
     */
    //@ApiOperation(value = "导出")
    //@PostMapping("exportTransfer")
    /*public void exportExcelBack(@RequestBody MerchantTransferListRequest form,HttpServletResponse response) throws Exception {

        // 表格sheet名称
        String sheetName = "平台子账户转账记录";
        // 文件名称
        String fileName = URLEncoder.encode(sheetName,"UTF-8") + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date())
                + CustomConstants.EXCEL_EXT;
        //设置默认查询时间
        if(StringUtils.isEmpty(form.getTransferTimeStart())){
            form.setTransferTimeStart(GetDate.getDate("yyyy-MM-dd"));
        }
        if(StringUtils.isEmpty(form.getTransferTimeEnd())){
            form.setTransferTimeEnd(GetDate.getDate("yyyy-MM-dd"));
        }
        form.setPageSize(-1);
        // 需要输出的结果列表
        MerchantTransferResponse merchantTransferResponse = this.transferService.selectMerchantTransfer(form);
        List<MerchantTransferVO> recordList = merchantTransferResponse.getResultList();
        String[] titles = new String[] { "序号", "订单号","转出子账户","转出子账户代号", "转入子账户","转入子账户代号","转账金额","备注", "转账状态", "操作人", "转账类型","转账时间" };
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = ExportExcel.createHSSFWorkbookTitle(workbook, titles, sheetName + "_第1页");
        if (recordList != null && recordList.size() > 0) {
            int sheetCount = 1;
            int rowNum = 0;
            for (int i = 0; i < recordList.size(); i++) {
                rowNum++;
                if (i != 0 && i % 60000 == 0) {
                    sheetCount++;
                    sheet = ExportExcel.createHSSFWorkbookTitle(workbook, titles,
                            (sheetName + "_第" + sheetCount + "页"));
                    rowNum = 1;
                }
                // 新建一行
                Row row = sheet.createRow(rowNum);
                // 循环数据
                for (int celLength = 0; celLength < titles.length; celLength++) {
                    MerchantTransferVO transfer = recordList.get(i);
                    // 创建相应的单元格
                    Cell cell = row.createCell(celLength);
                    if (celLength == 0) {
                        // 序号
                        cell.setCellValue(i + 1);
                    } else if (celLength == 1) {
                        // 订单号
                        cell.setCellValue(transfer.getOrderId());
                    } else if (celLength == 2) {
                        // 转出子账户
                        cell.setCellValue(transfer.getOutAccountName());
                    } else if (celLength == 3) {
                        // 转出子账户代号
                        cell.setCellValue(transfer.getOutAccountCode());
                    } else if (celLength == 4) {
                        // 转入子账户
                        cell.setCellValue(transfer.getInAccountName());
                    } else if (celLength == 5) {
                        // 转入子账户代号
                        cell.setCellValue(transfer.getInAccountCode());
                    } else if (celLength == 6) {
                        // 转账金额
                        cell.setCellValue(String.valueOf(transfer.getTransferAmount()));
                    } else if (celLength == 7) {
                        // 备注
                        cell.setCellValue(transfer.getRemark());
                    } else if (celLength == 8) {
                        // 转账状态
                        Map<String, String> transferStatus = CacheUtil.getParamNameMap("MER_TRANS_STATUS");
                        Set<String> tranKey = transferStatus.keySet();
                        for (String key :tranKey){
                            if(key.equals(String.valueOf(transfer.getTransferType()))){
                                cell.setCellValue(transferStatus.get(key));
                            }
                        }
                    }else if (celLength == 9 ) {
                        // 操作人
                        cell.setCellValue(transfer.getCreateUserName());
                    }else if (celLength == 10 ) {
                        // 交易类型
                        Map<String, String> transferTypes = CacheUtil.getParamNameMap("MER_TRANS_TYPE");
                        Set<String> tranKey = transferTypes.keySet();
                        for (String key :tranKey){
                            if(key.equals(String.valueOf(transfer.getTransferType()))){
                                cell.setCellValue(transferTypes.get(key));
                            }
                        }
                    }else if (celLength == 11) {
                        // 转账时间
                        if(transfer.getTransferTime() == null){
                            cell.setCellValue("");
                        }else{
                            cell.setCellValue(GetDate.date2Str(transfer.getTransferTime(), GetDate.datetimeFormat));
                        }
                    }
                }
            }
        }
        // 导出
        ExportExcel.writeExcelFile(response, workbook, titles, fileName);
    }*/


    /**
     * 导出excel
     *
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    @ApiOperation(value = "导出")
    @PostMapping("exportTransfer")
    public void exportExcelAccount(@RequestBody MerchantTransferListRequest form,HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 封装查询条件
        //设置默认查询时间
        if(StringUtils.isEmpty(form.getTransferTimeStart())){
            form.setTransferTimeStart(GetDate.getDate("yyyy-MM-dd"));
        }
        if(StringUtils.isEmpty(form.getTransferTimeEnd())){
            form.setTransferTimeEnd(GetDate.getDate("yyyy-MM-dd"));
        }
        //sheet默认最大行数
        int defaultRowMaxCount = Integer.valueOf(systemConfig.getDefaultRowMaxCount());
        // 表格sheet名称
        String sheetName = "平台子账户转账记录";
        // 文件名称
        String fileName = URLEncoder.encode(sheetName, CustomConstants.UTF8) + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date()) + ".xls";
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
        DataSet2ExcelSXSSFHelper helper = new DataSet2ExcelSXSSFHelper();
        //form.setLimitFlg(true);
        //请求第一页5000条
        form.setPageSize(defaultRowMaxCount);
        form.setCurrPage(1);
        // 需要输出的结果列表
        MerchantTransferResponse merchantTransferResponse = this.transferService.selectMerchantTransfer(form);
        Integer totalCount = merchantTransferResponse.getRecordTotal();
        int sheetCount = (totalCount % defaultRowMaxCount) == 0 ? totalCount / defaultRowMaxCount : totalCount / defaultRowMaxCount + 1;
        Map<String, String> beanPropertyColumnMap = buildMap();
        Map<String, IValueFormatter> mapValueAdapter = buildValueAdapter();
        String sheetNameTmp = sheetName + "_第1页";
        if (totalCount == 0) {
            helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter, new ArrayList());
        }else{
            helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter, merchantTransferResponse.getResultList());
        }
        for (int i = 1; i < sheetCount; i++) {
            form.setPageSize(defaultRowMaxCount);
            form.setCurrPage(i+1);
            MerchantTransferResponse merchantTransferResponse2 = this.transferService.selectMerchantTransfer(form);
            if (merchantTransferResponse2 != null && merchantTransferResponse2.getResultList().size()> 0) {
                sheetNameTmp = sheetName + "_第" + (i + 1) + "页";
                helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter,  merchantTransferResponse2.getResultList());
            } else {
                break;
            }
        }
        DataSet2ExcelSXSSFHelper.write2Response(request, response, fileName, workbook);
    }

    private Map<String, String> buildMap() {
        Map<String, String> map = Maps.newLinkedHashMap();
        map.put("orderId", "订单号");
        map.put("outAccountName", "转出子账户");
        map.put("outAccountCode", "转出子账户代号");
        map.put("inAccountName", "转入子账户");
        map.put("inAccountCode", "转入子账户代号");
        map.put("transferAmount", "转账金额");
        map.put("remark", "备注");
        map.put("transferType", "转账状态");
        map.put("createUserName", "操作人");
        map.put("transferStyle", "转账类型");
        map.put("transferTime", "转账时间");
        return map;
    }

    private Map<String, IValueFormatter> buildValueAdapter() {
        Map<String, IValueFormatter> mapAdapter = Maps.newHashMap();
        IValueFormatter transferAmountAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                BigDecimal transferAmount = (BigDecimal) object;
                return String.valueOf(transferAmount);
            }
        };

        IValueFormatter transferTypeAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                Integer transferType = (Integer) object;
                // 转账状态
                String transferStatusStr = "";
                Map<String, String> transferStatus = CacheUtil.getParamNameMap("MER_TRANS_STATUS");
                Set<String> tranKey = transferStatus.keySet();
                for (String key :tranKey){
                    if(key.equals(String.valueOf(transferType))){
                        transferStatusStr = transferStatus.get(key);
                    }
                }
                return transferStatusStr;
            }
        };

        IValueFormatter transferStyleAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                Integer transferStyle = (Integer) object;
                // 交易类型
                String transferTypesStr = "";
                Map<String, String> transferTypes = CacheUtil.getParamNameMap("MER_TRANS_TYPE");
                Set<String> tranKey = transferTypes.keySet();
                for (String key :tranKey){
                    if(key.equals(String.valueOf(transferStyle))){
                        transferTypesStr = transferTypes.get(key);
                    }
                }
                return transferTypesStr;
            }
        };

        IValueFormatter transferTimeAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                Date transferTime = (Date) object;
                // 转账时间
                String transferTimeStr = "";
                if(transferTime == null){
                    transferTimeStr = ("");
                }else{
                    transferTimeStr = (GetDate.date2Str(transferTime, GetDate.datetimeFormat));
                }
                return transferTimeStr;
            }
        };

        mapAdapter.put("transferTime", transferTimeAdapter);
        mapAdapter.put("transferAmount", transferAmountAdapter);
        mapAdapter.put("transferType", transferTypeAdapter);
        mapAdapter.put("transferStyle", transferStyleAdapter);
        return mapAdapter;
    }
}
