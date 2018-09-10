package com.kairos.dto.activity.counter;

import com.kairos.dto.activity.counter.enums.ConfLevel;
import com.kairos.commons.utils.NotNullOrEmpty;

import java.math.BigInteger;

public class KPICategoryDTO {
    private BigInteger id;
    @NotNullOrEmpty(message = "name can't be empty")
    private String name;
    private Long countryId;
    private Long unitId;
    private ConfLevel level;
    public KPICategoryDTO(){

    }

    public KPICategoryDTO(BigInteger id, String name){
        this.id = id;
        this.name = name;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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
