package com.kairos.dto.activity.shift;
/*
 *Created By Pavan on 17/9/18
 *
 */

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ShiftTimeDetails {
    private BigInteger activityId;
    private LocalTime activityStartTime;
    private Short totalTime;
    private boolean overNightActivity;

}
