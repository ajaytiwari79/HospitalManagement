package com.kairos.persistence.model.counter;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.persistence.model.common.MongoBaseEntity;

/*
 * @author: mohit.shakya@oodlestechnologies.com
 * @dated: JUL/05/2018
 * @usage: categories for KPIs.
 */

public class KPICategory extends MongoBaseEntity {
    private String name;
    private Long countryId;
    private Long unitId;
    private ConfLevel level;
    public KPICategory(){}

    public KPICategory(String name, Long countryId, Long unitId, ConfLevel level) {
        this.name = name;
        this.countryId = countryId;
        this.unitId = unitId;
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
