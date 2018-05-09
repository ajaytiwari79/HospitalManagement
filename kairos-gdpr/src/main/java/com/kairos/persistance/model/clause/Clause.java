package com.kairos.persistance.model.clause;


import com.kairos.persistance.model.common.MongoBaseEntity;
import com.kairos.persistance.model.organization.OrganizationService;
import com.kairos.persistance.model.organization.OrganizationType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


@Document(collection = "clause")
public class Clause extends MongoBaseEntity {

    @NotEmpty(message = "error.clause.title.cannotbe.empty")
    @NotNull(message = "error.clause.title.cannotbe.null")
    private String title;

    private List<String> tags = new ArrayList<>();

    @NotEmpty(message = "error.clause.title.cannotbe.empty")
    @NotNull(message = "error.clause.title.cannotbe.null")
    private String description;


    private List<Long> organizationServices;
    private List<Long> organizationSubServices;

    private List<Long> organizationTypes;
    private List<Long> organizationSubTypes;


    private List<AccountType> accountTypes;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

     public Clause() {

    }
    public Clause(String title,String description,List<String> tags,List<AccountType> accountTypes,List<Long> organizationServices
    ,List<Long> organizationSubServices,List<Long> organizationTypes,List<Long> organizationSubTypes) {
        this.title=title;
        this.description=description;
        this.tags=tags;
        this.accountTypes=accountTypes;
        this.organizationServices=organizationServices;
        this.organizationTypes=organizationTypes;
        this.organizationSubServices=organizationSubServices;
this.organizationSubTypes=organizationSubTypes;

    }

    public List<Long> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<Long> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<Long> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<Long> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

    public List<Long> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<Long> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<Long> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<Long> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<AccountType> getAccountTypes() {
        return accountTypes;
    }

    public void setAccountTypes(List<AccountType> accountTypes) {
        this.accountTypes = accountTypes;
    }
}
