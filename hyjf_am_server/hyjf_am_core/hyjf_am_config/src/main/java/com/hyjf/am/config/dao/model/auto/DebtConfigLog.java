package com.hyjf.am.config.dao.model.auto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DebtConfigLog implements Serializable {
    /**
     * 主键ID
     *
     * @mbggenerated
     */
    private Integer id;

    /**
     * 债转配置主键
     *
     * @mbggenerated
     */
    private Integer hyjfDebtConfigId;

    /**
     * 服务费率
     *
     * @mbggenerated
     */
    private BigDecimal attornRate;

    /**
     * 折让率上限
     *
     * @mbggenerated
     */
    private BigDecimal concessionRateUp;

    /**
     * 折让率下限
     *
     * @mbggenerated
     */
    private BigDecimal concessionRateDown;

    /**
     * 散标债转开关
     *
     * @mbggenerated
     */
    private Integer toggle;

    /**
     * 关闭提示
     *
     * @mbggenerated
     */
    private String closeDes;

    /**
     * 修改人
     *
     * @mbggenerated
     */
    private Integer updateUser;

    /**
     * 修改人名称
     *
     * @mbggenerated
     */
    private String updateUsername;

    /**
     * 最后修改时间
     *
     * @mbggenerated
     */
    private Date updateTime;

    /**
     * ip地址
     *
     * @mbggenerated
     */
    private String ipAddress;

    /**
     * MAC地址
     *
     * @mbggenerated
     */
    private String macAddress;

    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getHyjfDebtConfigId() {
        return hyjfDebtConfigId;
    }

    public void setHyjfDebtConfigId(Integer hyjfDebtConfigId) {
        this.hyjfDebtConfigId = hyjfDebtConfigId;
    }

    public BigDecimal getAttornRate() {
        return attornRate;
    }

    public void setAttornRate(BigDecimal attornRate) {
        this.attornRate = attornRate;
    }

    public BigDecimal getConcessionRateUp() {
        return concessionRateUp;
    }

    public void setConcessionRateUp(BigDecimal concessionRateUp) {
        this.concessionRateUp = concessionRateUp;
    }

    public BigDecimal getConcessionRateDown() {
        return concessionRateDown;
    }

    public void setConcessionRateDown(BigDecimal concessionRateDown) {
        this.concessionRateDown = concessionRateDown;
    }

    public Integer getToggle() {
        return toggle;
    }

    public void setToggle(Integer toggle) {
        this.toggle = toggle;
    }

    public String getCloseDes() {
        return closeDes;
    }

    public void setCloseDes(String closeDes) {
        this.closeDes = closeDes == null ? null : closeDes.trim();
    }

    public Integer getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Integer updateUser) {
        this.updateUser = updateUser;
    }

    public String getUpdateUsername() {
        return updateUsername;
    }

    public void setUpdateUsername(String updateUsername) {
        this.updateUsername = updateUsername == null ? null : updateUsername.trim();
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress == null ? null : ipAddress.trim();
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress == null ? null : macAddress.trim();
    }
}