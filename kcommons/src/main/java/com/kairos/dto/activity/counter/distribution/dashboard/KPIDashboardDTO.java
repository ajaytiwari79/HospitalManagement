package com.kairos.dto.activity.counter.distribution.dashboard;

import com.kairos.dto.activity.counter.enums.ConfLevel;

import java.math.BigInteger;

public class KPIDashboardDTO {
    private BigInteger id;
    private String parentModuleId;
    private String moduleId;
    private String name;
    private Long countryId;
    private Long unitId;
    private Long staffId;
    private ConfLevel level;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public String getParentModuleId() {
        return parentModuleId;
    }

    public void setParentModuleId(String parentModuleId) {
        this.parentModuleId = parentModuleId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
