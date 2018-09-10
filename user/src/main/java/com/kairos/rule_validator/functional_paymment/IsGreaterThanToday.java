package com.kairos.rule_validator.functional_paymment;

import com.kairos.persistence.model.user.expertise.Response.FunctionalPaymentDTO;
import com.kairos.rule_validator.AbstractSpecification;
import com.kairos.service.exception.ExceptionService;
import com.kairos.utils.DateUtil;

import java.time.LocalDate;
import java.util.Optional;

public class IsGreaterThanToday extends AbstractSpecification<FunctionalPaymentDTO> {

    private ExceptionService exceptionService;

    public IsGreaterThanToday(ExceptionService exceptionService) {
        this.exceptionService = exceptionService;
    }

    @Override
    public boolean isSatisfied(FunctionalPaymentDTO functionalPaymentDTO) {
        LocalDate currentDate = DateUtil.getCurrentLocalDate();
        if (currentDate.isAfter(functionalPaymentDTO.getStartDate())) {
            exceptionService.actionNotPermittedException("message.startdate.notlessthan.currentdate");
        }
        if (Optional.ofNullable(functionalPaymentDTO.getEndDate()).isPresent() && currentDate.isAfter(functionalPaymentDTO.getEndDate())){
            exceptionService.actionNotPermittedException("message.lastdate.notlessthan.startdate");
        }

        return true;
    }
}
