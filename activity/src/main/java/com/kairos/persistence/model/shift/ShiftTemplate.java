package com.kairos.persistence.model.shift;

import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ShiftTemplate extends MongoBaseEntity {
    private String name;
    private Set<BigInteger> individualShiftTemplateIds;
    private Long unitId;

    public ShiftTemplate() {
        //Default Constructor
    }

    public ShiftTemplate(String name, Set<BigInteger> individualShiftTemplateIds, Long unitId, Long createdBy) {
        this.name = name;
        this.individualShiftTemplateIds = individualShiftTemplateIds;
        this.unitId = unitId;
        this.createdBy = createdBy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<BigInteger> getIndividualShiftTemplateIds() {
        return individualShiftTemplateIds =Optional.ofNullable(individualShiftTemplateIds).orElse(new HashSet<>());
    }

    public void setIndividualShiftTemplateIds(Set<BigInteger> individualShiftTemplateIds) {
        this.individualShiftTemplateIds = individualShiftTemplateIds;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

}
