package com.kairos.persistance.model.clause;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.gdpr.OrganizationSubType;
import com.kairos.gdpr.OrganizationType;
import com.kairos.gdpr.ServiceCategory;
import com.kairos.gdpr.SubServiceCategory;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.user.country.system_setting.AccountTypeDTO;
import org.javers.core.metamodel.annotation.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@JsonIgnoreProperties(ignoreUnknown = true)
@TypeName("clause")
public class Clause extends MongoBaseEntity {

    @NotBlank
    private String title;
    @NotNull
    private List<ClauseTag> tags = new ArrayList<>();
    @NotNull
    private String description;

    private List<OrganizationType> organizationTypes;

    private List<OrganizationSubType> organizationSubTypes;

    private List<ServiceCategory> organizationServices;

    private List<SubServiceCategory> organizationSubServices;

    private Set<AccountTypeDTO> accountTypes;

    private Long countryId;

    private Boolean isDefault = true;

    private List<Long> organizationList;

    private BigInteger parentClauseId;

    private List<BigInteger> templateTypes;

    private Integer orderedIndex;

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

    public Set<AccountTypeDTO> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(Set<AccountTypeDTO> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public Integer getOrderedIndex() { return orderedIndex; }

    public void setOrderedIndex(Integer orderedIndex) { this.orderedIndex = orderedIndex; }

    public Clause(Long countryId, String title, String description) {
        this.countryId = countryId;
        this.title = title;
        this.description = description;
    }


    public Clause(String title, String description, Long countryId, List<OrganizationType> organizationTypes, List<OrganizationSubType> organizationSubTypes, List<ServiceCategory> organizationServices, List<SubServiceCategory> organizationSubServices) {
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
