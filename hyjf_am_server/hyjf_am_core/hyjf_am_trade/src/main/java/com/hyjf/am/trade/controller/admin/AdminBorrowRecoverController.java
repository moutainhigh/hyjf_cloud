package com.hyjf.am.trade.controller.admin;

import com.alibaba.fastjson.JSONObject;
import com.hyjf.am.response.admin.AdminBorrowRecoverResponse;
import com.hyjf.am.response.admin.AssetListCustomizeResponse;
import com.hyjf.am.response.trade.TenderDetailResponse;
import com.hyjf.am.resquest.admin.AssetListRequest;
import com.hyjf.am.resquest.admin.BorrowRecoverRequest;
import com.hyjf.am.trade.controller.AssetManageController;
import com.hyjf.am.trade.dao.model.customize.admin.AdminBorrowRecoverCustomize;
import com.hyjf.am.trade.dao.model.customize.trade.WebUserTradeListCustomize;
import com.hyjf.am.trade.service.admin.AdminBorrowRecoverService;
import com.hyjf.am.trade.service.admin.AdminHjhLabelService;
import com.hyjf.am.vo.admin.AssetListCustomizeVO;
import com.hyjf.am.vo.admin.BorrowRecoverCustomizeVO;
import com.hyjf.am.vo.trade.tradedetail.WebUserTradeListCustomizeVO;
import com.hyjf.common.paginator.Paginator;
import com.hyjf.common.util.CommonUtils;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * @author pangchengchao
 * @version AdminBorrowRecoverController, v0.1 2018/7/2 16:23
 */
@Api(value = "产品中心-汇直投-放款明细查询")
@RestController
@RequestMapping("/am-trade/adminBorrowRecover")
public class AdminBorrowRecoverController {
    private static Logger logger = LoggerFactory.getLogger(AssetManageController.class);
    @Autowired
    AdminBorrowRecoverService adminBorrowRecoverService;

    @RequestMapping(value = "/countBorrowRecover")
    public AdminBorrowRecoverResponse countBorrowRecover(@RequestBody @Valid BorrowRecoverRequest request){
        logger.info("request:" +JSONObject.toJSON(request));
        AdminBorrowRecoverResponse response = new AdminBorrowRecoverResponse();
        int count = this.adminBorrowRecoverService.countBorrowRecover(request);

        response.setTotal(count);
        return response;
    }

    @RequestMapping(value = "/selectBorrowRecoverList")
    public AdminBorrowRecoverResponse selectBorrowRecoverList(@RequestBody @Valid BorrowRecoverRequest request){
        logger.info("request:" +JSONObject.toJSON(request));
        AdminBorrowRecoverResponse response = new AdminBorrowRecoverResponse();

        List<AdminBorrowRecoverCustomize> list = adminBorrowRecoverService.selectBorrowRecoverList(request);
        if(!CollectionUtils.isEmpty(list)){
            List<BorrowRecoverCustomizeVO> voList = CommonUtils.convertBeanList(list, BorrowRecoverCustomizeVO.class);
            response.setResultList(voList);
        }
        return response;
    }

    @RequestMapping(value = "/sumBorrowRecoverList")
    public AdminBorrowRecoverResponse sumBorrowRecoverList(@RequestBody @Valid BorrowRecoverRequest request){
        logger.info("request:" +JSONObject.toJSON(request));
        AdminBorrowRecoverResponse response = new AdminBorrowRecoverResponse();

        AdminBorrowRecoverCustomize customize = adminBorrowRecoverService.sumBorrowRecoverList(request);
        if(customize!=null){
            BorrowRecoverCustomizeVO vo = CommonUtils.convertBean(customize,BorrowRecoverCustomizeVO.class);
            response.setResult(vo);
        }
        return response;
    }

}