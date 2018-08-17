package com.kairos.persistence.repository.shift;


import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.persistence.model.activity.Shift;
import com.kairos.persistence.repository.activity.CustomShiftMongoRepository;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by vipul on 30/8/17.
 */
@Repository
public interface ShiftMongoRepository extends MongoBaseRepository<Shift, BigInteger>, CustomShiftMongoRepository {

    @Query(value = "{unitPositionId:?0,deleted:false,isMainShift:true,startDate:{$gte:?1,$lte:?2}}", fields = "{ 'startDate' : 1, 'endDate' : 1,'unitPositionId':1}")
    List<ShiftQueryResult> findAllShiftBetweenDuration(Long unitPositionId, Date startDate, Date endDate);

    Long countByActivityId(BigInteger activityId);

    List<Shift> findByExternalIdIn(List<String> externalIds);

    @Query("{'unitPositionId':?0,'deleted':false,'isMainShift':true,'$or':[{'startDate': {$gte:?1,$lt: ?2}},{'endDate':{$gt:?1,$lte:?2}}]}")
    List<Shift> findShiftBetweenDurationByUnitPosition(Long unitPositionId, Date startDate, Date endDate);

    @Query("{'deleted':false,'unitId':?2, 'isMainShift':true, 'startDate':{$lt:?1} , 'endDate': {$gt:?0}}")
    List<Shift> findShiftBetweenDuration(Date startDate, Date endDate,Long unitId);


    List<Shift> findAllByIdInAndDeletedFalseOrderByStartDateAsc(List<BigInteger> shiftIds);

    @Query("{'unitPositionId':{'$in':?0},'deleted':false,'isMainShift':true, '$or':[{'startDate':{$gte:?1,$lte:?2}},{'endDate':{$gte:?1,$lte:?2}}]}")
    List<Shift> findShiftBetweenDurationByUnitPositions(List<Long> unitPositionIds, Date startDate, Date endDate);

    @Query("{deleted:false,staffId:{$in:?0}, isMainShift:true,startDate:{$gte:?1,$lte:?2}}")
    List<Shift> findAllShiftsByStaffIds(List<Long> staffIds,Date startDate,Date endDate);

    @Query("{deleted:false, _id:{'$in':?0}}")
    List<Shift> findAllByIds(List<String> shiftIds);

    @Query("{'deleted':false,'staffId':{'$in':?0},'startDate':{$lte:?1},'endDate':{'$gte':?1}}")
    ShiftQueryResult findShiftByStaffIdsAndDate(List<Long> staffids,Date date);
}
