package com.hyjf.am.trade.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.admin.AdminBorrowRepaymentInfoResponse;
import com.hyjf.am.response.admin.AdminBorrowRepaymentResponse;
import com.hyjf.am.resquest.admin.BorrowRecoverRequest;
import com.hyjf.am.resquest.admin.BorrowRepaymentInfoRequset;
import com.hyjf.am.trade.controller.AssetManageController;
import com.hyjf.am.trade.dao.model.customize.admin.AdminBorrowRepaymentCustomize;
import com.hyjf.am.trade.dao.model.customize.admin.AdminBorrowRepaymentInfoCustomize;
import com.hyjf.am.trade.service.admin.AdminBorrowRepaymentInfoService;
import com.hyjf.am.trade.service.admin.AdminBorrowRepaymentService;
import com.hyjf.am.vo.admin.BorrowRepaymentCustomizeVO;
import com.hyjf.am.vo.admin.BorrowRepaymentInfoCustomizeVO;
import com.hyjf.common.util.CommonUtils;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

/**
 * @author pangchengchao
 * @version AdminBorrowRepaymentInfoController, v0.1 2018/7/9 9:48
 */
@Api(value = "产品中心-汇直投-还款明细查询")
@RestController
@RequestMapping("/am-trade/adminBorrowRepaymentInfo")
public class AdminBorrowRepaymentInfoController {
    private static Logger logger = LoggerFactory.getLogger(AssetManageController.class);
    @Autowired
    AdminBorrowRepaymentInfoService adminBorrowRepaymentInfoService;

    @RequestMapping(value = "/countBorrowRepaymentInfo")
    public AdminBorrowRepaymentInfoResponse countBorrowRepaymentInfo(@RequestBody @Valid BorrowRepaymentInfoRequset request){
        logger.info("request:" +JSONObject.toJSON(request));
        AdminBorrowRepaymentInfoResponse response = new AdminBorrowRepaymentInfoResponse();
        int count = this.adminBorrowRepaymentInfoService.countBorrowRecover(request);

        response.setTotal(count);
        return response;
    }

    @RequestMapping(value = "/selectBorrowRepaymentInfoListForView")
    public AdminBorrowRepaymentInfoResponse selectBorrowRepaymentInfoListForView(@RequestBody @Valid BorrowRepaymentInfoRequset request){
        logger.info("request:" +JSONObject.toJSON(request));
        AdminBorrowRepaymentInfoResponse response = new AdminBorrowRepaymentInfoResponse();

        List<AdminBorrowRepaymentInfoCustomize> list = adminBorrowRepaymentInfoService.selectBorrowRepaymentInfoListForView(request);
        if(!CollectionUtils.isEmpty(list)){
            List<BorrowRepaymentInfoCustomizeVO> voList = CommonUtils.convertBeanList(list, BorrowRepaymentInfoCustomizeVO.class);
            response.setResultList(voList);
        }
        return response;
    }

    @RequestMapping(value = "/sumBorrowRepaymentInfo")
    public AdminBorrowRepaymentInfoResponse sumBorrowRepaymentInfo(@RequestBody @Valid BorrowRepaymentInfoRequset request){
        logger.info("request:" +JSONObject.toJSON(request));
        AdminBorrowRepaymentInfoResponse response = new AdminBorrowRepaymentInfoResponse();

        AdminBorrowRepaymentInfoCustomize customize = adminBorrowRepaymentInfoService.sumBorrowRecoverList(request);
        if(customize!=null){
            BorrowRepaymentInfoCustomizeVO vo = CommonUtils.convertBean(customize,BorrowRepaymentInfoCustomizeVO.class);
            response.setResult(vo);
        }
        return response;
    }

    @RequestMapping(value = "/selectBorrowRepaymentInfoList")
    public AdminBorrowRepaymentInfoResponse selectBorrowRepaymentInfoList(@RequestBody @Valid BorrowRepaymentInfoRequset request){
        logger.info("request:" +JSONObject.toJSON(request));
        AdminBorrowRepaymentInfoResponse response = new AdminBorrowRepaymentInfoResponse();

        List<AdminBorrowRepaymentInfoCustomize> list = adminBorrowRepaymentInfoService.selectBorrowRepaymentInfoList(request);
        if(!CollectionUtils.isEmpty(list)){
            List<BorrowRepaymentInfoCustomizeVO> voList = CommonUtils.convertBeanList(list, BorrowRepaymentInfoCustomizeVO.class);
            response.setResultList(voList);
        }
        return response;
    }

}
