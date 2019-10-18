package com.kairos.persistence.repository.repository_impl;

import com.kairos.dto.planner.vrp.task.VRPTaskDTO;
import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.repository.common.CustomAggregationOperation;
import com.kairos.persistence.repository.task_type.CustomTaskMongoRepository;
import com.kairos.wrapper.EscalatedTasksWrapper;
import com.kairos.wrapper.TaskCountWithAssignedUnit;
import com.kairos.wrapper.TaskWrapper;
import com.kairos.wrapper.task.StaffAssignedTasksWrapper;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
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
    public static final String CITIZEN_ID = "citizenId";
    public static final String DATE_FROM = "dateFrom";
    public static final String DATE_TO = "dateTo";
    public static final String IS_DELETED = "isDeleted";
    public static final String IS_SUB_TASK = "isSubTask";
    public static final String ADDRESS = "address";
    public static final String UNIT_ID = "unitId";
    public static final String IS_ACTIVE = "isActive";
    public static final String CLIENT_EXCEPTIONS = "clientExceptions";
    public static final String DURATION = "duration";
    public static final String TASKS = "tasks";
    public static final String TIME_FROM = "timeFrom";
    public static final String TIME_TO = "timeTo";
    public static final String VISITOUR_ID = "visitourId";
    public static final String TASK_TYPE = "taskType";

    @Inject private MongoTemplate mongoTemplate;

    @Override
    public List<Task> getActualPlanningTask(long citizenId, Date fromDate, Date toDate) {


        Query query = new Query(Criteria.where(CITIZEN_ID).is(citizenId).and(DATE_FROM).gte(fromDate).and(DATE_TO).lte(toDate).and(IS_DELETED).is(false).and(IS_SUB_TASK).is(false));
        query.fields().exclude("actualPlanningTask").exclude(ADDRESS);
        return mongoTemplate.find(query,Task.class);
    }


    public int deleteExceptionsFromTasks(long clientId, long unitId, List<BigInteger> exceptionIds){

        Query matchQuery = Query.query(Criteria.where(CITIZEN_ID).is(clientId).and(UNIT_ID).is(unitId).and(IS_ACTIVE).is(true).and(IS_SUB_TASK).is(false));
        Query removeQuery = Query.query(Criteria.where("id").in(exceptionIds));
        UpdateResult updateResult = mongoTemplate.updateMulti(matchQuery,new Update().pull(CLIENT_EXCEPTIONS,removeQuery),Task.class);
        return (int)updateResult.getModifiedCount();

    }

    public List<Task> getTaskByException(long citizenId, long unitId, BigInteger exceptionId){
        Query matchQuery = Query.query(Criteria.where(CITIZEN_ID).is(citizenId).and(UNIT_ID).is(unitId).and(IS_ACTIVE).is(true).and(IS_SUB_TASK).is(false).
                and(CLIENT_EXCEPTIONS).elemMatch(Criteria.where("id").is(exceptionId)));
        return mongoTemplate.find(matchQuery,Task.class);
    }

    public List<Task> getTasksByException(long citizenId, long unitId, List<BigInteger> exceptionIds){
        Query matchQuery = Query.query(Criteria.where(CITIZEN_ID).is(citizenId).and(UNIT_ID).is(unitId).and(IS_ACTIVE).is(true).and(IS_SUB_TASK).is(false).
                and(CLIENT_EXCEPTIONS).elemMatch(Criteria.where("id").in(exceptionIds)));
        return mongoTemplate.find(matchQuery,Task.class);
    }

    @Override
    public List<StaffAssignedTasksWrapper> getStaffAssignedTasks(long unitId, long staffId, Date dateFrom, Date dateTo){

        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where(UNIT_ID).is(unitId).and("assignedStaffIds").is(staffId).and(DATE_FROM).gt(dateFrom).and(DATE_TO).lte(dateTo)
                        .and(IS_DELETED).is(false)),
                project("name", DATE_FROM, DATE_TO, ADDRESS, DURATION,"status", CITIZEN_ID),
                group(CITIZEN_ID).push("$$ROOT").as(TASKS),
                sort(new Sort(Sort.DEFAULT_DIRECTION,"_id")));
        AggregationResults<StaffAssignedTasksWrapper> result = mongoTemplate.aggregate(aggregation,Task.class,StaffAssignedTasksWrapper.class);
        return result.getMappedResults();
    }

    @Override
    public List<BigInteger> updateTasksActiveStatusInBulk(List<BigInteger> taskIds, boolean makeActive) {
        Query query = new Query();
        logger.info("task ids to update{}",taskIds);
        query.addCriteria(Criteria
                .where("_id").in(taskIds));

        Update update = new Update();
        update.set(IS_ACTIVE, makeActive);
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, Task.class);
        if(updateResult.getModifiedCount() == taskIds.size()){
            return taskIds;
        }else{
            return Collections.emptyList();
        }
    }

    public List<Task> getTasksBetweenExceptionDates(long unitId, long citizenId, Date timeFrom, Date timeTo){
        Query query = Query.query(Criteria.where(CITIZEN_ID).is(citizenId).and(UNIT_ID).is(unitId).and(IS_SUB_TASK).is(false)
                .and(IS_ACTIVE).is(true).and(IS_DELETED).is(false).orOperator(Criteria.where(TIME_FROM).gte(timeFrom).lte(timeTo),
                        Criteria.where(TIME_TO).gte(timeFrom).lte(timeTo)));
        return mongoTemplate.find(query,Task.class);
    }

    public List<Task> getTasksBetweenExceptionDates(long unitId, List<Long> citizenId, Date timeFrom, Date timeTo){
        Query query = Query.query(Criteria.where(CITIZEN_ID).in(citizenId).and(UNIT_ID).is(unitId).and(IS_SUB_TASK).is(false)
                .and(IS_ACTIVE).is(true).and(IS_DELETED).is(false).orOperator(Criteria.where(TIME_FROM).gte(timeFrom).lte(timeTo),
                        Criteria.where(TIME_TO).gte(timeFrom).lte(timeTo)));
        return mongoTemplate.find(query,Task.class);
    }

    public List<EscalatedTasksWrapper> getStaffNotAssignedTasksGroupByCitizen(Long unitId){
        LocalDate now = LocalDate.now();
        Date dateFrom = Date.from(now.atStartOfDay(systemDefault()).toInstant());
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        Date dateTo = Date.from(now.plusDays(7-dayOfWeek.getValue()).atStartOfDay(systemDefault()).toInstant());
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where(UNIT_ID).is(unitId).and("assignedStaffIds").is(Collections.emptyList()).and(DATE_FROM).gt(dateFrom).and(DATE_TO).lte(dateTo)
                        .and(IS_DELETED).is(false).and(VISITOUR_ID).ne(null)),
                project("name", DATE_FROM, DATE_TO, VISITOUR_ID, DURATION, CITIZEN_ID, TIME_FROM, TIME_TO),
                group(CITIZEN_ID).push("$$ROOT").as(TASKS),
                sort(new Sort(Sort.DEFAULT_DIRECTION,"_id")));
        AggregationResults<EscalatedTasksWrapper> result = mongoTemplate.aggregate(aggregation,Task.class,EscalatedTasksWrapper.class);
        return result.getMappedResults();
    }

    public List<TaskWrapper> getUnhandledTaskForMobileView(long citizenId,long unitId, Date dateFrom, Date dateTo,Sort sort){

        Query query = Query.query(Criteria.where(CITIZEN_ID).is(citizenId).and(UNIT_ID).is(unitId)
                .and(CLIENT_EXCEPTIONS).exists(true).and(TIME_FROM).gte(dateFrom).and(TIME_TO).lte(dateTo).and(IS_DELETED).is(false).and(IS_SUB_TASK).is(false));
        query.with(sort);
        query.fields().include(TIME_FROM).include(TIME_TO).include("name").include(CLIENT_EXCEPTIONS);
        return mongoTemplate.find(query,TaskWrapper.class, TASKS);
    }

    @Override
    public List<Task> getCitizenTasksGroupByUnitIds(Long citizenId, Date date, final Pageable pageable) {
        Query query = Query.query(Criteria.where(CITIZEN_ID).is(citizenId).and(TIME_FROM).gte(date)).with(pageable);
        query.fields().include(VISITOUR_ID);
        return mongoTemplate.find(query,Task.class);
    }

    @Override
    public TaskCountWithAssignedUnit countOfTasksAfterDateAndAssignedUnits(Long citizenId, Date date) {
        MatchOperation matchOperation = match(Criteria.where(CITIZEN_ID).is(citizenId).and(TIME_FROM).gte(date));
        GroupOperation groupOperation = group(UNIT_ID).count().as("totalTasks").addToSet(UNIT_ID).as("unitIds");
        Aggregation aggregation = Aggregation.newAggregation(matchOperation,groupOperation);
        AggregationResults<TaskCountWithAssignedUnit> taskCountWithAssignedUnits = mongoTemplate.aggregate(aggregation,Task.class, TaskCountWithAssignedUnit.class);
        return (taskCountWithAssignedUnits.getMappedResults().isEmpty())?null:taskCountWithAssignedUnits.getMappedResults().get(0);
    }

    @Override
    public void deleteTasksAfterDate(Long citizenId,Date date){
        Query query = Query.query(Criteria.where(CITIZEN_ID).is(citizenId).and(TIME_FROM).gte(date));
        Update update = new Update();
        update.set(IS_DELETED,true);
        update.set("taskStatus",CANCELLED);
        mongoTemplate.updateMulti(query,update,Task.class);
    }

    @Override
    public void inactiveTasksAfterDate(Long citizenId, Date date) {
        Query query = Query.query(Criteria.where(CITIZEN_ID).is(citizenId).and(TIME_FROM).gte(date));
        Update update = new Update();
        update.set(IS_ACTIVE,false);
        mongoTemplate.updateMulti(query,update,Task.class);
    }


    @Override
    public List<VRPTaskDTO> getAllTasksByUnitId(Long unitId){
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where(UNIT_ID).is(unitId).and(IS_DELETED).is(false)),
        lookup("task_types","taskTypeId","_id", TASK_TYPE),
                project(DURATION, UNIT_ID, ADDRESS,"installationNumber", CITIZEN_ID,"citizenName", TASK_TYPE).and(TASK_TYPE).arrayElementAt(0).as(TASK_TYPE)
        );
        AggregationResults<VRPTaskDTO> results = mongoTemplate.aggregate(aggregation,Task.class, VRPTaskDTO.class);
        return results.getMappedResults();
    }

    @Override
    public Map<Long,BigInteger> getAllTasksInstallationNoAndTaskTypeId(Long unitId){
        Aggregation aggregation = Aggregation.newAggregation(match(Criteria.where(UNIT_ID).is(unitId).and(IS_DELETED).is(false)),
                //lookup("task_types","taskTypeId","_id","taskType"),
                new CustomAggregationOperation(Document.parse("{ \"$project\" : { \"installationIdandtaskType\" : { \"$concat\" : [{$substr: [\"$installationNumber\",0,64]},\"$taskTypeId\"] } } }"))
        );
        AggregationResults<Map> results = mongoTemplate.aggregate(aggregation,Task.class, Map.class);
        Map<Long,BigInteger> installationNoAndTaskTypeId = new HashMap<>();
        results.getMappedResults().forEach(t->installationNoAndTaskTypeId.put(new Long((String) t.get("installationIdandtaskType")),new BigInteger((String)t.get("_id"))));
        return installationNoAndTaskTypeId;
    }

}
