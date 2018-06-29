package com.kairos.user.country.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by vipul on 16/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PayTableMatrixDTO {
    @NotNull(message = "Pay Group Area can not be null")
    private Long payGroupAreaId;
    @NotNull(message = "Pay Grade value can not be null")
    private BigDecimal payGroupAreaAmount;
    private Long id;

    public PayTableMatrixDTO() {
        //default cons
    }

    public Long getPayGroupAreaId() {
        return payGroupAreaId;
    }

    public void setPayGroupAreaId(Long payGroupAreaId) {
        this.payGroupAreaId = payGroupAreaId;
    }

    public BigDecimal getPayGroupAreaAmount() {
        return payGroupAreaAmount;
    }

    public void setPayGroupAreaAmount(BigDecimal payGroupAreaAmount) {
        this.payGroupAreaAmount = payGroupAreaAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PayTableMatrixDTO(Long payGroupAreaId, BigDecimal payGroupAreaAmount, Long id) {
        this.payGroupAreaId = payGroupAreaId;
        this.payGroupAreaAmount = payGroupAreaAmount;
        this.id = id;
    }
}
