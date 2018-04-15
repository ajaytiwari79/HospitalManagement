package com.kairos.activity.persistence.model.counter;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class UnitRoleWiseCounter extends MongoBaseEntity{
    private BigInteger unitId;
    private BigInteger roleId;
    private BigInteger modulewiseCounterId;

    public UnitRoleWiseCounter(){

    }

    public UnitRoleWiseCounter(BigInteger unitId, BigInteger roleId, BigInteger modulewiseCounterId){
        this.modulewiseCounterId = modulewiseCounterId;
        this.roleId = roleId;
        this.unitId = unitId;
    }
    public BigInteger getUnitId() {
        return unitId;
    }

    public void setUnitId(BigInteger unitId) {
        this.unitId = unitId;
    }

    public BigInteger getModulewiseCounterId() {
        return modulewiseCounterId;
    }

    public void setModulewiseCounterId(BigInteger modulewiseCounterId) {
        this.modulewiseCounterId = modulewiseCounterId;
    }

    public BigInteger getRoleId() {
        return roleId;
    }

    public void setRoleId(BigInteger roleId) {
        this.roleId = roleId;
    }
}
