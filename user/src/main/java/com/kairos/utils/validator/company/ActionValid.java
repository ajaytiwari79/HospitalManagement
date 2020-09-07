package com.kairos.utils.validator.company;

import com.kairos.enums.kpermissions.PermissionAction;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ActionValid {
    String message() default "You don't have permission to perform this action";
    String modelName();
    PermissionAction  action();

    Class<?>[]groups() default {};

    Class<? extends Payload>[]payload() default {};
}
