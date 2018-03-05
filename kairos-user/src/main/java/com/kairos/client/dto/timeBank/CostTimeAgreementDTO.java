package com.kairos.client.dto.timeBank;

import java.util.Date;
import java.util.List;

public class CostTimeAgreementDTO {

    private Long unitEmploymentPositionId;
    private List<CTARuleTemplateDTO> ctaRuleTemplates;
    private int contractedMinByWeek;
    private int workingDays;
    private Long staffId;
    private Date unitEmploymentPositionDate;

    public CostTimeAgreementDTO(Long unitEmploymentPositionId) {
        this.unitEmploymentPositionId = unitEmploymentPositionId;
    }

    public CostTimeAgreementDTO(Long unitEmploymentPositionId, int contractedMinByWeek, int workingDays,Date unitEmploymentPositionDate) {
        this.unitEmploymentPositionId = unitEmploymentPositionId;
        this.contractedMinByWeek = contractedMinByWeek;
        this.workingDays = workingDays;
        this.unitEmploymentPositionDate = unitEmploymentPositionDate;
    }

    public CostTimeAgreementDTO() {
    }

    public Date getUnitEmploymentPositionDate() {
        return unitEmploymentPositionDate;
    }

    public void setUnitEmploymentPositionDate(Date unitEmploymentPositionDate) {
        this.unitEmploymentPositionDate = unitEmploymentPositionDate;
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

    public int getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(int workingDays) {
        this.workingDays = workingDays;
    }

    public Long getUnitEmploymentPositionId() {
        return unitEmploymentPositionId;
    }

    public void setUnitEmploymentPositionId(Long unitEmploymentPositionId) {
        this.unitEmploymentPositionId = unitEmploymentPositionId;
    }

    public List<CTARuleTemplateDTO> getCtaRuleTemplates() {
        return ctaRuleTemplates;
    }

    public void setCtaRuleTemplates(List<CTARuleTemplateDTO> ctaRuleTemplates) {
        this.ctaRuleTemplates = ctaRuleTemplates;
    }


}
