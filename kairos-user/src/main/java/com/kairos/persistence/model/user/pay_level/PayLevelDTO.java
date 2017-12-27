package com.kairos.persistence.model.user.pay_level;

import org.neo4j.ogm.annotation.typeconversion.DateLong;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.util.Date;

/**
 * Created by prabjot on 26/12/17.
 */
@QueryResult
public class PayLevelDTO {

    private Long id;
    private String name;
    private Long OrganizationTypeId;
    private Long LevelId;
    private Long expertiseId;
    private PaymentUnit paymentUnit;
    @DateLong
    private Date startDate;
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
}
