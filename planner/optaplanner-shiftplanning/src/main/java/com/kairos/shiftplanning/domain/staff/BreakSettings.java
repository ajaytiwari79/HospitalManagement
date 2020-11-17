package com.kairos.shiftplanning.domain.staff;

import com.kairos.shiftplanning.domain.activity.Activity;
import lombok.*;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class BreakSettings{
    private Long countryId;
    private Long shiftDurationInMinute;
    private Long breakDurationInMinute;
    private Long expertiseId;
    private Activity activity;
    private boolean primary;
    private boolean includeInPlanning;
    private BigInteger activityId;

}
