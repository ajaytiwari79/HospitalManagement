package com.kairos.rule_validator.functional_paymment;

import com.kairos.commons.utils.DateUtils;
import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.exception.ExceptionService;

import java.time.LocalDate;
import java.util.Optional;

import static com.kairos.constants.UserMessagesConstants.MESSAGE_LASTDATE_NOTLESSTHAN_STARTDATE;
import static com.kairos.constants.UserMessagesConstants.MESSAGE_STARTDATE_NOTLESSTHAN_CURRENTDATE;

public class IsGreaterThanToday extends AbstractSpecification<FunctionalPaymentDTO> {

    private ExceptionService exceptionService;

    public IsGreaterThanToday(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }

    @Override
    public boolean isSatisfied(FunctionalPaymentDTO functionalPaymentDTO) {
        LocalDate currentDate = DateUtils.getCurrentLocalDate();
        if (currentDate.isAfter(functionalPaymentDTO.getStartDate())) {
            exceptionService.actionNotPermittedException(MESSAGE_STARTDATE_NOTLESSTHAN_CURRENTDATE);
        }
        if (Optional.ofNullable(functionalPaymentDTO.getEndDate()).isPresent() && currentDate.isAfter(functionalPaymentDTO.getEndDate())){
            exceptionService.actionNotPermittedException(MESSAGE_LASTDATE_NOTLESSTHAN_STARTDATE);
        }

        return true;
    }
}
