/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.controller.finance.platformtransfer;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.result.ListResult;
import com.hyjf.admin.common.util.ShiroConstants;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.interceptor.AuthorityAnnotation;
import com.hyjf.admin.service.PlatformTransferService;
import com.hyjf.admin.utils.exportutils.DataSet2ExcelSXSSFHelper;
import com.hyjf.admin.utils.exportutils.IValueFormatter;
import com.hyjf.am.resquest.admin.PlatformTransferListRequest;
import com.hyjf.am.resquest.admin.PlatformTransferRequest;
import com.hyjf.am.vo.admin.AccountRechargeVO;
import com.hyjf.common.util.AsteriskProcessUtil;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.StringPool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.hyjf.admin.common.util.ShiroConstants.PERMISSION_EXPORT;
import static com.hyjf.admin.common.util.ShiroConstants.PERMISSION_VIEW;

/**
 * @author: sunpeikai
 * @version: PlatformTransferController, v0.1 2018/7/9 10:13
 */
@Api(value = "资金中心-转账管理-平台转账",tags = "资金中心-转账管理-平台转账")
@RestController
@RequestMapping(value = "/hyjf-admin/finance/platformtransfer")
public class PlatformTransferController extends BaseController {

    @Autowired
    private PlatformTransferService platformTransferService;
    private static final String PERMISSIONS = "recharge";

    /**
     * 平台转账-查询转账列表
     * @auth sunpeikai
     * @param request 查询条件
     * @return
     */
    @ApiOperation(value = "平台转账-查询转账列表",notes = "平台转账-查询转账列表")
    @PostMapping(value = "/transferlist")
    @AuthorityAnnotation(key = PERMISSIONS,value = PERMISSION_VIEW)
    public AdminResult<ListResult<AccountRechargeVO>> transferList(@RequestBody PlatformTransferListRequest request,HttpServletRequest httpServletRequest){
        Integer count = platformTransferService.getPlatformTransferCount(request);
        count = (count == null)?0:count;
        List<AccountRechargeVO> accountRechargeVOList = platformTransferService.searchPlatformTransferList(request);
        boolean isShow = this.havePermission(httpServletRequest,PERMISSIONS + ":" + ShiroConstants.PERMISSION_HIDDEN_SHOW);
        if (!isShow){
            accountRechargeVOList.forEach(accountRechargeVO -> {
                accountRechargeVO.setMobile(AsteriskProcessUtil.getAsteriskedMobile(accountRechargeVO.getMobile()));
            });
        }
        return new AdminResult<>(ListResult.build(accountRechargeVOList,count));
    }

    /**
     * 根据userName检查是否可以平台转账
     * @auth sunpeikai
     * @param requestMap 请求参数
     * @return
     */
    @ApiOperation(value = "平台转账-根据username查询用户信息",notes = "平台转账-根据username查询用户信息")
    @ApiImplicitParam(name = "userName", value = "用户名", required = true, dataType = "String")
    @PostMapping(value = "/getuserinfobyusername")
    public AdminResult getUserInfoByUserName(@RequestBody Map<String,String> requestMap){
        String userName = requestMap.get("userName");
        logger.info("平台转账--> getuserinfobyusername======userName=[{}]",userName);
        JSONObject result = new JSONObject();
        if(StringUtils.isNotEmpty(userName)){
            result = platformTransferService.checkTransfer(userName);
        }else{
            logger.error("平台转账--> getUserInfoByUserName,,,,userName=null");
            result.put("status","99");
            result.put("info","用户账号不能为空");
        }
        if("0".equals(result.get("status"))){
            return new AdminResult(SUCCESS,SUCCESS_DESC);
        }else{
            return new AdminResult(FAIL,result.getString("info"));
        }
    }

    @ApiOperation(value = "平台转账-查询商户账户余额",notes = "平台转账-查询商户账户余额")
    @PostMapping(value = "/tohandrecharge")
    public AdminResult toHandRecharge(HttpServletRequest request){
        Integer userId = Integer.valueOf(getUser(request).getId());
        logger.info("toHandRecharge=========userId:[{}]",userId);
        BigDecimal balance = platformTransferService.getAccountBalance(userId);
        Map<String,BigDecimal> map = new HashMap<>();
        map.put("balance",balance);
        return new AdminResult(map);
    }

    /**
     * 平台转账
     * @auth sunpeikai
     * @param request 传参
     * @return
     */
    @ApiOperation(value = "平台转账",notes = "平台转账")
    @PostMapping(value = "/handrecharge")
    public AdminResult handRecharge(HttpServletRequest request, @RequestBody PlatformTransferRequest platformTransferRequest){
        Integer userId = Integer.valueOf(getUser(request).getId());
        JSONObject result = platformTransferService.handRecharge(userId,request,platformTransferRequest);
        if("0".equals(result.get("status"))){
            return new AdminResult(SUCCESS,result.getString("result"));
        }else{
            return new AdminResult(FAIL,result.getString("result"));
        }
    }

    @ApiOperation(value = "平台转账-导出excel",notes = "平台转账-导出excel")
    @PostMapping(value = "/platformtransferlist")
    @AuthorityAnnotation(key = PERMISSIONS,value = PERMISSION_EXPORT)
    public void exportPlatformTransferList(HttpServletRequest request, HttpServletResponse response,@RequestBody PlatformTransferListRequest platformTransferListRequest) throws Exception {
        //sheet默认最大行数
        int defaultRowMaxCount = Integer.valueOf(systemConfig.getDefaultRowMaxCount());
        // 表格sheet名称
        String sheetName = "平台转账详情数据";
        // 文件名称
        String fileName = URLEncoder.encode(sheetName, CustomConstants.UTF8) + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date()) + CustomConstants.EXCEL_EXT;
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
        DataSet2ExcelSXSSFHelper helper = new DataSet2ExcelSXSSFHelper();

        platformTransferListRequest.setCurrPage(-1);
        Integer totalCount = platformTransferService.getPlatformTransferCount(platformTransferListRequest);

        int sheetCount = (totalCount % defaultRowMaxCount) == 0 ? totalCount / defaultRowMaxCount : totalCount / defaultRowMaxCount + 1;
        Map<String, String> beanPropertyColumnMap = buildMap();
        Map<String, IValueFormatter> mapValueAdapter = buildValueAdapter();
        if (totalCount == 0) {
            String sheetNameTmp = sheetName + "_第1页";
            helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter, new ArrayList());
        }
        boolean isShow = this.havePermission(request,PERMISSIONS + ":" + ShiroConstants.PERMISSION_HIDDEN_SHOW);
        for (int i = 1; i <= sheetCount; i++) {
            platformTransferListRequest.setPageSize(defaultRowMaxCount);
            platformTransferListRequest.setCurrPage(i);
            List<AccountRechargeVO> accountRechargeVOList2 = platformTransferService.searchPlatformTransferList(platformTransferListRequest);
            if (accountRechargeVOList2 != null && accountRechargeVOList2.size()> 0) {
                if (!isShow){
                    accountRechargeVOList2.forEach(accountRechargeVO -> {
                        accountRechargeVO.setMobile(AsteriskProcessUtil.getAsteriskedMobile(accountRechargeVO.getMobile()));
                    });
                }
                String sheetNameTmp = sheetName + "_第" + i + "页";
                helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter,  accountRechargeVOList2);
            } else {
                break;
            }
        }
        DataSet2ExcelSXSSFHelper.write2Response(request, response, fileName, workbook);
    }

    private Map<String, String> buildMap() {

        Map<String, String> map = Maps.newLinkedHashMap();
        map.put("nid", "订单号");
        map.put("username", "用户名");
        map.put("mobile", "手机号");
        map.put("money", "转账金额");
        map.put("balance", "可用余额");
        map.put("status", "转账状态");
        map.put("createTime", "转账时间");
        map.put("remark", "备注");
        map.put("txDate", "发送日期");
        map.put("txTimeStr", "发送时间");
        map.put("seqNo", "系统跟踪号");
        return map;
    }
    private Map<String, IValueFormatter> buildValueAdapter() {
        Map<String, IValueFormatter> mapAdapter = Maps.newHashMap();
        IValueFormatter statusAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                Integer status = (Integer) object;
                if(status == 1){
                    return  "转账中";
                }else if(status == 2){
                    return  "成功";
                }else{
                    return  "失败";
                }
            }
        };
        IValueFormatter createTimeAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                Date createTime = (Date) object;
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(createTime);
            }
        };
        IValueFormatter txDateAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                Integer date = (Integer) object;
                String dateStr = String.valueOf(date);
                String year = dateStr.substring(0,4);
                String month = dateStr.substring(4,6);
                String day = dateStr.substring(6,8);
                return year + "-" + month + "-" + day;
            }
        };
        mapAdapter.put("status", statusAdapter);
        mapAdapter.put("createTime", createTimeAdapter);
        mapAdapter.put("txDate", txDateAdapter);
        return mapAdapter;
    }

}
