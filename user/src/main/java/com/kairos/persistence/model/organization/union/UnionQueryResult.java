package com.kairos.persistence.model.organization.union;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by pavan on 15/3/18.
 */
@QueryResult
@Getter
@Setter
public class UnionQueryResult {
    private Long id;
    private String name;
}
