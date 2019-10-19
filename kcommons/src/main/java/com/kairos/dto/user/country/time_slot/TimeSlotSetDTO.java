package com.kairos.dto.user.country.time_slot;

import com.kairos.enums.TimeSlotType;
import com.kairos.enums.time_slot.TimeSlotMode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;

/**
 * Created by prabjot on 5/12/17.
 */
@Getter
@Setter
public class TimeSlotSetDTO {

    @NotEmpty(message = "Time slot set name can't be empty")
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TimeSlotDTO> timeSlots;
    private TimeSlotMode timeSlotMode;
    private TimeSlotType timeSlotType;
}
