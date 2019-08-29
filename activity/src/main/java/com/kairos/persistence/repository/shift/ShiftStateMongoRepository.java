package com.kairos.persistence.repository.shift;

import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.Set;


@Repository
public interface ShiftStateMongoRepository extends MongoBaseRepository<ShiftState, BigInteger> {

    @Query("{deleted:false,planningPeriodId:?0,shiftStatePhaseId:?1,unitId:?2}")
    List<ShiftState> getShiftsState(BigInteger planningPeriodId, BigInteger phaseId, Long unitId);

    @Query("{deleted:false,phaseId:?0,unitId:?1,shiftId:{'$in':?2}}")
    List<ShiftState> getShiftsState(BigInteger phaseId, Long unitId, List<BigInteger> shifiIds);

    @Query("{deleted:false,staffId:{$in:?0}, disabled:false,startDate: {$lt: ?2},endDate:{$gt:?1}}")
    List<ShiftState> getAllByStaffsByIdsBetweenDate(List<Long> staffIds, Date startDate, Date endDate);

    @Query("{deleted:false,shiftId:?0,shiftStatePhaseId:?1}")
    ShiftState findShiftStateByShiftIdAndActualPhase(BigInteger shiftId, BigInteger shiftStatePhaseId);

    @Query("{deleted:false,shiftId:{$in:?0},shiftStatePhaseId:?1}")
    List<ShiftState> findShiftStateByShiftIdsAndPhaseId(List<BigInteger> shiftId, BigInteger shiftStatePhaseId);

    @Query("{deleted:false,shiftId:?0,shiftStatePhaseId:?1,accessGroupRole:?2}")
    ShiftState findShiftStateByShiftIdAndActualPhaseAndRole(BigInteger shiftId, BigInteger shiftStatePhaseId, AccessGroupRole role);

    List<ShiftState> findAllByShiftIdInAndAccessGroupRoleAndValidatedNotNull(Set<BigInteger> shiftIds, AccessGroupRole accessGroupRole);


}
