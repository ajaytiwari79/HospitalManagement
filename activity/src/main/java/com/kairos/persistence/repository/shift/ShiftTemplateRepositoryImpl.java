package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.ShiftTemplate;
import com.kairos.response.dto.web.shift.ShiftTemplateDTO;
import com.kairos.util.ObjectMapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

public class ShiftTemplateRepositoryImpl implements CustomShitTemplateRepository {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ShiftTemplateDTO> getAllByUnitIdAndCreatedByAndDeletedFalse(Long unitId, Long createdBy) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId).and("deleted").is(false).and("createdBy").is(createdBy)),
                unwind("individualShiftTemplateIds"),
                lookup("individualShiftTemplate", "individualShiftTemplateIds", "_id", "shiftList"),
                unwind("shiftList"),
                graphLookup("individualShiftTemplate").startWith("$shiftList.subShiftIds").connectFrom("subShifts").connectTo("_id").as("subShifts"),
                project("shiftList.id","shiftList.name","shiftList.remarks","shiftList.activityId","shiftList.unitId","shiftList.startTime","shiftList.endTime","shiftList.isMainShift","createdBy","shiftList.subShifts"));

        AggregationResults<ShiftTemplateDTO> result = mongoTemplate.aggregate(aggregation, ShiftTemplate.class, ShiftTemplateDTO.class);
        return result.getMappedResults();

    }
}
