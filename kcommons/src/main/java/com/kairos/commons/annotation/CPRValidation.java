package com.kairos.commons.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Constraint(validatedBy = CPRValidatorConfiguration.class)
public @interface CPRValidation {

    String message() default "{javax.validation.constraints.Pattern.message}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default {};
}