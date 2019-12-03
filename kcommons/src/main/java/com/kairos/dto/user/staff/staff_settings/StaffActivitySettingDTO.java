package com.kairos.dto.user.staff.staff_settings;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StaffActivitySettingDTO {
    private BigInteger id;
    private Long staffId;
    private BigInteger activityId;
    private Long employmentId;
    private Long unitId;
    private Short shortestTime;
    private Short longestTime;
    private Integer minLength;
    private Integer maxThisActivityPerShift;
    private boolean eligibleForMove;
    private LocalTime earliestStartTime;
    private LocalTime latestStartTime;
    private LocalTime maximumEndTime;
    private List<Long> dayTypeIds= new ArrayList<>();
    private LocalTime defaultStartTime;
}
