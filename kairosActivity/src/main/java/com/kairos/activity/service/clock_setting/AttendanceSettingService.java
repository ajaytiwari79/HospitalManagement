package com.kairos.activity.service.clock_setting;

import com.kairos.activity.persistence.model.clock_setting.AttendanceSetting;
import com.kairos.activity.persistence.model.staffing_level.Duration;
import com.kairos.activity.persistence.repository.clock_setting.AttendanceSettingRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.response.dto.web.clock_setting.AttendanceSettingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;


@Service
public class AttendanceSettingService extends MongoBaseService{

@Autowired
private AttendanceSettingRepository attendanceSettingRepository;

public Duration getAttendanceSetting(Long unitId, Long staffId) {
    LocalDate currentDate=LocalDate.now();
    //AttendanceSettingDTO attendanceSettingDTO=new AttendanceSettingDTO(staffId,unitId,currentDate);
    Duration attendanceDuration=new Duration();
    AttendanceSetting getAttendanceSetting=attendanceSettingRepository.findbyUnitIdAndStaffIdAndDate(unitId,staffId,currentDate);
    if(Optional.ofNullable(getAttendanceSetting).isPresent()) {
        attendanceDuration=getAttendanceSetting.getClockInClockOutDuration();
        //attendanceSettingDTO.setClockINclockOutDuration(getAttendanceSetting.getClockInClockOutDuration());
    }
    return attendanceDuration;
}

public Duration updateAttendanceSetting(Long unitId, Long staffId, Duration clockInClockOutDuration) {
    LocalDate currentDate=LocalDate.now();
    AttendanceSetting attendanceSetting=attendanceSettingRepository.findbyUnitIdAndStaffIdAndDate(unitId,staffId,currentDate);
    if(Optional.ofNullable(attendanceSetting).isPresent()) {
    attendanceSetting.setClockInClockOutDuration(clockInClockOutDuration);
    }else {
        attendanceSetting = new AttendanceSetting(unitId, staffId,currentDate,clockInClockOutDuration);
    }
    save(attendanceSetting);
    return clockInClockOutDuration;
    }
}
