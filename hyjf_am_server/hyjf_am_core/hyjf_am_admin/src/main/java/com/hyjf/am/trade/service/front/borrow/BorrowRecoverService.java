/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.front.borrow;

import com.hyjf.am.resquest.trade.BorrowRegistRequest;
import com.hyjf.am.resquest.trade.TenderRequest;
import com.hyjf.am.resquest.user.BorrowFinmanNewChargeRequest;
import com.hyjf.am.trade.dao.model.auto.*;
import com.hyjf.am.vo.trade.ProjectCompanyDetailVO;
import com.hyjf.am.vo.trade.ProjectCustomeDetailVO;
import com.hyjf.am.vo.trade.WebProjectPersonDetailVO;
import com.hyjf.am.vo.trade.borrow.BorrowRecoverVO;
import com.hyjf.am.vo.trade.borrow.TenderBgVO;
import com.hyjf.am.vo.trade.borrow.TenderRetMsg;

import java.util.List;

/**
 * @author jijun
 * @date 20180630
 */
public interface BorrowRecoverService{


    BorrowRecover selectBorrowRecoverByTenderNid(String tenderAgreementID);

    BorrowRecover selectBorrowRecoverByNid(String nid);

    BorrowRecover selectBorrowRecoverById(Integer id);

    List<BorrowRecover> selectByBorrowNid(String borrowNid);

    int updateBorrowRecover(BorrowRecover borrowRecover);

    /**
     * 根据tenderNid 和bidNid 查询
     * @param tenderNid
     * @param bidNid
     * @return
     */
    BorrowRecover getBorrowRecoverByTenderNidBidNid(String tenderNid, String bidNid);

    /**
     * 根据tenderNid查询
     * @param tenderNid
     * @return
     */
    BorrowRecover getBorrowRecoverByTenderNid(String tenderNid);

    /**
     * 获取borrow_recover_plan更新每次还款时间
     * @param bidNid
     * @param creditTenderNid
     * @param periodNow
     * @return
     */
    BorrowRecoverPlan getPlanByBidTidPeriod(String bidNid, String creditTenderNid, Integer periodNow);
}