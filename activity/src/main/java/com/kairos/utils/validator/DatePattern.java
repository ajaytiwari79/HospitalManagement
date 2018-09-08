package com.kairos.utils.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Constraint(validatedBy = DatePatternValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatePattern {
	String message() default "Enter a valid date format";

	Class<?>[]groups() default {};

	Class<? extends Payload>[]payload() default {};
}
