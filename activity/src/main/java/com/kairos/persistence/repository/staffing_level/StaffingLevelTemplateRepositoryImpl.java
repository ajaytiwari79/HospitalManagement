package com.kairos.persistence.repository.staffing_level;/*
 *Created By Pavan on 13/8/18
 *
 */

import com.kairos.dto.activity.staffing_level.StaffingLevelTemplateDTO;
import com.kairos.persistence.model.staffing_level.StaffingLevelTemplate;
import com.mongodb.client.result.UpdateResult;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static com.kairos.commons.utils.ObjectUtils.isNotNull;

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

    @Override
    public List<StaffingLevelTemplateDTO> findByUnitIdDayTypeAndDate(Long unitID, Date proposedStartDate, Date proposedEndDate, List<Long> dayTypeIds, List<String> days){
        Criteria criteria = Criteria.where("unitId").is(unitID).and("disabled").is(false).and("deleted").is(false).and("dayType").in(dayTypeIds);
        if(isNotNull(days)){
            criteria.and("validDays").in(days);
        }
        Criteria firstCriteria = Criteria.where("validity.startDate").lte(proposedStartDate).and("validity.endDate").exists(false);
        Criteria secondCriteria = Criteria.where("validity.startDate").lte(proposedStartDate).and("validity.endDate").gte(proposedEndDate);
        criteria.orOperator(firstCriteria,secondCriteria);
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(criteria));
        return mongoTemplate.aggregate(aggregation,StaffingLevelTemplate.class,StaffingLevelTemplateDTO.class).getMappedResults();
    }
}
