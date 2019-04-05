package com.kairos.persistence.model.clause;

import com.kairos.annotations.PermissionModel;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.embeddables.*;
import com.kairos.persistence.model.template_type.TemplateType;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@PermissionModel
@Entity
public class MasterClause extends Clause {

    private Long countryId;

    @ElementCollection
    private List<OrganizationType> organizationTypes = new ArrayList<>();

    @ElementCollection
    private List <OrganizationSubType> organizationSubTypes = new ArrayList<>();

    @ElementCollection
    private List <ServiceCategory> organizationServices = new ArrayList<>();

    @ElementCollection
    private List <SubServiceCategory> organizationSubServices = new ArrayList<>();

    @ElementCollection
    private List<AccountType> accountTypes = new ArrayList<>();


    public MasterClause(@NotBlank String title, @NotNull String description, @NotEmpty List<ClauseTag> tags, List<TemplateType> templateTypes, Long countryId, UUID tempClauseId,List<AccountType> accountTypes
                          ,List<OrganizationType> organizationTypes,List <OrganizationSubType> organizationSubTypes ,List <ServiceCategory> organizationServices,List<SubServiceCategory> organizationSubServices ) {
        super(title, description, tags, templateTypes,tempClauseId);
        this.countryId = countryId;
        this.accountTypes=accountTypes;
        this.organizationTypes=organizationTypes;
        this.organizationServices=organizationServices;
        this.organizationSubTypes=organizationSubTypes;
        this.organizationSubServices=organizationSubServices;
    }

    public MasterClause(){

    }

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

    public List<AccountType> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(List<AccountType> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

}
