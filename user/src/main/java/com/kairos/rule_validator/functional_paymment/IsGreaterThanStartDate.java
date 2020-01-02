package com.kairos.rule_validator.functional_paymment;

import com.kairos.persistence.model.user.expertise.Expertise;
import com.kairos.persistence.model.user.expertise.ExpertiseLine;
import com.kairos.persistence.model.user.expertise.response.FunctionalPaymentDTO;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.exception.ExceptionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static com.kairos.constants.UserMessagesConstants.*;

public class IsGreaterThanStartDate extends AbstractSpecification<FunctionalPaymentDTO> {
    private Logger logger = LoggerFactory.getLogger(IsGreaterThanStartDate.class);
    private ExpertiseLine expertiseLine;
    private ExceptionService exceptionService;

    public IsGreaterThanStartDate(ExpertiseLine expertiseLine, ExceptionService exceptionService) {
        this.expertiseLine = expertiseLine;
        this.exceptionService=exceptionService;
    }

    @Override
    public boolean isSatisfied(FunctionalPaymentDTO functionalPaymentDTO) {
        if (expertiseLine.getStartDate().isAfter(functionalPaymentDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_FUNCTIONALPAYMENTSTARTDATE_GREATER);
        }
        if ( Optional.ofNullable(expertiseLine.getEndDate()).isPresent() &&  Optional.ofNullable(functionalPaymentDTO.getEndDate()).isPresent() &&
                expertiseLine.getEndDate().isAfter(functionalPaymentDTO.getEndDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_DATE_GREATERTHAN_DATE,FUNCTIONALPAYMENT,EXPERTISE);
        }

        if (Optional.ofNullable(functionalPaymentDTO.getEndDate()).isPresent() && functionalPaymentDTO.getStartDate().isAfter(functionalPaymentDTO.getEndDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_LASTDATE_NOTLESSTHAN_STARTDATE);
        }

        return true;
    }
}
