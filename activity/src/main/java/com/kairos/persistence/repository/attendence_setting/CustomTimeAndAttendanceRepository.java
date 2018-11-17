package com.kairos.persistence.repository.attendence_setting;



import com.kairos.persistence.model.attendence_setting.TimeAndAttendance;

import java.util.Date;

public interface CustomTimeAndAttendanceRepository {

    TimeAndAttendance findMaxAttendanceCheckIn(Long userId, Date date);
}
