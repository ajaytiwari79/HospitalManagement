package com.kairos.scheduler.config;

import java.lang.annotation.*;

/**
 * Created by prabjot on 21/12/17.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD})
public @interface OrderTest {
    public int order();
}
