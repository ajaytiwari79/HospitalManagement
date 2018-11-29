package com.kairos.persistence.repository.attendence_setting;

import com.kairos.dto.activity.attendance.TimeAndAttendanceDTO;
import com.kairos.persistence.model.attendence_setting.TimeAndAttendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class TimeAndAttendanceRepositoryImpl implements CustomTimeAndAttendanceRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

   public TimeAndAttendance findMaxAttendanceCheckIn(List<Long> staffIds, LocalDate date){
       Aggregation aggregation = Aggregation.newAggregation(
               match(Criteria.where("staffId").in(staffIds).and("date").gte(date)),
               sort(Sort.Direction.DESC,"updatedAt"),
               limit(1)
       );

       AggregationResults<TimeAndAttendance> result = mongoTemplate.aggregate(aggregation, TimeAndAttendance.class, TimeAndAttendance.class);
       return (result.getMappedResults().isEmpty())?null:result.getMappedResults().get(0);
   }

    public TimeAndAttendance findMaxAttendanceCheckOut(List<Long> staffIds, LocalDate date){
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("staffId").in(staffIds).and("date").gte(date).and("attendanceTimeSlot").elemMatch(Criteria.where("to").exists(false))),
                sort(Sort.Direction.DESC,"updatedAt"),
                limit(1)
        );

        AggregationResults<TimeAndAttendance> result = mongoTemplate.aggregate(aggregation, TimeAndAttendance.class, TimeAndAttendance.class);
        return (result.getMappedResults().isEmpty())?null:result.getMappedResults().get(0);
    }

    @Override
    public List<TimeAndAttendanceDTO> findAllAttendanceByStaffIds(List<Long> staffIds,Long unitId, Date lastDate,Date currentDate) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("staffId").in(staffIds).and("date").gte(lastDate)),
                unwind("attendanceTimeSlot"),
                match(Criteria.where("attendanceTimeSlot.unitId").is(unitId).orOperator(Criteria.where("attendanceTimeSlot.to").gte(currentDate),Criteria.where("attendanceTimeSlot.from").gte(currentDate))),
                group("staffId").push("$attendanceTimeSlot").as("attendanceTimeSlot"),
                Aggregation.project().and("$_id").as("staffId").and("attendanceTimeSlot").as("attendanceTimeSlot")
        );
        AggregationResults<TimeAndAttendanceDTO> result = mongoTemplate.aggregate(aggregation, TimeAndAttendance.class, TimeAndAttendanceDTO.class);
        return (result.getMappedResults().isEmpty())?null:result.getMappedResults();
    }

}


