package com.kairos.persistance.model.clause.dto;

import java.util.List;

public class ClauseGetQueryDto {

    private List<String> accountTypes;
    private List<String> organizationTypes;

    private List<String>  organizationServices;
    private List<String>  organizationSubServices;

    private List<String> tags;

    public List<String> getTags() {
        return tags;
    }

    public List<String> getAccountTypes() {
        return accountTypes;
    }

    public List<String> getOrganizationTypes() {
        return organizationTypes;
    }

    public List<String> getOrganizationServices() {
        return organizationServices;
    }

    public List<String> getOrganizationSubServices() {
        return organizationSubServices;
    }
}
