package com.kairos.persistence.model.clause;


import com.kairos.dto.gdpr.OrganizationSubType;
import com.kairos.dto.gdpr.OrganizationType;
import com.kairos.dto.gdpr.ServiceCategory;
import com.kairos.dto.gdpr.SubServiceCategory;
import com.kairos.dto.gdpr.master_data.AccountTypeVO;
import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.common.MongoBaseEntity;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Document
public class Clause extends MongoBaseEntity {

    @NotBlank
    private String title;
    @NotEmpty
    private List<ClauseTag> tags = new ArrayList<>();
    @NotNull
    private String description;
    private List<OrganizationType> organizationTypes;
    private List<OrganizationSubType> organizationSubTypes;
    private List<ServiceCategory> organizationServices;
    private List<SubServiceCategory> organizationSubServices;
    private List<AccountTypeVO> accountTypes;
    private Long countryId;
    private List<Long> organizationList;
    private BigInteger parentClauseId;
    private List<BigInteger> templateTypes;
    @Transient
    private Integer orderedIndex;
    @Transient
    private String titleHtml;
    @Transient
    private String descriptionHtml;



    public Clause(Long countryId, String title, String description) {
        this.countryId = countryId;
        this.title = title;
        this.description = description;
    }

    public Clause(@NotBlank String title, @NotNull String description) {
        this.title = title;
        this.description = description;
    }

    public Clause(@NotBlank String title, @NotNull String description, @NotEmpty List<ClauseTag> tags) {
        this.title = title;
        this.description = description;
        this.tags=tags;
    }

    public Clause(@NotBlank String title, @NotNull String description, @NotEmpty List<ClauseTag> tags,List<BigInteger> templateTypes) {
        this.title = title;
        this.description = description;
        this.tags=tags;
        this.templateTypes=templateTypes;
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

    public List<AccountTypeVO> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(List<AccountTypeVO> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public Integer getOrderedIndex() {
        return orderedIndex;
    }

    public void setOrderedIndex(Integer orderedIndex) {
        this.orderedIndex = orderedIndex;
    }

    public String getTitleHtml() {
        return titleHtml;
    }

    public void setTitleHtml(String titleHtml) {
        this.titleHtml = titleHtml;
    }

    public String getDescriptionHtml() {
        return descriptionHtml;
    }

    public void setDescriptionHtml(String descriptionHtml) {
        this.descriptionHtml = descriptionHtml;
    }


}
