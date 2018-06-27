package com.kairos.activity.service.attendence_setting;

import com.kairos.activity.client.GenericIntegrationService;
import com.kairos.activity.util.userContext.UserContext;
import com.kairos.response.dto.web.attendance.AttendanceDuration;
import com.kairos.activity.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.activity.persistence.repository.attendence_setting.AttendanceSettingRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.shift.ShiftService;
import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.activity.util.DateUtils;
import com.kairos.response.dto.web.attendance.AttendanceDTO;
import com.kairos.response.dto.web.attendance.UnitIdAndNameDTO;
import com.kairos.response.dto.web.staff.StaffResultDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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
    List<AttendanceSetting> attendanceSetting = attendanceSettingRepository.findMaxAttendanceCheckIn(UserContext.getUserDetails().getId(),DateUtils.asDate(LocalDate.now().minusDays(1)));
    return new AttendanceDTO(attendanceSetting.get(0).getAttendanceDuration());
    }

    public AttendanceDTO updateAttendanceSetting( AttendanceDuration attendanceDuration,Long unitId,boolean checkIn) {
        AttendanceDTO attendanceDTO = null;
        StaffResultDTO staffIds=null;
        ShiftQueryResult shiftQueryResults=null;
        Long userId = Long.valueOf(UserContext.getUserDetails().getId());
        List<StaffResultDTO> staffAndOrganizationIds = restClient.getStaffIdsByUserId(userId);
        if (!Optional.ofNullable(staffAndOrganizationIds).isPresent()) {
            exceptionService.actionNotPermittedException("message.user.staff.notfound");
        }
        if(checkIn==true) {
            List<Long> staffids = staffAndOrganizationIds.stream().map(e -> e.getStaffId()).collect(Collectors.toList());
            shiftQueryResults = shiftService.getShiftByStaffIdAndDate(staffids, DateUtils.asDate(LocalDateTime.now()));
            if (!Optional.ofNullable(shiftQueryResults).isPresent()&&unitId==null) {
                List<UnitIdAndNameDTO> unitIdAndName = staffAndOrganizationIds.stream().map(s->new UnitIdAndNameDTO(s.getUnitId(),s.getUnitName())).collect(Collectors.toList());
                return attendanceDTO = new AttendanceDTO(unitIdAndName);
            }
            AttendanceSetting attendanceSetting=null;
            if (unitId != null) {
                staffIds = staffAndOrganizationIds.stream().filter(e -> e.getUnitId().equals(unitId)).findAny().get();
                if (staffIds == null) {
                    exceptionService.actionNotPermittedException("message.staff.notfound");
                }
                 attendanceSetting = new AttendanceSetting(unitId, staffIds.getStaffId(),UserContext.getUserDetails().getId());
                AttendanceDuration setAttendanceDuration = new AttendanceDuration();
                setAttendanceDuration.setFrom(LocalDateTime.now());
                attendanceSetting.setAttendanceDuration(setAttendanceDuration);
                save(attendanceSetting);
                attendanceDTO = new AttendanceDTO();
                attendanceDTO.setDuration(attendanceDuration);
                return attendanceDTO;
            }
            else{
                 attendanceSetting = new AttendanceSetting(shiftQueryResults.getUnitId(), shiftQueryResults.getStaffId(),UserContext.getUserDetails().getId());
                AttendanceDuration setAttendanceDuration = new AttendanceDuration();
                setAttendanceDuration.setFrom(LocalDateTime.now());
                attendanceSetting.setAttendanceDuration(setAttendanceDuration);
            }
            save(attendanceSetting);
        }
        else {
            List<AttendanceSetting> attendanceSetting =null;
                if(unitId==null) {
                    attendanceSetting = attendanceSettingRepository.findMaxAttendanceCheckIn(UserContext.getUserDetails().getId(),DateUtils.asDate(LocalDate.now().minusDays(1)));
                }else{
                    attendanceSetting = attendanceSettingRepository.findMaxAttendanceCheckIn(UserContext.getUserDetails().getId(), DateUtils.asDate(LocalDate.now().minusDays(1)));
                }
                if (Optional.ofNullable(attendanceSetting).isPresent()) {
                        AttendanceDuration  duration = attendanceSetting.get(0).getAttendanceDuration();
                        if (duration.getTo() == null) {
                            duration.setTo(LocalDateTime.now());
                        }
                }
                save(attendanceSetting);
            }
        attendanceDTO = new AttendanceDTO();
        attendanceDTO.setDuration(attendanceDuration);
        return attendanceDTO;
    }
}



