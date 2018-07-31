package com.kairos.persistence.model.counter;

import com.kairos.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class TabKPIConf extends MongoBaseEntity {
    private String tabId;
    private BigInteger kpiAssignmentId;
    private Long countryId;
    private Long unitId;
    private Long staffId;
    private ConfLevel level;

    public TabKPIConf() {

    }

    public TabKPIConf(String tabId, BigInteger kpiAssignmentId, Long countryId, Long unitId, Long staffId, ConfLevel level) {
        this.tabId = tabId;
        this.kpiAssignmentId = kpiAssignmentId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
    }

    public String getTabId() {
        return tabId;
    }

    public void setTabId(String tabId) {
        this.tabId = tabId;
    }

    public BigInteger getKpiAssignmentId() {
        return kpiAssignmentId;
    }

    public void setKpiAssignmentId(BigInteger kpiAssignmentId) {
        this.kpiAssignmentId = kpiAssignmentId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public ConfLevel getLevel() {
        return level;
    }

    public void setLevel(ConfLevel level) {
        this.level = level;
    }
}
