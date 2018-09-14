package com.kairos.dto.user.country.experties;

import java.util.List;
import java.util.Set;

public class FunctionalPaymentMatrixDTO {
    private Set<Long> payGroupAreasIds;
    private List<SeniorityLevelFunctionDTO> seniorityLevelFunction;
    private Long id;

    public FunctionalPaymentMatrixDTO() {
        //dc
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Set<Long> getPayGroupAreasIds() {
        return payGroupAreasIds;
    }

    public void setPayGroupAreasIds(Set<Long> payGroupAreasIds) {
        this.payGroupAreasIds = payGroupAreasIds;
    }

    public List<SeniorityLevelFunctionDTO> getSeniorityLevelFunction() {
        return seniorityLevelFunction;
    }

    public void setSeniorityLevelFunction(List<SeniorityLevelFunctionDTO> seniorityLevelFunction) {
        this.seniorityLevelFunction = seniorityLevelFunction;
    }
}
