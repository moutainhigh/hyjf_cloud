package com.hyjf.am.trade.service.impl.admin;

import com.hyjf.am.resquest.admin.HjhReInvestDetailRequest;
import com.hyjf.am.trade.service.admin.HjhPlanCapitalService;
import com.hyjf.am.trade.service.impl.BaseServiceImpl;
import com.hyjf.am.vo.trade.hjh.HjhPlanCapitalVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 汇计划-资金计划
 * @Author : huanghui
 */
@Service
public class HjhPlanCapitalServiceImpl extends BaseServiceImpl implements HjhPlanCapitalService {

    @Override
    public List<HjhPlanCapitalVO> getReinvestInfo(HjhReInvestDetailRequest request) {

        Map<String, Object> param = new HashMap<String, Object>();

        if (StringUtils.isNotEmpty(request.getPlanNidSrch())){
            param.put("planNid", request.getPlanNidSrch());
        }

        List<HjhPlanCapitalVO> recordList = hjhReInvestDetailCustomizeMapper.queryReInvestDetails(param);
        return recordList;
    }
}