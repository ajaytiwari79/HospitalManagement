package com.kairos.commons.service.audit_logging;

import com.kairos.commons.repository.audit_logging.AuditLoggingRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Created by pradeep
 * Created at 4/6/19
 **/
public class AuditLoggingService {

    @Autowired
    private AuditLoggingRepository auditLoggingRepository;


    public List<Map> getAuditLoggingByType(String auditLogType){
        return auditLoggingRepository.getAuditLoggingByType(auditLogType);
    }

}
