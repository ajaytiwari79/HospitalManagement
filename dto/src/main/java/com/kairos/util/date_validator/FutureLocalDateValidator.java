package com.kairos.util.date_validator;

import com.kairos.util.DateUtils;
import jdk.nashorn.internal.runtime.options.Option;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Optional;

public class FutureLocalDateValidator implements ConstraintValidator<FutureLocalDate, LocalDate> {
    @Override
    public void initialize(FutureLocalDate constraintAnnotation) {

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
