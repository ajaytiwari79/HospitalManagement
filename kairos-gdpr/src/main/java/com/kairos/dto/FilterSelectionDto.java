package com.kairos.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FilterSelectionDto {

    private List<String> searchTags;

    private FilterSelection organizationTypes;
    private FilterSelection organizationSubTypes;
    private FilterSelection organizationServices;
    private FilterSelection organizationSubServices;


    public List<String> getSearchTags() {
        return searchTags;
    }

    public void setSearchTags(List<String> searchTags) {
        this.searchTags = searchTags;
    }

    public FilterSelection getOrganizationTypes() {
        return organizationTypes;
    }

    public void setOrganizationTypes(FilterSelection organizationTypes) {
        this.organizationTypes = organizationTypes;
    }

    public FilterSelection getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public void setOrganizationSubTypes(FilterSelection organizationSubTypes) {
        this.organizationSubTypes = organizationSubTypes;
    }

    public FilterSelection getOrganizationServices() {
        return organizationServices;
    }

    public void setOrganizationServices(FilterSelection organizationServices) {
        this.organizationServices = organizationServices;
    }

    public FilterSelection getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(FilterSelection organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }
}
