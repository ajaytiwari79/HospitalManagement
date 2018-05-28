package com.kairos.specification;

import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.ZoneId;
import java.util.Optional;

public class IsGreaterThanStartDate extends AbstractSpecification<FunctionalPaymentDTO> {
    private Logger logger = LoggerFactory.getLogger(IsGreaterThanStartDate.class);
    private Expertise expertise;
    private ExceptionService exceptionService;

    public IsGreaterThanStartDate(Expertise expertise,ExceptionService exceptionService) {
        this.expertise = expertise;
        this.exceptionService=exceptionService;
    }

    @Override
    public boolean isSatisfied(FunctionalPaymentDTO functionalPaymentDTO) {
        if (expertise.getStartDateMillis().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(functionalPaymentDTO.getStartDate())) {
            exceptionService.actionNotPermittedException("message.functionalPaymentStartDate.greater");
        }
        if ( Optional.ofNullable(expertise.getEndDateMillis()).isPresent() &&  Optional.ofNullable(functionalPaymentDTO.getEndDate()).isPresent() &&
                expertise.getEndDateMillis().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().isAfter(functionalPaymentDTO.getEndDate())) {
            exceptionService.actionNotPermittedException("message.date.greaterthan.date","functionalPayment","expertise");
        }

        if (Optional.ofNullable(functionalPaymentDTO.getEndDate()).isPresent() && functionalPaymentDTO.getStartDate().isAfter(functionalPaymentDTO.getEndDate())) {
            exceptionService.actionNotPermittedException("message.lastdate.notlessthan.startdate");
        }

        return true;
    }
}
