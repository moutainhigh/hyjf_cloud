/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.admin.common.util.ExportExcel;
import com.hyjf.admin.service.RegistRecordService;
import com.hyjf.am.response.Response;
import com.hyjf.am.resquest.user.RegistRcordRequest;
import com.hyjf.am.vo.user.RegistRecordVO;
import com.hyjf.common.cache.CacheUtil;
import com.hyjf.common.util.AsteriskProcessUtil;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author nxl
 * @version RegistRecordController, v0.1 2018/6/23 15:16
 */

@Api(value = "注册记录")
@RestController
@RequestMapping("/hyjf-admin/registRecord")
public class RegistRecordController {
    @Autowired
    private RegistRecordService registRecordService;

    @ApiOperation(value = "注册记录", notes = "注册记录页面初始化")
    @PostMapping(value = "/usersInit")
    @ResponseBody
    public JSONObject userManagerInit() {
        JSONObject jsonObject = new JSONObject();
        // 注册平台
        Map<String, String> registPlat = CacheUtil.getParamNameMap("CLIENT");
        jsonObject.put("registPlat", registPlat);
        return jsonObject;
    }

    //会员管理列表查询
    @ApiOperation(value = "注册记录", notes = "注册记录列表查询")
    @PostMapping(value = "/registRecordList")
    @ResponseBody
    public JSONObject selectRegistRecordList(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> map) {
        JSONObject jsonObject = new JSONObject();
        RegistRcordRequest registerRcordeRequest = setRequese(map);
        List<RegistRecordVO> listRgistRecord = registRecordService.findRegistRecordList(registerRcordeRequest);
        String status = Response.FAIL;
        if (null != listRgistRecord && listRgistRecord.size() > 0) {
            jsonObject.put("record", listRgistRecord);
            status = Response.SUCCESS;
        }
        jsonObject.put("status", status);
        return jsonObject;
    }

    private RegistRcordRequest setRequese(Map<String, Object> mapParam) {
        RegistRcordRequest registerRcordeRequest = new RegistRcordRequest();
        if (null != mapParam && mapParam.size() > 0) {
            if (mapParam.containsKey("userName")) {
                registerRcordeRequest.setUserName(mapParam.get("userName").toString());
            }
            if (mapParam.containsKey("mobile")) {
                registerRcordeRequest.setMobile(mapParam.get("mobile").toString());
            }
            if (mapParam.containsKey("recommendName")) {
                registerRcordeRequest.setRecommendName(mapParam.get("recommendName").toString());
            }
            if (mapParam.containsKey("registPlat")) {
                registerRcordeRequest.setRegistPlat(mapParam.get("registPlat").toString());
            }
            if (mapParam.containsKey("regTimeStart")) {
                registerRcordeRequest.setRegTimeStart(mapParam.get("regTimeStart").toString());
            }
            if (mapParam.containsKey("regTimeEnd")) {
                registerRcordeRequest.setRegTimeEnd(mapParam.get("regTimeEnd").toString());
            }
            if (mapParam.containsKey("limit") && StringUtils.isNotBlank(mapParam.get("limit").toString())) {
                registerRcordeRequest.setLimit(Integer.parseInt(mapParam.get("limit").toString()));
            }
        }
        return registerRcordeRequest;
    }

    /**
     * 根据业务需求导出相应的表格 此处暂时为可用情况 缺陷： 1.无法指定相应的列的顺序， 2.无法配置，excel文件名，excel sheet名称
     * 3.目前只能导出一个sheet 4.列的宽度的自适应，中文存在一定问题
     * 5.根据导出的业务需求最好可以在导出的时候输入起止页码，因为在大数据量的情况下容易造成卡顿
     *
     * @param request
     * @param response
     * @throws Exception
     */
    @ApiOperation(value = "注册记录", notes = "注册记录列表导出")
    @PostMapping(value = "/exportregist")
    public void exportExcel(HttpServletRequest request, HttpServletResponse response, @RequestBody Map<String, Object> map) throws Exception {

        // 表格sheet名称
        String sheetName = "注册记录";
        // 文件名称
        String fileName = sheetName + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date()) + CustomConstants.EXCEL_EXT;

        // 需要输出的结果列表
        RegistRcordRequest registerRcordeRequest = setRequese(map);
        List<RegistRecordVO> listRgistRecord = registRecordService.findRegistRecordList(registerRcordeRequest);
        String[] titles = new String[] { "序号", "用户名", "手机号", "推荐人", "注册渠道", "注册平台", "注册IP", "注册时间" };
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();
        // 生成一个表格
        HSSFSheet sheet = ExportExcel.createHSSFWorkbookTitle(workbook, titles, sheetName + "_第1页");

        if (listRgistRecord != null && listRgistRecord.size() > 0) {

            int sheetCount = 1;
            int rowNum = 0;

            for (int i = 0; i < listRgistRecord.size(); i++) {
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
                    RegistRecordVO user = listRgistRecord.get(i);
                    // 创建相应的单元格
                    Cell cell = row.createCell(celLength);
                    if (celLength == 0) {// 序号
                        cell.setCellValue(i + 1);
                    } else if (celLength == 1) {// 用户名
                        cell.setCellValue(user.getUserName());
                    } else if (celLength == 2) {// 手机号
                        cell.setCellValue(AsteriskProcessUtil.getAsteriskedValue(user.getMobile(),3,7));
                    } else if (celLength == 3) {// 推荐人
                        cell.setCellValue(user.getRecommendName());
                    } else if (celLength == 4) {// 注册渠道
                        cell.setCellValue(user.getSourceName());
                    } else if (celLength == 5) {// 注册平台
                        cell.setCellValue(user.getRegistPlat());
                    } else if (celLength == 6) {// 注册IP
                        cell.setCellValue(user.getRegIP());
                    } else if (celLength == 7) {// 注册时间
                        cell.setCellValue(user.getRegTime());
                    }
                }
            }
        }
        // 导出
        ExportExcel.writeExcelFile(response, workbook, titles, fileName);
    }
}
