package com.kairos.activity.service.attendence_setting;

import com.kairos.activity.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.activity.persistence.model.staffing_level.Duration;
import com.kairos.activity.persistence.repository.attendence_setting.AttendanceSettingRepository;
import com.kairos.activity.service.MongoBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Optional;


@Service
public class AttendanceSettingService extends MongoBaseService{

@Autowired
private AttendanceSettingRepository attendanceSettingRepository;

public Duration getAttendanceSetting(Long unitId, Long staffId) {
    LocalDate currentDate=LocalDate.now();
    Duration attendanceDuration=new Duration();
    AttendanceSetting getAttendanceSetting=attendanceSettingRepository.findbyUnitIdAndStaffIdAndDate(unitId,staffId,currentDate);
    if(Optional.ofNullable(getAttendanceSetting).isPresent()) {
     attendanceDuration=getAttendanceSetting.getClockInClockOutDuration().get(getAttendanceSetting.getClockInClockOutDuration().size()-1);
    }
    return attendanceDuration;
}

public Duration updateAttendanceSetting(Long unitId, Long staffId, Duration clockInClockOutDuration) {
    LocalDate currentDate=LocalDate.now();
    AttendanceSetting attendanceSetting=attendanceSettingRepository.findbyUnitIdAndStaffIdAndDate(unitId,staffId,currentDate);
    if(Optional.ofNullable(attendanceSetting).isPresent()) {
        if(clockInClockOutDuration.getTo()!=null){
            Duration duration=attendanceSetting.getClockInClockOutDuration().get(attendanceSetting.getClockInClockOutDuration().size()-1);
            if (duration.getTo() == null) {
                duration.setTo(clockInClockOutDuration.getTo());
            }
        }else{
            attendanceSetting.getClockInClockOutDuration().add(clockInClockOutDuration);
        }
    }else {
        attendanceSetting = new AttendanceSetting(unitId, staffId,currentDate);
        attendanceSetting.getClockInClockOutDuration().add(clockInClockOutDuration);
    }
    save(attendanceSetting);
    return clockInClockOutDuration;
    }
}
