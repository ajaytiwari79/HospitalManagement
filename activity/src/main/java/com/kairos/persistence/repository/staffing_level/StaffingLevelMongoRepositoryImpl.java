package com.kairos.persistence.repository.staffing_level;

import com.kairos.persistence.model.staffing_level.StaffingLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Date;
import java.util.List;

@Component
public class StaffingLevelMongoRepositoryImpl implements StaffingLevelCustomRepository{
    public static final String CURRENT_DATE = "currentDate";
    @Autowired
    private MongoTemplate mongoTemplate;


    public List<StaffingLevel> getStaffingLevelsByUnitIdAndDate(Long unitId, Date startDate, Date endDate){
        Query query = new Query(Criteria.where("unitId").is(unitId).and(CURRENT_DATE).gte(startDate).lte(endDate).and("deleted").is(false));
        query.with(Sort.by(Sort.Direction.ASC, CURRENT_DATE));
        return mongoTemplate.find(query,StaffingLevel.class);
    }


}
