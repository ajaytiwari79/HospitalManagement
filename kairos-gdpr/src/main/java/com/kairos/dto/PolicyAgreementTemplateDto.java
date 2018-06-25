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
public class PolicyAgreementTemplateDto {

    @NotNullOrEmpty(message = "error.agreement.name.cannot.be.empty.or.null")
    @Pattern(regexp = "^[a-zA-Z\\s]+$")
    private String name;

    @NotNullOrEmpty(message = "error.agreement.name.cannot.be.empty.or.null")
    private String description;

    @NotNull(message = "Organization Type cannot be null")
    @NotEmpty(message = "Organization Type cannot be empty")
    private List<OrganizationTypeAndServiceBasicDto>  organizationTypes;

    @NotNull(message = "Organization Sub Type cannot be null")
    @NotEmpty(message = "Organization Sub Type cannot be empty")
    private List<OrganizationTypeAndServiceBasicDto>  organizationSubTypes;

    @NotNull(message = "Service Type cannot be null")
    @NotEmpty(message = "Service Type cannot be empty")
    private List<OrganizationTypeAndServiceBasicDto>  organizationServices;

    @NotNull(message = "Service Sub Type cannot be null")
    @NotEmpty(message = "Service Sub Type cannot be empty")
    private List<OrganizationTypeAndServiceBasicDto>  organizationSubServices;

    @NotNull(message = "Account Type cannot be null")
    @NotEmpty(message = "Account Type cannot be empty")
    private Set<BigInteger> accountTypes;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private List<AgreementSection> agreementSections;

    private Long countryId;

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

    public Set<BigInteger> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(Set<BigInteger> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public PolicyAgreementTemplateDto() {
    }
}
