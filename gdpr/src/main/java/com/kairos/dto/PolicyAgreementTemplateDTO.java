package com.kairos.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistance.model.agreement_template.AgreementSection;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyAgreementTemplateDTO {

    @NotNullOrEmpty(message = "error.agreement.name.cannot.be.empty.or.null")
    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotNullOrEmpty(message = "error.agreement.name.cannot.be.empty.or.null")
    private String description;

    @Pattern(message = "String and Special characters are not allowed",regexp ="[0-9]+")
    private String templateId;

    @NotNull(message = "Organization Type cannot be null")
    @NotEmpty(message = "Organization Type cannot be empty")
    private List<OrganizationTypeDTO>  organizationTypes;

    @NotNull(message = "Organization Sub Type cannot be null")
    @NotEmpty(message = "Organization Sub Type cannot be empty")
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

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private List<AgreementSection> agreementSections;


    @NotEmpty(message = "error.message.list.cannot.be.empty")
    public List<AgreementSection> getAgreementSections() {
        return agreementSections;
    }

    public void setAgreementSections(List<AgreementSection> agreementSections) {
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

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public PolicyAgreementTemplateDTO() {
    }
}
