package com.kairos.persistence.model.user.employment.query_result;

import com.kairos.dto.activity.cta.CTAResponseDTO;
import com.kairos.dto.activity.wta.basic_details.WTAResponseDTO;
import com.kairos.dto.user.staff.staff.StaffChildDetailDTO;
import com.kairos.enums.EmploymentSubType;
import com.kairos.persistence.model.pay_table.PayGrade;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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
}
