package com.kairos.persistence.repository.shift;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;


@Repository
public interface ShiftStateMongoRepository extends MongoBaseRepository<ShiftState, BigInteger> {

    @Query("{deleted:false,planningPeriodId:?0,shiftStatePhaseId:?1,unitId:?2}")
    List<ShiftState> getShiftsState(BigInteger planningPeriodId, BigInteger phaseId, Long unitId);

    @Query("{deleted:false,phaseId:?0,unitId:?1,shiftId:{'$in':?2}}")
    List<ShiftState> getShiftsState(BigInteger phaseId, Long unitId, List<BigInteger> shifiIds);

    @Query("{deleted:false,staffId:?0, disabled:false,startDate: {$lt: ?2},endDate:{$gt:?1}}")
    List<ShiftState> getAllByStaffBetweenDate(Long staffId, Date startDate, Date endDate);

    @Query("{deleted:false,staffId:{$in:?0}, disabled:false,startDate: {$lt: ?2},endDate:{$gt:?1}}")
    List<ShiftState> getAllByStaffsByIdsBetweenDate(List<Long> staffIds, Date startDate, Date endDate);

    @Query("{deleted:false,shiftId:?0,actualPhaseState:?1}")
    ShiftState findShiftStateByShiftIdAndActualPhase(BigInteger shiftId, String actualPhaseState);

    @Query("{deleted:false,shiftId:{$in:?0},actualPhaseState:?1}")
    List<ShiftState> findShiftStateByShiftIdsAndActualPhase(List<BigInteger> shiftId, String actualPhaseState);

    @Query("{deleted:false,shiftId:?0,actualPhaseState:?1,accessGroupRole:?2}")
    ShiftState findShiftStateByShiftIdAndActualPhaseAndRole(BigInteger shiftId, String actualPhaseState, AccessGroupRole role);

    @Query("{deleted:false,shiftId:{$in:?0},actualPhaseState:?1}")
    List<ShiftState> findAllShiftStateByShiftIdAndActualPhase(List<BigInteger> shiftIds, String actualPhaseState);

    @Query(value = "{shiftId:?0}",delete = true)
    void deleteShiftStateByShiftId(BigInteger shiftId);


}
