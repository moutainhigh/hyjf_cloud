package com.hyjf.admin.controller.user;

import com.google.common.collect.Maps;
import com.hyjf.admin.beans.request.CertificateAuthorityExceptionBean;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.result.ListResult;
import com.hyjf.admin.common.util.ExportExcel;
import com.hyjf.admin.common.util.ShiroConstants;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.interceptor.AuthorityAnnotation;
import com.hyjf.admin.service.CertificateAuthorityExceptionService;
import com.hyjf.admin.utils.exportutils.DataSet2ExcelSXSSFHelper;
import com.hyjf.admin.utils.exportutils.IValueFormatter;
import com.hyjf.am.response.AdminResponse;
import com.hyjf.am.response.user.CertificateAuthorityResponse;
import com.hyjf.am.resquest.user.CertificateAuthorityExceptionRequest;
import com.hyjf.am.vo.user.CertificateAuthorityVO;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.GetDateUtils;
import com.hyjf.common.util.StringPool;
import io.swagger.annotations.Api;
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
import javax.xml.crypto.Data;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * CA认证异常
 *
 * @author dongzeshan
 */
@Api(value = "异常中心-CA认证记录",tags = "异常中心-CA认证记录")
@RestController
@RequestMapping("/hyjf-admin/certificate")
public class CertificateAuthorityExceptionController extends BaseController {

	private static final String PERMISSIONS = "CAException";
	
    @Autowired
    private CertificateAuthorityExceptionService certificateAuthorityExceptionService;

    /**
     * 画面初始化
     *
     * @param
     * @param
     * @return
     */
    @PostMapping("/search")
	@ApiOperation(value = "CA认证记录列表", notes = "CA认证记录列表")
	@AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    public AdminResult<ListResult<CertificateAuthorityVO>> init(@RequestBody CertificateAuthorityExceptionRequest certificateAuthorityExceptionBean) {


		CertificateAuthorityResponse prs = certificateAuthorityExceptionService.getRecordList(certificateAuthorityExceptionBean);

		if (prs == null) {
			return new AdminResult<>(FAIL, FAIL_DESC);
		}
		if (!AdminResponse.isSuccess(prs)) {
			return new AdminResult<>(FAIL, prs.getMessage());

		}
		return new AdminResult<ListResult<CertificateAuthorityVO>>(ListResult.build(prs.getResultList(), prs.getRecordTotal()));
    }

    /**
     * 获取CA认证异常列表
     * jijun 20180820
     * @param request
     * @return
     */
    @PostMapping("/searchExceptionList")
    @ApiOperation(value = "CA认证异常记录列表", notes = "CA认证异常记录列表")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
    public AdminResult<ListResult<CertificateAuthorityVO>> searchExceptionList(@RequestBody CertificateAuthorityExceptionRequest request) {
        CertificateAuthorityResponse response = certificateAuthorityExceptionService.getExceptionRecordList(request);
        if (response == null) {
            return new AdminResult<>(FAIL, FAIL_DESC);
        }
        if (!AdminResponse.isSuccess(response)) {
            return new AdminResult<>(FAIL, response.getMessage());
        }
        return new AdminResult<ListResult<CertificateAuthorityVO>>(ListResult.build(response.getResultList(), response.getRecordTotal()));
    }


    /**
     * 异常处理更新Action
     *
     * @param
     * @param
     * @return
     */
    @PostMapping("/modifyAction")
 	@ApiOperation(value = "CA认证更新", notes = "CA认证更新")
 	@AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_MODIFY)
    public AdminResult modifyAction(@RequestBody CertificateAuthorityExceptionBean certificateAuthorityExceptionBean) {

       // 发送CA认证MQ
    	CertificateAuthorityResponse prs = certificateAuthorityExceptionService.updateUserCAMQ(certificateAuthorityExceptionBean.getUserId());
        if (prs == null) {
			return new AdminResult<>(FAIL, FAIL_DESC);
		}
		if (!AdminResponse.isSuccess(prs)) {
			return new AdminResult<>(FAIL, prs.getMessage());

		}
		return new AdminResult();
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
    //@PostMapping("/exportAction")
 	//@ApiOperation(value = "CA导出列表", notes = "CA导出列表")
 	//@AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_EXPORT)
    /*public void exportExcel(@RequestBody CertificateAuthorityExceptionRequest certificateAuthorityExceptionBean, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // 表格sheet名称
        String sheetName = "CA认证记录";
        // 文件名称
        String fileName = URLEncoder.encode(sheetName, "UTF-8") + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date()) + ".xls";
        // 需要输出的结果列表
		// 可以直接使用
        certificateAuthorityExceptionBean.setCurrPage(-1);
        certificateAuthorityExceptionBean.setPageSize(-1);
        List<CertificateAuthorityVO> recordList =certificateAuthorityExceptionService.getRecordList(certificateAuthorityExceptionBean).getResultList();
        String[] titles = new String[] { "序号", "用户名", "CA认证手机号", "姓名/名称","证件号码" ,"用户类型", "邮箱", "客户编号", "状态", "申请时间", "备注" };
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
                    sheet = ExportExcel.createHSSFWorkbookTitle(workbook, titles, (sheetName + "_第" + sheetCount + "页"));
                    rowNum = 1;
                }

                // 新建一行
                Row row = sheet.createRow(rowNum);
                // 循环数据
                for (int celLength = 0; celLength < titles.length; celLength++) {
                	CertificateAuthorityVO data = recordList.get(i);
                    // 创建相应的单元格
                    Cell cell = row.createCell(celLength);
                    if (celLength == 0) {// 序号
                        cell.setCellValue(i + 1);
                    } else if (celLength == 1) {// 用户名
                        cell.setCellValue(data.getUserName());
                    } else if (celLength == 2) {// ca认证手机号
                        cell.setCellValue(data.getMobile());
                    } else if (celLength == 3) {// 姓名/名称
                        cell.setCellValue(data.getTrueName());
                    }   else if (celLength == 4){
                        cell.setCellValue(StringUtils.isBlank(data.getIdNo())?"":data.getIdNo());
                    } else if (celLength == 5) {// 用户类型
                        cell.setCellValue(data.getIdType().compareTo(0) == 0?"个人":"企业");
                    }else if (celLength == 6) {// 邮箱
                        cell.setCellValue(data.getEmail());
                    } else if (celLength == 7) {// 客户编号
                        cell.setCellValue(data.getCustomerId());
                    } else if (celLength == 8) {// 状态
                        cell.setCellValue(data.getCode().equals("1000")?"认证成功":"未认证或认证失败");
                    } else if (celLength == 9) {// 申请时间
                        cell.setCellValue( GetDateUtils.format(data.getCreateTime()));
                    } else if (celLength == 10) {// 备注
                        cell.setCellValue(data.getRemark());
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
     * @param certificateAuthorityExceptionBean
     * @param request
     * @param response
     * @throws Exception
     */
    @PostMapping("/exportAction")
    @ApiOperation(value = "CA导出列表", notes = "CA导出列表")
    @AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_EXPORT)
    public void exportBankAccountExcel(@RequestBody CertificateAuthorityExceptionRequest certificateAuthorityExceptionBean, HttpServletRequest request, HttpServletResponse response)throws Exception {
        // 封装查询条件
        CertificateAuthorityExceptionRequest certificateBean = new CertificateAuthorityExceptionRequest();
        BeanUtils.copyProperties(certificateAuthorityExceptionBean,certificateBean);
        //sheet默认最大行数
        int defaultRowMaxCount = Integer.valueOf(systemConfig.getDefaultRowMaxCount());
        // 表格sheet名称
        String sheetName = "CA认证记录";
        // 文件名称
        String fileName = URLEncoder.encode(sheetName, "UTF-8") + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date()) + ".xlsx";
        // 声明一个工作薄
        SXSSFWorkbook workbook = new SXSSFWorkbook(SXSSFWorkbook.DEFAULT_WINDOW_SIZE);
        DataSet2ExcelSXSSFHelper helper = new DataSet2ExcelSXSSFHelper();
        certificateBean.setLimitFlg(true);
        //请求第一页5000条
        certificateBean.setPageSize(defaultRowMaxCount);
        certificateBean.setCurrPage(1);
        // 需要输出的结果列表
        CertificateAuthorityResponse recordList = certificateAuthorityExceptionService.getRecordList(certificateAuthorityExceptionBean);
        Integer totalCount = recordList.getRecordTotal();
        int sheetCount = (totalCount % defaultRowMaxCount) == 0 ? totalCount / defaultRowMaxCount : totalCount / defaultRowMaxCount + 1;
        Map<String, String> beanPropertyColumnMap = buildMapBkAcc();
        Map<String, IValueFormatter> mapValueAdapter = buildValueAdapterBkAcc();
        String sheetNameTmp = sheetName + "_第1页";
        if (totalCount == 0) {
            helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter, new ArrayList());
        }else{
            helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter, recordList.getResultList());
        }
        for (int i = 1; i < sheetCount; i++) {
            certificateBean.setPageSize(defaultRowMaxCount);
            certificateBean.setCurrPage(i+1);
            CertificateAuthorityResponse recordList2 = certificateAuthorityExceptionService.getRecordList(certificateAuthorityExceptionBean);
            if (recordList2 != null && recordList2.getResultList().size()> 0) {
                sheetNameTmp = sheetName + "_第" + (i + 1) + "页";
                helper.export(workbook, sheetNameTmp, beanPropertyColumnMap, mapValueAdapter,  recordList2.getResultList());
            } else {
                break;
            }
        }
        DataSet2ExcelSXSSFHelper.write2Response(request, response, fileName, workbook);
    }

    private Map<String, String> buildMapBkAcc() {
        Map<String, String> map = Maps.newLinkedHashMap();
        map.put("userName", "用户名");
        map.put("mobile", "当前手机号");
        map.put("trueName", "姓名/名称");
        map.put("idNo", "证件号码");
        map.put("idType", "用户类型");
        map.put("email", "邮箱");
        map.put("customerId", "客户编号");
        map.put("code", "状态");
        map.put("createTime", "申请时间");
        map.put("remark", "备注");
        return map;
    }

    private Map<String, IValueFormatter> buildValueAdapterBkAcc() {
        Map<String, IValueFormatter> mapAdapter = Maps.newHashMap();
        IValueFormatter idNoAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                String idNo = (String) object;
                return StringUtils.isBlank(idNo)?"":idNo;
            }
        };

        IValueFormatter idTypeAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                Integer idType = (Integer) object;
                return idType.compareTo(0) == 0?"个人":"企业";
            }
        };

        IValueFormatter codeAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                String code = (String) object;
                return code.equals("1000")?"认证成功":"未认证或认证失败";
            }
        };

        IValueFormatter createTimeAdapter = new IValueFormatter() {
            @Override
            public String format(Object object) {
                Date createTime = (Date) object;
                return GetDateUtils.format(createTime);
            }
        };

        mapAdapter.put("idNo", idNoAdapter);
        mapAdapter.put("idType", idTypeAdapter);
        mapAdapter.put("code", codeAdapter);
        mapAdapter.put("createTime", createTimeAdapter);
        return mapAdapter;
    }

}
