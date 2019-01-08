package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterClauseDTO extends ClauseDTO{

    @Valid
    @NotEmpty(message = "error.message.organizationType.not.Selected")
    private List<OrganizationTypeDTO> organizationTypeDTOS =new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.organizationSubType.not.Selected")
    private List<OrganizationSubTypeDTO> organizationSubTypeDTOS =new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.serviceCategory.not.Selected")
    private List<ServiceCategoryDTO> organizationServices=new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.serviceSubCategory.not.Selected")
    private List<SubServiceCategoryDTO> organizationSubServices=new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.accountType.not.Selected")
    private List<AccountTypeVO> accountTypes=new ArrayList<>();

    public List<Long> getOrganizationList() { return organizationList; }

    public void setOrganizationList(List<Long> organizationList) { this.organizationList = organizationList; }

    private List<Long> organizationList;

    public List<OrganizationTypeDTO> getOrganizationTypes() {
        return organizationTypeDTOS;
    }

    public void setOrganizationTypes(List<OrganizationTypeDTO> organizationTypeDTOS) { this.organizationTypeDTOS = organizationTypeDTOS; }

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
