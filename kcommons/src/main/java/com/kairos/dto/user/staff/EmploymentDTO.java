package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by vipul on 5/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class EmploymentDTO {
    private Long id;
    private String expertiseName;
    private Long startDateMillis;
    private Long endDateMillis;
    private Long lastWorkingDateMillis;
    private int totalWeeklyMinutes;
    private int fullTimeWeeklyMinutes;

    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private float hourlyCost;

    private float salary;
    private Long timeCareExternalId;
}
