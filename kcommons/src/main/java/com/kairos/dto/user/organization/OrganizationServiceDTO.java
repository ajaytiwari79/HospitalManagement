package com.kairos.dto.user.organization;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by prerna on 15/11/17.
 */
public class OrganizationServiceDTO {
    @NotEmpty(message = "error.Organization.Service.customName.notEmptyOrNotNull") @NotNull(message = "error.Organization.Service.customName.notEmptyOrNotNull")
    private String customName;

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}
