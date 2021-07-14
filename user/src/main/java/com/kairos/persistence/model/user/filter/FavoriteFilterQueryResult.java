package com.kairos.persistence.model.user.filter;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by prerna on 1/5/18.
 */
@QueryResult
@Getter
@Setter
public class FavoriteFilterQueryResult {

    private Long id;
    private List<FavoriteFilterDetailQueryResult> filtersData;
    private String name;
    private int sequence;
    private int usedCount;
}
