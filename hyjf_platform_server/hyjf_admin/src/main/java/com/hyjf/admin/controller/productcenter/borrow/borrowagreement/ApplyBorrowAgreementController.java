package com.hyjf.admin.controller.productcenter.borrow.borrowagreement;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.admin.common.result.AdminResult;
import com.hyjf.admin.common.result.BaseResult;
import com.hyjf.admin.controller.BaseController;
import com.hyjf.admin.service.ApplyBorrowAgreementService;
import com.hyjf.am.resquest.admin.*;
import com.hyjf.am.vo.config.AdminSystemVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @version ApplyBorrowAgreementController, v0.1 2018/8/9 16:36
 * @Author: Zha Daojian
 */

@Api(value = "Admin端借款中心-协议下载申请",tags="Admin端借款中心-协议下载申请")
@RestController
@RequestMapping("/hyjf-admin/applyborrowagreement")
public class ApplyBorrowAgreementController extends BaseController {

    @Autowired
    private ApplyBorrowAgreementService applyBorrowAgreementService;


    @ApiOperation(value = "协议申请列表", notes = "协议申请列表")
    @PostMapping("/getList")
    public AdminResult  getList(@RequestBody ApplyBorrowAgreementRequest request){
        AdminResult result = applyBorrowAgreementService.getApplyBorrowAgreementList(request);
        return result;
    }

    @ApiOperation(value = "协议申请明细页", notes = "协议申请明细页")
    @PostMapping("/getBorrowAndInfoDetail")
    public AdminResult addListDetail(@RequestBody ApplyBorrowInfoRequest request){
        AdminResult result =  applyBorrowAgreementService.getApplyBorrowInfoDetail(request);
        return result;
    }

    @ApiOperation(value = "申请协议", notes = "申请协议")
    @PostMapping("/addBorrowAgreement")
    public AdminResult addBorrowAgreement(HttpServletRequest request, @RequestBody ApplyBorrowInfoRequest applyBorrowInfoRequest){
        AdminSystemVO currUser = getUser(request);
        if(currUser == null){
            return new AdminResult(BaseResult.FAIL, "未获取到当前登录用户信息");
        }
        AdminResult result =  applyBorrowAgreementService.addBorrowAgreement(applyBorrowInfoRequest,currUser);
        return result;
    }

    @ApiOperation(value = "下載协议", notes = "下載协议")
    @PostMapping("/downloadAction")
    public void downloadAction(@RequestBody DownloadAgreementRequest requestBean,HttpServletResponse response){
        logger.info("------------------------------下載协议requestBean:"+JSONObject.toJSON(requestBean));
        applyBorrowAgreementService.downloadAction(requestBean,response);
    }
}
