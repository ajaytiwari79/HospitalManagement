package com.kairos.dto.activity.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.commons.utils.ObjectUtils.isNullOrElse;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@NoArgsConstructor
public class EmploymentWithCtaDetailsDTO {

    private Long id;
    private List<CTARuleTemplateDTO> ctaRuleTemplates;
    private Integer totalWeeklyHours;
    private int totalWeeklyMinutes;
    private int workingDaysInWeek;
    private Long staffId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long countryId;
    private int minutesFromCta;
    private ZoneId unitTimeZone;
    private int fullTimeWeeklyMinutes;
    private float hourlyCost;
    private List<EmploymentLinesDTO> employmentLines;
    //This is the Intial value of accumulatedTimebank
    private long accumulatedTimebankMinutes;
    private LocalDate accumulatedTimebankDate;
    private Long unitId;
    private Long employmentTypeId;


    public EmploymentWithCtaDetailsDTO(Long id) {
        this.id = id;
    }


    public EmploymentWithCtaDetailsDTO(Long id, int totalWeeklyMinutes, int workingDaysInWeek, LocalDate startDate, LocalDate endDate, int totalWeeklyHours) {
        this.id = id;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.workingDaysInWeek = workingDaysInWeek;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public EmploymentWithCtaDetailsDTO(Long id, Integer totalWeeklyHours, int totalWeeklyMinutes, int workingDaysInWeek, Long staffId, LocalDate startDate, LocalDate endDate, List<EmploymentLinesDTO> employmentLines, long accumulatedTimebankMinutes, LocalDate accumulatedTimebankDate,Long unitId,Long employmentTypeId) {
        this.id = id;
        this.totalWeeklyHours = totalWeeklyHours;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.workingDaysInWeek = workingDaysInWeek;
        this.staffId = staffId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.employmentLines = employmentLines;
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
        this.accumulatedTimebankDate = accumulatedTimebankDate;
        this.unitId = unitId;
        this.employmentTypeId=employmentTypeId;
    }

}
