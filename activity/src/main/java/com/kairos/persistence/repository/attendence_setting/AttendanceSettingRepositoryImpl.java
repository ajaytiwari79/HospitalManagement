package com.kairos.persistence.repository.attendence_setting;

import com.kairos.persistence.model.attendence_setting.TimeAndAttendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Date;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class AttendanceSettingRepositoryImpl implements CustomAttendanceSettingRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

   public TimeAndAttendance findMaxAttendanceCheckIn(Long userId, Date date, String shiftState){
       Aggregation aggregation = Aggregation.newAggregation(
               match(Criteria.where("userId").is(userId).and("updatedAt").gte(date).orOperator(Criteria.where("shiftState").exists(true),Criteria.where("shiftState").ne(shiftState))),
               sort(Sort.Direction.DESC,"updatedAt"),
               limit(1)
       );

       AggregationResults<TimeAndAttendance> result = mongoTemplate.aggregate(aggregation, TimeAndAttendance.class, TimeAndAttendance.class);
       return (result.getMappedResults().isEmpty())?null:result.getMappedResults().get(0);
   }

    }


