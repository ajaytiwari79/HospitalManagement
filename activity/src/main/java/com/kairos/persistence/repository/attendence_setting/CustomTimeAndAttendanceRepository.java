package com.kairos.persistence.repository.attendence_setting;



import com.kairos.dto.activity.attendance.TimeAndAttendanceDTO;
import com.kairos.persistence.model.attendence_setting.TimeAndAttendance;

import java.util.Date;
import java.util.List;

public interface CustomTimeAndAttendanceRepository {

    TimeAndAttendance findMaxAttendanceCheckIn(List<Long> staffIds, Date date);

    TimeAndAttendanceDTO findMaxAttendanceCheckInsada(List<Long> staffIds, Date date);

    List<TimeAndAttendanceDTO> findAllAttendanceByStaffIds(List<Long> staffIds, Date date);
}
