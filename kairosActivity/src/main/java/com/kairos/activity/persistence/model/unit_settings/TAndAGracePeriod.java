package com.kairos.activity.persistence.model.unit_settings;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;

public class TAndAGracePeriod extends MongoBaseEntity {

    private Long unitId;
    private int gracePeriodDays;

    public TAndAGracePeriod() {
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public int getGracePeriodDays() {
        return gracePeriodDays;
    }

    public void setGracePeriodDays(int gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }

    public TAndAGracePeriod(Long unitId, int gracePeriodDays) {
        this.unitId = unitId;
        this.gracePeriodDays = gracePeriodDays;
    }
}
