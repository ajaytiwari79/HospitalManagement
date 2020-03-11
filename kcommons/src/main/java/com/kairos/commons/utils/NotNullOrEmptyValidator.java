package com.kairos.commons.utils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Collection;

import static com.kairos.commons.utils.ObjectUtils.isCollectionNotEmpty;

public class NotNullOrEmptyValidator implements ConstraintValidator<NotNullOrEmpty,Object> {
    @Override
    public void initialize(NotNullOrEmpty constraintAnnotation) {
        //Not in use
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value instanceof Collection){
            return isCollectionNotEmpty((Collection<?>) value);
        }else {
            if (value == null) {
                return false;
            }
            if (((String)value).length() == 0) {
                return false;
            }

            boolean isAllWhitespace = ((String)value).matches("^\\s*$");
            return !isAllWhitespace;
        }
    }
}
