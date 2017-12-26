package com.kairos.persistence.model.user.pay_level;

import com.kairos.persistence.model.common.UserBaseEntity;
import com.kairos.persistence.model.organization.Level;
import com.kairos.persistence.model.organization.OrganizationType;
import com.kairos.persistence.model.user.country.Country;
import com.kairos.persistence.model.user.expertise.Expertise;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by prabjot on 21/12/17.
 */
public class PayLevel extends UserBaseEntity {
    private String name;
    private Country country;
    private Expertise expertise;
    private OrganizationType organizationType;
    private Level level;
    private PaymentUnit paymentUnit;
    private List<PayLevelMatrix> payLevelMatrices=new ArrayList<>();
    @DateLong
    private Date startDate;
    @DateLong
    private Date endDate;
    private boolean deleted;
    private boolean disabled;

    public PayLevel() {
        //default constructor
    }

    public PayLevel(String name, Country country, Expertise expertise, OrganizationType organizationType, Level level,
                    PaymentUnit paymentUnit, Date startDate, Date endDate) {
        this.name = name;
        this.country = country;
        this.expertise = expertise;
        this.organizationType = organizationType;
        this.level = level;
        this.paymentUnit = paymentUnit;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public OrganizationType getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(OrganizationType organizationType) {
        this.organizationType = organizationType;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PayLevelMatrix> getPayLevelMatrices() {
        return payLevelMatrices;
    }

    public void setPayLevelMatrices(List<PayLevelMatrix> payLevelMatrices) {
        this.payLevelMatrices = payLevelMatrices;
    }
}
