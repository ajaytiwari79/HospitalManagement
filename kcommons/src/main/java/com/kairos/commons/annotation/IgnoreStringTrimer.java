package com.kairos.commons.annotation;

import java.lang.annotation.*;

/**
 * Created by pradeep
 * Created at 5/6/19
 **/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface IgnoreStringTrimer {}
