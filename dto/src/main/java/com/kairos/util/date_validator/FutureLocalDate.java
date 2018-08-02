package com.kairos.util.date_validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * The annotated element must be a date in the future.
 * Now is defined as the current time according to the virtual machine
 * The calendar used if the compared type is of type {@code Calendar}
 * is the calendar based on the current timezone and the current locale.
 * <p/>
 * Supported types are:
 * <ul>
 * <li>{@code java.util.Date}</li>
 * </ul>
 * <p/>
 * {@code null} elements are considered valid.
 */
@Documented
@Constraint(validatedBy = FutureLocalDateValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({FIELD, METHOD, PARAMETER, ANNOTATION_TYPE})
public @interface FutureLocalDate {

    String message() default "Date should be grater then or equal to current date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
