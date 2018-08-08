package com.kairos.response.dto.policy_agreement;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.OrganizationSubTypeDTO;
import com.kairos.dto.OrganizationTypeDTO;
import com.kairos.dto.ServiceCategoryDTO;
import com.kairos.dto.SubServiceCategoryDTO;
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

    private List<OrganizationTypeDTO> organizationTypes;

    private List<OrganizationSubTypeDTO> organizationSubTypes;
    private List<ServiceCategoryDTO> organizationServices;
    private List<SubServiceCategoryDTO> organizationSubServices;

    private List<AgreementSectionResponseDTO> agreementSections=new ArrayList<>();

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

    public List<AgreementSectionResponseDTO> getAgreementSections() {
        return agreementSections;
    }

    public void setAgreementSections(List<AgreementSectionResponseDTO> agreementSections) {
        this.agreementSections = agreementSections;
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

    public PolicyAgreementTemplateResponseDTO()
    {

    }


}
