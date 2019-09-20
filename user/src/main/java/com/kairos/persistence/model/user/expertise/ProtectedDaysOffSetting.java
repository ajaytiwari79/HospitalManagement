package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
public class ProtectedDaysOffSetting extends UserBaseEntity {
    private Long holidayId;
    private LocalDate publicHolidayDate;
    private boolean protechedDaysOff;
    private Long dayTypeId;

}
