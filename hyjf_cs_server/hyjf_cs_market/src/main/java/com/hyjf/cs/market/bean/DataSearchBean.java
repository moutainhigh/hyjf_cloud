package com.hyjf.cs.market.bean;

import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @author lisheng
 * @version DataSearchBean, v0.1 2018/8/21 10:33
 */

public class DataSearchBean implements Serializable {
    @ApiModelProperty(value = "出借时间：开始")
    private String addTimeStart;
    @ApiModelProperty(value = "出借时间：结束")
    private String addTimeEnd;
    @ApiModelProperty(value = "注册时间：开始")
    private String regTimeStart;
    @ApiModelProperty(value = "注册时间：结束")
    private String regTimeEnd;
    @ApiModelProperty(value = "出借类型 1:全部 2计划 3散标")
    private String Type;
    @ApiModelProperty(value = "当前页")
    private int currPage;
    @ApiModelProperty(value = "当前页条数")
    private int pageSize;
    @ApiModelProperty(value = "手机号")
    private String mobile;
    @ApiModelProperty(value = "验证码")
    private String code;

    @ApiModelProperty(value = "姓名")
    private String truename;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "推荐人姓名")
    private String reffername;

    public String getAddTimeStart() {
        return addTimeStart;
    }

    public void setAddTimeStart(String addTimeStart) {
        this.addTimeStart = addTimeStart;
    }

    public String getAddTimeEnd() {
        return addTimeEnd;
    }

    public void setAddTimeEnd(String addTimeEnd) {
        this.addTimeEnd = addTimeEnd;
    }

    public String getRegTimeStart() {
        return regTimeStart;
    }

    public void setRegTimeStart(String regTimeStart) {
        this.regTimeStart = regTimeStart;
    }

    public String getRegTimeEnd() {
        return regTimeEnd;
    }

    public void setRegTimeEnd(String regTimeEnd) {
        this.regTimeEnd = regTimeEnd;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }


    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getReffername() {
        return reffername;
    }

    public void setReffername(String reffername) {
        this.reffername = reffername;
    }
}
