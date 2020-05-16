package com.kairos.dto.activity.open_shift.priority_group;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StaffIncludeFilter {

    private boolean allowForFlexPool;
    private Float staffAvailability; // In Percentage
    private Integer distanceFromUnit; //In meter
}
