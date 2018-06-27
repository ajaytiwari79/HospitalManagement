package com.kairos.activity.service.attendence_setting;

import com.kairos.activity.client.GenericIntegrationService;
import com.kairos.activity.util.userContext.UserContext;
import com.kairos.response.dto.web.attendance.AttendanceDuration;
import com.kairos.activity.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.activity.persistence.model.staffing_level.Duration;
import com.kairos.activity.persistence.repository.attendence_setting.AttendanceSettingRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.shift.ShiftService;
import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.activity.util.DateUtils;
import com.kairos.response.dto.web.attendance.AttendanceDTO;
import com.kairos.response.dto.web.staff.StaffResultDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Map;
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

    public Duration getAttendanceSetting(Long unitId, Long staffId) {
//    LocalDate currentDate= DateUtils.getCurrentLocalDate();
//    Duration attendanceDuration=new Duration();
//    AttendanceSetting AttendanceSetting=attendanceSettingRepository.findbyUnitIdAndStaffIdAndDate(unitId,staffId,currentDate);
//    if(Optional.ofNullable(AttendanceSetting).isPresent()) {
//     attendanceDuration=AttendanceSetting.getAttendanceDuration().get(AttendanceSetting.getAttendanceDuration().size()-1);
//    }
        return null;
    }

    public AttendanceDTO updateAttendanceSetting( AttendanceDuration attendanceDuration) {
        AttendanceDTO attendanceDTO = null;
        Long userId = Long.valueOf(UserContext.getUserDetails().getId());
        List<StaffResultDTO> staffAndOrganizationIds = restClient.getStaffIdsByUserId(userId);
        if (!Optional.ofNullable(staffAndOrganizationIds).isPresent()) {
            exceptionService.actionNotPermittedException("error");
        }
        Date checkIn = DateUtils.getDateByLocalDateAndLocalTime(DateUtils.asLocalDate(DateUtils.getCurrentDate()), attendanceDuration.getFrom());
        if (attendanceDuration.getUnitId() != null) {
            Optional<StaffResultDTO> staff = staffAndOrganizationIds.stream().filter(e -> e.getUnitId() == attendanceDuration.getUnitId()).findAny();
            if (staff.isPresent()) {
                AttendanceSetting attendanceSetting = new AttendanceSetting(attendanceDuration.getUnitId(), staff.get().getStaffId());
                AttendanceDuration setAttendanceDuration = new AttendanceDuration();
                setAttendanceDuration.setCheckIn(checkIn);
                attendanceSetting.setAttendanceDuration(setAttendanceDuration);
                save(attendanceSetting);
                attendanceDTO = new AttendanceDTO();
                attendanceDTO.setDuration(attendanceDuration);
            }
        } else {
            List<Long> staffids = staffAndOrganizationIds.stream().map(e -> e.getStaffId()).collect(Collectors.toList());
            ShiftQueryResult shiftQueryResults = shiftService.getShiftByStaffIdAndDate(staffids, checkIn);
            if (!Optional.ofNullable(shiftQueryResults).isPresent()) {
                Map<Long, String> unitIdAndName = staffAndOrganizationIds.stream().collect(
                        Collectors.toMap(x -> x.getUnitId(), x -> x.getUnitName()));
                return attendanceDTO = new AttendanceDTO(unitIdAndName);
            } else {
                AttendanceDuration duration=null;
                AttendanceSetting getattendanceSetting = attendanceSettingRepository.findByUnitIdAndStaffIdAndDate(shiftQueryResults.getUnitId(), shiftQueryResults.getStaffId(), checkIn);
                if (Optional.ofNullable(getattendanceSetting).isPresent()) {
                    if (attendanceDuration.getTo() != null) {
                         duration = getattendanceSetting.getAttendanceDuration();
                        if (duration.getCheckOut() == null) {
                            Date checkOut = DateUtils.getDateByLocalDateAndLocalTime(DateUtils.getCurrentLocalDate(), attendanceDuration.getTo());
                            Date shiftCheckOut=DateUtils.getDate(shiftQueryResults.getEndDate());
                            if(checkOut.before(shiftCheckOut)||checkOut.equals(shiftCheckOut)){
                                duration.setCheckOut(checkOut);
                            } else{
                                exceptionService.actionNotPermittedException("your shift is over you forget to check out");
                            }

                        }
                    } else {
                        Date checkOut = DateUtils.getDateByLocalDateAndLocalTime(DateUtils.getCurrentLocalDate(), attendanceDuration.getTo());
                        duration.setCheckOut(checkOut);
                        getattendanceSetting.setAttendanceDuration(attendanceDuration);
                    }
                } else {
                    getattendanceSetting = new AttendanceSetting(shiftQueryResults.getUnitId(), shiftQueryResults.getStaffId());
                    AttendanceDuration setAttendanceDuration = new AttendanceDuration();
                    setAttendanceDuration.setCheckIn(checkIn);
                    getattendanceSetting.setAttendanceDuration(setAttendanceDuration);
                }
                save(getattendanceSetting);
                attendanceDTO = new AttendanceDTO();
                attendanceDTO.setDuration(attendanceDuration);
            }
        }
        return attendanceDTO;
    }
}



