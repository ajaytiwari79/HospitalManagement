package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.distribution.tab.KPIPosition;
import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.dto.activity.counter.enums.CounterSize;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class DashboardKPIConf extends MongoBaseEntity {
    private BigInteger kpiId;
    private BigInteger dashboardId;
    private String moduleId;
    private Long countryId;
    private Long unitId;
    private Long staffId;
    private ConfLevel level;
    private CounterSize size;
    private KPIPosition position;


    public DashboardKPIConf() {
    }

    public DashboardKPIConf(BigInteger kpiId, BigInteger dashboardId, String moduleId, Long countryId, Long unitId, Long staffId, ConfLevel level, KPIPosition position) {
        this.kpiId = kpiId;
        this.dashboardId = dashboardId;
        this.moduleId=moduleId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
        this.position=position;
    }

    public DashboardKPIConf(BigInteger kpiId, String moduleId, Long countryId, Long unitId, Long staffId, ConfLevel level, KPIPosition position) {
        this.kpiId = kpiId;
        this.moduleId = moduleId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
        this.position = position;
    }

    public CounterSize getSize() {
        return size;
    }

    public void setSize(CounterSize size) {
        this.size = size;
    }

    public KPIPosition getPosition() {
        return position;
    }

    public void setPosition(KPIPosition position) {
        this.position = position;
    }

    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
    }

    public BigInteger getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(BigInteger dashboardId) {
        this.dashboardId = dashboardId;
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

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }
}
