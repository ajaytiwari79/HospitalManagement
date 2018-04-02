package com.kairos.activity.response.dto.time_bank;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TimebankWrapper {

    private Long unitPositionId;
    private List<CTARuleTemplateDTO> ctaRuleTemplates;
    private int contractedMinByWeek;
    private int workingDaysPerWeek;
    private Long staffId;
    private LocalDate unitPositionDate;
    private Long countryId;
    private int minutesFromCta;


    public TimebankWrapper(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public TimebankWrapper() {
    }

    public int getMinutesFromCta() {
        return minutesFromCta;
    }

    public void setMinutesFromCta(int minutesFromCta) {
        this.minutesFromCta = minutesFromCta;
    }

    public TimebankWrapper(LocalDate unitPositionDate) {
        this.unitPositionDate = unitPositionDate;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public LocalDate getUnitPositionDate() {
        return unitPositionDate;
    }

    public void setUnitPositionDate(LocalDate unitPositionDate) {
        this.unitPositionDate = unitPositionDate;
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
