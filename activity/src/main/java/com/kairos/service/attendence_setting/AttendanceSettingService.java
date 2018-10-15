package com.kairos.service.attendence_setting;

import com.kairos.dto.activity.shift.ShiftQueryResult;

import com.kairos.dto.activity.unit_settings.FlexibleTimeSettingDTO;
import com.kairos.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.persistence.model.shift.Shift;
import com.kairos.persistence.repository.attendence_setting.AttendanceSettingRepository;
import com.kairos.persistence.repository.attendence_setting.SickSettingsRepository;
import com.kairos.dto.activity.attendance.AttendanceDuration;
import com.kairos.dto.activity.attendance.AttendanceDTO;
import com.kairos.dto.activity.attendance.AttendanceDurationDTO;
import com.kairos.dto.activity.attendance.SickSettingsDTO;
import com.kairos.dto.user.staff.staff.StaffResultDTO;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.persistence.repository.unit_settings.UnitSettingRepository;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftService;
import com.kairos.dto.user.organization.OrganizationCommonDTO;
import com.kairos.dto.user.reason_code.ReasonCodeDTO;
import com.kairos.commons.utils.DateUtils;
import com.kairos.utils.user_context.UserContext;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.management.timer.Timer.ONE_MINUTE;


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
        List<Long> staffIds=staffAndOrganizationIds.stream().map(e -> e.getStaffId()).collect(Collectors.toList());


        //ShiftQueryResult shiftQueryResults = shiftService.getShiftByStaffIdAndDate(staffIds, DateUtils.getCurrentDate());
        Shift shift=shiftMongoRepository.findShiftToBeDone(staffIds, DateUtils.getCurrentDayStart(), Date.from(ZonedDateTime.now().plusDays(1).truncatedTo(ChronoUnit.DAYS).toInstant()));
        //If shift is not found
        if(shift==null && checkIn){
            attendanceSetting =  checkInWithoutHavingShift(unitId, reasonCodeId, staffAndOrganizationIds);
            if(attendanceSetting==null) {
                List<OrganizationCommonDTO> unitIdAndNames = staffAndOrganizationIds.stream().map(s -> new OrganizationCommonDTO(s.getUnitId(), s.getUnitName())).collect(Collectors.toList());
                Set<ReasonCodeDTO> reasonCode = staffAndOrganizationIds.stream().flatMap(s -> s.getReasonCodes().stream()).collect(Collectors.toSet());
                return new AttendanceDTO(unitIdAndNames, reasonCode);
            }

        }
        //If shift exist of User
        else if(shift!=null && checkIn){
            StaffResultDTO staffResultDTO =staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(shift.getUnitId())).findAny().get();
            attendanceSetting= checkInHavingShift(shift,reasonCodeId,staffResultDTO);
            if(attendanceSetting==null){
                Set<ReasonCodeDTO> reasonCode = staffAndOrganizationIds.stream().flatMap(s -> s.getReasonCodes().stream()).collect(Collectors.toSet());
                return new AttendanceDTO(reasonCode);
            }else {
                shift.setAttendanceDuration(attendanceSetting.getAttendanceDuration());
                shiftMongoRepository.save(shift);
            }
        }

        else if(!checkIn){
            attendanceSetting= checkOutWithoutHavingShift(staffAndOrganizationIds,shift,reasonCodeId);
            if(attendanceSetting==null){
                Set<ReasonCodeDTO> reasonCode = staffAndOrganizationIds.stream().flatMap(s -> s.getReasonCodes().stream()).collect(Collectors.toSet());
                return new AttendanceDTO(reasonCode);
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

    private AttendanceSetting checkInWithoutHavingShift(Long unitId, Long reasonCodeId, List<StaffResultDTO> staffAndOrganizationIds) {
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

    private AttendanceSetting checkOutWithoutHavingShift(List<StaffResultDTO> staffAndOrganizationIds,Shift shift,Long reasonCodeId) {
        AttendanceDuration duration = null;
        AttendanceSetting attendanceSetting=null;
        if(shift!=null){
            boolean result= validateFlexibleTimeWhileCheckOut(shift,reasonCodeId);
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

    private AttendanceSetting validateFlexibleTime(Shift shift, Long reasonCodeId, StaffResultDTO staffAndOrganizationId) {
        AttendanceSetting attendanceSetting=null;
        FlexibleTimeSettingDTO flexibleTimeSettingDTO = unitSettingRepository.getFlexibleTimingByUnit(shift.getUnitId()).getFlexibleTimeSettings();
        if (flexibleTimeSettingDTO != null) {
            Short checkInFlexibleTime = flexibleTimeSettingDTO.getCheckInFlexibleTime();
            if (Math.abs((shift.getStartDate().getTime() - DateUtils.getCurrentMillis()) / ONE_MINUTE) < checkInFlexibleTime || reasonCodeId != null) {
                AttendanceDuration attendanceDuration = new AttendanceDuration(DateUtils.getTimezonedCurrentDateTime(staffAndOrganizationId.getTimeZone()));
                attendanceSetting = new AttendanceSetting(shift.getUnitId(), shift.getStaffId(), UserContext.getUserDetails().getId(), attendanceDuration);
            }
        }
        return attendanceSetting;

    }

    private AttendanceSetting checkInHavingShift(Shift shift,Long reasonCodeId,StaffResultDTO staffAndOrganizationId){
        return validateFlexibleTime(shift, reasonCodeId, staffAndOrganizationId);
    }


    private boolean validateFlexibleTimeWhileCheckOut(Shift shift,Long reasonCodeId){
        FlexibleTimeSettingDTO flexibleTimeSettingDTO = unitSettingRepository.getFlexibleTimingByUnit(shift.getUnitId()).getFlexibleTimeSettings();
        if (flexibleTimeSettingDTO != null) {
            Short checkInFlexibleTime = flexibleTimeSettingDTO.getCheckInFlexibleTime();
             return  (Math.abs((shift.getEndDate().getTime() - DateUtils.getCurrentMillis()) / ONE_MINUTE) < checkInFlexibleTime || reasonCodeId != null);
            }
        return false;
    }

}
