package com.kairos.persistence.repository.attendence_setting;

import com.kairos.persistence.model.attendence_setting.TimeAndAttendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

public class TimeAndAttendanceRepositoryImpl implements CustomTimeAndAttendanceRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

   public TimeAndAttendance findMaxAttendanceCheckIn(List<Long> staffIds, Date date){
       Aggregation aggregation = Aggregation.newAggregation(
               match(Criteria.where("staffid").in(staffIds).and("updatedAt").gte(date)),
               sort(Sort.Direction.DESC,"updatedAt"),
               limit(1)
       );

       AggregationResults<TimeAndAttendance> result = mongoTemplate.aggregate(aggregation, TimeAndAttendance.class, TimeAndAttendance.class);
       return (result.getMappedResults().isEmpty())?null:result.getMappedResults().get(0);
   }

    }


