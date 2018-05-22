package com.kairos.response.dto.web.experties;

import java.util.List;
import java.util.Set;

public class FunctionalPaymentMatrixDTOS {
    private Set<Long> payGroupAreasIds;
    private List<SeniorityLevelFunction> seniorityLevelFunction;

    public FunctionalPaymentMatrixDTOS() {
        //dc
    }

    public Set<Long> getPayGroupAreasIds() {
        return payGroupAreasIds;
    }

    public void setPayGroupAreasIds(Set<Long> payGroupAreasIds) {
        this.payGroupAreasIds = payGroupAreasIds;
    }

    public List<SeniorityLevelFunction> getSeniorityLevelFunction() {
        return seniorityLevelFunction;
    }

    public void setSeniorityLevelFunction(List<SeniorityLevelFunction> seniorityLevelFunction) {
        this.seniorityLevelFunction = seniorityLevelFunction;
    }
}
