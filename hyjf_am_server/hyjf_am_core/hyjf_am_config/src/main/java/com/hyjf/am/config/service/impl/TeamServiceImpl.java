/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.config.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hyjf.am.config.dao.mapper.auto.TeamMapper;
import com.hyjf.am.config.dao.model.auto.Team;
import com.hyjf.am.config.dao.model.auto.TeamExample;
import com.hyjf.am.config.service.TeamService;

/**
 * @author fuqiang
 * @version TeamServiceImpl, v0.1 2018/7/9 16:46
 */
@Service
public class TeamServiceImpl implements TeamService {

	@Autowired
	private TeamMapper teamMapper;

	@Override
	public Team getFounder() {
		TeamExample example = new TeamExample();
		TeamExample.Criteria cra = example.createCriteria();
		cra.andStatusEqualTo(1);// 开启状态
		example.setOrderByClause("`order` asc");
		List<Team> teamList = teamMapper.selectByExample(example);
		return teamList.size() == 0 ? new Team() : teamList.get(0);
	}
}
