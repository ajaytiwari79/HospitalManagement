package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.common.MongoBaseEntity;

public class KPIDashboard extends MongoBaseEntity{
    private String parentModuleId;
    private String moduleId;
    private String name;
    private Long countryId;
    private Long unitId;
    private Long staffId;
    private ConfLevel level;
    private boolean enable=true;
    private boolean defaultTab;

    public KPIDashboard() {
        //Default Constructor
    }

    public KPIDashboard(String parentModuleId, String moduleId, String name, Long countryId, Long unitId, Long staffId, ConfLevel level,boolean defaultTab) {
        this.parentModuleId = parentModuleId;
        this.moduleId=moduleId;
        this.name = name;
        this.countryId = countryId;
        this.unitId = unitId;
        this.staffId = staffId;
        this.level = level;
        this.defaultTab=defaultTab;
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

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isDefaultTab() {
        return defaultTab;
    }

    public void setDefaultTab(boolean defaultTab) {
        this.defaultTab = defaultTab;
    }
}
