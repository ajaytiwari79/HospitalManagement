package com.kairos.dto.activity.break_settings;


import com.kairos.dto.user.country.agreement.cta.cta_response.ActivityTypeDTO;

import java.math.BigInteger;

/**
 * CreatedBy vipulpandey on 20/11/18
 **/
public class BreakSettingsResponseDTO {
    private BigInteger id;
    private Long shiftDurationInMinute;
    private Long breakDurationInMinute;
    private ActivityTypeDTO activity;

    public BreakSettingsResponseDTO() {

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

    public ActivityTypeDTO getActivity() {
        return activity;
    }

    public void setActivity(ActivityTypeDTO activity) {
        this.activity = activity;
    }
}
