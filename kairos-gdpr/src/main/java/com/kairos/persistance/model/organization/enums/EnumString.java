package com.kairos.persistance.model.organization.enums;

import java.lang.annotation.*;
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface EnumString {

    String TYPE = "value";

    Class<? extends Enum> value();
}
