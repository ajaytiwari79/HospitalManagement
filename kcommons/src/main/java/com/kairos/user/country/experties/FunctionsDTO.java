package com.kairos.user.country.experties;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Created by vipul on 27/3/18.
 */
@JsonIgnoreProperties
public class FunctionsDTO {
    private Long functionId;
    private BigDecimal amount; // amount which is added to this function;

    public FunctionsDTO() {
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


    public FunctionsDTO(BigDecimal amount, Long functionId) {
        this.functionId = functionId;
        this.amount = amount;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("FunctionsDTO{");
        sb.append(", functionId=").append(functionId);
        sb.append(", amount=").append(amount);
        sb.append('}');
        return sb.toString();
    }
}
