/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.user.dao.mapper.customize;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.hyjf.am.user.dao.model.customize.PlatformUserCountCustomize;

/**
 * @author fq
 * @version PlatformUserCountCustomizeMapper, v0.1 2018/8/13 10:35
 */
public interface PlatformUserCountCustomizeMapper {
	List<PlatformUserCountCustomize> getUserInfo(@Param("startTime") String timeStartSrch,
			@Param("endTime") String timeEndSrch);
}
