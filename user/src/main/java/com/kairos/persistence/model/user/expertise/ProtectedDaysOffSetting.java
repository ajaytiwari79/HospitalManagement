package com.kairos.persistence.model.user.expertise;

import com.kairos.persistence.model.common.UserBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProtectedDaysOffSetting extends UserBaseEntity {
    private Long holidayId;
    private LocalDate publicHolidayDate;
    private boolean protectedDaysOff;
    private Long dayTypeId;


}
