package com.kairos.response.dto.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyAgreementTemplateResponseDto {

    @NotNullOrEmpty(message = "error.agreement.name.cannotbe.empty.or.null")
    private String name;

    @NotNullOrEmpty(message = "error.agreement.name.cannotbe.empty.or.null")
    private String description;

    private Set<AccountType> accountTypes;

    private Set<AgreementSectionResponseDto> agreementSections;

    private Long countryId;
    private List<OrganizationTypeAndServiceBasicDto> organizationTypes;

    private List<OrganizationTypeAndServiceBasicDto> organizationSubTypes;
    private List<OrganizationTypeAndServiceBasicDto> organizationServices;
    private List<OrganizationTypeAndServiceBasicDto> organizationSubServices;


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



    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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

    public PolicyAgreementTemplateResponseDto()
    {

    }


}
