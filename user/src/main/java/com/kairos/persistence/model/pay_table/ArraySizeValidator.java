package com.kairos.persistence.model.pay_table;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.Set;

/**
 * Created by vipul on 9/3/18.
 */
public class ArraySizeValidator implements ConstraintValidator<ValidSize, Set<Long>> {
    @Override
    public void initialize(ValidSize constraintAnnotation) {
        //this is overridden method
    }

    @Override
    public boolean isValid(Set<Long> values, ConstraintValidatorContext context) {
        return (!Optional.ofNullable(values).isPresent() || values.isEmpty()) ? false : true;
    }
}