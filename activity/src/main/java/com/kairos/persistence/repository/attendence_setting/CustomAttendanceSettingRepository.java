package com.kairos.persistence.repository.attendence_setting;



import com.kairos.persistence.model.attendence_setting.AttendanceSetting;

import java.util.Date;
import java.util.List;

public interface CustomAttendanceSettingRepository {

    AttendanceSetting findMaxAttendanceCheckIn(Long userId, Date date,String shiftState);
}
