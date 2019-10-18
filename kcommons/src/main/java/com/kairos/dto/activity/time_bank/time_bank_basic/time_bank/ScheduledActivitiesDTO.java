package com.kairos.dto.activity.time_bank.time_bank_basic.time_bank;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

/**
 * @author pradeep
 * @date - 20/8/18
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduledActivitiesDTO {

    private BigInteger id;
    private String name;
    private long scheduledMinutes;

}
