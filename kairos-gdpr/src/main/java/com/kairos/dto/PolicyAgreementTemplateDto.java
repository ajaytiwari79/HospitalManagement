package com.kairos.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyAgreementTemplateDto {

    @NotNullOrEmpty(message = "error.agreement.name.cannotbe.empty.or.null")
    private String name;

    @NotNullOrEmpty(message = "error.agreement.name.cannotbe.empty.or.null")
    private String description;


    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set<Long> organizationTypes;


    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set<Long> organizationSubTypes;


    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set<Long> organizationServices;


    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set<Long> organizationSubServices;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set<BigInteger> accountTypes;

    @NotEmpty(message = "error.message.list.cannot.be.empty")
    private Set<BigInteger> agreementSections;

    private Long countryId;

    public Set<BigInteger> getAgreementSections() {
        return agreementSections;
    }

    public void setAgreementSections(Set<BigInteger> agreementSections) {
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

    public Set<Long> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(Set<Long> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public Set<Long> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(Set<Long> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public Set<Long> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(Set<Long> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public Set<Long> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(Set<Long> organizationSubServices) {
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
