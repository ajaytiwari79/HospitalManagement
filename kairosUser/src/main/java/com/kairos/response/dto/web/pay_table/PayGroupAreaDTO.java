package com.kairos.response.dto.web.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by vipul on 16/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class PayGroupAreaDTO {
    @NotNull(message = "Pay Group Area can not be null")
    private Long payGroupAreaId;
    @NotNull(message = "Pay Grade value can not be null")
    private BigDecimal payGroupAreaAmount;
    private Long id;


    public PayGroupAreaDTO() {
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


    public PayGroupAreaDTO(Long payGroupAreaId, BigDecimal payGroupAreaAmount, Long id) {
        this.payGroupAreaId = payGroupAreaId;
        this.payGroupAreaAmount = payGroupAreaAmount;
        this.id = id;

    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PayTableMatrixDTO{");
        sb.append("payGroupAreaId=").append(payGroupAreaId);
        sb.append(", payGroupAreaAmount=").append(payGroupAreaAmount);
        sb.append('}');
        return sb.toString();
    }


}
