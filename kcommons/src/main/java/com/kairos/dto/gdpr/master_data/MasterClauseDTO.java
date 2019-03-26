package com.kairos.dto.gdpr.master_data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.gdpr.OrganizationSubTypeDTO;
import com.kairos.dto.gdpr.OrganizationTypeDTO;
import com.kairos.dto.gdpr.ServiceCategoryDTO;
import com.kairos.dto.gdpr.SubServiceCategoryDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterClauseDTO extends ClauseDTO{

    @Valid
    @NotEmpty(message = "error.message.organizationType.not.Selected")
    private Set<OrganizationTypeDTO> organizationTypes =new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.organizationSubType.not.Selected")
    private Set<OrganizationSubTypeDTO> organizationSubTypes =new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.serviceCategory.not.Selected")
    private Set<ServiceCategoryDTO> organizationServices=new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.serviceSubCategory.not.Selected")
    private Set<SubServiceCategoryDTO> organizationSubServices=new HashSet<>();

    @Valid
    @NotEmpty(message = "error.message.accountType.not.Selected")
    private List<AccountTypeVO> accountTypes=new ArrayList<>();

    public List<Long> getOrganizationList() { return organizationList; }

    public void setOrganizationList(List<Long> organizationList) { this.organizationList = organizationList; }

    private List<Long> organizationList;

    public Set<OrganizationTypeDTO> getOrganizationTypes() { return organizationTypes; }

    public void setOrganizationTypes(Set<OrganizationTypeDTO> organizationTypes) { this.organizationTypes = organizationTypes; }

    public Set<OrganizationSubTypeDTO> getOrganizationSubTypes() { return organizationSubTypes; }

    public void setOrganizationSubTypes(Set<OrganizationSubTypeDTO> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes; }

    public Set<ServiceCategoryDTO> getOrganizationServices() { return organizationServices; }

    public void setOrganizationServices(Set<ServiceCategoryDTO> organizationServices) { this.organizationServices = organizationServices; }

    public Set<SubServiceCategoryDTO> getOrganizationSubServices() { return organizationSubServices; }

    public void setOrganizationSubServices(Set<SubServiceCategoryDTO> organizationSubServices) { this.organizationSubServices = organizationSubServices; }

    public List<AccountTypeVO> getAccountTypes() { return accountTypes; }
    public void setAccountTypes(List<AccountTypeVO> accountTypes) { this.accountTypes = accountTypes; }
}
