package com.kairos.dto.gdpr.agreement_template;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;
import com.kairos.dto.gdpr.master_data.AccountTypeVO;
import com.kairos.dto.user.country.system_setting.AccountTypeDTO;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MasterAgreementTemplateDTO extends AgreementTemplateDTO{

    @Valid
    @NotEmpty(message = "error.message.organizationType.not.Selected")
    private List<OrganizationType> organizationTypes = new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.organizationSubType.not.Selected")
    private List<OrganizationSubType> organizationSubTypes = new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.serviceCategory.not.Selected")
    private List<ServiceCategory> organizationServices = new ArrayList<>();

    @Valid
    @NotEmpty(message = "error.message.serviceSubCategory.not.Selected")
    private List<SubServiceCategory> organizationSubServices = new ArrayList<>();


    @Valid
    @NotEmpty(message = "error.message.accountType.not.Selected")
    private List<AccountTypeVO> accountTypes=new ArrayList<>();

    public List<OrganizationType> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationType> organizationTypes) { this.organizationTypes = organizationTypes; }

    public List<OrganizationSubType> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationSubType> organizationSubTypes) { this.organizationSubTypes = organizationSubTypes; }

    public List<ServiceCategory> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<ServiceCategory> organizationServices) { this.organizationServices = organizationServices; }

    public List<SubServiceCategory> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<SubServiceCategory> organizationSubServices) { this.organizationSubServices = organizationSubServices; }

    public List<AccountTypeVO> getAccountTypes() { return accountTypes; }

    public void setAccountTypes(List<AccountTypeVO> accountTypes) { this.accountTypes = accountTypes; }
}
