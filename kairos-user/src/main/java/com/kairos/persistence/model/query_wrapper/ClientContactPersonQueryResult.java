package com.kairos.persistence.model.query_wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;

/**
 * Created by Jasgeet on 5/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class ClientContactPersonQueryResult {
    private Long staffId;
    private Long serviceId;
    private String  relationType;
    private List<Long> households;

    public Long getStaffId() {
        return staffId;
    }

    public void setStaffId(Long staffId) {
        this.staffId = staffId;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public List<Long> getHouseholds() {
        return households;
    }

    public void setHouseholds(List<Long> households) {
        this.households = households;
    }
}
