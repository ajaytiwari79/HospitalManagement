package com.kairos.persistence.repository.attendence_setting;



import com.kairos.persistence.model.attendence_setting.TimeAndAttendance;

import java.util.Date;

public interface CustomAttendanceSettingRepository {

    TimeAndAttendance findMaxAttendanceCheckIn(Long userId, Date date, String shiftState);
}
