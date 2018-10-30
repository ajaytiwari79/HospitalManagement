package com.kairos.persistence.model.country.functions;

import org.springframework.data.neo4j.annotation.QueryResult;

import java.math.BigDecimal;

/**
 * CreatedBy vipulpandey on 25/10/18
 **/
@QueryResult
public class FunctionWithAmountQueryResult {
    private Function function;
    private boolean amountEditableAtUnit;
    private BigDecimal amount;
    private String icon;

    public FunctionWithAmountQueryResult() {
        //
    }

    public Function getFunction() {
        return function;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public boolean isAmountEditableAtUnit() {
        return amountEditableAtUnit;
    }

    public void setAmountEditableAtUnit(boolean amountEditableAtUnit) {
        this.amountEditableAtUnit = amountEditableAtUnit;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
