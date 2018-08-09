package com.kairos.response.dto.clause;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.gdpr.OrganizationSubType;
import com.kairos.gdpr.OrganizationType;
import com.kairos.gdpr.ServiceCategory;
import com.kairos.gdpr.SubServiceCategory;
import com.kairos.response.dto.master_data.AccountTypeResponseDTO;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.response.dto.master_data.TemplateTypeResponseDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ClauseResponseDTO {

    @NotNull
    private BigInteger id;
    @NotBlank
    private String title;
    @NotNull
    private List<ClauseTag> tags = new ArrayList<>();
    @NotBlank
    private String description;

    private List<TemplateTypeResponseDTO> templateTypes;

    private List<OrganizationType> organizationTypes;

    private List<OrganizationSubType> organizationSubTypes;

    private List<ServiceCategory> organizationServices;

    private List<SubServiceCategory> organizationSubServices;

    private List<AccountTypeResponseDTO> accountTypes;

    public List<AccountTypeResponseDTO> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(List<AccountTypeResponseDTO> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public List<TemplateTypeResponseDTO> getTemplateTypes() {
        return templateTypes;
    }

    public void setTemplateTypes(List<TemplateTypeResponseDTO> templateTypes) {
        this.templateTypes = templateTypes;
    }

    public String getTitle() {
        return title;
    }

    public BigInteger getId() {
        return id;
    }

    public void setId(BigInteger id) {
        this.id = id;
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
}
