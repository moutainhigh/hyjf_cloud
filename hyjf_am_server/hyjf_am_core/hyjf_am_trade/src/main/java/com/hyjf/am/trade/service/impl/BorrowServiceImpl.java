/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyjf.am.resquest.trade.BorrowRegistRequest;
import com.hyjf.am.resquest.user.BorrowFinmanNewChargeRequest;
import com.hyjf.am.trade.dao.mapper.auto.BorrowConfigMapper;
import com.hyjf.am.trade.dao.mapper.auto.BorrowFinmanNewChargeMapper;
import com.hyjf.am.trade.dao.mapper.auto.BorrowInfoMapper;
import com.hyjf.am.trade.dao.mapper.auto.BorrowManinfoMapper;
import com.hyjf.am.trade.dao.mapper.auto.BorrowMapper;
import com.hyjf.am.trade.dao.mapper.auto.BorrowStyleMapper;
import com.hyjf.am.trade.dao.model.auto.Borrow;
import com.hyjf.am.trade.dao.model.auto.BorrowConfig;
import com.hyjf.am.trade.dao.model.auto.BorrowExample;
import com.hyjf.am.trade.dao.model.auto.BorrowFinmanNewCharge;
import com.hyjf.am.trade.dao.model.auto.BorrowFinmanNewChargeExample;
import com.hyjf.am.trade.dao.model.auto.BorrowInfo;
import com.hyjf.am.trade.dao.model.auto.BorrowInfoExample;
import com.hyjf.am.trade.dao.model.auto.BorrowManinfo;
import com.hyjf.am.trade.dao.model.auto.BorrowStyle;
import com.hyjf.am.trade.dao.model.auto.BorrowStyleExample;
import com.hyjf.am.trade.service.BorrowService;
import com.hyjf.am.vo.trade.borrow.BorrowVO;
import com.hyjf.common.util.GetDate;

/**
 * @author fuqiang
 * @version BorrowServiceImpl, v0.1 2018/6/13 18:53
 */
@Service
public class BorrowServiceImpl implements BorrowService {

    @Autowired
    private BorrowFinmanNewChargeMapper borrowFinmanNewChargeMapper;

    @Autowired
    private BorrowConfigMapper borrowConfigMapper;

    @Autowired
    private BorrowMapper borrowMapper;

    @Autowired
    private BorrowManinfoMapper borrowManinfoMapper;
    @Autowired
    private BorrowStyleMapper borrowStyleMapper;

    @Autowired
    private BorrowInfoMapper borrowInfoMapper;

    @Override
    public BorrowFinmanNewCharge selectBorrowApr(BorrowFinmanNewChargeRequest request) {
        BorrowFinmanNewChargeExample example = new BorrowFinmanNewChargeExample();
        BorrowFinmanNewChargeExample.Criteria cra = example.createCriteria();
        cra.andProjectTypeEqualTo(request.getBorrowClass());
        cra.andInstCodeEqualTo(request.getInstCode());
        cra.andAssetTypeEqualTo(request.getAssetType());
        cra.andManChargeTimeTypeEqualTo(request.getQueryBorrowStyle());
        cra.andManChargeTimeEqualTo(request.getBorrowPeriod());
        cra.andStatusEqualTo(0);

        List<BorrowFinmanNewCharge> list = this.borrowFinmanNewChargeMapper.selectByExample(example);

        if (!CollectionUtils.isEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Borrow getBorrow(String borrowNid) {
        BorrowExample example = new BorrowExample();
        BorrowExample.Criteria criteria = example.createCriteria();
        criteria.andBorrowNidEqualTo(borrowNid);
        List<Borrow> list = this.borrowMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public BorrowStyle getborrowStyleByNid(String borrowStyle) {
        BorrowStyleExample example = new BorrowStyleExample();
        BorrowStyleExample.Criteria cri = example.createCriteria();
        cri.andNidEqualTo(borrowStyle);
        List<BorrowStyle> style = borrowStyleMapper.selectByExample(example);
        return style.get(0);
    }

    public BorrowConfig getBorrowConfigByConfigCd(String configCd) {
        BorrowConfig borrowConfig = this.borrowConfigMapper.selectByPrimaryKey(configCd);
        return borrowConfig;
    }

    @Override
    public int insertBorrow(Borrow borrow) {
        return borrowMapper.insertSelective(borrow);
    }

    @Override
    public int insertBorrowManinfo(BorrowManinfo borrowManinfo) {
         return borrowManinfoMapper.insertSelective(borrowManinfo);
    }

    @Override
    public int updateBorrowRegist(BorrowRegistRequest request) {
        BorrowVO borrowVO = request.getBorrowVO();
        int status = request.getStatus();
        int registStatus = request.getRegistStatus();
        Date nowDate = new Date();
//		AdminSystem adminSystem = (AdminSystem) SessionUtils.getSession(CustomConstants.LOGIN_USER_INFO);
        BorrowExample example = new BorrowExample();
        example.createCriteria().andIdEqualTo(borrowVO.getId()).andStatusEqualTo(borrowVO.getStatus()).andRegistStatusEqualTo(borrowVO.getRegistStatus());
        borrowVO.setRegistStatus(registStatus);
        borrowVO.setStatus(status);
        borrowVO.setRegistUserId(1);//TODO:id写死1
        borrowVO.setRegistUserName("AutoRecord");
        borrowVO.setRegistTime(nowDate);
        Borrow borrow = new Borrow();
        BeanUtils.copyProperties(borrowVO, borrow);
        return borrowMapper.updateByExampleSelective(borrow, example);
    }

    /**
     * 检索正在还款中的标的
     * @return
     */
    @Override
    public List<Borrow> selectBorrowList() {
        BorrowExample example = new BorrowExample();
        BorrowExample.Criteria cra = example.createCriteria();
        cra.andStatusEqualTo(3);
        cra.andRepayFullStatusEqualTo(0);
        List<Borrow> list = this.borrowMapper.selectByExample(example);
        if (list != null && list.size() > 0) {
            return list;
        }
        return null;
    }

    /**
     *获取borrowInfo
     * @param borrowNid
     * @return
     */
    @Override
    public BorrowInfo getBorrowInfoByNid(String borrowNid) {
        BorrowInfoExample example = new BorrowInfoExample();
        BorrowInfoExample.Criteria cra = example.createCriteria();
        cra.andBorrowNidEqualTo(borrowNid);
        List<BorrowInfo> list=this.borrowInfoMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    /**
     * 检索逾期的还款标的
     */
	@Override
	public List<Borrow> selectOverdueBorrowList() {
		BorrowExample example = new BorrowExample();
    	example.createCriteria().andRepayLastTimeLessThanOrEqualTo(GetDate.getDayEnd10(GetDate.getTodayBeforeOrAfter(-1))).andStatusEqualTo(4).andPlanNidIsNull();
    	List<Borrow> borrows = borrowMapper.selectByExample(example);
    	if(CollectionUtils.isNotEmpty(borrows)){
    		return borrows;
    	}
    	return null;
	}

}
