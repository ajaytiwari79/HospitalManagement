package com.kairos.persistence.repository.shift;

import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.shift.ActivityShiftStatusSettings;
import com.kairos.persistence.repository.custom_repository.MongoBaseRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

/*
 *Created By Pavan on 29/8/18
 *
 */
@Repository
public interface ActivityShiftStatusSettingsRepository extends MongoBaseRepository<ActivityShiftStatusSettings,BigInteger> ,CustomActivityShiftStatusSettingsRepository {

    List<ActivityShiftStatusSettings> findAllByActivityId(BigInteger activityId);

    ActivityShiftStatusSettings findByPhaseIdAndActivityIdAndShiftStatus(BigInteger phaseId, BigInteger activityId, ShiftStatus status);

    @Query("{phaseId:{'$in':?0},activityId:{'$in':?1},shiftStatus:?2}")
    List<ActivityShiftStatusSettings> findByPhaseIdsAndActivityIdsAndShiftStatus(List<BigInteger> phaseIds, Set<BigInteger> activityIds, ShiftStatus status);
}
