/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.trade.controller.batch;

import com.hyjf.am.vo.trade.hjh.HjhPlanVO;
import com.hyjf.cs.trade.controller.BaseTradeController;
import com.hyjf.cs.trade.service.batch.WbsHjhPlanInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.util.List;

/**
 * WBS系统
 *
 * @author liuyang
 * @version WbsHjhPlanInfoController, v0.1 2019/4/15 18:00
 */
@ApiIgnore
@RestController
@RequestMapping("/cs-trade/wbsHjhPlanInfo")
public class WbsHjhPlanInfoController extends BaseTradeController {

    @Autowired
    private WbsHjhPlanInfoService wbsHjhPlanInfoService;

    @RequestMapping(value = "/wbsSendHjhPlanInfo")
    public void wbsSendHjhPlanInfo() {
        List<HjhPlanVO> resultList = wbsHjhPlanInfoService.selectWbsSendHjhPlanList();
        if (!CollectionUtils.isEmpty(resultList)) {
            for (HjhPlanVO hjhPlanVO : resultList) {
                // 计划编号
                String planNid = hjhPlanVO.getPlanNid();
                // 投资状态 0 全部；1 启用；2 关闭'
                Integer planInvestStatus = hjhPlanVO.getPlanInvestStatus();
                // 显示状态字段 1显示 2 隐藏'
                Integer planDisplayStatus = hjhPlanVO.getPlanDisplayStatus();
                // 计划可投金额
                BigDecimal planBalance = hjhPlanVO.getAvailableInvestAccount();
                // 可投余额 > 0
                if (planInvestStatus == 1 && planDisplayStatus == 1 && planBalance.compareTo(BigDecimal.ZERO) > 0) {
                    // 智投开启的状态:开启
                    try {
                        this.wbsHjhPlanInfoService.sendWbsPlanInfoMQ(planNid, "7", 1);
                    } catch (Exception e) {
                        logger.error("WBS系统发送智投服务MQ失败,[" + e + "].");
                    }
                } else {
                    // 智投开启的状态:关闭
                    try {
                        this.wbsHjhPlanInfoService.sendWbsPlanInfoMQ(planNid, "8", 1);
                    } catch (Exception e) {
                        logger.error("WBS系统发送智投服务MQ失败,[" + e + "].");
                    }
                }
            }
        }
    }
}
