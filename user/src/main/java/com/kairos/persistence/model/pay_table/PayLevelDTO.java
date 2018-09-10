package com.kairos.persistence.model.pay_table;

import com.kairos.dto.user.country.pay_table.FutureDate;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Created by prabjot on 26/12/17.
 */

public class PayLevelDTO {

    private Long id;
    private String name;
    private Long OrganizationTypeId;
    private Long LevelId;
    private Long expertiseId;
    private PaymentUnit paymentUnit;
    @NotNull(message = "Start date can't be null")
    @DateLong
    @FutureDate
    private Date startDate;
    @FutureDate
    @DateLong
    private Date endDate;

    public PayLevelDTO() {
        //default constructor
    }

    public PayLevelDTO(String name, Long organizationTypeId, Long expertiseId, PaymentUnit paymentUnit, Date startDate) {
        this.name = name;
        OrganizationTypeId = organizationTypeId;
        this.expertiseId = expertiseId;
        this.paymentUnit = paymentUnit;
        this.startDate = startDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getOrganizationTypeId() {
        return OrganizationTypeId;
    }

    public void setOrganizationTypeId(Long organizationTypeId) {
        OrganizationTypeId = organizationTypeId;
    }

    public Long getLevelId() {
        return LevelId;
    }

    public void setLevelId(Long levelId) {
        LevelId = levelId;
    }

    public Long getExpertiseId() {
        return expertiseId;
    }

    public void setExpertiseId(Long expertiseId) {
        this.expertiseId = expertiseId;
    }

    public PaymentUnit getPaymentUnit() {
        return paymentUnit;
    }

    public void setPaymentUnit(PaymentUnit paymentUnit) {
        this.paymentUnit = paymentUnit;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        PayLevelDTO that = (PayLevelDTO) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(name, that.name)
                .append(OrganizationTypeId, that.OrganizationTypeId)
                .append(LevelId, that.LevelId)
                .append(expertiseId, that.expertiseId)
                .append(paymentUnit, that.paymentUnit)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(name)
                .append(OrganizationTypeId)
                .append(LevelId)
                .append(expertiseId)
                .append(paymentUnit)
                .toHashCode();
    }


}
