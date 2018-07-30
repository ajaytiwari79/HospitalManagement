package com.kairos.util.date_validator;

import com.kairos.util.DateUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class FutureLocalDateValidator implements ConstraintValidator<FutureLocalDate, LocalDate> {
    @Override
    public void initialize(FutureLocalDate constraintAnnotation) {

    }
    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {

        if (value == null) {
            return true;
        }
        int dateValue = value.compareTo(DateUtils.getCurrentLocalDate());
        if (dateValue < 0) {
            return false;
        }
        return true;
    }
}
