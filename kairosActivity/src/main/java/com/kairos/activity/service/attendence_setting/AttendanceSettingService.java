package com.kairos.activity.service.attendence_setting;

import com.kairos.activity.client.GenericIntegrationService;
import com.kairos.activity.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.activity.persistence.model.staffing_level.Duration;
import com.kairos.activity.persistence.repository.attendence_setting.AttendanceSettingRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.service.exception.ExceptionService;
import com.kairos.activity.service.shift.ShiftService;
import com.kairos.activity.shift.ShiftQueryResult;
import com.kairos.activity.util.DateUtils;
import com.kairos.response.dto.web.staff.StaffResultDTO;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;


@Service
public class AttendanceSettingService extends MongoBaseService{

@Inject
private AttendanceSettingRepository attendanceSettingRepository;

@Inject
private GenericIntegrationService restClient;

@Inject
private ShiftService shiftService;

@Inject
private  ExceptionService exceptionService;

public Duration getAttendanceSetting(Long unitId, Long staffId) {
    LocalDate currentDate= DateUtils.getCurrentLocalDate();
    Duration attendanceDuration=new Duration();
    AttendanceSetting AttendanceSetting=attendanceSettingRepository.findbyUnitIdAndStaffIdAndDate(unitId,staffId,currentDate);
    if(Optional.ofNullable(AttendanceSetting).isPresent()) {
     attendanceDuration=AttendanceSetting.getAttendanceDuration().get(AttendanceSetting.getAttendanceDuration().size()-1);
    }
    return attendanceDuration;
}

public Duration updateAttendanceSetting(Long userId, Duration attendanceDuration) {
    StaffResultDTO staffAndOrganizationIds=restClient.getStaffIdsByUserId(userId);
    Date date=DateUtils.getDateByLocalDateAndLocalTime( DateUtils.getCurrentLocalDate(),attendanceDuration.getFrom());
    ShiftQueryResult shiftQueryResults=shiftService.getShiftByStaffIdAndDate(staffAndOrganizationIds.getStaffIds(),date);
    if(!Optional.ofNullable(shiftQueryResults).isPresent()){
        exceptionService.actionNotPermittedException("not found");
    }
    AttendanceSetting attendanceSetting=new AttendanceSetting(shiftQueryResults.getUnitId(), shiftQueryResults.getStaffId(),DateUtils.asLocalDate(date));
          attendanceSetting.getAttendanceDuration().add(attendanceDuration);
    save(attendanceSetting);
    return attendanceDuration;
    }
}
