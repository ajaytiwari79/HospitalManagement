package com.kairos.persistence.model.organization.services;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

@QueryResult
public class OrganizationServicesAndLevelQueryResult {
    private List<Long> servicesId;
    private Long levelId;

    public OrganizationServicesAndLevelQueryResult() {
        //dc
    }

    public List<Long> getServicesId() {
        return servicesId;
    }

    public void setServicesId(List<Long> servicesId) {
        this.servicesId = servicesId;
    }

    public Long getLevelId() {
        return levelId;
    }

    public void setLevelId(Long levelId) {
        this.levelId = levelId;
    }
}
