package com.kairos.dto.activity.break_settings;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
public class BreakSettingsDTO {
    private BigInteger id;
    @Min(value = 1, message = "error.breakSettings.shiftDuration.must.greaterThanZero")
    private Long shiftDurationInMinute;
    @Min(value = 1, message = "error.breakSettings.breakDuration.must.greaterThanZero")
    private Long breakDurationInMinute;
    private BigInteger activityId;
    private boolean primary;
    private boolean includeInPlanning;

}
