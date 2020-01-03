package com.kairos.dto.user.employment;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.user.country.experties.ExpertiseLineDTO;
import com.kairos.enums.EmploymentSubType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.kairos.commons.utils.ObjectUtils.isNull;

/**
 * @author pradeep
 * @date - 21/12/18
 */
@Getter
@Setter
public class EmploymentLinesDTO implements Comparable<EmploymentLinesDTO> {

    private Long id;
    private Long employmentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer workingDaysInWeek;
    private Integer totalWeeklyHours;
    private Float avgDailyWorkingHours;
    private Integer fullTimeWeeklyMinutes;
    private Integer totalWeeklyMinutes;
    //This is the Intial value of accumulatedTimebank of employment
    private long accumulatedTimebankMinutes;
    private BigDecimal hourlyCost;
    private Long employmentTypeId;
    private EmploymentSubType employmentSubType;


    //This getter is used for Accumulated Timebank calculation
    public LocalDate getEndDateForAccumulatedTimebank() {
        return isNull(endDate) ? LocalDate.now() : endDate;
    }

    public DateTimeInterval getInterval(){
        return endDate==null ? null : new DateTimeInterval(DateUtils.asDate(startDate),DateUtils.asDate(endDate));
    }

    public BigDecimal getHourlyCost() {

        return isNull(hourlyCost) ? new BigDecimal(0): hourlyCost;
    }

    @Override
    public int compareTo(EmploymentLinesDTO employmentLinesDTO) {

            if (getStartDate() == null || employmentLinesDTO.getStartDate() == null) {
                return 0;
            }
            return getStartDate().compareTo(employmentLinesDTO.getStartDate());
        }
}
