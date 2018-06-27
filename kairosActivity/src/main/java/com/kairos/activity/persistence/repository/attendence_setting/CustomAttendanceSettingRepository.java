package com.kairos.activity.persistence.repository.attendence_setting;

import com.kairos.activity.persistence.model.attendence_setting.AttendanceSetting;

import java.util.Date;
import java.util.List;

public interface CustomAttendanceSettingRepository {

    List<AttendanceSetting> findMaxAttendanceCheckIn(Long userId,Date date);
}
