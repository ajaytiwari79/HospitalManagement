package com.kairos.activity.response.dto.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTimeZone;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimebankWrapper {

    private Long unitPositionId;
    private List<CTARuleTemplateDTO> ctaRuleTemplates;
    private int contractedMinByWeek;
    private int workingDaysPerWeek;
    private Long staffId;
    private LocalDate unitPositionStartDate;
    private LocalDate unitPositionEndDate;
    private Long countryId;
    private int minutesFromCta;
    private ZoneId unitTimeZone;
    private DateTimeZone unitDateTimeZone;


    public TimebankWrapper(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public TimebankWrapper() {
    }

    public DateTimeZone getUnitDateTimeZone() {
        return unitDateTimeZone;
    }

    public void setUnitDateTimeZone(DateTimeZone unitDateTimeZone) {
        this.unitDateTimeZone = unitDateTimeZone;
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

    public TimebankWrapper(LocalDate unitPositionStartDate) {
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

    public Long getUnitPositionId() {
        return unitPositionId;
    }

    public void setUnitPositionId(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public List<CTARuleTemplateDTO> getCtaRuleTemplates() {
        return ctaRuleTemplates;
    }

    public void setCtaRuleTemplates(List<CTARuleTemplateDTO> ctaRuleTemplates) {
        this.ctaRuleTemplates = ctaRuleTemplates;
    }


}
