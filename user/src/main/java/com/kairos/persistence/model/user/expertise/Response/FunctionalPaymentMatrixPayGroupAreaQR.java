package com.kairos.persistence.model.user.expertise.Response;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

@QueryResult
public class FunctionalPaymentMatrixPayGroupAreaQR {
    private Map<String,Object> payGroupAreas;
    private List<SeniorityLevelFunctionQR> seniorityLevelFunction;
    private Long id;

    public FunctionalPaymentMatrixPayGroupAreaQR() {
        // dc
    }

    public Map<String, Object> getPayGroupAreas() {
        return payGroupAreas;
    }

    public void setPayGroupAreas(Map<String, Object> payGroupAreas) {
        this.payGroupAreas = payGroupAreas;
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
