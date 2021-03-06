/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.admin.service.impl;

import com.hyjf.admin.beans.request.PlatformCountRequestBean;
import com.hyjf.admin.client.AmAdminClient;
import com.hyjf.admin.service.PlatformCountService;
import com.hyjf.am.vo.admin.PlatformCountCustomizeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author fuqiang
 * @version PlatformCountServiceImpl, v0.1 2018/7/18 17:59
 */
@Service
public class PlatformCountServiceImpl implements PlatformCountService {
    @Autowired
    private AmAdminClient platformCountClient;

    @Override
    public List<PlatformCountCustomizeVO>  searchAction(PlatformCountRequestBean requestBean) {
        List<PlatformCountCustomizeVO> resultList1 = platformCountClient.searchAction(requestBean).getResultList();
        return resultList1;
    }
}
