package com.hyjf.am.vo.trade.borrow;

import java.io.Serializable;

public class BorrowStyleWithBLOBs extends BorrowStyleVo implements Serializable {
    private String contents;

    private String remark;

    private static final long serialVersionUID = 1L;

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents == null ? null : contents.trim();
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }
}