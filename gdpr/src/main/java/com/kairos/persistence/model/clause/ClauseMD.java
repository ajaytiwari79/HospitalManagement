package com.kairos.persistence.model.clause;


import com.kairos.persistence.model.clause_tag.ClauseTagMD;
import com.kairos.persistence.model.common.BaseEntity;
import com.kairos.persistence.model.embeddables.*;
import com.kairos.persistence.model.template_type.TemplateTypeMD;
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
public class ClauseMD extends BaseEntity {

    @NotBlank
    private String title;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    private List<ClauseTagMD> tags  = new ArrayList<>();

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

    //TODO
    //private List<Long> organizationList;
    private Long parentClauseId;

    @OneToMany
    private List<TemplateTypeMD> templateTypes  = new ArrayList<>();

    @Transient
    private Integer orderedIndex;
    @Transient
    private String titleHtml;
    @Transient
    private String descriptionHtml;

    @Nullable
    private UUID tempClauseId;



    public ClauseMD(Long countryId, String title, String description) {
        this.countryId = countryId;
        this.title = title;
        this.description = description;
    }

    public ClauseMD(@NotBlank String title, @NotNull String description) {
        this.title = title;
        this.description = description;
    }

    public ClauseMD(@NotBlank String title, @NotNull String description, @NotEmpty List<ClauseTagMD> tags) {
        this.title = title;
        this.description = description;
        this.tags=tags;
    }

    public ClauseMD(@NotBlank String title, @NotNull String description, @NotEmpty List<ClauseTagMD> tags, List<TemplateTypeMD> templateTypes) {
        this.title = title;
        this.description = description;
        this.tags=tags;
        this.templateTypes=templateTypes;
    }

    public ClauseMD(String title, String description, Long countryId) {
        this.title = title;
        this.description = description;
        this.countryId = countryId;
    }

    public ClauseMD() {
    }

   /* public List<Long> getOrganizationList() {
        return organizationList;
    }

    public void setOrganizationList(List<Long> organizationList) {
        this.organizationList = organizationList;
    }*/

    public List<TemplateTypeMD> getTemplateTypes() {
        return templateTypes;
    }

    public void setTemplateTypes(List<TemplateTypeMD> templateTypes) {
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

    public List<ClauseTagMD> getTags() {
        return tags;
    }

    public void setTags(List<ClauseTagMD> tags) {
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
}
