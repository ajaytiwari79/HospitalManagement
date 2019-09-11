package com.kairos.persistence.model.pay_table;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * Created by vipul on 9/3/18.
 */

@Documented
@Constraint(validatedBy = ArraySizeValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
public @interface ValidSize {

    //Class<?> elementType();

    String message() default "Please select at-least 1 choice";

    Class<? extends Payload>[] payload() default {};

    Class<?>[] groups() default {};

    /* Specify constraints when collection element type is NOT constrained
     * validator.getConstraintsForClass(elementType).isBeanConstrained(); */
    Class<?>[] constraints() default {};


}
