package com.kairos.persistence.repository.todo;

import com.kairos.commons.utils.ObjectUtils;
import com.kairos.dto.activity.todo.TodoDTO;
import com.kairos.enums.shift.TodoStatus;
import com.kairos.enums.todo.TodoType;
import com.kairos.persistence.model.counter.KPI;
import com.kairos.persistence.model.todo.Todo;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import javax.inject.Inject;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class TodoRepositoryImpl implements CustomTodoRepository {

    public static final String STAFF_ID = "staffId";
    public static final String STATUS = "status";
    public static final String SHIFTS = "shifts";
    @Inject
    MongoTemplate mongoTemplate;


    @Override
    public List<TodoDTO> findAllByKpiFilter(Long unit, Date startDate, Date endDate, List<Long> staffIds, Collection<String> statuses) {
        Criteria criteria=Criteria.where("unitId").is(unit).and(STAFF_ID).in(staffIds).and("type").is(TodoType.APPROVAL_REQUIRED).and("shiftDate").gte(startDate).lt(endDate);
        if (ObjectUtils.isCollectionNotEmpty(statuses)) {
            criteria.and(STATUS).in(statuses);
        }
        if(statuses.contains(TodoStatus.DISAPPROVE.toString())){
            criteria.orOperator(Criteria.where("deleted").is("false"),criteria);
        }
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.match(criteria),
             Aggregation.lookup(SHIFTS,"entityId","_id", SHIFTS),
             Aggregation.project("id", STATUS, STAFF_ID).and(SHIFTS).arrayElementAt(0).as("shift"),
                Aggregation.project("id", STATUS, STAFF_ID).and("shift.startDate").as("shiftDateTime")
        );
        AggregationResults<TodoDTO> result = mongoTemplate.aggregate(aggregation, Todo.class, TodoDTO.class);
        return result.getMappedResults();

    }
}
