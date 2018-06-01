package com.kairos.activity.persistence.model.kpi;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class ModuleWiseKpi extends MongoBaseEntity {
    private String moduleId;
    private BigInteger kpiId;
    private BigInteger countryId;

    public ModuleWiseKpi(){

    }

    public ModuleWiseKpi(String moduleId, BigInteger kpiId, BigInteger countryId){
        this.kpiId = kpiId;
        this.moduleId = moduleId;
        this.countryId = countryId;
    }

    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public BigInteger getCountryId() {
        return countryId;
    }

    public void setCountryId(BigInteger countryId) {
        this.countryId = countryId;
    }
}
