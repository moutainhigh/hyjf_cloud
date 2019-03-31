package com.hyjf.am.trade.controller.admin.borrow;

import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.BorrowRepayInfoCurrentResponse;
import com.hyjf.am.resquest.admin.BorrowRepayInfoCurrentRequest;
import com.hyjf.am.trade.controller.BaseController;
import com.hyjf.am.trade.service.admin.borrow.BorrowRepayInfoCurrentService;
import com.hyjf.am.vo.admin.BorrowRepayInfoCurrentCustomizeVO;
import com.hyjf.common.paginator.Paginator;
import com.hyjf.common.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 当前债权还款明细
 * @author hesy
 */
@RestController
@RequestMapping("/am-admin/repayinfo_current")
public class BorrowRepayInfoCurrentController extends BaseController {
    @Autowired
    BorrowRepayInfoCurrentService borrowRepayInfoCurrentService;

    /**
     * 当前债权还款明细页面数据获取
     * @param requestBean
     * @return
     */
    @RequestMapping(value = "/getData",method = RequestMethod.POST)
    public BorrowRepayInfoCurrentResponse getData(@RequestBody @Valid BorrowRepayInfoCurrentRequest requestBean){
        logger.info("当前债权还款明细-start, requestBean:{}", requestBean);
        BorrowRepayInfoCurrentResponse response = new BorrowRepayInfoCurrentResponse();

        //请求参数校验
        if(requestBean.getCurrPage() <= 0){
            requestBean.setCurrPage(1);
        }
        if(requestBean.getPageSize() <= 0){
            requestBean.setPageSize(10);
        }
        //borrowNid为必须传的参数
        if(StringUtils.isBlank(requestBean.getBorrowNid())){
            response.setRtn(Response.FAIL);
            response.setMessage("请求参数错误，borrowNid为空");
            return response;
        }

        // 查询列表总记录数
        Integer count = borrowRepayInfoCurrentService.getRepayInfoCurrentCount(requestBean);
        Paginator paginator = new Paginator(requestBean.getCurrPage(), count,requestBean.getPageSize());

        // 查询列表数据
        List<BorrowRepayInfoCurrentCustomizeVO> resultList;
        if(count > 0){
            resultList = borrowRepayInfoCurrentService.getRepayInfoCurrentList(requestBean, paginator.getOffset(), paginator.getLimit());
        }else{
            resultList = new ArrayList<>();
        }

        // 查询列表统计数据
        Map<String,Object> sumInfo = borrowRepayInfoCurrentService.getRepayInfoCurrentSum(requestBean);

        response.setResultList(resultList);
        response.setCount(count);
        response.setSumInfo(sumInfo);
        response.setRtn(Response.SUCCESS);

        logger.info("当前债权还款明细-end, respinse:{}" + response);
        return response;
    }

}
