package com.kairos.dto.activity.break_settings;

import javax.validation.constraints.Min;
import java.math.BigInteger;

public class BreakSettingsDTO {
    private BigInteger id;
    @Min(value = 1, message = "error.breakSettings.shiftDuration.must.greaterThanZero")
    private Long shiftDurationInMinute;
    @Min(value = 1, message = "error.breakSettings.breakDuration.must.greaterThanZero")
    private Long breakDurationInMinute;
    private Long expertiseId;
    private BigInteger activityId;


    public BreakSettingsDTO() {
        //Default Constructor
    }

    public BreakSettingsDTO(Long shiftDurationInMinute, Long breakDurationInMinute, Long expertiseId) {
        this.shiftDurationInMinute = shiftDurationInMinute;
        this.breakDurationInMinute = breakDurationInMinute;
        this.expertiseId = expertiseId;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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
