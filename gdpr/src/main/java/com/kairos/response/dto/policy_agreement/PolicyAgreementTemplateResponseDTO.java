package com.kairos.response.dto.policy_agreement;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.gdpr.OrganizationSubType;
import com.kairos.gdpr.OrganizationType;
import com.kairos.gdpr.ServiceCategory;
import com.kairos.gdpr.SubServiceCategory;
import com.kairos.response.dto.master_data.AccountTypeResponseDTO;
import com.kairos.response.dto.master_data.TemplateTypeResponseDTO;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyAgreementTemplateResponseDTO {

    private BigInteger id;

    private String name;

    private String description;

    private List<AccountTypeResponseDTO> accountTypes;

    private List<OrganizationType> organizationTypes;

    private List<OrganizationSubType> organizationSubTypes;
    private List<ServiceCategory> organizationServices;
    private List<SubServiceCategory> organizationSubServices;

    private List<BigInteger> agreementSections=new ArrayList<>();

    private TemplateTypeResponseDTO templateType;

    public TemplateTypeResponseDTO getTemplateType() {
        return templateType;
    }

    public void setTemplateType(TemplateTypeResponseDTO templateType) {
        this.templateType = templateType;
    }

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

    public List<BigInteger> getAgreementSections() { return agreementSections; }

    public void setAgreementSections(List<BigInteger> agreementSections) { this.agreementSections = agreementSections; }

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

    public PolicyAgreementTemplateResponseDTO()
    {

    }


}
