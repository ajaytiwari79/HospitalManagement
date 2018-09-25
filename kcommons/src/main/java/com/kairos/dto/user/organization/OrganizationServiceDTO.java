package com.kairos.dto.user.organization;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by prerna on 15/11/17.
 */
public class OrganizationServiceDTO {
    @NotEmpty(message = "error.Organization.Service.customName.notEmptyOrNotNull") @NotNull(message = "error.Organization.Service.customName.notEmptyOrNotNull")
    private String customName;
    private Long id;
    private String name;
    private List<OrganizationServiceDTO> organizationSubServices;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrganizationServiceDTO> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<OrganizationServiceDTO> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public String getCustomName() {
        return customName;
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }
}
