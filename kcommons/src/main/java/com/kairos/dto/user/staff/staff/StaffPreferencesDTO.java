package com.kairos.dto.user.staff.staff;


import com.kairos.enums.ShiftBlockType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
@Getter
@Setter
public class StaffPreferencesDTO {
    private ShiftBlockType shiftBlockType;
    private BigInteger activityId;
    private LocalDate startDate;
}
