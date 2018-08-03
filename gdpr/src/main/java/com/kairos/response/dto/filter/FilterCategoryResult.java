package com.kairos.response.dto.filter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FilterCategoryResult {

    private List<FilterAttributes> organizationTypes = new ArrayList<>();
    private List<FilterAttributes> organizationSubTypes = new ArrayList<>();
    private List<FilterAttributes> organizationServices = new ArrayList<>();
    private List<FilterAttributes> organizationSubServices = new ArrayList<>();
    private List<FilterAttributes> accountTypes = new ArrayList<>();

    public List<FilterAttributes> getAccountTypes() {
        return accountTypes;
    }


    public void setAccountTypes(List<FilterAttributes> accountTypes) {
        this.accountTypes = accountTypes;
    }

    public List<FilterAttributes> getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(List<FilterAttributes> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public List<FilterAttributes> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(List<FilterAttributes> organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public List<FilterAttributes> getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(List<FilterAttributes> organizationServices) {
        this.organizationServices = organizationServices;
    }

    public List<FilterAttributes> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<FilterAttributes> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }

}
