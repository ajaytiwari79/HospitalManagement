package com.kairos.persistence.repository.todo;

import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoType;
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


    @Override
    public List<TodoDTO> findAllByKpiFilter(Long unit, Date startDate, Date endDate, List<Long> staffIds, Collection<String> statuses) {
        Criteria criteria=Criteria.where("unitId").is(unit).and("type").is(TodoType.APPROVAL_REQUIRED).and("shiftDate").gte(startDate).lt(endDate);
        if(ObjectUtils.isCollectionNotEmpty(staffIds)){
            criteria.and("staffId").in(staffIds);
        }
        if (ObjectUtils.isCollectionNotEmpty(statuses)) {
            criteria.and("status").in(statuses);
        }
        if(statuses.contains(TodoStatus.DISAPPROVE)){
            criteria.orOperator(Criteria.where("deleted").is("false"),criteria);
        }
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(criteria),
             Aggregation.lookup("shifts","entityId","_id","shifts"),
             Aggregation.project("id","status","staffId").and("shifts").arrayElementAt(0).as("shift"),
                Aggregation.match(new Criteria().and("shift").exists(true)),
                Aggregation.project("id","status","staffId").and("shift.startDate").as("shiftDateTime")
        );
        AggregationResults<TodoDTO> result = mongoTemplate.aggregate(aggregation, Todo.class, TodoDTO.class);
        return result.getMappedResults();

    }
}
