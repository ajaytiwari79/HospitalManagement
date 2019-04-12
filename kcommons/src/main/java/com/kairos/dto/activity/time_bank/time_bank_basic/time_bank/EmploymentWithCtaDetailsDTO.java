package com.kairos.dto.activity.time_bank.time_bank_basic.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.activity.time_bank.CTARuleTemplateCalulatedTimeBankDTO;
import org.joda.time.DateTimeZone;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EmploymentWithCtaDetailsDTO {

    private Long employmentId;
    private List<CTARuleTemplateCalulatedTimeBankDTO> ctaRuleTemplates;
    private int contractedMinByWeek;
    private int workingDaysPerWeek;
    private Long staffId;
    private LocalDate employmentStartDate;
    private LocalDate employmentEndDate;
    private int totalWeeklyMinutes;
    private int fullTimeWeeklyMinutes;
    private Long countryId;
    private int minutesFromCta;
    private ZoneId unitTimeZone;




    public EmploymentWithCtaDetailsDTO(Long employmentId) {
        this.employmentId = employmentId;
    }

    public EmploymentWithCtaDetailsDTO(Long employmentId, int contractedMinByWeek, int workingDaysPerWeek, LocalDate employmentStartDate, LocalDate employmentEndDate) {
        this.employmentId = employmentId;
        this.contractedMinByWeek = contractedMinByWeek;
        this.workingDaysPerWeek = workingDaysPerWeek;
        this.employmentStartDate = employmentStartDate;
        this.employmentEndDate = employmentEndDate;
    }

    public EmploymentWithCtaDetailsDTO() {
    }

    public DateTimeZone getUnitDateTimeZone() {
        return unitTimeZone!=null?DateTimeZone.forID(unitTimeZone.getId()):null;
    }

    public int getTotalWeeklyMinutes() {
        return totalWeeklyMinutes;
    }

    public void setTotalWeeklyMinutes(int totalWeeklyMinutes) {
        this.totalWeeklyMinutes = totalWeeklyMinutes;
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

    public LocalDate getEmploymentEndDate() {
        return employmentEndDate;
    }

    public void setEmploymentEndDate(LocalDate employmentEndDate) {
        this.employmentEndDate = employmentEndDate;
    }

    public int getMinutesFromCta() {
        return minutesFromCta;
    }

    public void setMinutesFromCta(int minutesFromCta) {
        this.minutesFromCta = minutesFromCta;
    }

    public EmploymentWithCtaDetailsDTO(LocalDate employmentStartDate) {
        this.employmentStartDate = employmentStartDate;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public LocalDate getEmploymentStartDate() {
        return employmentStartDate;
    }

    public void setEmploymentStartDate(LocalDate employmentStartDate) {
        this.employmentStartDate = employmentStartDate;
    }


    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public int getContractedMinByWeek() {
        return contractedMinByWeek;
    }

    public void setContractedMinByWeek(int contractedMinByWeek) {
        this.contractedMinByWeek = contractedMinByWeek;
    }

    public int getWorkingDaysPerWeek() {
        return workingDaysPerWeek;
    }

    public void setWorkingDaysPerWeek(int workingDaysPerWeek) {
        this.workingDaysPerWeek = workingDaysPerWeek;
    }

    public Long getEmploymentId() {
        return employmentId;
    }

    public void setEmploymentId(Long employmentId) {
        this.employmentId = employmentId;
    }

    public List<CTARuleTemplateCalulatedTimeBankDTO> getCtaRuleTemplates() {
        return ctaRuleTemplates;
    }

    public void setCtaRuleTemplates(List<CTARuleTemplateCalulatedTimeBankDTO> ctaRuleTemplates) {
        this.ctaRuleTemplates = ctaRuleTemplates;
    }




}
