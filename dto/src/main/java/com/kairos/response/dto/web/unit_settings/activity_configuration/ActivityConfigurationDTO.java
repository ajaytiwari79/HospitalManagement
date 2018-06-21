package com.kairos.response.dto.web.unit_settings.activity_configuration;

import com.kairos.enums.unit_settings.TimeTypeEnum;

import java.math.BigInteger;
import java.util.List;

public class ActivityConfigurationDTO {
    private BigInteger id;
    private TimeTypeEnum timeType;
    private BigInteger timeTypeId;
    private List<PhasePlannedTime> phasePlannedTimes;

    public ActivityConfigurationDTO() {
        //DC
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

    public TimeTypeEnum getTimeType() {
        return timeType;
    }

    public void setTimeType(TimeTypeEnum timeType) {
        this.timeType = timeType;
    }

    public List<PhasePlannedTime> getPhasePlannedTimes() {
        return phasePlannedTimes;
    }

    public void setPhasePlannedTimes(List<PhasePlannedTime> phasePlannedTimes) {
        this.phasePlannedTimes = phasePlannedTimes;
    }

    public BigInteger getTimeTypeId() {
        return timeTypeId;
    }

    public void setTimeTypeId(BigInteger timeTypeId) {
        this.timeTypeId = timeTypeId;
    }
}
