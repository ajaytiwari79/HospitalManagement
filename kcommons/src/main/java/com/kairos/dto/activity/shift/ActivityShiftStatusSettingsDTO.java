package com.kairos.dto.activity.shift;/*
 *Created By Pavan on 29/8/18
 *
 */

import com.kairos.enums.shift.ShiftStatus;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.Set;

@Getter
@Setter
public class ActivityShiftStatusSettingsDTO {
    private BigInteger id;
    @NotNull
    private BigInteger activityId;
    @NotNull
    private BigInteger phaseId;
    @NotNull
    private ShiftStatus shiftStatus;
    private Set<Long> accessGroupIds;
}
