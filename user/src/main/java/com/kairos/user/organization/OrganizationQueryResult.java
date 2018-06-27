package com.kairos.user.organization;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 24/3/17.
 */
@QueryResult
public class OrganizationQueryResult {

    List<Map<String,Object>> organizations;

    public void setOrganizations(List<Map<String, Object>> organizations) {
        this.organizations = organizations;
    }

    public List<Map<String, Object>> getOrganizations() {
        return organizations;
    }
}
