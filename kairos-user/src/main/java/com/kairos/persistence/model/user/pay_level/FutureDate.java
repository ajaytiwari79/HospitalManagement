package com.kairos.persistence.model.user.pay_level;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.Future;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The annotated element must be a date in the future.
 * Now is defined as the current time according to the virtual machine
 * The calendar used if the compared type is of type {@code Calendar}
 * is the calendar based on the current timezone and the current locale.
 * <p/>
 * Supported types are:
 * <ul>
 *     <li>{@code java.util.Date}</li>
 * </ul>
 * <p/>
 * {@code null} elements are considered valid.
 *
 * @author Prabjot Singh
 */
@Documented
@Constraint(validatedBy = FutureDateValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.FIELD})
public @interface FutureDate {

    String message() default "Date should be grater then or equal to current date";
    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * Defines several {@link Future} annotations on the same element.
     *
     * @see Future
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        Future[] value();
    }
}
