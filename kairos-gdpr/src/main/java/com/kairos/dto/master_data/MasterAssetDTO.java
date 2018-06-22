package com.kairos.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.OrganizationTypeAndServiceBasicDTO;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MasterAssetDTO {

    @NotNullOrEmpty(message = "Name  can't be Empty")
    @Pattern(regexp = "^[a-zA-Z\\s]+$",message = "title can not contain number or special character")
    private String name;

    @NotNullOrEmpty(message = "Description cannot be Empty")
    private String description;

    @NotNull(message = "Organization Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDTO> organizationTypes;

    @NotNull(message = "Organization Sub Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDTO> organizationSubTypes;

    @NotNull(message = "Service Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDTO> organizationServices;

    @NotNull(message = "Service Sub Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDTO> organizationSubServices;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OrganizationTypeAndServiceBasicDTO> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeAndServiceBasicDTO> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationTypeAndServiceBasicDTO> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeAndServiceBasicDTO> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<OrganizationTypeAndServiceBasicDTO> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationTypeAndServiceBasicDTO> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<OrganizationTypeAndServiceBasicDTO> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<OrganizationTypeAndServiceBasicDTO> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }
}
