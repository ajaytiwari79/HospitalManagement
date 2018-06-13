package com.kairos.dto.master_data;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClauseDto {


    @NotNullOrEmpty(message = "Title cannot be empty ")
    @Pattern(message = "Numbers and Special characters are not allowed",regexp = "^[a-zA-Z\\s]+$")
    private String title;

    @Valid
    @NotEmpty(message = "Tags cannot be empty")
    private List<ClauseTagDto> tags = new ArrayList<>();

    @NotNullOrEmpty(message = "description cannot be Empty ")
    private String description;

    @NotNull(message = "Organization  Type cannot be null")
    @NotEmpty(message = "Organization Type cannot be Empty")
    private List<OrganizationTypeAndServiceBasicDto>  organizationTypes;

    @NotNull(message = "Organization Sub Type cannot be null")
    @NotEmpty(message = "Organization Sub Type cannot be Empty")
    private List<OrganizationTypeAndServiceBasicDto>  organizationSubTypes;

    @NotNull(message = "Service Type cannot be null")
    @NotEmpty(message = "Service cannot be Empty")
    private List<OrganizationTypeAndServiceBasicDto>  organizationServices;

    @NotNull(message = "Service Sub Type cannot be null")
    @NotEmpty(message = "Service Sub Type cannot empty")
    private List<OrganizationTypeAndServiceBasicDto>  organizationSubServices;

    @NotEmpty(message = "Account type cannot be Empty")
    private Set<BigInteger> accountType;


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

    public Set<BigInteger> getAccountType() {
        return accountType;
    }

    public void setAccountType(Set<BigInteger> accountType) {
        this.accountType = accountType;
    }
}
