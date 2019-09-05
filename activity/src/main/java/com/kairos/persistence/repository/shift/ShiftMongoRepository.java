package com.kairos.persistence.repository.shift;

import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.CustomShiftMongoRepository;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 30/8/17.
 */
@Repository
public interface ShiftMongoRepository extends MongoBaseRepository<Shift, BigInteger>, CustomShiftMongoRepository {

    @Query(value = "{employmentId:?0,deleted:false,disabled:false,startDate:{$gte:?1,$lte:?2}}", fields = "{ 'startDate' : 1, 'endDate' : 1,'employmentId':1,'activities':1}")
    List<ShiftDTO> findAllShiftBetweenDuration(Long employmentId, Date startDate, Date endDate);




    @Query(value = "{'planningPeriodId':{'$in':?2},employmentId:?0,staffId:?1,unitId:?3,deleted:false,disabled:false,draftShift:{$exists:true}}")
    List<Shift> getAllDraftShiftBetweenDuration(Long employmentId,Long staffId,List<BigInteger> planningPeriodIds,Long unitId);

    List<Shift> findByExternalIdIn(List<String> externalIds);


    @Query(value = "{employmentId:?0,deleted:false, disabled:false,startDate: {$lt: ?2},endDate:{$gt:?1}}")
    List<Shift> findShiftBetweenDurationByEmploymentId(Long employmentId, Date startDate, Date endDate);

    @Query(value = "{staffId:?0,deleted:false, disabled:false,startDate: {$lt: ?2},endDate:{$gt:?1}}")
    List<Shift> findShiftBetweenDurationByStaffId(Long staffId, Date startDate, Date endDate);

    @Query("{'deleted':false,'unitId':?2, 'disabled':false, startDate: {$lte: ?1},endDate:{$gte:?0},'$or':[{draftShift:{$exists:true},{draft:true}]}")
    List<Shift> findDraftShiftBetweenDurationAndUnitIdAndDeletedFalse(Date startDate, Date endDate, Long unitId);

    @Query("{'deleted':false,'unitId':?2, 'disabled':false, startDate: {$lte: ?1},endDate:{$gte:?0}}")
    List<Shift> findShiftBetweenDurationAndUnitIdAndDeletedFalse(Date startDate, Date endDate, Long unitId);


    @Query("{'deleted':false,'unitId':?2, 'disabled':false, '_id':{'$ne':?3},'startDate':{$lt:?1} , 'endDate': {$gt:?0}}")
    List<Shift> findShiftBetweenDurationAndUnitIdAndDeletedFalseAndIdNotEqualTo(Date startDate, Date endDate, Long unitId,BigInteger shiftId);

    List<Shift> findAllByIdInAndDeletedFalseOrderByStartDateAsc(List<BigInteger> shiftIds);

    @Query("{'employmentId':{'$in':?0},'deleted':false, 'disabled':false,'$or':[{'startDate':{$gte:?1,$lte:?2}},{'endDate':{$gte:?1,$lte:?2}}]}")
    List<Shift> findShiftBetweenDurationByEmploymentIds(List<Long> employmentIds, Date startDate, Date endDate);

    @Query("{deleted:false,staffId:{$in:?0}, 'disabled':false, startDate:{$gte:?1,$lte:?2}}")
    List<Shift> findAllShiftsByStaffIds(List<Long> staffIds, Date startDate, Date endDate);

    @Query("{deleted:false,staffId:{$in:?0}, 'disabled':false, 'startDate':{$lt:?2} , 'endDate': {$gt:?1}}")
    List<Shift> findAllShiftsByStaffIdsAndDate(List<Long> staffIds, LocalDateTime startDate, LocalDateTime endDate);

    @Query("{'deleted':false, 'disabled':false, 'staffId':{'$in':?0},startDate: {$lt: ?2},endDate:{$gt:?1}}")
    List<Shift> findShiftByStaffIdsAndDate(List<Long> staffids,Date startDate, Date endDate);


    @Query("{'deleted':false,'disabled':false, 'unitId':?2,'startDate':{$lt:?1} , 'endDate': {$gt:?0}}")
    List<Shift> findShiftBetweenDurationAndUnitIdAndDeletedFalse(LocalDateTime startDate, LocalDateTime endDate, Long unitId);

   @Query("{'deleted':false, 'unitId':?1, 'startDate':{$gte:?2}, 'employmentId':?0, '$or':[{disabled:true},{sickShift:true}] }")
    List<Shift> findAllDisabledOrSickShiftsByEmploymentIdAndUnitId(Long employmentId, Long unitId, LocalDate startDate);

    @Query("{deleted:false, disabled:false, planningPeriodId:?0,unitId:?1}")
    List<Shift> findAllShiftsByPlanningPeriod(BigInteger planningPeriodId, Long unitId);

    List<Shift> findAllByStaffIdInAndSickShiftTrueAndDeletedFalseAndStartDateGreaterThanEqualAndEndDateLessThanEqual(Set<Long> staffIds, Date startDate, Date endDate);

    List<Shift> findAllByDeletedFalse();

    @Query("{deleted:false,employmentId:?0,disabled:false,startDate:{$lte:?2},endDate:{$gte:?1}}")
    List<Shift> findAllOverlappedShiftsAndEmploymentId(Long employmentId, Date startDate, Date endDate);

    @Query(value = "{disabled:false,deleted:false,planningPeriodId:{$exists:false},shiftType:?0}")
    List<Shift> findAllAbsenceShifts(String shiftType);

    @Query(value = "{disabled:false,deleted:false,unitId:?0}")
    List<Shift> findAllByUnitId(Long unitId);

    @Query(value = "{employmentId:?0,deleted:false,'_id':{'$ne':?3}, disabled:false,startDate: {$lt: ?2},endDate:{$gt:?1}}",exists=true)
    boolean shiftOverLapped(Long employmentId, Date startDate, Date endDate,BigInteger shiftId);

    @Query(value = "{employmentId:?0,deleted:false, disabled:false,startDate:{$gt:?1,$lt:?2},'activities.activityId':{'$in':?3}}")
    List<Shift> findAllShiftByActivityIdAndBetweenDuration(Long employmentId, Date startDate, Date endDate,Collection<BigInteger> activityIds);

    @Query("{deleted:false,employmentId:?0, 'disabled':false, startDate:{$gte:?1,$lt:?2}}")
    List<Shift> findAllShiftsByEmploymentIdBetweenDate(Long employmentId, Date startDate, Date endDate);
}
