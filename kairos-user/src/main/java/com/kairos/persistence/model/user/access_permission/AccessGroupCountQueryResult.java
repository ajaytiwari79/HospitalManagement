package com.kairos.persistence.model.user.access_permission;

import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prerna on 5/3/18.
 */
@QueryResult
public class AccessGroupCountQueryResult {
    private short hubCount;

    private short organizationCount;

    private short unionCount;

    public short getHubCount() {
        return hubCount;
    }

    public void setHubCount(short hubCount) {
        this.hubCount = hubCount;
    }

    public short getOrganizationCount() {
        return organizationCount;
    }

    public void setOrganizationCount(short organizationCount) {
        this.organizationCount = organizationCount;
    }

    public short getUnionCount() {
        return unionCount;
    }

    public void setUnionCount(short unionCount) {
        this.unionCount = unionCount;
    }
}
