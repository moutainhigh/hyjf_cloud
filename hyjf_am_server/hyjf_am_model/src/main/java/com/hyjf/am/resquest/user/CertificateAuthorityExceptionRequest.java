package com.hyjf.am.resquest.user;


import java.io.Serializable;
import java.util.List;

import com.hyjf.am.vo.BasePage;

/**
 * CA认证异常Bean
 *
 * @author 
 * dongzeshan
 */
public class CertificateAuthorityExceptionRequest  extends BasePage implements Serializable {

    private static final long serialVersionUID = 661775352441250125L;
    // 用户名(检索用)
    private String userNameSrch;
    // 手机号(检索用)
    private String mobileSrch;
    // 姓名(检索用)
    private String trueNameSrch;
    // 状态(检索用)
    private String statusSrch;
    // 检索开始时间
    private String startTimeSrch;
    // 检索结束时间
    private String endTimeSrch;

    // 用户ID
    private String userId;

    /**
     * 翻页机能用的隐藏变量
     */
    private int paginatorPage = 1;



    /**
     * 检索条件 limitStart
     */
    private int limitStart = -1;

    /**
     * 检索条件 limitEnd
     */
    private int limitEnd = -1;


    public int getPaginatorPage() {
        if (paginatorPage == 0) {
            paginatorPage = 1;
        }
        return paginatorPage;
    }

    public void setPaginatorPage(int paginatorPage) {
        this.paginatorPage = paginatorPage;
    }



    public String getUserNameSrch() {
        return userNameSrch;
    }

    public void setUserNameSrch(String userNameSrch) {
        this.userNameSrch = userNameSrch;
    }

    public String getMobileSrch() {
        return mobileSrch;
    }

    public void setMobileSrch(String mobileSrch) {
        this.mobileSrch = mobileSrch;
    }

    public String getTrueNameSrch() {
        return trueNameSrch;
    }

    public void setTrueNameSrch(String trueNameSrch) {
        this.trueNameSrch = trueNameSrch;
    }

    public String getStartTimeSrch() {
        return startTimeSrch;
    }

    public void setStartTimeSrch(String startTimeSrch) {
        this.startTimeSrch = startTimeSrch;
    }

    public String getEndTimeSrch() {
        return endTimeSrch;
    }

    public void setEndTimeSrch(String endTimeSrch) {
        this.endTimeSrch = endTimeSrch;
    }

    public String getStatusSrch() {
        return statusSrch;
    }

    public void setStatusSrch(String statusSrch) {
        this.statusSrch = statusSrch;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getLimitStart() {
        return limitStart;
    }

    public void setLimitStart(int limitStart) {
        this.limitStart = limitStart;
    }

    public int getLimitEnd() {
        return limitEnd;
    }

    public void setLimitEnd(int limitEnd) {
        this.limitEnd = limitEnd;
    }
}
