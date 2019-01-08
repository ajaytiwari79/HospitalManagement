package com.kairos.response.dto.policy_agreement;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
import com.kairos.dto.gdpr.master_data.AccountTypeVO;
import com.kairos.response.dto.master_data.TemplateTypeResponseDTO;

import java.math.BigInteger;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyAgreementTemplateResponseDTO {

    private BigInteger id;
    private String name;
    private String description;
    private List<AccountTypeVO> accountTypes;
    private List<OrganizationTypeDTO> organizationTypeDTOS;
    private List<OrganizationSubTypeDTO> organizationSubTypeDTOS;
    private List<ServiceCategoryDTO> organizationServices;
    private List<SubServiceCategoryDTO> organizationSubServices;
    private List<BigInteger> sections;

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

    public List<AccountTypeVO> getAccountTypes() { return accountTypes; }

    public void setAccountTypes(List<AccountTypeVO> accountTypes) { this.accountTypes = accountTypes; }

    public List<BigInteger> getSections() { return sections; }

    public void setSections(List<BigInteger> sections) { this.sections = sections; }

    public List<OrganizationTypeDTO> getOrganizationTypeDTOS() {
        return organizationTypeDTOS;
    }

    public void setOrganizationTypeDTOS(List<OrganizationTypeDTO> organizationTypeDTOS) {
        this.organizationTypeDTOS = organizationTypeDTOS;
    }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypeDTOS() {
        return organizationSubTypeDTOS;
    }

    public void setOrganizationSubTypeDTOS(List<OrganizationSubTypeDTO> organizationSubTypeDTOS) {
        this.organizationSubTypeDTOS = organizationSubTypeDTOS;
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
