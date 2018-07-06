package com.hyjf.am.trade.service.impl.admin.manage;

import com.hyjf.am.trade.dao.model.customize.admin.AdminHjhDebtCreditCustomize;
import com.hyjf.am.trade.service.admin.manage.AdminHjhDebtCreditService;
import com.hyjf.am.trade.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Auther:yangchangwei
 * @Date:2018/7/4
 * @Description: 后台列表 汇计划-转让列表
 */
@Service
public class AdminHjhDebtCreditServiceImpl extends BaseServiceImpl implements AdminHjhDebtCreditService {

    /**
     * 获取查询列表总数
     * @param paramMap
     * @return
     */
    @Override
    public Integer getListTotal(Map paramMap) {

        int total = this.adminHjhDebtCreditCustomizeMapper.countDebtCredit(paramMap);

        return total;
    }

    /**
     * 获取汇转让列表
     * @param map
     * @return
     */
    @Override
    public List<AdminHjhDebtCreditCustomize> getList(Map map,int limitStart,int limitEnd) {


        return null;
    }

}
