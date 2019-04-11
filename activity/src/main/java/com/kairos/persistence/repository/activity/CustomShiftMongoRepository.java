package com.kairos.persistence.repository.activity;


import com.kairos.dto.activity.counter.chart.BasicChartKpiDateUnit;
import com.kairos.dto.activity.counter.chart.CommonKpiDataUnit;
import com.kairos.dto.activity.shift.ShiftCountDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.enums.TimeTypes;
import com.kairos.enums.shift.ShiftType;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.attendence_setting.SickSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.wrapper.ShiftResponseDTO;
import com.kairos.wrapper.shift.ShiftWithActivityDTO;

import java.math.BigInteger;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by vipul on 22/9/17.
 */
public interface CustomShiftMongoRepository {


    List<ShiftDTO> findAllShiftsBetweenDuration(Long unitPositionId, Long staffId, Date startDate, Date endDate, Long unitId);

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmployment(Long unitPositionId, Date startDate, Date endDate);

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmployments(List<Long> unitPositionIds, Date startDate, Date endDate);

    List<ShiftDTO> getAllAssignedShiftsByDateAndUnitId(Long unitId, Date startDate, Date endDate);

    List<Long> getUnitIdListOfShiftBeforeDate(Date date);

    List<ShiftDTO> getShiftsByUnitBeforeDate(Long unitId, Date endDate);

    List<ShiftDTO> findAllShiftsBetweenDurationOfUnitAndStaffId(Long staffId, Date startDate, Date endDate, Long unitId);

    List<ShiftCountDTO> getAssignedShiftsCountByEmploymentId(List<Long> unitPositionIds, Date startDate);

    List<ShiftResponseDTO> findAllByIdGroupByDate(List<BigInteger> shiftIds);

    void deleteShiftsAfterDate(Long staffId, LocalDateTime employmentEndDate);

    List<Shift> findAllShiftByDynamicQuery(List<SickSettings> sickSettings, Map<BigInteger, Activity> activityMap);

    Long countByActivityId(BigInteger activityId);

    List<Shift> findShiftsForCheckIn(List<Long> staffIds, Date startDateMillis, Date endDateMillis);

    void deleteShiftAfterRestorePhase(BigInteger planningPeriodId, BigInteger phaseId);

    Shift findShiftByShiftActivityId(BigInteger shiftActivityId);

    List<Shift> findAllShiftsByCurrentPhaseAndPlanningPeriod(BigInteger planningPeriodId, BigInteger phaseId);

    List<ShiftResponseDTO> findShiftsBetweenDurationByEmploymentIds(List<Long> unitPositionIds, Date startDate, Date endDate);

    List<ShiftWithActivityDTO> findAllShiftsByIds(List<BigInteger> shiftIds);

    List<CommonKpiDataUnit> findShiftsByKpiFilters(List<Long> staffIds, List<Long> unitIds, List<String> shiftActivityStatus, Set<BigInteger> timeTypeIds, Date startDate, Date endDate);

    List<ShiftWithActivityDTO> findShiftsByShiftAndActvityKpiFilters(List<Long> staffIds, List<Long> unitIds, List<BigInteger> activitiesIds, List<Integer> dayOfWeeks, Date startDate, Date endDate);

    void updateRemarkInShiftActivity(BigInteger shiftActivityId, String remark);

    List<Shift> findAllShiftByIntervalAndEmploymentId(Long unitPositionId, Date startDate, Date endDate);

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentAndActivityIds(Long unitPositionId, Date startDate, Date endDate, Set<BigInteger> activityIds);

    boolean existShiftsBetweenDurationByEmploymentId(BigInteger shiftId, Long unitPositionId, Date startDate, Date endDate, ShiftType shiftType);

    ShiftDTO findOneByIdWithActivityPriority(BigInteger shiftId);

    boolean existShiftsBetweenDurationByEmploymentIdAndTimeType(BigInteger shiftId, Long unitPositionId, Date startDate, Date endDate, TimeTypes timeType);


}
