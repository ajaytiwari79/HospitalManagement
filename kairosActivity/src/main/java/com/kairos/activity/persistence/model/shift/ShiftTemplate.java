package com.kairos.activity.persistence.model.shift;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ShiftTemplate extends MongoBaseEntity {
    private String name;
    private Set<BigInteger> shiftDayTemplateIds;
    private Long unitId;
    private Long createdBy;

    public ShiftTemplate() {
        //Default Constructor
    }

    public ShiftTemplate(BigInteger id,String name, Set<BigInteger> shiftDayTemplateIds, Long unitId, Long createdBy) {
        this.id=id;
        this.name = name;
        this.shiftDayTemplateIds = shiftDayTemplateIds;
        this.unitId = unitId;
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<BigInteger> getShiftDayTemplateIds() {
        return shiftDayTemplateIds=Optional.ofNullable(shiftDayTemplateIds).orElse(new HashSet<>());
    }

    public void setShiftDayTemplateIds(Set<BigInteger> shiftDayTemplateIds) {
        this.shiftDayTemplateIds = shiftDayTemplateIds;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
}
