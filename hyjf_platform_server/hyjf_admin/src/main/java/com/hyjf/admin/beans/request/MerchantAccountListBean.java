/**
 * Description:商户子账户记录列表前端查询所用vo
 * Copyright: Copyright (HYJF Corporation) 2015
 * Company: HYJF Corporation
 * @author: 王坤
 * @version: 1.0
 * Created at: 2015年11月11日 下午2:17:31
 * Modification History:
 * Modified by : 
 */

package com.hyjf.admin.beans.request;

import com.hyjf.am.vo.BasePage;
import com.hyjf.am.vo.admin.MerchantAccountVO;

import java.io.Serializable;
import java.util.List;

/**
 * @author
 */

public class MerchantAccountListBean extends BasePage implements Serializable {

	/** serialVersionUID */
	private static final long serialVersionUID = 7768418442884796575L;
	
	private String accountBalanceSum;
	
	private String availableBalanceSum;
	
	private String frostSum;
	
	private List<MerchantAccountVO> recordList;

	/**
	 * 翻页机能用的隐藏变量
	 */
	private int paginatorPage = 1;


	public int getPaginatorPage() {
		if (paginatorPage == 0) {
			paginatorPage = 1;
		}
		return paginatorPage;
	}

	public void setPaginatorPage(int paginatorPage) {
		this.paginatorPage = paginatorPage;
	}


	public List<MerchantAccountVO> getRecordList() {
		return recordList;
	}

	public void setRecordList(List<MerchantAccountVO> recordList) {
		this.recordList = recordList;
	}

	public String getAccountBalanceSum() {
		return accountBalanceSum;
	}

	public void setAccountBalanceSum(String accountBalanceSum) {
		this.accountBalanceSum = accountBalanceSum;
	}

	public String getAvailableBalanceSum() {
		return availableBalanceSum;
	}

	public void setAvailableBalanceSum(String availableBalanceSum) {
		this.availableBalanceSum = availableBalanceSum;
	}

	public String getFrostSum() {
		return frostSum;
	}

	public void setFrostSum(String frostSum) {
		this.frostSum = frostSum;
	}

}
