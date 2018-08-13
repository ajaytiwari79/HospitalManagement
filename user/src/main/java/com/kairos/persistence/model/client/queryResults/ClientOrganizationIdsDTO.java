package com.kairos.persistence.model.client.queryResults;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by oodles on 3/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class ClientOrganizationIdsDTO {
    Long citizenId;
    Long organizationId;

    public Long getCitizenId() {
        return citizenId;
    }

    public void setCitizenId(Long citizenId) {
        this.citizenId = citizenId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }
}
