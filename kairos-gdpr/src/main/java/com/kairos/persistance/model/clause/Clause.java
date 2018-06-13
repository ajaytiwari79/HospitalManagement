package com.kairos.persistance.model.clause;


import com.kairos.dto.OrganizationTypeAndServiceBasicDto;
import com.kairos.persistance.model.account_type.AccountType;
import com.kairos.persistance.model.clause_tag.ClauseTag;
import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.utils.custome_annotation.NotNullOrEmpty;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "clause")
public class Clause extends MongoBaseEntity {

    @NotNullOrEmpty
    private String title;
    @NotNull
    private List<ClauseTag> tags = new ArrayList<>();
    @NotNull
    private String description;


    private List<OrganizationTypeAndServiceBasicDto> organizationTypes;
    private List<OrganizationTypeAndServiceBasicDto> organizationSubTypes;
    private List<OrganizationTypeAndServiceBasicDto> organizationServices;
    private List<OrganizationTypeAndServiceBasicDto> organizationSubServices;
    private List<AccountType> accountTypes;

    private Long countryId;

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
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



    public List<OrganizationTypeAndServiceBasicDto> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<OrganizationTypeAndServiceBasicDto> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<OrganizationTypeAndServiceBasicDto> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<OrganizationTypeAndServiceBasicDto> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<OrganizationTypeAndServiceBasicDto> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<OrganizationTypeAndServiceBasicDto> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public List<AccountType> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(List<AccountType> accountTypes) {
        this.accountTypes = accountTypes;
    }
    public Clause(Long countryId,String title,String description) {
        this.countryId=countryId;
        this.title=title;
        this.description=description;
    }
    public Clause() {
    }
}
