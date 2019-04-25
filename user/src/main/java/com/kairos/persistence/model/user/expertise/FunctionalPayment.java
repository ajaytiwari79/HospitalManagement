package com.kairos.persistence.model.user.expertise;

import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.persistence.model.common.UserBaseEntity;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.kairos.persistence.model.constants.RelationshipConstants.*;

@NodeEntity
public class FunctionalPayment extends UserBaseEntity {
    @Relationship(type = APPLICABLE_FOR_EXPERTISE)
    private Expertise expertise;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean published;
    private PaidOutFrequencyEnum paymentUnit;
    @Relationship(type = FUNCTIONAL_PAYMENT_MATRIX)
    private List<FunctionalPaymentMatrix> functionalPaymentMatrices;


    @Relationship(type = VERSION_OF)
    private FunctionalPayment parentFunctionalPayment;

    private boolean hasDraftCopy = false;
    // this is kept for tracking to show how many percentage got increase via payTable
    private BigDecimal percentageValue;


    public FunctionalPayment() {

    }

    public FunctionalPayment(Expertise expertise, LocalDate startDate, LocalDate endDate, PaidOutFrequencyEnum paymentUnit) {
        this.expertise = expertise;
        this.startDate = startDate;
        this.endDate = endDate;
        this.published = false;
        this.hasDraftCopy = false;
        this.paymentUnit = paymentUnit;
    }

    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getStartDate() {

        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public PaidOutFrequencyEnum getPaymentUnit() {
        return paymentUnit;
    }

    public void setPaymentUnit(PaidOutFrequencyEnum paymentUnit) {
        this.paymentUnit = paymentUnit;
    }

    public List<FunctionalPaymentMatrix> getFunctionalPaymentMatrices() {
        return Optional.ofNullable(functionalPaymentMatrices).orElse(new ArrayList<>());
    }

    public void setFunctionalPaymentMatrices(List<FunctionalPaymentMatrix> functionalPaymentMatrices) {
        this.functionalPaymentMatrices = functionalPaymentMatrices;
    }

    public FunctionalPayment getParentFunctionalPayment() {
        return parentFunctionalPayment;
    }

    public void setParentFunctionalPayment(FunctionalPayment parentFunctionalPayment) {
        this.parentFunctionalPayment = parentFunctionalPayment;
    }

    public boolean isHasDraftCopy() {
        return hasDraftCopy;
    }

    public void setHasDraftCopy(boolean hasDraftCopy) {
        this.hasDraftCopy = hasDraftCopy;
    }

    public BigDecimal getPercentageValue() {
        return percentageValue;
    }

    public void setPercentageValue(BigDecimal percentageValue) {
        this.percentageValue = percentageValue;
    }

}
