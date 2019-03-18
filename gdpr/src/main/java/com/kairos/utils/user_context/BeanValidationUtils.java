package com.kairos.utils.user_context;

import com.kairos.commons.utils.CommonsExceptionUtil;
import com.kairos.utils.ValidateRequestBodyList;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.List;
import java.util.Set;

@Component
public class BeanValidationUtils extends CommonsExceptionUtil {


    private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();

    public <T> void validateConstriantOfJavaBean(List<T> entity) {

        Assert.notEmpty(entity, "list cannot be emty");
        ValidateRequestBodyList<T> requestBodyList = new ValidateRequestBodyList<>(entity);
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<ValidateRequestBodyList>> violations = validator.validate(requestBodyList);
        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<ValidateRequestBodyList> violation : violations) {
            message.append(convertMessage(violation.getMessage()));
            message.append(System.getProperty("line.separator"));
        }
        throw new RuntimeException(message.toString());

    }

    public <T> void validateConstriantOfJavaBean(T entity) {

        Assert.notNull(entity, "null entity not allowed");
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(entity);
        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<T> violation : violations) {
            message.append(convertMessage(violation.getMessage()));
            message.append(System.getProperty("line.separator"));
        }
        throw new RuntimeException(message.toString());
    }


}
