package com.kairos.persistence.model.user.country.tag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by prerna on 13/11/17.
 */
@QueryResult
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagQueryResult {
    private long id;
    private String name;
    private String masterDataType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMasterDataType() {
        return masterDataType;
    }

    public void setMasterDataType(String masterDataType) {
        this.masterDataType = masterDataType;
    }
}
