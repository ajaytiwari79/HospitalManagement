package com.kairos.shiftplanning.domain.staff;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmploymentLine {
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalWeeklyMinutes;
    private int fullTimeWeeklyMinutes;  // Its coming from expertise
    private float avgDailyWorkingHours;
    private int workingDaysInWeek;       // same from expertise
    private BigDecimal hourlyCost;

    public DateTimeInterval getInterval(){
        return endDate==null ? null : new DateTimeInterval(DateUtils.asDate(startDate),DateUtils.asDate(endDate));
    }

    public boolean isValidByDate(LocalDate localDate){
        return (isNull(this.getEndDate()) && !this.getStartDate().isAfter(localDate)) || (isNotNull(this.getEndDate()) && !this.getStartDate().isAfter(localDate) && !this.getEndDate().isBefore(localDate));
    }

}
