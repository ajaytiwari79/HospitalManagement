package com.kairos.persistence.model.user.filter;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by prerna on 1/5/18.
 */
@QueryResult
public class FavoriteFilterDetailQueryResult {

    private Long id;
    private String name;
    private List<String> value;

    public FavoriteFilterDetailQueryResult(){
        // default constructor
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }
}
