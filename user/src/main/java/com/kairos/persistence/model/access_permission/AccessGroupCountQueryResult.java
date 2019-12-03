package com.kairos.persistence.model.access_permission;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prerna on 5/3/18.
 */
@QueryResult
@Getter
@Setter
public class AccessGroupCountQueryResult {
    private short hubCount;

    private short organizationCount;

    private short unionCount;
}
