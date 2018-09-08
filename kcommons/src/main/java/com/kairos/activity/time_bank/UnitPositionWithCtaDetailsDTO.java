package com.kairos.activity.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.activity.cta.CTARuleTemplateDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitPositionWithCtaDetailsDTO {

    private Long id;
    private List<CTARuleTemplateDTO> ctaRuleTemplates;
    private int totalWeeklyMinutes;
    private int workingDaysInWeek;
    private Long staffId;
    private LocalDate unitPositionStartDate;
    private LocalDate unitPositionEndDate;
    private Long countryId;
    private int minutesFromCta;
    private ZoneId unitTimeZone;
    private int fullTimeWeeklyMinutes;


    public UnitPositionWithCtaDetailsDTO(Long id) {
        this.id = id;
    }

    public UnitPositionWithCtaDetailsDTO() {
    }

    public UnitPositionWithCtaDetailsDTO(Long id, int totalWeeklyMinutes, int workingDaysInWeek, LocalDate unitPositionStartDate, LocalDate unitPositionEndDate) {
        this.id = id;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.workingDaysInWeek = workingDaysInWeek;
        this.unitPositionStartDate = unitPositionStartDate;
        this.unitPositionEndDate = unitPositionEndDate;
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

    public LocalDate getUnitPositionEndDate() {
        return unitPositionEndDate;
    }

    public void setUnitPositionEndDate(LocalDate unitPositionEndDate) {
        this.unitPositionEndDate = unitPositionEndDate;
    }

    public int getMinutesFromCta() {
        return minutesFromCta;
    }

    public void setMinutesFromCta(int minutesFromCta) {
        this.minutesFromCta = minutesFromCta;
    }

    public UnitPositionWithCtaDetailsDTO(LocalDate unitPositionStartDate) {
        this.unitPositionStartDate = unitPositionStartDate;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public LocalDate getUnitPositionStartDate() {
        return unitPositionStartDate;
    }

    public void setUnitPositionStartDate(LocalDate unitPositionStartDate) {
        this.unitPositionStartDate = unitPositionStartDate;
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


}
