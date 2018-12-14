package com.kairos.persistence.repository.task_type;

import com.kairos.persistence.model.task.Task;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.joda.time.DateTime;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Created by prabjot on 2/11/16.
 */
@Repository
public interface TaskMongoRepository extends MongoBaseRepository<Task,BigInteger>,CustomTaskMongoRepository {

    List<Task> findByCitizenId(Long citizenId);
    List<Task> findAllByTaskDemandIdAndIsDeleted(String taskDemandId, boolean isDeleted, Sort sort);
    Task findByExternalId(String externalId);

    Task findByExternalIdAndIsActive(String externalId, Boolean isActive);


    List<Task> findByStaffIdAndUnitId(Long staffId, Long organizationId);

    /**
     * @desc This method return the list of tasks created by Pre Kairos (Beacons) to sync the visitour
     * */
    @Query("{'taskOriginator':'PRE_KAIROS', 'executionDate':{'$exit':false}, 'taskStatus':{'$ne':'CANCELLED'}}")
    List<Task> listPreKairosTasksToPlan();

    @Query(value="{'citizenId' : ?0, 'dateFrom' : {$gte:?1},'dateTo' : {$lte:?2}, 'isSubTask' : false, 'isDeleted' : false,'taskStatus':{'$ne':'CANCELLED'},'relatedTaskId':{'$exists':false}}")
    List<Task> findAllBetweenDates(long citizenId, Date startDate, Date endDate);

    List<Task> findByStaffAnonymousId(Long staffAnonymousId);

    List<Task> findByIdIn(List<BigInteger> taskIds, Sort sort);

    @Query("{ _id : {'$in': ?0} }")
    List<Task> getAllTasksByIdsIn(List<String> taskIds);

    @Query("{ _id : {'$in': ?0}, 'dateFrom':{'$gte': ?1}, 'dateTo':{'$lte': ?2} }")
    List<Task> getAllTasksByIdsInAndDateRange(List<String> taskIds, Date from, Date to);

    @Query(value="{'staffId' : ?0,'startDate' : {$gte:?1, $lte:?2} }")
    List<Task> findByStaffIdAndStartDate(Long staffId, DateTime startDateFrom, DateTime startDateTo, Sort sort);

    @Query(value="{'staffId' : ?0,'startDate' : {$gte:?1, $lte:?2} }")
    List<Task> getStaffIdAndStartDate(Long staffId, Date startDateFrom, Date startDateTo, Sort sort);


    List<Task> findByStaffIdAndStartDateBetween(long staffId, Date startDateFrom, Date startDateTo, Sort sort);


    Task findByParentTaskId(BigInteger id);

    @Query(value="{$or:[{taskOriginator:'ACTUAL_PLANNING'},{hasActualTask:false}],'citizenId' : ?0, 'dateFrom' : {$gte:?1},'dateTo' : {$lte:?2}, 'isSubTask' : false, 'isDeleted' : false}")
    List<Task> getActualPlanningTask(long citizenId, Date startDate, Date endDate);

    @Query("{ joinEventId : ?0 , 'dateFrom' : {$gte:?1}, 'isDeleted' : false }")
    List<Task> getTaskRepetitionsByEventIdAndAndStartDate(String joinEventId, Date dateFrom);

    @Query("{ 'taskDemandId' : ?0 ,'dateTo':{'$gt': ?1} }, 'isDeleted' : false")
    List<Task> getTasksByDemandIdAndDateTo(String taskDemandId, Date dateTo);

    @Query("{ 'relatedTaskId' : ?0 }, 'isDeleted' : false")
    List<Task> getRelatedMultiStaffTasks(String taskId);

    @Query("{ 'unitId' : ?0 ,'dateFrom' : {$gte:?1},'dateTo' : {$lte:?2}},'isDeleted' : false }")
    List<Task> getTaskBetweenDatesForUnit(long unitId, Date startDate, Date endDate);

    @Query("{unitId:?0 ,citizenId:{$in:?1},timeFrom:{$gte:?2,$lte:?3},isSubTask:false,isActive:true,isDeleted:false }")
    List<Task> getPrePlanningTaskBetweenExceptionDates(long unitId, List<Long> citizenId, Date timeFrom, Date timeTo);

    Task findBySubTaskIdsAndIsDeleted(String subTaskId, boolean isDeleted);

    @Query(value = "{isActive:true,isDeleted:false,clientExceptions: { $exists: true, $ne: [] },timeFrom:{$gte:?0},timeTo:{$lte:?1}}",fields="{citizenId:1,unitId:1,timeFrom:1,timeTo:1}")
    List<Task> getUnhandledTaskBetweenDates(Date dateFrom, Date dateTo, Sort sort);

    Task findByKmdExternalId(String kmdExternalId);

    @Query(value="{'citizenId' : ?0, unitId:?1, 'taskDemandId' : ?2, timeFrom :{$gte:?3,$lt:?4}, 'isSubTask' : false, 'isDeleted' : false,'taskStatus':{'$ne':'CANCELLED'},'relatedTaskId':{'$exists':false}}")
    List<Task> getTasksByCitizenIdAndUnitIdAndByTaskDemandIdAndBetweenDates(long citizenId, long unitId, String taskDemandId, Date timeFrom, Date timeTo);

    @Query(value="{'taskDemandId' : ?0, 'dateFrom' : {$gte:?1},'dateTo' : {$lte:?2}, 'isSubTask' : false, 'isDeleted' : false,'taskStatus':{'$ne':'CANCELLED'},'relatedTaskId':{'$exists':false}}")
    List<Task> findAllByTaskDemandAndBetweenDates(long taskDemandId, Date startDate, Date endDate);

    @Query(value = "{ 'taskDemandId' : ?0 , 'taskDemandVisitId' : ?1, dateFrom:{$gte:?2,$lte:?3}, 'isDeleted' : false}")
    Task getTaskByDemandIdAndVisitIdAndBetweenDates(String taskDemandId, BigInteger taskDemandVisitId, Date dateFrom, Date dateTo);

    @Query("{citizenId:?0,taskTypeId:?1}")
    List<Task> findAllBycitizenIdAndTaskTypeId(Long citizenId,BigInteger taskTypeId);

}
