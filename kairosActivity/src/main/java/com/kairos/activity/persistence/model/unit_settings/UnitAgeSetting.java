package com.kairos.activity.persistence.model.unit_settings;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

public class UnitAgeSetting extends MongoBaseEntity {

    // Max age for being younger
    private Integer younger;

    // Min age for being older
    private Integer older;
    private Long unitId;

    public UnitAgeSetting(){
        // default constructor
    }

    public UnitAgeSetting(Integer younger, Integer older, Long unitId){
        this.younger = younger;
        this.older = older;
        this.unitId = unitId;
    }

    public Integer getYounger() {
        return younger;
    }

    public void setYounger(Integer younger) {
        this.younger = younger;
    }

    public Integer getOlder() {
        return older;
    }

    public void setOlder(Integer older) {
        this.older = older;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
