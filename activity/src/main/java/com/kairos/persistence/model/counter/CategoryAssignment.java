package com.kairos.persistence.model.counter;

import com.kairos.activity.counter.enums.ConfLevel;

import java.math.BigInteger;

public class CategoryAssignment {
    private BigInteger categoryId;
    private Long countryId;
    private Long unitId;
    private ConfLevel level;

    public CategoryAssignment() {

    }

    public BigInteger getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(BigInteger categoryId) {
        this.categoryId = categoryId;
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
}
