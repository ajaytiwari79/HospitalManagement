package com.kairos.client.dto.timeBank;

import com.kairos.persistence.model.user.agreement.cta.CTARuleTemplateDTO;

import java.util.Date;
import java.util.List;

public class CostTimeAgreementDTO {

    private Long unitEmploymentPositionId;
    private List<CTARuleTemplateBasicDTO> ctaRuleTemplates;
    private int contractedMinByWeek;
    private int workingDaysPerWeek;
    private Long staffId;
    private Date unitEmploymentPositionDate;
    private Long countryId;

    public CostTimeAgreementDTO(Long unitEmploymentPositionId) {
        this.unitEmploymentPositionId = unitEmploymentPositionId;
    }

    public CostTimeAgreementDTO(Long unitEmploymentPositionId, int contractedMinByWeek, int workingDaysPerWeek, Date unitEmploymentPositionDate) {
        this.unitEmploymentPositionId = unitEmploymentPositionId;
        this.contractedMinByWeek = contractedMinByWeek;
        this.workingDaysPerWeek = workingDaysPerWeek;
        this.unitEmploymentPositionDate = unitEmploymentPositionDate;
    }

    public CostTimeAgreementDTO() {
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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

    public int getWorkingDaysPerWeek() {
        return workingDaysPerWeek;
    }

    public void setWorkingDaysPerWeek(int workingDaysPerWeek) {
        this.workingDaysPerWeek = workingDaysPerWeek;
    }

    public Long getUnitEmploymentPositionId() {
        return unitEmploymentPositionId;
    }

    public void setUnitEmploymentPositionId(Long unitEmploymentPositionId) {
        this.unitEmploymentPositionId = unitEmploymentPositionId;
    }

    public List<CTARuleTemplateBasicDTO> getCtaRuleTemplates() {
        return ctaRuleTemplates;
    }

    public void setCtaRuleTemplates(List<CTARuleTemplateBasicDTO> ctaRuleTemplates) {
        this.ctaRuleTemplates = ctaRuleTemplates;
    }


}
