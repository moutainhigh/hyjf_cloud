/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.message.controller.hgreportdata.cert;

import com.hyjf.am.response.BooleanResponse;
import com.hyjf.am.resquest.hgreportdata.cert.CertReportEntitRequest;
import com.hyjf.am.vo.hgreportdata.cert.CertReportEntityVO;
import com.hyjf.cs.common.controller.BaseController;
import com.hyjf.cs.message.bean.hgreportdata.cert.CertReportEntity;
import com.hyjf.cs.message.service.hgreportdata.cert.CertStatisticalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

/**
 * 国家应急中心相关mongo处理
 *
 * @author nxl
 * @version CertStatisticalController, v0.1 2019/1/18 17:26
 */
@ApiIgnore
@RestController
@RequestMapping("/cs-message/certStatistical")
public class CertStatisticalController extends BaseController {

    @Autowired
    CertStatisticalService certStatisticalService;


    /**
     * 插入mongo，保存报送记录
     *
     * @param certReportEntityVO
     * @return
     */
    @PostMapping("/insertAndSendPost")
    public BooleanResponse insertNifaBorrowerInfo(@RequestBody @Valid CertReportEntityVO certReportEntityVO) {
        if(null!=certReportEntityVO){
            CertReportEntity certReportEntity = new CertReportEntity();
            BeanUtils.copyProperties(certReportEntityVO,certReportEntity);
            certStatisticalService.insertAndSendPost(certReportEntity);
            return new BooleanResponse(true);
        }
        return new BooleanResponse(false);
    }

    /**
     * 修改mongo，修改报送状态
     *
     * @param certReportEntityVO
     * @return
     */
    @PostMapping("/updateCertReport")
    public BooleanResponse updateAndSendPost(@RequestBody @Valid CertReportEntitRequest certReportEntityVO) {
        if(null!=certReportEntityVO){
            certStatisticalService.updateCertReport(certReportEntityVO);
            return new BooleanResponse(true);
        }
        return new BooleanResponse(false);
    }
}
