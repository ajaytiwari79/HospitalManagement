package com.kairos.persistence.model.break_settings;

import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;

@Document
public class BreakSettings extends MongoBaseEntity {
    private Long countryId;
    private Long shiftDurationInMinute;
    private Long breakDurationInMinute;
    private Long expertiseId;
    private BigInteger activityId;

    public BreakSettings() {
        //Default Constructor
    }

    public BreakSettings(Long countryId, Long shiftDurationInMinute, Long breakDurationInMinute, Long expertiseId,BigInteger activityId) {
        this.countryId = countryId;
        this.shiftDurationInMinute = shiftDurationInMinute;
        this.breakDurationInMinute = breakDurationInMinute;
        this.expertiseId = expertiseId;
        this.activityId=activityId;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getShiftDurationInMinute() {
        return shiftDurationInMinute;
    }

    public void setShiftDurationInMinute(Long shiftDurationInMinute) {
        this.shiftDurationInMinute = shiftDurationInMinute;
    }

    public Long getBreakDurationInMinute() {
        return breakDurationInMinute;
    }

    public void setBreakDurationInMinute(Long breakDurationInMinute) {
        this.breakDurationInMinute = breakDurationInMinute;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public BigInteger getActivityId() {
        return activityId;
    }

    public void setActivityId(BigInteger activityId) {
        this.activityId = activityId;
    }
}
