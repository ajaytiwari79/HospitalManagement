package com.kairos.persistence.model.organization;

import com.kairos.response.dto.web.experties.PaidOutFrequencyEnum;
import org.hibernate.validator.constraints.Range;

/**
 * Created by vipul on 12/4/18.
 */
public class PaymentSettingsDTO {
    private Long id;
    private PaidOutFrequencyEnum type;
    @Range(min = 1l, max = 31L)
    private Long dateOfPayment;
    @Range(min = 1l, max = 12L)
    private Long monthOfPayment;

    public PaymentSettingsDTO() {
        // default cons
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public PaymentSettingsDTO(Long id, PaidOutFrequencyEnum type, Long dateOfPayment, @Range(min = 1l, max = 12L) Long monthOfPayment) {
        this.id = id;
        this.type = type;
        this.dateOfPayment = dateOfPayment;
        this.monthOfPayment = monthOfPayment;
    }
}
