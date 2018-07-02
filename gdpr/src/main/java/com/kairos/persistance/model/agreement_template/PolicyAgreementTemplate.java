package com.kairos.persistance.model.agreement_template;


import com.kairos.dto.OrganizationTypeAndServiceBasicDTO;

import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

@Document(collection = "agreement_template")
public class PolicyAgreementTemplate extends MongoBaseEntity {

    @NotNullOrEmpty(message = "Name cannot be empty")
    private String name;

    @NotNullOrEmpty(message = "template id cannot be empty")
    private String templateId;

    @NotNullOrEmpty(message = "Description cannot be empty")
    private String description;

    @NotNull(message = "Account types cannot be empty ")
    private Set<BigInteger> accountTypes;

    @NotNull(message = "Sections cannot be emoty")
    private Set<BigInteger> agreementSections;

    private Long countryId;


    private List<OrganizationTypeAndServiceBasicDTO> organizationTypes;

    private List<OrganizationTypeAndServiceBasicDTO> organizationSubTypes;
    private List<OrganizationTypeAndServiceBasicDTO> organizationServices;
    private List<OrganizationTypeAndServiceBasicDTO> organizationSubServices;


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


    public Set<BigInteger> getAgreementSections() {
        return agreementSections;
    }

    public void setAgreementSections(Set<BigInteger> agreementSections) {
        this.agreementSections = agreementSections;
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

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public PolicyAgreementTemplate(Long countryId, String name, String description) {
        this.name = name;
        this.countryId = countryId;
        this.description = description;

    }

    public Set<BigInteger> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(Set<BigInteger> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public PolicyAgreementTemplate() {

    }


}
