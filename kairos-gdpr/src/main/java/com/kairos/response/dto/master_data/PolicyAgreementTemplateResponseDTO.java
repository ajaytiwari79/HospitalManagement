package com.kairos.response.dto.master_data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.OrganizationTypeAndServiceBasicDTO;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyAgreementTemplateResponseDTO {

    private BigInteger id;

    @NotNullOrEmpty(message = "error.agreement.name.cannotbe.empty.or.null")
    private String name;

    @NotNullOrEmpty(message = "error.agreement.name.cannotbe.empty.or.null")
    private String description;

    private List<AccountTypeResponseDTO> accountTypes;

    private List<AgreementSectionResponseDTO> agreementSections;

    private Long countryId;
    private List<OrganizationTypeAndServiceBasicDTO> organizationTypes;

    private List<OrganizationTypeAndServiceBasicDTO> organizationSubTypes;
    private List<OrganizationTypeAndServiceBasicDTO> organizationServices;
    private List<OrganizationTypeAndServiceBasicDTO> organizationSubServices;

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
    }

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

    public List<AccountTypeResponseDTO> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(List<AccountTypeResponseDTO> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public List<AgreementSectionResponseDTO> getAgreementSections() {
        return agreementSections;
    }

    public void setAgreementSections(List<AgreementSectionResponseDTO> agreementSections) {
        this.agreementSections = agreementSections;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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

    public PolicyAgreementTemplateResponseDTO()
    {

    }


}
