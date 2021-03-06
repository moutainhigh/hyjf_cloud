package com.hyjf.am.resquest.trade;

import com.hyjf.am.resquest.Request;

/**
 * @author jijun
 * @since 20180622
 */
public class BorrowCreditRequest extends Request {

    private String creditNid;
    private int sellerUserId;
    private String tenderOrderId;

    private String tenderNid;

    private String bidNid;

    public String getBidNid() {
        return bidNid;
    }

    public void setBidNid(String bidNid) {
        this.bidNid = bidNid;
    }

    public String getTenderNid() {
        return tenderNid;
    }

    public void setTenderNid(String tenderNid) {
        this.tenderNid = tenderNid;
    }

    public String getCreditNid() {
        return creditNid;
    }

    public void setCreditNid(String creditNid) {
        this.creditNid = creditNid;
    }

    public int getSellerUserId() {
        return sellerUserId;
    }

    public void setSellerUserId(int sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    public String getTenderOrderId() {
        return tenderOrderId;
    }

    public void setTenderOrderId(String tenderOrderId) {
        this.tenderOrderId = tenderOrderId;
    }

}
