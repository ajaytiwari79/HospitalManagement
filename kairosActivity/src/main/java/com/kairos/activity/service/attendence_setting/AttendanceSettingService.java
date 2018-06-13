package com.kairos.activity.service.attendence_setting;

import com.kairos.activity.persistence.model.attendence_setting.AttendanceSetting;
import com.kairos.activity.persistence.model.staffing_level.Duration;
import com.kairos.activity.persistence.repository.attendence_setting.AttendanceSettingRepository;
import com.kairos.activity.service.MongoBaseService;
import com.kairos.activity.util.DateUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.LocalDate;
import java.util.Optional;


@Service
public class AttendanceSettingService extends MongoBaseService{

@Inject
private AttendanceSettingRepository attendanceSettingRepository;

public Duration getAttendanceSetting(Long unitId, Long staffId) {
    LocalDate currentDate= DateUtils.getCurrentLocalDate();
    Duration attendanceDuration=new Duration();
    AttendanceSetting AttendanceSetting=attendanceSettingRepository.findbyUnitIdAndStaffIdAndDate(unitId,staffId,currentDate);
    if(Optional.ofNullable(AttendanceSetting).isPresent()) {
     attendanceDuration=AttendanceSetting.getAttendanceDuration().get(AttendanceSetting.getAttendanceDuration().size()-1);
    }
    return attendanceDuration;
}

public Duration updateAttendanceSetting(Long unitId, Long staffId, Duration attendanceDuration) {
    LocalDate currentDate= DateUtils.getCurrentLocalDate();
    AttendanceSetting attendanceSetting=attendanceSettingRepository.findbyUnitIdAndStaffIdAndDate(unitId,staffId,currentDate);
    if(Optional.ofNullable(attendanceSetting).isPresent()) {
        if(attendanceDuration.getTo()!=null){
            Duration duration=attendanceSetting.getAttendanceDuration().get(attendanceSetting.getAttendanceDuration().size()-1);
            if (duration.getTo() == null) {
                duration.setTo(attendanceDuration.getTo());
            }
        }else{
            attendanceSetting.getAttendanceDuration().add(attendanceDuration);
        }
    }else {
        attendanceSetting = new AttendanceSetting(unitId, staffId,currentDate);
        attendanceSetting.getAttendanceDuration().add(attendanceDuration);
    }
    save(attendanceSetting);
    return attendanceDuration;
    }
}
