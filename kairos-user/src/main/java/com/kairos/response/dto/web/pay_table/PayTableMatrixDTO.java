package com.kairos.response.dto.web.pay_table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kairos.persistence.model.user.pay_table.PayGradeStateEnum;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by vipul on 16/3/18.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@QueryResult
public class PayTableMatrixDTO {
    @NotNull(message = "Pay Group Area can not be null")
    private Long payGroupAreaId;
    @NotNull(message = "Pay Grade value can not be null")
    private BigDecimal payGroupAreaAmount;
    private Long id;
    private PayGradeStateEnum state;

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

    public PayGradeStateEnum getState() {
        return state;
    }

    public void setState(PayGradeStateEnum state) {
        this.state = state;
    }

    public PayTableMatrixDTO(Long payGroupAreaId, BigDecimal payGroupAreaAmount, Long id, PayGradeStateEnum payGradeStateEnum) {
        this.payGroupAreaId = payGroupAreaId;
        this.payGroupAreaAmount = payGroupAreaAmount;
        this.id = id;
        this.state = payGradeStateEnum;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("PayTableMatrixDTO{");
        sb.append("payGroupAreaId=").append(payGroupAreaId);
        sb.append(", payGroupAreaAmount=").append(payGroupAreaAmount);
        sb.append(", state=").append(state);
        sb.append('}');
        return sb.toString();
    }


}
