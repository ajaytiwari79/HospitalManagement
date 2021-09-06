package com.kairos.persistence.repository.activity;

import com.kairos.dto.activity.shift.ShiftActivityDTO;
import com.kairos.dto.activity.shift.ShiftCountDTO;
import com.kairos.dto.activity.shift.ShiftDTO;
import com.kairos.dto.activity.shift.ShiftWithActivityDTO;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.dto.user.filter.RequiredDataForFilterDTO;
import com.kairos.dto.user.staff.StaffFilterDTO;
import com.kairos.enums.FilterType;
import com.kairos.enums.TimeTypeEnum;
import com.kairos.enums.shift.ShiftStatus;
import com.kairos.persistence.model.shift.CoverShiftSetting;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.wrapper.shift.StaffShiftDetailsDTO;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by vipul on 22/9/17.
 */
public interface CustomShiftMongoRepository {

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmployments(Set<Long> employmentIds, Date startDate, Date endDate ,Set<BigInteger> activityIds);

    StaffShiftDetailsDTO getAllShiftsForOneStaffWithEmploymentsAndBetweenDuration(Set<Long> employmentIds, Date startDate, Date endDate);

    List<Long> getUnitIdListOfShiftBeforeDate(Date date);

    List<ShiftDTO> getShiftsByUnitBeforeDate(Long unitId, Date endDate);

    List<ShiftCountDTO> getAssignedShiftsCountByEmploymentId(List<Long> employmentIds, Date startDate);

    void deleteShiftsAfterDate(Long staffId, LocalDateTime employmentEndDate);

    Long countByActivityId(BigInteger activityId);

    List<Shift> findShiftsForCheckIn(List<Long> staffIds, Date startDateMillis, Date endDateMillis);

    void deleteShiftAfterRestorePhase(BigInteger planningPeriodId, BigInteger phaseId);

    Shift findShiftByShiftActivityId(BigInteger shiftActivityId);

    List<Shift> findAllShiftsByCurrentPhaseAndPlanningPeriod(BigInteger planningPeriodId, BigInteger phaseId);

    List<ShiftWithActivityDTO> findAllShiftsByIds(List<BigInteger> shiftIds);

    List<ShiftWithActivityDTO> findAllDraftShiftsByIds(List<BigInteger> shiftIds , boolean draftShift);

    void updateRemarkInShiftActivity(BigInteger shiftActivityId, String remark);

    List<Shift> findAllShiftByIntervalAndEmploymentId(Long employmentId, Date startDate, Date endDate);

    List<ShiftWithActivityDTO> findOverlappedShiftsByEmploymentId(BigInteger shiftId, Long staffId, Date startDate, Date endDate);

    List<Shift> findAllUnPublishShiftByPlanningPeriodAndUnitId(BigInteger planningPeriodId,Long unitId,List<Long> staffIds ,List<ShiftStatus> shiftStatus);

    Long getCountOfPublishShiftByEmploymentId(Long employmentId);

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentId(BigInteger shiftId,Long employmentId, Date startDate, Date endDate,Boolean draftShift);

    List<ShiftDTO> absenceShiftExistsByDate(Long unitId,Date startDate,Date endDate,Long staffId);

    void deleteShiftBetweenDatesByEmploymentId(Long employmentId,Date startDate,Date endDate,Collection<BigInteger> shiftIds);
    List<ShiftDTO> findAllByStaffIdsAndDeleteFalse(Collection<Long> staffIds, LocalDate startDate, LocalDate endDate);

    List<Shift> findShiftByShiftActivityIdAndBetweenDate(Collection<BigInteger> shiftActivityIds,LocalDate startDate,LocalDate endDate,Long staffId);

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentIdNotEqualShiftIds(Long employmentId, Date startDate, Date endDate,List<BigInteger> shiftIds);


    List<Shift> findAllSicknessShiftByEmploymentIdAndActivityIds(Long employmentId,Date startDate,Date endDate);

    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentIds(BigInteger shiftId,Collection<Long> employmentIds, Date startDate, Date endDate,Boolean draftShift,Set<BigInteger> shiftIds);

    void updateValidateDetailsOfShift(BigInteger shiftId, AccessGroupRole accessGroupRole, LocalDate localDate);

    List<ShiftDTO> getAllAssignedShiftsByDateAndUnitId(Long unitId, Date startDate, Date endDate, StaffFilterDTO staffFilterDTO);
    List<ShiftDTO> findAllShiftsBetweenDurationOfUnitAndStaffId(Long staffId, Date startDate, Date endDate, Long unitId,StaffFilterDTO staffFilterDTO);
    List<ShiftDTO> getAllShiftBetweenDuration(Long employmentId,Long staffId, Date startDate, Date endDate,Long unitId,StaffFilterDTO staffFilterDTO);
    List<Shift> findShiftByStaffIdsAndDate(List<Long> staffids, Date startDate, Date endDate, StaffFilterDTO staffFilterDTO);
    List<ShiftDTO> findAllShiftsBetweenDuration(Long employmentId, Long staffId, Date startDate, Date endDate, Long unitId,StaffFilterDTO staffFilterDTO);
    <T> List<StaffShiftDetailsDTO> getStaffListFilteredByShiftCriteria(Set<Long> employmentIds, Map<FilterType, Set<T>> values, Long unitId, Date startDate, Date endDate, boolean includeDateComparison, RequiredDataForFilterDTO requiredDataForFilterDTO);
    Set<Long> getNotEligibleStaffsForCoverShifts(Date startDate, Date endDate, CoverShiftSetting coverShiftSetting, List<Long> staffIds);

    List<ShiftActivityDTO> findAllShiftActivityiesBetweenDurationByEmploymentAndActivityIds(Long employmentId, Date startDate, Date endDate, Set<BigInteger> activityIds);
    List<ShiftWithActivityDTO> findAllShiftsBetweenDurationByEmploymentId(BigInteger shiftId,Long employmentId, Date startDate, Date endDate,Boolean draftShift,Set<BigInteger> shiftIds);

}
