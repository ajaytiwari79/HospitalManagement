package com.kairos.persistence.model.user.filter;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by prerna on 1/5/18.
 */
@QueryResult
public class FavoriteFilterQueryResult {

    private Long id;
    private List<FavoriteFilterDetailQueryResult> filtersData;
    private String name;

    public FavoriteFilterQueryResult(){
        // default constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<FavoriteFilterDetailQueryResult> getFiltersData() {
        return filtersData;
    }

    public void setFiltersData(List<FavoriteFilterDetailQueryResult> filtersData) {
        this.filtersData = filtersData;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
