package com.kairos.persistance.model.clause;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.dto.OrganizationSubTypeDTO;
import com.kairos.dto.OrganizationTypeDTO;
import com.kairos.dto.ServiceCategoryDTO;
import com.kairos.dto.SubServiceCategoryDTO;
import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.persistance.model.common.MongoBaseEntity;
import org.javers.core.metamodel.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
@TypeName("clause")
public class Clause extends MongoBaseEntity {

    @NotBlank
    private String title;
    @NotNull
    private List<ClauseTag> tags = new ArrayList<>();
    @NotNull
    private String description;

    private List<OrganizationTypeDTO> organizationTypes;

    private List<OrganizationSubTypeDTO> organizationSubTypes;

    private List<ServiceCategoryDTO> organizationServices;

    private List<SubServiceCategoryDTO> organizationSubServices;

    private List<AccountType> accountTypes;

    private Long countryId;

    private Boolean isDefault = true;

    private List<Long> organizationList;

    private BigInteger parentClauseId;

    private List<BigInteger> templateTypes;


    public List<Long> getOrganizationList() {
        return organizationList;
    }

    public void setOrganizationList(List<Long> organizationList) {
        this.organizationList = organizationList;
    }

    public List<BigInteger> getTemplateTypes() {
        return templateTypes;
    }

    public void setTemplateTypes(List<BigInteger> templateTypes) {
        this.templateTypes = templateTypes;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public BigInteger getParentClauseId() {
        return parentClauseId;
    }

    public void setParentClauseId(BigInteger parentClauseId) {
        this.parentClauseId = parentClauseId;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ClauseTag> getTags() {
        return tags;
    }

    public void setTags(List<ClauseTag> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public List<AccountType> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(List<AccountType> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public Clause(Long countryId, String title, String description) {
        this.countryId = countryId;
        this.title = title;
        this.description = description;
    }


    public Clause(String title,String description, Long countryId, List<OrganizationTypeDTO> organizationTypes, List<OrganizationSubTypeDTO> organizationSubTypes, List<ServiceCategoryDTO> organizationServices, List<SubServiceCategoryDTO> organizationSubServices) {
        this.title = title;
        this.description = description;
        this.organizationTypes = organizationTypes;
        this.organizationSubTypes = organizationSubTypes;
        this.organizationServices = organizationServices;
        this.organizationSubServices = organizationSubServices;
        this.countryId = countryId;
    }

    public Clause() {
    }
}
