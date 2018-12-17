package com.kairos.persistence.repository.activity;


import com.kairos.dto.activity.shift.ShiftCountDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.attendence_setting.SickSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.wrapper.ShiftResponseDTO;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by vipul on 22/9/17.
 */
public interface CustomShiftMongoRepository {


    List<ShiftDTO> findAllShiftsBetweenDuration(Long unitPositionId, Long staffId, Date startDate, Date endDate, Long unitId);

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByUnitPosition(Long unitPositionId, Date startDate, Date endDate);

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByUnitPositions(List<Long> unitPositionIds, Date startDate, Date endDate);

    List<ShiftDTO> getAllAssignedShiftsByDateAndUnitId(Long unitId, Date startDate, Date endDate);

    List<Long> getUnitIdListOfShiftBeforeDate(Date date);

    List<ShiftDTO> getShiftsByUnitBeforeDate(Long unitId, Date endDate);

    List<ShiftDTO> findAllShiftsBetweenDurationOfUnitAndStaffId(Long staffId, Date startDate, Date endDate, Long unitId);

    List<ShiftCountDTO> getAssignedShiftsCountByUnitPositionId(List<Long> unitPositionIds, Date startDate);

    List<ShiftResponseDTO> findAllByIdGroupByDate(List<BigInteger> shiftIds);

    void deleteShiftsAfterDate(Long staffId, LocalDateTime employmentEndDate);

    List<Shift> findAllShiftByDynamicQuery(List<SickSettings> sickSettings, Map<BigInteger, Activity> activityMap);

    Long countByActivityId(BigInteger activityId);

    List<Shift> findShiftsForCheckIn(List<Long> staffIds, Date startDateMillis, Date endDateMillis);

    void deleteShiftAfterRestorePhase(BigInteger planningPeriodId, BigInteger phaseId);

    Shift findShiftByShiftActivityId(BigInteger shiftActivityId);

    List<Shift> findAllShiftsByCurrentPhaseAndPlanningPeriod(BigInteger planningPeriodId, BigInteger phaseId);

    List<ShiftResponseDTO> findShiftsBetweenDurationByUnitPositions(List<Long> unitPositionIds, Date startDate, Date endDate);

    List<ShiftWithActivityDTO> findAllShiftsByIds(List<BigInteger> shiftIds);

    void updateRemarkInShiftActivity(BigInteger shiftActivityId,String remark);
}
