package com.hyjf.am.user.service.front.user.impl;

import com.hyjf.am.user.service.front.user.MyInviteService;
import com.hyjf.am.user.service.impl.BaseServiceImpl;
import com.hyjf.am.vo.user.MyInviteListCustomizeVO;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的邀请记录
 * @author hesy
 * @version MyInviteServiceImpl, v0.1 2018/6/22 20:08
 */
@Service
public class MyInviteServiceImpl extends BaseServiceImpl implements MyInviteService {

    /**
     * 邀请记录列表
     */
    @Override
    public List<MyInviteListCustomizeVO> selectMyInviteList(String userId, Integer limitStart, Integer limitEnd){
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("userId", userId);
        param.put("limitStart", limitStart);
        param.put("limitEnd", limitEnd);

        return myInviteCustomizeMapper.selectMyInviteList(param);
    }

    /**
     * 统计总的邀请记录数
     */
    @Override
    public Integer countMyInviteList(String userId){
        Map<String,Object> param = new HashMap<String, Object>();
        param.put("userId", userId);

        Integer result = myInviteCustomizeMapper.countMyInviteList(param);
        if(result == null){
            result = 0;
        }
        return result;
    }

}
