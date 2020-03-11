package com.kairos.repositories.task_type;


import com.kairos.persistence.model.task_demand.TaskDemand;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by prabjot on 2/11/16.
 */
@Repository
public interface TaskDemandMongoRepository extends MongoRepository<TaskDemand,BigInteger>, CustomTaskDemandMongoRepository {




    List<TaskDemand> findByTaskTypeId(ObjectId taskTypeId);
    List<TaskDemand> findByIdIn(List<String> demandIds);
    List<TaskDemand> findByCreatedByStaffId(Long staffId);
    List<TaskDemand> findByTaskTypeIdIn(List<String> demandIds);

    TaskDemand findByCitizenIdAndTaskTypeId(long citizenId,BigInteger taskTypeId);

    @Query(value="{ 'unitId' : ?0 , 'citizenId' : ?1, 'startDate' : {$gte:?2},'endDate' : {$lte:?3}, 'isDeleted' : false }")
    List<TaskDemand> findAllBetweenDates(long unitId, long citizenId, Date startDate, Date endDate);


    @Query(value="{ 'unitId' : ?0 , 'citizenId' : ?1, 'startDate' : {$gte:?2}, 'isDeleted' : false }")
    List<TaskDemand> findAllAfterDate(long unitId, long citizenId, Date startDate);

    @Query(value="{ 'unitId' : ?0 , 'citizenId' : ?1, 'endDate' : {$lte:?2}, 'isDeleted' : false }")
    List<TaskDemand> findAllBeforeDate(long unitId, long citizenId, Date endDate);

    @Query(value="{ 'citizenId' : ?0 , 'unitId' : ?1, 'isDeleted' : false }")
    List <TaskDemand> findAllByCitizenIdAndUnitId(Long citizenId, Long unitId);

    @Query(value="{ 'id' : ?0, 'unitId' : ?1, 'isDeleted' : ?2 }")
    TaskDemand findByTaskDemandIdAndUnitIdAndIsDeleted(String taskDemandId, Long unitId, boolean isDeleted);

    @Query(value="{ 'citizenId' : ?0 , 'unitId' : ?1, 'status' : {$ne:?2}, 'isDeleted' : ?3, $or:[{'endDate':{$gte:?4}},{'endDate':{'$exists':false}}] }")
    List<TaskDemand> getByCitizenIdAndUnitIdAndStatusNotAndIsDeletedAndEndDateGreaterOrNull(Long citizenId, Long unitId, TaskDemand.Status demandStatus, boolean isDeleted, Date upcomingMonday);

    List<TaskDemand> findByCitizenIdInAndTaskTypeIdAndIsDeleted(List<Long> citizenIds, String taskTypeId,boolean isDeleted);


    @Query(value="{ 'kmdExternalId' : ?0 }")
    TaskDemand findByKmdExternalId(String kmdExternalId);

    @Query(value="{ 'unitId' : ?0 , 'status' : ?1, 'endDate' : {$gte:?2}, 'weekdayVisits.preferredHour':{$ne:''},'isDeleted' : false }")
    List<TaskDemand> getByUnitIdAndStatusBetweenDates(long unitId, TaskDemand.Status status, Date endDate);

   /* @Query(value="{ 'unitId' : ?0 , 'status' : ?1, 'startDate' : {$gte:?2},'isDeleted' : false }")
    List<TaskDemand> getByUnitIdAndStatusBetweenDates(long unitId, TaskDemand.Status status, Date startDate);*/

    @Query(value="{ 'citizenId' : ?0 , 'status' : ?1, 'unitId' : ?2 }")
    List<TaskDemand> getByCitizenIdAndStatusAndUnitId(long citizenId, TaskDemand.Status status, Long unitId);

    @Query(value="{ 'citizenId' : ?0 , 'unitId' : ?1, 'isDeleted' : false, 'recurrencePattern' : ?2 }")
    List <TaskDemand> findAllByCitizenIdAndUnitIdAndRecurrencePattern(Long citizenId, Long unitId, TaskDemand.RecurrencePattern recurrencePattern);

}
