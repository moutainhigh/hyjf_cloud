package com.hyjf.admin.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.hyjf.admin.client.AmConfigClient;
import com.hyjf.admin.client.AmTradeClient;
import com.hyjf.admin.client.AmUserClient;
import com.hyjf.admin.service.BorrowCommonService;
import com.hyjf.am.response.Response;
import com.hyjf.am.response.admin.BorrowCommonResponse;
import com.hyjf.am.response.config.AdminSystemResponse;
import com.hyjf.am.response.config.LinkResponse;
import com.hyjf.am.response.user.UserInfoResponse;
import com.hyjf.am.response.user.UserResponse;
import com.hyjf.am.resquest.admin.BorrowCommonRequest;
import com.hyjf.am.vo.trade.borrow.BorrowCommonVO;
import com.hyjf.am.vo.user.UserInfoVO;
import com.hyjf.am.vo.user.UserVO;
import java.util.List;
/**
 * @author GOGTZ-Z
 * @version V1.0  
 * @package com.hyjf.admin.maintenance.AdminPermissions
 * @date 2015/07/09 17:00
 */
@Service
public class BorrowCommonServiceImpl implements BorrowCommonService{
    @Autowired
    private AmTradeClient amTradeClient;
    @Autowired
    private AmConfigClient amConfigClient;
    @Autowired
    private AmUserClient amUserClient;

	@Override
	public BorrowCommonResponse moveToInfoAction(BorrowCommonRequest borrowCommonRequest) {
		
		return amTradeClient.moveToInfoAction(borrowCommonRequest);
	}

	@Override
	public BorrowCommonResponse insertAction(BorrowCommonRequest borrowCommonRequest) throws Exception {
		return amTradeClient.insertAction(borrowCommonRequest);
	}

	@Override
	public LinkResponse getLinks() {
	
		return amConfigClient.getLinks();
	}

	@Override
	public int isExistsUser(String userId) {

		return amTradeClient.isExistsUser(userId);
	}

	@Override
	public AdminSystemResponse isExistsApplicant(String applicant) {
		return amConfigClient.isExistsApplicant(applicant);
	}

	@Override
	public String getBorrowPreNid() {
		return amTradeClient.getBorrowPreNid();
	}

	@Override
	public String getXJDBorrowPreNid() {

		return amTradeClient.getXJDBorrowPreNid();
	}

	@Override
	public boolean isExistsBorrowPreNidRecord(String borrowPreNid) {
		return amTradeClient.isExistsBorrowPreNidRecord(borrowPreNid);
	}

	@Override
	public String getBorrowServiceScale(String borrowPeriod, String borrowStyle, Integer projectType,
			String instCode) {
		BorrowCommonRequest borrowCommonRequest=new BorrowCommonRequest();
		borrowCommonRequest.setBorrowPeriod(borrowPeriod);
		borrowCommonRequest.setBorrowStyle(borrowStyle);
		borrowCommonRequest.setProjectType(projectType);
		borrowCommonRequest.setInstCode(instCode);
		return amTradeClient.getBorrowServiceScale(borrowCommonRequest);
	}

	@Override
	public BorrowCommonResponse getProductTypeAction(String instCode) {
		return amTradeClient.getProductTypeAction(instCode);
	}

	@Override
	public int isEntrustedExistsUser(String userName) {
		return amTradeClient.isEntrustedExistsUser(userName);
	}

	@Override
	public List<UserVO> selectUserByUsername(String repayOrgName) {
		return amUserClient.searchUserByUsername(repayOrgName);
	}

	@Override
	public UserInfoVO findUserInfoById(int userId) {
		return amUserClient.findUserInfoById(userId);
	}


	@Override
	public boolean isBorrowUserCACheck(String param, String name) {
	
		return Response.isSuccess(amUserClient.selectCertificateAuthorityByIdNoName(param, name));
	}

	@Override
	public boolean isCAIdNoCheck(String param, String name) {
		return Response.isSuccess(amUserClient.isCAIdNoCheck(param, name));
	}

}