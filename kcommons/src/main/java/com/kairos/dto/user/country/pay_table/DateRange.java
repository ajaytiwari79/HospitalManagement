package com.kairos.dto.user.country.pay_table;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;

/**
 * Created by vipul on 10/3/18.
 */

@Target({TYPE, ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DateRangeValidator.class})
@Documented
public @interface DateRange {

    String message() default "'start date' must be less than 'end date'. ";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
