package com.kairos.dto.activity.shift;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Date;

@Getter @Setter @NoArgsConstructor
@AllArgsConstructor
public class ShiftActivityLineInterval {
    private Date startDate;
    private Date endDate;
    private BigInteger activityId;
    private String activityName;
}
