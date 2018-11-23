package com.kairos.dto.user.country.experties;

import com.kairos.dto.activity.shift.Expertise;
import com.kairos.enums.shift.PaidOutFrequencyEnum;

import java.time.LocalDate;
import java.util.List;

public class FunctionalSeniorityLevelDTO {
    private Long id;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long functionalPaymentId;
    private List<FunctionalPaymentMatrixDTO> functionalPaymentMatrix;
    private Expertise expertise;
    private PaidOutFrequencyEnum paymentUnit;


    public FunctionalSeniorityLevelDTO() {
        //DC
    }


    public Long getFunctionalPaymentId() {
        return functionalPaymentId;
    }

    public void setFunctionalPaymentId(Long functionalPaymentId) {
        this.functionalPaymentId = functionalPaymentId;
    }

    public List<FunctionalPaymentMatrixDTO> getFunctionalPaymentMatrix() {
        return functionalPaymentMatrix;
    }

    public void setFunctionalPaymentMatrix(List<FunctionalPaymentMatrixDTO> functionalPaymentMatrix) {
        this.functionalPaymentMatrix = functionalPaymentMatrix;
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

    public PaidOutFrequencyEnum getPaymentUnit() {
        return paymentUnit;
    }

    public void setPaymentUnit(PaidOutFrequencyEnum paymentUnit) {
        this.paymentUnit = paymentUnit;
    }
}
