package com.hyjf.cs.user.result;

import com.hyjf.am.vo.user.UsersContactVO;
import com.hyjf.cs.common.bean.result.WebResult;

import java.util.HashMap;
import java.util.Map;

/**
 * 紧急联系人设置返回bean
 * @author hesy
 *
 */
public class ContractSetResultBean extends WebResult<UsersContactVO>{
	private String checkRelationName = "";
	private String checkRelationId = "";
	
	Map<String, String> relationMap = new HashMap<String, String>();
	
	public String getCheckRelationName() {
		return checkRelationName;
	}
	public void setCheckRelationName(String checkRelationName) {
		this.checkRelationName = checkRelationName;
	}
	public String getCheckRelationId() {
		return checkRelationId;
	}
	public void setCheckRelationId(String checkRelationId) {
		this.checkRelationId = checkRelationId;
	}
	public Map<String, String> getRelationMap() {
		return relationMap;
	}
	public void setRelationMap(Map<String, String> relationMap) {
		this.relationMap = relationMap;
	}
	
	
	
}
