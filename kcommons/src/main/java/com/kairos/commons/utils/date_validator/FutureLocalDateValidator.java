package com.kairos.commons.utils.date_validator;

import com.kairos.commons.utils.DateUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Optional;

public class FutureLocalDateValidator implements ConstraintValidator<FutureLocalDate, LocalDate> {
    @Override
    public void initialize(FutureLocalDate constraintAnnotation) {
        //Not in use
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        boolean result = true;
        if (!Optional.ofNullable(value).isPresent()) {
            result = true;
        } else {
            int dateValue = value.compareTo(DateUtils.getCurrentLocalDate());
            if (dateValue < 0) {
                result = false;
            }
        }
        return result;
    }
}
