package com.hyjf.am.response.admin;

import java.util.List;

import com.hyjf.am.response.AdminResponse;
import com.hyjf.am.vo.admin.TemplateDisposeVO;
import com.hyjf.am.vo.user.TemplateConfigVO;
import com.hyjf.am.vo.user.UtmPlatVO;

public class TemplateDisposeResponse<T> extends AdminResponse<TemplateDisposeVO>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private List<UtmPlatVO> userRoles;
	private List<UtmPlatVO> userRoles2;
	private List<TemplateConfigVO>	templateConfigList;
	private List<T> listUserRoles;
	private TemplateConfigVO templateConfig;
    /**
     * 状态，1启用，0禁用
     *
     * @mbggenerated
     */
    private Integer flag;
	
    
    
	public List<UtmPlatVO> getUserRoles2() {
		return userRoles2;
	}

	public void setUserRoles2(List<UtmPlatVO> userRoles2) {
		this.userRoles2 = userRoles2;
	}

	public Integer getFlag() {
		return flag;
	}

	public void setFlag(Integer flag) {
		this.flag = flag;
	}

	public TemplateConfigVO getTemplateConfig() {
		return templateConfig;
	}

	public void setTemplateConfig(TemplateConfigVO templateConfig) {
		this.templateConfig = templateConfig;
	}

	public List<T> getListUserRoles() {
		return listUserRoles;
	}

	public void setListUserRoles(List<T> listUserRoles) {
		this.listUserRoles = listUserRoles;
	}

	public List<UtmPlatVO> getUserRoles() {
		return userRoles;
	}

	public void setUserRoles(List<UtmPlatVO> userRoles) {
		this.userRoles = userRoles;
	}

	public List<TemplateConfigVO> getTemplateConfigList() {
		return templateConfigList;
	}

	public void setTemplateConfigList(List<TemplateConfigVO> templateConfigList) {
		this.templateConfigList = templateConfigList;
	}

	
}
