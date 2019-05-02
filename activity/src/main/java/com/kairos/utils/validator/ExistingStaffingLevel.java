package com.kairos.utils.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ExistingStaffingLevelValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ExistingStaffingLevel {
	String message() default "staffing level already exists for selected date .";

	Class<?>[]groups() default {};

	Class<? extends Payload>[]payload() default {};
}
