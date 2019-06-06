package com.kairos.commons.config.mongo;

import com.kairos.commons.audit_logging.AuditLogging;
import com.kairos.commons.controller.audit_logging.AuditLogController;
import com.kairos.commons.repository.audit_logging.AuditLoggingRepository;
import com.kairos.commons.service.audit_logging.AuditLoggingService;
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
@Import({AuditLogMongoConfig.class, AuditLogging.class, AuditLoggingService.class, AuditLoggingRepository.class, AuditLogController.class})
@Configuration
public @interface EnableAuditLogging {
}
