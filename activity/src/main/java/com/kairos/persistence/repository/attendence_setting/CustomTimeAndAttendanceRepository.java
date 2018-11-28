package com.kairos.persistence.repository.attendence_setting;



import com.kairos.dto.activity.attendance.TimeAndAttendanceDTO;
import com.kairos.persistence.model.attendence_setting.TimeAndAttendance;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface CustomTimeAndAttendanceRepository {

    TimeAndAttendance findMaxAttendanceCheckIn(List<Long> staffIds, LocalDate date);

    TimeAndAttendance findMaxAttendanceCheckOut(List<Long> staffIds, LocalDate date);

    List<TimeAndAttendanceDTO> findAllAttendanceByStaffIds(List<Long> staffIds,Long unitId, Date lastDate,Date currentDate);
}
