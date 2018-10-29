package com.kairos.dto.user.country.experties;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Created by vipul on 27/3/18.
 */
@JsonIgnoreProperties
public class FunctionsDTO {
    private Long id;
    private String name; // THIS is used for FE compactibility
    private BigDecimal amount; // amount which is added to this function;
    private boolean amountEditableAtUnit;

    public FunctionsDTO() {
        // dc
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFunctionId() {
        return id;
    } // THIS IS for FE compactibility We need to remove this Impact on FUNCTION inside expertise

    public void setFunctionId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public boolean isAmountEditableAtUnit() {
        return amountEditableAtUnit;
    }

    public void setAmountEditableAtUnit(boolean amountEditableAtUnit) {
        this.amountEditableAtUnit = amountEditableAtUnit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FunctionsDTO(BigDecimal amount, Long id) {
        this.id = id;
        this.amount = amount;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FunctionsDTO{");
        sb.append(", functionId=").append(id);
        sb.append(", amount=").append(amount);
        sb.append('}');
        return sb.toString();
    }
}
