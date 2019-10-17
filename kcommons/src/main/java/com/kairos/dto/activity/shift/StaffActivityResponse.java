package com.kairos.dto.activity.shift;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class StaffActivityResponse {
    private Long staffId;
    private BigInteger activityId;
    private String message;

}
