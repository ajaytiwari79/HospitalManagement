package com.kairos.commons.config.mongo;

import com.kairos.commons.audit_logging.AuditLogging;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Created by pradeep
 * Created at 3/6/19
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
@Import({AuditLogMongoConfig.class, AuditLogging.class})
@Configuration
public @interface EnableAuditLogging {
}
