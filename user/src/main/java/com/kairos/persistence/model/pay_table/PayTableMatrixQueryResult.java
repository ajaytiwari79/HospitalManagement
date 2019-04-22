package com.kairos.persistence.model.pay_table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * Created by vipul on 22/3/18.
 */
@QueryResult
public class PayTableMatrixQueryResult {
    private Long payGroupAreaId;
    private String payGroupAreaName;
    private BigDecimal payGroupAreaAmount;


    public PayTableMatrixQueryResult() {
        //Default Constructor
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


    public String getPayGroupAreaName() {
        return payGroupAreaName;
    }

    public void setPayGroupAreaName(String payGroupAreaName) {
        this.payGroupAreaName = payGroupAreaName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof PayTableMatrixQueryResult)) return false;

        PayTableMatrixQueryResult that = (PayTableMatrixQueryResult) o;

        return new EqualsBuilder()
                .append(getPayGroupAreaId(), that.getPayGroupAreaId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(getPayGroupAreaId())
                .toHashCode();
    }
}
