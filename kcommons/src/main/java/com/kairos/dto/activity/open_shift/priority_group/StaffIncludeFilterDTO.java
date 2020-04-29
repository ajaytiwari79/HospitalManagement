package com.kairos.dto.activity.open_shift.priority_group;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
@Getter
@Setter
public class StaffIncludeFilterDTO {


    private boolean allowForFlexPool;
    private List<Long> expertiseIds;
    private Float staffAvailability; // In Percentage
    private Integer distanceFromUnit; //In meter
    private List<Long> employmentTypeIds;
    private LocalDate openShiftDate;
    private Long maxOpenShiftDate;

}
