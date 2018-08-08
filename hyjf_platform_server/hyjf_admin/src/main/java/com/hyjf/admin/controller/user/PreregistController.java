/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.controller.user;


import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import com.hyjf.admin.beans.request.PreRegistRequestBean;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.result.ListResult;
import com.hyjf.admin.common.util.ExportExcel;
import com.hyjf.admin.common.util.ShiroConstants;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.interceptor.AuthorityAnnotation;
import com.hyjf.admin.service.PreregistService;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.user.AdminPreRegistListResponse;
import com.hyjf.am.resquest.user.AdminPreRegistListRequest;
import com.hyjf.am.vo.user.AdminPreRegistListVO;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.StringPool;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * @author DongZeShan
 * @version LoginController.java, v0.1 2018年6月15日 上午9:32:30
 */
@Api(value = "预注册用户",tags ="预注册用户")
@RestController
@RequestMapping("/hyjf-admin/preregist")
public class PreregistController extends BaseController {
	//权限名称
	private static final String PERMISSIONS = "preregist";
	@Autowired
	private PreregistService preregistService;

	/**
	 * @Author: dongzeshan
	 * @Desc :预注册用户页面载入
	 * @Param: * @param map
	 * @Date: 16:43 2018/6/15
	 * @Return: BaseResult<ListResult<AdminPreRegistListVO>>
	 */
	@ApiOperation(value = "预注册用户页面载入", notes = "预注册用户页面载入")
	@PostMapping(value = "/init")
	@ResponseBody
	@AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_VIEW)
	public AdminResult<ListResult<AdminPreRegistListVO>> init(HttpServletRequest request,
			@RequestBody PreRegistRequestBean adminPreRegistListRequest) {
		AdminPreRegistListRequest aprlr = new AdminPreRegistListRequest();
		
		 //可以直接使用
		 BeanUtils.copyProperties(adminPreRegistListRequest, aprlr);
		 
		AdminPreRegistListResponse prs = preregistService.getRecordList(aprlr);
		
		if(prs==null) {
			return new AdminResult<>(FAIL, FAIL_DESC);
		}
		if (!Response.isSuccess(prs)) {
			return new AdminResult<>(FAIL, prs.getMessage());

		}
		return new AdminResult<ListResult<AdminPreRegistListVO>>(ListResult.build(prs.getResultList(), prs.getRecordTotal())) ;
	}

	/**
	 * @Author: dongzeshan
	 * @Desc :编辑页信息
	 * @Param: * @param map
	 * @Date: 16:43 2018/6/15
	 * @Return: JSONObject
	 */

	@ApiOperation(value = "编辑页信息", notes = "编辑页信息")
	@PostMapping(value = "/updatepreregistlist")
	@ResponseBody
	@AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_UPDATE)
	public AdminResult<AdminPreRegistListVO> updatepreregistlist(HttpServletRequest request, 
			@RequestBody PreRegistRequestBean adminPreRegistListRequest) {
		AdminPreRegistListRequest aprlr = new AdminPreRegistListRequest();
		aprlr.setId(adminPreRegistListRequest.id);
		AdminPreRegistListResponse prs = preregistService.getPreRegist(aprlr);
		if(prs==null) {
			return new AdminResult<>(FAIL, FAIL_DESC);
		}
		if (!Response.isSuccess(prs)) {
			return new AdminResult<>(FAIL, prs.getMessage());

		}
		return new AdminResult<AdminPreRegistListVO>(prs.getResult());
	}

	/**
	 * @Author: dongzeshan
	 * @Desc :获取权限列表
	 * @Param: *
	 * @Date: 16:43 2018/6/15
	 * @Return: JSONObject
	 */
	@ApiOperation(value = "编辑页保存", notes = "编辑页保存")
	@PostMapping(value = "/savepreregistlist")
	@ResponseBody
	@AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_UPDATE)
	public AdminResult getUserPermission(HttpServletRequest request,
			@RequestBody PreRegistRequestBean adminPreRegistListRequest) {
		AdminPreRegistListRequest aprlr = new AdminPreRegistListRequest();
		BeanUtils.copyProperties(adminPreRegistListRequest, aprlr);
		AdminPreRegistListResponse prs = preregistService.savePreRegist(aprlr);
		if(prs==null) {
			return new AdminResult<>(FAIL, FAIL_DESC);
		}
		if (!Response.isSuccess(prs)) {
			return new AdminResult<>(FAIL, prs.getMessage());

		}
		return new AdminResult<>();
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
	@ApiOperation(value = "预注册用户下载文件", notes = "预注册用户下载文件")
	@PostMapping(value = "/exportpreregist")
	@ResponseBody
	@AuthorityAnnotation(key = PERMISSIONS, value = ShiroConstants.PERMISSION_EXPORT)
	public void exportExcel(@RequestBody PreRegistRequestBean adminPreRegistListRequest, HttpServletRequest request, HttpServletResponse response) throws Exception {

		AdminPreRegistListRequest aprlr = new AdminPreRegistListRequest();
		
		 //可以直接使用
		 BeanUtils.copyProperties(adminPreRegistListRequest, aprlr);
		 aprlr.setCurrPage(-1);
		 aprlr.setPageSize(-1);
		AdminPreRegistListResponse prs = preregistService.getRecordList(aprlr);
		// 表格sheet名称
		String sheetName = "预注册用户";
		// 文件名称
		String fileName = URLEncoder.encode(sheetName, "UTF-8") + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date()) + ".xls";
		// 需要输出的结果列表

		 List<AdminPreRegistListVO> recordList = preregistService.getRecordList(aprlr).getResultList();
		String[] titles = new String[] { "序号", "手机号", "推荐人", "渠道", "预注册时间", "是否已经注册", "操作终端", "备注" };
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
					AdminPreRegistListVO preRegist = recordList.get(i);
					// 创建相应的单元格
					Cell cell = row.createCell(celLength);
					if (celLength == 0) {// 序号
						cell.setCellValue(i + 1);
					} else if (celLength == 1) {// 手机号
						cell.setCellValue(preRegist.getMobile());
					} else if (celLength == 2) {// 推荐人
						cell.setCellValue(preRegist.getReferrer());
					} else if (celLength == 3) {// 渠道
						cell.setCellValue(preRegist.getSource());
					} else if (celLength == 4) {// 预注册时间
						cell.setCellValue(preRegist.getPreRegistTime());
					} else if (celLength == 5) {// 是否已经注册
						if (preRegist.getRegistFlag().equals("1")) {
							cell.setCellValue("是");
						} else {
							cell.setCellValue("否");
						}
					} else if (celLength == 6) {// 操作终端
						cell.setCellValue(preRegist.getPlatformName());
					} else if (celLength == 7) {// 备注
						cell.setCellValue(preRegist.getRemark());
					}
				}
			}
		}
		// 导出
		ExportExcel.writeExcelFile(response, workbook, titles, fileName);
	}
}
