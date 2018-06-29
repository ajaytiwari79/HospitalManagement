package com.kairos.service.attendence_setting;

import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.persistence.repository.attendence_setting.AttendanceSettingRepository;
import com.kairos.response.dto.web.attendance.AttendanceDuration;
import com.kairos.activity.util.DateUtils;
import com.kairos.response.dto.web.attendance.AttendanceDTO;
import com.kairos.response.dto.web.attendance.UnitIdAndNameDTO;
import com.kairos.response.dto.web.staff.StaffResultDTO;
import com.kairos.rest_client.GenericIntegrationService;
import com.kairos.service.MongoBaseService;
import com.kairos.service.exception.ExceptionService;
import com.kairos.service.shift.ShiftService;
import com.kairos.util.userContext.UserContext;
import org.springframework.stereotype.Service;
import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class AttendanceSettingService extends MongoBaseService {

    @Inject
    private AttendanceSettingRepository attendanceSettingRepository;

    @Inject
    private GenericIntegrationService restClient;

    @Inject
    private ShiftService shiftService;

    @Inject
    private ExceptionService exceptionService;

    public AttendanceDTO getAttendanceSetting() {
        AttendanceSetting attendanceSetting = attendanceSettingRepository.findMaxAttendanceCheckIn(UserContext.getUserDetails().getId(), DateUtils.asDate(LocalDate.now().minusDays(1)));
        if (!Optional.ofNullable(attendanceSetting).isPresent()) {
            exceptionService.actionNotPermittedException("message.attendance.notexists");
        }
        return new AttendanceDTO(attendanceSetting.getAttendanceDuration());
    }

    public AttendanceDTO updateAttendanceSetting(Long unitId, boolean checkIn) {
        AttendanceDTO attendanceDTO = null;
        AttendanceSetting attendanceSetting = null;
        Long userId = Long.valueOf(UserContext.getUserDetails().getId());
        List<StaffResultDTO> staffAndOrganizationIds = restClient.getStaffIdsByUserId(userId);
        if (!Optional.ofNullable(staffAndOrganizationIds).isPresent()) {
            exceptionService.actionNotPermittedException("message.user.staff.notfound");
        }
        attendanceSetting = (checkIn == true) ? checkInAttendanceSetting(unitId,staffAndOrganizationIds):checkOutAttendanceSetting(staffAndOrganizationIds);
        if(Optional.ofNullable(attendanceSetting).isPresent()) {
            save(attendanceSetting);
            attendanceDTO = new AttendanceDTO(attendanceSetting.getAttendanceDuration());
        } else {
            List<UnitIdAndNameDTO> unitIdAndNames = staffAndOrganizationIds.stream().map(s -> new UnitIdAndNameDTO(s.getUnitId(), s.getUnitName())).collect(Collectors.toList());
            attendanceDTO = new AttendanceDTO(unitIdAndNames);
        }
        return attendanceDTO;
    }

    private AttendanceSetting checkInAttendanceSetting(Long unitId, List<StaffResultDTO> staffAndOrganizationIds) {
        AttendanceSetting attendanceSetting = null;
        StaffResultDTO staffAndOrganizationId;
        if (unitId != null) {
            staffAndOrganizationId = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(unitId)).findAny().get();
            if (staffAndOrganizationId == null) {
                exceptionService.actionNotPermittedException("message.staff.notfound");
            }
            AttendanceDuration attendanceDuration = new AttendanceDuration(DateUtils.getTimezonedCurrentDateTime(staffAndOrganizationId.getTimeZone()));
            attendanceSetting = new AttendanceSetting(unitId, staffAndOrganizationId.getStaffId(), UserContext.getUserDetails().getId(), attendanceDuration);
        } else {
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
        attendanceSetting = attendanceSettingRepository.findMaxAttendanceCheckIn(UserContext.getUserDetails().getId(), DateUtils.asDate(LocalDate.now().minusDays(1)));
        if (Optional.ofNullable(attendanceSetting).isPresent()) {
            duration = attendanceSetting.getAttendanceDuration();
            if (duration.getTo() == null) {
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
}



