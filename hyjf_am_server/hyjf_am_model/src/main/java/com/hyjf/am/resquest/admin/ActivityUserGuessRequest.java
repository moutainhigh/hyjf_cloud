/*
 * @Copyright: 2005-2018 www.hyjf.com. All rights reserved.
 */
package com.hyjf.am.resquest.admin;

import com.hyjf.am.vo.BasePage;

/**
 * @author yaoyong
 * @version ActivityUserGuessRequest, v0.1 2019/4/18 14:47
 */
public class ActivityUserGuessRequest extends BasePage {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 真实姓名
     */
    private String trueName;

    /**
     * 竞猜档位
     */
    private Integer grade;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getTrueName() {
        return trueName;
    }

    public void setTrueName(String trueName) {
        this.trueName = trueName;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }
}
