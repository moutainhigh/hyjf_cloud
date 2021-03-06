/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.vo.trade.hjh;

import com.hyjf.am.vo.BaseVO;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author PC-LIUSHOUYI
 * @version BorrowBailVO, v0.1 2018/8/30 17:22
 */
public class BorrowBailVO extends BaseVO implements Serializable {

    private Integer id;

    private String borrowNid;

    private Integer borrowUid;

    private Integer operaterUid;

    private BigDecimal bailNum;

    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBorrowNid() {
        return borrowNid;
    }

    public void setBorrowNid(String borrowNid) {
        this.borrowNid = borrowNid;
    }

    public Integer getBorrowUid() {
        return borrowUid;
    }

    public void setBorrowUid(Integer borrowUid) {
        this.borrowUid = borrowUid;
    }

    public Integer getOperaterUid() {
        return operaterUid;
    }

    public void setOperaterUid(Integer operaterUid) {
        this.operaterUid = operaterUid;
    }

    public BigDecimal getBailNum() {
        return bailNum;
    }

    public void setBailNum(BigDecimal bailNum) {
        this.bailNum = bailNum;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
