package com.hyjf.am.response.admin;

import com.hyjf.am.response.Response;
import com.hyjf.am.vo.admin.TenderCommissionVO;

public class TenderCommissionResponse extends Response<TenderCommissionVO> {

    private int count;

    private int flag;
    
    private int type;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}