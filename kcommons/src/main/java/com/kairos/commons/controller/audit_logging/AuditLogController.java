package com.kairos.commons.controller.audit_logging;

import com.kairos.commons.service.audit_logging.AuditLoggingService;
import com.kairos.commons.utils.ResponseHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.newArrayList;
import static com.kairos.commons.utils.ResponseHandler.generateResponse;
import static com.kairos.commons.utils.ResponseHandler.invalidResponse;
import static com.kairos.constants.CommonConstants.API_V1;

/**
 * Created by pradeep
 * Created at 4/6/19
 **/
@RestController
@RequestMapping(API_V1)
public class AuditLogController {

    @Inject
    private ApplicationContext context;

    @GetMapping(value = "/get_audit_log/{auditLogType}")
    public ResponseEntity<Map<String, Object>> getAuditLoggingByType(@PathVariable String auditLogType) {
        ResponseEntity<Map<String,Object>> responseEntity;
        if(!context.containsBean("AuditLoggingMongoTemplate")){
            responseEntity = invalidResponse(HttpStatus.BAD_REQUEST, false, "Audit Logging is not enabled for this module");
        }else {
            responseEntity = generateResponse(HttpStatus.OK, false,context.getBean(AuditLoggingService.class).getAuditLoggingByType(auditLogType));
        }
        return responseEntity;
    }

    @GetMapping(value = "/get_audit_log_staff/{staffId}")
    public ResponseEntity<Map<String,Object>>getAuditLoggingOfStaff(@PathVariable Long staffId,
                                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
                                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate){
        return ResponseHandler.generateResponse(HttpStatus.OK,true,context.getBean(AuditLoggingService.class).getAuditLogOfStaff(newArrayList(staffId),startDate,endDate));

    }

}
