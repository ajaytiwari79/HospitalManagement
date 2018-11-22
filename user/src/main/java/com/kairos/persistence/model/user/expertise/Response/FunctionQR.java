package com.kairos.persistence.model.user.expertise.Response;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;

@QueryResult
public class FunctionQR {
    private Long functionId;
    private Long functionName; // this is for FE compatibility
    private BigDecimal amount; // amount which is added to this function;

    public FunctionQR() {
        // dc
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Long getFunctionName() {
        return functionName;
    }

    public void setFunctionName(Long functionName) {
        this.functionName = functionName;
    }
}
