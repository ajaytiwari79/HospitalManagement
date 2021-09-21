package com.kairos.dto.user.country.experties;

import com.kairos.enums.shift.PaidOutFrequencyEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class FunctionalSeniorityLevelDTO {

    private Long functionalPaymentId;
    private List<FunctionalPaymentMatrixDTO> functionalPaymentMatrix;
    private PaidOutFrequencyEnum paymentUnit;
}
