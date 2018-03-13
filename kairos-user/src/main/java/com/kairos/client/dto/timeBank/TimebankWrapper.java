package com.kairos.client.dto.timeBank;

import java.util.Date;
import java.util.List;

public class TimebankWrapper {

    private Long unitPositionId;
    private List<CTARuleTemplateBasicDTO> ctaRuleTemplates;
    private int contractedMinByWeek;
    private int workingDaysPerWeek;
    private Long staffId;
    private Date unitPositionDate;
    private Long countryId;

    public TimebankWrapper(Long unitPositionId) {
        this.unitPositionId = unitPositionId;
    }

    public TimebankWrapper(Long unitPositionId, int contractedMinByWeek, int workingDaysPerWeek, Date unitPositionDate) {
        this.unitPositionId = unitPositionId;
        this.contractedMinByWeek = contractedMinByWeek;
        this.workingDaysPerWeek = workingDaysPerWeek;
        this.unitPositionDate = unitPositionDate;
    }

    public TimebankWrapper() {
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Date getUnitPositionDate() {
        return unitPositionDate;
    }

    public void setUnitPositionDate(Date unitPositionDate) {
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

    public List<CTARuleTemplateBasicDTO> getCtaRuleTemplates() {
        return ctaRuleTemplates;
    }

    public void setCtaRuleTemplates(List<CTARuleTemplateBasicDTO> ctaRuleTemplates) {
        this.ctaRuleTemplates = ctaRuleTemplates;
    }


}
