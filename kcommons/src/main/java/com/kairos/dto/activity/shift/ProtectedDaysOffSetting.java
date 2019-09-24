package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ProtectedDaysOffSetting {
    private Long holidayId;
    private LocalDate publicHolidayDate;
    private boolean protechedDaysOff;
    private Long dayTypeId;
}
