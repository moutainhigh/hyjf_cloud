package com.hyjf.am.trade.controller.admin.productcenter.plancenter.planrepay;

import com.hyjf.am.response.Response;
import com.hyjf.am.response.trade.HjhRepayResponse;
import com.hyjf.am.resquest.admin.HjhRepayRequest;
import com.hyjf.am.resquest.admin.Paginator;
import com.hyjf.am.trade.service.admin.hjhplan.HjhRepayService;
import com.hyjf.am.vo.trade.hjh.HjhRepayVO;
import com.hyjf.common.util.CommonUtils;
import io.swagger.annotations.Api;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 汇计划-订单退出
 * @Author : huanghui
 */
@Api(value = "汇计划")
@RestController
@RequestMapping("/am-trade/hjhRepay")
public class HjhRepayController {

    @Autowired
    private HjhRepayService hjhRepayService;

    private static Logger logger = LoggerFactory.getLogger(HjhRepayController.class);

    /**
     * 计划还款列表
     * @param request
     * @return
     */
    @RequestMapping(value = "/hjhRepayList", method = RequestMethod.POST)
    public HjhRepayResponse hjhRepayList(@RequestBody HjhRepayRequest request){
        HjhRepayResponse response = new HjhRepayResponse();

        Map<String, Object> params = new HashedMap();
        if(org.apache.commons.lang.StringUtils.isNotEmpty(request.getAccedeOrderIdSrch())){
            params.put("planOrderId", request.getAccedeOrderIdSrch());
        }
        if(org.apache.commons.lang.StringUtils.isNotEmpty(request.getPlanNidSrch())){
            params.put("planNidSrch", request.getPlanNidSrch());
        }
        if(org.apache.commons.lang.StringUtils.isNotEmpty(request.getUserNameSrch())){
            params.put("userName", request.getUserNameSrch());
        }
        if(org.apache.commons.lang.StringUtils.isNotEmpty(request.getDebtLockPeriodSrch())){
            params.put("debtLockPeriodSrch", request.getDebtLockPeriodSrch());
        }
        if(org.apache.commons.lang.StringUtils.isNotEmpty(request.getRepayStatusSrch())){
            params.put("repayStatus", Integer.valueOf(request.getRepayStatusSrch()));
        }
        if(org.apache.commons.lang.StringUtils.isNotEmpty(request.getOrderStatusSrch())){
            params.put("orderStatusSrch", Integer.valueOf(request.getOrderStatusSrch()));
        }
        if(org.apache.commons.lang.StringUtils.isNotEmpty(request.getBorrowStyleSrch())){
            params.put("borrowStyleSrch", request.getBorrowStyleSrch());
        }

        //清算时间
        if (StringUtils.isNotEmpty(request.getRepayTimeStart())){
            params.put("repayTimeStart", request.getRepayTimeStart() + " 00:00:00");
            params.put("repayTimeEnd", request.getRepayTimeEnd() + " 23:59:59");
        }

        //实际退出时间
        if (StringUtils.isNotEmpty(request.getActulRepayTimeStart())){
            params.put("actulRepayTimeStart", request.getActulRepayTimeStart() + " 00:00:00");
            params.put("actulRepayTimeEnd", request.getActulRepayTimeEnd() + "23:59:59");
        }

        // 汇计划三期新增 推荐人查询
        if(org.apache.commons.lang.StringUtils.isNotEmpty(request.getRefereeNameSrch())){
            params.put("refereeNameSrch", request.getRefereeNameSrch());
        }
        // 查询 总条数
        Integer count = this.hjhRepayService.getRepayCount(params);

        if (request.getCurrPage() > 0){

            Paginator paginator = new Paginator(request.getCurrPage(), count);
            params.put("limitStart", paginator.getOffset());
            if (request.getPageSize() > 0){
                params.put("limitEnd", request.getPageSize());
            }else {
                // paginator.getLimit() 默认 10条
                params.put("limitEnd", paginator.getLimit());
            }
        }

        List<HjhRepayVO> repayVOList = this.hjhRepayService.selectByExample(params);

        // 初始化总计数据
        BigDecimal sumAccedeAccount = BigDecimal.ZERO;
        BigDecimal sumRepayInterest = BigDecimal.ZERO;
        // 汇计划三期 实际收益 总计
        BigDecimal sumActualRevenue = BigDecimal.ZERO;
        // 汇计划三期 实际回款 总计
        BigDecimal sumActualPayTotal = BigDecimal.ZERO;
        // 汇计划三期 清算服务费 总计
        BigDecimal sumLqdServiceFee = BigDecimal.ZERO;

        for(int i = 0; i < repayVOList.size(); i++){
            if (repayVOList.get(i).getAccedeAccount() == null){
                sumAccedeAccount = BigDecimal.ZERO;
            }else {
                sumAccedeAccount = sumAccedeAccount.add(repayVOList.get(i).getAccedeAccount());
            }
            if (repayVOList.get(i).getRepayInterest() == null){
                sumRepayInterest = BigDecimal.ZERO;
            }else {
                sumRepayInterest = sumRepayInterest.add(repayVOList.get(i).getRepayInterest());
            }
            if (repayVOList.get(i).getActualRevenue() == null){
                sumActualRevenue = BigDecimal.ZERO;
            }else {
                sumActualRevenue = sumActualRevenue.add(repayVOList.get(i).getActualRevenue());
            }
            if (repayVOList.get(i).getActualPayTotal() == null){
                sumActualPayTotal = BigDecimal.ZERO;
            }else {
                sumActualPayTotal = sumActualPayTotal.add(repayVOList.get(i).getActualPayTotal());
            }
            if (repayVOList.get(i).getLqdServiceFee() == null){
                sumLqdServiceFee = BigDecimal.ZERO;
            }else {
                sumLqdServiceFee = sumLqdServiceFee.add(repayVOList.get(i).getLqdServiceFee());
            }
        }

        if (!CollectionUtils.isEmpty(repayVOList)){
            response.setResultList(repayVOList);
            response.setCount(count);
            HjhRepayVO sumHjhRepayVO = new HjhRepayVO();
            sumHjhRepayVO.setAccedeAccount(sumAccedeAccount);
            sumHjhRepayVO.setRepayInterest(sumRepayInterest);
            sumHjhRepayVO.setActualRevenue(sumActualRevenue);
            sumHjhRepayVO.setActualPayTotal(sumActualPayTotal);
            sumHjhRepayVO.setLqdServiceFee(sumLqdServiceFee);
            response.setSumHjhRepayVO(sumHjhRepayVO);
            response.setRtn(Response.SUCCESS);
        }
        return response;
    }

    @RequestMapping(value = "/hjhRepaymentDetails/{accedeOrderId}")
    public HjhRepayResponse hjhRepaymentDetails(@PathVariable String accedeOrderId){
        HjhRepayResponse response = new HjhRepayResponse();


        List<HjhRepayVO> hjhRepayMentDetailList = hjhRepayService.selectByAccedeOrderId(accedeOrderId);

        if (!CollectionUtils.isEmpty(hjhRepayMentDetailList)){
            List<HjhRepayVO> hjhRepayMentDetailVoList = CommonUtils.convertBeanList(hjhRepayMentDetailList, HjhRepayVO.class);
            response.setResultList(hjhRepayMentDetailVoList);
        }
        return  response;
    }
}
