package com.kairos.dto.activity.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;
import com.kairos.dto.user.employment.EmploymentLinesDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
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


    public EmploymentWithCtaDetailsDTO(Long id) {
        this.id = id;
    }

    public EmploymentWithCtaDetailsDTO() {
    }

    public EmploymentWithCtaDetailsDTO(Long id, int totalWeeklyMinutes, int workingDaysInWeek, LocalDate startDate, LocalDate endDate, int totalWeeklyHours) {
        this.id = id;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.workingDaysInWeek = workingDaysInWeek;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public EmploymentWithCtaDetailsDTO(Long id, Integer totalWeeklyHours, int totalWeeklyMinutes, int workingDaysInWeek, Long staffId, LocalDate startDate, LocalDate endDate, List<EmploymentLinesDTO> employmentLines, long accumulatedTimebankMinutes, LocalDate accumulatedTimebankDate,Long unitId) {
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
    }

    public List<EmploymentLinesDTO> getEmploymentLines() {
        return employmentLines;
    }

    public void setEmploymentLines(List<EmploymentLinesDTO> employmentLines) {
        this.employmentLines = Optional.ofNullable(employmentLines).isPresent() ? employmentLines : new ArrayList<>();
    }

    public Integer getTotalWeeklyHours() {
        return totalWeeklyHours;
    }

    public void setTotalWeeklyHours(Integer totalWeeklyHours) {
        this.totalWeeklyHours = totalWeeklyHours;
    }

    public float getHourlyCost() {
        return hourlyCost;
    }

    public void setHourlyCost(float hourlyCost) {
        this.hourlyCost = hourlyCost;
    }

    public int getFullTimeWeeklyMinutes() {
        return fullTimeWeeklyMinutes;
    }

    public void setFullTimeWeeklyMinutes(int fullTimeWeeklyMinutes) {
        this.fullTimeWeeklyMinutes = fullTimeWeeklyMinutes;
    }

    public ZoneId getUnitTimeZone() {
        return unitTimeZone;
    }

    public void setUnitTimeZone(ZoneId unitTimeZone) {
        this.unitTimeZone = unitTimeZone;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getMinutesFromCta() {
        return minutesFromCta;
    }

    public void setMinutesFromCta(int minutesFromCta) {
        this.minutesFromCta = minutesFromCta;
    }

    public EmploymentWithCtaDetailsDTO(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }


    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public int getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public void setTotalWeeklyMinutes(int totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
    }

    public int getWorkingDaysInWeek() {
        return workingDaysInWeek;
    }

    public void setWorkingDaysInWeek(int workingDaysInWeek) {
        this.workingDaysInWeek = workingDaysInWeek;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<CTARuleTemplateDTO> getCtaRuleTemplates() {
        return ctaRuleTemplates;
    }

    public void setCtaRuleTemplates(List<CTARuleTemplateDTO> ctaRuleTemplates) {
        this.ctaRuleTemplates = ctaRuleTemplates;
    }

    public long getAccumulatedTimebankMinutes() {
        return accumulatedTimebankMinutes;
    }

    public void setAccumulatedTimebankMinutes(long accumulatedTimebankMinutes) {
        this.accumulatedTimebankMinutes = accumulatedTimebankMinutes;
    }

    public LocalDate getAccumulatedTimebankDate() {
        return accumulatedTimebankDate;
    }

    public void setAccumulatedTimebankDate(LocalDate accumulatedTimebankDate) {
        this.accumulatedTimebankDate = accumulatedTimebankDate;
    }

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }
}
