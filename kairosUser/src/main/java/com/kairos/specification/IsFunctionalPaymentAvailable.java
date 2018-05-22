package com.kairos.specification;

import com.kairos.persistence.model.user.expertise.FunctionalPayment;
import com.kairos.persistence.repository.user.expertise.FunctionalPaymentGraphRepository;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.service.exception.ExceptionService;

import static java.time.temporal.ChronoUnit.DAYS;

public class IsFunctionalPaymentAvailable extends AbstractSpecification<FunctionalPaymentDTO> {
    private FunctionalPaymentGraphRepository functionalPaymentGraphRepository;
    private ExceptionService exceptionService;

    private Long expertiseId;

    public IsFunctionalPaymentAvailable(Long expertiseId, FunctionalPaymentGraphRepository functionalPaymentGraphRepository, ExceptionService exceptionService) {
        this.expertiseId = expertiseId;
        this.exceptionService=exceptionService;
        this.functionalPaymentGraphRepository=functionalPaymentGraphRepository;
    }

    @Override
    public boolean isSatisfied(FunctionalPaymentDTO functionalPaymentDTO) {
        FunctionalPayment functionalPayment = functionalPaymentGraphRepository.getLastFunctionalPaymentOfExpertise(expertiseId);

        if (functionalPayment.getEndDate() != null) {
            long daysBetween = DAYS.between(functionalPaymentDTO.getStartDate(), functionalPayment.getEndDate());
            if (daysBetween != -1L) {
                exceptionService.actionNotPermittedException("message.startdate.allowed", functionalPayment.getEndDate().plusDays(1L));
            }
        } else {
            exceptionService.actionNotPermittedException("message.functionalPayment.alreadyactive");
        }
        return true;
    }


}
