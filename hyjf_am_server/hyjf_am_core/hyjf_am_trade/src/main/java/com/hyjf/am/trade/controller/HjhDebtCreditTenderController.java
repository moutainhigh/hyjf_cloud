/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.controller;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hyjf.am.response.trade.HjhDebtCreditTenderResponse;
import com.hyjf.am.resquest.trade.HjhDebtCreditTenderRequest;
import com.hyjf.am.trade.dao.model.auto.HjhDebtCreditTender;
import com.hyjf.am.trade.service.HjhDebtCreditTenderService;
import com.hyjf.am.vo.trade.hjh.HjhDebtCreditTenderVO;
import com.hyjf.common.util.CommonUtils;

import io.swagger.annotations.Api;

/**
 * @author jijun
 * @date 20180629
 */

@Api(value = "汇计划债转投资")
@RestController
@RequestMapping("/am-trade/hjhDebtCreditTender")
public class HjhDebtCreditTenderController extends BaseController{

    @Autowired
    HjhDebtCreditTenderService hjhDebtCreditTenderService;

    @PostMapping("/getHjhDebtCreditTenderList")
    public HjhDebtCreditTenderResponse getHjhDebtCreditTenderList(@RequestBody HjhDebtCreditTenderRequest request) {
        HjhDebtCreditTenderResponse response = new HjhDebtCreditTenderResponse();
        List<HjhDebtCreditTender> hjhDebtCreditTenders = hjhDebtCreditTenderService.getHjhDebtCreditTenderList(request);
        if(CollectionUtils.isNotEmpty(hjhDebtCreditTenders)){
            response.setResultList(CommonUtils.convertBeanList(hjhDebtCreditTenders,HjhDebtCreditTenderVO.class));
        }
        return response;
    }

}