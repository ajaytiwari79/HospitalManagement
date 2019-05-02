package com.kairos.utils.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Target({ ElementType.METHOD, ElementType.FIELD })
@Constraint(validatedBy = DatePatternValidator.class)
@Retention(RetentionPolicy.RUNTIME)
public @interface DatePattern {
	String message() default "Enter a valid date format";

	Class<?>[]groups() default {};

	Class<? extends Payload>[]payload() default {};
}
