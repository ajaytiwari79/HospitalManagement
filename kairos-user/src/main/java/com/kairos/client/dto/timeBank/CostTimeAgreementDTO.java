package com.kairos.client.dto.timeBank;

import java.util.List;

public class CostTimeAgreementDTO {

    private Long unitEmploymentPositionId;
    private List<CTARuleTemplateDTO> ctaRuleTemplateDTOS;
    private int contractedMinByWeek;
    private int workingDays;
    private Long staffId;

    public CostTimeAgreementDTO(Long unitEmploymentPositionId) {
        this.unitEmploymentPositionId = unitEmploymentPositionId;
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

    public List<CTARuleTemplateDTO> getCtaRuleTemplateDTOS() {
        return ctaRuleTemplateDTOS;
    }

    public void setCtaRuleTemplateDTOS(List<CTARuleTemplateDTO> ctaRuleTemplateDTOS) {
        this.ctaRuleTemplateDTOS = ctaRuleTemplateDTOS;
    }


}
