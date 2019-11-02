package com.kairos.dto.user.country.localAreaTag;

import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalTime;

/**
 * @author pradeep
 * @date - 11/6/18
 */
@Getter
@Setter
public class DayTimeWindowDTO {

    private DayOfWeek dayOfWeek;
    private LocalTime fromTime;
    private LocalTime toTime;
}
