package com.kairos.dto.user.country.time_slot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Jasgeet on 12/9/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class TimeSlotsDeductionDTO {
    private int dayShiftTimeDeduction; //in percentage

    private int nightShiftTimeDeduction ; //in percentage
}
