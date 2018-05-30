package com.kairos.activity.persistence.repository.activity;


import com.kairos.activity.response.dto.ShiftWithActivityDTO;
import com.kairos.activity.shift.ShiftQueryResult;

import java.util.Date;
import java.util.List;

/**
 * Created by vipul on 22/9/17.
 */
public interface CustomShiftMongoRepository {

    void updatePhasesOfActivities(Long orgId, Date startDateInISO, Date endDateInISO, String phaseName, String PhaseDescription);

    List<ShiftQueryResult> findAllActivityBetweenDuration(Long unitPositionId, Long staffId, Date startDate, Date endDate, Long unitId);

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByUEP(Long unitEmploymentPositionId, Date startDate, Date endDate);

    List<ShiftQueryResult> getAllAssignedShiftsByDateAndUnitId(Long unitId, Date startDate, Date endDate);

}
