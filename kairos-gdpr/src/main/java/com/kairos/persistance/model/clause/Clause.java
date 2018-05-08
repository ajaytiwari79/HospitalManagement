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


    private List<OrganizationService> organizationServiceList;

    private List<OrganizationType> organizationTypeList;

    private List<AccountType> accountTypeList;


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

    public List<OrganizationService> getOrganizationServiceList() {
        return organizationServiceList;
    }

    public void setOrganizationServiceList(List<OrganizationService> organizationServiceList) {
        this.organizationServiceList = organizationServiceList;
    }

    public List<OrganizationType> getOrganizationTypeList() {
        return organizationTypeList;
    }

    public void setOrganizationTypeList(List<OrganizationType> organizationTypeList) {
        this.organizationTypeList = organizationTypeList;
    }

    public List<AccountType> getAccountTypeList() {
        return accountTypeList;
    }

    public void setAccountTypeList(List<AccountType> accountTypeList) {
        this.accountTypeList = accountTypeList;
    }

    public Clause() {

    }
    public Clause(String title,String description,List<String> tags,List<AccountType> accountTypeList,List<OrganizationService> organizationServiceList
    ,List<OrganizationType> organizationTypeList) {
        this.title=title;
        this.description=description;
        this.tags=tags;
        this.accountTypeList=accountTypeList;
        this.organizationServiceList=organizationServiceList;
        this.organizationTypeList=organizationTypeList;

    }


}
