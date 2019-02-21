package com.kairos.persistence.model.clause;


import com.kairos.persistence.model.clause_tag.ClauseTag;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.*;
import com.kairos.persistence.model.template_type.TemplateType;
import org.springframework.data.annotation.Transient;

import javax.annotation.Nullable;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
public class Clause extends BaseEntity {

    @NotBlank
    private String title;

    @OneToMany
    private List<ClauseTag> tags  = new ArrayList<>();

    @NotNull
    private String description;

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
    private Long countryId;
    private Long organizationId;

    //TODO
    //private List<Long> organizationList;
    private Long parentClauseId;

    @OneToMany
    private List<TemplateType> templateTypes  = new ArrayList<>();

    @Transient
    private Integer orderedIndex;
    @Transient
    private String titleHtml;
    @Transient
    private String descriptionHtml;

    @Nullable
    private UUID tempClauseId;



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

    public Clause(@NotBlank String title, @NotNull String description, @NotEmpty List<ClauseTag> tags, List<TemplateType> templateTypes) {
        this.title = title;
        this.description = description;
        this.tags=tags;
        this.templateTypes=templateTypes;
    }

    public Clause(String title, String description, Long countryId) {
        this.title = title;
        this.description = description;
        this.countryId = countryId;
    }

    public Clause() {
    }

   /* public List<Long> getOrganizationList() {
        return organizationList;
    }

    public void setOrganizationList(List<Long> organizationList) {
        this.organizationList = organizationList;
    }*/

    public List<TemplateType> getTemplateTypes() {
        return templateTypes;
    }

    public void setTemplateTypes(List<TemplateType> templateTypes) {
        this.templateTypes = templateTypes;
    }

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getParentClauseId() {
        return parentClauseId;
    }

    public void setParentClauseId(Long parentClauseId) {
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

    public List<AccountType> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(List<AccountType> accountTypes) {
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

    public UUID getTempClauseId() {
        return tempClauseId;
    }

    public void setTempClauseId(UUID tempClauseId) {
        this.tempClauseId = tempClauseId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }


}
