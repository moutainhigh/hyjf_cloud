/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.market.controller.admin.pointsshop.duiba.points;

import com.hyjf.am.market.controller.admin.activity.ActivityController;
import com.hyjf.am.market.dao.model.auto.DuibaPointsModify;
import com.hyjf.am.market.service.pointsshop.duiba.points.DuibaPointsModifyService;
import com.hyjf.am.response.BooleanResponse;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.DuibaPointsModifyResponse;
import com.hyjf.am.resquest.admin.DuibaPointsRequest;
import com.hyjf.am.resquest.admin.Paginator;
import com.hyjf.am.vo.admin.DuibaPointsModifyVO;
import com.hyjf.am.vo.admin.DuibaPointsVO;
import com.hyjf.common.util.CommonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 兑吧积分调整表
 * @author PC-LIUSHOUYI
 * @version DuibaPointsModifyController, v0.1 2019/5/29 9:47
 */
@RestController
@RequestMapping("/am-admin/duibapointsmodify")
public class DuibaPointsModifyController {

    private static final Logger logger = LoggerFactory.getLogger(ActivityController.class);

    @Autowired
    DuibaPointsModifyService duibaPointsModifyService;

    /**
     * 根据筛选条件查找
     *
     * @param request
     * @return
     */
    @RequestMapping("/selectDuibaPointsModifyList")
    public DuibaPointsModifyResponse selectDuibaPointsList(@RequestBody DuibaPointsRequest request) {
        DuibaPointsModifyResponse response = new DuibaPointsModifyResponse();
        //查询条数
        Integer recordTotal = this.duibaPointsModifyService.selectDuibaPointsModifyCount(request);

        if (recordTotal > 0) {
            // 查询列表传入分页
            Paginator paginator;
            if (request.getPageSize() == 0) {
                // 前台传分页
                paginator = new Paginator(request.getCurrPage(), recordTotal);
            } else {
                // 前台未传分页那默认 10
                paginator = new Paginator(request.getCurrPage(), recordTotal, request.getPageSize());
            }
            request.setLimitStart(paginator.getOffset());
            request.setLimitEnd(paginator.getLimit());
            request.setPaginator(paginator);
            List<DuibaPointsModifyVO> recordList = duibaPointsModifyService.selectDuibaPointsModifyList(request);
            if (CollectionUtils.isNotEmpty(recordList)) {
                response.setResultList(recordList);
                response.setRecordTotal(recordTotal);
                response.setRtn(Response.SUCCESS);
            }
        }
        return response;
    }

    /**
     * 插入积分审批表
     *
     * @param duibaPointsModifyVO
     * @return
     */
    @RequestMapping("/insertPointsModifyList")
    public BooleanResponse insertPointsModifyList(@RequestBody DuibaPointsModifyVO duibaPointsModifyVO) {
        BooleanResponse response = new BooleanResponse();
        boolean result = false;
        try {
            result = duibaPointsModifyService.insertPointsModifyList(duibaPointsModifyVO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setResultBoolean(result);
        return response;
    }

    /**
     * 更新兑吧积分调整审批状态
     *
     * @param request
     * @return
     */
    @RequestMapping("/updatePointsModifyStatus")
    public BooleanResponse updatePointsModifyStatus(@RequestBody DuibaPointsRequest request) {
        BooleanResponse response = new BooleanResponse();
        boolean result = false;
        try {
            result = duibaPointsModifyService.updatePointsModifyStatus(request);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setResultBoolean(result);
        return response;
    }

    /**
     * 根据订单号获取订单详情
     *
     * @param orderId
     * @return
     */
    @RequestMapping("/selectDuibaPointsModifyByOrdid/{orderId}")
    public DuibaPointsModifyResponse selectDuibaPointsModifyByOrdid(@PathVariable String orderId) {
        DuibaPointsModifyResponse response = new DuibaPointsModifyResponse();
        try {
            DuibaPointsModify duibaPointsModify = duibaPointsModifyService.selectDuibaPointsModifyByOrdid(orderId);
            if(null != duibaPointsModify) {
                DuibaPointsModifyVO vo = CommonUtils.convertBean(duibaPointsModify, DuibaPointsModifyVO.class);
                response.setResult(vo);
                response.setRtn(Response.SUCCESS);
                return response;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.setRtn(Response.FAIL);
        return response;
    }
}
