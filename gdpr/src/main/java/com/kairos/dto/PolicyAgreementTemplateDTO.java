package com.kairos.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.master_data.AgreementSectionDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyAgreementTemplateDTO {

    @NotBlank(message = "error.agreement.name.cannot.be.empty.or.null")
    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotBlank(message = "error.agreement.name.cannot.be.empty.or.null")
    private String description;

    @NotNull(message = "ManagingOrganization Type cannot be null")
    @NotEmpty(message = "ManagingOrganization Type cannot be empty")
    private List<OrganizationTypeDTO>  organizationTypes;

    @NotNull(message = "ManagingOrganization Sub Type cannot be null")
    @NotEmpty(message = "ManagingOrganization Sub Type cannot be empty")
    private List<OrganizationSubTypeDTO>  organizationSubTypes;

    @NotNull(message = "Service Type cannot be null")
    @NotEmpty(message = "Service Type cannot be empty")
    private List<ServiceCategoryDTO>  organizationServices;

    @NotNull(message = "Service Sub Type cannot be null")
    @NotEmpty(message = "Service Sub Type cannot be empty")
    private List<SubServiceCategoryDTO>  organizationSubServices;

    @NotNull(message = "Account Type cannot be null")
    @NotEmpty(message = "Account Type cannot be empty")
    private Set<BigInteger> accountTypes;

    private List<AgreementSectionDTO> agreementSections=new ArrayList<>();

    @NotNull
    private BigInteger templateTypeId;

    public BigInteger getTemplateTypeId() {
        return templateTypeId;
    }

    public void setTemplateTypeId(BigInteger templateTypeId) {
        this.templateTypeId = templateTypeId;
    }

    public List<AgreementSectionDTO> getAgreementSections() {
        return agreementSections;
    }

    public void setAgreementSections(List<AgreementSectionDTO> agreementSections) {
        this.agreementSections = agreementSections;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<OrganizationTypeDTO> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeDTO> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubTypeDTO> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<ServiceCategoryDTO> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<ServiceCategoryDTO> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<SubServiceCategoryDTO> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategoryDTO> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public Set<BigInteger> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(Set<BigInteger> accountTypes) {
        this.accountTypes = accountTypes;
    }


}
