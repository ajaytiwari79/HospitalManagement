package com.kairos.persistence.repository.staffing_level;

import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.persistence.model.staffing_level.StaffingLevelState;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface StaffingLevelStateMongoRepository  extends MongoBaseRepository<StaffingLevelState, BigInteger> {

    @Query("{deleted:false,planningPeriodId:?0,staffingLevelStatePhaseId:?1,unitId:?2}")
    List<StaffingLevelState> getStaffingLevelState(BigInteger planningPeriodId, BigInteger phaseId, Long unitId);

}
