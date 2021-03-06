/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.controller.finance.bankaleve;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.result.ListResult;
import com.hyjf.admin.common.util.ShiroConstants;
import com.hyjf.admin.config.SystemConfig;
import com.hyjf.admin.interceptor.AuthorityAnnotation;
import com.hyjf.admin.service.BankAleveService;
import com.hyjf.admin.utils.exportutils.DataSet2ExcelSXSSFHelper;
import com.hyjf.admin.utils.exportutils.IValueFormatter;
import com.hyjf.am.resquest.admin.BankAleveRequest;
import com.hyjf.am.vo.admin.BankAleveVO;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.StringPool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author zdj
 * @version BankAleveController, v0.1 2018/7/20 15:16
 * 资金中心->银行账务明细
 */

@Api(value = "资金中心-银行账务明细",tags = "资金中心-银行账务明细")
@RestController
@RequestMapping("/hyjf-admin/bankaleve")
public class BankAleveController {

    @Autowired
    public SystemConfig systemConfig;
    @Autowired
    private BankAleveService bankAleveService;
    /** 权限 */
    public static final String PERMISSIONS = "bankAleve";

    /**
     * 银行账务明细列表查询
    * @author Zha Daojian
    * @date 2018/8/21 9:56
    * @param bankAleveRequest
    * @return com.alibaba.fastjson.JSONObject
    **/
    @ApiOperation(value = "银行账务明细", notes = "银行账务明细列表查询")
    @PostMapping(value = "/bankalevelist")
    @ResponseBody
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    public AdminResult<ListResult<BankAleveVO>> getBankaleveList(@RequestBody BankAleveRequest bankAleveRequest){

        Integer count = bankAleveService.queryBankAleveCount(bankAleveRequest);
        count = (count == null)?0:count;
        List<BankAleveVO> bankAleveList =bankAleveService.queryBankAleveList(bankAleveRequest);
        return new AdminResult<>(ListResult.build(bankAleveList,count));
    }

    /**
     * 对账文件手动导入
     * @author liushouyi
     * @date 2018/8/21 9:56
     * @param bankAleveRequest
     **/
    @ApiOperation(value = "银行账务明细", notes = "对账文件导入")
    @PostMapping(value = "/dualHistoryData")
    @ResponseBody
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    public AdminResult dualHistoryData(@RequestBody BankAleveRequest bankAleveRequest) {
        AdminResult result = new AdminResult();

        if(StringUtils.isBlank(bankAleveRequest.getDualDate())){
            result.setData("请输入处理时间！");
            return result;
        }

        String dualDate = "";
        try {
            dualDate = GetDate.dataformat(bankAleveRequest.getDualDate(),"yyyy-MM-dd").replaceAll("-","");
            if(StringUtils.isBlank(dualDate)) {
                result.setData("时间格式化失败，请输入正确处理日期！");
                return result;
            }
            // 判断20180228-20190228期间数据、已通过程序入库不可再手动修复
            Integer dualDateInt = Integer.parseInt(dualDate);
            if(dualDateInt > 20180228 && dualDateInt < 20190301) {
                result.setData("暂不支持2018-03-01到2019-02-28时间段的数据修复，如有疑问请与开发人员联系！");
                return result;
            }
        } catch (Exception e) {
            result.setData("时间格式化失败，请输入正确处理日期！");
            return result;
        }

        // 成功下载并发送mq后当天日期加10分钟锁、防止数据库导入数据事务未提交或数据未同步导致重复导入数据
        boolean redisResult = RedisUtils.tranactionSet(RedisConstants.DUAL_HISTORY_ALEVE + dualDate,10 * 60);
        if(!redisResult) {
            result.setData("日期：" + dualDate + "的数据已提交处理，请稍后再试！");
            return result;
        }

        // 文件下载
        String re = bankAleveService.dualHistoryData(dualDate);
        result.setData(re);
        return result;
    }

    /**
     * 根据业务需求导出相应的表格 此处暂时为可用情况 缺陷： 1.无法指定相应的列的顺序， 2.无法配置，excel文件名，excel sheet名称
     * 3.目前只能导出一个sheet 4.列的宽度的自适应，中文存在一定问题
     * 5.根据导出的业务需求最好可以在导出的时候输入起止页码，因为在大数据量的情况下容易造成卡顿
     *
     * @param bankAleveRequest
     * @param response
     * @throws Exception
     */
    @ApiOperation(value = "银行账务明细", notes = "银行账务明细导出")
    @PostMapping(value = "/exportbankaleve")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_EXPORT)
    public void exportBankaleveList(HttpServletRequest request, HttpServletResponse response, @RequestBody BankAleveRequest bankAleveRequest) throws UnsupportedEncodingException {

        //sheet默认最大行数
        int defaultRowMaxCount = Integer.valueOf(systemConfig.getDefaultRowMaxCount());
        // 表格sheet名称
        String sheetName = "银行账务明细";
        // 文件名称
        String fileName = URLEncoder.encode(sheetName, CustomConstants.UTF8) + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date()) + ".xlsx";
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
        DataSet2ExcelSXSSFHelper helper = new DataSet2ExcelSXSSFHelper();

        int sheetCount = 0;
        String sheetNameTmp = sheetName + "_第1页";
        Map<String, String> beanPropertyColumnMap = buildMap();
        Map<String, IValueFormatter> mapValueAdapter = buildValueAdapter();
        bankAleveRequest.setPageSize(defaultRowMaxCount);
        bankAleveRequest.setCurrPage(1);


        // 需要输出的结果列表
        Integer count = bankAleveService.queryBankAleveCount(bankAleveRequest);

        // 检索列表

        List<BankAleveVO> bankAleveList =bankAleveService.queryBankAleveList(bankAleveRequest);
        if (count == null || count.equals(0)){
            helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter, new ArrayList());
        }else{
            int totalCount = count;
            sheetCount = (totalCount % defaultRowMaxCount) == 0 ? totalCount / defaultRowMaxCount : totalCount / defaultRowMaxCount + 1;
            helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter, bankAleveList);
        }

        for (int i = 1; i < sheetCount; i++) {
            bankAleveRequest.setPageSize(defaultRowMaxCount);
            bankAleveRequest.setCurrPage(i+1);
            List<BankAleveVO> bankAleveList2 = bankAleveService.queryBankAleveList(bankAleveRequest);
            if (!CollectionUtils.isEmpty(bankAleveList2)) {
                sheetNameTmp = sheetName + "_第" + (i + 1) + "页";
                helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter,  bankAleveList2);
            } else {
                break;
            }
        }
        DataSet2ExcelSXSSFHelper.write2Response(request, response, fileName, workbook);
    }



    private Map<String, String> buildMap() {
        Map<String, String> map = Maps.newLinkedHashMap();
        map.put("bank", "银行号");
        map.put("cardnbr", "电子账号");
        map.put("amount", "交易金额");
        map.put("curNum", "货币代码");
        map.put("crflag", "交易金额符号");
        map.put("valdate", "入账日期");
        map.put("inpdate", "交易日期");
        map.put("reldate", "自然日期");
        map.put("inptime","交易时间");
        map.put("tranno","交易流水号");
        map.put("oriTranno","关联交易流水号");
        map.put("transtype","交易类型");
        map.put("desline","交易描述");
        map.put("currBal","交易后余额");
        map.put("forcardnbr","对手交易账号");
        map.put("revind","冲正撤销标志");
        map.put("accchg","交易标识");
        map.put("seqno","系统跟踪号");
        map.put("oriNum","原交易流水号");
        map.put("resv","保留域");
        return map;
    }


    private Map<String, IValueFormatter> buildValueAdapter() {
        Map<String, IValueFormatter> mapAdapter = Maps.newHashMap();
        IValueFormatter revindAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                if(object instanceof Integer){
                    Integer revind = (Integer) object;
                    return revind == 1 ? "已撤销/冲正" : "";
                }
                return "";
            }

        };
        mapAdapter.put("revind", revindAdapter);

        IValueFormatter accchgAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                if (object instanceof String) {
                    String accchg = (String) object;
                    return accchg.equals("1") ? "调账" : "";
                }
                return null;
            }
        };
        mapAdapter.put("accchg", accchgAdapter);
        IValueFormatter inptimeFormatter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                if (object instanceof Integer) {
                    String inptimeStr = String.valueOf(object);
                    if (inptimeStr.length() <= 8) {
                        try {
                            // 时间不满8位前补0
                            Integer inptime = Integer.valueOf(inptimeStr);
                            inptimeStr = String.format("%08d",inptime);
                            String[] parts = Iterables.toArray(
                                    Splitter
                                            .fixedLength(2)
                                            .split(inptimeStr),
                                    String.class
                            );
                            return Joiner.on(":").join(parts);
                        } catch (NumberFormatException e) {
                            return inptimeStr;
                        }
                    } else {
                        return inptimeStr;
                    }
                }
                return "";
            }
        };
        mapAdapter.put("inptime", inptimeFormatter);
        return mapAdapter;
    }
}
