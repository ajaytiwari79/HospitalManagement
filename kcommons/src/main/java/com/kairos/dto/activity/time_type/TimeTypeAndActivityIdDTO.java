package com.kairos.dto.activity.time_type;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class TimeTypeAndActivityIdDTO {
    private BigInteger activityId;
    private String timeType;

}
