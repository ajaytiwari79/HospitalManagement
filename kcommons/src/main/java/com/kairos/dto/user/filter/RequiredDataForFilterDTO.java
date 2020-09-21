package com.kairos.dto.user.filter;

import com.kairos.dto.user.country.time_slot.TimeSlotDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
public class RequiredDataForFilterDTO {
    private Set<BigInteger> sickTimeTypeIds;
    private List<LocalDate> functionDates;
    private List<BigInteger> teamActivityIds;
    private List<TimeSlotDTO> timeSlotDTOS;
}
