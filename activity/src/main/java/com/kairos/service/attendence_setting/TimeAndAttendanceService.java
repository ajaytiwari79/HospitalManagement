package com.kairos.service.attendence_setting;

import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.DateUtils;
import com.kairos.dto.activity.attendance.*;
import com.kairos.dto.activity.glide_time.ActivityGlideTimeDetails;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.enums.LocationEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.LocationActivityTab;
import com.kairos.persistence.model.attendence_setting.TimeAndAttendance;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.SickSettingsRepository;
import com.kairos.persistence.repository.attendence_setting.TimeAndAttendanceRepository;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.phase.PhaseMongoRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.UserIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftService;
import com.kairos.service.shift.ShiftStateService;
import com.kairos.utils.user_context.UserContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.kairos.commons.utils.DateUtils.getDate;
import static com.kairos.commons.utils.DateUtils.getStartOfDay;


@Service
public class TimeAndAttendanceService extends MongoBaseService {

    @Inject
    private TimeAndAttendanceRepository timeAndAttendanceRepository;

    @Inject
    private UserIntegrationService userIntegrationService;
    @Inject
    private PhaseMongoRepository phaseMongoRepository;
    @Inject
    private ShiftService shiftService;
    @Inject
    private MongoSequenceRepository mongoSequenceRepository;
    @Inject
    private ExceptionService exceptionService;
    @Inject
    private SickSettingsRepository sickSettingsRepository;
    @Inject
    private ShiftMongoRepository shiftMongoRepository;
    @Inject
    private UnitSettingRepository unitSettingRepository;
    @Inject private ShiftStateMongoRepository shiftStateMongoRepository;
    @Inject private ActivityMongoRepository activityMongoRepository;
    @Inject private ShiftStateService shiftStateService;

    public TimeAndAttendanceDTO getAttendanceSetting() {
        List<StaffResultDTO> staffAndUnitId = userIntegrationService.getStaffIdsByUserId(UserContext.getUserDetails().getId());
        TimeAndAttendance timeAndAttendance = timeAndAttendanceRepository.findMaxAttendanceCheckIn(staffAndUnitId.stream().map(staffResultDTO -> staffResultDTO.getStaffId()).collect(Collectors.toList()),LocalDate.now().minusDays(1));
        SickSettingsDTO sickSettings = sickSettingsRepository.checkUserIsSick(UserContext.getUserDetails().getId());
        return (Optional.ofNullable(timeAndAttendance).isPresent()) ? new TimeAndAttendanceDTO(getAttendanceDTOObject(timeAndAttendance.getAttendanceTimeSlot()), sickSettings) : new TimeAndAttendanceDTO(null, sickSettings);
    }
        // peer review
    public TimeAndAttendanceDTO updateTimeAndAttendance(Long unitId, Long reasonCodeId, Long unitPositionId, boolean checkIn) {
        TimeAndAttendanceDTO timeAndAttendanceDTO = null;
        TimeAndAttendance timeAndAttendance = null;
        Long userId = UserContext.getUserDetails().getId();
        List<StaffResultDTO> staffAndOrganizationIds = userIntegrationService.getStaffIdsByUserId(userId);
        if (!Optional.ofNullable(staffAndOrganizationIds).isPresent()) {
            exceptionService.actionNotPermittedException("message.staff.notfound");
        }
        Map<Long,StaffResultDTO> unitIdAndStaffResultMap=staffAndOrganizationIds.stream().collect(Collectors.toMap(k->k.getUnitId(),v->v));
        List<Long> staffIds=staffAndOrganizationIds.stream().map(e -> e.getStaffId()).collect(Collectors.toList());
        List<OrganizationAndReasonCodeDTO> organizationAndReasonCodeDTOS=staffAndOrganizationIds.stream().map(reasonCode->new OrganizationAndReasonCodeDTO(reasonCode.getUnitId(),reasonCode.getUnitName(),reasonCode.getReasonCodes(),reasonCode.getUnitPosition())).collect(Collectors.toList());
        Shift shift=null;
        List<Shift> shifts=shiftMongoRepository.findShiftsForCheckIn(staffIds, Date.from(ZonedDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS).toInstant()), Date.from(ZonedDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS).toInstant()));
        Map<Long,List<ReasonCodeDTO>> unitAndReasonCode=staffAndOrganizationIds.stream().collect(Collectors.toMap(StaffResultDTO::getUnitId,StaffResultDTO::getReasonCodes));
        Map<BigInteger,LocationActivityTab> activityIdAndLocationActivityTabMap = new HashMap<>();
        if(!shifts.isEmpty()) {
            Set<BigInteger> activityIds = shifts.stream().flatMap(shift1 -> shift1.getActivities().stream()).map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toSet());
            List<Activity> activities = activityMongoRepository.findAllActivitiesByIds(activityIds);
            activityIdAndLocationActivityTabMap = activities.stream().collect(Collectors.toMap(k->k.getId(),v->v.getLocationActivityTab()));
        }
            for (Shift checkInshift : shifts) {
                boolean result;
                if (activityIdAndLocationActivityTabMap.containsKey(checkInshift.getActivities().get(0).getActivityId())) {
                    result = checkIn ? validateGlideTimeWhileCheckIn(checkInshift, unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone(), activityIdAndLocationActivityTabMap) : validateGlideTimeWhileCheckOut(checkInshift, reasonCodeId, unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone(), activityIdAndLocationActivityTabMap);
                        DateTimeInterval interval = new DateTimeInterval(checkInshift.getStartDate(), checkInshift.getEndDate());
                        if (interval.contains(DateUtils.getCurrentMillistByTimeZone(unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone()))) {
                            result = (result || reasonCodeId != null);
                            if (!result) {
                                timeAndAttendanceDTO= new TimeAndAttendanceDTO(new ArrayList<>(), unitAndReasonCode.get(checkInshift.getUnitId()));
                            }
                        } else {
                            if (!result&&checkIn&&reasonCodeId==null) {
                                timeAndAttendanceDTO= new TimeAndAttendanceDTO(organizationAndReasonCodeDTOS, new ArrayList<>());
                            }
                        }
                    if (result) {
                        shift = checkInshift;
                        break;
                    }
                }
            }
            if(shift==null&&checkIn) {
                timeAndAttendance = checkInWithoutShift(unitId, reasonCodeId,unitPositionId, staffAndOrganizationIds);
                if (timeAndAttendance == null) {
                    timeAndAttendanceDTO= (timeAndAttendanceDTO !=null)? timeAndAttendanceDTO :new TimeAndAttendanceDTO(organizationAndReasonCodeDTOS,new ArrayList<>());
                }
            }

        //If shift exist of User
         if(shift!=null && checkIn){
                timeAndAttendance = checkInWithShift(shift,reasonCodeId,unitIdAndStaffResultMap.get(shift.getUnitId()));
            if(timeAndAttendance ==null){
                timeAndAttendanceDTO= new TimeAndAttendanceDTO(new ArrayList<>(),unitAndReasonCode.get(shift.getUnitId()));
            }
        }

        else if(!checkIn){
            TimeAndAttendance oldTimeAndAttendance = timeAndAttendanceRepository.findMaxAttendanceCheckOut(staffAndOrganizationIds.stream().map(staffResultDTO -> staffResultDTO.getStaffId()).collect(Collectors.toList()), LocalDate.now().minusDays(1));
            if(!(Optional.ofNullable(shift).isPresent()&&Optional.ofNullable(oldTimeAndAttendance.getAttendanceTimeSlot().get(oldTimeAndAttendance.getAttendanceTimeSlot().size()-1).getShiftId()).isPresent()&&validateGlideTimeWhileCheckOut(shift,reasonCodeId, unitIdAndStaffResultMap.get(shift.getUnitId()).getTimeZone(), activityIdAndLocationActivityTabMap))&&!Optional.ofNullable(reasonCodeId).isPresent()){
                 timeAndAttendanceDTO= new TimeAndAttendanceDTO(new ArrayList<>(),unitAndReasonCode.get(oldTimeAndAttendance.getAttendanceTimeSlot().get(oldTimeAndAttendance.getAttendanceTimeSlot().size()-1).getUnitId()));
             }else{
                 timeAndAttendance = checkOut(staffAndOrganizationIds, shift, oldTimeAndAttendance, reasonCodeId, activityIdAndLocationActivityTabMap);
             }
            }

        if (Optional.ofNullable(timeAndAttendance).isPresent()) {
            save(timeAndAttendance);
            if(Optional.ofNullable(shift).isPresent()) {
                unitId=shift.getUnitId();
                checkIn=(!checkIn)?!validateGlideTimeWhileCheckOut(shift,reasonCodeId, unitIdAndStaffResultMap.get(shift.getUnitId()).getTimeZone(), activityIdAndLocationActivityTabMap):true;
                shiftStateService.createShiftState(Arrays.asList(shift), checkIn,unitId);
            }
            timeAndAttendanceDTO = new TimeAndAttendanceDTO(getAttendanceDTOObject(timeAndAttendance.getAttendanceTimeSlot()), null);
        }
        return timeAndAttendanceDTO;
    }

    private TimeAndAttendance checkInWithoutShift(Long unitId, Long reasonCodeId,Long unitPositionId, List<StaffResultDTO> staffAndOrganizationIds) {
        TimeAndAttendance timeAndAttendance = null;
        StaffResultDTO staffAndOrganizationId;
        if (Optional.ofNullable(unitId).isPresent() &&!Optional.ofNullable(unitPositionId).isPresent()&&!Optional.ofNullable(reasonCodeId).isPresent()) {
            exceptionService.actionNotPermittedException("message.unitid.reasoncodeid.notnull", "");
        } else if (Optional.ofNullable(unitId).isPresent() && Optional.ofNullable(reasonCodeId).isPresent()) {
            staffAndOrganizationId = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(unitId)).findAny().get();
            if (!Optional.ofNullable(staffAndOrganizationId).isPresent()) {
                exceptionService.actionNotPermittedException("message.staff.unitid.notfound");
            }
            TimeAndAttendance oldTimeAndAttendance = timeAndAttendanceRepository.findMaxAttendanceCheckIn(Arrays.asList(staffAndOrganizationId.getStaffId()),LocalDate.now());
            AttendanceTimeSlot attendanceTimeSlot = new AttendanceTimeSlot(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(staffAndOrganizationId.getTimeZone())),reasonCodeId,unitPositionId,unitId);
            if(oldTimeAndAttendance !=null){
                oldTimeAndAttendance.getAttendanceTimeSlot().add(attendanceTimeSlot);
                timeAndAttendance = oldTimeAndAttendance;
            }else{
                timeAndAttendance = new TimeAndAttendance(staffAndOrganizationId.getStaffId(),UserContext.getUserDetails().getId(), Arrays.asList(attendanceTimeSlot),LocalDate.now());
            }
        }
        return timeAndAttendance;
    }


    private TimeAndAttendance checkOut(List<StaffResultDTO> staffAndOrganizationIds, Shift shift, TimeAndAttendance oldTimeAndAttendance, Long reasonCodeId,Map<BigInteger,LocationActivityTab> activityIdAndLocationActivityTabMap) {
        AttendanceTimeSlot duration ;
        TimeAndAttendance timeAndAttendance = oldTimeAndAttendance;
        Long unitId= timeAndAttendance.getAttendanceTimeSlot().get(timeAndAttendance.getAttendanceTimeSlot().size()-1).getUnitId();
        if (Optional.ofNullable(timeAndAttendance).isPresent()) {
            timeAndAttendance.getAttendanceTimeSlot().sort((s1, s2)->s1.getFrom().compareTo(s2.getFrom()));
            duration = timeAndAttendance.getAttendanceTimeSlot().get(timeAndAttendance.getAttendanceTimeSlot().size()-1);
            if (!Optional.ofNullable(duration.getTo()).isPresent()) {
                StaffResultDTO staffAndOrganizationId = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(unitId)).findAny().get();
                duration.setTo(DateUtils.getTimezonedCurrentDateTime(staffAndOrganizationId.getTimeZone()));
                duration.setClockOutReasonCode(reasonCodeId);
            } else {
                exceptionService.actionNotPermittedException("message.checkout.exists");
            }
        } else {
            exceptionService.actionNotPermittedException("message.attendance.notexists");
        }
        return timeAndAttendance;
    }


    private AttendanceDurationDTO getAttendanceDTOObject(List<AttendanceTimeSlot> attendanceTimeSlot) {
        attendanceTimeSlot.sort((a1, a2)->a1.getFrom().compareTo(a2.getFrom()));
        AttendanceDurationDTO attendanceDurationDTO = new AttendanceDurationDTO();
        attendanceDurationDTO.setClockInDate(DateUtils.getLocalDateFromLocalDateTime(attendanceTimeSlot.get(attendanceTimeSlot.size()-1).getFrom()));
        attendanceDurationDTO.setClockInTime(DateUtils.getLocalTimeFromLocalDateTime(attendanceTimeSlot.get(attendanceTimeSlot.size()-1).getFrom()));
        if (Optional.ofNullable(attendanceTimeSlot.get(attendanceTimeSlot.size()-1).getTo()).isPresent()) {
            attendanceDurationDTO.setClockOutDate(DateUtils.getLocalDateFromLocalDateTime(attendanceTimeSlot.get(attendanceTimeSlot.size()-1).getTo()));
            attendanceDurationDTO.setClockOutTime(DateUtils.getLocalTimeFromLocalDateTime(attendanceTimeSlot.get(attendanceTimeSlot.size()-1).getTo()));
        }
        return attendanceDurationDTO;
    }

        private TimeAndAttendance checkInWithShift(Shift shift, Long reasonCodeId, StaffResultDTO staffAndOrganizationId) {
            TimeAndAttendance timeAndAttendance =null;
                TimeAndAttendance oldTimeAndAttendance =timeAndAttendanceRepository.findMaxAttendanceCheckIn(Arrays.asList(staffAndOrganizationId.getStaffId()),LocalDate.now());
                AttendanceTimeSlot attendanceTimeSlot = new AttendanceTimeSlot(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(staffAndOrganizationId.getTimeZone())),shift.getUnitId(),reasonCodeId,shift.getUnitPositionId(),shift.getId());
                if(oldTimeAndAttendance !=null){
                    oldTimeAndAttendance.getAttendanceTimeSlot().add(attendanceTimeSlot);
                    timeAndAttendance = oldTimeAndAttendance;
                }else{
                    timeAndAttendance = new TimeAndAttendance(shift.getStaffId(), UserContext.getUserDetails().getId(),Arrays.asList(attendanceTimeSlot),LocalDate.now());
                }
        return timeAndAttendance;

    }

    private boolean validateGlideTimeWhileCheckOut(Shift checkInshift, Long reasonCodeId, String timeZone,Map<BigInteger,LocationActivityTab> activityIdAndLocationActivityTabMap){
        ActivityGlideTimeDetails glideTimeDetails = activityIdAndLocationActivityTabMap.get(checkInshift.getActivities().get(checkInshift.getActivities().size()-1).getActivityId()).getCheckOutGlideTime(LocationEnum.OFFICE);
        if(!Optional.ofNullable(glideTimeDetails).isPresent()){
            exceptionService.dataNotFoundException("error.glidetime.notfound",checkInshift.getActivities().get(checkInshift.getActivities().size()-1).getActivityName());
        }
        Date glidStartDateTime=DateUtils.asDate(DateUtils.dateToLocalDateTime(checkInshift.getEndDate()).minusMinutes(glideTimeDetails.getBefore()));
        Date glidEndDateTime =DateUtils.asDate(DateUtils.dateToLocalDateTime(checkInshift.getEndDate()).plusMinutes(glideTimeDetails.getAfter()));
        DateTimeInterval glidTimeInterval = new DateTimeInterval(glidStartDateTime,glidEndDateTime);
        return glidTimeInterval.contains(DateUtils.getCurrentMillistByTimeZone(timeZone));
    }

    private boolean validateGlideTimeWhileCheckIn(Shift checkInshift,String timeZone,Map<BigInteger,LocationActivityTab> activityIdAndLocationActivityTabMap){
        ActivityGlideTimeDetails glideTimeDetails = activityIdAndLocationActivityTabMap.get(checkInshift.getActivities().get(0).getActivityId()).getCheckInGlideTime(LocationEnum.OFFICE);
        if(!Optional.ofNullable(glideTimeDetails).isPresent()){
            exceptionService.dataNotFoundException("error.glidetime.notfound",checkInshift.getActivities().get(0).getActivityName());
        }
        Date glidStartDateTime=DateUtils.asDate(DateUtils.dateToLocalDateTime(checkInshift.getStartDate()).minusMinutes(glideTimeDetails.getBefore()));
        Date glidEndDateTime =DateUtils.asDate(DateUtils.dateToLocalDateTime(checkInshift.getStartDate()).plusMinutes(glideTimeDetails.getAfter()));
        DateTimeInterval glidTimeInterval = new DateTimeInterval(glidStartDateTime,glidEndDateTime);
        return glidTimeInterval.contains(DateUtils.getCurrentMillistByTimeZone(timeZone));
    }




    // check out after job run
    public void checkOutBySchedulerJob(Long unitId){
        List<TimeAndAttendance> timeAndAttendances = timeAndAttendanceRepository.findAllbyUnitIdAndDate(unitId,DateUtils.asDate(DateUtils.getEndOfDayFromLocalDateTime()));
        List<Shift> shifts=shiftMongoRepository.findShiftBetweenDurationAndUnitIdAndDeletedFalse(getStartOfDay(getDate()),getDate(),unitId);
             Map<BigInteger, Shift> staffIdAndShifts = shifts.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
             timeAndAttendances.forEach(timeAndAttendance -> {
                 if (staffIdAndShifts.get(timeAndAttendance.getAttendanceTimeSlot().get(timeAndAttendance.getAttendanceTimeSlot().size() - 1).getShiftId()) != null) {
                     Shift shift = staffIdAndShifts.get(timeAndAttendance.getAttendanceTimeSlot().get(timeAndAttendance.getAttendanceTimeSlot().size() - 1).getShiftId());
                     if (!DateUtils.asLocalDate(shift.getEndDate()).isAfter(DateUtils.getCurrentLocalDate())) {
                         timeAndAttendance.getAttendanceTimeSlot().sort((a1, a2) -> a1.getFrom().compareTo(a2.getFrom()));
                         timeAndAttendance.getAttendanceTimeSlot().get(timeAndAttendance.getAttendanceTimeSlot().size() - 1).setTo(DateUtils.asLocalDateTime(shift.getEndDate()));
                     }
                 } else {
                     timeAndAttendance.getAttendanceTimeSlot().get(timeAndAttendance.getAttendanceTimeSlot().size() - 1).setTo(DateUtils.getEndOfDayFromLocalDateTime());
                 }
                 timeAndAttendance.getAttendanceTimeSlot().get(timeAndAttendance.getAttendanceTimeSlot().size() - 1).setSystemGeneratedClockOut(true);
             });
             if (!timeAndAttendances.isEmpty()) timeAndAttendanceRepository.saveEntities(timeAndAttendances);
             shiftStateService.createShiftState(shifts, false,unitId);
         }
}
