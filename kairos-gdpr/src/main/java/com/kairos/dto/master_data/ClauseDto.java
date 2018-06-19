package com.kairos.dto.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.response.dto.master_data.AccountTypeRequestAndResponseDto;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseDto {


    @NotNullOrEmpty(message = "Title cannot be empty ")
    @Pattern(message = "Numbers and Special characters are not allowed",regexp = "^[a-zA-Z\\s]+$")
    private String title;

    @Valid
    @NotEmpty(message = "Tags  can't be empty")
    private List<ClauseTagDto> tags = new ArrayList<>();

    @NotNullOrEmpty(message = "description  can't be  Empty ")
    private String description;

    @NotNull(message = "Organization  Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDto>  organizationTypes;

    @NotNull(message = "Organization Sub Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDto>  organizationSubTypes;

    @NotNull(message = "Service Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDto>  organizationServices;

    @NotNull(message = "Service Sub Type  can't be  null")
    @Valid
    private List<OrganizationTypeAndServiceBasicDto>  organizationSubServices;

    @NotNull(message = "Account Type cannot be null")
    @Valid
    private Set<AccountTypeRequestAndResponseDto> accountType;


    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ClauseTagDto> getTags() {
        return tags;
    }

    public void setTags(List<ClauseTagDto> tags) {
        this.tags = tags;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeAndServiceBasicDto> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeAndServiceBasicDto> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationTypeAndServiceBasicDto> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<OrganizationTypeAndServiceBasicDto> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public Set<AccountTypeRequestAndResponseDto> getAccountType() {
        return accountType;
    }

    public void setAccountType(Set<AccountTypeRequestAndResponseDto> accountType) {
        this.accountType = accountType;
    }
}
