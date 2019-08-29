package com.kairos.persistence.repository.activity;

import com.kairos.dto.activity.shift.ShiftCountDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.attendence_setting.SickSettings;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.wrapper.ShiftResponseDTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by vipul on 22/9/17.
 */
public interface CustomShiftMongoRepository {


    List<ShiftDTO> findAllShiftsBetweenDuration(Long employmentId, Long staffId, Date startDate, Date endDate, Long unitId);

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmployments(List<Long> employmentIds, Date startDate, Date endDate);

    List<ShiftDTO> getAllAssignedShiftsByDateAndUnitId(Long unitId, Date startDate, Date endDate);

    List<Long> getUnitIdListOfShiftBeforeDate(Date date);

    List<ShiftDTO> getShiftsByUnitBeforeDate(Long unitId, Date endDate);

    List<ShiftDTO> findAllShiftsBetweenDurationOfUnitAndStaffId(Long staffId, Date startDate, Date endDate, Long unitId);

    List<ShiftCountDTO> getAssignedShiftsCountByEmploymentId(List<Long> employmentIds, Date startDate);

    List<ShiftResponseDTO> findAllByIdGroupByDate(List<BigInteger> shiftIds);

    void deleteShiftsAfterDate(Long staffId, LocalDateTime employmentEndDate);

    List<Shift> findAllShiftByDynamicQuery(List<SickSettings> sickSettings, Map<BigInteger, Activity> activityMap);

    Long countByActivityId(BigInteger activityId);

    List<Shift> findShiftsForCheckIn(List<Long> staffIds, Date startDateMillis, Date endDateMillis);

    List<ShiftDTO> getAllShiftBetweenDuration(Long employmentId,Long staffId, Date startDate, Date endDate,Long unitId);

    void deleteShiftAfterRestorePhase(BigInteger planningPeriodId, BigInteger phaseId);

    Shift findShiftByShiftActivityId(BigInteger shiftActivityId);

    List<Shift> findAllShiftsByCurrentPhaseAndPlanningPeriod(BigInteger planningPeriodId, BigInteger phaseId);

    List<ShiftResponseDTO> findShiftsBetweenDurationByEmploymentIds(List<Long> employmentIds, Date startDate, Date endDate);

    List<ShiftWithActivityDTO> findAllShiftsByIds(List<BigInteger> shiftIds);

    List<ShiftWithActivityDTO> findAllDraftShiftsByIds(List<BigInteger> shiftIds , boolean draftShift);

    List<Shift> findShiftsByKpiFilters(List<Long> staffIds, List<Long> unitIds, List<String> shiftActivityStatus, Set<BigInteger> timeTypeIds, Date startDate, Date endDate);

    List<ShiftWithActivityDTO> findShiftsByShiftAndActvityKpiFilters(List<Long> staffIds, List<Long> unitIds, List<BigInteger> activitiesIds, List<Integer> dayOfWeeks, Date startDate, Date endDate);

    void updateRemarkInShiftActivity(BigInteger shiftActivityId, String remark);

    List<Shift> findAllShiftByIntervalAndEmploymentId(Long employmentId, Date startDate, Date endDate);

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentAndActivityIds(Long employmentId, Date startDate, Date endDate, Set<BigInteger> activityIds);

    List<ShiftWithActivityDTO> findOverlappedShiftsByEmploymentId(BigInteger shiftId, Long employmentId, Date startDate, Date endDate);

    List<Shift> findAllUnPublishShiftByPlanningPeriodAndUnitId(BigInteger planningPeriodId,Long unitId,List<Long> staffIds ,List<ShiftStatus> shiftStatus);


    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentId(Long employmentId, Date startDate, Date endDate);

    boolean absenceShiftExistsByDate(Long unitId,Date startDate,Date endDate,Long staffId);

    List<ShiftWithActivityDTO> findAllShiftBetweenDurationByUnitId(Long unitId, Date startDate, Date endDate);

    void deleteShiftBetweenDatesByEmploymentId(Long employmentId,Date startDate,Date endDate,Collection<BigInteger> shiftIds);
    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentIdAndDraftShiftExists(Long employmentId, Date startDate, Date endDate,boolean draftShiftExists);
    List<ShiftDTO> findAllByStaffIdsAndDeleteFalse(List<Long> staffIds, LocalDate startDate, LocalDate endDate);

    List<Shift> findShiftByShiftActivityIdAndBetweenDate(Collection<BigInteger> shiftActivityIds,LocalDate startDate,LocalDate endDate,Long staffId);

}
