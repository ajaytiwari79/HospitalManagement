package com.kairos.persistence.repository.attendence_setting;

import com.kairos.persistence.model.attendence_setting.AttendanceSetting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class AttendanceSettingRepositoryImpl implements CustomAttendanceSettingRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

   public AttendanceSetting findMaxAttendanceCheckIn(Long userId, Date date){
       Aggregation aggregation = Aggregation.newAggregation(
               match(Criteria.where("userId").is(userId).and("createdAt").gte(date)),
               sort(Sort.Direction.DESC,"createdAt"),
               limit(1)
       );

       AggregationResults<AttendanceSetting> result = mongoTemplate.aggregate(aggregation, AttendanceSetting.class, AttendanceSetting.class);
       return (result.getMappedResults().isEmpty())?null:result.getMappedResults().get(0);
   }

    }


