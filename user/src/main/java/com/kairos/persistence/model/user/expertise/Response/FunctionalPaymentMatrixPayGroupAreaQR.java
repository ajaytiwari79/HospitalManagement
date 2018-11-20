package com.kairos.persistence.model.user.expertise.Response;

import com.kairos.persistence.model.user.pay_group_area.PayGroupAreaQueryResult;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Map;
import java.util.Set;

@QueryResult
public class FunctionalPaymentMatrixPayGroupAreaQR {
    private List<PayGroupAreaQueryResult> payGroupAreas;
    private List<SeniorityLevelFunctionQR> seniorityLevelFunction;
    private Long id;

    public FunctionalPaymentMatrixPayGroupAreaQR() {
        // dc
    }

    public List<PayGroupAreaQueryResult> getPayGroupAreas() {
        return payGroupAreas;
    }

    public void setPayGroupAreas(List<PayGroupAreaQueryResult> payGroupAreas) {
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
