package com.kairos.persistence.repository.todo;

import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.todo.Todo;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class TodoRepositoryImpl implements CustomTodoRepository {

    @Inject
    MongoTemplate mongoTemplate;


//    @Override
//    public List<TodoDTO> findAllByKpiFilter(Long unit, Date startDate, Date endDate, List<Long> staffIds, Collection<String> statuses) {
//        Aggregation aggregation=Aggregation.newAggregation(
//            Aggregation.match(Criteria.where("unitId").is(unit).and("type").is(TodoType.APPROVAL_REQUIRED).and("staffId").in(staffIds).and("status").in(statuses).and("shiftDate").gte(startDate).lte(endDate)),
//             Aggregation.lookup("shifts","entityId","_id","shifts"),
//             Aggregation.project("_id","status").and("shifts").arrayElementAt(0).as("shift"),
//                Aggregation.project("_id","status").and("shift.startDate.").as("shiftDateTime")
//        );
//        AggregationResults<TodoDTO> result = mongoTemplate.aggregate(aggregation, Todo.class, TodoDTO.class);
//        return result.getMappedResults();
//
//    }
}
