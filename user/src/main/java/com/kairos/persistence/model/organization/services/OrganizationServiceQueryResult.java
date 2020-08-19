package com.kairos.persistence.model.organization.services;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by prerna on 16/11/17.
 */
@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class OrganizationServiceQueryResult {
    private Long id;
    private String name;
    private String customName;
    private String description;
    private List<OrganizationServiceQueryResult> organizationSubServices;
}
