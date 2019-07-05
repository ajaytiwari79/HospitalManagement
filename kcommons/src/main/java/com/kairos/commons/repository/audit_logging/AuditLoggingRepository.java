package com.kairos.commons.repository.audit_logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;
import java.util.Map;

/**
 * Created by pradeep
 * Created at 4/6/19
 **/

public class AuditLoggingRepository {

    @Autowired
    @Qualifier("AuditLoggingMongoTemplate")
    private MongoTemplate mongoTemplate;

    public List<Map> getAuditLoggingByType(String auditLogType){
        return mongoTemplate.findAll(Map.class,auditLogType);
    }
}
