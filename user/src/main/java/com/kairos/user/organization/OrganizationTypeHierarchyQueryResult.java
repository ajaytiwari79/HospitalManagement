package com.kairos.user.organization;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by prabjot on 7/4/17.
 */
@QueryResult
public class OrganizationTypeHierarchyQueryResult {

    private List<Map<String,Object>> organizationTypes;

    public List<Map<String, Object>> getOrganizationTypes() {
        return Optional.ofNullable(organizationTypes).orElse(Collections.emptyList());
    }

    public void setOrganizationTypes(List<Map<String, Object>> organizationTypes) {
        this.organizationTypes = organizationTypes;
    }
}
