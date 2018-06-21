package com.kairos.activity.persistence.model.unit_settings;

import com.kairos.activity.persistence.model.common.MongoBaseEntity;
import com.kairos.enums.unit_settings.TimeTypeEnum;
import com.kairos.response.dto.web.unit_settings.activity_configuration.PhasePlannedTime;

import java.math.BigInteger;
import java.util.List;

public class ActivityConfiguration extends MongoBaseEntity {
    private Long unitId;
    private BigInteger timeTypeId;
    private TimeTypeEnum timeType;   // if this will
    private List<PhasePlannedTime> phasePlannedTimes;

    public ActivityConfiguration() {
        // dc
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public BigInteger getTimeTypeId() {
        return timeTypeId;
    }

    public void setTimeTypeId(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }

    public List<PhasePlannedTime> getPhasePlannedTimes() {
        return phasePlannedTimes;
    }

    public void setPhasePlannedTimes(List<PhasePlannedTime> phasePlannedTimes) {
        this.phasePlannedTimes = phasePlannedTimes;
    }

    public TimeTypeEnum getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeTypeEnum timeType) {
        this.timeType = timeType;
    }
}
