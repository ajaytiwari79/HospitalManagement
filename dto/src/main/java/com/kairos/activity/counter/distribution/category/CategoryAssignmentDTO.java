package com.kairos.activity.counter.distribution.category;

import com.kairos.activity.counter.KPICategoryDTO;
import com.kairos.activity.counter.enums.ConfLevel;

import java.math.BigInteger;

public class CategoryAssignmentDTO {
    private BigInteger id;
    private KPICategoryDTO category;
    private Long countryId;
    private Long unitId;
    private ConfLevel level;

    public CategoryAssignmentDTO(){}

    public KPICategoryDTO getCategory() {
        return category;
    }

    public void setCategory(KPICategoryDTO category) {
        this.category = category;
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

    public ConfLevel getLevel() {
        return level;
    }

    public void setLevel(ConfLevel level) {
        this.level = level;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }
}
