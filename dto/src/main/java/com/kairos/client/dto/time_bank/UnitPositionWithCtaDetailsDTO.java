package com.kairos.client.dto.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.joda.time.DateTimeZone;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitPositionWithCtaDetailsDTO {

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
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UnitPositionWithCtaDetailsDTO(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public UnitPositionWithCtaDetailsDTO() {
    }

    public UnitPositionWithCtaDetailsDTO(Long unitPositionId, int contractedMinByWeek, int workingDaysPerWeek, LocalDate unitPositionStartDate, LocalDate unitPositionEndDate) {
        this.unitPositionId = unitPositionId;
        this.contractedMinByWeek = contractedMinByWeek;
        this.workingDaysPerWeek = workingDaysPerWeek;
        this.unitPositionStartDate = unitPositionStartDate;
        this.unitPositionEndDate = unitPositionEndDate;
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
