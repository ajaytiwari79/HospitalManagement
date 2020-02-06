package com.kairos.commons.service.audit_logging;

import com.kairos.commons.repository.audit_logging.AuditLoggingRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.kairos.commons.utils.ObjectUtils.newArrayList;

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

    public List<Map> getAuditLogOfStaff(List<Long> staffIds ,LocalDate startDate,LocalDate endDate){
        return auditLoggingRepository.getAuditLogOfStaff(staffIds,startDate,endDate);
    }


}
