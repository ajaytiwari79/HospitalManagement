package com.kairos.scheduler.persistence.model.unit_settings;

import com.kairos.scheduler.persistence.model.common.MongoBaseEntity;

import java.time.ZoneId;

public class UnitTimeZoneMapping extends MongoBaseEntity {

    private Long unitId;
    private String timezone;

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }
    public UnitTimeZoneMapping() {

    }
    public UnitTimeZoneMapping(Long unitId,String timezone) {
        this.unitId = unitId;
        this.timezone = timezone;
    }
    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }



}
