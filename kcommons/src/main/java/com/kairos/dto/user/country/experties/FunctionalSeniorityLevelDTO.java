package com.kairos.dto.user.country.experties;

import com.kairos.dto.activity.shift.Expertise;
import com.kairos.enums.shift.PaidOutFrequencyEnum;

import java.time.LocalDate;
import java.util.List;

public class FunctionalSeniorityLevelDTO {

    private Long functionalPaymentId;
    private List<FunctionalPaymentMatrixDTO> functionalPaymentMatrix;


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

}
