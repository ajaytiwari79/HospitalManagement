package com.kairos.specification;

import com.kairos.persistence.model.user.expertise.FunctionalPayment;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.service.exception.ExceptionService;

import java.util.Optional;

import static java.time.temporal.ChronoUnit.DAYS;

public class IsFunctionalPaymentAvailable extends AbstractSpecification<FunctionalPaymentDTO> {
    private FunctionalPayment functionalPayment;
    private ExceptionService exceptionService;

    public IsFunctionalPaymentAvailable(FunctionalPayment functionalPayment, ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
        this.functionalPayment = functionalPayment;
    }

    @Override
    public boolean isSatisfied(FunctionalPaymentDTO functionalPaymentDTO) {
        if (Optional.ofNullable(functionalPayment).isPresent()) {
            if (functionalPayment.getEndDate() != null) {
                long daysBetween = DAYS.between(functionalPaymentDTO.getStartDate(), functionalPayment.getEndDate());
                if (daysBetween != -1L) {
                    exceptionService.actionNotPermittedException("message.startdate.allowed", functionalPayment.getEndDate().plusDays(1L));
                }
            } else {
                exceptionService.actionNotPermittedException("message.functionalPayment.alreadyactive");
            }
        }
        return true;
    }


}
