package com.kairos.activity.persistence.repository.activity;


import com.kairos.activity.persistence.model.activity.Shift;
import com.kairos.activity.response.dto.shift.ShiftQueryResult;
import com.kairos.activity.response.dto.ShiftQueryResultWithActivity;
import java.util.Date;
import java.util.List;

/**
 * Created by vipul on 22/9/17.
 */
public interface CustomShiftMongoRepository {

    void updatePhasesOfActivities(Long orgId, Date startDateInISO, Date endDateInISO, String phaseName, String PhaseDescription);

    List<ShiftQueryResult> findAllActivityBetweenDuration(Long staffId, Date startDate, Date endDate, Long unitId);
    List<ShiftQueryResultWithActivity> findAllShiftsBetweenDurationByUEP(Long unitEmploymentPositionId, Date startDate, Date endDate);
    List<ShiftQueryResultWithActivity> findAllShiftsBetweenDurationByUEPs(List<Long> unitEmploymentPositionId, Date startDate, Date endDate);

}
