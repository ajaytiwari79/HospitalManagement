package com.kairos.persistence.model.organization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kairos.response.dto.web.experties.PaidOutFrequencyEnum;
import org.springframework.data.neo4j.annotation.QueryResult;

/**
 * Created by vipul on 17/4/18.
 */
@QueryResult
@JsonInclude(JsonInclude.Include.NON_EMPTY)   // this annotation is used if the field is empty then it will not be in your response
public class PaymentSettingsQueryResult {
    private PaidOutFrequencyEnum type;
    private Long dateOfPayment;
    private Long monthOfPayment;
    private Long id;

    public PaymentSettingsQueryResult() {
        //
    }

    public PaidOutFrequencyEnum getType() {
        return type;
    }

    public void setType(PaidOutFrequencyEnum type) {
        this.type = type;
    }

    public Long getDateOfPayment() {
        return dateOfPayment;
    }

    public void setDateOfPayment(Long dateOfPayment) {
        this.dateOfPayment = dateOfPayment;
    }

    public Long getMonthOfPayment() {
        return monthOfPayment;
    }

    public void setMonthOfPayment(Long monthOfPayment) {
        this.monthOfPayment = monthOfPayment;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
