package com.kairos.persistance.model.agreement_template;


import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.persistance.country.Country;

import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Document(collection = "agreement_template")
public class PolicyAgreementTemplate extends MongoBaseEntity {

    @NotNullOrEmpty(message = "error.agreement.name.cannotbe.empty.or.null")
    private String name;

    @NotNullOrEmpty(message = "error.agreement.name.cannotbe.empty.or.null")
    private String description;

    private Set<BigInteger> accountTypes;

    private List<BigInteger> agreementSections;

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

    public Set<BigInteger> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(Set<BigInteger> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public List<BigInteger> getAgreementSections() {
        return agreementSections;
    }

    public void setAgreementSections(List<BigInteger> agreementSections) {
        this.agreementSections = agreementSections;
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

    public PolicyAgreementTemplate(Long countryId,String name, String description) {
        this.name = name;
        this.countryId = countryId;
        this.description = description;

    }

    public PolicyAgreementTemplate() {

    }


}
