package com.kairos.persistence.model.user.employment.query_result;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.staff.staff.StaffChildDetailDTO;
import com.kairos.enums.EmploymentSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;
import static com.kairos.commons.utils.ObjectUtils.isNull;

/**
 * CreatedBy vipulpandey on 26/9/18
 **/
@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class EmploymentLinesQueryResult {
    private Long id;
    private Long employmentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer workingDaysInWeek;
    private Integer totalWeeklyHours;
    private Float avgDailyWorkingHours;
    private Integer fullTimeWeeklyMinutes;
    private Double salary;
    private Integer totalWeeklyMinutes;
    private BigDecimal hourlyCost;
    private Map<String, Object> employmentType;
    private Map<String, Object> seniorityLevel;
    private WTAResponseDTO workingTimeAgreement;
    private CTAResponseDTO costTimeAgreement;
    private long accumulatedTimebankMinutes;
    private LocalDate accumulatedTimebankDate;
    private Long employmentTypeId;
    private EmploymentSubType employmentSubType;
    private List<StaffChildDetailDTO> staffChildDetails;
    private long payTableAmount;
    private Long payGradeLevel;

    public EmploymentLinesQueryResult(Long id, LocalDate startDate, LocalDate endDate, Integer workingDaysInWeek, Integer totalWeeklyHours, Float avgDailyWorkingHours, Integer fullTimeWeeklyMinutes, Double salary, Integer totalWeeklyMinutes, BigDecimal hourlyCost, Map<String, Object> employmentType, Map<String, Object> seniorityLevel, Long employmentId, long accumulatedTimebankMinutes) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.workingDaysInWeek = workingDaysInWeek;
        this.totalWeeklyHours = totalWeeklyHours;
        this.avgDailyWorkingHours = avgDailyWorkingHours;
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
        this.salary = salary;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.hourlyCost = hourlyCost;
        this.employmentType = employmentType;
        this.seniorityLevel = seniorityLevel;
        this.employmentId = employmentId;
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
    }

    public boolean isValid(LocalDate startDate, LocalDate endDate){
        DateTimeInterval employmentLineInterval = new DateTimeInterval(this.startDate,isNotNull(this.endDate) ? this.endDate : isNotNull(endDate) ? endDate : LocalDate.now());
        DateTimeInterval interval = new DateTimeInterval(startDate,isNotNull(endDate) ? endDate : isNotNull(this.endDate) ? this.endDate : LocalDate.now());
        return employmentLineInterval.getStart().toLocalDate().equals((startDate))|| employmentLineInterval.contains(startDate) || interval.contains(this.startDate) || interval.getStart().toLocalDate().equals(this.startDate) || employmentLineInterval.overlaps(interval) || employmentLineInterval.abuts(interval);
    }
}
