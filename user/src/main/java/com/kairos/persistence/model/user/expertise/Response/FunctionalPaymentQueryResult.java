package com.kairos.persistence.model.user.expertise.Response;
/*
 *Created By Pavan on 22/11/18
 *
 */

import com.kairos.config.neo4j.converter.LocalDateConverter;
import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.FunctionalPaymentMatrix;
import com.kairos.persistence.model.user.expertise.SeniorityLevelFunctionsRelationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.util.List;

@QueryResult
public class FunctionalPaymentQueryResult {
   private Long id;
   private LocalDate startDate;
   private LocalDate endDate;
   private List<FunctionalPaymentMatrixQueryResult> functionalPaymentMatrices;
   private Expertise expertise;
   private PaidOutFrequencyEnum paymentUnit;
   private boolean published;


    public FunctionalPaymentQueryResult() {
        //Default Constructor
    }

    public FunctionalPaymentQueryResult(Long id, LocalDate startDate, LocalDate endDate, Expertise expertise, PaidOutFrequencyEnum paymentUnit) {
        this.id = id;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expertise = expertise;
        this.paymentUnit = paymentUnit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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


    public Expertise getExpertise() {
        return expertise;
    }

    public void setExpertise(Expertise expertise) {
        this.expertise = expertise;
    }

    public List<FunctionalPaymentMatrixQueryResult> getFunctionalPaymentMatrices() {
        return functionalPaymentMatrices;
    }

    public void setFunctionalPaymentMatrices(List<FunctionalPaymentMatrixQueryResult> functionalPaymentMatrices) {
        this.functionalPaymentMatrices = functionalPaymentMatrices;
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
}
