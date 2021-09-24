package com.kairos.persistence.model.attendence_setting;


import com.kairos.dto.activity.attendance.AttendanceTimeSlot;
import com.kairos.persistence.model.common.MongoBaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Document
@Getter
@Setter
@NoArgsConstructor
public class TimeAndAttendance extends MongoBaseEntity {
    private Long userId;
    private Long staffId;
    private LocalDate date;
    private List<AttendanceTimeSlot> attendanceTimeSlot;

    public TimeAndAttendance(Long staffId, Long userId, List<AttendanceTimeSlot> attendanceTimeSlot,LocalDate date) {
        this.staffId =staffId;
        this.userId=userId;
        this.attendanceTimeSlot = attendanceTimeSlot;
        this.date=date;
    }


}
