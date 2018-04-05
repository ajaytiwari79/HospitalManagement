package com.kairos.client.dto.timeBank;

import java.time.LocalDate;
import java.util.List;

public class TimebankWrapper {

    private Long unitPositionId;
    private List<CTARuleTemplateBasicDTO> ctaRuleTemplates;
    private int contractedMinByWeek;
    private int workingDaysPerWeek;
    private Long staffId;
    private LocalDate unitPositionStartDate;
    private LocalDate unitPositionEndDate;
    private Long countryId;

    public TimebankWrapper(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public TimebankWrapper(Long unitPositionId, int contractedMinByWeek, int workingDaysPerWeek, LocalDate unitPositionStartDate) {
        this.unitPositionId = unitPositionId;
        this.contractedMinByWeek = contractedMinByWeek;
        this.workingDaysPerWeek = workingDaysPerWeek;
        this.unitPositionStartDate = unitPositionStartDate;
    }

    public TimebankWrapper() {
    }

    public LocalDate getUnitPositionEndDate() {
        return unitPositionEndDate;
    }

    public void setUnitPositionEndDate(LocalDate unitPositionEndDate) {
        this.unitPositionEndDate = unitPositionEndDate;
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

    public List<CTARuleTemplateBasicDTO> getCtaRuleTemplates() {
        return ctaRuleTemplates;
    }

    public void setCtaRuleTemplates(List<CTARuleTemplateBasicDTO> ctaRuleTemplates) {
        this.ctaRuleTemplates = ctaRuleTemplates;
    }


}
