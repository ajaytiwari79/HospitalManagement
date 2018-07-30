package com.kairos.persistence.model.counter;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: Jun/26/2018
 */

public class UnitRoleCounter extends MongoBaseEntity {
    private BigInteger unitId;
    private BigInteger roleId;
    private BigInteger refCounterId;

    public UnitRoleCounter() {

    }

    public UnitRoleCounter(BigInteger unitId, BigInteger roleId, BigInteger refCounterId) {
        this.refCounterId = refCounterId;
        this.roleId = roleId;
        this.unitId = unitId;
    }

    public BigInteger getUnitId() {
        return unitId;
    }

    public void setUnitId(BigInteger unitId) {
        this.unitId = unitId;
    }

    public BigInteger getRefCounterId() {
        return refCounterId;
    }

    public void setRefCounterId(BigInteger refCounterId) {
        this.refCounterId = refCounterId;
    }

    public BigInteger getRoleId() {
        return roleId;
    }

    public void setRoleId(BigInteger roleId) {
        this.roleId = roleId;
    }

}
