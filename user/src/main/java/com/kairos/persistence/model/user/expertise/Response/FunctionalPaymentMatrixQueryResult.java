package com.kairos.persistence.model.user.expertise.Response;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Set;

@QueryResult
public class FunctionalPaymentMatrixQueryResult {
    private Set<Long> payGroupAreasIds;
    private List<SeniorityLevelFunctionQR> seniorityLevelFunction;
    private Long id;

    public FunctionalPaymentMatrixQueryResult() {
        // dc
    }

    public Set<Long> getPayGroupAreasIds() {
        return payGroupAreasIds;
    }

    public void setPayGroupAreasIds(Set<Long> payGroupAreasIds) {
        this.payGroupAreasIds = payGroupAreasIds;
    }

    public List<SeniorityLevelFunctionQR> getSeniorityLevelFunction() {
        return seniorityLevelFunction;
    }

    public void setSeniorityLevelFunction(List<SeniorityLevelFunctionQR> seniorityLevelFunction) {
        this.seniorityLevelFunction = seniorityLevelFunction;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
