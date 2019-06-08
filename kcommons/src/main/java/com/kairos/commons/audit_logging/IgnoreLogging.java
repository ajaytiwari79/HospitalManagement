package com.kairos.commons.audit_logging;

import java.lang.annotation.*;

/**
 * Created by pradeep
 * Created at 3/6/19
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.METHOD,ElementType.TYPE})
public @interface IgnoreLogging {
}
