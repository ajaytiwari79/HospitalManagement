package com.kairos.service.attendence_setting;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.attendance.*;
import com.kairos.dto.activity.glide_time.ActivityGlideTimeDetails;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.dto.user_context.UserContext;
import com.kairos.enums.LocationEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.ActivityLocationSettings;
import com.kairos.persistence.model.attendence_setting.TimeAndAttendance;
import com.kairos.persistence.model.common.MongoBaseEntity;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftActivity;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.SickSettingsRepository;
import com.kairos.persistence.repository.attendence_setting.TimeAndAttendanceRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.reason_code.ReasonCodeRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.shift.ShiftStateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.asDate;
import static com.kairos.commons.utils.ObjectUtils.*;
import static com.kairos.constants.ActivityMessagesConstants.*;
import static com.kairos.enums.reason_code.ReasonCodeType.ATTENDANCE;

@Service
//Todo we have to refactor this service
public class TimeAndAttendanceService {

    @Inject
    private TimeAndAttendanceRepository timeAndAttendanceRepository;
    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private SickSettingsRepository sickSettingsRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private UnitSettingRepository unitSettingRepository;
    @Inject
    private ShiftStateMongoRepository shiftStateMongoRepository;
    @Inject
    private ActivityMongoRepository activityMongoRepository;
    @Autowired
    @Lazy
    private ShiftStateService shiftStateService;
    @Inject
    private ReasonCodeRepository reasonCodeRepository;

    public TimeAndAttendanceDTO getAttendanceSetting() {
        List<StaffResultDTO> staffAndUnitId = userIntegrationService.getStaffIdsByUserId(UserContext.getUserDetails().getId());
        TimeAndAttendance timeAndAttendance = timeAndAttendanceRepository.findMaxAttendanceCheckIn(staffAndUnitId.stream().map(StaffResultDTO::getStaffId).collect(Collectors.toList()), LocalDate.now().minusDays(1));
        SickSettingsDTO sickSettings = sickSettingsRepository.checkUserIsSick(UserContext.getUserDetails().getId());
        return (Optional.ofNullable(timeAndAttendance).isPresent()) ? new TimeAndAttendanceDTO(getAttendanceDTOObject(timeAndAttendance.getAttendanceTimeSlot()), sickSettings) : new TimeAndAttendanceDTO(null, sickSettings);
    }

    public TimeAndAttendanceDTO updateTimeAndAttendance(Long unitId, Long reasonCodeId, Long employmentId, boolean checkIn) {
        TimeAndAttendanceDTO timeAndAttendanceDTO = null;
        TimeAndAttendance timeAndAttendance = null;
        Long userId = UserContext.getUserDetails().getId();
        List<StaffResultDTO> staffAndOrganizationIds = userIntegrationService.getStaffIdsByUserId(userId);
        if (!Optional.ofNullable(staffAndOrganizationIds).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_STAFF_NOTFOUND);
        }
        List<ReasonCodeDTO> reasonCodeDTOS=reasonCodeRepository.findByReasonCodeTypeAndUnitIdNotNull(ATTENDANCE);
        Map<Long, StaffResultDTO> unitIdAndStaffResultMap = staffAndOrganizationIds.stream().collect(Collectors.toMap(StaffResultDTO::getUnitId, v -> v));
        List<Long> staffIds = staffAndOrganizationIds.stream().map(StaffResultDTO::getStaffId).collect(Collectors.toList());
        List<OrganizationAndReasonCodeDTO> organizationAndReasonCodeDTOS = staffAndOrganizationIds.stream().map(reasonCode -> new OrganizationAndReasonCodeDTO(reasonCode.getUnitId(), reasonCode.getUnitName(), reasonCode.getReasonCodes(), reasonCode.getEmployment())).collect(Collectors.toList());
        Shift shift = null;
        List<Shift> shifts = shiftMongoRepository.findShiftsForCheckIn(staffIds, Date.from(ZonedDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS).toInstant()), Date.from(ZonedDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS).toInstant()));
        Map<Long, List<ReasonCodeDTO>> unitAndReasonCode = reasonCodeDTOS.stream().collect(Collectors.groupingBy(ReasonCodeDTO::getUnitId));
        Map<BigInteger, ActivityLocationSettings> activityIdAndLocationActivityMap = new HashMap<>();
        timeAndAttendanceDTO = updateTimeAttendanceDetails(shifts,activityIdAndLocationActivityMap,unitAndReasonCode, checkIn, reasonCodeId, timeAndAttendanceDTO, shift,unitIdAndStaffResultMap,organizationAndReasonCodeDTOS);
        if (checkIn) {
            timeAndAttendance = checkInWithoutShift(unitId, reasonCodeId, employmentId, staffAndOrganizationIds);
            if (timeAndAttendance == null) {
                timeAndAttendanceDTO = (timeAndAttendanceDTO != null) ? timeAndAttendanceDTO : new TimeAndAttendanceDTO(organizationAndReasonCodeDTOS, new ArrayList<>());
            }
        }

        //If shift exist of User
        UpdateTimeAttendanceIfShiftExists updateTimeAttendanceIfShiftExists = new UpdateTimeAttendanceIfShiftExists(reasonCodeId, checkIn, timeAndAttendanceDTO, timeAndAttendance, staffAndOrganizationIds, unitIdAndStaffResultMap, shift, unitAndReasonCode, activityIdAndLocationActivityMap).invoke();
        timeAndAttendanceDTO = updateTimeAttendanceIfShiftExists.getTimeAndAttendanceDTO();
        timeAndAttendance = updateTimeAttendanceIfShiftExists.getTimeAndAttendance();
        return getTimeAndAttendanceDTO(checkIn, timeAndAttendanceDTO, timeAndAttendance, unitIdAndStaffResultMap, shift, activityIdAndLocationActivityMap);
    }

    private TimeAndAttendanceDTO updateTimeAttendanceDetails(List<Shift> shifts, Map<BigInteger, ActivityLocationSettings> activityIdAndLocationActivityMap, Map<Long, List<ReasonCodeDTO>> unitAndReasonCode, boolean checkIn, Long reasonCodeId, TimeAndAttendanceDTO timeAndAttendanceDTO, Shift shift, Map<Long, StaffResultDTO> unitIdAndStaffResultMap, List<OrganizationAndReasonCodeDTO> organizationAndReasonCodeDTOS) {
        activityIdAndLocationActivityMap = getBigIntegerLocationActivityMap(shifts, activityIdAndLocationActivityMap);
        for (Shift checkInshift : shifts) {
            boolean result;
            if (activityIdAndLocationActivityMap.containsKey(checkInshift.getActivities().get(0).getActivityId())) {
                result = checkIn ? validateGlideTimeWhileCheckIn(checkInshift, unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone(), activityIdAndLocationActivityMap) : validateGlideTimeWhileCheckOut(checkInshift, unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone(), activityIdAndLocationActivityMap);
                DateTimeInterval interval = new DateTimeInterval(checkInshift.getStartDate(), checkInshift.getEndDate());
                timeAndAttendanceDTO = updateAttendanceDetailsOnTheBasisOfInterval(interval,result,checkInshift,checkIn,reasonCodeId,unitIdAndStaffResultMap,unitAndReasonCode,organizationAndReasonCodeDTOS);
                if (result) {
                    shift = checkInshift;
                    break;
                }
            }
        }
        return timeAndAttendanceDTO;
    }

    private TimeAndAttendanceDTO updateAttendanceDetailsOnTheBasisOfInterval(DateTimeInterval interval, boolean result, Shift checkInshift, boolean checkIn, Long reasonCodeId, Map<Long, StaffResultDTO> unitIdAndStaffResultMap, Map<Long, List<ReasonCodeDTO>> unitAndReasonCode, List<OrganizationAndReasonCodeDTO> organizationAndReasonCodeDTOS) {
        TimeAndAttendanceDTO timeAndAttendanceDTO = null;
        if (interval.contains(DateUtils.getCurrentMillistByTimeZone(unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone()))) {
            result = (result || reasonCodeId != null);
            if (!result) {
                timeAndAttendanceDTO = new TimeAndAttendanceDTO(new ArrayList<>(), unitAndReasonCode.get(checkInshift.getUnitId()));
            }
        } else {
            if (!result && checkIn && reasonCodeId == null) {
                timeAndAttendanceDTO = new TimeAndAttendanceDTO(organizationAndReasonCodeDTOS, new ArrayList<>());
            }
        }
        return timeAndAttendanceDTO;
    }

    private Map<BigInteger, ActivityLocationSettings> getBigIntegerLocationActivityMap(List<Shift> shifts, Map<BigInteger, ActivityLocationSettings> activityIdAndLocationActivityMap) {
        if (!shifts.isEmpty()) {
            Set<BigInteger> activityIds = shifts.stream().flatMap(shift1 -> shift1.getActivities().stream()).map(ShiftActivity::getActivityId).collect(Collectors.toSet());
            List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(activityIds);
            activityIdAndLocationActivityMap = activities.stream().collect(Collectors.toMap(MongoBaseEntity::getId, Activity::getActivityLocationSettings));
        }
        return activityIdAndLocationActivityMap;
    }

    private TimeAndAttendanceDTO getTimeAndAttendanceDTO(boolean checkIn, TimeAndAttendanceDTO timeAndAttendanceDTO, TimeAndAttendance timeAndAttendance, Map<Long, StaffResultDTO> unitIdAndStaffResultMap, Shift shift, Map<BigInteger, ActivityLocationSettings> activityIdAndLocationActivityMap) {
        Long unitId;
        if (isNotNull(timeAndAttendance)) {
            timeAndAttendanceRepository.save(timeAndAttendance);
            if (isNotNull(shift)) {
                unitId = shift.getUnitId();
                checkIn = (!checkIn) ? !validateGlideTimeWhileCheckOut(shift, unitIdAndStaffResultMap.get(shift.getUnitId()).getTimeZone(), activityIdAndLocationActivityMap) : true;
                shiftStateService.createShiftState(Arrays.asList(shift), checkIn, unitId);
            }
            timeAndAttendanceDTO = new TimeAndAttendanceDTO(getAttendanceDTOObject(timeAndAttendance.getAttendanceTimeSlot()), null);
        }
        return timeAndAttendanceDTO;
    }

    private TimeAndAttendance checkInWithoutShift(Long unitId, Long reasonCodeId, Long employmentId, List<StaffResultDTO> staffAndOrganizationIds) {
        TimeAndAttendance timeAndAttendance = null;
        StaffResultDTO staffAndOrganizationId;
        if (Optional.ofNullable(unitId).isPresent() && !Optional.ofNullable(employmentId).isPresent() && !Optional.ofNullable(reasonCodeId).isPresent()) {
            exceptionService.actionNotPermittedException(MESSAGE_UNITID_REASONCODEID_NOTNULL, "");
        } else if (Optional.ofNullable(unitId).isPresent() && Optional.ofNullable(reasonCodeId).isPresent()) {
            staffAndOrganizationId = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(unitId)).findAny().get();
            if (!Optional.ofNullable(staffAndOrganizationId).isPresent()) {
                exceptionService.actionNotPermittedException(MESSAGE_STAFF_UNITID_NOTFOUND);
            }
            TimeAndAttendance oldTimeAndAttendance = timeAndAttendanceRepository.findMaxAttendanceCheckIn(Arrays.asList(staffAndOrganizationId.getStaffId()), LocalDate.now());
            AttendanceTimeSlot attendanceTimeSlot = new AttendanceTimeSlot(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(staffAndOrganizationId.getTimeZone())), reasonCodeId, employmentId, unitId);
            if (oldTimeAndAttendance != null) {
                oldTimeAndAttendance.getAttendanceTimeSlot().add(attendanceTimeSlot);
                timeAndAttendance = oldTimeAndAttendance;
            } else {
                timeAndAttendance = new TimeAndAttendance(staffAndOrganizationId.getStaffId(), UserContext.getUserDetails().getId(), Arrays.asList(attendanceTimeSlot), LocalDate.now());
            }
        }
        return timeAndAttendance;
    }


    private AttendanceDurationDTO getAttendanceDTOObject(List<AttendanceTimeSlot> attendanceTimeSlot) {
        attendanceTimeSlot.sort((a1, a2) -> a1.getFrom().compareTo(a2.getFrom()));
        AttendanceDurationDTO attendanceDurationDTO = new AttendanceDurationDTO();
        attendanceDurationDTO.setClockInDate(DateUtils.getLocalDateFromLocalDateTime(attendanceTimeSlot.get(attendanceTimeSlot.size() - 1).getFrom()));
        attendanceDurationDTO.setClockInTime(DateUtils.getLocalTimeFromLocalDateTime(attendanceTimeSlot.get(attendanceTimeSlot.size() - 1).getFrom()));
        if (Optional.ofNullable(attendanceTimeSlot.get(attendanceTimeSlot.size() - 1).getTo()).isPresent()) {
            attendanceDurationDTO.setClockOutDate(DateUtils.getLocalDateFromLocalDateTime(attendanceTimeSlot.get(attendanceTimeSlot.size() - 1).getTo()));
            attendanceDurationDTO.setClockOutTime(DateUtils.getLocalTimeFromLocalDateTime(attendanceTimeSlot.get(attendanceTimeSlot.size() - 1).getTo()));
        }
        return attendanceDurationDTO;
    }

    private boolean validateGlideTimeWhileCheckOut(Shift checkInshift, String timeZone, Map<BigInteger, ActivityLocationSettings> activityIdAndLocationActivityMap) {
        ActivityGlideTimeDetails glideTimeDetails = activityIdAndLocationActivityMap.get(checkInshift.getActivities().get(checkInshift.getActivities().size() - 1).getActivityId()).getCheckOutGlideTime(LocationEnum.OFFICE);
        if (!Optional.ofNullable(glideTimeDetails).isPresent()) {
            exceptionService.dataNotFoundException(ERROR_GLIDETIME_NOTFOUND, checkInshift.getActivities().get(checkInshift.getActivities().size() - 1).getActivityName());
        }
        Date glidStartDateTime = DateUtils.asDate(DateUtils.dateToLocalDateTime(checkInshift.getEndDate()).minusMinutes(glideTimeDetails.getBefore()));
        Date glidEndDateTime = DateUtils.asDate(DateUtils.dateToLocalDateTime(checkInshift.getEndDate()).plusMinutes(glideTimeDetails.getAfter()));
        DateTimeInterval glidTimeInterval = new DateTimeInterval(glidStartDateTime, glidEndDateTime);
        return glidTimeInterval.contains(DateUtils.getCurrentMillistByTimeZone(timeZone));
    }

    private boolean validateGlideTimeWhileCheckIn(Shift checkInshift, String timeZone, Map<BigInteger, ActivityLocationSettings> activityIdAndLocationActivityMap) {
        ActivityGlideTimeDetails glideTimeDetails = activityIdAndLocationActivityMap.get(checkInshift.getActivities().get(0).getActivityId()).getCheckInGlideTime(LocationEnum.OFFICE);
        if (!Optional.ofNullable(glideTimeDetails).isPresent()) {
            exceptionService.dataNotFoundException(ERROR_GLIDETIME_NOTFOUND, checkInshift.getActivities().get(0).getActivityName());
        }
        Date glidStartDateTime = DateUtils.asDate(DateUtils.dateToLocalDateTime(checkInshift.getStartDate()).minusMinutes(glideTimeDetails.getBefore()));
        Date glidEndDateTime = DateUtils.asDate(DateUtils.dateToLocalDateTime(checkInshift.getStartDate()).plusMinutes(glideTimeDetails.getAfter()));
        DateTimeInterval glidTimeInterval = new DateTimeInterval(glidStartDateTime, glidEndDateTime);
        return glidTimeInterval.contains(DateUtils.getCurrentMillistByTimeZone(timeZone));
    }


    // check out after job run
    public void checkOutBySchedulerJob(Long unitId, Date startDate, Long staffId) {
        ZonedDateTime startZonedDateTime = DateUtils.asZonedDateTime(startDate).truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime endZonedDateTime = startZonedDateTime.plusDays(1);
        List<Shift> shifts = isNull(staffId) ? shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(asDate(startZonedDateTime), asDate(endZonedDateTime), newArrayList(unitId)) :
                shiftMongoRepository.findShiftBetweenDurationByStaffId(staffId, asDate(startZonedDateTime), asDate(endZonedDateTime));
        shiftStateService.createShiftState(shifts, false, unitId);
    }

    private class UpdateTimeAttendanceIfShiftExists {
        private Long reasonCodeId;
        private boolean checkIn;
        private TimeAndAttendanceDTO timeAndAttendanceDTO;
        private TimeAndAttendance timeAndAttendance;
        private List<StaffResultDTO> staffAndOrganizationIds;
        private Map<Long, StaffResultDTO> unitIdAndStaffResultMap;
        private Shift shift;
        private Map<Long, List<ReasonCodeDTO>> unitAndReasonCode;
        private Map<BigInteger, ActivityLocationSettings> activityIdAndLocationActivityMap;

        public UpdateTimeAttendanceIfShiftExists(Long reasonCodeId, boolean checkIn, TimeAndAttendanceDTO timeAndAttendanceDTO, TimeAndAttendance timeAndAttendance, List<StaffResultDTO> staffAndOrganizationIds, Map<Long, StaffResultDTO> unitIdAndStaffResultMap, Shift shift, Map<Long, List<ReasonCodeDTO>> unitAndReasonCode, Map<BigInteger, ActivityLocationSettings> activityIdAndLocationActivityMap) {
            this.reasonCodeId = reasonCodeId;
            this.checkIn = checkIn;
            this.timeAndAttendanceDTO = timeAndAttendanceDTO;
            this.timeAndAttendance = timeAndAttendance;
            this.staffAndOrganizationIds = staffAndOrganizationIds;
            this.unitIdAndStaffResultMap = unitIdAndStaffResultMap;
            this.shift = shift;
            this.unitAndReasonCode = unitAndReasonCode;
            this.activityIdAndLocationActivityMap = activityIdAndLocationActivityMap;
        }

        public TimeAndAttendanceDTO getTimeAndAttendanceDTO() {
            return timeAndAttendanceDTO;
        }

        public TimeAndAttendance getTimeAndAttendance() {
            return timeAndAttendance;
        }

        public UpdateTimeAttendanceIfShiftExists invoke() {
            if (shift != null && checkIn) {
                timeAndAttendance = checkInWithShift(shift, reasonCodeId, unitIdAndStaffResultMap.get(shift.getUnitId()));
                if (timeAndAttendance == null) {
                    timeAndAttendanceDTO = new TimeAndAttendanceDTO(new ArrayList<>(), unitAndReasonCode.get(shift.getUnitId()));
                }
            } else if (!checkIn) {
                TimeAndAttendance oldTimeAndAttendance = timeAndAttendanceRepository.findMaxAttendanceCheckOut(staffAndOrganizationIds.stream().map(StaffResultDTO::getStaffId).collect(Collectors.toList()), LocalDate.now().minusDays(1));
                if (!(isNotNull(shift) && Optional.ofNullable(oldTimeAndAttendance.getAttendanceTimeSlot().get(oldTimeAndAttendance.getAttendanceTimeSlot().size() - 1).getShiftId()).isPresent() && validateGlideTimeWhileCheckOut(shift, unitIdAndStaffResultMap.get(shift.getUnitId()).getTimeZone(), activityIdAndLocationActivityMap)) && !Optional.ofNullable(reasonCodeId).isPresent()) {
                    timeAndAttendanceDTO = new TimeAndAttendanceDTO(new ArrayList<>(), unitAndReasonCode.get(oldTimeAndAttendance.getAttendanceTimeSlot().get(oldTimeAndAttendance.getAttendanceTimeSlot().size() - 1).getUnitId()));
                } else {
                    timeAndAttendance = checkOut(staffAndOrganizationIds, oldTimeAndAttendance, reasonCodeId);
                }
            }
            return this;
        }

        private TimeAndAttendance checkInWithShift(Shift shift, Long reasonCodeId, StaffResultDTO staffAndOrganizationId) {
            TimeAndAttendance timeAndAttendance = null;
            TimeAndAttendance oldTimeAndAttendance = timeAndAttendanceRepository.findMaxAttendanceCheckIn(Arrays.asList(staffAndOrganizationId.getStaffId()), LocalDate.now());
            AttendanceTimeSlot attendanceTimeSlot = new AttendanceTimeSlot(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(staffAndOrganizationId.getTimeZone())), shift.getUnitId(), reasonCodeId, shift.getEmploymentId(), shift.getId());
            if (oldTimeAndAttendance != null) {
                oldTimeAndAttendance.getAttendanceTimeSlot().add(attendanceTimeSlot);
                timeAndAttendance = oldTimeAndAttendance;
            } else {
                timeAndAttendance = new TimeAndAttendance(shift.getStaffId(), UserContext.getUserDetails().getId(), Arrays.asList(attendanceTimeSlot), LocalDate.now());
            }
            return timeAndAttendance;

        }

        private TimeAndAttendance checkOut(List<StaffResultDTO> staffAndOrganizationIds, TimeAndAttendance oldTimeAndAttendance, Long reasonCodeId) {
            AttendanceTimeSlot duration;
            TimeAndAttendance timeAndAttendance = oldTimeAndAttendance;
            Long unitId = timeAndAttendance.getAttendanceTimeSlot().get(timeAndAttendance.getAttendanceTimeSlot().size() - 1).getUnitId();
            if (Optional.ofNullable(timeAndAttendance).isPresent()) {
                timeAndAttendance.getAttendanceTimeSlot().sort((s1, s2) -> s1.getFrom().compareTo(s2.getFrom()));
                duration = timeAndAttendance.getAttendanceTimeSlot().get(timeAndAttendance.getAttendanceTimeSlot().size() - 1);
                if (!Optional.ofNullable(duration.getTo()).isPresent()) {
                    StaffResultDTO staffAndOrganizationId = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(unitId)).findAny().get();
                    duration.setTo(DateUtils.getTimezonedCurrentDateTime(staffAndOrganizationId.getTimeZone()));
                    duration.setClockOutReasonCode(reasonCodeId);
                } else {
                    exceptionService.actionNotPermittedException(MESSAGE_CHECKOUT_EXISTS);
                }
            } else {
                exceptionService.actionNotPermittedException(MESSAGE_ATTENDANCE_NOTEXISTS);
            }
            return timeAndAttendance;
        }
    }
}
