package com.kairos.persistence.model.organization;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by prabjot on 12/4/17.
 */
@QueryResult
public class OrgTypeExpertiseQueryResult {

    private List<Map<String,Object>> expertise;

    public void setExpertise(List<Map<String, Object>> expertise) {
        this.expertise = expertise;
    }

    public List<Map<String, Object>> getExpertise() {
        return expertise;
    }
}
