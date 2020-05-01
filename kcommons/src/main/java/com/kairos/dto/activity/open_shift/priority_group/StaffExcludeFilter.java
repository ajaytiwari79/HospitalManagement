package com.kairos.dto.activity.open_shift.priority_group;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class StaffExcludeFilter {
    private Integer numberOfShiftAssigned;
    private Integer numberOfPendingRequest;
    private Integer unitExperienceInWeek;
    private Integer minTimeBank; // In Minutes
    private Integer minRestingTimeBeforeShiftStart; // In Minutes
    private Integer minRestingTimeAfterShiftEnd; // In Minutes
    private Integer maxPlannedTime; // In Minutes
    private Integer maxDeltaWeeklyTimeBankPerWeek; // In Minutes
    private boolean personalEntriesFoundFromPrivateCalender;
    private Integer lastWorkingDaysInUnit;
    private Integer lastWorkingDaysWithActivity;
    private Integer minRemainingTimeLeftInActivityPlanningPeriod; //In Minutes
    private boolean negativeAvailabilityInCalender;
    private boolean veto;
    private boolean stopBricks;
}
