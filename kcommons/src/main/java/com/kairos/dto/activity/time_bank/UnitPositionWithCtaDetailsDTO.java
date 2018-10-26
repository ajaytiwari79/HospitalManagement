package com.kairos.dto.activity.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.cta.CTARuleTemplateDTO;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitPositionWithCtaDetailsDTO {

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


    public UnitPositionWithCtaDetailsDTO(Long id) {
        this.id = id;
    }

    public UnitPositionWithCtaDetailsDTO() {
    }

    public UnitPositionWithCtaDetailsDTO(Long id, int totalWeeklyMinutes, int workingDaysInWeek, LocalDate startDate, LocalDate endDate,int totalWeeklyHours) {
        this.id = id;
        this.totalWeeklyMinutes = totalWeeklyMinutes;
        this.workingDaysInWeek = workingDaysInWeek;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalWeeklyHours = totalWeeklyHours;
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

    public UnitPositionWithCtaDetailsDTO(LocalDate startDate) {
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


}
