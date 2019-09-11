package com.kairos.persistence.model.organization.services;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Set;

@QueryResult
@Getter
@Setter
public class OrganizationServicesAndLevelQueryResult {
    private Set<Long> servicesId;
    private Long levelId;
}
