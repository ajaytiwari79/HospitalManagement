package com.kairos.persistence.repository.staffing_level;/*
 *Created By Pavan on 13/8/18
 *
 */

import com.kairos.persistence.model.staffing_level.StaffingLevelTemplate;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.math.BigInteger;

public class StaffingLevelTemplateRepositoryImpl implements CustomStaffingLevelTemplateRepository {
    @Inject
    private MongoTemplate mongoTemplate;
    @Override
    public boolean deleteStaffingLevelTemplate(BigInteger staffingLevelTemplateId) {
        Query query=new Query(Criteria.where("_id").is(staffingLevelTemplateId));
        Update update=new Update();
        update.set("deleted",true);
        UpdateResult updateResult= mongoTemplate.updateFirst(query,update,StaffingLevelTemplate.class);
        return updateResult.getModifiedCount()>0;

    }
}
