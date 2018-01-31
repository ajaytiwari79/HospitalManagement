package com.kairos.response.dto.web.timeBank;

import java.util.List;

public class CostTimeAgreementDTO {

    private Long unitEmploymentPositionId;
    private List<CTARuleTemplateDTO> ctaRuleTemplateDTOS;
    private int contractedMin;
    private int workingDays;


    public CostTimeAgreementDTO(Long unitEmploymentPositionId) {
        this.unitEmploymentPositionId = unitEmploymentPositionId;
    }

    public int getContractedMin() {
        return contractedMin;
    }

    public void setContractedMin(int contractedMin) {
        this.contractedMin = contractedMin;
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
