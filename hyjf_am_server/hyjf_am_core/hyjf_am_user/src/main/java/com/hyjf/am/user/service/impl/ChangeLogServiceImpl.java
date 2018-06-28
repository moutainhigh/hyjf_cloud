package com.hyjf.am.user.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyjf.am.user.dao.mapper.auto.UserChangeLogMapper;
import com.hyjf.am.user.dao.mapper.customize.ChangeLogCustomizeMapper;
import com.hyjf.am.user.dao.model.auto.UserChangeLog;
import com.hyjf.am.user.dao.model.auto.UserChangeLogExample;
import com.hyjf.am.user.dao.model.customize.ChangeLogCustomize;
import com.hyjf.am.user.service.ChangeLogService;

@Service
public class ChangeLogServiceImpl extends BaseServiceImpl implements ChangeLogService {
	@Autowired
	private UserChangeLogMapper usersChangeLogMapper;
	@Autowired
	private ChangeLogCustomizeMapper changeLogCustomizeMapper;

    /**
     * 获取用户信息修改列表
     * 
     * @return
     */
    public List<UserChangeLog> getRecordList(ChangeLogCustomize userChangeLog, int limitStart, int limitEnd) {
        UserChangeLogExample example = new UserChangeLogExample();
        example.setOrderByClause(" change_time desc ");
        if (limitStart != -1) {
            example.setLimitStart(limitStart);
            example.setLimitEnd(limitEnd);
        }
        UserChangeLogExample.Criteria criteria = example.createCriteria();
        // 条件查询
        if (StringUtils.isNotEmpty(userChangeLog.getUsername())) {
            criteria.andUsernameEqualTo(userChangeLog.getUsername());
        }
        if (StringUtils.isNotEmpty(userChangeLog.getRealName())) {
            criteria.andRealNameEqualTo(userChangeLog.getRealName());
        }
        if (StringUtils.isNotEmpty(userChangeLog.getMobile())) {
            criteria.andMobileEqualTo(userChangeLog.getMobile());
        }
        if (StringUtils.isNotEmpty(userChangeLog.getRecommendUser())) {
            criteria.andRecommendUserEqualTo(userChangeLog.getRecommendUser());
        }
        if (userChangeLog.getAttribute() != null) {
            criteria.andAttributeEqualTo(Integer.parseInt(userChangeLog.getAttribute()));
        }
        if (userChangeLog.getIs51() != null) {
            criteria.andIs51EqualTo(userChangeLog.getIs51());
        }
        return usersChangeLogMapper.selectByExample(example);
    }

    /**
     * 获取用户信息修改列表
     * @param userChangeLog
     * @return
     */
    public List<ChangeLogCustomize> getChangeLogList(ChangeLogCustomize userChangeLog) {
        List<ChangeLogCustomize> changeLogs = changeLogCustomizeMapper.queryChangeLogList(userChangeLog);
        return changeLogs;
    }
    
    /**
     * 
     * 获取某一用户的信息修改列表
     * @param userChangeLog
     * @param limitStart
     * @param limitEnd
     * @return
     */
    public List<ChangeLogCustomize> getUserRecordList(ChangeLogCustomize userChangeLog, int limitStart, int limitEnd) {
        if (limitStart != -1) {
            userChangeLog.setLimitStart(limitStart);
            userChangeLog.setLimitEnd(limitEnd);
           
        }
        return changeLogCustomizeMapper.queryChangeLogList(userChangeLog);
    }
    
    /**
     * 获取用户信息修改记录数
     * 
     * @param form
     * @return
     */

    @Override
    public int countRecordTotal(ChangeLogCustomize userChangeLog) {
        return changeLogCustomizeMapper.queryChangeLogCount(userChangeLog);
    }

}
