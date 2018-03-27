package com.kairos.response.dto.web.experties;

import java.math.BigDecimal;

/**
 * Created by vipul on 27/3/18.
 */
public class FunctionsDTO {
    private Long functionId;
    private BigDecimal amount; // amount which is added to this function;

    public FunctionsDTO() {
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
}
