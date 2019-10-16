package com.kairos.commons.repository.audit_logging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.LocalDate;
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
    public List<Map> getAuditLogOfStaff(Long staffId,LocalDate startDate,LocalDate endDate) {

        Query query = new Query();
        query.addCriteria(Criteria.where("staffId").is(staffId).and ("startDate").gte(startDate).and("endDate").lte(endDate));
        return mongoTemplate.find(query ,Map.class,"Shift");
    }
}


