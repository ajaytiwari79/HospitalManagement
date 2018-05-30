package com.kairos.activity.persistence.repository.activity;


import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.persistence.repository.custom_repository.MongoBaseRepository;
import com.kairos.activity.shift.ShiftQueryResult;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
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
    public List<Shift> findShiftBetweenDurationByUnitPosition(Long unitPositionId, Date startDate, Date endDate);

    List<Shift> findByIdInAndDeletedFalse(List<BigInteger> shiftIds);

    @Query("{'unitPositionId':{'$in':?0},'deleted':false,'isMainShift':true,'startDate:{$gte:?1},'parentOpenShiftId':{exists:true}}")
    public List<Shift> findShiftBetweenDurationByUnitPositionIdsAfterDate(List<Long> unitPositionIds, Date startDate);

    @Query("{'unitPositionId':{'$in':?0},'deleted':false,'isMainShift':true, '$or':[{'startDate':{$gte:?1,$lte?2}},{'endDate':{$gte:?1,$lte:?2}}]}")
    public List<Shift> findShiftBetweenDurationByUnitPositions(List<Long> unitPositionIds, Date startDate, Date endDate);

}
