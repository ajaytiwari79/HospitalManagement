package com.kairos.persistence.model.user.expertise.Response;


import com.kairos.config.neo4j.converter.LocalDateConverter;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDate;

@QueryResult
public class FunctionalPaymentDTO {
    @Min(0)
    private Long expertiseId;
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private PaidOutFrequencyEnum paymentUnit;
    private boolean published;
    private BigDecimal percentageValue;




    public FunctionalPaymentDTO() {
        //dc
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PaidOutFrequencyEnum getPaymentUnit() {
        return paymentUnit;
    }

    public void setPaymentUnit(PaidOutFrequencyEnum paymentUnit) {
        this.paymentUnit = paymentUnit;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public BigDecimal getPercentageValue() {
        return percentageValue;
    }

    public void setPercentageValue(BigDecimal percentageValue) {
        this.percentageValue = percentageValue;
    }
}
