package com.kairos.persistence.model.query_wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by oodles on 6/10/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class ClientContactPersonQueryResultByService {
    private Long serviceId;

    private List<Map<String, Object>> clientContactPersonQueryResults;

    public List<Map<String, Object>> getClientContactPersonQueryResults() {
        return clientContactPersonQueryResults;
    }

    public void setClientContactPersonQueryResults(List<Map<String, Object>> clientContactPersonQueryResults) {
        this.clientContactPersonQueryResults = clientContactPersonQueryResults;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }
}
