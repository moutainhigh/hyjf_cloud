package com.hyjf.am.trade.service.front.account.impl;

import com.hyjf.am.resquest.trade.TenderCancelRequest;
import com.hyjf.am.trade.dao.mapper.auto.BorrowTenderTmpMapper;
import com.hyjf.am.trade.dao.mapper.auto.FreezeHistoryMapper;
import com.hyjf.am.trade.dao.model.auto.BorrowTenderTmp;
import com.hyjf.am.trade.dao.model.auto.BorrowTenderTmpExample;
import com.hyjf.am.trade.dao.model.auto.FreezeHistory;
import com.hyjf.am.trade.service.front.account.BankTenderCancelService;
import com.hyjf.am.vo.trade.borrow.BorrowTenderTmpVO;
import com.hyjf.common.util.GetDate;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 出借撤销异常
 * @author jijun
 * @date 20180625
 */
@Service
public class BankTenderCancelServiceImpl implements BankTenderCancelService {

    private static final Logger logger = LoggerFactory.getLogger(BankTenderCancelServiceImpl.class);

    @Autowired
    private BorrowTenderTmpMapper borrowTenderTmpMapper;
    @Autowired
    private FreezeHistoryMapper freezeHistoryMapper;

    @Override
    public List<BorrowTenderTmp> getBorrowTenderTmpsForTenderCancel() {
        //获取当天的开始时间
        Date dayStart10 = GetDate.getDayStartOfSomeDay(new Date());//当天开始时间
        BorrowTenderTmpExample example = new BorrowTenderTmpExample();
        example.createCriteria().andIsBankTenderEqualTo(1).andCreateTimeLessThan(dayStart10).andStatusNotEqualTo(3);//除上次处理异常数据
        example.setLimitStart(0);
        example.setLimitEnd(99);
        List<BorrowTenderTmp> tmpList = this.borrowTenderTmpMapper.selectByExample(example);
        return tmpList;
    }


    @Override
    public void updateBidCancelRecord(TenderCancelRequest request) {
        BorrowTenderTmpVO tenderTmp= request.getBorrowTenderTmpVO();
        String username= request.getUserName();

        if (tenderTmp==null || StringUtils.isBlank(username)){
            logger.info("传入参数为空,撤销出借操作失败!");
            throw new RuntimeException("传入参数为空,撤销出借操作失败!");
        }

        Integer userId = tenderTmp.getUserId();
        boolean tenderTmpFlag = this.borrowTenderTmpMapper.deleteByPrimaryKey(tenderTmp.getId()) > 0 ? true : false;
        if (!tenderTmpFlag) {
            logger.info("删除出借日志表失败，出借订单号：" + tenderTmp.getNid());
            throw new RuntimeException("删除出借日志表失败，出借订单号：" + tenderTmp.getNid());
        }
        FreezeHistory freezeHistory = new FreezeHistory();
        freezeHistory.setTrxId(tenderTmp.getNid());
        freezeHistory.setNotes("自动任务银行出借撤销");
        freezeHistory.setFreezeUser(username);
        freezeHistory.setFreezeTime(GetDate.getNowTime10());
        boolean freezeHisLog = this.freezeHistoryMapper.insert(freezeHistory) > 0 ? true : false;
        if (!freezeHisLog) {
            logger.info("插入出借删除日志表失败，出借订单号：" + tenderTmp.getNid());
            throw new RuntimeException("插入出借删除日志表失败，出借订单号：" + tenderTmp.getNid());
        }
    }


    @Override
    public boolean updateTenderCancelExceptionData(BorrowTenderTmp info) {
        BorrowTenderTmp record;
        BorrowTenderTmpExample example = new BorrowTenderTmpExample();
        example.createCriteria().andNidEqualTo(info.getNid());
        List<BorrowTenderTmp> tempInfo = borrowTenderTmpMapper.selectByExample(example);
        record = tempInfo.get(0);
        record.setStatus(3);
        int result=borrowTenderTmpMapper.updateByPrimaryKey(record);
        if (result>0){
            return true;
        }else{
            return false;
        }

    }
}
