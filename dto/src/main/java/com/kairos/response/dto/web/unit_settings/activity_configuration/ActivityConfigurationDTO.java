package com.kairos.response.dto.web.unit_settings.activity_configuration;

import com.kairos.enums.unit_settings.TimeTypeEnum;

import java.math.BigInteger;

public class ActivityConfigurationDTO {
    private BigInteger id;
    private TimeTypeEnum timeType;  // presence absence
    private PresencePlannedTime presencePlannedTime;   // this is applicable for presence
    private AbsencePlannedTime absencePlannedTime; // this is only for absence


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

    public PresencePlannedTime getPresencePlannedTime() {
        return presencePlannedTime;
    }

    public void setPresencePlannedTime(PresencePlannedTime presencePlannedTime) {
        this.presencePlannedTime = presencePlannedTime;
    }

    public AbsencePlannedTime getAbsencePlannedTime() {
        return absencePlannedTime;
    }

    public void setAbsencePlannedTime(AbsencePlannedTime absencePlannedTime) {
        this.absencePlannedTime = absencePlannedTime;
    }
}
