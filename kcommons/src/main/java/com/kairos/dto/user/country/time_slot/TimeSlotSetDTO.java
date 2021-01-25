package com.kairos.dto.user.country.time_slot;

import com.kairos.enums.TimeSlotType;
import com.kairos.enums.time_slot.TimeSlotMode;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

/**
 * Created by prabjot on 5/12/17.
 */
@Getter
@Setter
public class TimeSlotSetDTO implements Serializable {

    private static final long serialVersionUID = -576798808196456379L;
    @NotEmpty(message = "Time slot set name can't be empty")
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<TimeSlotDTO> timeSlots;
    private TimeSlotMode timeSlotMode;
    private TimeSlotType timeSlotType;
    private BigInteger id;
}
