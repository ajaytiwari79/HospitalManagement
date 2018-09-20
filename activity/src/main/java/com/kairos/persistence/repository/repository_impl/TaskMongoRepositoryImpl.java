package com.kairos.persistence.repository.repository_impl;

import com.kairos.persistence.model.task.Task;
import com.kairos.wrapper.EscalatedTasksWrapper;
import com.kairos.wrapper.task.StaffAssignedTasksWrapper;
import com.kairos.wrapper.TaskCountWithAssignedUnit;
import com.kairos.wrapper.TaskWrapper;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.persistence.repository.task_type.CustomTaskMongoRepository;

import com.kairos.dto.planner.vrp.task.VRPTaskDTO;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

import static com.kairos.persistence.model.task.TaskStatus.CANCELLED;
import static java.time.ZoneId.systemDefault;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by prabjot on 1/6/17.
 */
@Repository
public class TaskMongoRepositoryImpl implements CustomTaskMongoRepository {

    private static final Logger logger = LoggerFactory.getLogger(TaskMongoRepositoryImpl.class);

    @Inject private MongoTemplate mongoTemplate;

    @Override
    public List<Task> getActualPlanningTask(long citizenId, Date fromDate, Date toDate) {


        Query query = new Query(Criteria.where("citizenId").is(citizenId).and("dateFrom").gte(fromDate).and("dateTo").lte(toDate).and("isDeleted").is(false).and("isSubTask").is(false));
        query.fields().exclude("actualPlanningTask").exclude("address");
        return mongoTemplate.find(query,Task.class);
    }


    public int deleteExceptionsFromTasks(long clientId, long unitId, List<BigInteger> exceptionIds){

        Query matchQuery = Query.query(Criteria.where("citizenId").is(clientId).and("unitId").is(unitId).and("isActive").is(true).and("isSubTask").is(false));
        Query removeQuery = Query.query(Criteria.where("id").in(exceptionIds));
        UpdateResult updateResult = mongoTemplate.updateMulti(matchQuery,new Update().pull("clientExceptions",removeQuery),Task.class);
        return (int)updateResult.getModifiedCount();

    }

    public List<Task> getTaskByException(long citizenId, long unitId, BigInteger exceptionId){
        Query matchQuery = Query.query(Criteria.where("citizenId").is(citizenId).and("unitId").is(unitId).and("isActive").is(true).and("isSubTask").is(false).
                and("clientExceptions").elemMatch(Criteria.where("id").is(exceptionId)));
        return mongoTemplate.find(matchQuery,Task.class);
    }

    public List<Task> getTasksByException(long citizenId, long unitId, List<BigInteger> exceptionIds){
        Query matchQuery = Query.query(Criteria.where("citizenId").is(citizenId).and("unitId").is(unitId).and("isActive").is(true).and("isSubTask").is(false).
                and("clientExceptions").elemMatch(Criteria.where("id").in(exceptionIds)));
        return mongoTemplate.find(matchQuery,Task.class);
    }

    @Override
    public List<StaffAssignedTasksWrapper> getStaffAssignedTasks(long unitId, long staffId, Date dateFrom, Date dateTo){

        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where("unitId").is(unitId).and("assignedStaffIds").is(staffId).and("dateFrom").gt(dateFrom).and("dateTo").lte(dateTo)
                        .and("isDeleted").is(false)),
                project("name","dateFrom","dateTo","address","duration","status","citizenId"),
                group("citizenId").push("$$ROOT").as("tasks"),
                sort(new Sort(Sort.DEFAULT_DIRECTION,"_id")));
        AggregationResults<StaffAssignedTasksWrapper> result = mongoTemplate.aggregate(aggregation,Task.class,StaffAssignedTasksWrapper.class);
        return result.getMappedResults();
    }

    @Override
    public List<BigInteger> updateTasksActiveStatusInBulk(List<BigInteger> taskIds, boolean makeActive) {
        Query query = new Query();
        logger.info("task ids to update"+taskIds);
        query.addCriteria(Criteria
                .where("_id").in(taskIds));

        Update update = new Update();
        update.set("isActive", makeActive);

// if use updateFirst, it will update 1004 only.
// mongoOperation.updateFirst(query4, update4, User.class);

        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, Task.class);
        if(updateResult.getModifiedCount() == taskIds.size()){
            return taskIds;
        }else{
            return Collections.emptyList();
        }
    }

    public List<Task> getTasksBetweenExceptionDates(long unitId, long citizenId, Date timeFrom, Date timeTo){
        Query query = Query.query(Criteria.where("citizenId").is(citizenId).and("unitId").is(unitId).and("isSubTask").is(false)
                .and("isActive").is(true).and("isDeleted").is(false).orOperator(Criteria.where("timeFrom").gte(timeFrom).lte(timeTo),
                        Criteria.where("timeTo").gte(timeFrom).lte(timeTo)));
        return mongoTemplate.find(query,Task.class);
    }

    public List<Task> getTasksBetweenExceptionDates(long unitId, List<Long> citizenId, Date timeFrom, Date timeTo){
        Query query = Query.query(Criteria.where("citizenId").in(citizenId).and("unitId").is(unitId).and("isSubTask").is(false)
                .and("isActive").is(true).and("isDeleted").is(false).orOperator(Criteria.where("timeFrom").gte(timeFrom).lte(timeTo),
                        Criteria.where("timeTo").gte(timeFrom).lte(timeTo)));
        return mongoTemplate.find(query,Task.class);
    }

    public List<EscalatedTasksWrapper> getStaffNotAssignedTasksGroupByCitizen(Long unitId){
        LocalDate now = LocalDate.now();
        Date dateFrom = Date.from(now.atStartOfDay(systemDefault()).toInstant());
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        Date dateTo = Date.from(now.plusDays(7-dayOfWeek.getValue()).atStartOfDay(systemDefault()).toInstant());
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where("unitId").is(unitId).and("assignedStaffIds").is(Collections.EMPTY_LIST).and("dateFrom").gt(dateFrom).and("dateTo").lte(dateTo)
                        .and("isDeleted").is(false).and("visitourId").ne(null)),
                project("name","dateFrom","dateTo","visitourId","duration","citizenId","timeFrom","timeTo"),
                group("citizenId").push("$$ROOT").as("tasks"),
                sort(new Sort(Sort.DEFAULT_DIRECTION,"_id")));
        AggregationResults<EscalatedTasksWrapper> result = mongoTemplate.aggregate(aggregation,Task.class,EscalatedTasksWrapper.class);
        return result.getMappedResults();
    }

    public List<TaskWrapper> getUnhandledTaskForMobileView(long citizenId,long unitId, Date dateFrom, Date dateTo,Sort sort){

        Query query = Query.query(Criteria.where("citizenId").is(citizenId).and("unitId").is(unitId)
                .and("clientExceptions").exists(true).and("timeFrom").gte(dateFrom).and("timeTo").lte(dateTo).and("isDeleted").is(false).and("isSubTask").is(false));
        query.with(sort);
        query.fields().include("timeFrom").include("timeTo").include("name").include("clientExceptions");
        return mongoTemplate.find(query,TaskWrapper.class,"tasks");
    }

    @Override
    public List<Task> getCitizenTasksGroupByUnitIds(Long citizenId, Date date, final Pageable pageable) {
        Query query = Query.query(Criteria.where("citizenId").is(citizenId).and("timeFrom").gte(date)).with(pageable);
        query.fields().include("visitourId");
        return mongoTemplate.find(query,Task.class);
    }

    @Override
    public TaskCountWithAssignedUnit countOfTasksAfterDateAndAssignedUnits(Long citizenId, Date date) {
        MatchOperation matchOperation = match(Criteria.where("citizenId").is(citizenId).and("timeFrom").gte(date));
        GroupOperation groupOperation = group("unitId").count().as("totalTasks").addToSet("unitId").as("unitIds");
        Aggregation aggregation = Aggregation.newAggregation(matchOperation,groupOperation);
        AggregationResults<TaskCountWithAssignedUnit> taskCountWithAssignedUnits = mongoTemplate.aggregate(aggregation,Task.class, TaskCountWithAssignedUnit.class);
        return (taskCountWithAssignedUnits.getMappedResults().isEmpty())?null:taskCountWithAssignedUnits.getMappedResults().get(0);
    }

    @Override
    public void deleteTasksAfterDate(Long citizenId,Date date){
        Query query = Query.query(Criteria.where("citizenId").is(citizenId).and("timeFrom").gte(date));
        Update update = new Update();
        update.set("isDeleted",true);
        update.set("taskStatus",CANCELLED);
        mongoTemplate.updateMulti(query,update,Task.class);
    }

    @Override
    public void inactiveTasksAfterDate(Long citizenId, Date date) {
        Query query = Query.query(Criteria.where("citizenId").is(citizenId).and("timeFrom").gte(date));
        Update update = new Update();
        update.set("isActive",false);
        mongoTemplate.updateMulti(query,update,Task.class);
    }


    @Override
    public List<VRPTaskDTO> getAllTasksByUnitId(Long unitId){
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where("unitId").is(unitId).and("isDeleted").is(false)),
        lookup("task_types","taskTypeId","_id","taskType"),
                project("duration","unitId","address","installationNumber","citizenId","citizenName","taskType").and("taskType").arrayElementAt(0).as("taskType")
        );
        AggregationResults<VRPTaskDTO> results = mongoTemplate.aggregate(aggregation,Task.class, VRPTaskDTO.class);
        return results.getMappedResults();
    }

    @Override
    public Map<Long,BigInteger> getAllTasksInstallationNoAndTaskTypeId(Long unitId){
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where("unitId").is(unitId).and("isDeleted").is(false)),
                //lookup("task_types","taskTypeId","_id","taskType"),
                new CustomAggregationOperation(Document.parse("{ \"$project\" : { \"installationIdandtaskType\" : { \"$concat\" : [{$substr: [\"$installationNumber\",0,64]},\"$taskTypeId\"] } } }"))
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation,Task.class, Map.class);
        Map<Long,BigInteger> installationNoAndTaskTypeId = new HashMap<>();
        results.getMappedResults().forEach(t->installationNoAndTaskTypeId.put(new Long((String) t.get("installationIdandtaskType")),new BigInteger((String)t.get("_id"))));
        return installationNoAndTaskTypeId;
    }

}
