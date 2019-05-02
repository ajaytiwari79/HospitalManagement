package com.kairos.persistence.repository.repository_impl;

import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.persistence.repository.task_type.CustomTaskDemandMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import static java.time.ZoneId.systemDefault;

/**
 * Created by oodles on 10/8/17.
 */
@Repository
public class TaskDemandMongoRepositoryImpl implements CustomTaskDemandMongoRepository {

    @Inject
    private MongoTemplate mongoTemplate;



    public List<TaskDemand> getTaskDemandWhichTaskCreatedTillDateNotNull(){
        LocalDate now = LocalDate.now();
        Date fromDate = Date.from(now.plusMonths(1).atStartOfDay(systemDefault()).toInstant());
        Criteria criteria  = Criteria.where("taskCreatedTillDate").lte(fromDate);
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(criteria)
        );
        AggregationResults<TaskDemand> finalResult =
                mongoTemplate.aggregate(aggregation, TaskDemand.class, TaskDemand.class);
        /*DBCollection collection = mongoTemplate.getCollection("task_demands");
        DBCursor myCursor=collection.find(new BasicDBObject("taskCreatedTillDate",fromDate).append("isDeleted", false));
        return myCursor;*/
        return finalResult.getMappedResults();
    }
}
