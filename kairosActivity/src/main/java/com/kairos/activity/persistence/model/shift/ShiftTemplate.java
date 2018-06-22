package com.kairos.activity.persistence.model.shift;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.Set;

public class ShiftTemplate extends MongoBaseEntity {
    private String name;
    private Set<BigInteger> shiftDayTemplateIds;

    public ShiftTemplate() {
        //Default Constructor
    }

    public ShiftTemplate(String name, Set<BigInteger> shiftDayTempplateIds) {
        this.name = name;
        this.shiftDayTemplateIds = shiftDayTempplateIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<BigInteger> getShiftDayTempplateIds() {
        return shiftDayTemplateIds;
    }

    public void setShiftDayTempplateIds(Set<BigInteger> shiftDayTempplateIds) {
        this.shiftDayTemplateIds = shiftDayTempplateIds;
    }
}
