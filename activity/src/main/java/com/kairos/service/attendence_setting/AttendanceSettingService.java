package com.kairos.service.attendence_setting;

import com.kairos.commons.utils.DateTimeInterval;

import com.kairos.dto.activity.activity.LocationActivityTabWithActivityIdDTO;
import com.kairos.dto.activity.activity.activity_tabs.LocationActivityTabDTO;
import com.kairos.dto.activity.attendance.*;
import com.kairos.dto.activity.glide_time.ActivityGlideTimeDetails;
import com.kairos.dto.activity.unit_settings.FlexibleTimeSettingDTO;
import com.kairos.enums.LocationEnum;
import com.kairos.persistence.model.activity.Activity;
import com.kairos.persistence.model.activity.tabs.LocationActivityTab;
import com.kairos.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.activity.ActivityMongoRepository;
import com.kairos.persistence.repository.attendence_setting.AttendanceSettingRepository;
import com.kairos.persistence.repository.attendence_setting.SickSettingsRepository;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
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
import java.sql.Date;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class AttendanceSettingService extends MongoBaseService {

    @Inject
    private AttendanceSettingRepository attendanceSettingRepository;

    @Inject
    private GenericIntegrationService genericIntegrationService;

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
    @Inject private ActivityMongoRepository activityMongoRepository;
    public AttendanceDTO getAttendanceSetting() {
        AttendanceSetting attendanceSetting = attendanceSettingRepository.findMaxAttendanceCheckIn(UserContext.getUserDetails().getId(), DateUtils.getDateFromLocalDate(LocalDate.now().minusDays(1)));
        SickSettingsDTO sickSettings = sickSettingsRepository.checkUserIsSick(UserContext.getUserDetails().getId());
        return (Optional.ofNullable(attendanceSetting).isPresent()) ? new AttendanceDTO(getAttendanceDTOObject(attendanceSetting.getAttendanceDuration()), sickSettings) : new AttendanceDTO(null, sickSettings);
    }

    public AttendanceDTO updateAttendanceSetting(Long unitId, Long reasonCodeId, boolean checkIn) {
        AttendanceDTO attendanceDTO = null;
        AttendanceSetting attendanceSetting = null;
        Long userId = UserContext.getUserDetails().getId();
        List<StaffResultDTO> staffAndOrganizationIds = genericIntegrationService.getStaffIdsByUserId(userId);
        if (!Optional.ofNullable(staffAndOrganizationIds).isPresent()) {
            exceptionService.actionNotPermittedException("message.staff.notfound");
        }
        Map<Long,StaffResultDTO> unitIdAndStaffResultMap=staffAndOrganizationIds.stream().collect(Collectors.toMap(k->k.getUnitId(),v->v));
        List<Long> staffIds=staffAndOrganizationIds.stream().map(e -> e.getStaffId()).collect(Collectors.toList());
        List<OrganizationAndReasonCodeDTO> organizationAndReasonCodeDTOS=staffAndOrganizationIds.stream().map(reasonCode->new OrganizationAndReasonCodeDTO(reasonCode.getUnitId(),reasonCode.getUnitName(),reasonCode.getReasonCodes())).collect(Collectors.toList());
        Shift shift=null;
        List<Shift> shifts=shiftMongoRepository.findShiftsForCheckIn(staffIds, Date.from(ZonedDateTime.now().minusDays(1).truncatedTo(ChronoUnit.DAYS).toInstant()), Date.from(ZonedDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS).toInstant()));
        Map<Long,List<ReasonCodeDTO>> unitAndReasonCode=staffAndOrganizationIds.stream().collect(Collectors.toMap(StaffResultDTO::getUnitId,StaffResultDTO::getReasonCodes));
        Map<BigInteger,LocationActivityTabDTO> activityIdAndLocationActivityTabMap = new HashMap<>();
        if(!shifts.isEmpty()) {
            Set<BigInteger> activityIds = shifts.stream().flatMap(shift1 -> shift1.getActivities().stream()).map(shiftActivity -> shiftActivity.getActivityId()).collect(Collectors.toSet());
            List<LocationActivityTabWithActivityIdDTO> locationActivityTabWithActivityIds = activityMongoRepository.findAllActivitieIdsAndLocationActivityTabByIds(activityIds);
            activityIdAndLocationActivityTabMap = locationActivityTabWithActivityIds.stream().collect(Collectors.toMap(k->k.getId(),v->v.getLocationActivityTab()));
            /*List<UnitSettingDTO> unitSettingDTOS=unitSettingRepository.getGlideTimeByUnitIds(staffAndOrganizationIds.stream().map(s->s.getUnitId()).collect(Collectors.toList()));
            for(UnitSettingDTO unitSettingDTO:unitSettingDTOS){
                    unitIdAndFlexibleTimeMap.put(unitSettingDTO.getUnitId(),unitSettingDTO.getFlexibleTimeSettings());
            }*/
        }
        if(checkIn) {
            for (Shift checkInshift : shifts) {
                boolean result;
                if (activityIdAndLocationActivityTabMap.containsKey(checkInshift.getActivities().get(0).getActivityId())) {
                    /*if(unitIdAndFlexibleTimeMap.get(checkInshift.getUnitId())==null){
                        exceptionService.dataNotFoundException("error.glidetime.notfound",checkInshift.getUnitId());
                    }*/
                    ActivityGlideTimeDetails glideTimeDetails = activityIdAndLocationActivityTabMap.get(checkInshift.getActivities().get(0).getActivityId()).getCheckInGlideTime(LocationEnum.OFFICE);
                    if(!Optional.ofNullable(glideTimeDetails).isPresent()){
                        exceptionService.dataNotFoundException("error.glidetime.notfound",checkInshift.getActivities().get(0).getActivityName());
                    }
                    ZonedDateTime glidStartDateTime = DateUtils.getZonedDateTimeFromZoneId(ZoneId.of(unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone())).minusMinutes(glideTimeDetails.getBefore());
                    ZonedDateTime glidEndDateTime = DateUtils.getZonedDateTimeFromZoneId(ZoneId.of(unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone())).plusMinutes(glideTimeDetails.getAfter());
                    DateTimeInterval glidTimeInterval = new DateTimeInterval(glidStartDateTime,glidEndDateTime);
                    DateTimeInterval interval = new DateTimeInterval(checkInshift.getStartDate(), checkInshift.getEndDate());
                    /*result=(Math.abs((Duration.between(DateUtils.getLocalDateTimeFromDate(checkInshift.getStartDate()), DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone())))).toMinutes()) < unitIdAndFlexibleTimeMap.get(checkInshift.getUnitId()).getCheckInFlexibleTime());*/
                    result = glidTimeInterval.contains(checkInshift.getStartDate());
                    if (interval.contains(DateUtils.getCurrentMillistByTimeZone(unitIdAndStaffResultMap.get(checkInshift.getUnitId()).getTimeZone()))) {
                        result = (result || reasonCodeId != null);
                        if (!result) {
                                attendanceDTO = new AttendanceDTO(new ArrayList<>(),unitAndReasonCode.get(checkInshift.getUnitId()));
                        }
                    } else {
                        if (!result ) {
                            attendanceDTO = new AttendanceDTO(organizationAndReasonCodeDTOS,new ArrayList<>());
                        }
                    }
                    if (result) {
                        shift = checkInshift;
                            break;
                    }
                }
            }
            if(shift==null) {
                attendanceSetting = checkInWithoutShift(unitId, reasonCodeId, staffAndOrganizationIds);
                if (attendanceSetting == null) {
                    return (attendanceDTO!=null)?attendanceDTO:new AttendanceDTO(organizationAndReasonCodeDTOS,new ArrayList<>());
                }
            }
        }else{
            shift=shifts.stream().filter(shift1 -> shift1.getAttendanceDuration()!=null&&shift1.getAttendanceDuration().getFrom()!=null&&shift1.getAttendanceDuration().getTo()==null).findAny().orElse(null);
        }


        //If shift exist of User
         if(shift!=null && checkIn){
                attendanceSetting= checkInWithShift(shift,reasonCodeId,unitIdAndStaffResultMap.get(shift.getUnitId()));
            if(attendanceSetting==null){
                return new AttendanceDTO(new ArrayList<>(),unitAndReasonCode.get(shift.getUnitId()));
            }else {
                shift.setAttendanceDuration(attendanceSetting.getAttendanceDuration());
                shiftMongoRepository.save(shift);
            }
        }

        else if(!checkIn){
            attendanceSetting= checkOut(unitIdAndStaffResultMap.get(shift.getUnitId()).getTimeZone(),staffAndOrganizationIds,shift,reasonCodeId);
            if(attendanceSetting==null){
                return new AttendanceDTO(new ArrayList<>(),unitAndReasonCode.get(shift.getUnitId()));
            }else {
                if(shift!=null){
                    shift.setAttendanceDuration(attendanceSetting.getAttendanceDuration());
                    shiftMongoRepository.save(shift);
                }
            }
        }


        if (Optional.ofNullable(attendanceSetting).isPresent()) {
            save(attendanceSetting);
            attendanceDTO = new AttendanceDTO(getAttendanceDTOObject(attendanceSetting.getAttendanceDuration()), null);
        }
        return attendanceDTO;
    }

    private AttendanceSetting checkInWithoutShift(Long unitId, Long reasonCodeId, List<StaffResultDTO> staffAndOrganizationIds) {
        AttendanceSetting attendanceSetting = null;
        StaffResultDTO staffAndOrganizationId;
        if (Optional.ofNullable(unitId).isPresent() && !Optional.ofNullable(reasonCodeId).isPresent()) {
            exceptionService.actionNotPermittedException("message.unitid.reasoncodeid.notnull", "");
        } else if (Optional.ofNullable(unitId).isPresent() && Optional.ofNullable(reasonCodeId).isPresent()) {
            staffAndOrganizationId = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(unitId)).findAny().get();
            if (!Optional.ofNullable(staffAndOrganizationId).isPresent()) {
                exceptionService.actionNotPermittedException("message.staff.unitid.notfound");
            }
            AttendanceDuration attendanceDuration = new AttendanceDuration(DateUtils.getTimezonedCurrentDateTime(staffAndOrganizationId.getTimeZone()));
            attendanceSetting = new AttendanceSetting(unitId, staffAndOrganizationId.getStaffId(), UserContext.getUserDetails().getId(), reasonCodeId, attendanceDuration);
        }
        return attendanceSetting;
    }

    private AttendanceSetting checkOut(String timeZone,List<StaffResultDTO> staffAndOrganizationIds,Shift shift,Long reasonCodeId) {
        AttendanceDuration duration = null;
        AttendanceSetting attendanceSetting=null;
        if(shift!=null){
            boolean result= validateGlideTimeWhileCheckOut(shift,reasonCodeId,timeZone);
            if(!result){
                return null;
            }

        }
        attendanceSetting = attendanceSettingRepository.findMaxAttendanceCheckIn(UserContext.getUserDetails().getId(), DateUtils.getDateFromLocalDate(LocalDate.now().minusDays(1)));
        final Long unitId=attendanceSetting.getUnitId();
        if (Optional.ofNullable(attendanceSetting).isPresent()) {
            duration = attendanceSetting.getAttendanceDuration();
            if (!Optional.ofNullable(duration.getTo()).isPresent()) {
                StaffResultDTO staffAndOrganizationId = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(unitId)).findAny().get();
                duration.setTo(DateUtils.getTimezonedCurrentDateTime(staffAndOrganizationId.getTimeZone()));
            } else {
                exceptionService.actionNotPermittedException("message.checkout.exists");
            }
        } else {
            exceptionService.actionNotPermittedException("message.attendance.notexists");
        }
        return attendanceSetting;
    }


    private AttendanceDurationDTO getAttendanceDTOObject(AttendanceDuration attendanceDuration) {
        AttendanceDurationDTO attendanceDurationDTO = new AttendanceDurationDTO();
        attendanceDurationDTO.setClockInDate(DateUtils.getLocalDateFromLocalDateTime(attendanceDuration.getFrom()));
        attendanceDurationDTO.setClockInTime(DateUtils.getLocalTimeFromLocalDateTime(attendanceDuration.getFrom()));
        if (Optional.ofNullable(attendanceDuration.getTo()).isPresent()) {
            attendanceDurationDTO.setClockOutDate(DateUtils.getLocalDateFromLocalDateTime(attendanceDuration.getTo()));
            attendanceDurationDTO.setClockOutTime(DateUtils.getLocalTimeFromLocalDateTime(attendanceDuration.getTo()));
        }
        return attendanceDurationDTO;
    }

        private AttendanceSetting checkInWithShift(Shift shift, Long reasonCodeId, StaffResultDTO staffAndOrganizationId) {
                AttendanceDuration attendanceDuration = new AttendanceDuration(DateUtils.getLocalDateTimeFromZoneId(ZoneId.of(staffAndOrganizationId.getTimeZone())));
            AttendanceSetting attendanceSetting = new AttendanceSetting(shift.getUnitId(), shift.getStaffId(), UserContext.getUserDetails().getId(),reasonCodeId,attendanceDuration);
        return attendanceSetting;

    }

    private boolean validateGlideTimeWhileCheckOut(Shift shift, Long reasonCodeId, String timeZone){
        //Map<Long,StaffResultDTO> unitIdAndStaffResultMap=staffAndOrganizationIds.stream().collect(Collectors.toMap(k->k.getUnitId(),v->v));
        LocationActivityTab locationActivityTab = activityMongoRepository.findActivityGlideTimeByIdAndEnabled(shift.getActivities().get(shift.getActivities().size()-1).getActivityId());
        ActivityGlideTimeDetails glideTimeDetails = locationActivityTab.getCheckOutGlideTime(LocationEnum.OFFICE);
        ZonedDateTime glidStartDateTime = DateUtils.getZonedDateTimeFromZoneId(ZoneId.of(timeZone)).minusMinutes(glideTimeDetails.getBefore());
        ZonedDateTime glidEndDateTime = DateUtils.getZonedDateTimeFromZoneId(ZoneId.of(timeZone)).plusMinutes(glideTimeDetails.getAfter());
        DateTimeInterval glidTimeInterval = new DateTimeInterval(glidStartDateTime,glidEndDateTime);
        //FlexibleTimeSettingDTO flexibleTimeSettingDTO = unitSettingRepository.getFlexibleTimingByUnit(shift.getUnitId()).getFlexibleTimeSettings();

        //if (flexibleTimeSettingDTO != null) {
           // Short checkInFlexibleTime = flexibleTimeSettingDTO.getCheckInFlexibleTime();
             //return  (Math.abs((shift.getEndDate().getTime() - DateUtils.getCurrentMillis()) / ONE_MINUTE) < checkInFlexibleTime || reasonCodeId != null);
        return glidTimeInterval.contains(shift.getEndDate()) || reasonCodeId!=null;
       // return false;
    }

}
