package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.common.MongoBaseEntity;

import java.math.BigInteger;

public class CategoryKPIConf extends MongoBaseEntity {
    private BigInteger kpiId;
    private BigInteger categoryId;
    private Long countryId;
    private Long unitId;
    private ConfLevel level;

    public CategoryKPIConf() {
    }

    public CategoryKPIConf(BigInteger kpiId, BigInteger categoryId, Long countryId, Long unitId, ConfLevel level) {
        this.kpiId = kpiId;
        this.categoryId = categoryId;
        this.countryId = countryId;
        this.unitId = unitId;
        this.level = level;
    }

    public BigInteger getKpiId() {
        return kpiId;
    }

    public void setKpiId(BigInteger kpiId) {
        this.kpiId = kpiId;
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
