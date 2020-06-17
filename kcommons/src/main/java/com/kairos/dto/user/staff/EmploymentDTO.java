package com.kairos.dto.user.staff;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.user.country.agreement.cta.cta_response.EmploymentTypeDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import com.kairos.enums.EmploymentSubType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by vipul on 5/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class EmploymentDTO {
    private Long id;
    private Long expertiseId;
    private String expertiseName;
    private Long startDateMillis;
    private Long endDateMillis;
    private Long lastWorkingDateMillis;
    private int totalWeeklyMinutes;
    private int fullTimeWeeklyMinutes;
    private LocalDate startDate;
    private LocalDate endDate;
    private float avgDailyWorkingHours;
    private int workingDaysInWeek;
    private float hourlyCost;
    private boolean published;
    private boolean nightWorker;
    private float salary;
    private Long timeCareExternalId;
    private EmploymentSubType employmentSubType;
    private List<EmploymentLinesDTO> employmentLinesDTOS;
    private List<EmploymentLinesDTO> employmentLines;
    private EmploymentTypeDTO employmentType;


}
