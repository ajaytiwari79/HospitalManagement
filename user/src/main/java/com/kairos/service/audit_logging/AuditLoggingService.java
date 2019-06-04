package com.kairos.service.audit_logging;

import com.kairos.persistence.repository.custom_repository.AuditLoggingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by pradeep
 * Created at 4/6/19
 **/
@Service
public class AuditLoggingService {

    @Autowired
    private AuditLoggingRepository auditLoggingRepository;


    public List<Map> getAuditLoggingByType(String auditLogType){
        return auditLoggingRepository.getAuditLoggingByType(auditLogType);
    }

}
