package com.kairos.persistence.repository.shift;



import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftQueryResult;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.CustomShiftMongoRepository;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by vipul on 30/8/17.
 */
@Repository
public interface ShiftMongoRepository extends MongoBaseRepository<Shift, BigInteger>, CustomShiftMongoRepository {

    @Query(value = "{unitPositionId:?0,deleted:false,disabled:false,startDate:{$gte:?1,$lte:?2}}", fields = "{ 'startDate' : 1, 'endDate' : 1,'unitPositionId':1}")
    List<ShiftDTO> findAllShiftBetweenDuration(Long unitPositionId, Date startDate, Date endDate);


    List<Shift> findByExternalIdIn(List<String> externalIds);

    @Query(value = "{unitPositionId:?0,deleted:false, disabled:false,startDate: {$lt: ?2},endDate:{$gt:?1}}")
    List<Shift> findShiftBetweenDurationByUnitPosition(Long unitPositionId, Date startDate, Date endDate);

    @Query(value = "{_id: {$ne:?0} ,unitPositionId:?1,deleted:false, disabled:false,startDate: {$lt: ?2},endDate:{$gt:?3}}")
    List<Shift> findShiftBetweenDurationByUnitPositionNotEqualToShiftId(BigInteger shiftId,Long unitPositionId, Date startDate, Date endDate);

    @Query(value = "{staffId:?0,deleted:false, disabled:false,startDate: {$lt: ?2},endDate:{$gt:?1}}")
    List<Shift> findShiftBetweenDurationBystaffId(Long staffId, Date startDate, Date endDate);

    @Query("{'deleted':false,'unitId':?2, 'disabled':false, 'startDate':{$lt:?1} , 'endDate': {$gt:?0}}")
    List<Shift> findShiftBetweenDuration(Date startDate, Date endDate,Long unitId);


    List<Shift> findAllByIdInAndDeletedFalseOrderByStartDateAsc(List<BigInteger> shiftIds);

    @Query("{'unitPositionId':{'$in':?0},'deleted':false, 'disabled':false,'$or':[{'startDate':{$gte:?1,$lte:?2}},{'endDate':{$gte:?1,$lte:?2}}]}")
    List<Shift> findShiftBetweenDurationByUnitPositions(List<Long> unitPositionIds, Date startDate, Date endDate);

    @Query("{deleted:false,staffId:{$in:?0}, 'disabled':false, startDate:{$gte:?1,$lte:?2}}")
    List<Shift> findAllShiftsByStaffIds(List<Long> staffIds, Date startDate, Date endDate);

    @Query("{deleted:false, _id:{'$in':?0}}")
    List<Shift> findAllByIds(List<String> shiftIds);

    @Query("{'deleted':false, 'disabled':false, 'staffId':{'$in':?0},startDate: {$lt: ?2},endDate:{$gt:?1}}")
    List<Shift> findShiftByStaffIdsAndDate(List<Long> staffids,Date startDate, Date endDate);


    @Query("{'deleted':false,'disabled':false, 'unitId':?2,'startDate':{$lt:?1} , 'endDate': {$gt:?0}}")
    List<Shift> findShiftBetweenDuration(LocalDateTime startDate, LocalDateTime endDate, Long unitId);

   @Query("{'deleted':false, 'unitId':?1, 'startDate':{$gte:?2}, 'unitPositionId':?0, '$or':[{disabled:true},{sickShift:true}] }")
    List<Shift> findAllDisabledOrSickShiftsByUnitPositionIdAndUnitId(Long unitPositionId,Long unitId, LocalDate startDate);

    @Query("{deleted:false, disabled:false, planningPeriodId:?0,unitId:?1}")
    List<Shift> findAllShiftsByPlanningPeriod(BigInteger planningPeriodId, Long unitId);

    List<Shift> findAllByStaffIdInAndSickShiftTrueAndDeletedFalseAndStartDateGreaterThanEqualAndEndDateLessThanEqual(Set<Long> staffIds, Date startDate, Date endDate);

    @Query("{deleted:false,staffId:{$in:?0}}")
    List<Shift> findAllShiftByIds(List<BigInteger> shiftIds);
}
