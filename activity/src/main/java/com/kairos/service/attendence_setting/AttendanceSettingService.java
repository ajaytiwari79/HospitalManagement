package com.kairos.service.attendence_setting;

import com.kairos.activity.shift.ShiftQueryResult;

import com.kairos.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.persistence.model.attendence_setting.SickSettings;
import com.kairos.persistence.repository.attendence_setting.AttendanceSettingRepository;
import com.kairos.persistence.repository.attendence_setting.SickSettingsRepository;
import com.kairos.persistence.repository.shift.ShiftMongoRepository;
import com.kairos.response.dto.web.attendance.AttendanceDuration;
import com.kairos.response.dto.web.attendance.AttendanceDTO;
import com.kairos.response.dto.web.attendance.AttendanceDurationDTO;
import com.kairos.response.dto.web.attendance.SickSettingsDTO;
import com.kairos.response.dto.web.staff.StaffResultDTO;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftService;
import com.kairos.user.organization.OrganizationCommonDTO;
import com.kairos.user.reason_code.ReasonCodeDTO;
import com.kairos.util.DateUtils;
import com.kairos.util.user_context.UserContext;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    @Inject private SickSettingsRepository sickSettingsRepository;
    @Inject private ShiftMongoRepository shiftMongoRepository;

    public AttendanceDTO getAttendanceSetting() {
        AttendanceSetting attendanceSetting = attendanceSettingRepository.findMaxAttendanceCheckIn(UserContext.getUserDetails().getId(), DateUtils.getDateFromLocalDate(LocalDate.now().minusDays(1)));
        SickSettingsDTO sickSettings=sickSettingsRepository.checkUserIsSick(UserContext.getUserDetails().getId());
        return (Optional.ofNullable(attendanceSetting).isPresent())?new AttendanceDTO(getAttendanceDTOObject(attendanceSetting.getAttendanceDuration()),sickSettings):new AttendanceDTO(null,sickSettings);
    }

    public AttendanceDTO updateAttendanceSetting(Long unitId, Long reasonCodeId,boolean checkIn) {
        AttendanceDTO attendanceDTO = null;
        AttendanceSetting attendanceSetting = null;
        Long userId = Long.valueOf(UserContext.getUserDetails().getId());
        List<StaffResultDTO> staffAndOrganizationIds = genericIntegrationService.getStaffIdsByUserId(userId);
        if (!Optional.ofNullable(staffAndOrganizationIds).isPresent()) {
            exceptionService.actionNotPermittedException("message.staff.notfound");
        }
        ShiftQueryResult shiftQueryResults = (unitId==null)?shiftService.getShiftByStaffIdAndDate(staffAndOrganizationIds.stream().map(StaffResultDTO::getStaffId).collect(Collectors.toList()), DateUtils.getCurrentDate()):
                shiftMongoRepository.findByStaffIdAndUnitIdAndDeletedFalseAndGreaterThanStartDate(staffAndOrganizationIds.get(0).getStaffId(),unitId,DateUtils.getCurrentDate());


        attendanceSetting = (checkIn) ? checkInAttendanceSetting(unitId,reasonCodeId,staffAndOrganizationIds):checkOutAttendanceSetting(staffAndOrganizationIds);
        if(Optional.ofNullable(attendanceSetting).isPresent()) {
            save(attendanceSetting);
            attendanceDTO = new AttendanceDTO(getAttendanceDTOObject(attendanceSetting.getAttendanceDuration()),null);
        } else {
            List<OrganizationCommonDTO> unitIdAndNames = staffAndOrganizationIds.stream().map(s -> new OrganizationCommonDTO(s.getUnitId(), s.getUnitName())).collect(Collectors.toList());
            Set<ReasonCodeDTO> reasonCode=staffAndOrganizationIds.stream().flatMap(s->s.getReasonCodes().stream()).collect(Collectors.toSet());
            attendanceDTO = new AttendanceDTO(unitIdAndNames,reasonCode);
        }
        return attendanceDTO;
    }

    private AttendanceSetting checkInAttendanceSetting(Long unitId,Long reasonCodeId, List<StaffResultDTO> staffAndOrganizationIds) {
        AttendanceSetting attendanceSetting = null;
        StaffResultDTO staffAndOrganizationId;
        if (Optional.ofNullable(unitId).isPresent()&&!Optional.ofNullable(reasonCodeId).isPresent()) {
            exceptionService.actionNotPermittedException("message.unitid.reasoncodeid.notnull","");
        }else if(Optional.ofNullable(unitId).isPresent()&&Optional.ofNullable(reasonCodeId).isPresent()){
            staffAndOrganizationId = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(unitId)).findAny().get();
            if (!Optional.ofNullable(staffAndOrganizationId).isPresent()) {
                exceptionService.actionNotPermittedException("message.staff.unitid.notfound");
            }
            AttendanceDuration attendanceDuration = new AttendanceDuration(DateUtils.getTimezonedCurrentDateTime(staffAndOrganizationId.getTimeZone()));
            attendanceSetting = new AttendanceSetting(unitId, staffAndOrganizationId.getStaffId(), UserContext.getUserDetails().getId(),reasonCodeId,attendanceDuration);
        } else{
            List<Long> staffIds = staffAndOrganizationIds.stream().map(e -> e.getStaffId()).collect(Collectors.toList());
            ShiftQueryResult shiftQueryResults = shiftService.getShiftByStaffIdAndDate(staffIds, DateUtils.getCurrentDate());
            if(Optional.ofNullable(shiftQueryResults).isPresent()) {
                staffAndOrganizationId = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(shiftQueryResults.getUnitId())).findAny().get();
                AttendanceDuration attendanceDuration = new AttendanceDuration(DateUtils.getTimezonedCurrentDateTime(staffAndOrganizationId.getTimeZone()));
                attendanceSetting = new AttendanceSetting(shiftQueryResults.getUnitId(), shiftQueryResults.getStaffId(), UserContext.getUserDetails().getId(), attendanceDuration);
            }
        }
        return attendanceSetting;
    }

    private AttendanceSetting checkOutAttendanceSetting(List<StaffResultDTO> staffAndOrganizationIds) {
        AttendanceDuration duration = null;
        AttendanceSetting attendanceSetting;
        attendanceSetting = attendanceSettingRepository.findMaxAttendanceCheckIn(UserContext.getUserDetails().getId(), DateUtils.getDateFromLocalDate(LocalDate.now().minusDays(1)));
        if (Optional.ofNullable(attendanceSetting).isPresent()) {
            duration = attendanceSetting.getAttendanceDuration();
            if (!Optional.ofNullable(duration.getTo()).isPresent()) {
                StaffResultDTO staffAndOrganizationId = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(attendanceSetting.getUnitId())).findAny().get();
                duration.setTo(DateUtils.getTimezonedCurrentDateTime(staffAndOrganizationId.getTimeZone()));
            }else{
                exceptionService.actionNotPermittedException("message.checkout.exists");
            }
        }else{
            exceptionService.actionNotPermittedException("message.attendance.notexists");
        }
        return attendanceSetting;
    }


    private AttendanceDurationDTO getAttendanceDTOObject(AttendanceDuration attendanceDuration){
        AttendanceDurationDTO attendanceDurationDTO=new AttendanceDurationDTO();
        attendanceDurationDTO.setClockInDate(DateUtils.getLocalDateFromLocalDateTime(attendanceDuration.getFrom()));
        attendanceDurationDTO.setClockInTime(DateUtils.getLocalTimeFromLocalDateTime(attendanceDuration.getFrom()));
       if(Optional.ofNullable(attendanceDuration.getTo()).isPresent()) {
           attendanceDurationDTO.setClockOutDate(DateUtils.getLocalDateFromLocalDateTime(attendanceDuration.getTo()));
           attendanceDurationDTO.setClockOutTime(DateUtils.getLocalTimeFromLocalDateTime(attendanceDuration.getTo()));
       }
       return  attendanceDurationDTO;
        }
}
