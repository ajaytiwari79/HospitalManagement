package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;
import com.kairos.dto.user.country.system_setting.AccountTypeDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseDTO {


    @NotBlank(message = "error.message.title.notNull.orEmpty")
    @Pattern(message = "error.message.number.and.special.character.notAllowed", regexp = "^[a-zA-Z\\s]+$")
    private String title;

    @Valid
    @NotEmpty(message = "Tags  can't be empty")
    private List<ClauseTagDTO> tags = new ArrayList<>();

    @NotBlank(message = "error.message.description.notNull.orEmpty")
    private String description;

    @Valid
    @NotNull(message = "ManagingOrganization  Type  can't be  null")
    @NotEmpty(message = "ManagingOrganization  Type  can't be  empty")
    private List<OrganizationType> organizationTypes;

    @Valid
    @NotNull(message = "ManagingOrganization Sub Type  can't be  null")
    @NotEmpty(message = "ManagingOrganization Sub Type  can't be  empty")
    private List<OrganizationSubType> organizationSubTypes;

    @Valid
    @NotNull(message = "Service Type  can't be  null")
    @NotEmpty(message = "Service  Type  can't be  empty")
    private List<ServiceCategory> organizationServices;

    @Valid
    @NotNull(message = "Service Sub Type  can't be  null")
    @NotEmpty(message = "Service Sub Type  can't be  empty")
    private List<SubServiceCategory> organizationSubServices;

    @NotNull(message = "Account Type cannot be null")
    @NotEmpty
    private Set<AccountTypeDTO> accountTypes;


    @NotNull(message = "Template Type cannot be null")
    @NotEmpty(message = "Template Type Can't be empty")
    private List<BigInteger> templateTypes;

    private List<Long> organizationList;

    public List<Long> getOrganizationList() {
        return organizationList;
    }

    public void setOrganizationList(List<Long> organizationList) {
        this.organizationList = organizationList;
    }

    public List<BigInteger> getTemplateTypes() {
        return templateTypes;
    }

    public void setTemplateTypes(List<BigInteger> templateTypes) {
        this.templateTypes = templateTypes;
    }

    public String getTitle() {
        return title.trim();
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

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationType> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<ServiceCategory> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public Set<AccountTypeDTO> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(Set<AccountTypeDTO> accountTypes) {
        this.accountTypes = accountTypes;
    }
}
