package com.kairos.dto.user.staff.employment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffEmploymentQueryResult {

    private Long staffId;
    private String staffEmail;
    private Long employmentId;
    private Integer workingDaysPerWeek;
    private Integer totalWeeklyHours;
    private Integer contractedMinByWeek;
    private Long startDate;
    private Long endDate;
    private Integer accumulatedTimeBank;
    private Integer deltaWeeklytimeBank;
    private Integer plannedHoursWeek;



    public String toString() {
    return this.staffId+" ---- "+this.staffEmail;
    }
}
