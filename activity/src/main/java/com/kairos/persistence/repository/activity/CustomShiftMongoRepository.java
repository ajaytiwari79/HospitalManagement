package com.kairos.persistence.repository.activity;


import com.kairos.activity.shift.ShiftCountDTO;
import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.wrapper.DateWiseShiftResponse;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Created by vipul on 22/9/17.
 */
public interface CustomShiftMongoRepository {

    void updatePhasesOfActivities(Long orgId, Date startDateInISO, Date endDateInISO, String phaseName, String PhaseDescription);

    List<ShiftQueryResult> findAllShiftsBetweenDuration(Long unitPositionId, Long staffId, Date startDate, Date endDate, Long unitId);

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByUEP(Long unitEmploymentPositionId, Date startDate, Date endDate);

    List<ShiftQueryResult> getAllAssignedShiftsByDateAndUnitId(Long unitId, Date startDate, Date endDate);

    List<Long> getUnitIdListOfShiftBeforeDate(Date date);

    List<ShiftQueryResult> getShiftsByUnitBeforeDate(Long unitId, Date endDate);

    List<ShiftQueryResult> findAllShiftsBetweenDurationOfUnitAndStaffId(Long staffId, Date startDate, Date endDate, Long unitId);

    List<ShiftCountDTO> getAssignedShiftsCountByUnitPositionId(List<Long> unitPositionIds, Date startDate);

    List<DateWiseShiftResponse> findAllByIdGroupByDate(List<BigInteger> shiftIds);

    void deleteShiftsAfterDate(Long staffId, LocalDateTime employmentEndDate);

}
