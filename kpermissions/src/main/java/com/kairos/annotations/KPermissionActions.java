package com.kairos.annotations;

import com.kairos.enums.kpermissions.PermissionAction;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface KPermissionActions {
    String modelName() default "";
    PermissionAction action();
}
