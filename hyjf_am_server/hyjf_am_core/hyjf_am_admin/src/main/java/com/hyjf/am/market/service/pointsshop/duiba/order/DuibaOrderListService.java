/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.market.service.pointsshop.duiba.order;

import com.hyjf.am.resquest.admin.DuibaOrderRequest;
import com.hyjf.am.trade.service.BaseService;
import com.hyjf.am.vo.admin.DuibaOrderVO;

import java.util.List;

/**
 * @author wenxin
 * @version DuibaOrderListService, v0.1 2019/5/29 9:48
 */
public interface DuibaOrderListService extends BaseService {

    Integer selectOrderListCount(DuibaOrderRequest request);

    List<DuibaOrderVO> selectOrderList(DuibaOrderRequest request);

    DuibaOrderVO findOneOrder(Integer orderId);

    Integer updateOneOrderByPrimaryKey(DuibaOrderVO duibaOrderVO);

    DuibaOrderVO selectOrderByOrderId(String duibaOrderId);
}
