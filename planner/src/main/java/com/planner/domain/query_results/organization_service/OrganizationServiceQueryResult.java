package com.planner.domain.query_results.organization_service;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class OrganizationServiceQueryResult {

    private Long id;
    private String name;
    private List<OrganizationServiceQueryResult> organizationSubServices;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<OrganizationServiceQueryResult> getOrganizationSubServices() {
        return organizationSubServices;
    }

    public void setOrganizationSubServices(List<OrganizationServiceQueryResult> organizationSubServices) {
        this.organizationSubServices = organizationSubServices;
    }
}
