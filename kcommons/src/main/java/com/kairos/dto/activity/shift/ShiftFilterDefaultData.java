package com.kairos.dto.activity.shift;

import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShiftFilterDefaultData {
    List<TimeSlotDTO> timeSlotDTOS;
    List<BigInteger> teamActivityIds;
}
