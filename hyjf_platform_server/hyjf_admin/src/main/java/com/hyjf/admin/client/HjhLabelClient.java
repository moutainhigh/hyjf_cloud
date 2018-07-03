/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.client;

import java.util.List;

import com.hyjf.am.resquest.admin.HjhLabelInfoRequest;
import com.hyjf.am.resquest.admin.HjhLabelRequest;
import com.hyjf.am.vo.admin.HjhLabelCustomizeVO;
import com.hyjf.am.vo.trade.borrow.BorrowProjectTypeVO;
import com.hyjf.am.vo.trade.borrow.BorrowStyleVO;

/**
 * @author libin
 * @version HjhLabelClient.java, v0.1 2018年6月30日 上午9:46:12
 */
public interface HjhLabelClient {
	
    /**
     * 项目类型
     * @param 
     * @return
     */
    List<BorrowProjectTypeVO> findBorrowProjectTypeList();
    /**
     * 还款方式
     * @param 
     * @return
     */
    List<BorrowStyleVO> findBorrowStyleList();
    
    /**
     * 查询标签配置列表
     * @param request
     * @return
     */
    List<HjhLabelCustomizeVO> findHjhLabelList(HjhLabelRequest request);
    
    /**
     * 查询标签配置列表
     * @param request
     * @return
     */
    List<HjhLabelCustomizeVO> findHjhLabelListById(HjhLabelRequest request);
    
    /**
     * 查询标签配置列表
     * @param request
     * @return
     */
    List<HjhLabelCustomizeVO> findHjhLabelListLabelName(HjhLabelRequest request);
    
	/**
     * 插入标签配置列表
     * @param request
     */
    void insertHjhLabelRecord(HjhLabelInfoRequest request);
    
	/**
     * 更新标签配置列表
     * @param request
     */
    int updateHjhLabelRecord(HjhLabelInfoRequest request);
    
	/**
     * 更新引擎表
     * @param request
     */
    int updateAllocationRecord(HjhLabelInfoRequest request);
    
    
}