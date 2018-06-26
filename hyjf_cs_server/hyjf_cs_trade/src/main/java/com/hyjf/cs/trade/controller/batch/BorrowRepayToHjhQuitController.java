/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.cs.trade.controller.batch;

import com.hyjf.am.vo.trade.hjh.HjhAccedeVO;
import com.hyjf.common.cache.RedisConstants;
import com.hyjf.common.cache.RedisUtils;
import com.hyjf.cs.trade.service.BorrowRepayToHjhQuitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author PC-LIUSHOUYI
 * @version BorrowRepayToHjhQuitController, v0.1 2018/6/25 9:33
 */
@RestController
@RequestMapping("/borrowRepay")
public class BorrowRepayToHjhQuitController {
    private static final Logger logger = LoggerFactory.getLogger(BorrowRepayToHjhQuitController.class);

    @Autowired
    private BorrowRepayToHjhQuitService borrowRepayToHjhQuitService;

    @RequestMapping("/hjhQuit")
    public void entryUpdate(){
        logger.info("【汇计划计划进入锁定期/退出计划开始】开始。。。");
//        borrowRepayToHjhQuitService.selectWaitQuitHjhList();
        List<HjhAccedeVO> accedeList = borrowRepayToHjhQuitService.selectWaitQuitHjhList();
        if (accedeList != null) {
            for (int i = 0; i < accedeList.size(); i++) {
                HjhAccedeVO accede = accedeList.get(i);
                if(isRepeat(accede.getAccedeOrderId())){
                    // 发送短信提示
//                    String key = "hyjf-routingkey-Repay-hjhQuit";
//                    sendMessage(key, accede);
                }else {
                    logger.info("-----------------汇计划计划进入锁定期/退出计划执行中，请勿重复执行，订单号："+ accede.getAccedeOrderId() +"--------------------------------");
                }

            }
        }
        logger.info("【汇计划计划进入锁定期/退出计划开始】结束。。。");
    }
    /**
     * 判断是否重复推送
     * @return
     * @param accedeOrderId
     */
    private boolean isRepeat(String accedeOrderId) {

        String key = RedisConstants.HJH_LOCK_REPEAT;
        Boolean sismember = RedisUtils.sismember(key, accedeOrderId);
        if (sismember){
            return false;
        }
        RedisUtils.sadd(key,accedeOrderId);
        return true;
    }
}