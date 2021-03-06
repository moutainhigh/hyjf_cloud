/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.trade.controller.batch;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.vo.admin.NifaReportLogVO;
import com.hyjf.am.vo.hgreportdata.nifa.NifaBorrowInfoVO;
import com.hyjf.am.vo.trade.borrow.BorrowApicronVO;
import com.hyjf.am.vo.trade.borrow.BorrowRepayPlanVO;
import com.hyjf.am.vo.trade.borrow.BorrowRepayVO;
import com.hyjf.common.constants.MQConstant;
import com.hyjf.common.http.HttpDeal;
import com.hyjf.common.util.GetDate;
import com.hyjf.cs.common.controller.BaseController;
import com.hyjf.cs.trade.mq.base.CommonProducer;
import com.hyjf.cs.trade.mq.base.MessageContent;
import com.hyjf.cs.trade.service.batch.NifaFileDualService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author yaoyong
 * @version NifaFileController, v0.1 2018/12/18 16:43
 * 互金反馈文件
 */
@RestController
@ApiIgnore
@RequestMapping("/cs-trade/nifaFileDeal")
public class NifaFileController extends BaseController {

    @Autowired
    private NifaFileDualService nifaFileDealService;

    /**
     * 获取上传地址前缀
     */
    @Value("${hyjf.nifa.upload.url}")
    private String UPLOAD_URL;

    /**
     * 获取18位社会信用代码
     */
    @Value("${hyjf.com.social.credit.code}")
    private String COM_SOCIAL_CREDIT_CODE;

    /**
     * 反馈文件下载地址
     */
    @Value("${hyjf.nifa.feedback.path}")
    private String FEED_BACK_PATH;

    /**
     * 互金反馈文件下载地址
     */
    @Value("${hyjf.nifa.download.path}")
    private String DOWNLOAD_PATH;

    /**
     * 互金上传文件上报数据
     *
     * @return
     */
    @RequestMapping("/uploadFile")
    public boolean uploadFile() {
        try {
            logger.info("------【互金上传文件】上传开始------");

            // 各种时间格式
            SimpleDateFormat date_sdf = new SimpleDateFormat("yyyy-MM-dd");

            // 地址末尾撇拼接反斜杠
            String feedBackPath = FEED_BACK_PATH;
            if (!feedBackPath.endsWith("/")) {
                feedBackPath = feedBackPath.concat("/");
            }

            // 获取前一天日期yyyyMMdd
            String beforDay = GetDate.yyyyMMdd.format(GetDate.countDate(new Date(), 5, -1));
            String beforeSdfDay = date_sdf.format(GetDate.countDate(new Date(), 5, -1));
            // 登记系统业务数据文件名
            String businessDataFileName = COM_SOCIAL_CREDIT_CODE.concat(beforDay).concat("33001");
            // 登记系统合同模板文件
            String contractTemplateFileName = COM_SOCIAL_CREDIT_CODE.concat(beforDay).concat("34001");
            // 统计系统 24 互联网债权类融资
            String businessZhaiqFileName = COM_SOCIAL_CREDIT_CODE.concat(beforDay).concat("24001");
            // 26 互联网金融产品及收益权转让融资
            String businessJinrFileName = COM_SOCIAL_CREDIT_CODE.concat(beforDay).concat("26001");

            // 拉取数据生成文件并更新数据库
            boolean fileMakeResult = nifaFileDealService.insertMakeFileReportLog(businessDataFileName, contractTemplateFileName);
            if (!fileMakeResult) {
                logger.error("【互金上传文件】文件作成失败！");
            }
            // 统计系统拉取数据生成文件并更新数据库、根据前一天日期生成数据
            fileMakeResult = nifaFileDealService.insertMonitorMakeZhaiFileReportLog(businessZhaiqFileName, beforeSdfDay);
            if (!fileMakeResult) {
                logger.error("【互金上传文件】统计债权系统文件作成失败！");
            }
            fileMakeResult = nifaFileDealService.insertMonitorMakeJinrFileReportLog(businessJinrFileName, beforeSdfDay);
            if (!fileMakeResult) {
                logger.error("【互金上传文件】统计转让系统文件作成失败！");
            }

            // 拉取未成功上传的文件名集合（可能会包含之前上传未成功的数据）
            List<NifaReportLogVO> nifaReportLogList = nifaFileDealService.selectNifaReportLogList();
            if (nifaReportLogList == null || nifaReportLogList.size() == 0) {
                logger.info("【互金上传文件】未获取到需要上传的数据！");
                return true;
            }

            // 循环文件名集合
            for (NifaReportLogVO nifaReportLog : nifaReportLogList) {
                // 记录更新时间
                nifaReportLog.setUpdateTime(new Date());

                // 判断该条数据对应上传文件类型（后面有时间在数据库增加类型字段）
                String uploadType = nifaReportLog.getUploadName().substring(26, 28);
                String requestURL = "";
                // 登记系统拼接请求地址
                if ("33".equals(uploadType) || "34".equals(uploadType)) {
                    // 拼接请求地址
                    requestURL = UPLOAD_URL.concat("?systemid=2&stype=")
                            .concat(uploadType).concat("&sourcePath=")
                            .concat(nifaReportLog.getUploadPath())
                            .concat(nifaReportLog.getUploadName())
                            .concat(".zip");
                }

                // 统计系统拼接请求地址
                if ("24".equals(uploadType) || "26".equals(uploadType)) {
                    requestURL = UPLOAD_URL.concat("?systemid=1&stype=")
                            .concat(uploadType).concat("&sourcePath=")
                            .concat(nifaReportLog.getUploadPath())
                            .concat(nifaReportLog.getUploadName())
                            .concat(".zip");
                }

                if (StringUtils.isBlank(requestURL)) {
                    logger.error("【互金上传文件】文件上传地址拼接错误，文件id：" + nifaReportLog.getId());
                    continue;
                }

                // 文件上传请求
                logger.info("【互金上传文件】get请求开始：" + GetDate.getNowTime());
                String uploadResult = HttpDeal.get(requestURL);
                logger.info("【互金上传文件】get请求结束：" + GetDate.getNowTime());
                // 上传结果解析
                JSONObject jsonObject = JSONObject.parseObject(uploadResult);
                if (!"true".equals(jsonObject.get("success"))) {
                    logger.error("【互金上传文件】上传错误，返回错误信息：" + jsonObject);
                    // 更新上传结果
                    nifaReportLog.setFileUploadStatus(2);
                    nifaFileDealService.updateNifaReportLog(nifaReportLog);
                    continue;
                }

                // 读取上传异步回调文件
                // 异步回传文件地址与前置服务配置匹配写到环境变量
                String filePathName = feedBackPath.concat(nifaReportLog.getUploadName()).concat(".txt");
                String fileReadResult = nifaFileDealService.UploadResultFileRead(filePathName);
                if (StringUtils.isBlank(fileReadResult)) {
                    logger.error("【互金上传文件】互金上传文件反馈信息为空");
                    nifaReportLog.setFileUploadStatus(2);
                } else if ("ERROR".equals(fileReadResult)) {
                    logger.error("【互金上传文件】上传反馈文件解析失败");
                    nifaReportLog.setFileUploadStatus(2);
                } else {
                    logger.info("【互金上传文件】上传状态：" + fileReadResult);
                    nifaReportLog.setFileUploadStatus(1);
                    // 删除filePathName文件
                    File file = new File(filePathName);
                    if (file.exists() && file.isFile()) {
                        file.delete();
                    }
                }
                nifaReportLog.setUploadTime(GetDate.getNowTime10());
                // 更新上传结果
                boolean result = nifaFileDealService.updateNifaReportLog(nifaReportLog);
                if (!result) {
                    logger.error("【互金上传文件】更新上传结果失败!");
                }
            }
            return true;
        } catch (Exception e) {
            logger.error("------【互金上传文件】上传失败------", e);
            return false;
        } finally {
            logger.info("------【互金上传文件】上传结束------");
        }
    }

    /**
     * 互金下载反馈文件
     *
     * @return
     */
    @RequestMapping("/downloadFile")
    public boolean downFile() {
        logger.info("------【互金下载反馈文件】处理开始------");
        try {

            SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

            // 获取当天日期yyyyMMdd
            String nowDay = yyyyMMdd.format(new Date());
            // 获取未成功下载日志的数据
            List<NifaReportLogVO> nifaReportLogList = nifaFileDealService.selectNifaReportLogDownloadPath();
            if (null == nifaReportLogList || nifaReportLogList.size() <= 0) {
                logger.info("【互金下载反馈文件】未获取到需要下载反馈文件的数据！");
                return true;
            }

            // 循环文件名集合
            for (NifaReportLogVO nifaReportLog : nifaReportLogList) {
                Integer downloadResult = 0;
                // 记录更新时间
                nifaReportLog.setUpdateTime(new Date());

                String feedBackType = nifaReportLog.getUploadName().substring(26, 28);
                String filePathDate = nifaReportLog.getUploadName().substring(18, 26);
                // 登记接口下载
                if ("33".equals(feedBackType) || "34".equals(feedBackType)) {
                    downloadResult = this.nifaFileDealService.downloadFiles(filePathDate) ? 1 : 2;
                    // 文件名称
                    nifaReportLog.setFeedbackName(filePathDate);
                }
                // 统计二期接口下载
                if ("24".equals(feedBackType) || "26".equals(feedBackType)) {
                    downloadResult = this.nifaFileDealService.downloadFilesByUrl(nifaReportLog, feedBackType, filePathDate) ? 1 : 2;
                    // 文件名称
                    nifaReportLog.setFeedbackName(nifaReportLog.getUploadName().concat("1"));
                }
                // 更新结果
                nifaReportLog.setFeedbackResult(downloadResult);
                // 文件地址
                nifaReportLog.setFeedbackPath(DOWNLOAD_PATH + filePathDate);
                // 更新下载结果
                boolean result = nifaFileDealService.updateNifaReportLog(nifaReportLog);
                if (!result) {
                    logger.error("【互金下载反馈文件】更新下载结果失败!");
                }
            }
        } catch (Exception e) {
            logger.info("------【互金下载反馈文件】处理失败------", e);
            return false;
        }
        logger.info("------【互金下载反馈文件】处理结束------");
        return true;
    }
}
