package com.hyjf.admin.controller.promotion.channel;

import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.util.ExportExcel;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.service.promotion.channel.ChannelService;
import com.hyjf.admin.utils.FileUpLoadUtil;
import com.hyjf.admin.utils.ValidatorFieldCheckUtil;
import com.hyjf.am.response.admin.promotion.UtmResultResponse;
import com.hyjf.am.vo.admin.promotion.channel.ChannelCustomizeVO;
import com.hyjf.am.vo.admin.promotion.channel.UtmChannelVO;
import com.hyjf.am.vo.user.UserVO;
import com.hyjf.common.util.CustomConstants;
import com.hyjf.common.util.GetDate;
import com.hyjf.common.util.StringPool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @Auther: walter.limeng
 * @Date: 2018/7/14 10:38
 * @Description: ChannelController
 */
@Api(tags ="推广中心-推广管理列表")
@RestController
@RequestMapping("/hyjf-admin/promotion/channel")
public class ChannelController extends BaseController {
    private Logger logger = LoggerFactory.getLogger(ChannelController.class);
    @Resource
    private ChannelService channelService;

    @ApiOperation(value = "页面初始化", notes = "推广列表")
    @PostMapping("/init")
    public AdminResult channelListInit(HttpServletRequest request, HttpServletResponse response, @RequestBody ChannelCustomizeVO channelCustomizeVO) {
        AdminResult adminResult = new AdminResult();
        Integer count = this.channelService.countList(channelCustomizeVO);
        if(null != count){
            channelCustomizeVO.setLimitStart(channelCustomizeVO.getLimitStart());
            channelCustomizeVO.setLimitEnd(channelCustomizeVO.getLimitEnd());
            List<ChannelCustomizeVO> channelList = channelService.getByPageList(channelCustomizeVO);
            adminResult.setData(channelList);
        }
        adminResult.setTotalCount(count);
        return adminResult;
    }

    @ApiOperation(value = "画面迁移(含有id更新，不含有id添加)", notes = "画面迁移(含有id更新，不含有id添加)")
    @PostMapping("/infoaction")
    public AdminResult info(HttpServletRequest request, HttpServletResponse response, @RequestBody ChannelCustomizeVO channelCustomizeVO){
        UtmResultResponse adminResult = new UtmResultResponse();
        if (StringUtils.isNotEmpty(channelCustomizeVO.getUtmId())) {
            UtmChannelVO record = channelService.getRecord(channelCustomizeVO.getUtmId());
            if (record.getUtmReferrer() == null || record.getUtmReferrer() == 0) {
                adminResult.setUtmReferrer("");
            } else {
                UserVO user = this.channelService.getUser(StringUtils.EMPTY, String.valueOf(record.getUtmReferrer()));
                adminResult.setUtmReferrer(user.getUsername());
            }
            adminResult.setData(record);
            if (record != null) {
                adminResult.setUrl(getUrl(record));
            }
            adminResult.setData(record);
        }
        return new AdminResult(adminResult);
    }

    @ApiOperation(value = "添加或修改信息", notes = "添加或修改信息")
    @PostMapping("/insertorupdateaction")
    public UtmResultResponse insertAction(HttpServletRequest request, HttpServletResponse response, @RequestBody ChannelCustomizeVO channelCustomizeVO){
        UtmResultResponse adminResult = new UtmResultResponse();
        //根据utmId判断，如存在，则为修改，如不存在，则为新增
        validatorFieldCheck(adminResult,channelCustomizeVO);
        if(AdminResult.SUCCESS.equals(adminResult.getStatus())){
            boolean flag = channelService.insertOrUpdateUtm(channelCustomizeVO);
            if(!flag){
                adminResult.setStatus(AdminResult.FAIL);
                adminResult.setStatusDesc("系统异常，请联系管理员!");
            }
        }
        return adminResult;
    }


    @ApiOperation(value = "删除信息", notes = "删除信息")
    @PostMapping("/deleteaction")
    public UtmResultResponse deleteAction(HttpServletRequest request, HttpServletResponse response, @RequestBody ChannelCustomizeVO channelCustomizeVO){
        UtmResultResponse adminResult = new UtmResultResponse();
        //根据utmId判断，如存在，则为修改，如不存在，则为新增
        if(StringUtils.isNotEmpty(channelCustomizeVO.getUtmId())){
            boolean flag = channelService.deleteAction(channelCustomizeVO);
            if(!flag){
                adminResult.setStatus(AdminResult.FAIL);
                adminResult.setStatusDesc("系统异常，请联系管理员!");
            }
        }else{
            adminResult.setStatus(AdminResult.FAIL);
            adminResult.setStatusDesc("删除异常！");
        }
        return adminResult;
    }

    /**
     * @Author walter.limeng
     * @Description  验证对象
     * @Date 10:24 2018/7/16
     * @Param adminResult
     * @Param channelCustomizeVO
     * @return
     */
    public void validatorFieldCheck(UtmResultResponse adminResult,ChannelCustomizeVO channelCustomizeVO){
        ModelAndView modelAndView = new ModelAndView();
        // 渠道
        if(StringUtils.isNotEmpty(channelCustomizeVO.getSourceId())){
            // 推广方式
            ValidatorFieldCheckUtil.validateMaxLength(modelAndView, "utmMedium", channelCustomizeVO.getUtmMedium(), 50, false);
            // 推广单元
            ValidatorFieldCheckUtil.validateMaxLength(modelAndView, "utmContent", channelCustomizeVO.getUtmContent(), 50, false);
            // 推广计划
            ValidatorFieldCheckUtil.validateMaxLength(modelAndView, "utmCampaign", channelCustomizeVO.getUtmCampaign(), 50, false);
            // 关键字
            boolean utmTermFlag = ValidatorFieldCheckUtil.validateMaxLength(modelAndView, "utmTerm", channelCustomizeVO.getUtmTerm(), 50, false);
            // 推荐人
//		boolean utmReferrerFlag = ValidatorFieldCheckUtil.validateRequired(modelAndView, "utmReferrer", form.getUtmReferrer());
//		if (utmReferrerFlag) {
//			int usersFlag = this.channelService.checkUtmReferrer(form.getUtmReferrer());
//			if (usersFlag == 1) {
//				ValidatorFieldCheckUtil.validateSpecialError(modelAndView, "utmReferrer", "referrer_username.not.exists");
//			}
//		}
            // 链接地址
            ValidatorFieldCheckUtil.validateRequired(modelAndView, "linkAddress", channelCustomizeVO.getLinkAddress());
            ValidatorFieldCheckUtil.validateMaxLength(modelAndView, "linkAddress", channelCustomizeVO.getLinkAddress(), 250, false);
            // 备注说明
            ValidatorFieldCheckUtil.validateMaxLength(modelAndView, "remark", channelCustomizeVO.getRemark(), 100, false);

//            if (sourceIdFlag && utmTermFlag && StringUtils.isNotEmpty(channelCustomizeVO.getUtmTerm())) {
//                Utm utm = this.channelService.getRecord(channelCustomizeVO.getSourceId(), channelCustomizeVO.getUtmTerm());
//                if (utm != null) {
//                    if(StringUtils.isNotEmpty(channelCustomizeVO.getUtmId())){
//                        if(utm.getUtmId() != Integer.parseInt(channelCustomizeVO.getUtmId())){
//                            ValidatorFieldCheckUtil.validateSpecialError(modelAndView, "sourceId-utmTerm", "exists.sourceid.utmterm");
//                        }
//                    }else{
//                        ValidatorFieldCheckUtil.validateSpecialError(modelAndView, "sourceId-utmTerm", "exists.sourceid.utmterm");
//                    }
//                }
//
//            }
//            if("000".equals(modelAndView.getStatus())){
//                adminResult.setStatus(AdminResult.SUCCESS);
//            }else{
//                adminResult.setStatus(UtmResultResponse.FAIL);
//                adminResult.setStatusDesc("参数异常！");
//            }
            adminResult.setStatus(AdminResult.SUCCESS);
        }else{
            adminResult.setStatus(UtmResultResponse.FAIL);
            adminResult.setStatusDesc("SourceId为空！");
        }
    }

    /**
     * 获取URL
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    private String getUrl(UtmChannelVO record){
        StringBuffer strBuf = new StringBuffer();
        strBuf.append(record.getLinkAddress());
        strBuf.append("?utm_id=");
        strBuf.append(record.getUtmId());
        if (StringUtils.isNotEmpty(record.getUtmSource())) {
            strBuf.append("&utm_source=");
            strBuf.append(toUnicode(record.getUtmSource()));
        }
        if (StringUtils.isNotEmpty(record.getUtmMedium())) {
            strBuf.append("&utm_medium=");
            strBuf.append(toUnicode(record.getUtmMedium()));

        }
        if (StringUtils.isNotEmpty(record.getUtmTerm())) {
            strBuf.append("&utm_term=");
            strBuf.append(toUnicode(record.getUtmTerm()));

        }
        if (StringUtils.isNotEmpty(record.getUtmContent())) {
            strBuf.append("&utm_content=");
            strBuf.append(toUnicode(record.getUtmContent()));

        }
        if (StringUtils.isNotEmpty(record.getUtmCampaign())) {
            strBuf.append("&utm_campaign=");
            strBuf.append(toUnicode(record.getUtmCampaign()));

        }
        if (record.getUtmReferrer() != null && record.getUtmReferrer() != 0) {
            strBuf.append("&utm_referrer=");
            strBuf.append(record.getUtmReferrer());

        }
        return strBuf.toString();
    }

    /**
     * Unicode
     *
     * @param str
     * @return
     * @throws UnsupportedEncodingException
     */
    private String toUnicode(String str) {
        try{
            return URLEncoder.encode(str, "UTF-8");
        }catch (Exception e){
            logger.info("UnsupportedEncodingException异常",e);
        }
        return null;
    }

    @Autowired
    private FileUpLoadUtil fileUpLoadUtil ;
    /**
     * 资料上传
     *
     * @param request
     * @return
     * @throws Exception
     */
    @ApiOperation(value = "资料上传", notes = "资料上传")
    @PostMapping("/upload")
    public AdminResult uploadFile(HttpServletRequest request) throws Exception {
        logger.info(ChannelController.class.toString(), "startLog -- /hyjf-admin/promotion/channel/upload");
        AdminResult adminResult = new AdminResult();
        adminResult.setData(fileUpLoadUtil.upLoad(request));
        adminResult.setStatus(SUCCESS);
        adminResult.setStatusDesc(SUCCESS_DESC);
        logger.info(ChannelController.class.toString(), "endLog -- /hyjf-admin/promotion/channel/upload");
        return adminResult;
    }

    /**
     * 导出功能
     * @param request
     * @param response
     * @throws Exception
     */
    @ApiOperation(value = "导出功能", notes = "导出功能")
    @PostMapping("/export")
    public void exportAction(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 表格sheet名称
        String sheetName = "推广管理模板";

        String fileName = URLEncoder.encode(sheetName, "UTF-8") + StringPool.UNDERLINE + GetDate.getServerDateTime(8, new Date()) + CustomConstants.EXCEL_EXT;

        String[] titles = new String[] { "渠道(utm_source)", "推广方式(utm_medium)", "推广单元(utm_content)", "推广计划(utm_campaign)", "关键字(utm_term)", "推荐人(utm_referrer)", "备注" };
        // 声明一个工作薄
        HSSFWorkbook workbook = new HSSFWorkbook();

        // 生成一个表格
        ExportExcel.createHSSFWorkbookTitle(workbook, titles, sheetName);

        // 导出
        ExportExcel.writeExcelFile(response, workbook, titles, fileName);
    }

}
