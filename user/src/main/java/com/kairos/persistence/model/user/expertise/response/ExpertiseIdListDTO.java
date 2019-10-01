package com.kairos.persistence.model.user.expertise.response;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by vipul on 14/9/17.
 */

@QueryResult
public class ExpertiseIdListDTO {
    List<Long> allExpertiseIds;
    List<Long>  linkedExpertiseIds;

    public List<Long> getAllExpertiseIds() {
        return allExpertiseIds;
    }

    public void setAllExpertiseIds(List<Long> allExpertiseIds) {
        this.allExpertiseIds = allExpertiseIds;
    }

    public List<Long> getLinkedExpertise() {
        return linkedExpertiseIds;
    }

    public void setLinkedExpertise(List<Long> linkedExpertise) {
        this.linkedExpertiseIds = linkedExpertise;
    }
}
