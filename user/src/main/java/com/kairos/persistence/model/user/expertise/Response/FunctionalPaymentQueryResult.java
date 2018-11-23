package com.kairos.persistence.model.user.expertise.Response;
/*
 *Created By Pavan on 22/11/18
 *
 */

import com.kairos.enums.shift.PaidOutFrequencyEnum;
import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.FunctionalPaymentMatrix;
import org.springframework.data.neo4j.annotation.QueryResult;

import java.time.LocalDate;
import java.util.List;

@QueryResult
public class FunctionalPaymentQueryResult {
   private Long id;
   private LocalDate startDate;
   private LocalDate endDate;
   private List<FunctionalPaymentMatrix> functionalPaymentMatrices;
   private Expertise expertise;
   private PaidOutFrequencyEnum paymentUnit;


    public FunctionalPaymentQueryResult() {
        //Default Constructor
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

    public List<FunctionalPaymentMatrix> getFunctionalPaymentMatrices() {
        return functionalPaymentMatrices;
    }

    public void setFunctionalPaymentMatrices(List<FunctionalPaymentMatrix> functionalPaymentMatrices) {
        this.functionalPaymentMatrices = functionalPaymentMatrices;
    }

    public PaidOutFrequencyEnum getPaymentUnit() {
        return paymentUnit;
    }

    public void setPaymentUnit(PaidOutFrequencyEnum paymentUnit) {
        this.paymentUnit = paymentUnit;
    }
}
