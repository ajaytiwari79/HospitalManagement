package com.kairos.persistance.model.clause.dto;

import java.math.BigInteger;
import java.util.List;

public class ClauseGetQueryDto {

    private List<String> accountTypes;
    private List<String> tags;
    private List<Long> organizationTypes;
    private List<Long>  organizationSubTypes;

    private List<Long>  organizationServices;
    private List<Long>  organizationSubServices;



    public List<String> getTags() {
        return tags;
    }

    public List<String> getAccountTypes() {
        return accountTypes;
    }

    public List<Long> getOrganizationTypes() {
        return organizationTypes;
    }

    public List<Long> getOrganizationSubTypes() {
        return organizationSubTypes;
    }

    public List<Long> getOrganizationServices() {
        return organizationServices;
    }

    public List<Long> getOrganizationSubServices() {
        return organizationSubServices;
    }
}
