package com.kairos.dto.gdpr.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;
import com.kairos.dto.gdpr.master_data.AccountTypeVO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterAgreementTemplateDTO extends AgreementTemplateDTO{

    @Valid
    @NotEmpty(message = "error.message.organizationType.not.Selected")
    private List<OrganizationTypeDTO> organizationTypeDTOS = new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.organizationSubType.not.Selected")
    private List<OrganizationSubTypeDTO> organizationSubTypeDTOS = new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.serviceCategory.not.Selected")
    private List<ServiceCategoryDTO> organizationServices = new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.serviceSubCategory.not.Selected")
    private List<SubServiceCategoryDTO> organizationSubServices = new ArrayList<>();


    @Valid
    @NotEmpty(message = "error.message.accountType.not.Selected")
    private List<AccountTypeVO> accountTypes=new ArrayList<>();

    public List<OrganizationTypeDTO> getOrganizationTypeDTOS() {
        return organizationTypeDTOS;
    }

    public void setOrganizationTypeDTOS(List<OrganizationTypeDTO> organizationTypeDTOS) { this.organizationTypeDTOS = organizationTypeDTOS; }

    public List<OrganizationSubTypeDTO> getOrganizationSubTypeDTOS() {
        return organizationSubTypeDTOS;
    }

    public void setOrganizationSubTypeDTOS(List<OrganizationSubTypeDTO> organizationSubTypeDTOS) { this.organizationSubTypeDTOS = organizationSubTypeDTOS; }

    public List<ServiceCategoryDTO> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<ServiceCategoryDTO> organizationServices) { this.organizationServices = organizationServices; }

    public List<SubServiceCategoryDTO> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategoryDTO> organizationSubServices) { this.organizationSubServices = organizationSubServices; }

    public List<AccountTypeVO> getAccountTypes() { return accountTypes; }

    public void setAccountTypes(List<AccountTypeVO> accountTypes) { this.accountTypes = accountTypes; }
}
