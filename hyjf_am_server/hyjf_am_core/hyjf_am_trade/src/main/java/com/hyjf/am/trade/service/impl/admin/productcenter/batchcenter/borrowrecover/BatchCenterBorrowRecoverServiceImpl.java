package com.hyjf.am.trade.service.impl.admin.productcenter.batchcenter.borrowrecover;

import com.hyjf.am.resquest.admin.BatchBorrowRecoverRequest;
import com.hyjf.am.trade.service.admin.productcenter.batchcenter.borrowRecover.BatchCenterBorrowRecoverService;
import com.hyjf.am.trade.service.impl.BaseServiceImpl;
import com.hyjf.am.vo.admin.BatchBorrowRecoverVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther:yangchangwei
 * @Date:2018/7/7
 * @Description:
 */
@Service
public class BatchCenterBorrowRecoverServiceImpl extends BaseServiceImpl implements BatchCenterBorrowRecoverService{

    /**
     * 获取列表数量
     * @param request
     * @return
     */
    @Override
    public Integer getListTotal(BatchBorrowRecoverRequest request) {

        Integer count = this.batchCenterCustomizeMapper.countBorrowBatchCenterListTotal(request);
        return count;
    }

    /**
     * 获取列表
     * @param request
     * @param limitStart
     * @param limitEnd
     * @return
     */
    @Override
    public List<BatchBorrowRecoverVo> getList(BatchBorrowRecoverRequest request, int limitStart, int limitEnd) {

        request.setLimitStart(limitStart);
        request.setLimitEnd(limitEnd);
        List<BatchBorrowRecoverVo> recoverVoList = this.batchCenterCustomizeMapper.queryBatchCenterList(request);
        return recoverVoList;
    }
}