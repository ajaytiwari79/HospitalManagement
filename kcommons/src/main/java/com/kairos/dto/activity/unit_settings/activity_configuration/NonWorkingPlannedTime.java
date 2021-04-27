package com.kairos.dto.activity.unit_settings.activity_configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import static com.kairos.constants.CommonMessageConstants.PLANNED_TIME_CANNOT_EMPTY;

/**
 * Created By G.P.Ranjan on 13/9/19
 **/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NonWorkingPlannedTime implements Serializable {
    private static final long serialVersionUID = -5966724315718437757L;
    private BigInteger phaseId;
    private BigInteger timeTypeId;  // if exception is true then time type is null
    @NotEmpty(message = PLANNED_TIME_CANNOT_EMPTY)
    private List<BigInteger> plannedTimeIds;
    private boolean exception;

    public NonWorkingPlannedTime(BigInteger phaseId,List<BigInteger> plannedTimeIds, boolean exception) {
        this.phaseId = phaseId;
        this.plannedTimeIds = plannedTimeIds;
        this.exception = exception;
    }
}
