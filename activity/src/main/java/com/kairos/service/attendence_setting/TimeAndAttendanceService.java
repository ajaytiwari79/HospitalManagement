package com.kairos.service.attendence_setting;
import com.kairos.commons.utils.DateTimeInterval;
import com.kairos.commons.utils.ObjectMapperUtils;
import com.kairos.constants.AppConstants;
import com.kairos.dto.activity.attendance.*;
import com.kairos.dto.activity.glide_time.ActivityGlideTimeDetails;
import com.kairos.dto.activity.shift.ShiftActivity;
import com.kairos.dto.user.access_permission.AccessGroupRole;
import com.kairos.persistence.model.attendence_setting.TimeAndAttendance;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.model.shift.ShiftState;
import com.kairos.enums.LocationEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.LocationActivityTab;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.TimeAndAttendanceRepository;
import com.kairos.persistence.repository.attendence_setting.SickSettingsRepository;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.persistence.repository.common.MongoSequenceRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.shift.ShiftStateMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftService;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.commons.utils.DateUtils;
import com.kairos.utils.user_context.UserContext;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class TimeAndAttendanceService extends MongoBaseService {

    @Inject
    private TimeAndAttendanceRepository timeAndAttendanceRepository;

    @Inject
    private GenericIntegrationService genericIntegrationService;

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

    public TimeAndAttendanceDTO getAttendanceSetting() {
        List<StaffResultDTO> staffAndUnitId = genericIntegrationService.getStaffIdsByUserId(UserContext.getUserDetails().getId());
        TimeAndAttendance timeAndAttendance = timeAndAttendanceRepository.findMaxAttendanceCheckIn(staffAndUnitId.stream().map(staffResultDTO -> staffResultDTO.getStaffId()).collect(Collectors.toList()), DateUtils.getDateFromLocalDate(LocalDate.now().minusDays(1)));
        SickSettingsDTO sickSettings = sickSettingsRepository.checkUserIsSick(UserContext.getUserDetails().getId());
        return (Optional.ofNullable(timeAndAttendance).isPresent()) ? new TimeAndAttendanceDTO(getAttendanceDTOObject(timeAndAttendance.getAttendanceTimeSlot()), sickSettings) : new TimeAndAttendanceDTO(null, sickSettings);
    }

    public TimeAndAttendanceDTO updateTimeAndAttendance(Long unitId, Long reasonCodeId, Long unitPositionId, boolean checkIn) {
        TimeAndAttendanceDTO timeAndAttendanceDTO = null;
        TimeAndAttendance timeAndAttendance = null;
        Long userId = UserContext.getUserDetails().getId();
        List<StaffResultDTO> staffAndOrganizationIds = genericIntegrationService.getStaffIdsByUserId(userId);
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
                    if (checkIn) {
                        result = validateGlideTimeWhileCheckIn(checkInshift, unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone(), activityIdAndLocationActivityTabMap);
                    }
                    else {
                        result = validateGlideTimeWhileCheckOut(checkInshift, reasonCodeId, unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone(), activityIdAndLocationActivityTabMap);
                    }
                        DateTimeInterval interval = new DateTimeInterval(checkInshift.getStartDate(), checkInshift.getEndDate());
                        if (interval.contains(DateUtils.getCurrentMillistByTimeZone(unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone()))) {
                            result = (result || reasonCodeId != null);
                            if (!result) {
                                return new TimeAndAttendanceDTO(new ArrayList<>(), unitAndReasonCode.get(checkInshift.getUnitId()));
                            }
                        } else {
                            if (!result&&checkIn&&reasonCodeId==null) {
                                return new TimeAndAttendanceDTO(organizationAndReasonCodeDTOS, new ArrayList<>());
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
                    return (timeAndAttendanceDTO !=null)? timeAndAttendanceDTO :new TimeAndAttendanceDTO(organizationAndReasonCodeDTOS,new ArrayList<>());
                }
            }

        //If shift exist of User
         if(shift!=null && checkIn){
                timeAndAttendance = checkInWithShift(shift,reasonCodeId,unitIdAndStaffResultMap.get(shift.getUnitId()));
            if(timeAndAttendance ==null){
                return new TimeAndAttendanceDTO(new ArrayList<>(),unitAndReasonCode.get(shift.getUnitId()));
            }
        }

        else if(!checkIn){
            TimeAndAttendance oldTimeAndAttendance = timeAndAttendanceRepository.findMaxAttendanceCheckIn(staffAndOrganizationIds.stream().map(staffResultDTO -> staffResultDTO.getStaffId()).collect(Collectors.toList()), DateUtils.getDateFromLocalDate(LocalDate.now().minusDays(1)));
            timeAndAttendance = checkOut(staffAndOrganizationIds,shift, oldTimeAndAttendance,reasonCodeId,activityIdAndLocationActivityTabMap);
            if(timeAndAttendance ==null){
                return new TimeAndAttendanceDTO(new ArrayList<>(),unitAndReasonCode.get(oldTimeAndAttendance.getAttendanceTimeSlot().get(oldTimeAndAttendance.getAttendanceTimeSlot().size()-1).getUnitId()));
            }
        }

        if (Optional.ofNullable(timeAndAttendance).isPresent()) {
            save(timeAndAttendance);
            //createShiftState(Arrays.asList(shift),checkIn,Arrays.asList(timeAndAttendance));
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
            TimeAndAttendance oldTimeAndAttendance = timeAndAttendanceRepository.findMaxAttendanceCheckIn(Arrays.asList(UserContext.getUserDetails().getId()), DateUtils.getDateFromLocalDate(LocalDate.now().minusDays(1)));
            AttendanceTimeSlot attendanceTimeSlot = new AttendanceTimeSlot(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(staffAndOrganizationId.getTimeZone())),reasonCodeId,null,unitId);
            if(oldTimeAndAttendance !=null){
                oldTimeAndAttendance.getAttendanceTimeSlot().add(attendanceTimeSlot);
                timeAndAttendance = oldTimeAndAttendance;
            }else{
                timeAndAttendance = new TimeAndAttendance(staffAndOrganizationId.getStaffId(),UserContext.getUserDetails().getId(), Arrays.asList(attendanceTimeSlot));
            }
        }
        return timeAndAttendance;
    }


    private TimeAndAttendance checkOut(List<StaffResultDTO> staffAndOrganizationIds, Shift shift, TimeAndAttendance oldTimeAndAttendance, Long reasonCodeId,Map<BigInteger,LocationActivityTab> activityIdAndLocationActivityTabMap) {
        AttendanceTimeSlot duration ;
        TimeAndAttendance timeAndAttendance = oldTimeAndAttendance;
        if(timeAndAttendance!=null && shift!=null){
            Map<Long,StaffResultDTO> unitIdAndStaffResultMap=staffAndOrganizationIds.stream().collect(Collectors.toMap(k->k.getUnitId(),v->v));
            boolean result= validateGlideTimeWhileCheckOut(shift,reasonCodeId,unitIdAndStaffResultMap.get(shift.getUnitId()).getTimeZone(),activityIdAndLocationActivityTabMap);
            if(!result){
                return null;
            }
        }else if(reasonCodeId==null){
            return null;
        }
        final Long unitId= timeAndAttendance.getAttendanceTimeSlot().get(timeAndAttendance.getAttendanceTimeSlot().size()-1).getUnitId();
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
                TimeAndAttendance oldTimeAndAttendance =timeAndAttendanceRepository.findMaxAttendanceCheckIn(Arrays.asList(UserContext.getUserDetails().getId()), DateUtils.getDateFromLocalDate(LocalDate.now().minusDays(1)));
                AttendanceTimeSlot attendanceTimeSlot = new AttendanceTimeSlot(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(staffAndOrganizationId.getTimeZone())),reasonCodeId,shift.getUnitPositionId(),shift.getUnitId());
                if(oldTimeAndAttendance !=null){
                    oldTimeAndAttendance.getAttendanceTimeSlot().add(attendanceTimeSlot);
                    timeAndAttendance = oldTimeAndAttendance;
                }else{
                    timeAndAttendance = new TimeAndAttendance(shift.getStaffId(), UserContext.getUserDetails().getId(),Arrays.asList(attendanceTimeSlot));
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


    public void createShiftState(List<Shift> shifts,boolean checkIn,List<TimeAndAttendance> timeAndAttendances){
        List<ShiftState> realtimeShiftStates;
        List<ShiftState> timeAndAttendanceShiftStates=null;
       Map<BigInteger,Shift> shiftMap=shifts.stream().collect(Collectors.toMap(k->k.getId(),v->v));
        realtimeShiftStates=shiftStateMongoRepository.findShiftStateByShiftIdsAndActualPhase(shifts.stream().map(s->s.getId()).collect(Collectors.toList()), AppConstants.REALTIME);
        if(shifts!=null&&checkIn) {
            if(realtimeShiftStates.isEmpty()) {
                ShiftState shiftState=null;
                shiftState=createRealTimeShiftState(shiftState,shifts.get(0), timeAndAttendances);
                realtimeShiftStates.add(shiftState);
            }
            shiftStateMongoRepository.saveEntities(realtimeShiftStates);
        } else{
            if(!realtimeShiftStates.isEmpty()){
                realtimeShiftStates.forEach(realtimeShiftState->{
                   // realtimeShiftState.setAttendanceTimeSlot(shiftMap.get(realtimeShiftState.getShiftId()).getAttendanceTimeSlot());
                });
                shiftStateMongoRepository.saveEntities(realtimeShiftStates);
            }
            timeAndAttendanceShiftStates=createTimeAndAttendanceShiftState(timeAndAttendanceShiftStates,realtimeShiftStates,shifts, timeAndAttendances);
            if(!timeAndAttendanceShiftStates.isEmpty()) shiftStateMongoRepository.saveEntities(timeAndAttendanceShiftStates);
     }
    }
    public ShiftState createRealTimeShiftState(ShiftState realtimeShiftState,Shift shift,List<TimeAndAttendance> timeAndAttendance){
        realtimeShiftState = ObjectMapperUtils.copyPropertiesByMapper(shift, ShiftState.class);
        realtimeShiftState.setId(null);
        realtimeShiftState.setShiftId(shift.getId());
        realtimeShiftState.setTAndARole(AccessGroupRole.STAFF);
        return realtimeShiftState;
    }

    public List<ShiftState> createTimeAndAttendanceShiftState(List<ShiftState> timeAndAttendanceShiftStates,List<ShiftState> realtimeShiftStates,List<Shift> shifts,List<TimeAndAttendance> timeAndAttendance){
        timeAndAttendanceShiftStates=shiftStateMongoRepository.findShiftStateByShiftIdsAndActualPhase(shifts.stream().map(shift -> shift.getId()).collect(Collectors.toList()),AppConstants.TIME_AND_ATTENDANCE);
        Map<BigInteger,ShiftState> realtimeShiftStateMap=realtimeShiftStates.stream().collect(Collectors.toMap(k->k.getShiftId(),v->v));
        Map<BigInteger,ShiftState> timeAndAttendanceShiftStateMap=timeAndAttendanceShiftStates.stream().collect(Collectors.toMap(k->k.getShiftId(),v->v));
        for (Shift shift:shifts) {
            if (timeAndAttendanceShiftStateMap.get(shift.getId()) != null) {
                ObjectMapperUtils.copyProperties(realtimeShiftStateMap.get(shift.getId()), timeAndAttendanceShiftStateMap.get(shift.getId()), "id", "actualPhaseState", "accessGroupRole","attendanceSettingId");
                timeAndAttendanceShiftStates.add(timeAndAttendanceShiftStateMap.get(shift.getId()));
            } else {
                if (realtimeShiftStateMap.get(shift.getId()) != null) {
                    ShiftState timeAndAttendanceShiftState = ObjectMapperUtils.copyPropertiesByMapper(realtimeShiftStateMap.get(shift.getId()), ShiftState.class);
                    timeAndAttendanceShiftState.setId(null);
                    timeAndAttendanceShiftState.setTAndARole(AccessGroupRole.STAFF);
                    timeAndAttendanceShiftState.getActivities().forEach(a -> a.setId(mongoSequenceRepository.nextSequence(ShiftActivity.class.getSimpleName())));
                    timeAndAttendanceShiftStates.add(timeAndAttendanceShiftState);
                }
            }
        }
        return timeAndAttendanceShiftStates;
    }

    // check out after job run
    public void checkOutBySchedulerJob(Long unitId){
        List<Shift> saveShifts=new ArrayList<>();
        List<TimeAndAttendance> timeAndAttendances = timeAndAttendanceRepository.findAllbyUnitIdAndDate(unitId,DateUtils.asDate(DateUtils.getEndOfDayFromLocalDateTime()));
        List<Shift> shifts=null;//shiftMongoRepository.findAllShiftByIds(timeAndAttendances.stream().map(timeAndAttendance -> timeAndAttendance.getShiftId()).collect(Collectors.toList()));
             Map<BigInteger, Shift> staffIdAndShifts = shifts.stream().collect(Collectors.toMap(k -> k.getId(), v -> v));
             timeAndAttendances.forEach(timeAndAttendance -> {
                 if (staffIdAndShifts.get(0) != null) {
                     Shift shift = null;//staffIdAndShifts.get(timeAndAttendance.getShiftId());
                     if (!DateUtils.asLocalDate(shift.getEndDate()).isAfter(DateUtils.getCurrentLocalDate())) {
                         timeAndAttendance.getAttendanceTimeSlot().sort((a1, a2) -> a1.getFrom().compareTo(a2.getFrom()));
                         timeAndAttendance.getAttendanceTimeSlot().get(timeAndAttendance.getAttendanceTimeSlot().size() - 1).setTo(DateUtils.asLocalDateTime(shift.getEndDate()));
                         saveShifts.add(shift);
                     }
                 } else {
                     timeAndAttendance.getAttendanceTimeSlot().get(timeAndAttendance.getAttendanceTimeSlot().size() - 1).setTo(DateUtils.getEndOfDayFromLocalDateTime());
                 }
             });
             if (!timeAndAttendances.isEmpty()) timeAndAttendanceRepository.saveEntities(timeAndAttendances);
             if (!saveShifts.isEmpty()) shiftMongoRepository.saveEntities(saveShifts);
             //createShiftState(shifts, false, timeAndAttendances);
         }
}
