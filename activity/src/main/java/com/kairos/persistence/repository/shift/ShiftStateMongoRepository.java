package com.kairos.persistence.repository.shift;

import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;


@Repository
public interface ShiftStateMongoRepository extends MongoBaseRepository<ShiftState, BigInteger> {

    @Query("{deleted:false,planningPeriodId:?0,phaseId:?1,unitId:?2}")
    List<ShiftState> getShiftStateByPlanningPeriodIdAndPhaseId(BigInteger planningPeriodId, BigInteger phaseId,Long unitId);

    @Query("{deleted:false,phaseId:?0,unitId:?1,shiftId:{'$in':?2}}")
    List<ShiftState> getShiftStateByPlanningPeriodAndPhaseAndUnitAndStaffId( BigInteger phaseId,Long unitId,List<BigInteger> shifiIds);

}
