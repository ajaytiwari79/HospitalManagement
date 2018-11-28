package com.kairos.persistence.model.user.expertise.Response;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;

@QueryResult
public class FunctionQR {
    private Long functionId;
    private Long functionName; // this is for FE compatibility
    private BigDecimal amount; // amount which is added to this function;
    private boolean amountEditableAtUnit;

    public FunctionQR() {
        // dc
    }

    public FunctionQR(Long functionId, Long functionName, BigDecimal amount, boolean amountEditableAtUnit) {
        this.functionId = functionId;
        this.functionName = functionName;
        this.amount = amount;
        this.amountEditableAtUnit = amountEditableAtUnit;
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

    public boolean isAmountEditableAtUnit() {
        return amountEditableAtUnit;
    }

    public void setAmountEditableAtUnit(boolean amountEditableAtUnit) {
        this.amountEditableAtUnit = amountEditableAtUnit;
    }
}
