package com.kairos.activity.persistence.model.night_worker;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

public class NightWorkerUnitSettings extends MongoBaseEntity {

    // Min age eligible for night worker
    private Integer eligibleMinAge;

    // Max age eligible for night worker
    private Integer eligibleMaxAge;
    private Long unitId;

    public NightWorkerUnitSettings(){
        // default constructor
    }

    public NightWorkerUnitSettings(Integer eligibleMinAge, Integer eligibleMaxAge, Long unitId){
        this.eligibleMinAge = eligibleMinAge;
        this.eligibleMaxAge = eligibleMaxAge;
        this.unitId = unitId;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Integer getEligibleMinAge() {
        return eligibleMinAge;
    }

    public void setEligibleMinAge(Integer eligibleMinAge) {
        this.eligibleMinAge = eligibleMinAge;
    }

    public Integer getEligibleMaxAge() {
        return eligibleMaxAge;
    }

    public void setEligibleMaxAge(Integer eligibleMaxAge) {
        this.eligibleMaxAge = eligibleMaxAge;
    }
}
