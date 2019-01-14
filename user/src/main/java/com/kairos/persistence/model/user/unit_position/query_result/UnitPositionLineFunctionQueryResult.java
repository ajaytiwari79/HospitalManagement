package com.kairos.persistence.model.user.unit_position.query_result;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.persistence.model.country.functions.FunctionDTO;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@QueryResult
public class UnitPositionLineFunctionQueryResult {

    private LocalDate startDate;
    private BigDecimal hourlyCost;
    private BigDecimal basePayGradeAmount;
    private List<FunctionDTO> functions;

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public BigDecimal getHourlyCost() {
        return hourlyCost;
    }

    public void setHourlyCost(BigDecimal hourlyCost) {
        this.hourlyCost = hourlyCost;
    }

    public BigDecimal getBasePayGradeAmount() {
        return basePayGradeAmount;
    }

    public void setBasePayGradeAmount(BigDecimal basePayGradeAmount) {
        this.basePayGradeAmount = basePayGradeAmount;
    }

    public List<FunctionDTO> getFunctions() {
        return functions;
    }

    public void setFunctions(List<FunctionDTO> functions) {
        this.functions = functions;
    }
}
