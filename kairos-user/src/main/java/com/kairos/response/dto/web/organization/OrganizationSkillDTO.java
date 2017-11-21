package com.kairos.response.dto.web.organization;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 9/11/17.
 */
public class OrganizationSkillDTO {

    private String customName;

    @NotEmpty(message = "error.Organization.Skill.visitourId.notEmptyOrNotNull") @NotNull(message = "error.Organization.Skill.visitourId.notEmptyOrNotNull")
    private String visitourId;

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getVisitourId() {
        return visitourId;
    }

    public void setVisitourId(String visitourId) {
        this.visitourId = visitourId;
    }
}
