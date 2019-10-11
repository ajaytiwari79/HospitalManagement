package com.kairos.persistence.model.user.expertise.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.List;
import java.util.Set;

@QueryResult
@Getter
@Setter
@NoArgsConstructor
public class FunctionalPaymentMatrixQueryResult {
    private Set<Long> payGroupAreasIds;
    private List<SeniorityLevelFunctionQR> seniorityLevelFunction;
    private Long id;

    public FunctionalPaymentMatrixQueryResult(Set<Long> payGroupAreasIds, List<SeniorityLevelFunctionQR> seniorityLevelFunction) {
        this.payGroupAreasIds = payGroupAreasIds;
        this.seniorityLevelFunction = seniorityLevelFunction;
    }

}
