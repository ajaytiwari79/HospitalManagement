package com.kairos.persistence.repository.repository_impl;

import com.kairos.dto.activity.task_type.TaskTypeResponseDTO;
import com.kairos.persistence.model.task_demand.TaskDemand;
import com.kairos.persistence.model.task_type.TaskType;
import com.kairos.persistence.repository.task_type.CustomTaskTypeRepository;
import com.kairos.dto.user.staff.client.ClientFilterDTO;
import com.kairos.wrapper.OrgTaskTypeAggregateResult;
import com.kairos.wrapper.TaskTypeAggregateResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

/**
 * Created by prabjot on 16/5/17.
 */
@Repository
public class CustomTaskTypeRepositoryImpl implements CustomTaskTypeRepository {

    @Inject
    private MongoTemplate mongoTemplate;

//    @Inject
//    private TaskTypeService taskTypeService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public List<TaskTypeAggregateResult> getTaskTypesOfCitizens(List<Long> citizenIds) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria
                        .where("citizenId").in(citizenIds).and("isDeleted").is(false)),
                group("citizenId").addToSet("taskTypeId").as("taskTypeIds")
        );
        AggregationResults<TaskTypeAggregateResult> result = mongoTemplate.aggregate(aggregation, TaskDemand.class,TaskTypeAggregateResult.class);
        return result.getMappedResults();
    }

    @Override
    public List<OrgTaskTypeAggregateResult> getTaskTypesOfUnit(long unitId) {
        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("unitId").is(unitId)),
                lookup("task_types","taskTypeId","_id","task_types"),
                unwind("task_types"),
                group("task_types._id").first("task_types.title").as("title")
        );
        AggregationResults<OrgTaskTypeAggregateResult> result = mongoTemplate.aggregate(aggregation, TaskDemand.class,OrgTaskTypeAggregateResult.class);
        return result.getMappedResults();
    }

    @Override
    public void updateUnitTaskTypesStatus(BigInteger taskTypeId, boolean status) {
        Query query = Query.query(Criteria.where("rootId").is(taskTypeId));
        Update update = new Update();
        update.set("isEnabled",status);
        mongoTemplate.updateMulti(query, update,TaskType.class);
    }


    public List<TaskTypeAggregateResult> getCitizenTaskTypesOfUnit(Long unitId, ClientFilterDTO clientFilterDTO, List<String> taskTypeIdsByServiceIds) {
        Criteria criteria = Criteria.where("unitId").is(unitId).and("isDeleted").is(false);
        Criteria c = new Criteria();
        Set taskTypesSet = new HashSet();
        List<BigInteger> taskTypes = new ArrayList();
        taskTypesSet.addAll(clientFilterDTO.getTaskTypes());
        if(clientFilterDTO.isNewDemands()){
            criteria.and("status").ne(TaskDemand.Status.VISITATED);
        }

        /*if(!clientFilterDTO.getServicesTypes().isEmpty()){
            List<String> taskTypeIdsByServiceIds = taskTypeService.getTaskTypeIdsByServiceIds(clientFilterDTO.getServicesTypes(),unitId);
            taskTypesSet.addAll(taskTypeIdsByServiceIds);
        }*/
        taskTypesSet.addAll(taskTypeIdsByServiceIds);
        taskTypes.addAll(taskTypesSet);
        logger.info("taskTypes----------> "+taskTypes);
        if(!taskTypes.isEmpty()){
          c =  c.where("taskTypeIds").in(taskTypes);
        }
        if(!clientFilterDTO.getTimeSlots().isEmpty()){
            c.andOperator(Criteria.where("weekDayTimeSlotIds").in(clientFilterDTO.getTimeSlots()).orOperator(Criteria.where("weekEndTimeSlotIds").in(clientFilterDTO.getTimeSlots())));
        }

        Aggregation aggregation = Aggregation.newAggregation(

                match(
                        criteria
                ),
                group("citizenId").addToSet("taskTypeId").as("taskTypeIds").addToSet("weekdayVisits.timeSlotId").as("weekDayTimeSlotIds").addToSet("weekendVisits.timeSlotId").as("weekEndTimeSlotIds"),
                unwind("$taskTypeIds"),
                unwind("$weekDayTimeSlotIds"),
                unwind("$weekEndTimeSlotIds"),
                match(
                        c
                )

        );
        AggregationResults<TaskTypeAggregateResult> result = mongoTemplate.aggregate(aggregation, TaskDemand.class,TaskTypeAggregateResult.class);
        return result.getMappedResults();
    }

//    @Query(value="{'isEnabled':true,'organizationId':0}")
    @Override
    public List<TaskTypeResponseDTO> getAllTaskType(){

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("isEnabled").is(true).and("organizationId").is(0)),
                unwind("tags", true),
                lookup("tag","tags","_id","tags_data"),
                unwind("tags_data",true),
                group("$id")
                        .first("$title").as("title")
                        .first("$description").as("description")
                        .first("$subServiceId").as("subServiceId")
                        .first("$expiresOn").as("expiresOn")
                        .first("$isEnabled").as("status")
                        .first("$rootId").as("parentTaskTypeId")
                        .push("tags_data").as("tags")
        );

        AggregationResults<TaskTypeResponseDTO> result = mongoTemplate.aggregate(aggregation, TaskType.class,TaskTypeResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<TaskTypeResponseDTO> getAllTaskTypeBySubServiceAndOrganizationAndIsEnabled(long subServiceId, long organizationId, boolean isEnabled){

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("isEnabled").is(true).and("organizationId").is(organizationId).and("subServiceId").is(subServiceId)),
                unwind("tags", true),
                lookup("tag","tags","_id","tags_data"),
                unwind("tags_data",true),
                group("$id")
                        .first("$title").as("title")
                        .first("$description").as("description")
                        .first("$subServiceId").as("subServiceId")
                        .first("$expiresOn").as("expiresOn")
                        .first("$isEnabled").as("status")
                        .first("$rootId").as("parentTaskTypeId")
                        .push("tags_data").as("tags")
        );

        AggregationResults<TaskTypeResponseDTO> result = mongoTemplate.aggregate(aggregation, TaskType.class,TaskTypeResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<TaskTypeResponseDTO> getAllTaskTypeByTeamIdAndSubServiceAndIsEnabled(long teamId, long subServiceId, boolean isEnabled){

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("isEnabled").is(true).and("teamId").is(teamId).and("subServiceId").is(subServiceId)),
                unwind("tags", true),
                lookup("tag","tags","_id","tags_data"),
                unwind("tags_data",true),
                group("$id")
                        .first("$title").as("title")
                        .first("$description").as("description")
                        .first("$subServiceId").as("subServiceId")
                        .first("$expiresOn").as("expiresOn")
                        .first("$isEnabled").as("status")
                        .first("$rootId").as("parentTaskTypeId")
                        .push("tags_data").as("tags")
        );

        AggregationResults<TaskTypeResponseDTO> result = mongoTemplate.aggregate(aggregation, TaskType.class,TaskTypeResponseDTO.class);
        return result.getMappedResults();
    }

    @Override
    public List<TaskTypeResponseDTO> findAllBySubServiceIdAndOrganizationId(long subServiceId, long organizationId){

        Aggregation aggregation = Aggregation.newAggregation(
                match(Criteria.where("isEnabled").is(true).and("subServiceId").is(subServiceId).and("organizationId").is(0)),
                unwind("tags", true),
                lookup("tag","tags","_id","tags_data"),
                unwind("tags_data",true),
                group("$id")
                        .first("$title").as("title")
                        .first("$description").as("description")
                        .first("$subServiceId").as("subServiceId")
                        .first("$expiresOn").as("expiresOn")
                        .first("$isEnabled").as("status")
                        .first("$rootId").as("parentTaskTypeId")
                        .push("tags_data").as("tags")
        );

        AggregationResults<TaskTypeResponseDTO> result = mongoTemplate.aggregate(aggregation, TaskType.class,TaskTypeResponseDTO.class);
        return result.getMappedResults();
    }
}
