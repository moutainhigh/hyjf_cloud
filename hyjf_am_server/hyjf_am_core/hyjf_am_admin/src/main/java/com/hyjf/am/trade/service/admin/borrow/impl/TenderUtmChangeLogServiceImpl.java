/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.trade.service.admin.borrow.impl;

import java.util.List;

import javax.annotation.Nullable;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.hyjf.am.trade.dao.mapper.customize.TenderUtmChangeLogCustomizeMapper;
import com.hyjf.am.trade.dao.model.customize.TenderUtmChangeLogCustomize;
import com.hyjf.am.trade.service.admin.borrow.TenderUtmChangeLogService;
import com.hyjf.am.vo.trade.borrow.TenderUpdateUtmHistoryVO;

/**
 * @author cui
 * @version TenderUtmChangeLogServiceImpl, v0.1 2019/6/18 10:45
 */
@Service
public class TenderUtmChangeLogServiceImpl implements TenderUtmChangeLogService {

    @Autowired
    private TenderUtmChangeLogCustomizeMapper tenderUtmChangeLogCustomizeMapper;

    @Override
    public List<TenderUpdateUtmHistoryVO> getChangeLog(String nid) {

        List<TenderUtmChangeLogCustomize> lstCustomize=tenderUtmChangeLogCustomizeMapper.getUtmChangeLog(nid);

        List<TenderUpdateUtmHistoryVO> lstResult= Lists.transform(lstCustomize, new Function<TenderUtmChangeLogCustomize, TenderUpdateUtmHistoryVO>() {
            @Nullable
            @Override
            public TenderUpdateUtmHistoryVO apply(@Nullable TenderUtmChangeLogCustomize tenderUtmChangeLogCustomize) {
                TenderUpdateUtmHistoryVO vo=new TenderUpdateUtmHistoryVO();
                BeanUtils.copyProperties(tenderUtmChangeLogCustomize,vo);
                return vo;
            }
        });

        return lstResult;

    }
}
