/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.controller;

import com.hyjf.am.response.MapResponse;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.trade.HjhAccedeResponse;
import com.hyjf.am.resquest.trade.SaveCreditTenderLogRequest;
import com.hyjf.am.resquest.trade.UpdateBorrowForAutoTenderRequest;
import com.hyjf.am.resquest.trade.UpdateCreditForAutoTenderRequest;
import com.hyjf.am.trade.dao.model.auto.Borrow;
import com.hyjf.am.trade.dao.model.auto.HjhAccede;
import com.hyjf.am.trade.dao.model.auto.HjhDebtCredit;
import com.hyjf.am.trade.dao.model.auto.HjhPlan;
import com.hyjf.am.trade.dao.model.customize.trade.HjhAccedeCustomize;
import com.hyjf.am.trade.service.AutoTenderService;
import com.hyjf.am.vo.trade.hjh.HjhAccedeVO;
import com.hyjf.am.vo.trade.hjh.HjhDebtCreditVO;
import com.hyjf.common.util.CommonUtils;
import com.hyjf.pay.lib.bank.bean.BankCallBean;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 自动投资（原子层）
 *
 * @author liubin
 * @version AutoTenderController, v0.1 2018/6/28 19:08
 */
@RestController
@RequestMapping("/am-trade/autoTenderController")
public class AutoTenderController extends BaseController {

    @Autowired
    private AutoTenderService autoTenderService;

    @RequestMapping("/selectPlanJoinList")
    public HjhAccedeResponse selectPlanJoinList() {
        HjhAccedeResponse response = new HjhAccedeResponse();
        List<HjhAccedeCustomize> list = autoTenderService.selectPlanJoinList();
        if (!CollectionUtils.isEmpty(list)) {
            response.setResultList(CommonUtils.convertBeanList(list, HjhAccedeVO.class));
        }
        return response;
    }

    @RequestMapping("/saveCreditTenderLog")
    public MapResponse saveCreditTenderLog(@RequestBody SaveCreditTenderLogRequest request) {
        MapResponse response = new MapResponse();
        response.setResultMap(this.autoTenderService.saveCreditTenderLog(
                                                        request.getCredit(),
                                                        request.getHjhAccede(),
                                                        request.getOrderId(),
                                                        request.getOrderDate(),
                                                        request.getYujiAmoust(),
                                                        request.isLast()));
        return response;
    }

    @RequestMapping("/getPreCreditCapital")
    public Response<BigDecimal> getPreCreditCapital(@RequestBody HjhDebtCreditVO hjhDebtCreditVO) {
        HjhDebtCredit hjhDebtCredit = new HjhDebtCredit();
        BeanUtils.copyProperties(hjhDebtCreditVO, hjhDebtCredit);
        return new Response(this.autoTenderService.getPreCreditCapital(hjhDebtCredit));
    }

    /**
     * 银行自动投资成功后，更新投资数据
     *
     * @param UpdateBorrowForAutoTenderRequest
     * @return
     */
    @RequestMapping("/updateBorrowForAutoTender")
    public Response updateBorrowForAutoTender(@RequestBody UpdateBorrowForAutoTenderRequest UpdateBorrowForAutoTenderRequest) {
        Borrow borrow = new Borrow();
        HjhAccede hjhAccede = new HjhAccede();
        BankCallBean bean = new BankCallBean();
        BeanUtils.copyProperties(UpdateBorrowForAutoTenderRequest.getBorrowVO(), borrow);
        BeanUtils.copyProperties(UpdateBorrowForAutoTenderRequest.getHjhAccedeVO(), hjhAccede);
        BeanUtils.copyProperties(UpdateBorrowForAutoTenderRequest.getBankCallBeanVO(), bean);
        boolean result = this.autoTenderService.updateBorrowForAutoTender(borrow, hjhAccede, bean);
        if (!result) {
            return new Response(Response.FAIL, Response.FAIL_MSG);
        }
        return new Response();
    }

    /**
     * 银行自动债转成功后，更新债转数据
     *
     * @param UpdateCreditForAutoTenderRequest
     * @return
     */
    @RequestMapping("/updateCreditForAutoTender")
    public Response updateCreditForAutoTender(@RequestBody UpdateCreditForAutoTenderRequest UpdateCreditForAutoTenderRequest) {
        HjhDebtCredit credit = new HjhDebtCredit();
        HjhAccede hjhAccede = new HjhAccede();
        HjhPlan hjhPlan = new HjhPlan();
        BankCallBean bean = new BankCallBean();
        String tenderUsrcustid = UpdateCreditForAutoTenderRequest.getTenderUsrcustid();
        String sellerUsrcustid = UpdateCreditForAutoTenderRequest.getSellerUsrcustid();
        Map<String, Object> resultMap = UpdateCreditForAutoTenderRequest.getResultMap();
        BeanUtils.copyProperties(UpdateCreditForAutoTenderRequest.getHjhDebtCreditVO(), credit);
        BeanUtils.copyProperties(UpdateCreditForAutoTenderRequest.getHjhAccedeVO(), hjhAccede);
        BeanUtils.copyProperties(UpdateCreditForAutoTenderRequest.getHjhPlanVO(), hjhPlan);
        BeanUtils.copyProperties(UpdateCreditForAutoTenderRequest.getBankCallBeanVO(), bean);
        boolean result = this.autoTenderService.updateCreditForAutoTender(
                credit, hjhAccede, hjhPlan, bean,
                tenderUsrcustid, sellerUsrcustid, resultMap
        );
        if (!result) {
            return new Response(Response.FAIL, Response.FAIL_MSG);
        }
        return new Response();
    }

    /**
     * 根据是否原始债权获出让人投标成功的授权号
     *
     * @param tenderOrderId
     * @param sourceType
     * @return
     */
    @RequestMapping("/getSellerAuthCode/{tenderOrderId}/{sourceType}")
    public Response<String> getSellerAuthCode(@PathVariable String tenderOrderId, @PathVariable Integer sourceType) {
        String authCode = this.autoTenderService.getSellerAuthCode(tenderOrderId, sourceType);
        return new Response(authCode);
    }
}
