package com.hyjf.am.user.dao.mapper.auto;


import com.hyjf.am.user.dao.model.customize.EmployeeCustomize;
import org.apache.ibatis.annotations.Param;

public interface EmployeeCustomizeMapper {

	/**
	 * 获取员工信息
	 * 
	 * @param  
	 * @return
	 */
	EmployeeCustomize selectEmployeeByUserId(@Param("userId") Integer userId);
	
	/**
	 * 根据平台userid查询其在crm的属性
	 * @param userId
	 * @return
	 */
	public Integer queryCuttype(@Param("userId") Integer userId);

}