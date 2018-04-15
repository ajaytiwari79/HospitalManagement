package com.kairos.activity.persistence.model.kpi;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class UnitRoleWiseKpi extends MongoBaseEntity{
    private BigInteger unitId;
    private BigInteger roleId;
    private BigInteger modulewiseKpiId;

    public UnitRoleWiseKpi(){

    }

    public UnitRoleWiseKpi(BigInteger unitId, BigInteger roleId, BigInteger modulewiseCounterId){
        this.modulewiseKpiId = modulewiseCounterId;
        this.roleId = roleId;
        this.unitId = unitId;
    }
    public BigInteger getUnitId() {
        return unitId;
    }

    public void setUnitId(BigInteger unitId) {
        this.unitId = unitId;
    }

    public BigInteger getModulewiseKpiId() {
        return modulewiseKpiId;
    }

    public void setModulewiseKpiId(BigInteger modulewiseKpiId) {
        this.modulewiseKpiId = modulewiseKpiId;
    }

    public BigInteger getRoleId() {
        return roleId;
    }

    public void setRoleId(BigInteger roleId) {
        this.roleId = roleId;
    }
}
