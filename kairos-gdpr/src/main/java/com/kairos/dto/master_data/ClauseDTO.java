package com.kairos.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.OrganizationTypeAndServiceBasicDTO;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseDTO {


    @NotNullOrEmpty(message = "Title cannot be empty ")
    @Pattern(message = "Numbers and Special characters are not allowed",regexp = "^[a-zA-Z\\s]+$")
    private String title;

    @Valid
    @NotEmpty(message = "Tags  can't be empty")
    private List<ClauseTagDTO> tags = new ArrayList<>();

    @NotNullOrEmpty(message = "description  can't be  Empty ")
    private String description;

    @NotNull(message = "Organization  Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDTO>  organizationTypes;

    @NotNull(message = "Organization Sub Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDTO>  organizationSubTypes;

    @NotNull(message = "Service Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDTO>  organizationServices;

    @NotNull(message = "Service Sub Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDTO>  organizationSubServices;

    @NotNull(message = "Account Type cannot be null")
    @NotEmpty
    private Set<BigInteger> accountTypes;


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ClauseTagDTO> getTags() {
        return tags;
    }

    public void setTags(List<ClauseTagDTO> tags) {
        this.tags = tags;
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

    public Set<BigInteger> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(Set<BigInteger> accountTypes) {
        this.accountTypes = accountTypes;
    }
}
